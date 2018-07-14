package com.geariot.platform.fishery.service;

import cmcc.iot.onenet.javasdk.api.datapoints.GetDatapointsListApi;
import cmcc.iot.onenet.javasdk.api.datastreams.GetDatastreamApi;
import cmcc.iot.onenet.javasdk.api.device.GetDeviceApi;
import cmcc.iot.onenet.javasdk.api.device.GetLatesDeviceData;
import cmcc.iot.onenet.javasdk.api.triggers.AddTriggersApi;
import cmcc.iot.onenet.javasdk.api.triggers.DeleteTriggersApi;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.datapoints.DatapointsList;
import cmcc.iot.onenet.javasdk.response.datapoints.DatapointsList.DatastreamsItem;
import cmcc.iot.onenet.javasdk.response.datapoints.DatapointsList.DatastreamsItem.DatapointsItem;
import cmcc.iot.onenet.javasdk.response.datastreams.DatastreamsResponse;
import cmcc.iot.onenet.javasdk.response.device.DeciceLatestDataPoint;
import cmcc.iot.onenet.javasdk.response.device.DeviceResponse;
import cmcc.iot.onenet.javasdk.response.triggers.NewTriggersResponse;
import com.geariot.platform.fishery.dao.*;
import com.geariot.platform.fishery.entities.*;
import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.model.*;
import com.geariot.platform.fishery.timer.CMDUtils;
import com.geariot.platform.fishery.utils.DataExportExcel;
import com.geariot.platform.fishery.utils.Diagnosing;
import com.mysql.fabric.xmlrpc.base.Array;
import com.sun.net.httpserver.Authenticator.Success;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//import com.geariot.platform.fishery.socket.CMDUtils;

@Service
@Transactional
public class EquipmentService {

	private static Logger logger = LogManager.getLogger(EquipmentService.class);

	@Autowired
	private AIODao aioDao;

	@Autowired
	private SensorDao sensorDao;

	@Autowired
	private ControllerDao controllerDao;

	@Autowired
	private LimitDao limitDao;

	@Autowired
	private TimerDao timerDao;

	@Autowired
	private Sensor_DataDao sensor_DataDao;

	@Autowired
	private PondDao pondDao;

	@Autowired
	private CompanyDao companyDao;

	@Autowired
	private WXUserDao wxUserDao;

	@Autowired
	private AeratorStatusDao statusDao;

	@Autowired
	private Sensor_ControllerDao sensor_ControllerDao;

	@Autowired
	private DataAlarmDao daDao;

	@Autowired
	private Dev_TriggerDao dev_triggerDao;
	
	@Autowired
	private DeviceDao deviceDao;
	
	@Autowired
	private FishCateDao fishcateDao;

	@Autowired
	private SocketSerivce socketService;

	@Autowired
	private BindService bindService;
	
	@Autowired
	private PondFishDao pondFishDao;
	
	
	
	private String type = "";
	private String relation = "";
	private Company company = null;
	private AIO aio = null;
	private Sensor sensor = null;
	private Controller controller = null;
	private WXUser wxUser = null;
	private String key = "7zMmzMWnY1jlegImd=m4p9EgZiI=";

	public Map<String, Object> VertifyDevicesn(String divsn) {
		/**
		 * 精确查询单个设备
		 * 参数顺序与构造函数顺序一致
		 * @param devid:设备名，String
		 * @param key:masterkey 或者 设备apikey,String
		 */
		String device_sn = divsn.substring(2, divsn.length());
		logger.debug("获得编码"+divsn);
		GetDeviceApi api = new GetDeviceApi(divsn.substring(2, divsn.length()), key);
		BasicResponse<DeviceResponse> response = api.executeApi();
		System.out.println("errno:"+response.errno+" error:"+response.error);
		System.out.println(response.getJson());
		if(response.errno == 0) {
			Device device = deviceDao.findDevice(device_sn);
			if(device == null) {
				return RESCODE.SUCCESS.getJSONRES();
			}else {
				return RESCODE.DEVICESNS_REPEAT.getJSONRES();
			}			
		}else {
			return RESCODE.NOT_FOUND.getJSONRES();
		}
	}

	//删除设备
	public Map<String, Object> delEquipment(String device_sn){
		Device  d = deviceDao.findDevice(device_sn);
		if(d!=null) {
			int type = d.getType();
			switch(type) {
				case 1://传感器
					sensorDao.delete(device_sn);
					//删除触发器
					deleteTriggerBySensorId(device_sn);
					dev_triggerDao.delete(device_sn);
					List<Dev_Trigger> devTriList = dev_triggerDao.findDev_TriggerBydevsn(device_sn);
					for(Dev_Trigger devTri:devTriList) {
						DeleteTriggersApi api = new DeleteTriggersApi(devTri.getTriger_id(), key);
						BasicResponse<Void> response = api.executeApi();
						System.out.println("errno:"+response.errno+" error:"+response.error);
					}
					deviceDao.delete(device_sn);					
					break;
				case 2://一体机
					//一体机完善后，需加入一体机的传感器的触发器的删除部分
					aioDao.delete(device_sn);
					deviceDao.delete(device_sn);
					break;
				case 3://控制器
					controllerDao.delete(device_sn);
					deviceDao.delete(device_sn);
					limitDao.delete(device_sn);
					timerDao.delete(device_sn);
					break;
			}
			return RESCODE.SUCCESS.getJSONRES();
		}else {
			return RESCODE.ACCOUNT_NOT_EXIST.getJSONRES();
		}		
	}

	public void changeControllerWayOnoff(String divsn, int way ,int key) {
		String text = "KM"+way+":"+key;
		int results = CMDUtils.sendStrCmd(divsn,text);
	}

	public Map<String, Object> addSensor(Sensor...sensors) {
		//为塘口添加传感器
		//只可添加一个传感器，数组形式为便于前端渲染	
			Sensor sensor  =sensors[0];
			//截除前两个表示设备类型的编号
			String  device_sn = sensor.getDevice_sn();
			logger.debug(device_sn);
			String api_device_sn = device_sn.substring(2, device_sn.length());
			
			if(deviceDao.findDevice(api_device_sn)==null) {	
					logger.debug("设备不存在，添加设备");
					//设备号不存在，添加设备
					Device device = new Device();
					device.setDevice_sn(api_device_sn);
					device.setType(1);
					deviceDao.save(device);									
					sensor.setDevice_sn(api_device_sn);
					logger.debug("在device中添加了一个传感器");
					logger.debug("塘口Id:" + sensor.getPondId() + "尝试与传感器设备,设备编号为:" + sensor.getDevice_sn() + "进行绑定...");
					Pond pond = pondDao.findPondByPondId(sensor.getPondId());
					if (pond == null) {
						//塘口不存在,传感器的塘口Id设为0
						logger.debug("塘口Id:" + sensor.getPondId()+ "在数据库中无记录!!!");
						sensor.setPondId(0);
						//return RESCODE.POND_NOT_EXIST.getJSONRES();
					}else {
						//塘口存在，添加触发器
						//根据鱼的种类添加触发器1.鱼2.虾3.蟹
						Pond sensorbindpond=pondDao.findPondByPondId(sensor.getPondId());
						List<PondFish> pondFishList = pondFishDao.getFishbyPondId(sensorbindpond.getId());
			            int pondfishtype;
			            if (pondFishList.size()!=0){
			            	//塘口中有鱼
			            	logger.debug("塘口中有鱼");
			            	logger.debug("塘口中共有"+pondFishList.size()+"种");
			                PondFish senbinfs=pondFishList.get(0);
			                pondfishtype=senbinfs.getType();
			                logger.debug("鱼塘中鱼的种类为："+pondfishtype);
			                int triggeraddresult=addTrigerbyFishtype(sensor.getDevice_sn(), pondfishtype);			            
			            }	        	
					}	
					sensorDao.save(sensor);
					logger.debug("在sensor中添加了一个传感器");	
					return RESCODE.SUCCESS.getJSONRES();
				}else {
					//设备号存在，设备已使用
					System.out.println("设备号存在，设备已使用");
					/*Sensor sensorFind = sensorDao.findSensorByDeviceSns(api_device_sn);
					//传感器不存在，用户已删除设备
					if(sensorFind==null) {
						logger.debug("塘口Id:" + sensor.getPondId() + "尝试与传感器设备,设备编号为:" + sensor.getDevice_sn() + "进行绑定...");
						Pond pond = pondDao.findPondByPondId(sensor.getPondId());
						if (pond == null) {
							//塘口不存在
							sensor.setPondId(0);
							logger.debug("塘口Id:" + sensor.getPondId()+ "在数据库中无记录!!!");
							return RESCODE.POND_NOT_EXIST.getJSONRES();
						}else {
							sensor.setPondId(sensor.getPondId());
							//塘口存在，添加触发器
							//根据鱼的种类添加触发器1.鱼2.虾3.蟹
							Pond sensorbindpond=pondDao.findPondByPondId(sensor.getPondId());
							List<PondFish> pondFishList = pondFishDao.getFishbyPondId(sensorbindpond.getId());
				            int pondfishtype; 
				            if (pondFishList.size()!=0){
				            	//塘口中有鱼
				            	logger.debug("塘口中有鱼");
				            	logger.debug("塘口中共有"+pondFishList.size()+"种");
				                PondFish senbinfs=pondFishList.get(0);
				                pondfishtype=senbinfs.getType();
				                logger.debug("鱼塘中鱼的种类为："+pondfishtype);
				                int triggeraddresult=addTrigerbyFishtype(sensor.getDevice_sn(), pondfishtype);
				            }else return RESCODE.POND_NO_FISH.getJSONRES();		      
						}
						sensorDao.updateSensor(sensor);
						return RESCODE.SUCCESS.getJSONRES();
					}else {
						return RESCODE.DEVICESNS_REPEAT.getJSONRES();
					}
					*/
					return RESCODE.DEVICESNS_REPEAT.getJSONRES();
				}
			}


