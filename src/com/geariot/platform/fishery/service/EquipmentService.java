package com.geariot.platform.fishery.service;

import cmcc.iot.onenet.javasdk.api.datapoints.GetDatapointsListApi;
import cmcc.iot.onenet.javasdk.api.datastreams.GetDatastreamApi;
import cmcc.iot.onenet.javasdk.api.device.GetDeviceApi;
import cmcc.iot.onenet.javasdk.api.device.GetDevicesStatus;
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
import cmcc.iot.onenet.javasdk.response.device.DevicesStatusList;
import cmcc.iot.onenet.javasdk.response.triggers.NewTriggersResponse;

import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.geariot.platform.fishery.dao.*;
import com.geariot.platform.fishery.entities.*;
import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.model.*;
import com.geariot.platform.fishery.timer.CMDUtils;
import com.geariot.platform.fishery.utils.DataExportExcel;
import com.geariot.platform.fishery.utils.Diagnosing;
import com.geariot.platform.fishery.utils.VmsUtils;
import com.geariot.platform.fishery.wxutils.WechatSendMessageUtils;
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
import java.text.ParseException;
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
	private UserService userService;
	
	@Autowired
	private WebServiceService webServiceService;
	
	@Autowired
	private BindService bindService;
	
	@Autowired
	private PondFishDao pondFishDao;
	@Autowired
	private pHDao phDao;
	@Autowired
	private DODao doDao;
	@Autowired
	private DOSDao dosDao;
	@Autowired
	private WTDao wtDao;
	@Autowired
	private KM1Dao km1Dao;
	@Autowired
	private KM2Dao km2Dao;
	@Autowired
	private KM3Dao km3Dao;
	@Autowired
	private KM4Dao km4Dao;
	@Autowired
	private PFDao pfDao;
	@Autowired
	private DP1Dao dp1Dao;
	@Autowired
	private DP2Dao dp2Dao;
	@Autowired
	private DP3Dao dp3Dao;
	@Autowired
	private DP4Dao dp4Dao;
	
	
	private String key = "7zMmzMWnY1jlegImd=m4p9EgZiI=";

	public Map<String, Object> VertifyDevicesn(String divsn) {
		/**
		 * 精确查询单个设备
		 * 参数顺序与构造函数顺序一致
		 * @param devid:设备名，String
		 * @param key:masterkey 或者 设备apikey,String
		 */
		String device_sn = divsn.substring(2, divsn.length());
		logger.debug("检查设备在onenet上是否存在");
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
		logger.debug("进入删除设备，设备编号为："+device_sn);
		Device  d = deviceDao.findDevice(device_sn);
		if(d!=null) {
			int type = d.getType();
			switch(type) {
				case 1://传感器
					logger.debug("删除设备类型为传感器");
					sensorDao.delete(device_sn);
					deviceDao.delete(device_sn);
					//删除触发器
					deleteTriggerBySensorId(device_sn);
					dev_triggerDao.delete(device_sn);
					List<Dev_Trigger> devTriList = dev_triggerDao.findDev_TriggerBydevsn(device_sn);
					for(Dev_Trigger devTri:devTriList) {
						DeleteTriggersApi api = new DeleteTriggersApi(devTri.getTriger_id(), key);
						BasicResponse<Void> response = api.executeApi();
						System.out.println("errno:"+response.errno+" error:"+response.error);
					}
										
					break;
				case 2://一体机
					//一体机完善后，需加入一体机的传感器的触发器的删除部分
					logger.debug("删除设备类型为一体机");
					aioDao.delete(device_sn);
					deviceDao.delete(device_sn);
					break;
				case 3://控制器
					logger.debug("删除设备类型为控制器");
					controllerDao.delete(device_sn);
					deviceDao.delete(device_sn);
					deleteTriggerBySensorId(device_sn);
					dev_triggerDao.delete(device_sn);
					List<Dev_Trigger> devTriList2 = dev_triggerDao.findDev_TriggerBydevsn(device_sn);
					for(Dev_Trigger devTri:devTriList2) {
						DeleteTriggersApi api = new DeleteTriggersApi(devTri.getTriger_id(), key);
						BasicResponse<Void> response = api.executeApi();
						System.out.println("errno:"+response.errno+" error:"+response.error);
					}
					limitDao.delete(device_sn);
					timerDao.delete(device_sn);
					break;
			}
			return RESCODE.SUCCESS.getJSONRES();
		}else {
			logger.debug("设备表中未找到需要删除的设备");
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
			synchronized(this) {
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
						//塘口存在，添加塘口信息
						Pond pondExsit=pondDao.findPondByPondId(sensor.getPondId());
						float lat = 0;
						float lon  = 0;
						GetLatesDeviceData api = new GetLatesDeviceData(sensor.getDevice_sn(),key);
				        BasicResponse<DeciceLatestDataPoint> response = api.executeApi();
				        System.out.println("errno:"+response.errno+" error:"+response.error);
				        System.out.println(response.getJson());
				        if(response.errno == 0) {
				        	
				           List<cmcc.iot.onenet.javasdk.response.device.DeciceLatestDataPoint.DeviceItem.DatastreamsItem> datastreamsList = response.data.getDevices().get(0).getDatastreams();
				 	       if(datastreamsList == null) {
				 	    	   System.out.println("无数据流");
				 	       }else {
				 	    	  if(datastreamsList != null) {
						    	   for(int i=0;i<datastreamsList.size();i++) {	
							        	if(datastreamsList.get(i).getId().equals("location")) {
							        		System.out.println(datastreamsList.get(i).getId());
								        	System.out.println(datastreamsList.get(i).getValue());
								        	String location = datastreamsList.get(i).getValue().toString();
								        	lat = Float.parseFloat(location.substring(5, location.indexOf(",")));
								        	System.out.println(lat);
								        	lon = Float.parseFloat(location.substring(location.indexOf(",")+6,location.length()-1));
								        	System.out.println(lon);
							        	}
						    	   }
					        } 
				 	       }
						       
				        }
				       
						pondExsit.setLatitude(lat);
						pondExsit.setLongitude(lon);
						String address = webServiceService.getLocation(lon, lat);
						pondExsit.setAddress(address);						
						pondDao.update(pondExsit);						
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
					return RESCODE.DEVICESNS_REPEAT.getJSONRES();
				}
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
				//dsid,device_sn,type,threshold,localtype,way
				addTrigger("PF", api_device_sn, ">", 0, 3, 0);
				addTrigger("DP1", api_device_sn, ">", 0, 3, 0);
				addTrigger("DP2", api_device_sn, ">", 0, 3, 0);
				addTrigger("DP3", api_device_sn, ">", 0, 3, 0);
				addTrigger("DP4", api_device_sn, ">", 0, 3, 0);
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
						logger.debug("控制器编号"+controller.getDevice_sn());
						String relation = controller.getRelation();
						List<Sensor> sensorList = sensorDao.findSensorsByRelation(relation);
						for(Sensor sensor:sensorList) {
							int[] pondIds = controller.getPondIds();
							for(int i=0;i<pondIds.length;i++){
								if(pondIds[i]==sensor.getPondId()) {
									addTrigger("DO", sensor.getDevice_sn(), "<", 4, 2, controller.getPort());
									addTrigger("DO", sensor.getDevice_sn(), ">", 6, 2, controller.getPort());
								}
							}							
						}
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
                 	
                 	if(id.equals("pH")) {
                 		String v = String.valueOf(DatastreamsList.get(i).getValue());
                     	float value = Float.parseFloat(v);
                 		sensor.setpH_value(value);
                 	}else if(id.equals("DO")) {
                 		String v = String.valueOf(DatastreamsList.get(i).getValue());
                     	float value = Float.parseFloat(v);
                 		sensor.setOxygen(value);
                 	}else if(id.equals("WT")) {
                 		String v = String.valueOf(DatastreamsList.get(i).getValue());
                     	float value = Float.parseFloat(v);
                 		sensor.setWater_temperature(value);
                 	}  
                 	
                 }
             	logger.debug("将数据存入到传感器中");
             	return sensor;
             }else {
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
		logger.debug("limit_Install已有，进入更新修改");
			limitDao.updateLimit(limit_Install);
			//limit_Install中设备编号及路数确定增氧机
			List<Controller> controllerList =  controllerDao.findControllerByDeviceSnAndWay(limit_Install.getDevice_sn(), limit_Install.getWay());
			logger.debug("controllerList:"+controllerList.size());
			if(controllerList !=null ) {
				logger.debug("用户："+controllerList.get(0).getRelation());
				List<Sensor> sensorList =sensorDao.findSensorsByRelation(controllerList.get(0).getRelation());
				logger.debug("开始删除触发器");
				for(Controller controller:controllerList) {					
					int pondId = controller.getPondId();
					logger.debug("控制器塘口："+pondId);
					for(Sensor sensor:sensorList) {
						if(sensor.getPondId() ==pondId) {
							logger.debug("获得传感器");
							List<Dev_Trigger> triggerList= dev_triggerDao.findDev_TriggerBydevsn(sensor.getDevice_sn());
							logger.debug("获得触发器");
							for(Dev_Trigger trigger:triggerList) {
								if(trigger.getTrigertype()==2) {
									dev_triggerDao.deleteByTriggerId(trigger.getTriger_id());
									deleteTriggerByTriggerId(trigger.getTriger_id());
								}								
							}
						}
					}
				}
				int result1 =0;
				int result2 = 0;
				logger.debug("开始添加触发器");
				for(Controller controller:controllerList) {
					int pondId = controller.getPondId();
					for(Sensor sensor:sensorList) {
						if(sensor.getPondId() ==pondId) {
							result1 =addTrigger("DO", sensor.getDevice_sn(), "<", limit_Install.getLow_limit(), 2,limit_Install.getWay());
							result2 =addTrigger("DO", sensor.getDevice_sn(), ">", limit_Install.getUp_limit(), 2,limit_Install.getWay());
						}
					}
				}
				
				if(result1==1&&result2==1) {
					return RESCODE.SUCCESS.getJSONRES();
				}else {
					return RESCODE.TRIGGER_FAILED.getJSONRES();
				}
			}else {
				return RESCODE.TRIGGER_FAILED.getJSONRES();
			}
			
			
	/*	}	*/	
	}
	
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
		logger.debug("获得设备："+device_sn+"今日数据");
		Map<String, Object> mapReturn = new HashMap<>();
		long startLong = System.currentTimeMillis();
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date dateEnd = new Date();//当前时间
		dateEnd.setHours(24);
		dateEnd.setMinutes(0);
		dateEnd.setSeconds(0);
		Date dateStart = new Date();//今日零点
		dateStart.setHours(0);
		dateStart.setMinutes(0);
		dateStart.setSeconds(0);
		int min = 24*60;
		
		String dataFormat1 = sdf1.format(dateStart);
		String dataFormat2  = sdf1.format(dateEnd);
		String dataFormatEnd = dataFormat2+"T00:00:00";
		String dataFormatStart = dataFormat1+"T00:00:00";
		

		/**
		 * 今日数据点查询
		 * @param datastreamids:查询的数据流，多个数据流之间用逗号分隔（可选）,String
		 * @param start:提取数据点的开始时间（可选）,String
		 * @param end:提取数据点的结束时间（可选）,String
		 * @param devid:设备ID,String
		 * 
		 * @param duration:查询时间区间（可选，单位为秒）,Integer
		 *  start+duration：按时间顺序返回从start开始一段时间内的数据点
		 *  end+duration：按时间倒序返回从end回溯一段时间内的数据点
		 * 
		 * @param limit:限定本次请求最多返回的数据点数，0<n<=6000（可选，默认1440）,Integer
		 * @param cursor:指定本次请求继续从cursor位置开始提取数据（可选）,String
		 * @param interval:通过采样方式返回数据点，interval值指定采样的时间间隔（可选）,Integer
		 * @param metd:指定在返回数据点时，同时返回统计结果，可能的值为（可选）,String
		 * @param first:返回结果中最值的时间点。1-最早时间，0-最近时间，默认为1（可选）,Integer
		 * @param sort:值为DESC|ASC时间排序方式，DESC:倒序，ASC升序，默认升序,String
		 * @param key:masterkey 或者 设备apikey
		 */
		List<DatapointsItem> pHList = new ArrayList<>();
		List<DatapointsItem> DOLsit = new ArrayList<>();
		List<DatapointsItem> WTList = new ArrayList<>();
		List<DatapointsItem> pHList1 = new ArrayList<>();
		List<DatapointsItem> DOLsit1 = new ArrayList<>();
		List<DatapointsItem> WTList1 = new ArrayList<>();
		GetDatapointsListApi api = new GetDatapointsListApi(null, dataFormatStart, dataFormatEnd, device_sn, null, 6000, null, 137,
				null, null, null, key);
		BasicResponse<DatapointsList> response = api.executeApi();
		logger.debug("设备："+device_sn + "的今日数据："+response.getJson());
		Map<String, Object> map = null;
		if(response.errno==0) {
			List<DatastreamsItem> dl= response.getData().getDevices();
			System.out.println("参数个数："+dl.size());		
			System.out.println("总共获得数据量为："+response.getData().getCount());
			for(int i=0;i<dl.size();i++) {				
				DatastreamsItem di = dl.get(i);
				List<DatapointsItem> ld =di.getDatapoints();
				/*System.out.println(di.getId()+"参数下数据量："+ld.size());*/
				if("pH".equals(di.getId())) {
					/*System.out.println("pH数据：");*/
					for(int j=0;j<ld.size();j++) {
						pHList.add(ld.get(j));
					/*	System.out.println(ld.get(j).getValue());*/
					}
				}else if("DO".equals(di.getId()) ) {
					/*System.out.println("DO数据：");*/
					for(int j=0;j<ld.size();j++) {
						DOLsit.add(ld.get(j));
						/*System.out.println(ld.get(j).getValue());*/
					}
				}else if("WT".equals(di.getId())) {
					/*System.out.println("WT数据");*/
					for(int j=0;j<ld.size();j++) {
						WTList.add(ld.get(j));
						/*System.out.println(ld.get(j).getValue());*/
					}
				}
								
			}
		}
		
		
		
		for(int i = 0 ; i<(min/20+1) ; i++) {
			Date dateStart1 = new Date();
			dateStart1.setTime(dateStart.getTime());
			dateStart1.setMinutes(dateStart1.getMinutes()-3);
			Date dateEnd1 = new Date();
			dateEnd1.setTime(dateStart.getTime());
			dateEnd1.setMinutes(dateEnd1.getMinutes()+3);
		/*	System.out.println("+++++++++++++++");*/
			String start = sdf1.format(dateStart1) + "T" + sdf2.format(dateStart1);
			String end = sdf1.format(dateEnd1) + "T" + sdf2.format(dateEnd1);	
			
		/*	System.out.println("开始时间："+dateStart1);
			System.out.println("结束时间："+dateEnd1);*/
			float sum1 =0;
			int count1 = 0;
			float sum2 =0;
			int count2 = 0;
			float sum3 =0;
			int count3 = 0;
			for(DatapointsItem dpsi:pHList) {
				String at = dpsi.getAt();
				 try {
					Date dt = sdf3.parse(at);					
					if(dt.getTime()>dateStart1.getTime()&&dt.getTime()<dateEnd1.getTime()) {
						/*System.out.println(dt);	*/			
						sum1 += Float.parseFloat((String)dpsi.getValue());
						count1++;
					}					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for(DatapointsItem dpsi:DOLsit) {
				String at = dpsi.getAt();
				 try {
					Date dt = sdf3.parse(at);					
					if(dt.getTime()>dateStart1.getTime()&&dt.getTime()<dateEnd1.getTime()) {
			
						sum2 += Float.parseFloat((String)dpsi.getValue());
						count2++;
					}					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for(DatapointsItem dpsi:WTList) {
				String at = dpsi.getAt();
				 try {
					Date dt = sdf3.parse(at);					
					if(dt.getTime()>dateStart1.getTime()&&dt.getTime()<dateEnd1.getTime()) {				
						sum3 += Float.parseFloat((String)dpsi.getValue());
						count3++;
					}					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			pHList1.add(new DatapointsItem(sdf3.format(dateStart), ((float)(Math.round(sum1/count1*100))/100)==0?null:(float)(Math.round(sum1/count1*100))/100));
			DOLsit1.add(new DatapointsItem(sdf3.format(dateStart), ((float)(Math.round(sum2/count2*100))/100)==0?null:(float)(Math.round(sum2/count2*100))/100));
			WTList1.add(new DatapointsItem(sdf3.format(dateStart), ((float)(Math.round(sum3/count3*100))/100)==0?null:(float)(Math.round(sum3/count3*100))/100));
			dateStart.setMinutes(dateStart.getMinutes()+20);
		}
		long endLong = System.currentTimeMillis();
		System.out.println("程序运行时间："+(endLong-startLong));
		
		List<String> doatList = new ArrayList<>();
		List<Float> dovalueList = new ArrayList<>();
		Map<String, Object> DOMap = new HashMap<>();
		for(int t = 0;t<DOLsit1.size();t++) {
			doatList.add((String) DOLsit1.get(t).getAt());
			dovalueList.add((Float) DOLsit1.get(t).getValue());
		}
		DOMap.put("value", dovalueList);
		DOMap.put("at", doatList);
		
		List<String> wtatList = new ArrayList<>();
		List<Float> wtvalueList = new ArrayList<>();
		Map<String, Object> WTMap = new HashMap<>();
		for(int t = 0;t<WTList1.size();t++) {
			wtatList.add((String) WTList1.get(t).getAt());
			wtvalueList.add((Float) WTList1.get(t).getValue());
		}
		WTMap.put("value", wtvalueList);
		WTMap.put("at", wtatList);
		
		List<String> phatList = new ArrayList<>();
		List<Float> phvalueList = new ArrayList<>();
		Map<String, Object> pHMap = new HashMap<>();
		for(int t = 0;t<pHList1.size();t++) {
			phatList.add((String) pHList1.get(t).getAt());
			phvalueList.add((Float) pHList1.get(t).getValue());
		}
		pHMap.put("value", phvalueList);
		pHMap.put("at", phatList);
		
		mapReturn.put("pH", pHMap);
		mapReturn.put("DO", DOMap);
		mapReturn.put("WT", WTMap);
		
		
		Sensor sensor = sensorDao.findSensorByDeviceSns(device_sn);
		if(sensor!=null) {
			logger.debug(sensor.getPondId());
		    List<Controller> controllerList= controllerDao.findByPondId(sensor.getPondId());
		    Controller conOxygen = new Controller();
		    Limit_Install limit = null;
			for(Controller controller:controllerList) {
				if(controller.getType()==0) {
					conOxygen = controller;
					limit = limitDao.findLimitByDeviceSnsAndWay(conOxygen.getDevice_sn(), conOxygen.getPort());						
					break;
				}
			}
			mapReturn.put("Limit", limit);
			
		}
		return mapReturn;
	}
	
	public Map<String, Object> dataYesterday(String device_sn) {
		logger.debug("获取设备："+device_sn+"昨日数据");
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
			
			
			for(int i=0;i<dl.size();i++) {
				List<String> atList = new ArrayList<String>();
				List<Float> valueList = new ArrayList<Float>();
				DatastreamsItem di = dl.get(i);
				List<DatapointsItem> ld =di.getDatapoints();
				if(di.getId().equals("location")==false) {
					logger.debug(di.getId()+"参数下数据量："+ld.size());
					for(int j=0;j<ld.size();j++) {
						atList.add(ld.get(j).getAt());
						valueList.add(Float.parseFloat((ld.get(j).getValue().toString())));
					}
				
				Map<String, Object> oneValueMap = new HashMap<String, Object>();
				oneValueMap.put("at", atList);
				oneValueMap.put("value",valueList);
				oneMap.put(di.getId(), oneValueMap);
				}				
			}
		}
		return oneMap;
	}
	public void saveALLDataYesterday() {
		List<Device> deviceList = deviceDao.getAllDevices();
		for(Device device:deviceList) {
			String datastreamids="";
			switch(device.getType()) {
			case 1://传感器
				datastreamids = "WT,DO,pH,DOS";
				break;
			case 2://一体机
				break;
			case 3://控制器
				datastreamids = "KM1,KM2,KM3,KM4,PF,DP1,DP2,DP3,DP4";
				break;
			}
			
			Date dateToday = new Date();
			Date dateYesterday = new Date();
			dateYesterday.setDate(dateYesterday.getDate()-1);
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			String dataFormat1 = sdf1.format(dateYesterday);
			String dataFormatStart = dataFormat1+"T00:00:00";
			dataFormat1 = sdf1.format(dateToday);
			String dataFormatEnd =  dataFormat1+"T00:00:00";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			GetDatapointsListApi api = new GetDatapointsListApi(datastreamids, dataFormatStart, dataFormatEnd, device.getDevice_sn(), null, 6000, null, null,
					null, null, null, key);
			BasicResponse<DatapointsList> response = api.executeApi();
			if(response.errno==0) {
				int count = Integer.parseInt(response.data.getCount().toString()) ;
				if(response.errno == 0 && count!=0) {
					List<DatastreamsItem> datastreamsItemList = response.data.getDevices();
					List<DatastreamsItem> dl= response.getData().getDevices();
					logger.debug("参数个数："+dl.size());
					logger.debug("总共获得数据量为："+response.getData().getCount());	
			
					for(int i=0;i<dl.size();i++) {
						List<String> atList = new ArrayList<String>();
						List<Float> valueList = new ArrayList<Float>();
						DatastreamsItem di = dl.get(i);
						List<DatapointsItem> ld =di.getDatapoints();
						if(di.getId().equals("location")==false) {
							logger.debug(di.getId()+"参数下数据量："+ld.size());
							for(int j=0;j<ld.size();j++) {
								Date date = new Date();
								try {
									date = sdf.parse(ld.get(j).getAt().toString());
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Float value = (float) 0;
								int value3 = 0;
								switch(device.getType()) {
								case 1://传感器
									value  = Float.parseFloat((ld.get(j).getValue().toString()));
									switch(di.getId()) {
									case "pH":
										pH ph = new pH();
										ph.setDate(date);
										ph.setDevice_sn(device.getDevice_sn());
										ph.setValue(value);
										phDao.save(ph);
										break;
									case "DO":
										DO Do = new DO();
										Do.setDate(date);
										Do.setDevice_sn(device.getDevice_sn());
										Do.setValue(value);
										doDao.save(Do);
										break;
									case "DOS":
										DOS dos = new DOS();
										dos.setDate(date);
										dos.setDevice_sn(device.getDevice_sn());
										dos.setValue(value);
										dosDao.save(dos);
										break;
									case "WT":
										WT wt = new WT();
										wt.setDate(date);
										wt.setDevice_sn(device.getDevice_sn());
										wt.setValue(value);
										wtDao.save(wt);
										break;			
										
									}
									break;
								case 2://一体机
									break;
								case 3://控制器
									value3 = Integer.parseInt((ld.get(j).getValue().toString()));
									switch(di.getId()) {
									case "KM1":
										KM1 km1 = new KM1();
										km1.setDate(date);
										km1.setDevice_sn(device.getDevice_sn());
										km1.setValue(value3);
										km1Dao.save(km1);
										break;
									case "KM2":
										KM2 km2 = new KM2();
										km2.setDate(date);
										km2.setDevice_sn(device.getDevice_sn());
										km2.setValue(value3);
										km2Dao.save(km2);
										break;
									case "KM3":
										KM3 km3 = new KM3();
										km3.setDate(date);
										km3.setDevice_sn(device.getDevice_sn());
										km3.setValue(value3);
										km3Dao.save(km3);
										break;
									case "KM4":
										KM4 km4 = new KM4();
										km4.setDate(date);
										km4.setDevice_sn(device.getDevice_sn());
										km4.setValue(value3);
										km4Dao.save(km4);
										break;
									case "PF":
										PF pf  =  new PF();
										pf.setDate(date);
										pf.setDevice_sn(device.getDevice_sn());
										pf.setValue(value3);
										pfDao.save(pf);
										break;
									case "DP1":
										DP1 dp1 = new DP1();
										dp1.setDate(date);
										dp1.setDevice_sn(device.getDevice_sn());
										dp1.setValue(value3);
										dp1Dao.save(dp1);
										break;
									case "DP2":
										DP2 dp2 = new DP2();
										dp2.setDate(date);
										dp2.setDevice_sn(device.getDevice_sn());
										dp2.setValue(value3);
										dp2Dao.save(dp2);
										break;
									case "DP3":
										DP3 dp3 = new DP3();
										dp3.setDate(date);
										dp3.setDevice_sn(device.getDevice_sn());
										dp3.setValue(value3);
										dp3Dao.save(dp3);
										break;
									case "DP4":
										DP4 dp4 = new DP4();
										dp4.setDate(date);
										dp4.setDevice_sn(device.getDevice_sn());
										dp4.setValue(value3);
										dp4Dao.save(dp4);
										break;
									}
									break;
								}
								
								
							}
						
						}				
					}
				}
			}
	
						
		}
	}

	public Map<String, Object> data3days(String device_sn, int way) {
		
		logger.debug("获得设备："+device_sn+"3日数据");
		Map<String, Object> mapReturn = new HashMap<>();
		
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date dateEnd = new Date();//当前时间
		dateEnd.setHours(24);
		dateEnd.setMinutes(0);
		dateEnd.setSeconds(0);
		Date dateStart = new Date();//今日零点
		dateStart.setDate(dateStart.getDate()-2);
		dateStart.setHours(0);
		dateStart.setMinutes(0);
		dateStart.setSeconds(0);
		int min = 24*60;
		
		String dataFormat1 = sdf1.format(dateStart);
		String dataFormat2  = sdf1.format(dateEnd);
		String dataFormatEnd = dataFormat2+"T00:00:00";
		String dataFormatStart = dataFormat1+"T00:00:00";
		

		/**
		 * 今日数据点查询
		 * @param datastreamids:查询的数据流，多个数据流之间用逗号分隔（可选）,String
		 * @param start:提取数据点的开始时间（可选）,String
		 * @param end:提取数据点的结束时间（可选）,String
		 * @param devid:设备ID,String
		 * 
		 * @param duration:查询时间区间（可选，单位为秒）,Integer
		 *  start+duration：按时间顺序返回从start开始一段时间内的数据点
		 *  end+duration：按时间倒序返回从end回溯一段时间内的数据点
		 * 
		 * @param limit:限定本次请求最多返回的数据点数，0<n<=6000（可选，默认1440）,Integer
		 * @param cursor:指定本次请求继续从cursor位置开始提取数据（可选）,String
		 * @param interval:通过采样方式返回数据点，interval值指定采样的时间间隔（可选）,Integer
		 * @param metd:指定在返回数据点时，同时返回统计结果，可能的值为（可选）,String
		 * @param first:返回结果中最值的时间点。1-最早时间，0-最近时间，默认为1（可选）,Integer
		 * @param sort:值为DESC|ASC时间排序方式，DESC:倒序，ASC升序，默认升序,String
		 * @param key:masterkey 或者 设备apikey
		 */
		List<DatapointsItem> pHList = new ArrayList<>();
		List<DatapointsItem> DOLsit = new ArrayList<>();
		List<DatapointsItem> WTList = new ArrayList<>();
		List<DatapointsItem> pHList1 = new ArrayList<>();
		List<DatapointsItem> DOLsit1 = new ArrayList<>();
		List<DatapointsItem> WTList1 = new ArrayList<>();
		
		GetDatapointsListApi api1 = new GetDatapointsListApi("WT", dataFormatStart, dataFormatEnd, device_sn, null, 6000, null, null,
				null, null, null, key);
		BasicResponse<DatapointsList> response1 = api1.executeApi();
		logger.debug("设备："+device_sn + "的3日数据："+response1.getJson());
		if(response1.errno==0) {
			Map<String, Object> map = null;
			List<DatastreamsItem> dl= response1.getData().getDevices();
		
			logger.debug("参数个数："+dl.size());
			logger.debug("总共获得数据量为："+response1.getData().getCount());
			for(int i=0;i<dl.size();i++) {				
				DatastreamsItem di = dl.get(i);
				List<DatapointsItem> ld =di.getDatapoints();
				if("WT".equals(di.getId())) {
					/*System.out.println("WT数据");*/
					for(int j=0;j<ld.size();j++) {
						WTList.add(ld.get(j));
						/*System.out.println(ld.get(j).getValue());*/
					}
				}
								
			}
		}
		
		GetDatapointsListApi api2 = new GetDatapointsListApi("pH", dataFormatStart, dataFormatEnd, device_sn, null, 6000, null, null,
				null, null, null, key);
		BasicResponse<DatapointsList> response2 = api2.executeApi();
		logger.debug("设备："+device_sn + "的3日数据："+response2.getJson());
		if(response2.errno==0) {
			Map<String, Object> map = null;
			List<DatastreamsItem> dl= response2.getData().getDevices();
			logger.debug("参数个数："+dl.size());
			logger.debug("总共获得数据量为："+response1.getData().getCount());
			for(int i=0;i<dl.size();i++) {				
				DatastreamsItem di = dl.get(i);
				List<DatapointsItem> ld =di.getDatapoints();
				/*System.out.println(di.getId()+"参数下数据量："+ld.size());*/
				if("pH".equals(di.getId())) {
					/*System.out.println("pH数据：");*/
					for(int j=0;j<ld.size();j++) {
						pHList.add(ld.get(j));
					/*	System.out.println(ld.get(j).getValue());*/
					}
				}
								
			}
		}
		
		GetDatapointsListApi api3 = new GetDatapointsListApi("DO", dataFormatStart, dataFormatEnd, device_sn, null, 6000, null, null,
				null, null, null, key);
		BasicResponse<DatapointsList> response3 = api3.executeApi();
		logger.debug("设备："+device_sn + "的3日数据："+response3.getJson());
		if(response3.errno==0) {
			Map<String, Object> map = null;
			List<DatastreamsItem> dl= response3.getData().getDevices();
			logger.debug("参数个数："+dl.size());
			logger.debug("总共获得数据量为："+response1.getData().getCount());
			for(int i=0;i<dl.size();i++) {				
				DatastreamsItem di = dl.get(i);
				List<DatapointsItem> ld =di.getDatapoints();
				if("DO".equals(di.getId()) ) {
					/*System.out.println("DO数据：");*/
					for(int j=0;j<ld.size();j++) {
						DOLsit.add(ld.get(j));
						/*System.out.println(ld.get(j).getValue());*/
					}
				}
								
			}
		}				
		for(int i = 0 ; i<(24*3+1) ; i++) {
			Date dateStart1 = new Date();
			dateStart1.setTime(dateStart.getTime());
			dateStart1.setMinutes(dateStart1.getMinutes()-3);
			Date dateEnd1 = new Date();
			dateEnd1.setTime(dateStart.getTime());
			dateEnd1.setMinutes(dateEnd1.getMinutes()+3);

			String start = sdf1.format(dateStart1) + "T" + sdf2.format(dateStart1);
			String end = sdf1.format(dateEnd1) + "T" + sdf2.format(dateEnd1);	
			
	
			float sum1 =0;
			int count1 = 0;
			float sum2 =0;
			int count2 = 0;
			float sum3 =0;
			int count3 = 0;
			for(DatapointsItem dpsi:pHList) {
				String at = dpsi.getAt();
				 try {
					Date dt = sdf3.parse(at);					
					if(dt.getTime()>dateStart1.getTime()&&dt.getTime()<dateEnd1.getTime()) {
						/*System.out.println(dt);	*/			
						sum1 += Float.parseFloat((String)dpsi.getValue());
						count1++;
					}					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for(DatapointsItem dpsi:DOLsit) {
				String at = dpsi.getAt();
				 try {
					Date dt = sdf3.parse(at);					
					if(dt.getTime()>dateStart1.getTime()&&dt.getTime()<dateEnd1.getTime()) {
			
						sum2 += Float.parseFloat((String)dpsi.getValue());
						count2++;
					}					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for(DatapointsItem dpsi:WTList) {
				String at = dpsi.getAt();
				 try {
					Date dt = sdf3.parse(at);					
					if(dt.getTime()>dateStart1.getTime()&&dt.getTime()<dateEnd1.getTime()) {				
						sum3 += Float.parseFloat((String)dpsi.getValue());
						count3++;
					}					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			pHList1.add(new DatapointsItem(sdf3.format(dateStart), ((float)(Math.round(sum1/count1*100))/100)==0?null:(float)(Math.round(sum1/count1*100))/100));
			DOLsit1.add(new DatapointsItem(sdf3.format(dateStart), ((float)(Math.round(sum2/count2*100))/100)==0?null:(float)(Math.round(sum2/count2*100))/100));
			WTList1.add(new DatapointsItem(sdf3.format(dateStart), ((float)(Math.round(sum3/count3*100))/100)==0?null:(float)(Math.round(sum3/count3*100))/100));
			dateStart.setMinutes(dateStart.getMinutes()+60);
		}
		
		List<String> doatList = new ArrayList<>();
		List<Float> dovalueList = new ArrayList<>();
		Map<String, Object> DOMap = new HashMap<>();
		for(int t = 0;t<DOLsit1.size();t++) {
			doatList.add((String) DOLsit1.get(t).getAt());
			dovalueList.add((Float) DOLsit1.get(t).getValue());
		}
		DOMap.put("value", dovalueList);
		DOMap.put("at", doatList);
		
		List<String> wtatList = new ArrayList<>();
		List<Float> wtvalueList = new ArrayList<>();
		Map<String, Object> WTMap = new HashMap<>();
		for(int t = 0;t<WTList1.size();t++) {
			wtatList.add((String) WTList1.get(t).getAt());
			wtvalueList.add((Float) WTList1.get(t).getValue());
		}
		WTMap.put("value", wtvalueList);
		WTMap.put("at", wtatList);
		
		List<String> phatList = new ArrayList<>();
		List<Float> phvalueList = new ArrayList<>();
		Map<String, Object> pHMap = new HashMap<>();
		for(int t = 0;t<pHList1.size();t++) {
			phatList.add((String) pHList1.get(t).getAt());
			phvalueList.add((Float) pHList1.get(t).getValue());
		}
		pHMap.put("value", phvalueList);
		pHMap.put("at", phatList);
		
		mapReturn.put("pH", pHMap);
		mapReturn.put("DO", DOMap);
		mapReturn.put("WT", WTMap);
		
		
		Sensor sensor = sensorDao.findSensorByDeviceSns(device_sn);
		if(sensor!=null) {
			logger.debug(sensor.getPondId());
		    List<Controller> controllerList= controllerDao.findByPondId(sensor.getPondId());
		    Controller conOxygen = new Controller();
		    Limit_Install limit = null;
			for(Controller controller:controllerList) {
				if(controller.getType()==0) {
					conOxygen = controller;
					limit = limitDao.findLimitByDeviceSnsAndWay(conOxygen.getDevice_sn(), conOxygen.getPort());						
					break;
				}
			}
			mapReturn.put("Limit", limit);
			
		}
		return mapReturn;
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
	

	public Map<String, Object> delLimit(String device_sn,int way) {
		List<Controller> controllerList = controllerDao.findControllerByDeviceSnAndWay(device_sn, way);
		List<Integer> pondIds =new ArrayList<>();
		for(Controller con:controllerList) {
			pondIds.add(con.getPondId());
		}
		for(int pondId:pondIds) {
			List<Sensor> sensorList = sensorDao.findSensorsByPondId(pondId);
			for(Sensor sensor:sensorList) {
				List<Dev_Trigger> triggerList = dev_triggerDao.findDev_TriggerBydevsn(sensor.getDevice_sn());
				for(Dev_Trigger trigger:triggerList) {
					if(trigger.getTrigertype()==2) {
						DeleteTriggersApi api = new DeleteTriggersApi(trigger.getTriger_id(), key);
						BasicResponse<Void> response = api.executeApi();
						System.out.println("errno:"+response.errno+" error:"+response.error);
					}
				}
			}
		}		
		limitDao.deleteByDevice_snandWay(device_sn, way);
		return RESCODE.SUCCESS.getJSONRES();
	}
	
	public Map<String, Object> modifyEquipment(String device_sn, Object newEquipment) {
		newEquipment.getClass().getName();
	
		return null;
	}
	
	public Map<String, Object> modifySensor(Sensor...sensors){
		System.out.println("进入修改");
		boolean flag = false;
		for(Sensor sensor:sensors) {
			if(deviceDao.findDevice(sensor.getDevice_sn())==null) {
				flag = false;
			}else {
				sensorDao.updateSensorByDevicesn(sensor);
				Pond pondExsit=pondDao.findPondByPondId(sensor.getPondId());
				float lat = 0;
				float lon  = 0;
				GetLatesDeviceData api = new GetLatesDeviceData(sensor.getDevice_sn(),key);
		        BasicResponse<DeciceLatestDataPoint> response = api.executeApi();
		        System.out.println("errno:"+response.errno+" error:"+response.error);
		        System.out.println(response.getJson());
		        List<cmcc.iot.onenet.javasdk.response.device.DeciceLatestDataPoint.DeviceItem.DatastreamsItem> DatastreamsList = response.data.getDevices().get(0).getDatastreams();
		        for(int i=0;i<DatastreamsList.size();i++) {
		        	if(DatastreamsList.get(i).getId().equals("location")) {
		        		System.out.println(DatastreamsList.get(i).getId());
			        	System.out.println(DatastreamsList.get(i).getValue());
			        	String location = DatastreamsList.get(i).getValue().toString();
			        	lat = Float.parseFloat(location.substring(5, location.indexOf(",")));
			        	System.out.println(lat);
			        	lon = Float.parseFloat(location.substring(location.indexOf(",")+6,location.length()-1));
			        	System.out.println(lon);
		        	}
		        	
		        }				
				pondExsit.setLatitude(lat);
				pondExsit.setLongitude(lon);
				String address = webServiceService.getLocation(lon, lat);
				pondExsit.setAddress(address);						
				pondDao.update(pondExsit);					
				flag = true;
			}
		}
		if(flag) {
			System.out.println("修改成功");
			return RESCODE.SUCCESS.getJSONRES();
		}else {
			System.out.println("修改失败");
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
		logger.debug("触发器触发");
		logger.debug("触发器"+data);
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
		logger.debug("dev_id："+dev_id);
		logger.debug("数据流："+ds_id);
		logger.debug("触发器类型："+triggertype);
		logger.debug("触发器id："+triggerid);
		logger.debug("数值为："+value);
		
		//根据触发器id获得触发器，根据设备编号获得传感器（传感器只有一路），为传感器设置状态
		//状态值0：正常，1：预警，2：危险
		Dev_Trigger trigger = dev_triggerDao.findTriggerBytriggerId(triggerid);
		if(trigger!=null) {
			logger.debug("触发器本地类型："+trigger.getTrigertype());
			//控制器的缺相断电触发
			if(trigger.getTrigertype()==3) {
				String device_sn = trigger.getDevice_sn();
				List<Controller> controllerList = controllerDao.findControllerByDeviceSns(device_sn);
				WXUser wxUser = wxUserDao.findUserByRelation(controllerList.get(0).getRelation());
				String publicOpenID = userService.getPublicOpenId(wxUser.getOpenId());
				if(ds_id.equals("PF")) {
					WechatSendMessageUtils.sendWechatAlarmMessages("控制器断电", publicOpenID, device_sn);
					
					String json = "{\"deviceName\":\"" +controllerList.get(0).getDevice_sn()+ "\",\"way\":" + 0 + "}";
					try {
						logger.debug("准备启用阿里云语音服务");
						SingleCallByTtsResponse scbtr =VmsUtils.singleCallByTts(wxUser.getPhone(), "TTS_126781509", json);
					} catch (ClientException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					String dp = ds_id.substring(2, 3);
					int i = Integer.parseInt(dp);
					WechatSendMessageUtils.sendWechatAlarmMessages("控制器第"+i+"路缺相", publicOpenID, device_sn);
					String json = "{\"deviceName\":\"" + controllerList.get(i-1).getName() + "\",\"way\":" + i + "}";
					try {
						logger.debug("准备启用阿里云语音服务");
						VmsUtils.singleCallByTts(wxUser.getPhone(), "TTS_126866281", json);
					} catch (ClientException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if(trigger.getTrigertype()==2) {//增氧机触发
				logger.debug("增氧机触发");
				logger.debug("触发设备："+trigger.getDevice_sn());
				Sensor sensor = sensorDao.findSensorByDeviceSns(trigger.getDevice_sn());
				sensor.getPondId();
				List<Controller> controllers = controllerDao.findByRelation(sensor.getRelation());
				List<Controller> controllerList = new ArrayList<>();
				for(Controller controller:controllers) {
					if( controller.getPondId() == sensor.getPondId()) {
						controllerList.add(controller);
					}
				}
				logger.debug(controllerList.size());
				if(controllerList !=null &&controllerList.size()>0) {
					WXUser wxUser = wxUserDao.findUserByRelation(controllerList.get(0).getRelation());
					String publicOpenID = userService.getPublicOpenId(wxUser.getOpenId());
					if(triggertype.equals("<")) {//低于溶氧下限，打开增氧机
						/*
						 *判断增氧机状态 
						 */
						String divsn= controllerList.get(0).getDevice_sn();
						int way = trigger.getWay();
						String id = "KM"+(way+1);
						GetDatastreamApi api = new GetDatastreamApi(divsn, id, key);
						BasicResponse<DatastreamsResponse> response = api.executeApi();
						int currentvalue =  Integer.parseInt(response.data.getCurrentValue().toString()) ;	
						if(currentvalue != 1) {
							WechatSendMessageUtils.sendWechatOnOffMessages("低于溶氧下限，打开增氧机", publicOpenID, controllerList.get(0).getDevice_sn());						
							String text = "KM"+(way+1)+":"+1;
							CMDUtils.sendStrCmd(divsn,text);
						}						
					}else if(triggertype.equals(">")) {//高于溶氧上限，关闭增氧机
						/*
						 *判断增氧机状态 
						 */
						String divsn= controllerList.get(0).getDevice_sn();
						int way = trigger.getWay();
						String id = "KM"+(way+1);
						GetDatastreamApi api = new GetDatastreamApi(divsn, id, key);
						BasicResponse<DatastreamsResponse> response = api.executeApi();
						int currentvalue =  Integer.parseInt(response.data.getCurrentValue().toString()) ;	
						if(currentvalue != 0) {
							WechatSendMessageUtils.sendWechatOnOffMessages("高于溶氧上限，关闭增氧机", publicOpenID, controllerList.get(0).getDevice_sn());						
							String text = "KM"+(way+1)+":"+0;
							CMDUtils.sendStrCmd(divsn,text);
						}
						
					}
				}
					
			}
			Sensor sensor = sensorDao.findSensorByDeviceSns(trigger.getDevice_sn());
			
			if(sensor != null) {
				logger.debug("获取设备"+sensor.getDevice_sn()+"的实时数据");
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
	    	 int trigger22 = addTrigger("WT", device_sn, ">", 30, 0,0);
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
    		 if(trigger11==1&&trigger12==1&&trigger21==1&&trigger22==1&&trigger23==1&&trigger31==1&&trigger32==1&&trigger33==1&&trigger34==1) {
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
    	 int trigger22 = addTrigger("WT", device_sn, ">", 30, 0,0);
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
 		 if(trigger11==1&&trigger12==1&&trigger21==1&&trigger22==1&&trigger23==1&&trigger31==1&&trigger32==1&&trigger33==1&&trigger34==1) {
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
		//String url = "https://45b427a0.ngrok.io/fishery/api/equipment/triggeractive";
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
			logger.debug(e.getMessage());
			return 1;
		}
	}
	
	public String getControllerPortStatus(String devId,int port) {
		logger.debug("获取设备"+devId+"第"+(port+1)+"路的状态");
		 String key = "7zMmzMWnY1jlegImd=m4p9EgZiI=";
		 String id = "KM"+port;
	        /**
	    	 * 批量查询设备状态
	         * 参数顺序与构造函数顺序一致
	    	 * @param devIds:设备id用逗号隔开, 限制1000个设备
	    	 * @param key :masterkey 或者 设备apikey,String
	    	 */
	     GetDevicesStatus api1 = new GetDevicesStatus(devId,key);
	     BasicResponse<DevicesStatusList> response1 = api1.executeApi();
	     System.out.println("errno:"+response1.errno+" error:"+response1.error);
	     System.out.println(response1.data.getDevices().get(0).getIsonline());
	     if(response1.errno == 0) {//获取设备状态
	    	 System.out.println("获取设备状态");
	    	 if(response1.data.getDevices().get(0).getIsonline() != null) {
	    		 boolean online = response1.data.getDevices().get(0).getIsonline();
	    		 if(online == true) {//设备在线
	    			 System.out.println("设备在线");
	    			 Map<String, Object> controllerDataMap=new HashMap<>();
	    			 GetLatesDeviceData lddapi = new GetLatesDeviceData(devId, key);
				     BasicResponse<DeciceLatestDataPoint> response2 = lddapi.executeApi();
				     System.out.println(response2.getJson()); 
				     if(response2.errno == 0) {
				        	List<cmcc.iot.onenet.javasdk.response.device.DeciceLatestDataPoint.DeviceItem.DatastreamsItem> datastreamsList = response2.data.getDevices().get(0).getDatastreams();
				        	if(datastreamsList!=null) {
				        		for(int i=0;i<datastreamsList.size();i++) {
						        	controllerDataMap.put(datastreamsList.get(i).getId(), datastreamsList.get(i).getValue());
						        }
				        	}
				     }
				    
	    			 if(controllerDataMap.get("PF") !=null) {
	    				 String  PF = (String) controllerDataMap.get("PF");					     
	    				 if(PF.equals("1")) {//断电
	    					 System.out.println("断电");
	    					 return "2";
	    				 }else {
	    					
	    					 if(controllerDataMap.get("DP"+(port+1))!=null) {
	    						 String DP = (String) controllerDataMap.get("DP"+(port+1));
	    					     
	    						 if(DP.equals("1")) {//缺相
	    							 System.out.println("缺相");
	    							 return "2";
	    						 }else {
	    							 System.out.println("设备正常在线，获取其状态");
	    							 if(controllerDataMap.get("KM"+(port+1))!=null) {
	    								
	    								 String KM = (String) controllerDataMap.get("KM"+(port+1));
	    								 return KM;
	    							 }else {
	    								 return "2";
	    							 }
	    							
	    						 }
	    					 }else {
	    						 if(controllerDataMap.get("KM"+(port+1))!=null) {
	    							 String KM = (String) controllerDataMap.get("KM"+(port+1));
	    							 return KM;
	    						 }else {
	    							 return "2";
	    						 }
	    					 }
	    				 }
	    				 
	    			 }else {
	    				 if(controllerDataMap.get("KM"+(port+1))!=null) {
	    					 String KM = (String) controllerDataMap.get("KM"+(port+1));
							 return KM;
						 }else {
							 return "2";
						 }
	    			 }	    			 
	    		 }return "2"; 
	    	 }return "2"; 	    	 
	     }return "2"; 

		
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
		logger.debug("进入用户"+relation+"的个人诊断");
		List<Map<String, Object>> personalDai = new ArrayList<>(); 
 		List<Pond> pondList = pondDao.queryPondByRelation(relation);
		for(Pond pond:pondList) {
			logger.debug("进入塘口："+pond.getId());
			Map<String, Object> pondData = getDianosing(pond.getId());
			personalDai.add(pondData);
		}		
		return personalDai;
	}
	
	
	public Map<String, Object> getDianosing(int pondId){
		//根据pondId获得池塘各个参数与分析结果和解决方案
		logger.debug("根据"+pondId+"获得池塘各个参数与分析结果和解决方案");
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
			logger.debug("Analysis1"+Analysis1);
			logger.debug("Analysis2"+Analysis2);
			Analysis = (Analysis1==null?"":(Analysis1))+(Analysis2==null?"":(Analysis2));
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
		logger.debug("分析塘口："+pondId+"的数据");

		
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
			if(WTMap!=null) {
				valueList = (List<Float>) WTMap.get("value");
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
		List<Dev_Trigger> devTriggerList = dev_triggerDao.findDev_TriggerByDevsnAndWay(device_sn, way);				
			/**
			 * 触发器删除
			 * @param tirggerid:触发器ID,String
			 * @param key:masterkey 或者 设备apikey
			 */
		for(Dev_Trigger devTrigger:devTriggerList) {
			DeleteTriggersApi api = new DeleteTriggersApi(devTrigger.getTriger_id(), key);
			BasicResponse<Void> response = api.executeApi();
			System.out.println("errno:"+response.errno+" error:"+response.error);
		}		
	}
	public void deleteTriggerByTriggerId(String triggerId) {					
			/**
			 * 触发器删除
			 * @param tirggerid:触发器ID,String
			 * @param key:masterkey 或者 设备apikey
			 */
			logger.debug("在onenet上删除触发器");
			DeleteTriggersApi api = new DeleteTriggersApi(triggerId, key);
			BasicResponse<Void> response = api.executeApi();
			System.out.println("errno:"+response.errno+" error:"+response.error);
		
	}
	
	public List<Controller> getAllControllers(){
		return controllerDao.getAllControllers();
	}
}
