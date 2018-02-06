package com.geariot.platform.fishery.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geariot.platform.fishery.dao.AIODao;
import com.geariot.platform.fishery.dao.AeratorStatusDao;
import com.geariot.platform.fishery.dao.CompanyDao;
import com.geariot.platform.fishery.dao.ControllerDao;
import com.geariot.platform.fishery.dao.DataAlarmDao;
import com.geariot.platform.fishery.dao.LimitDao;
import com.geariot.platform.fishery.dao.PondDao;
import com.geariot.platform.fishery.dao.SensorDao;
import com.geariot.platform.fishery.dao.Sensor_ControllerDao;
import com.geariot.platform.fishery.dao.Sensor_DataDao;
import com.geariot.platform.fishery.dao.TimerDao;
import com.geariot.platform.fishery.dao.WXUserDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.AeratorStatus;
import com.geariot.platform.fishery.entities.Company;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.DataAlarm;
import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.entities.Sensor_Controller;
import com.geariot.platform.fishery.entities.Sensor_Data;
import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.entities.WXUser;
import com.geariot.platform.fishery.model.Equipment;
import com.geariot.platform.fishery.model.ExcelData;
import com.geariot.platform.fishery.model.Oxygen;
import com.geariot.platform.fishery.model.PH;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.model.Temperature;
import com.geariot.platform.fishery.socket.CMDUtils;
import com.geariot.platform.fishery.utils.DataExportExcel;

@Service
@Transactional
public class EquipmentService {

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
	


	private String type = "";
	private String relation = "";
	private Company company = null;
	private AIO aio = null;
	private Sensor sensor = null;
	private Controller controller = null;
	private WXUser wxUser = null;