	public Map<String, Object> addController(Controller[] controllers) {

		if(controllers.length>0) {
			String device_sn = controllers[0].getDevice_sn();
			logger.debug(device_sn);
			String api_device_sn = device_sn.substring(2, device_sn.length());
			logger.debug(api_device_sn);
			if(deviceDao.findDevice(api_device_sn)==null) {
//				在设备表中，若没有改设备编号，在设备表中添加设备				
				Device device = new Device();
				device.setDevice_sn(api_device_sn);
				device.setType(3);
				deviceDao.save(device);
				logger.debug("添加了一个新的控制器设备");
				for (Controller controller : controllers ){
					//截掉扫码编码的前两位
					controller.setDevice_sn(api_device_sn);					
					//添加增氧机，即添加其默认limit
					if(controller.getType()==0) {
						Limit_Install limit = new Limit_Install();
						limit.setHigh_limit(20);
						limit.setUp_limit(6);
						limit.setLow_limit(4);
						limit.setDevice_sn(controller.getDevice_sn());
						limit.setWay(controller.getPort());
						addTrigger("DO", controller.getDevice_sn(), "<", 4, 3, controller.getPort());
						limitDao.save(limit);
					}
					//获取控制器绑定的塘口
					int[] pondIds = controller.getPondIds();
					logger.debug(pondIds);
					for(int k = 0;k<pondIds.length;k++) {
						Controller con = new Controller();
						con.setDevice_sn(controller.getDevice_sn());
						con.setType(controller.getType());
						con.setPondId(pondIds[k]);
						con.setRelation(controller.getRelation());
						con.setName(controller.getName());
						con.setPort(controller.getPort());
						con.setPondIds(controller.getPondIds());
						logger.debug("添加塘口为"+con.getPondId());
						controllerDao.save(con);
						logger.debug("根据塘口添加控制器");
					}				
				}
				return RESCODE.SUCCESS.getJSONRES();
			}else {
				return RESCODE.DEVICESNS_REPEAT.getJSONRES();
			}														
		}else {
				return RESCODE.NOT_FOUND.getJSONRES();
		}
			
	}
	public Map<String, Object> getControllersBydevice_sn(String device_sn){
		List<Controller> controllerList = controllerDao.findControllerByDeviceSns(device_sn);
		Map<String, Object> returnController = new HashMap<>();
		//获得所有port
		Set<Integer> portSet = new HashSet<>();
		for(Controller controller:controllerList) {
			portSet.add(controller.getPort());
		}
		for(int i:portSet) {
			int j=0;
			//根据设备路获得控制器
			List<Controller> controller_port_List = controllerDao.findControllerByDeviceSnAndWay(device_sn, i);			
			Map<String, Object> port_controller = new HashMap<>();
			port_controller.put("port", i);
			port_controller.put("controller", controller_port_List);
			returnController.put(j+"", port_controller);
			j++;
		}
		
		
		return returnController;
	}
	
	//测试用，具体实现后续完成
	public Map<String, Object> addAio(AIO[] aios) {		
		if(aios.length>0) {
			String device_sn = aios[0].getDevice_sn();
			String api_device_sn = device_sn.substring(2, device_sn.length());
			GetDeviceApi api = new GetDeviceApi(api_device_sn, key);
			BasicResponse<DeviceResponse> response = api.executeApi();
			if(response.errno ==0) {
				for (AIO aio:aios){
					aio.setDevice_sn(aio.getDevice_sn().substring(2, aio.getDevice_sn().length()));
					if(deviceDao.findDevice(aio.getDevice_sn())==null) {
						
						//设备号不存在，添加设备
						Device device = new Device();
						device.setDevice_sn(aio.getDevice_sn());
						device.setType(2);
						deviceDao.save(device);					
										
						logger.debug("在device中添加了一个一体机");
						aioDao.save(aio);
						logger.debug("在aio中添加了一个一体机");
						
						logger.debug("塘口Id:" + aio.getPondId() + "尝试与一体机设备,设备编号为:" + aio.getDevice_sn() + "进行绑定...");
						Pond pond = pondDao.findPondByPondId(aio.getPondId());
						if (pond == null) {
							//塘口不存在
							logger.debug("塘口Id:" + aio.getPondId()+ "在数据库中无记录!!!");
							return RESCODE.NOT_FOUND.getJSONRES();
						}else {
							//塘口存在，添加触发器
							//根据鱼的种类添加触发器1.鱼2.虾3.蟹
							Pond bindpond=pondDao.findPondByPondId(aio.getPondId());
							List<PondFish> pondFishList = pondFishDao.getFishbyPondId(bindpond.getId());
				            int pondfishtype;
				            if (pondFishList.size()!=0){
				            	//塘口中有鱼
				            	logger.debug("塘口中有鱼");
				            	logger.debug("塘口中共有"+pondFishList.size()+"种");
				                PondFish senbinfs=pondFishList.get(0);
				                pondfishtype=senbinfs.getType();
				                logger.debug("鱼塘中鱼的种类为："+pondfishtype);
					            int triggeraddresult=addAioTrigerbyFishtype(aio.getDevice_sn(), pondfishtype,aio.getWay());
					            if (triggeraddresult !=0) {
					            	return RESCODE.TRIGGER_FAILED.getJSONRES();	
					            }else {
					            	 return RESCODE.SUCCESS.getJSONRES();
					            }	
				            }else {
				            	return RESCODE.POND_NO_FISH.getJSONRES();
				            }
				        			            
						}					
						
					}else {
						//设备号存在，不可添加
						return RESCODE.DEVICESNS_INVALID.getJSONRES();	
					}
				}
			}else {
				return RESCODE.DEVICESNS_INVALID.getJSONRES();
			}
		}else {
			return RESCODE.NO_DEVICE.getJSONRES();
		}
		return null;		
	}


	public Sensor realTimeData(String device_sn) {
		logger.debug("获取"+device_sn+"的实时数据");
    	GetLatesDeviceData api = new GetLatesDeviceData(device_sn,key);
        BasicResponse<DeciceLatestDataPoint> response = api.executeApi();
        logger.debug(response.getJson());
        if(response.getErrno()==0) {
        	
        	 List<cmcc.iot.onenet.javasdk.response.device.DeciceLatestDataPoint.DeviceItem.DatastreamsItem> DatastreamsList = response.data.getDevices().get(0).getDatastreams();
        	
        	 //实时数据仅提供给传感器
             if(sensorDao.findSensorByDeviceSns(device_sn)!=null) {//设备为传感器，将水温、溶解氧、pH存入传感器表
             	Sensor sensor = sensorDao.findSensorByDeviceSns(device_sn);
             	for(int i=0;i<DatastreamsList.size();i++) {
             		logger.debug("数据流："+DatastreamsList.get(i).getId());
             		logger.debug("数据流数值："+DatastreamsList.get(i).getValue());
                 	String id = DatastreamsList.get(i).getId();
                 	String v = String.valueOf(DatastreamsList.get(i).getValue());
                 	float value = Float.parseFloat(v);
                 	if(id.equals("pH")) {
                 		sensor.setpH_value(value);
                 	}else if(id.equals("DO")) {
                 		sensor.setOxygen(value);
                 	}else if(id.equals("WT")) {
                 		sensor.setWater_temperature(value);
                 	}  
                 	
                 }
             	logger.debug("将数据存入到传感器中");
             	return sensor;
             }else {
            	/* Sensor sensor = new Sensor();
            	 for(int i=0;i<DatastreamsList.size();i++) {
              		logger.debug("数据流："+DatastreamsList.get(i).getId());
              		logger.debug("数据流数值："+DatastreamsList.get(i).getValue());
                  	String id = DatastreamsList.get(i).getId();
                  	float value = Float.parseFloat((String)DatastreamsList.get(i).getValue());
                  	if(id.equals("pH")) {
                  		sensor.setpH_value(value);
                  	}else if(id.equals("DO")) {
                  		sensor.setOxygen(value);
                  	}else if(id.equals("WT")) {
                  		sensor.setWater_temperature(value);
                  	}  
                  	
                  }*/
            	 logger.debug("数据库中不存在该设备");
            	 return null;
             }
        }else {
        	return null;
        }
	}
	
	public Map<String, Object> myEquipment(String relation) {
		List<Sensor> sensors = sensorDao.querySensorByNameAndRelation(relation, null);
 		List<AIO> aios = aioDao.queryAIOByNameAndRelation(relation, null);
		List<Controller> controllers = controllerDao.queryControllerByNameAndRelation(relation, null);
		List<String> conDSN = new ArrayList<String>();//控制器设备编号
		List<String> aioDSN = new ArrayList<String>();//一体机设备编号
		//type:1.传感器2.一体机3.控制器
		//控制器
		//遍历获得控制器设备编号
		if(controllers!=null) {
			for(Controller controller:controllers) {
				if(conDSN==null) {
					conDSN.add(controller.getDevice_sn());
				}else {
					boolean flag = true;
					for(String s:conDSN) {
						if(s.equals(controller.getDevice_sn())) {
							flag = false;
							break;
						}
					}
					if(flag) {
						conDSN.add(controller.getDevice_sn());
					}
				}
			}
		}
		//遍历获得一体机设备编号
		if(aios!=null) {
			for(AIO aio:aios) {
				if(aioDSN==null) {
					aioDSN.add(aio.getDevice_sn());
				}else {
					boolean flag = true;
					for(String s:aioDSN) {
						if(s.equals(aio.getDevice_sn())) {
							flag = false;
							break;
						}
					}
					if(flag) {
						aioDSN.add(aio.getDevice_sn());
					}
				}
			}
		}
		
		//根据设备编号获得控制器各路的具体参数
		List<Object> ConResult = new ArrayList<>();
		for(String s:conDSN) {//根据设备编号处理数据
			Map<String, Object> ConResultSE = new HashMap<>();
			//获得编号下所有controller
			List<Controller> controllerAll = new ArrayList<>();
			for(Controller con:controllers) {
				if(con.getDevice_sn().equals(s)) {
					controllerAll.add(con);
				}				
			}
			//获得所有port
			Set<Integer> portSet = new HashSet<>();
			for(Controller con:controllerAll) {
				portSet.add(con.getPort());
			}
			//根据port获得controller
			List<Controller> cl = new ArrayList<>();
			for(int port:portSet) {	
				Controller conNew  = new Controller();
				List<Controller> conList1 = new ArrayList<>();
				for(Controller con:controllerAll) {					
					if(con.getPort() == port) {
						conList1.add(con);
					}
				}
				//获得一路下的所有塘口
				//将设备编号下的塘口id取出放入list中
				List<Integer> pondIdList = new ArrayList<>();
				for(Controller con:conList1) {
					if(con.getDevice_sn().equals(s)) {
						pondIdList.add(con.getPondId());
					}
				}
				//将list转成数组ai
				Object[] a = (Object[])pondIdList.toArray();
				int[] ai = new int[a.length];
				for(int j=0;j<ai.length;j++) {
					ai[j] = (int)a[j];
				}
				
				if(conList1.size()>0) {
					conNew = conList1.get(0);
					conNew.setPondIds(ai);
					cl.add(conNew);
				}
								
			}
			ConResultSE.put("id", s);		
			ConResultSE.put("content", cl);
			ConResultSE.put("type", 3);
			ConResult.add(ConResultSE);
		}
		//一体机
		//遍历获得所有一体机设备编号
		
		
		//根据设备编号获得一体机各路的具体参数
		List<Object> AioResult = new ArrayList<>();
		
		for(String s:aioDSN) {
			Map<String, Object> AioResultSE = new HashMap<>();
			List<AIO> al = new ArrayList<AIO>();
			for(AIO aio:aios) {
				if(aio.getDevice_sn().equals(s)) {
					al.add(aio);
				}
			}
			AioResultSE.put("id", s);
			AioResultSE.put("content", al);
			AioResultSE.put("type",2);
			logger.debug("id:"+s);
			AioResult.add(AioResultSE);
		}		
		
		List<Object> senResult = new ArrayList<>();
		
		for(Sensor s:sensors) {
			Map<String, Object> senResultSE = new HashMap<>();
			List<Sensor> sl = new ArrayList<Sensor>();
			sl.add(s);
			senResultSE.put("id", s.getDevice_sn());
			senResultSE.put("content", sl);
			senResultSE.put("type", 1);
			senResult.add(senResultSE);
		}
	
		Map<String, Object> result = RESCODE.SUCCESS.getJSONRES();
		result.put("controller", ConResult);
		result.put("aio", AioResult);
		result.put("sensor", senResult);
		if (relation != null && relation.length() > 0) {
			if (relation.contains("WX")) {
				WXUser wxUser = wxUserDao.findUserByRelation(relation);
				result.put("user", wxUser == null ? "" : wxUser.getName());
			} else if (relation.contains("CO")) {
				Company company = companyDao.findCompanyByRelation(relation);
				result.put("user", company == null ? "" : company.getName());
			} else {
				result.put("user", "");
			}
		} else {
			result.put("user", "");
		}
		return result;
	}

