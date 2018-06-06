package com.geariot.platform.fishery.service;

import cmcc.iot.onenet.javasdk.api.triggers.DeleteTriggersApi;
import com.geariot.platform.fishery.dao.*;
import com.geariot.platform.fishery.entities.*;
import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.model.*;
//import com.geariot.platform.fishery.socket.CMDUtils;
import com.geariot.platform.fishery.utils.DataExportExcel;
import com.geariot.platform.fishery.wxutils.WechatSendMessageUtils;

import cmcc.iot.onenet.javasdk.api.datapoints.GetDatapointsListApi;
import cmcc.iot.onenet.javasdk.api.triggers.AddTriggersApi;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.triggers.NewTriggersResponse;
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
import com.geariot.platform.fishery.service.BindService;

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
	private SocketSerivce socketService;

	@Autowired
	private BindService bindService;
	

	private String type = "";
	private String relation = "";
	private Company company = null;
	private AIO aio = null;
	private Sensor sensor = null;
	private Controller controller = null;
	private WXUser wxUser = null;

//	public Map<String, Object> setLimit(Limit_Install limit_Install) {
//		String deviceSn;
//		try {
//			deviceSn = limit_Install.getDevice_sn().substring(0, 2);
//			Map<String, Object> map = CMDUtils.downLimitCMD(limit_Install);
//			if (!map.containsKey("0"))
//				return map;
//		} catch (Exception e) {
//			return RESCODE.DEVICESNS_INVALID.getJSONRES();
//		}
//		if (deviceSn.equals("01") || deviceSn.equals("02")) {
//			if (aioDao.findAIOByDeviceSns(limit_Install.getDevice_sn()) == null) {
//				return RESCODE.DEVICESNS_INVALID.getJSONRES();
//			}
//		} else if (deviceSn.equals("03")) {
//			if (sensorDao.findSensorByDeviceSns(limit_Install.getDevice_sn()) == null) {
//				return RESCODE.DEVICESNS_INVALID.getJSONRES();
//			}
//		} else
//			return RESCODE.DEVICESNS_INVALID.getJSONRES();
//
//		limitDao.updateLimit(limit_Install);
//
//		return RESCODE.SUCCESS.getJSONRES();
//
//	}

	public Map<String, Object> delEquipment(String device_sn) {

		String devices;
		try {
			devices = device_sn.trim().substring(0, 2);
			device_sn = device_sn.substring(2);
			deviceDao.delete(device_sn);
			List<Dev_Trigger> trilist = dev_triggerDao.findDev_TriggerBydevsn(device_sn);
			if (trilist !=null) {
				for (Dev_Trigger dev_trigger : trilist) {
					String tirggerid = dev_trigger.getTriger_id();
					String key = "LTKhU=GLGsWmPrpHICwWOnzx=bA=";
					/**
					 * 触发器删除
					 * @param tirggerid:触发器ID,String
					 * @param key:masterkey 或者 设备apikey
					 */
					DeleteTriggersApi api = new DeleteTriggersApi(tirggerid, key);
					BasicResponse<Void> response = api.executeApi();
					System.out.println("errno:"+response.errno+" error:"+response.error);
				}
			}
			dev_triggerDao.delete(device_sn);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
			if (devices.equals("02")) {
				aioDao.delete(device_sn);
				statusDao.delete(device_sn);
			} else if (devices.equals("01")) {
				int sensorId = sensorDao.findSensorByDeviceSns(device_sn).getId();
				sensorDao.delete(device_sn);
				List<Sensor_Controller> list = sensor_ControllerDao.list(sensorId);
				for (Sensor_Controller sensor_Controller : list) {
					controller = controllerDao.findControllerById(sensor_Controller.getControllerId());
					if (controller == null) {
						continue;
					} else {
						//changeControllerPortStatusClose(controller, sensor_Controller.getController_port());
					}
				}
				sensor_ControllerDao.delete(sensorId);
			} else if (devices.equals("03")) {
				int controllerId = controllerDao.findControllerByDeviceSns(device_sn).getId();
				controllerDao.delete(device_sn);
				List<Sensor_Controller> list = sensor_ControllerDao.controller(controllerId);
				for (Sensor_Controller sensor_Controller : list) {
						sensor = sensorDao.findSensorById(sensor_Controller.getSensorId());
						if (sensor == null) {
							continue;
						} else {
							changeSensorPortStatusClose(sensor, sensor_Controller.getSensor_port());
						}
				}
				sensor_ControllerDao.deleteController(controllerId);
			} else {
				return RESCODE.DELETE_ERROR.getJSONRES();
			}

		return RESCODE.SUCCESS.getJSONRES();
	}

	private void changeSensorPortStatusClose(Sensor sensor, int port) {
		StringBuffer sb = new StringBuffer(sensor.getPort_status());
		sb.setCharAt(port - 1, '0');
		sensor.setPort_status(sb.toString());
	}

	private void changeSensorPortStatusOn(Sensor sensor, int port) {
		StringBuffer sb = new StringBuffer(sensor.getPort_status());
		sb.setCharAt(port - 1, '1');
		sensor.setPort_status(sb.toString());
	}

//	private void changeControllerPortStatusOn(Controller controller, int port) {
//		StringBuffer sb = new StringBuffer(controller.getPort_status());
//		sb.setCharAt(port - 1, '1');
//		controller.setPort_status(sb.toString());
//	}
//
//	private void changeControllerPortStatusClose(Controller controller, int port) {
//		StringBuffer sb = new StringBuffer(controller.getPort_status());
//		sb.setCharAt(port - 1, '0');
//		controller.setPort_status(sb.toString());
//	}

	private void addVirtualData(List<Sensor_Data> sensor_Datas){
		Sensor_Data temp = null;
		List<Sensor_Data> tempList = new ArrayList<>();
		if(sensor_Datas.size()>1){
			for(int i=0;i<sensor_Datas.size()-1;i++){
				if((sensor_Datas.get(i).getReceiveTime().getTime()+7200000) < sensor_Datas.get(i+1).getReceiveTime().getTime()){
					temp = new Sensor_Data();
					temp.setDevice_sn(sensor_Datas.get(i).getDevice_sn());
					temp.setId(sensor_Datas.size()+i+2);
					temp.setOxygen(0);
					temp.setpH_value(0);
					temp.setWater_temperature(0);
					temp.setSaturation(0);
					temp.setWay(sensor_Datas.get(i).getWay());
					temp.setReceiveTime(new Date(sensor_Datas.get(i).getReceiveTime().getTime()+3600000));
					tempList.add(temp);
				}
			}
			sensor_Datas.addAll(tempList);
			Collections.sort(sensor_Datas, new Comparator<Sensor_Data>(){
				public int compare(Sensor_Data o1, Sensor_Data o2) {
					if(o1.getReceiveTime().getTime()>o2.getReceiveTime().getTime()){
						return 1;
					}
					if(o1.getReceiveTime().getTime() == o2.getReceiveTime().getTime()){
						return 0;
					}
					return -1;
				}
			});
		}

	}

	public boolean exportData(String device_sn, String startTime, String endTime, HttpServletResponse response) {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			List<ExcelData> list = sensor_DataDao.getExcelData(device_sn, sdf.parse(startTime), sdf.parse(endTime));
			String[] fields = { "Id", "device_sn", "oxygen", "ph", "receiveTime", "waterTemperature" };
			DataExportExcel dataExportExcel = new DataExportExcel();
			HSSFWorkbook wb = dataExportExcel.generateExcel();
			wb = dataExportExcel.generateSheet(wb, "DataTable", fields, list);
			dataExportExcel.export(wb, response);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Map<String, Object> addEquipment(String device_sn, String name, String relation,int type,int pondId) {
		String deviceSn;
		try {
			deviceSn = device_sn.trim().substring(0, 2);
			device_sn = device_sn.substring(2);
			Device device = new Device();
			device.setDevice_sn(device_sn);
			device.setType(type);
			deviceDao.save(device);
			System.out.println("sss");
		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}


		logger.debug("塘口Id:" + pondId + "尝试与传感器设备,设备编号为:" + device_sn + "进行绑定...");
		Pond pond = pondDao.findPondByPondId(pondId);
		if (pond == null) {
			logger.debug("塘口Id:" + pondId + "在数据库中无记录!!!");
			return RESCODE.NOT_FOUND.getJSONRES();
		} else {
		if (deviceSn.equals("02")) {
			AIO exist = aioDao.findAIOByDeviceSns(device_sn);
			if (exist != null) {
				return RESCODE.AIO_EXIST.getJSONRES();
			} else {
				AIO aio = new AIO();
				aio.setDevice_sn(device_sn);
				aio.setName(name);
				aio.setRelation(relation);
				aio.setStatus("11");
				aio.setPondId(pondId);
				aioDao.save(aio);
				// 初始化两个增氧机状态
				AeratorStatus status1 = new AeratorStatus();
				status1.setDevice_sn(device_sn);
				status1.setOn_off(false);
				status1.setTimed(false);
				status1.setWay(1);
				statusDao.save(status1);
				AeratorStatus status2 = new AeratorStatus();
				status2.setDevice_sn(device_sn);
				status2.setOn_off(false);
				status2.setTimed(false);
				status2.setWay(2);
				statusDao.save(status2);
				return RESCODE.SUCCESS.getJSONRES();
			}
		} else if (deviceSn.equals("01")) {
			Sensor exist = sensorDao.findSensorByDeviceSns(device_sn);
			if (exist != null) {
				return RESCODE.SENSOR_EXIST.getJSONRES();
			} else {
				Sensor sensor = new Sensor();
				sensor.setDevice_sn(device_sn);
				sensor.setName(name);
				sensor.setRelation(relation);
				sensor.setStatus(1);
				sensor.setPondId(pondId);
				sensor.setPort_status("00");
				sensorDao.save(sensor);


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
				String dsid="Battery";
				String url = "http://xx.bb.com";
				List<String> devids=new ArrayList<String>();
				devids.add(device_sn);
				String con = "==";
				int threshold = 100;
				String key = "LTKhU=GLGsWmPrpHICwWOnzx=bA=";
				int triggerid;

				AddTriggersApi api = new AddTriggersApi(null, dsid, devids, null, url, con, threshold, key);
				try{
					BasicResponse<NewTriggersResponse> response = api.executeApi();
					System.out.println(response.getJson());
					JSONObject tempjson = JSONObject.fromObject(response.getJson());
					int errnoint = tempjson.getInt("errno");
					if (errnoint==0){
						JSONObject triobj = tempjson.getJSONObject("data");
						triggerid = triobj.getInt("trigger_id");

						List<Dev_Trigger> triggerexist = dev_triggerDao.findDev_TriggerBydevsn(device_sn);
						if (exist != null) {
							return RESCODE.SENSOR_EXIST.getJSONRES();
						} else {
							Dev_Trigger trigger = new Dev_Trigger();
							trigger.setDevice_sn(device_sn);
							trigger.setTriger_id(String.valueOf(triggerid));
							trigger.setTrigertype(3);
							dev_triggerDao.save(trigger);
						}

					}
				}catch(Exception e){
					System.out.println(e.getMessage());
				}
				threshold = 98;
				AddTriggersApi api1 = new AddTriggersApi(null, dsid, devids, null, url, con, threshold, key);
				try{
					BasicResponse<NewTriggersResponse> response = api1.executeApi();
					System.out.println(response.getJson());
					JSONObject tempjson = JSONObject.fromObject(response.getJson());
					int errnoint = tempjson.getInt("errno");
					if (errnoint==0){
						JSONObject triobj = tempjson.getJSONObject("data");
						triggerid = triobj.getInt("trigger_id");

						List<Dev_Trigger> triggerexist = dev_triggerDao.findDev_TriggerBydevsn(device_sn);
						if (exist != null) {
							return RESCODE.SENSOR_EXIST.getJSONRES();
						} else {
							Dev_Trigger trigger = new Dev_Trigger();
							trigger.setDevice_sn(device_sn);
							trigger.setTriger_id(String.valueOf(triggerid));
							trigger.setTrigertype(3);
							dev_triggerDao.save(trigger);
						}

					}
				}catch(Exception e){
					System.out.println(e.getMessage());
				}

				threshold = 50;
				AddTriggersApi api2 = new AddTriggersApi(null, dsid, devids, null, url, con, threshold, key);
				try{
					BasicResponse<NewTriggersResponse> response = api2.executeApi();
					System.out.println(response.getJson());
					JSONObject tempjson = JSONObject.fromObject(response.getJson());
					int errnoint = tempjson.getInt("errno");
					if (errnoint==0){
						JSONObject triobj = tempjson.getJSONObject("data");
						triggerid = triobj.getInt("trigger_id");

						List<Dev_Trigger> triggerexist = dev_triggerDao.findDev_TriggerBydevsn(device_sn);
						if (exist != null) {
							return RESCODE.SENSOR_EXIST.getJSONRES();
						} else {
							Dev_Trigger trigger = new Dev_Trigger();
							trigger.setDevice_sn(device_sn);
							trigger.setTriger_id(String.valueOf(triggerid));
							trigger.setTrigertype(3);
							dev_triggerDao.save(trigger);
						}

					}
				}catch(Exception e){
					System.out.println(e.getMessage());
				}



				return RESCODE.SUCCESS.getJSONRES();
			}
		} else if (deviceSn.equals("03")) {
			Controller exist = controllerDao.findControllerByDeviceSns(device_sn);
			if (exist != null) {
				return RESCODE.SENSOR_EXIST.getJSONRES();
			} else {
				Controller controller = new Controller();
				controller.setDevice_sn(device_sn);
				controller.setName(name);
				controller.setRelation(relation);
				controller.setStatus(1);
				//controller.setPort_status("0000");
				controllerDao.save(controller);
				return RESCODE.SUCCESS.getJSONRES();
			}
		} else {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		}
	}

//	public Map<String, Object> realTimeData(String device_sn, int way) {
//		String deviceSn;
//		Sensor_Data data = null;
//		Map<String, Object> map = null;
//		Date current_time=new Date();
//		Date last_time;
//		try {
//			deviceSn = device_sn.trim().substring(0, 2);
//		} catch (Exception e) {
//			return RESCODE.DEVICESNS_INVALID.getJSONRES();
//		}
//		if (deviceSn.equals("01") || deviceSn.equals("02")) {
//			if (aioDao.findAIOByDeviceSns(device_sn) == null) {
//				return RESCODE.DEVICESNS_INVALID.getJSONRES();
//			}
//			data = sensor_DataDao.findDataByDeviceSnAndWay(device_sn, way);
//			last_time = data.getReceiveTime();
//			long diff = current_time.getTime() - last_time.getTime();
//			long minutes = diff / (1000 * 60);
//			if (minutes < 6) {
//				AIO aio = aioDao.findAIOByDeviceSns(device_sn);
//				map = RESCODE.SUCCESS.getJSONRES(data);
//				Limit_Install install = limitDao.findLimitByDeviceSnsAndWay(device_sn, way);
//				StringBuffer sb = new StringBuffer(aio.getStatus());
//				if (install != null) {
//					map.put("low_limit", install.getLow_limit());
//					map.put("up_limit", install.getUp_limit());
//					map.put("high_limit", install.getHigh_limit());
//					map.put("status", String.valueOf(sb.charAt(way - 1)));
//					map.put("name", aio.getName());
//				} else {
//					map.put("status", String.valueOf(sb.charAt(way - 1)));
//					map.put("name", aio.getName());
//					map.put("low_limit", 5);
//					map.put("up_limit", 10);
//					map.put("high_limit", 15);
//				}
//				return map;
//			}else
//				return RESCODE.OFF_LINE.getJSONRES();
//
//		} else if (deviceSn.equals("03")) {
//			if (sensorDao.findSensorByDeviceSns(device_sn) == null) {
//				return RESCODE.DEVICESNS_INVALID.getJSONRES();
//			}
//			data = sensor_DataDao.findDataByDeviceSns(device_sn);
//			last_time = data.getReceiveTime();
//			long diff = current_time.getTime() - last_time.getTime();
//			long minutes = diff / (1000 * 60);
//			if (minutes < 6) {
//				Sensor sensor = sensorDao.findSensorByDeviceSns(device_sn);
//				map = RESCODE.SUCCESS.getJSONRES(data);
//				Limit_Install install = limitDao.findLimitByDeviceSns(device_sn);
//				if (install != null) {
//					map.put("low_limit", install.getLow_limit());
//					map.put("up_limit", install.getUp_limit());
//					map.put("high_limit", install.getHigh_limit());
//					map.put("status", sensor.getStatus());
//					map.put("name", sensor.getName());
//				} else {
//					map.put("status", sensor.getStatus());
//					map.put("name", sensor.getName());
//					map.put("low_limit", 5);
//					map.put("up_limit", 10);
//					map.put("high_limit", 15);
//				}
//				return map;
//			} else
//				return RESCODE.OFF_LINE.getJSONRES();
//		}else
//			return RESCODE.DEVICESNS_INVALID.getJSONRES();
//	}
//	
	public Map<String, Object> realTimeData(String device_sn) {
		System.out.println(device_sn);
		String datastreamIds="temperature";
		String devId=device_sn;
		String sort = "ASC" ;
		String key ="BzFI2NGGcWgiMKODrBDiGkA7Psc=";
		Integer first = 1;
		
    	GetDatapointsListApi api = new GetDatapointsListApi(datastreamIds,devId,first,sort,key);
    	
    	JSONObject tempjson = JSONObject.fromObject(api.executeApi().getJson());
    	System.out.println(tempjson);
    	JSONObject temp = tempjson.getJSONObject("data");
    	JSONArray array= temp.getJSONArray("datastreams");
    	JSONObject obj1 = array.getJSONObject(0);
    	JSONArray obj2 =obj1.getJSONArray("datapoints");
    	JSONObject obj3=obj2.getJSONObject(0);
    	int obj4 =obj3.getInt("value");
    	//String obj5=obj3.getString("at");    	
     	System.out.println(obj4);
    	return temp;

	}
	
	public Map<String, Object> myEquipment(String relation) {
		List<Sensor> sensors = sensorDao.querySensorByNameAndRelation(relation, null);
		List<AIO> aios = aioDao.queryAIOByNameAndRelation(relation, null);
		List<Controller> controllers = controllerDao.queryControllerByNameAndRelation(relation, null);
		Map<String, Object> result = RESCODE.SUCCESS.getJSONRES();
		result.put("sensor", sensors);
		result.put("controller", controllers);
		result.put("aio", aios);
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

	public Map<String, Object> adminFindEquipment(String device_sn, String userName, int page, int number) {
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
	}

	private Map<String, Object> deviceSnConditionQuery(String device_sn, int from, int number) {
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
	}

	private List<Equipment> shareDealMethod(List<Equipment> equipments) {
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
	}

	private Map<String, Object> noConditionsQuery(int from, int number) {
		List<Equipment> equipments = pondDao.adminFindEquipmentAll(from, number);
		shareDealMethod(equipments);
		long count = pondDao.adminFindEquipmentCountAll();
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
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
		List<Sensor_Data> list = new ArrayList<>();
		if (way > 0) {
			list = sensor_DataDao.today(device_sn, way);
		} else {
			list = sensor_DataDao.today(device_sn);
		}
		addVirtualData(list);
		List<PH> phs = new ArrayList<>();
		List<Oxygen> oxygens = new ArrayList<>();
		List<Temperature> temperatures = new ArrayList<>();
		PH ph = null;
		Oxygen oxygen = null;
		Temperature temperature = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// for (Sensor_Data sensor_Data : list) {
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
			addVirtualData(splitlist);
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
		addVirtualData(list);
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
		addVirtualData(splitlist);
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

//	public Map<String, Object> autoSet(Limit_Install limit_Install, Timer[] timers) {
//		String type;
//		try {
//			type = limit_Install.getDevice_sn().substring(0, 2);
//			if (type.equals("01") || type.equals("02")) {
//				if (aioDao.findAIOByDeviceSns(limit_Install.getDevice_sn()) == null) {
//					return RESCODE.DEVICESNS_INVALID.getJSONRES();
//				}
//			} else if (type.equals("03")) {
//				if (sensorDao.findSensorByDeviceSns(limit_Install.getDevice_sn()) == null) {
//					return RESCODE.DEVICESNS_INVALID.getJSONRES();
//				}
//			} else {
//				return RESCODE.DEVICESNS_INVALID.getJSONRES();
//			}
//			Map<String, Object> map = CMDUtils.downLimitCMD(limit_Install);
//			/*for(Map.Entry<String, Object> m:map.entrySet()) {
//			System.out.println(m.getKey()+" : "+m.getValue());
//			}*/
//			if (!(map.containsValue("成功"))) {
//				return map;
//			}
//			Limit_Install install = limitDao.findLimitByDeviceSnsAndWay(limit_Install.getDevice_sn(),
//					limit_Install.getWay());
//			logger.debug(limit_Install.toString());
//			if (install == null) {
//
//				limitDao.save(limit_Install);
//			} else {
//
//				install.setHigh_limit(limit_Install.getHigh_limit());
//				install.setLow_limit(limit_Install.getLow_limit());
//				install.setUp_limit(limit_Install.getUp_limit());
//			}
//
//			if (timers == null) {
//				if (statusDao.findByDeviceSnAndWay(limit_Install.getDevice_sn(), limit_Install.getWay()).isOn_off()) {
//					CMDUtils.serverOnOffOxygenCMD(limit_Install.getDevice_sn(), limit_Install.getWay(), 0);
//				}
//				timerDao.delete(limit_Install.getDevice_sn(), limit_Install.getWay());
//				return RESCODE.SUCCESS.getJSONRES();
//			} else {
//				AeratorStatus status = statusDao.findByDeviceSnAndWay(limit_Install.getDevice_sn(),
//						limit_Install.getWay());
//
//				if (timers.length > 0) {
//					status.setTimed(true);
//					Timer timer = timers[0];
//					timerDao.delete(timer.getDevice_sn(), timer.getWay());
//					for (Timer timersave : timers) {
//						timerDao.save(timersave);
//					}
//				}
//
//				return RESCODE.SUCCESS.getJSONRES();
//			}
//		} catch (Exception e) {
//			return RESCODE.SEND_FAILED.getJSONRES();
//		}
//	}

	public Map<String, Object> queryAeratorData(String device_sn, int way) {

		String type = null;
		try {
			type = device_sn.substring(0, 2);
		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		if (type.equals("01") || type.equals("02")) {
			AIO aio = aioDao.findAIOByDeviceSns(device_sn);
			if (aio == null) {
				return RESCODE.NOT_FOUND.getJSONRES();
			}
			Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
			Limit_Install limit = limitDao.findLimitByDeviceSnsAndWay(device_sn, way);
			List<Timer> timer = timerDao.findTimerByDeviceSnAndWay(device_sn, way);
			Sensor_Data sensor_data = sensor_DataDao.findDataByDeviceSnAndWay(device_sn, way);
			if (sensor_data != null) {
				map.put("currentOxygens", sensor_data.getOxygen());
			}
			if (limit != null) {
				map.put("oxyHighLimit", limit.getHigh_limit());
				map.put("oxyUpLimit", limit.getUp_limit());
				map.put("oxyLowLimit", limit.getLow_limit());
			}
			if (timer != null) {
				map.put("timerList", timer);
			}
			return map;
		}

		return RESCODE.DEVICESNS_INVALID.getJSONRES();
	}

	public Map<String, Object> queryAlarm(String openId) {
		// TODO Auto-generated method stub
		WXUser wxuser = wxUserDao.findUserByOpenId(openId);

		if (null == wxuser) {
			return RESCODE.ACCOUNT_NOT_EXIST.getJSONRES();
		}
		String relation = wxuser.getRelation();
		if (null == relation) {
			return RESCODE.NO_BIND_RELATION.getJSONRES();
		}
		List<DataAlarm> dalist = daDao.queryDataAlarm(relation);
		if (null == dalist) {
			return RESCODE.NOT_FOUND.getJSONRES();
		}

		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();

		map.put("dataAlarm", dalist);

		return map;
	}

	public Map<String, Object> alarmIsRead(Integer id) {
		DataAlarm da = daDao.findDataAlarmById(id);
		if (null == da) {
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		da.setIsWatch(1);
		// daDao.updateStatus(da);
		return RESCODE.SUCCESS.getJSONRES();
	}

	public Map<String, Object> modifyEquipment(String device_sn, String name) {
		String type = null;
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

		return map;

	}

//	public Map<String, Object> aeratorOnOff(String device_sn, int way, int openOrclose) {
//		AIO aio = aioDao.findAIOByDeviceSns(device_sn);
//		char aeratorStatus = new StringBuffer(aio.getStatus()).charAt(way-1);
//		if (aio != null && aeratorStatus == '3') {
//			String openId = socketService.findWXUserByDeviceSn(device_sn).getOpenId();
//			WechatSendMessageUtils.sendWechatOxyAlarmMessages("打开增氧机失败，因为该增氧机存在缺相报警问题", openId, device_sn);
//			return RESCODE.SUCCESS.getJSONRES();
//		}
//		return CMDUtils.serverOnOffOxygenCMD(device_sn, way, openOrclose);
//
//	}

}