	public Map<String, Object> setLimit(Limit_Install limit_Install) {
		String deviceSn;
		try {
			deviceSn = limit_Install.getDevice_sn().substring(0, 2);
			Map<String, Object> map = CMDUtils.downLimitCMD(limit_Install);
			if (!map.containsKey("0"))
				return map;
		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		if (deviceSn.equals("01") || deviceSn.equals("02")) {
			if (aioDao.findAIOByDeviceSns(limit_Install.getDevice_sn()) == null) {
				return RESCODE.DEVICESNS_INVALID.getJSONRES();
			}
		} else if (deviceSn.equals("03")) {
			if (sensorDao.findSensorByDeviceSns(limit_Install.getDevice_sn()) == null) {
				return RESCODE.DEVICESNS_INVALID.getJSONRES();
			}
		} else
			return RESCODE.DEVICESNS_INVALID.getJSONRES();

		limitDao.updateLimit(limit_Install);

		return RESCODE.SUCCESS.getJSONRES();

	}

	public Map<String, Object> delEquipment(String[] device_sns) {
		for (String device : device_sns) {

			String devices;
			try {
				devices = device.trim().substring(0, 2);
			} catch (Exception e) {
				return RESCODE.DEVICESNS_INVALID.getJSONRES();
			}
			if (devices.equals("01") || devices.equals("02")) {
				aioDao.delete(device);
				statusDao.delete(device);
			} else if (devices.equals("03")) {
				int sensorId = sensorDao.findSensorByDeviceSns(device).getId();
				sensorDao.delete(device);
				List<Sensor_Controller> list = sensor_ControllerDao.list(sensorId);
				for (Sensor_Controller sensor_Controller : list) {
					controller = controllerDao.findControllerById(sensor_Controller.getControllerId());
					if (controller == null) {
						continue;
					} else {
						changeControllerPortStatusClose(controller, sensor_Controller.getController_port());
					}
				}
				sensor_ControllerDao.delete(sensorId);
			} else if (devices.equals("04")) {
				int controllerId = controllerDao.findControllerByDeviceSns(device).getId();
				controllerDao.delete(device);
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

	private void changeControllerPortStatusOn(Controller controller, int port) {
		StringBuffer sb = new StringBuffer(controller.getPort_status());
		sb.setCharAt(port - 1, '1');
		controller.setPort_status(sb.toString());
	}

	private void changeControllerPortStatusClose(Controller controller, int port) {
		StringBuffer sb = new StringBuffer(controller.getPort_status());
		sb.setCharAt(port - 1, '0');
		controller.setPort_status(sb.toString());
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

	public Map<String, Object> addEquipment(String device_sn, String name, String relation) {
		String deviceSn;
		try {
			deviceSn = device_sn.trim().substring(0, 2);
		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		if (deviceSn.equals("01") || deviceSn.equals("02")) {
			AIO exist = aioDao.findAIOByDeviceSns(device_sn);
			if (exist != null) {
				return RESCODE.AIO_EXIST.getJSONRES();
			} else {
				AIO aio = new AIO();
				aio.setDevice_sn(device_sn);
				aio.setName(name);
				aio.setRelation(relation);
				aio.setStatus(1);
				aio.setType(Integer.parseInt(deviceSn));
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
		} else if (deviceSn.equals("03")) {
			Sensor exist = sensorDao.findSensorByDeviceSns(device_sn);
			if (exist != null) {
				return RESCODE.SENSOR_EXIST.getJSONRES();
			} else {
				Sensor sensor = new Sensor();
				sensor.setDevice_sn(device_sn);
				sensor.setName(name);
				sensor.setRelation(relation);
				sensor.setStatus(1);
				sensor.setPort_status("00");
				sensorDao.save(sensor);
				return RESCODE.SUCCESS.getJSONRES();
			}
		} else if (deviceSn.equals("04")) {
			Controller exist = controllerDao.findControllerByDeviceSns(device_sn);
			if (exist != null) {
				return RESCODE.SENSOR_EXIST.getJSONRES();
			} else {
				Controller controller = new Controller();
				controller.setDevice_sn(device_sn);
				controller.setName(name);
				controller.setRelation(relation);
				controller.setStatus(1);
				controller.setPort_status("0000");
				controllerDao.save(controller);
				return RESCODE.SUCCESS.getJSONRES();
			}
		} else {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}

	}

	public Map<String, Object> realTimeData(String device_sn, int way) {
		String deviceSn;
		Sensor_Data data = null;
		Map<String, Object> map = null;
		try {
			deviceSn = device_sn.trim().substring(0, 2);
		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		if (deviceSn.equals("01") || deviceSn.equals("02")) {
			if (aioDao.findAIOByDeviceSns(device_sn) == null) {
				return RESCODE.DEVICESNS_INVALID.getJSONRES();
			}
			data = sensor_DataDao.findDataByDeviceSnAndWay(device_sn, way);
			AIO aio = aioDao.findAIOByDeviceSns(device_sn);
			map = RESCODE.SUCCESS.getJSONRES(data);
			map.put("status", aio.getStatus());
			map.put("name", aio.getName());
			return map;
		} else if (deviceSn.equals("03")) {
			if (sensorDao.findSensorByDeviceSns(device_sn) == null) {
				return RESCODE.DEVICESNS_INVALID.getJSONRES();
			}
			data = sensor_DataDao.findDataByDeviceSns(device_sn);
			Sensor sensor = sensorDao.findSensorByDeviceSns(device_sn);
			map = RESCODE.SUCCESS.getJSONRES(data);
			map.put("status", sensor.getStatus());
			map.put("name", sensor.getName());
			return map;
		} else
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
	}

	public Map<String, Object> myEquipment(String relation) {
		List<Sensor> sensors = sensorDao.querySensorByNameAndRelation(relation, null);
		List<AIO> aios = aioDao.queryAIOByNameAndRelation(relation, null);
		List<Controller> controllers = controllerDao.queryControllerByNameAndRelation(relation, null);
		Map<String, Object> result = RESCODE.SUCCESS.getJSONRES();
		result.put("sensor", sensors);
		result.put("controller", controllers);
		result.put("aio", aios);
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
				return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
			} else {
				List<Equipment> equipments = pondDao.adminFindEquipmentDouble(device_sn, relations, from, number);
				long count = pondDao.adminFindEquipmentCountDouble(device_sn, relations);
				int size = (int) Math.ceil(count / (double) number);
				return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
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
		List<PH> phs = new ArrayList<>();
		List<Oxygen> oxygens = new ArrayList<>();
		List<Temperature> temperatures = new ArrayList<>();
		PH ph = null;
		Oxygen oxygen = null;
		Temperature temperature = null;
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		for (Sensor_Data sensor_Data : list) {
			ph = new PH(sensor_Data.getpH_value(), format.format(sensor_Data.getReceiveTime()));
			oxygen = new Oxygen(sensor_Data.getOxygen(), format.format(sensor_Data.getReceiveTime()));
			temperature = new Temperature(sensor_Data.getWater_temperature(),
					format.format(sensor_Data.getReceiveTime()));
			phs.add(ph);
			oxygens.add(oxygen);
			temperatures.add(temperature);
		}
		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
		map.put("phs", phs);
		map.put("oxygens", oxygens);
		map.put("temperatures", temperatures);
		return map;//
	}

	public Map<String, Object> dataAll(String device_sn, int way) {
		List<Sensor_Data> list = new ArrayList<>();
		if (way > 0) {
			list = sensor_DataDao.sevenData(device_sn, way);
		} else {
			list = sensor_DataDao.sevenData(device_sn);
		}
		
		List<Sensor_Data> splitlist=new ArrayList<>();
		int i=0;
		while(i<2016) {
			try {
				splitlist.add(list.get(i));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				break;
			}
			
			i=i+14;
		}
		List<PH> phs = new ArrayList<>();
		List<Oxygen> oxygens = new ArrayList<>();
		List<Temperature> temperatures = new ArrayList<>();
		PH ph = null;
		Oxygen oxygen = null;
		Temperature temperature = null;
		SimpleDateFormat format = new SimpleDateFormat("MM-dd");
		for (Sensor_Data sensor_Data : splitlist) {
			ph = new PH(sensor_Data.getpH_value(), format.format(sensor_Data.getReceiveTime()));
			oxygen = new Oxygen(sensor_Data.getOxygen(), format.format(sensor_Data.getReceiveTime()));
			temperature = new Temperature(sensor_Data.getWater_temperature(),
					format.format(sensor_Data.getReceiveTime()));
			phs.add(ph);
			oxygens.add(oxygen);
			temperatures.add(temperature);
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
		List<PH> phs = new ArrayList<>();
		List<Oxygen> oxygens = new ArrayList<>();
		List<Temperature> temperatures = new ArrayList<>();
		PH ph = null;
		Oxygen oxygen = null;
		Temperature temperature = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Sensor_Data sensor_Data : list) {
			ph = new PH(sensor_Data.getpH_value(), format.format(sensor_Data.getReceiveTime()));
			oxygen = new Oxygen(sensor_Data.getOxygen(), format.format(sensor_Data.getReceiveTime()));
			temperature = new Temperature(sensor_Data.getWater_temperature(),
					format.format(sensor_Data.getReceiveTime()));
			phs.add(ph);
			oxygens.add(oxygen);
			temperatures.add(temperature);
		}
		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
		map.put("phs", phs);
		map.put("oxygens", oxygens);
		map.put("temperatures", temperatures);
		return map;//
	}

	public Map<String, Object> pcDataAll(String device_sn, int way) {
		List<Sensor_Data> list = new ArrayList<>();
		if (way > 0) {
			list = sensor_DataDao.sevenData(device_sn, way);
		} else {
			list = sensor_DataDao.sevenData(device_sn);
		}
		
		List<Sensor_Data> splitlist=new ArrayList<>();
		int i=0;
		while(i<2016) {
			try {
				splitlist.add(list.get(i));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				break;
			}
			i=i+14;
		}
		
		List<PH> phs = new ArrayList<>();
		List<Oxygen> oxygens = new ArrayList<>();
		List<Temperature> temperatures = new ArrayList<>();
		PH ph = null;
		Oxygen oxygen = null;
		Temperature temperature = null;
		SimpleDateFormat format = new SimpleDateFormat("MM-dd");
		for (Sensor_Data sensor_Data : splitlist) {
			ph = new PH(sensor_Data.getpH_value(), format.format(sensor_Data.getReceiveTime()));
			oxygen = new Oxygen(sensor_Data.getOxygen(), format.format(sensor_Data.getReceiveTime()));
			temperature = new Temperature(sensor_Data.getWater_temperature(),
					format.format(sensor_Data.getReceiveTime()));
			phs.add(ph);
			oxygens.add(oxygen);
			temperatures.add(temperature);
		}
	  
		
		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
		map.put("phs", phs);
		map.put("oxygens", oxygens);
		map.put("temperatures", temperatures);
		return map;//
	}

	public Map<String, Object> autoSet(Limit_Install limit_Install, Timer[] timers) {
		String deviceSn;
		try {
			deviceSn = limit_Install.getDevice_sn().substring(0, 2);
			if (timers == null) {
				if (statusDao.findByDeviceSnAndWay(limit_Install.getDevice_sn(), limit_Install.getWay()).isOn_off()) {
					CMDUtils.serverOnOffOxygenCMD(limit_Install.getDevice_sn(), limit_Install.getWay(), 0);
				}

				timerDao.delete(limit_Install.getDevice_sn(), limit_Install.getWay());

				return RESCODE.SUCCESS.getJSONRES();
			}
			Map<String, Object> map = CMDUtils.downLimitCMD(limit_Install);
			if (!map.containsKey("0"))
				return map;

		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		if (deviceSn.equals("01") || deviceSn.equals("02")) {
			if (aioDao.findAIOByDeviceSns(limit_Install.getDevice_sn()) == null) {
				return RESCODE.DEVICESNS_INVALID.getJSONRES();
			}
		} else if (deviceSn.equals("03")) {
			if (sensorDao.findSensorByDeviceSns(limit_Install.getDevice_sn()) == null) {
				return RESCODE.DEVICESNS_INVALID.getJSONRES();
			}
		} else
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		limitDao.updateLimit(limit_Install);
		AeratorStatus status = statusDao.findByDeviceSnAndWay(limit_Install.getDevice_sn(), limit_Install.getWay());
		Timer timer = timers[0];
		if (timers.length > 0) {
			status.setTimed(true);
		}
		timerDao.delete(timer.getDevice_sn(), timer.getWay());
		for (Timer timersave : timers) {
			timerDao.save(timersave);
		}
		return RESCODE.SUCCESS.getJSONRES();
	}

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
		WXUser wxuser=wxUserDao.findUserByOpenId(openId);
		
		if(null==wxuser) {
			return RESCODE.ACCOUNT_NOT_EXIST.getJSONRES();
		}
		String relation=wxuser.getRelation();
		if(null==relation) {
			return RESCODE.NO_BIND_RELATION.getJSONRES();
		}
		List<DataAlarm> dalist=daDao.queryDataAlarm(relation);
		if(null==dalist) {
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		
		
		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
		
		
		map.put("dataAlarm", dalist);
		
		return map;
	}

	public Map<String, Object> alarmIsRead(Integer id) {
		DataAlarm da=daDao.findDataAlarmById(id);
		if(null==da) {
		return RESCODE.NOT_FOUND.getJSONRES();
		}
		da.setIsWatch(1);
		//daDao.updateStatus(da);
		return RESCODE.SUCCESS.getJSONRES();
	}

	public Map<String, Object> modifyEquipment(String device_sn,String name) {
		String type=null;
		Map<String, Object> map=null;
		try {
			type=device_sn.substring(0, 2);
		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		if(type.equals("01")||type.equals("02")) {
			AIO aio=aioDao.findAIOByDeviceSns(device_sn);
			aio.setName(name);
			map=RESCODE.SUCCESS.getJSONRES();
			map.put("aio", aio);
		}else if(type.equals("03")) {
			Sensor sensor=sensorDao.findSensorByDeviceSns(device_sn);
			sensor.setName(name);
			map=RESCODE.SUCCESS.getJSONRES();
			map.put("sensor", sensor);
		}else if(type.equals("04")) {
			Controller controller=controllerDao.findControllerByDeviceSns(device_sn);
			controller.setName(name);
			map=RESCODE.SUCCESS.getJSONRES();
			map.put("controller", controller);
		}
		
	    return map;
		
	}

}