	public Map<String, Object> setLimit(Limit_Install limit_Install){
			limitDao.updateLimit(limit_Install);
		/*	dev_triggerDao.*/
			int result1 = addTrigger("DO", limit_Install.getDevice_sn(), "<", limit_Install.getLow_limit(), 2,limit_Install.getWay());
			if(result1==1) {
				return RESCODE.SUCCESS.getJSONRES();
			}else {
				return RESCODE.TRIGGER_FAILED.getJSONRES();
			}
			
	/*	}	*/	
	}
	
	/*public Map<String, Object> adminFindEquipment(String device_sn, String userName, int page, int number) {
		int from = (page - 1) * number;
		if ((device_sn == null || device_sn.length() < 0) && (userName == null || userName.length() < 0)) {
			return noConditionsQuery(from, number);
		}
		if ((device_sn == null || device_sn.length() < 0)
				&& (userName != null && !userName.isEmpty() && !userName.trim().isEmpty())) {
			return nameConditionQuery(userName, from, number);
		}
		if ((device_sn != null && !device_sn.isEmpty() && !device_sn.trim().isEmpty())
				&& (userName == null || userName.length() < 0)) {
			return deviceSnConditionQuery(device_sn, from, number);
		}
		return doubleConditionQuery(device_sn, userName, from, number);
	}

	private Map<String, Object> doubleConditionQuery(String device_sn, String userName, int from, int number) {
		List<Equipment> equipments = new ArrayList<>();
		List<Company> companies = companyDao.companies(userName);
		List<WXUser> wxUsers = wxUserDao.wxUsers(userName);
		List<String> relations = new ArrayList<>();
		for (Company company : companies) {
			relations.add(company.getRelation());
		}
		for (WXUser wxUser : wxUsers) {
			relations.add(wxUser.getRelation());
		}
		if (relations.isEmpty()) {
			return RESCODE.SUCCESS.getJSONRES(equipments, 0, 0);
		} else {
			equipments = pondDao.adminFindEquipmentDouble(device_sn, relations, from, number);
			shareDealMethod(equipments);
			long count = pondDao.adminFindEquipmentCountDouble(device_sn, relations);
			int size = (int) Math.ceil(count / (double) number);
			return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
		}
	}*/

/*	private Map<String, Object> deviceSnConditionQuery(String device_sn, int from, int number) {
		List<Equipment> equipments = pondDao.adminFindEquipmentBySn(device_sn);
		shareDealMethod(equipments);
		long count = pondDao.adminFindEquipmentCountSn(device_sn);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
	}

	private Map<String, Object> nameConditionQuery(String userName, int from, int number) {
		List<Equipment> equipments = new ArrayList<>();
		List<Company> companies = companyDao.companies(userName);
		List<WXUser> wxUsers = wxUserDao.wxUsers(userName);
		List<String> relations = new ArrayList<>();
		for (Company company : companies) {
			relations.add(company.getRelation());
		}
		for (WXUser wxUser : wxUsers) {
			relations.add(wxUser.getRelation());
		}
		if (relations.isEmpty()) {
			return RESCODE.SUCCESS.getJSONRES(equipments, 0, 0);
		} else {
			equipments = pondDao.adminFindEquipmentByName(relations, from, number);
			shareDealMethod(equipments);
			long count = pondDao.adminFindEquipmentCountName(relations);
			int size = (int) Math.ceil(count / (double) number);
			return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
		}
	}*/

	/*private List<Equipment> shareDealMethod(List<Equipment> equipments) {
		for (Equipment equipment : equipments) {
			type = equipment.getDevice_sn().substring(0, 2);
			switch (type) {
				case "01":
					aio = aioDao.findAIOByDeviceSns(equipment.getDevice_sn());
					relation = aio.getRelation();
					if (relation == null) {
						equipment.setName("");
						equipment.setRelation("0");
					} else {
						if (relation.contains("CO")) {
							company = companyDao.findCompanyByRelation(relation);
							if (company == null) {
								equipment.setName("");
								equipment.setRelation("0");
							} else {
								equipment.setUserName(company.getName());
								equipment.setRelation(relation);
							}
						} else if (relation.contains("WX")) {
							wxUser = wxUserDao.findUserByRelation(relation);
							if (wxUser == null) {
								equipment.setName("");
								equipment.setRelation("0");
							} else {
								equipment.setUserName(wxUser.getName());
								equipment.setRelation(relation);
							}
						} else {
							equipment.setName("");
							equipment.setRelation("0");
						}
					}
					break;
				case "02":
					aio = aioDao.findAIOByDeviceSns(equipment.getDevice_sn());
					relation = aio.getRelation();
					if (relation == null) {
						equipment.setName("");
						equipment.setRelation("0");
					} else {
						if (relation.contains("CO")) {
							company = companyDao.findCompanyByRelation(relation);
							if (company == null) {
								equipment.setName("");
								equipment.setRelation("0");
							} else {
								equipment.setUserName(company.getName());
								equipment.setRelation(relation);
							}
						} else if (relation.contains("WX")) {
							wxUser = wxUserDao.findUserByRelation(relation);
							if (wxUser == null) {
								equipment.setName("");
								equipment.setRelation("0");
							} else {
								equipment.setUserName(wxUser.getName());
								equipment.setRelation(relation);
							}
						} else {
							equipment.setName("");
							equipment.setRelation("0");
						}
					}
					break;
				case "03":
					sensor = sensorDao.findSensorByDeviceSns(equipment.getDevice_sn());
					relation = sensor.getRelation();
					if (relation == null) {
						equipment.setName("");
						equipment.setRelation("0");
					} else {
						if (relation.contains("CO")) {
							company = companyDao.findCompanyByRelation(relation);
							if (company == null) {
								equipment.setName("");
								equipment.setRelation("0");
							} else {
								equipment.setUserName(company.getName());
								equipment.setRelation(relation);
							}
						} else if (relation.contains("WX")) {
							wxUser = wxUserDao.findUserByRelation(relation);
							if (wxUser == null) {
								equipment.setName("");
								equipment.setRelation("0");
							} else {
								equipment.setUserName(wxUser.getName());
								equipment.setRelation(relation);
							}
						} else {
							equipment.setName("");
							equipment.setRelation("0");
						}
					}
					break;
				case "04":
					controller = controllerDao.findControllerByDeviceSns(equipment.getDevice_sn());
					relation = controller.getRelation();
					if (relation == null) {
						equipment.setName("");
						equipment.setRelation("0");
					} else {
						if (relation.contains("CO")) {
							company = companyDao.findCompanyByRelation(relation);
							if (company == null) {
								equipment.setName("");
								equipment.setRelation("0");
							} else {
								equipment.setUserName(company.getName());
								equipment.setRelation(relation);
							}
						} else if (relation.contains("WX")) {
							wxUser = wxUserDao.findUserByRelation(relation);
							if (wxUser == null) {
								equipment.setName("");
								equipment.setRelation("0");
							} else {
								equipment.setUserName(wxUser.getName());
								equipment.setRelation(relation);
							}
						} else {
							equipment.setName("");
							equipment.setRelation("0");
						}
					}
					break;
				default:
					break;
			}
		}
		return equipments;
	}*/

/*	private Map<String, Object> noConditionsQuery(int from, int number) {
		List<Equipment> equipments = pondDao.adminFindEquipmentAll(from, number);
		shareDealMethod(equipments);
		long count = pondDao.adminFindEquipmentCountAll();
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
	}
*/
	public Map<String, Object> companyFindEquipment(String device_sn, String relation, int page, int number) {
		int from = (page - 1) * number;
		Sensor sensor = null;
		Company company = companyDao.findCompanyByRelation(relation);
		if (company == null) {
			return RESCODE.NOT_FOUND.getJSONRES();
		} else {
			List<String> relations = new ArrayList<>();
			relations.add(company.getRelation());
			if (device_sn == null || device_sn.length() < 0) {
				List<Equipment> equipments = pondDao.adminFindEquipmentByName(relations, from, number);
				for (Equipment equipment : equipments) {
					String type = equipment.getDevice_sn().substring(0, 2);
					if (type.equals("03")) {
						sensor = sensorDao.findSensorByDeviceSns(equipment.getDevice_sn());
						if (sensor == null) {
							equipment.setSensorId(0);
						} else {
							equipment.setSensorId(sensor.getId());
						}
					} else {
						equipment.setSensorId(0);
					}
				}
				long count = pondDao.adminFindEquipmentCountName(relations);
				int size = (int) Math.ceil(count / (double) number);
				Map<String, Object> map = RESCODE.SUCCESS.getJSONRES(equipments, size, count);
				map.put("user", company.getName());
				return map;
			} else {
				List<Equipment> equipments = pondDao.adminFindEquipmentDouble(device_sn, relations, from, number);
				long count = pondDao.adminFindEquipmentCountDouble(device_sn, relations);
				int size = (int) Math.ceil(count / (double) number);
				Map<String, Object> map = RESCODE.SUCCESS.getJSONRES(equipments, size, count);
				map.put("user", company.getName());
				return map;
			}
		}
	}

	public Map<String, Object> dataToday(String device_sn, int way) {
		logger.debug(device_sn);
		Map<String, Object> mapReturn = new HashMap();
		//数据的获取针对传感器，传感器不需要way，way为一体机留用
		//获得当天数据，从零点开始00:00:00
		Date date = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
		String dataFormat1 = sdf1.format(date);
		String dataFormat2  = sdf2.format(date);
		String dataFormatEnd = dataFormat1+"T"+dataFormat2;
		String dataFormatStart = dataFormat1+"T00:00:00";
		logger.debug(dataFormatStart);
		logger.debug(dataFormatEnd);
		GetDatapointsListApi api = new GetDatapointsListApi(null, dataFormatStart, dataFormatEnd, device_sn, null, 6000, null, 137,
				null, null, null, key);
		BasicResponse<DatapointsList> response = api.executeApi();
		List<DatastreamsItem> dl= response.getData().getDevices();
		logger.debug("获取当天数据");
		logger.debug("参数个数："+dl.size());
		logger.debug("总共获得数据量为："+response.getData().getCount());
		
		for(int i=0;i<dl.size();i++) {				
			DatastreamsItem di = dl.get(i);
			List<DatapointsItem> ld =di.getDatapoints();
			
			logger.debug(di.getId()+"参数下数据量："+ld.size());
			
			if("DO".equals(di.getId())) {
				List<DatapointsItem> ldNew =new ArrayList<>();
		
				for(int j=0;j<ld.size();j+=6) {//数据5min*6=半小时发一次
					ldNew.add(ld.get(j));					
				}
				List<Float> value = new ArrayList<>();
				List<String> at = new ArrayList<>();
				for(int j=0;j<ldNew.size();j+=6) {//数据5min*6=半小时发一次
					value.add(Float.parseFloat((String) ldNew.get(j).getValue()) );	
					at.add(ldNew.get(j).getAt());
				}
				Map<String, Object> singlemap = new HashMap<>();
				singlemap.put("value", value);
				singlemap.put("at", at);
				mapReturn.put("DO", singlemap);
				/*以时间划分
				 * String id = (String) ld.get(0).getValue();
				String at = (String) ld.get(0).getAt();
				String time = at.substring(at.indexOf(" "), at.length());
				logger.debug(time);
				for(int j=1;j<ld.size();j++) {
					id = (String) ld.get(j).getValue();
					 at = (String) ld.get(j).getAt();
					time = at.substring(at.indexOf(" "), at.length());
					logger.debug(time);
				}*/
			}else if("WT".equals(di.getId())) {
				List<DatapointsItem> ldNew =new ArrayList<>();
				for(int j=0;j<ld.size();j+=6) {//数据5min*6=半小时发一次
					ldNew.add(ld.get(j));
				}
				List<Float> value = new ArrayList<>();
				List<String> at = new ArrayList<>();
				for(int j=0;j<ldNew.size();j+=6) {//数据5min*6=半小时发一次
					value.add(Float.parseFloat((String)ldNew.get(j).getValue()) );	
					at.add(ldNew.get(j).getAt());
				}
				Map<String, Object> singlemap = new HashMap<>();
				singlemap.put("value", value);
				singlemap.put("at", at);
				mapReturn.put("WT", singlemap);
			}else if("pH".equals(di.getId())) {
				List<DatapointsItem> ldNew =new ArrayList<>();
				for(int j=0;j<ld.size();j+=6) {//数据5min*6=半小时发一次
					ldNew.add(ld.get(j));					
				}
				List<Float> value = new ArrayList<>();
				List<String> at = new ArrayList<>();
				for(int j=0;j<ldNew.size();j+=6) {//数据5min*6=半小时发一次
					value.add(Float.parseFloat((String)ldNew.get(j).getValue()));	
					at.add(ldNew.get(j).getAt());
				}
				Map<String, Object> singlemap = new HashMap<>();
				singlemap.put("value", value);
				singlemap.put("at", at);
				mapReturn.put("pH", singlemap);
			}
							
		}
		//System.out.println(response.getJson());
		
		/*if(sensorDao.findSensorByDeviceSns(device_sn) != null) {
			controllerDao  sensor.getPondId();
		}*/
		Sensor sensor = sensorDao.findSensorByDeviceSns(device_sn);
		if(sensor!=null) {
			logger.debug(sensor.getPondId());
		    List<Controller> controllerList= controllerDao.findByPondId(sensor.getPondId());
		    Controller conOxygen = new Controller();
			for(Controller controller:controllerList) {
				if(controller.getType()==0) {
					conOxygen = controller;
					break;
				}
			}
			Limit_Install limit = limitDao.findLimitByDeviceSnsAndWay(conOxygen.getDevice_sn(), conOxygen.getPort());
			mapReturn.put("Limit", limit);
		}
		return mapReturn;
	}
	
	public Map<String, Object> dataYesterday(String device_sn) {
		Map<String, Object> mapReturn = new HashMap();
		//数据的获取针对传感器，传感器不需要way，way为一体机留用
		//获得昨天数据，从零点开始00:00:00
		Date dateToday = new Date();
		Date dateYesterday = new Date();
		dateYesterday.setDate(dateYesterday.getDate()-1);
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		String dataFormat1 = sdf1.format(dateYesterday);
		String dataFormatStart = dataFormat1+"T00:00:00";
		dataFormat1 = sdf1.format(dateToday);
		String dataFormatEnd =  dataFormat1+"T00:00:00";
		GetDatapointsListApi api = new GetDatapointsListApi(null, dataFormatStart, dataFormatEnd, device_sn, null, 6000, null, 137,
				null, null, null, key);
		BasicResponse<DatapointsList> response = api.executeApi();
		List<Object> returnList = new ArrayList<Object>();	
		Map<String, Object> oneMap = new HashMap<String, Object>();
		int count = Integer.parseInt(response.data.getCount()) ;
		if(response.errno == 0 && count!=0) {
			List<DatastreamsItem> datastreamsItemList = response.data.getDevices();
			List<DatastreamsItem> dl= response.getData().getDevices();
			/*System.out.println("参数个数："+dl.size());
			
			System.out.println("总共获得数据量为：");
			System.out.println(response.getData().getCount());*/
			
			for(int i=0;i<dl.size();i++) {
				List<String> atList = new ArrayList<String>();
				List<Float> valueList = new ArrayList<Float>();
				DatastreamsItem di = dl.get(i);
				List<DatapointsItem> ld =di.getDatapoints();
				System.out.println(di.getId()+"参数下数据量："+ld.size());
					for(int j=0;j<ld.size();j++) {
						atList.add(ld.get(j).getAt());
						valueList.add(Float.parseFloat((String)ld.get(j).getValue()));
					}
				
				Map<String, Object> oneValueMap = new HashMap<String, Object>();
				oneValueMap.put("at", atList);
				oneValueMap.put("value",valueList);
				oneMap.put(di.getId(), oneValueMap);
			}
		}
		return oneMap;
	}

	public Map<String, List<DatapointsItem>> data3days(String device_sn, int way) {
		Date date = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss");
		SimpleDateFormat sdf3 = new SimpleDateFormat("dd");
		String dataFormat1 = sdf1.format(date);
		String dataFormat2  = sdf2.format(date);
		String dates = sdf3.format(date);
		String dataFormatEnd = dataFormat1+"-"+dates+"T"+dataFormat2;
		date.setDate(date.getDate()-2);
		dates = sdf3.format(date);
		String dataFormatStart = dataFormat1+"-"+dates+"T00:00:00";
		
		Map<String, List<DatapointsItem>> data3days = new HashMap<>();
		
		GetDatapointsListApi api1 = new GetDatapointsListApi("pH", dataFormatStart, dataFormatEnd, device_sn,null,6000, null,null,
				null, null, null, key);
		BasicResponse<DatapointsList> response1 = api1.executeApi();
		
		logger.debug(response1.getJson());
		int count = Integer.parseInt(response1.data.getCount()) ;
		if(response1.errno == 0 && count!=0) {
			DatastreamsItem di1  = response1.data.getDevices().get(0);
			String id1 =  di1.getId();
			List<DatapointsItem> dpil1 = di1.getDatapoints();
			List<DatapointsItem> dpil11 =new ArrayList<>();
			for(int j=0;j<dpil1.size();j+=18) {//数据5min*18=一个半小时发一次
				dpil11.add(dpil1.get(j));					
			}
			data3days.put(id1, dpil11);
		}
		
		
		GetDatapointsListApi api2 = new GetDatapointsListApi("DO", dataFormatStart, dataFormatEnd, device_sn,null,6000, null,null,
				null, null, null, key);
		BasicResponse<DatapointsList> response2 = api2.executeApi();
		count = Integer.parseInt(response2.data.getCount()) ;
		if(response2.errno == 0 && count!=0) {
			DatastreamsItem di2  = response2.data.getDevices().get(0);
			String id2 =  di2.getId();
			List<DatapointsItem> dpil2 = di2.getDatapoints();
			List<DatapointsItem> dpil12 =new ArrayList<>();
			for(int j=0;j<dpil2.size();j+=18) {//数据5min*18=一个半小时发一次
				dpil12.add(dpil2.get(j));					
			}
			data3days.put(id2, dpil12);
		}
		
		
		GetDatapointsListApi api3 = new GetDatapointsListApi("WT", dataFormatStart, dataFormatEnd, device_sn,null,6000, null,null,
				null, null, null, key);
		BasicResponse<DatapointsList> response3 = api3.executeApi();
		count = Integer.parseInt(response3.data.getCount()) ;
		if(response3.errno == 0 && count!=0) {
			DatastreamsItem di3  = response3.data.getDevices().get(0);
			String id3 =  di3.getId();
			List<DatapointsItem> dpil3 = di3.getDatapoints();
			List<DatapointsItem> dpil13 =new ArrayList<>();
			for(int j=0;j<dpil3.size();j+=18) {//数据5min*18=一个半小时发一次
				dpil13.add(dpil3.get(j));					
			}
			data3days.put(id3, dpil13);
		}
		
		
//		Sensor sensor = sensorDao.findSensorByDeviceSns(device_sn);
//		if(sensor!=null) {
//			logger.debug(sensor.getPondId());
//		    List<Controller> controllerList= controllerDao.findByPondId(sensor.getPondId());
//		    Controller conOxygen = new Controller();
//			for(Controller controller:controllerList) {
//				if(controller.getType()==0) {
//					conOxygen = controller;
//					break;
//				}
//			}
//			Limit_Install limit = limitDao.findLimitByDeviceSnsAndWay(conOxygen.getDevice_sn(), conOxygen.getPort());
//			data3days.put("Limit", limit);
//		}
		return data3days;
	}
	
	
	
	public Map<String, Object> dataAll(String device_sn, int way,int day) {
		List<Sensor_Data> list = new ArrayList<>();
		if (way > 0) {
			list = sensor_DataDao.sevenData(device_sn, way,day);
		} else {
			list = sensor_DataDao.sevenData(device_sn,day);
		}
		List<PH> phs = new ArrayList<>();
		List<Oxygen> oxygens = new ArrayList<>();
		List<Temperature> temperatures = new ArrayList<>();
		PH ph = null;
		Oxygen oxygen = null;
		Temperature temperature = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//SimpleDateFormat isSameDay = new SimpleDateFormat("MM-dd");
		//String temp = "";
		List<Sensor_Data> splitlist = new ArrayList<>();
		int i = 0;
		while (i < 2016) {
			try {
				splitlist.add(list.get(i));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				break;
			}
			i = i + 8;
		}
		if (!list.isEmpty()) {
		//	addVirtualData(splitlist);
			for (Sensor_Data sensor_Data : splitlist) {
				/*if (!temp.equals(isSameDay.format(sensor_Data.getReceiveTime()))) {
					temp = isSameDay.format(sensor_Data.getReceiveTime());
					ph = new PH(sensor_Data.getpH_value(), isSameDay.format(sensor_Data.getReceiveTime()));
					oxygen = new Oxygen(sensor_Data.getOxygen(), isSameDay.format(sensor_Data.getReceiveTime()));
					temperature = new Temperature(sensor_Data.getWater_temperature(),
							isSameDay.format(sensor_Data.getReceiveTime()));
					phs.add(ph);
					oxygens.add(oxygen);
					temperatures.add(temperature);
				} else {*/
				ph = new PH(sensor_Data.getpH_value(), format.format(sensor_Data.getReceiveTime()));
				oxygen = new Oxygen(sensor_Data.getOxygen(), format.format(sensor_Data.getReceiveTime()));
				temperature = new Temperature(sensor_Data.getWater_temperature(),
						format.format(sensor_Data.getReceiveTime()));
				phs.add(ph);
				oxygens.add(oxygen);
				temperatures.add(temperature);
				//}
			}
		}

		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
		map.put("phs", phs);
		map.put("oxygens", oxygens);
		map.put("temperatures", temperatures);
		return map;//
	}

	public Map<String, Object> pcDataToday(String device_sn, int way) {
		List<Sensor_Data> list = new ArrayList<>();
		if (way > 0) {
			list = sensor_DataDao.today(device_sn, way);
		} else {
			list = sensor_DataDao.today(device_sn);
		}
		//addVirtualData(list);
		List<PH> phs = new ArrayList<>();
		List<Oxygen> oxygens = new ArrayList<>();
		List<Temperature> temperatures = new ArrayList<>();
		PH ph = null;
		Oxygen oxygen = null;
		Temperature temperature = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Sensor_Data sensor_Data = null;
		for (int i = RandomUtils.nextInt(6); i < 288; i = i + 6) {
			try {
				sensor_Data = list.get(i);
				ph = new PH(sensor_Data.getpH_value(), format.format(sensor_Data.getReceiveTime()));
				oxygen = new Oxygen(sensor_Data.getOxygen(), format.format(sensor_Data.getReceiveTime()));
				temperature = new Temperature(sensor_Data.getWater_temperature(),
						format.format(sensor_Data.getReceiveTime()));
				phs.add(ph);
				oxygens.add(oxygen);
				temperatures.add(temperature);
			} catch (Exception e) {
				break;
			}
		}
		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
		map.put("phs", phs);
		map.put("oxygens", oxygens);
		map.put("temperatures", temperatures);
		return map;//
	}

	public Map<String, Object> pcDataAll(String device_sn, int way,int day) {
		List<Sensor_Data> list = new ArrayList<>();
		if (way > 0) {
			list = sensor_DataDao.sevenData(device_sn, way,day);
		} else {
			list = sensor_DataDao.sevenData(device_sn,day);
		}
		List<PH> phs = new ArrayList<>();
		List<Oxygen> oxygens = new ArrayList<>();
		List<Temperature> temperatures = new ArrayList<>();
		PH ph = null;
		Oxygen oxygen = null;
		Temperature temperature = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//SimpleDateFormat isSameDay = new SimpleDateFormat("MM-dd");
		//String temp = "";
		List<Sensor_Data> splitlist = new ArrayList<>();
		int i = 0;
		while (i < 2016) {
			try {
				splitlist.add(list.get(i));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				break;
			}
			i = i + 8;
		}
		//addVirtualData(splitlist);
		for (Sensor_Data sensor_Data : splitlist) {
			/*if (!temp.equals(isSameDay.format(sensor_Data.getReceiveTime()))) {
				temp = isSameDay.format(sensor_Data.getReceiveTime());
				ph = new PH(sensor_Data.getpH_value(), isSameDay.format(sensor_Data.getReceiveTime()));
				oxygen = new Oxygen(sensor_Data.getOxygen(), isSameDay.format(sensor_Data.getReceiveTime()));
				temperature = new Temperature(sensor_Data.getWater_temperature(),
						isSameDay.format(sensor_Data.getReceiveTime()));
				phs.add(ph);
				oxygens.add(oxygen);
				temperatures.add(temperature);
			} else {*/
			ph = new PH(sensor_Data.getpH_value(), format.format(sensor_Data.getReceiveTime()));
			oxygen = new Oxygen(sensor_Data.getOxygen(), format.format(sensor_Data.getReceiveTime()));
			temperature = new Temperature(sensor_Data.getWater_temperature(),
					format.format(sensor_Data.getReceiveTime()));
			phs.add(ph);
			oxygens.add(oxygen);
			temperatures.add(temperature);
			/*}*/
		}

		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
		map.put("phs", phs);
		map.put("oxygens", oxygens);
		map.put("temperatures", temperatures);
		return map;//
	}

	public Map<String, Object> addTimer(Timer timer) {
		timerDao.save(timer);
		return RESCODE.SUCCESS.getJSONRES();
	}
	
	public List<Timer> getTimerByDevice_snandWay(String device_sn,int way){
		List<Timer> timerList = timerDao.findTimerByDeviceSnAndWay(device_sn, way);
		return timerList;
	}
	
	public Map<String, Object> modifyTimer(Timer timer) {
		timerDao.updateTimer(timer);
		return RESCODE.SUCCESS.getJSONRES();

	}
	
	public Map<String, Object> delTimer(String device_sn,int way) {
		timerDao.delete(device_sn, way);
		return RESCODE.SUCCESS.getJSONRES();

	}
	

	public Map<String, Object> modifyEquipment(String device_sn, Object newEquipment) {
		newEquipment.getClass().getName();
		/*String type = null;
		Map<String, Object> map = null;
		try {
			type = device_sn.substring(0, 2);
		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		if (type.equals("01") || type.equals("02")) {
			AIO aio = aioDao.findAIOByDeviceSns(device_sn);
			aio.setName(name);
			map = RESCODE.SUCCESS.getJSONRES();
			map.put("equipment", aio);
		} else if (type.equals("03")) {
			Sensor sensor = sensorDao.findSensorByDeviceSns(device_sn);
			sensor.setName(name);
			map = RESCODE.SUCCESS.getJSONRES();
			map.put("equipment", sensor);
		} else if (type.equals("04")) {
			Controller controller = controllerDao.findControllerByDeviceSns(device_sn);
			controller.setName(name);
			map = RESCODE.SUCCESS.getJSONRES();
			map.put("equipment", controller);
		}

		return map;*/
		return null;
	}
	
	public Map<String, Object> modifySensor(Sensor...sensors){
		boolean flag = false;
		for(Sensor sensor:sensors) {
			if(deviceDao.findDevice(sensor.getDevice_sn())==null) {
				flag = false;
			}else {
				sensorDao.updateSensor(sensor);
				flag = true;
			}
		}
		if(flag) {
			return RESCODE.SUCCESS.getJSONRES();
		}else {
			return RESCODE.ACCOUNT_NOT_EXIST.getJSONRES();
		}
		
	}
	
	public Map<String, Object> modifyAio(AIO...aios){
		if(aios.length>0) {
			String DSN = aios[0].getDevice_sn();
			boolean flag = true;
			//判断一体机是否存在
			if(deviceDao.findDevice(DSN)==null) {
				flag = false;
			}
			/*if(aioDao.findAIOByDeviceSns(DSN)==null) {
				flag = false;
			}*/
			//判断是否为一个一体机的多路
			for(AIO aio:aios) {
				if(aio.getDevice_sn().equals(DSN)==false) {
					flag = false;
					break;
				}
			}
			if(flag) {
				for(AIO aio:aios) {
					logger.debug("进入aio循环");
				//	aioDao.updateByAioId(aio);
					aioDao.update(aio);
				}
				return RESCODE.SUCCESS.getJSONRES();
			}else {
				return RESCODE.WRONG_PARAM.getJSONRES();
			}
		}else {
			return RESCODE.NO_DEVICE.getJSONRES();
		}
		
	}
	
	public Map<String, Object> modifyController(Controller...controller){
		//传入controller对象需要device_sn,port,pondIds,type
		//修改时控制器至少存在一路
		if(controller.length>0) {
			String device_sn = controller[0].getDevice_sn();
			logger.debug("修改设备编码为："+device_sn);
			//查看设备是否存在
			if(deviceDao.findDevice(device_sn)==null) {
				return RESCODE.NOT_FOUND.getJSONRES();
			}else {			
				for(int i=0;i<controller.length;i++) {
					Controller con = controller[i];
					List<Controller> conList = controllerDao.findControllerByDeviceSnAndWay(con.getDevice_sn(), con.getPort());
					for(Controller co:conList) {						
						controllerDao.delete(co.getId());						
					}					
					int[] s = con.getPondIds();
					for(int j=0;j<s.length;j++) {
						Controller cont = new Controller();
						cont.setDevice_sn(con.getDevice_sn());
						cont.setType(con.getType());
						cont.setPondId(s[j]);
						cont.setRelation(con.getRelation());
						cont.setName(con.getName());
						cont.setPort(con.getPort());
						cont.setPondIds(con.getPondIds());
						
						controllerDao.save(cont);
						
					}
				}			
				return RESCODE.SUCCESS.getJSONRES();
				
			}
		}else {
			return RESCODE.NO_DEVICE.getJSONRES();
		}
		
	} 

	/*
	 * 获取limit_install
	 */
	public List<Limit_Install> queryLimitByDeviceSn(String device_sn){
		List<Limit_Install> limitList = limitDao.queryLimitByDeviceSn(device_sn);
		return limitList;
	}
	
	/*
	 * 触发器触发，改变传感器状态
	 */	
	public void triggeractive(JSONObject data) {
		System.out.println("触发器"+data);
		/*JSONObject tempjson = JSONObject.fromObject(data);*/
		JSONObject tempjson = data;
		JSONObject temp12 = tempjson.getJSONObject("trigger");
		//获得触发器id,用于获得触发器类型（预警/危险）,设定传感器状态
		int triggerid=temp12.getInt("id");		
		String triggertype=temp12.getString("type");
		JSONArray currentdata=tempjson.getJSONArray("current_data");
		JSONObject obj1 = currentdata.getJSONObject(0);
		String dev_id= obj1.getString("dev_id");//设备编号
		String ds_id= obj1.getString("ds_id");//数据流
		String value = obj1.getString("value");//触发时的值，不能直接使用，分数值和map
		double va = Double.parseDouble(value);
		
		System.out.println(dev_id);
		System.out.println(ds_id);
		System.out.println("触发器类型"+triggertype);
		System.out.println(""+triggerid);
		System.out.println("数值为："+value);
		//根据触发器id获得触发器，根据设备编号获得传感器（传感器只有一路），为传感器设置状态
		//状态值0：正常，1：预警，2：危险
		Dev_Trigger trigger = dev_triggerDao.findTriggerBytriggerId(triggerid);
		if(trigger!=null) {
			System.out.println("触发器本地类型："+trigger.getTrigertype());
			Sensor sensor = sensorDao.findSensorByDeviceSns(trigger.getDevice_sn());
			Sensor sensorData = realTimeData(sensor.getDevice_sn());
			sensor.setOxygen(sensorData.getOxygen());
			sensor.setpH_value(sensorData.getpH_value());
			sensor.setWater_temperature(sensorData.getWater_temperature());
			
			if(triggertype.equals("inout")) {//触发器为inout，
				JSONObject threshold = temp12.getJSONObject("threshold");
				double lolmt = threshold.getDouble("lolmt");
				double uplmt = threshold.getDouble("uplmt");
				if(va>=lolmt&&va<=uplmt) {//触发值在触发范围内
					if(trigger.getTrigertype() == 0) {//预警
						if(ds_id.equals("DO")) {
							sensor.setOxygen_status(1);
						}else if(ds_id.equals("WT")) {
							sensor.setWT_status(1);
						}else if(ds_id.equals("pH")) {
							sensor.setpH_status(1);
						}	
					}else if(trigger.getTrigertype() == 1){//危险
						if(ds_id.equals("DO")) {
							sensor.setOxygen_status(2);	
						}else if(ds_id.equals("WT")) {
							sensor.setWT_status(2);
						}else if(ds_id.equals("pH")) {
							sensor.setpH_status(2);
						}
					}else if(trigger.getTrigertype() == 4) {//正常
						if(ds_id.equals("DO")) {
							sensor.setOxygen_status(0);	
						}else if(ds_id.equals("WT")) {
							sensor.setWT_status(0);
						}else if(ds_id.equals("pH")) {
							sensor.setpH_status(0);
						}
					}
				}else {//触发值离开触发范围，默认恢复正常
					if(ds_id.equals("DO")) {
						sensor.setOxygen_status(0);
					}else if(ds_id.equals("WT")) {
						sensor.setWT_status(0);
					}else if(ds_id.equals("pH")) {
						sensor.setpH_status(0);
					}	
				}
			}else {//触发器类型不为inout				
				if(trigger.getTrigertype()==0) {//预警
					if(ds_id.equals("DO")) {
						sensor.setOxygen_status(1);	
					}else if(ds_id.equals("WT")) {
						sensor.setWT_status(1);
					}else if(ds_id.equals("pH")) {
						sensor.setpH_status(1);
					}					
				}else if(trigger.getTrigertype()==1){//危险
					if(ds_id.equals("DO")) {
						sensor.setOxygen_status(2);	
					}else if(ds_id.equals("WT")) {
						sensor.setWT_status(2);
					}else if(ds_id.equals("pH")) {
						sensor.setpH_status(2);
					}
				}else if(trigger.getTrigertype()==2) {//低于溶氧下限，打开增氧机
					int way = trigger.getWay();
					String divsn=trigger.getDevice_sn();
					String text = "KM"+way+":"+1;
					CMDUtils.sendStrCmd(divsn,text);
				}else if(trigger.getTrigertype()==4){//正常
					if(ds_id.equals("DO")) {
						sensor.setOxygen_status(0);	
					}else if(ds_id.equals("WT")) {
						sensor.setWT_status(0);
					}else if(ds_id.equals("pH")) {
						sensor.setpH_status(0);
					}
				}
			}
			sensorDao.updateSensor(sensor);
		}
	}

/*
 * onenet触发器的type中exp无效，故使用inout替代其功能
 * 
 */
	public int addTrigerbyFishtype(String device_sn,int fishtype){
		List<Fish_Category> fishcate = fishcateDao.getallfish();
		//危险触发器和预警触发器的添加顺序不能改变，否则传感器状态会出错
		//addTrigger(String dsid,String device_sn,String type,Object threshold,int localtype,int way)
	    if (fishtype==1) {//鱼
	    	//预警触发器与危险触发器，0预警1危险 4正常
	    	/*
	    	 *溶解氧
	    	 */
	    	int trigger11 = addTrigger("DO", device_sn, "<", 2, 1,0);
	    	Map threshold12 = new HashMap<String, Float>();
			threshold12.put("lolmt", 2);
			threshold12.put("uplmt", 4);
	    	int trigger12 = addTrigger("DO",device_sn,"inout",threshold12,0,0);
	    	int trigger13 = addTrigger("DO", device_sn, ">", 4, 4,0);
	    	/*
	    	 * 水温
	    	 */
	    	 int trigger21 = addTrigger("WT", device_sn, "<", 10, 0,0);
             int trigger22 =addTrigger("WT", device_sn, ">", 30, 0,0);
             Map threshold23 = new HashMap<String, Float>();
             threshold23.put("lolmt", 10);
             threshold23.put("uplmt",30 );
 	    	int trigger23 = addTrigger("WT",device_sn,"inout",threshold23,4,0);
             /*
              * pH
              */
	             
	    		 //预警
	    		 Map threshold33 = new HashMap<String, Float>();
	 			threshold33.put("lolmt", 4.5);
	 			threshold33.put("uplmt", 6.5);
	    		 int trigger33 = addTrigger("pH", device_sn, "inout", threshold33, 0,0);
	    		 Map threshold34 = new HashMap<String, Float>();
	  			threshold34.put("lolmt", 9);
	  			threshold34.put("uplmt", 10.2);
	     		 int trigger34 = addTrigger("pH", device_sn, "inout", threshold34, 0,0);
	     		//危险
	             int trigger31 = addTrigger("pH", device_sn, "<", 4.5, 1,0);
	    		 int trigger32 = addTrigger("pH", device_sn, ">", 10.2, 1,0);
	    		 //正常
		     		Map threshold35 = new HashMap<String, Float>();
		     		threshold35.put("lolmt", 6.5);
		     		threshold35.put("uplmt", 9);
		     		 int trigger35 = addTrigger("pH", device_sn, "inout", threshold35, 4,0);
	     		 
	     		 if(trigger11==1&&trigger12==1&&trigger13==1&&trigger21==1&&trigger22==1&&trigger23==1&&trigger31==1&&trigger32==1&&trigger33==1&&trigger34==1&&trigger35==1) {
	     			 return 1;
	     		 }else {
	     			 return 0;
	     		 }
	     		
	     		
        }else if (fishtype==2) {//虾
        	//预警触发器与危险触发器
        	/*
	    	 *溶解氧
	    	 */
        	
	    	Map threshold12 = new HashMap<String, Float>();
			threshold12.put("lolmt", 2);
			threshold12.put("uplmt", 5);
	    	int trigger12 = addTrigger("DO",device_sn,"inout",threshold12,0,0);
	    	int trigger11 = addTrigger("DO", device_sn, "<", 2, 1,0);
	    	int trigger13 = addTrigger("DO", device_sn, ">", 5, 4,0);
	    	/*
	    	 * 水温
	    	 */
	    	 int trigger21 = addTrigger("WT", device_sn, "<", 18, 0,0);
	    	 Map threshold23 = new HashMap<String, Float>();
             threshold23.put("lolmt", 18);
             threshold23.put("uplmt",30 );
 	    	int trigger23 = addTrigger("WT",device_sn,"inout",threshold23,4,0);
	        /*
	         * pH
	         */
	    	
			//预警
	    	Map threshold33 = new HashMap<String, Float>();
			threshold33.put("lolmt", 6.5);
			threshold33.put("uplmt", 7.8);
			int trigger33 = addTrigger("pH", device_sn, "inout", threshold33, 0,0);
   		 	Map threshold34 = new HashMap<String, Float>();
 			threshold34.put("lolmt", 8.5);
 			threshold34.put("uplmt", 9.2);
    		 int trigger34 = addTrigger("pH", device_sn, "inout", threshold34, 0,0);
    		//危险
  	        int trigger31 = addTrigger("pH", device_sn, "<", 6.5, 1,0);
  			int trigger32 = addTrigger("pH", device_sn, ">", 9.2, 1,0);
  			 //正常
     		Map threshold35 = new HashMap<String, Float>();
     		threshold35.put("lolmt", 7.8);
     		threshold35.put("uplmt", 8.5);
     		 int trigger35 = addTrigger("pH", device_sn, "inout", threshold34, 4,0);
    		 if(trigger11==1&&trigger12==1&&trigger21==1&&trigger31==1&&trigger32==1&&trigger33==1&&trigger34==1) {
     			 return 1;
     		 }else {
     			 return 0;
     		 }
    		
        }else if (fishtype==3) {//蟹
        	//预警触发器与危险触发器
    	/*
    	 *溶解氧
    	 */
    	
    	Map threshold12 = new HashMap<String, Float>();
		threshold12.put("lolmt", 2.5);
		threshold12.put("uplmt", 5);
    	int trigger12 = addTrigger("DO",device_sn,"inout",threshold12,0,0);
    	int trigger11 = addTrigger("DO", device_sn, "<", 2.5, 1,0);
    	int trigger13 = addTrigger("DO", device_sn, ">", 5, 4,0);
    	/*
    	 * 水温
    	 */
    	 int trigger21 = addTrigger("WT", device_sn, "<", 18, 0,0);
    	 Map threshold23 = new HashMap<String, Float>();
    	 threshold23.put("lolmt", 18);
         threshold23.put("uplmt",30 );
	    	int trigger23 = addTrigger("WT",device_sn,"inout",threshold23,4,0);
    	/*
         * pH
         */
    	
		//预警
		 Map threshold33 = new HashMap<String, Float>();
			threshold33.put("lolmt", 6);
			threshold33.put("uplmt", 6.8);
		 int trigger33 = addTrigger("pH", device_sn, "inout", threshold33, 0,0);
		 Map threshold34 = new HashMap<String, Float>();
			threshold34.put("lolmt", 6.8);
			threshold34.put("uplmt", 8.3);
 		 int trigger34 = addTrigger("pH", device_sn, "inout", threshold34, 0,0);
 		//危险
         int trigger31 = addTrigger("pH", device_sn, "<", 6, 1,0);
		 int trigger32 = addTrigger("pH", device_sn, ">", 9, 1,0);
		 //正常
  		Map threshold35 = new HashMap<String, Float>();
  		threshold35.put("lolmt", 7.8);
  		threshold35.put("uplmt", 8.5);
  		 int trigger35 = addTrigger("pH", device_sn, "inout", threshold34, 4,0);
 		 if(trigger11==1&&trigger12==1&&trigger21==1&&trigger31==1&&trigger32==1&&trigger33==1&&trigger34==1) {
 			 return 1;
 		 }else {
 			 return 0;
 		 }
 		
        }else {
        	return 0;
        }
    }

	
	public int addAioTrigerbyFishtype(String device_sn,int fishtype,int way){
		List<Fish_Category> fishcate = fishcateDao.getallfish();
		//危险触发器和预警触发器的添加顺序不能改变，否则传感器状态会出错
		
	    if (fishtype==1) {//鱼
	    	//预警触发器与危险触发器，0预警1危险 
	    	/*
	    	 *溶解氧
	    	 */
	    	int trigger11 = addTrigger("DO"+way, device_sn, "<", 2, 1,way);
	    	Map threshold12 = new HashMap<String, Float>();
			threshold12.put("lolmt", 2);
			threshold12.put("uplmt", 4);
	    	int trigger12 = addTrigger("DO"+way,device_sn,"inout",threshold12,0,way);
	    	/*
	    	 * 水温
	    	 */
	    	 int trigger21 = addTrigger("WT"+way, device_sn, "<", 10, 0,way);
             int trigger22 =addTrigger("WT"+way, device_sn, ">", 30, 0,way);
             /*
              * pH
              */
	             
	    		 //预警
	    		 Map threshold33 = new HashMap<String, Float>();
	 			threshold33.put("lolmt", 4.5);
	 			threshold33.put("uplmt", 6.5);
	    		 int trigger33 = addTrigger("pH"+way, device_sn, "inout", threshold33, 0,way);
	    		 Map threshold34 = new HashMap<String, Float>();
	  			threshold34.put("lolmt", 9);
	  			threshold34.put("uplmt", 10.5);
	     		 int trigger34 = addTrigger("pH"+way, device_sn, "inout", threshold34, 0,way);
	     		//危险
	             int trigger31 = addTrigger("pH"+way, device_sn, "<", 4.5, 1,way);
	    		 int trigger32 = addTrigger("pH"+way, device_sn, ">", 10.2, 1,way);
	     		 
	     		 if(trigger11==1&&trigger12==1&&trigger21==1&&trigger22==1&&trigger31==1&&trigger32==1&&trigger33==1&&trigger34==1) {
	     			 return 1;
	     		 }else {
	     			 return 0;
	     		 }
	     		
        }else if (fishtype==2) {//虾
        	//预警触发器与危险触发器
        	/*
	    	 *溶解氧
	    	 */
        	
	    	Map threshold12 = new HashMap<String, Float>();
			threshold12.put("lolmt", 2);
			threshold12.put("uplmt", 5);
	    	int trigger12 = addTrigger("DO"+way,device_sn,"inout",threshold12,0,way);
	    	int trigger11 = addTrigger("DO"+way, device_sn, "<", 2, 1,way);
	    	/*
	    	 * 水温
	    	 */
	    	 int trigger21 = addTrigger("WT"+way, device_sn, "<", 18, 0,way);
	        /*
	         * pH
	         */
	    	
			//预警
	    	Map threshold33 = new HashMap<String, Float>();
			threshold33.put("lolmt", 6.5);
			threshold33.put("uplmt", 7.8);
			int trigger33 = addTrigger("pH"+way, device_sn, "inout", threshold33, 0,way);
   		 	Map threshold34 = new HashMap<String, Float>();
 			threshold34.put("lolmt", 8.5);
 			threshold34.put("uplmt", 9.2);
    		 int trigger34 = addTrigger("pH"+way, device_sn, "inout", threshold34, 0,way);
    		//危险
  	        int trigger31 = addTrigger("pH"+way, device_sn, "<", 6.5, 1,way);
  			int trigger32 = addTrigger("pH"+way, device_sn, ">", 9.2, 1,way);
    		 if(trigger11==1&&trigger12==1&&trigger21==1&&trigger31==1&&trigger32==1&&trigger33==1&&trigger34==1) {
     			 return 1;
     		 }else {
     			 return 0;
     		 }
    		
        }else if (fishtype==3) {//蟹
        	//预警触发器与危险触发器
    	/*
    	 *溶解氧
    	 */
    	
    	Map threshold12 = new HashMap<String, Float>();
		threshold12.put("lolmt", 2.5);
		threshold12.put("uplmt", 5);
    	int trigger12 = addTrigger("DO"+way,device_sn,"inout",threshold12,0,way);
    	int trigger11 = addTrigger("DO"+way, device_sn, "<", 2.5, 1,way);
    	/*
    	 * 水温
    	 */
    	 int trigger21 = addTrigger("WT"+way, device_sn, "<", 18, 0,way);
    	/*
         * pH
         */
    	
		//预警
		 Map threshold33 = new HashMap<String, Float>();
			threshold33.put("lolmt", 6);
			threshold33.put("uplmt", 6.8);
		 int trigger33 = addTrigger("pH"+way, device_sn, "inout", threshold33, 0,way);
		 Map threshold34 = new HashMap<String, Float>();
			threshold34.put("lolmt", 8.3);
			threshold34.put("uplmt", 9);
 		 int trigger34 = addTrigger("pH"+way, device_sn, "inout", threshold34, 0,way);
 		//危险
         int trigger31 = addTrigger("pH"+way, device_sn, "<", 6, 1,way);
		 int trigger32 = addTrigger("pH"+way, device_sn, ">", 9, 1,way);
 		 if(trigger11==1&&trigger12==1&&trigger21==1&&trigger31==1&&trigger32==1&&trigger33==1&&trigger34==1) {
 			 return 1;
 		 }else {
 			 return 0;
 		 }
 		
        }else {
        	return 0;
        }
    }

	
	public int addTrigger(String dsid,String device_sn,String type,Object threshold,int localtype,int way){
		/**
		 * 触发器新增
		 * @param title:名称（可选）,String
		 * @param dsid:数据流名称（id）（可选）,String
		 * @param devids:设备ID（可选）,List<String>
		 * @param dsuuids:数据流uuid（可选）,List<String>
		 * @param desturl:url,String
		 * @param type:触发类型，String
		 * @param threshold:阙值，根据type不同，见以下说明,Integer
		 * @param key:masterkey 或者 设备apikey
		 */
		//微信小程序使用url
		String url = "https://www.fisherymanager.net/fishery/api/equipment/triggeractive";
		//本机使用
		//String url = "https://262101ef.ngrok.io/fishery/api/equipment/triggeractive";
		List<String> devids=new ArrayList<String>();
		devids.add(device_sn);
		String key = "7zMmzMWnY1jlegImd=m4p9EgZiI=";
		int triggerid;	
		AddTriggersApi api = new AddTriggersApi(null, dsid, devids, null, url, type, threshold, key);
		try{
			BasicResponse<NewTriggersResponse> response = api.executeApi();
			System.out.println(response.getJson());
			JSONObject tempjson = JSONObject.fromObject(response.getJson());
			int errnoint = tempjson.getInt("errno");
			if (errnoint==0){
				JSONObject triobj = tempjson.getJSONObject("data");
				triggerid = triobj.getInt("trigger_id");
				Dev_Trigger trigger = new Dev_Trigger();
				trigger.setDevice_sn(device_sn);
				trigger.setTriger_id(String.valueOf(triggerid));
				trigger.setTrigertype(localtype);
				trigger.setWay(way);
				dev_triggerDao.save(trigger);
				return 0;
			}else return 1;
		}catch(Exception e){
			System.out.println(e.getMessage());
			return 1;
		}
	}
	
	public String getControllerPortStatus(String devId,int port) {
		//System.out.println("123123123123123123123123123");
		String key = "7zMmzMWnY1jlegImd=m4p9EgZiI=";
		String id = "KM"+port;
		/**
		 * 查询单个数据流
		 * @param devid:设备ID,String
		 * @param datastreamid:数据流名称 ,String
		 * @param key:masterkey 或者 设备apikey
		 */
		System.out.println("获取控制器端口状态");
		GetDatastreamApi api = new GetDatastreamApi(devId, id, key);
		BasicResponse<DatastreamsResponse> response = api.executeApi();
		System.out.println("errno:"+response.errno+" error:"+response.error);
		System.out.println(response.getJson());
		if(response.errno == 0) {
			return (String)response.data.getCurrentValue();
		}else {
			return "2";
		}
	}
	
	public int checkTimer(String dev_sn,int way) {
		
		List<Timer> timerList = timerDao.findTimerByDeviceSnAndWay(dev_sn, way);
		if(timerList.size()==0) {
			return 0;
		}else {
			return 1;
		}
	}

	public Map<String, Object> refeshcondition(String device_sn,int port,String status){

		Map<String, Object> refeshflag = new HashMap<>();
		String controllerKey = getControllerPortStatus(device_sn,port);
		if (controllerKey.equals("2")== false){
			//查询成功
			if (controllerKey.equals(status)==false){
				//状态已改，不需要下一次
				refeshflag.put("ifnext", 0);
				refeshflag.put("switch",controllerKey);
			}
			if (controllerKey.equals(status)==true){
				//状态未改变，需要再次打开
				refeshflag.put("ifnext", 1);
				refeshflag.put("switch",status);

			}
			return refeshflag;
		}else {
			//查询失败
			refeshflag.put("false", 1);//?
			return refeshflag;
		}

		//String controllerKey = "0";

	}
	
	public Map<String, Object> getPersonalAnalysis(String relation){
	
		List<Pond> pondList = pondDao.queryPondByRelation(relation);
		for(Pond pond:pondList) {
			Map<String, Object> pondData = analysisData(pond.getId());
		}
		return null;
	}
	
	public List<Map<String, Object>> getPersonalDianosing(String relation){
		List<Map<String, Object>> personalDai = new ArrayList<>(); 
 		List<Pond> pondList = pondDao.queryPondByRelation(relation);
		for(Pond pond:pondList) {
			Map<String, Object> pondData = getDianosing(pond.getId());
			personalDai.add(pondData);
		}		
		return personalDai;
	}
	
	
	public Map<String, Object> getDianosing(int pondId){
		//根据pondId获得池塘各个参数与分析结果和解决方案
		Map<String, Object> diaResult = new HashMap<>();
		String result = null;//参数分析结果
		String solution = null;//解决方案
		String Analysis = null;//曲线峰谷分析
		String pondName = pondDao.findPondByPondId(pondId).getName();//池塘名称
		
		Diagnosing dia = new Diagnosing();
		Map<String, Object> diaMap= dia.getDiagnosing();
		String[] peakAndValleyAnalysis = (String[]) diaMap.get("peakAndValleyAnalysis");
		
		String returnString = null;
		List<PondFish> pondFish = pondFishDao.getFishbyPondId(pondId);
		int type=0;
		if(pondFish.size()>0) {
			type = pondFish.get(0).getType();
		}	
		//获得塘口数值分析结果
		Map<String, Object> dataResult = analysisData(pondId);
		String Analysis1 = null;
		String Analysis2 = null;
		
		String pHResult = null;
		String DOResult = null;
		String WTResult = null;
		
		String pHSolution = null;
		String DOSolution = null;
		String WTSolution = null;
		//根据数值分析，得出文字结果
		if(dataResult !=null) {//
			//分析pH，获得pH诊断
			Map<String , Object> pHMap =   (Map<String, Object>) dataResult.get("pH");
			if(pHMap != null) {
				Float min = (Float) pHMap.get("min");
				Float max = (Float) pHMap.get("max");				
				Float average = (Float) pHMap.get("average");
				average   =  (float)(Math.round(average*100))/100;
				pHResult = "PH最小值"+min+"，最大值"+max+"，均值为"+average+"。";
				pHSolution = dia.getDiagnosing("pH", average, type);
				Float dvalue =max-min;	
				
				if(dvalue>2) {
					Analysis1 = peakAndValleyAnalysis[2];
				}	
				
			}
			//分析DO，获得DO诊断
			Map<String , Object> DOMap =  (Map<String, Object>) dataResult.get("DO");
			if(DOMap != null) {
				Float min = (Float) DOMap.get("min");
				Float max = (Float) DOMap.get("max");
				Float average = (Float) DOMap.get("average");
				average   =  (float)(Math.round(average*100))/100;
				Float dvalue =max-min;
				DOResult = "水体溶氧最小值"+min+"，最大值"+max+"，均值为"+average+"。";
				DOSolution = dia.getDiagnosing("DO", average, type);
				if(dvalue>12) {
					Analysis2 = peakAndValleyAnalysis[0];
				}else if(dvalue<2 && min<8){
					Analysis2 =peakAndValleyAnalysis[1];
				}
			}
			//分析WT，获得WT诊断
			Map<String , Object> WTMap =  (Map<String, Object>) dataResult.get("WT");
			if(WTMap != null) {
				Float min = (Float) WTMap.get("min");
				Float max = (Float) WTMap.get("max");
				Float average = (Float) WTMap.get("average");
				average   =  (float)(Math.round(average*100))/100;
				WTResult = "水温最小值"+min+"，最大值"+max+"，均值为"+average+"。";
				WTSolution = dia.getDiagnosing("WT", average, type);
			}
		}
		if(Analysis1!=null || Analysis2!=null) {
			Analysis = Analysis1==null?"":(Analysis1 +";")+Analysis1==null?"":(Analysis1 +"。");
		}
		//solution = (DOSolution==null?"":(DOSolution +";"))+( pHSolution==null?"":(pHSolution +";"))+( WTSolution==null?"":(WTSolution +"。"));
		result = (DOResult==null?"":DOResult) + (pHResult==null?"":pHResult)+ (WTResult==null?"":WTResult);
		solution = (DOSolution==null?"":DOSolution) + (pHSolution==null?"":pHSolution)+ (WTSolution==null?"":WTSolution)+(Analysis==null?"":Analysis);
		diaResult.put("result", result);
		diaResult.put("solution", solution);
		diaResult.put("pondName", pondName);
		return diaResult;
	}
	
	
	
	
	
	public Map<String, Object> analysisData(int pondId){
		//分析塘口数据,获得pH、DO、WT的最高、最低、以及平均值
		/*
		 * 传感器和一体机都可以获得鱼塘的数据
		 * 优先使用传感器设备数据
		 * 目前，只分析一台设备的数据
		 */
		logger.debug("塘口"+pondId);

		
		List<Sensor> sensorList= sensorDao.findSensorsByPondId(pondId);
		logger.debug("传感器"+sensorList.size());
		List<AIO> aioList = aioDao.findAIOsByPondId(pondId);
		logger.debug("一体机"+aioList.size());
		Map<String, Object> dataY = new HashMap<>();
		if(sensorList.size()==0 && aioList.size()==0) {//传感器和一体机都没有
			return RESCODE.NOT_FOUND.getJSONRES();
		}else {
			if(aioList.size()==0) {
				//没有一体机，使用传感器
				Sensor sensor = sensorList.get(0);	
				logger.debug("设备编号："+sensor.getDevice_sn());
				dataY =dataYesterday(sensor.getDevice_sn());
			}else if(sensorList.size()==0) {
				//没有传感器
				AIO aio = aioList.get(0);
				dataY =dataYesterday(aio.getDevice_sn());
			}else {
				//一体机和传感器都有，使用传感器
				Sensor sensor = sensorList.get(0);	
				logger.debug("设备编号："+sensor.getDevice_sn());
				dataY =dataYesterday(sensor.getDevice_sn());
			}
			Map<String , Object> pHMap =  (Map<String, Object>) dataY.get("pH");
			Map<String , Object> DOMap = (Map<String, Object>) dataY.get("DO");
			Map<String , Object> WTMap = (Map<String, Object>) dataY.get("WT");
			Map<String , Object> returnMap = new HashMap<>();
			List<Float> valueList = new ArrayList<Float>();
			float max;
			float min;
			float sum;
			float average;
			//pH分析
			if(pHMap != null) {
				valueList = (List<Float>) pHMap.get("value");
				max=valueList.get(0);
				 min = valueList.get(0);
				sum = (float) 0.0;
				for(int j=1;j<valueList.size();j++) {				
					if(valueList.get(j)>max) {
						max=valueList.get(j);
					} 
					if(valueList.get(j)<min) {
						min=valueList.get(j);
					} 				
					sum+=valueList.get(j);
				}
				average = sum/valueList.size();
				Map<String , Object> pHResult = new HashMap<>();
				pHResult.put("max", max);
				pHResult.put("min", min);
				pHResult.put("average", average);
				returnMap.put("pH", pHResult);
			}
			
			//DO分析
			if(DOMap !=null) {
				valueList = (List<Float>) DOMap.get("value");
				max=valueList.get(0);
				min = valueList.get(0);
				sum = (float) 0.0;			
				for(int j=1;j<valueList.size();j++) {				
					if(valueList.get(j)>max) {
						max=valueList.get(j);
					} 
					if(valueList.get(j)<min) {
						min=valueList.get(j);
					} 				
					sum+=valueList.get(j);
				}
				average = sum/valueList.size();
				Map<String , Object> DOResult = new HashMap<>();
				DOResult.put("max", max);
				DOResult.put("min", min);
				DOResult.put("average", average);
				returnMap.put("DO", DOResult);
			}
			
			//WT分析
			if(DOMap!=null) {
				valueList = (List<Float>) DOMap.get("value");
				max=valueList.get(0);
				min = valueList.get(0);
				sum = (float) 0.0;			
				for(int j=1;j<valueList.size();j++) {				
					if(valueList.get(j)>max) {
						max=valueList.get(j);
					} 
					if(valueList.get(j)<min) {
						min=valueList.get(j);
					} 				
					sum+=valueList.get(j);
				}
				average = sum/valueList.size();
				Map<String , Object> WTResult = new HashMap<>();
				WTResult.put("max", max);
				WTResult.put("min", min);
				WTResult.put("average", average);
				returnMap.put("WT", WTResult);
			}
			
			return returnMap;			
		}
	}
	
	public void deleteTriggerBySensorId(String device_sn) {
		List<Dev_Trigger> devTriggerList = dev_triggerDao.findDev_TriggerBydevsn(device_sn);
		for(Dev_Trigger devtrigger:devTriggerList) {		
			/**
			 * 触发器删除
			 * @param tirggerid:触发器ID,String
			 * @param key:masterkey 或者 设备apikey
			 */
			DeleteTriggersApi api = new DeleteTriggersApi(devtrigger.getTriger_id(), key);
			BasicResponse<Void> response = api.executeApi();
			System.out.println("errno:"+response.errno+" error:"+response.error);
		}
	}

	public void deleteTriggerByDevice_snAndWay(String device_sn,int way) {
		Dev_Trigger devTrigger = dev_triggerDao.findDev_TriggerByDevsnAndWay(device_sn, way);
				
			/**
			 * 触发器删除
			 * @param tirggerid:触发器ID,String
			 * @param key:masterkey 或者 设备apikey
			 */
			DeleteTriggersApi api = new DeleteTriggersApi(devTrigger.getTriger_id(), key);
			BasicResponse<Void> response = api.executeApi();
			System.out.println("errno:"+response.errno+" error:"+response.error);
		
	}
}
