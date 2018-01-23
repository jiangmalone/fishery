package com.geariot.platform.fishery.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geariot.platform.fishery.dao.AIODao;
import com.geariot.platform.fishery.dao.CompanyDao;
import com.geariot.platform.fishery.dao.ControllerDao;
import com.geariot.platform.fishery.dao.LimitDao;
import com.geariot.platform.fishery.dao.PondDao;
import com.geariot.platform.fishery.dao.SensorDao;
import com.geariot.platform.fishery.dao.Sensor_DataDao;
import com.geariot.platform.fishery.dao.TimerDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Company;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.entities.Sensor_Data;
import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.model.Equipment;
import com.geariot.platform.fishery.model.ExcelData;
import com.geariot.platform.fishery.model.Oxygen;
import com.geariot.platform.fishery.model.PH;
import com.geariot.platform.fishery.model.RESCODE;
import com.geariot.platform.fishery.model.Temperature;
import com.geariot.platform.fishery.socket.CMDUtils;
import com.geariot.platform.fishery.socket.TimerTask;
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
	
	private String type = "";
	private String relation = "";
	private Company company = null;
	private AIO aio = null;
	private Sensor sensor = null;
	private Controller controller = null;

	public Map<String, Object> setLimit(Limit_Install limit_Install) {
		String deviceSn;
		try {
			deviceSn = limit_Install.getDevice_sn().substring(0, 2);
			Map<String, Object> map=CMDUtils.downLimitCMD(limit_Install);
			if(!map.containsKey("0"))
				return map;
		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		if (deviceSn.equals("01")|| deviceSn.equals("02")) {
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
		
		return	RESCODE.SUCCESS.getJSONRES();
		
		
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
			} else if (devices.equals("03")) {
				sensorDao.delete(device);
			} else if (devices.equals("04")) {
				controllerDao.delete(device);
			} else {
				return RESCODE.DELETE_ERROR.getJSONRES();
			}
		}
		return RESCODE.SUCCESS.getJSONRES();
	}

	public Map<String, Object> setTimer(Timer[] timerArray) {
		String deviceSn;
		Timer timer=timerArray[0];
		try {
			deviceSn = timer.getDevice_sn().substring(0, 2);
			
		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		if (deviceSn.equals("01")|| deviceSn.equals("02")) {
			if (aioDao.findAIOByDeviceSns(timer.getDevice_sn()) == null) {
				return RESCODE.DEVICESNS_INVALID.getJSONRES();
			}
		} else if (deviceSn.equals("03")) {
			if (sensorDao.findSensorByDeviceSns(timer.getDevice_sn()) == null) {
				return RESCODE.DEVICESNS_INVALID.getJSONRES();
			}
		} else 
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
         timerDao.delete(timer.getDevice_sn());
         for(Timer timersave:timerArray) {
		timerDao.save(timersave);
         }
		return RESCODE.SUCCESS.getJSONRES();
	}

	/*public Map<String, Object> queryEquipment(String device_sn, String relation, String name, int page, int number) {
		String deviceSn;
		try {
			deviceSn = device_sn.trim().substring(0, 2);
		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		if (deviceSn.equals("01") || deviceSn.equals("02")) {
			List<AIO> list=aioDao.queryAIOByNameAndRelation(relation, name, page, number);
			if(list.isEmpty())
				return RESCODE.NOT_FOUND.getJSONRES();
			return RESCODE.SUCCESS.getJSONRES(list);
		} else if (deviceSn.equals("03")) {
			List<Sensor> list=sensorDao.querySensorByNameAndRelation(relation, name, page, number);
			if(list.isEmpty())
				return RESCODE.NOT_FOUND.getJSONRES();
			return RESCODE.SUCCESS.getJSONRES(list);
		} else if (deviceSn.equals("04")) {
			List<Controller> list=controllerDao.queryControllerByNameAndRelation(relation, name, page, number);
			if(list.isEmpty())
				return RESCODE.NOT_FOUND.getJSONRES();
			return RESCODE.SUCCESS.getJSONRES(list);
		} else {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}

	}*/

	public boolean exportData(String device_sn, String startTime, String endTime,HttpServletResponse response) {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			List<ExcelData> list = sensor_DataDao.getExcelData(device_sn,sdf.parse(startTime),sdf.parse(endTime));
			String[] fields = { "Id", "device_sn","oxygen","ph","receiveTime","waterTemperature" };
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

	public Map<String, Object> addEquipment(String device_sn ,String name, String relationId) {
		String deviceSn;
		try {
			deviceSn = device_sn.trim().substring(0, 2);
		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		if (deviceSn.equals("01") || deviceSn.equals("02")) {
			AIO exist=aioDao.findAIOByDeviceSns(device_sn);
			if(exist!=null) 
				return RESCODE.AIO_EXIST.getJSONRES();
				AIO aio=new AIO();
				aio.setDevice_sn(device_sn);
				aio.setName(name);
				aio.setRelationId(relationId);
			aioDao.save(aio);
			return RESCODE.SUCCESS.getJSONRES();
		} else if (deviceSn.equals("03")) {
			Sensor exist=sensorDao.findSensorByDeviceSns(device_sn);
			if(exist!=null) 
				return RESCODE.SENSOR_EXIST.getJSONRES();
			Sensor sensor=new Sensor();
			sensor.setDevice_sn(device_sn);
			sensor.setName(name);
			sensor.setRelationId(relationId);
			sensor.setPort_status("00");
			sensorDao.save(sensor);
			return RESCODE.SUCCESS.getJSONRES();
		} else if (deviceSn.equals("04")) {
			Controller exist=controllerDao.findControllerByDeviceSns(device_sn);
			if(exist!=null) 
				return RESCODE.SENSOR_EXIST.getJSONRES();
			Controller controller=new Controller();
			controller.setDevice_sn(device_sn);
			controller.setName(name);
			controller.setRelationId(relationId);
			controller.setPort_status("0000");
			controllerDao.save(controller);
			return RESCODE.SUCCESS.getJSONRES();
		} else {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}

	}

	public Map<String, Object> realTimeData(String device_sn) {
		String deviceSn;
		Sensor_Data data = null;
		Map<String,Object> map = null;
		try {
			deviceSn = device_sn.trim().substring(0, 2);
		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		if (deviceSn.equals("01")|| deviceSn.equals("02")) {
			if (aioDao.findAIOByDeviceSns(device_sn) == null) {
				return RESCODE.DEVICESNS_INVALID.getJSONRES();
			}
			data = sensor_DataDao.findDataByDeviceSns(device_sn);
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
	
	public Map<String, Object> myEquipment(String relationId){
		List<Sensor> sensors = sensorDao.querySensorByNameAndRelation(relationId, null);
		List<AIO> aios = aioDao.queryAIOByNameAndRelation(relationId, null);
		List<Controller> controllers = controllerDao.queryControllerByNameAndRelation(relationId, null);
		Map<String, Object> result = RESCODE.SUCCESS.getJSONRES();
		result.put("sensor", sensors);
		result.put("controller", controllers);
		result.put("aio", aios);
		return result;
	}

	public Map<String, Object> adminFindEquipment(String device_sn, String companyName, int page, int number) {
		int from = (page - 1) * number;
		if((device_sn == null || device_sn.length() < 0) && 
				(companyName == null || companyName.length() < 0)){
			return noConditionsQuery(from, number);
		}
		if((device_sn == null || device_sn.length() < 0) && 
				(companyName != null && !companyName.isEmpty() && !companyName.trim().isEmpty())){
			return companyConditionQuery(companyName, from, number);
		}
		if((device_sn != null && !device_sn.isEmpty() && !device_sn.trim().isEmpty()) &&
				(companyName == null || companyName.length() <0)){
			return deviceSnConditionQuery(device_sn, from, number);
		}
		return doubleConditionQuery(device_sn, companyName, from, number);
	}

	private Map<String, Object> doubleConditionQuery(String device_sn, String companyName, int from, int number) {
		List<Company> companies = companyDao.companies(companyName);
		List<Equipment> equipments = pondDao.adminFindEquipmentDouble(device_sn, companies, from, number);
		shareDealMethod(equipments);
		long count = pondDao.adminFindEquipmentCountDouble(device_sn, companies);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
	}

	private Map<String, Object> deviceSnConditionQuery(String device_sn, int from, int number) {
		List<Equipment> equipments = pondDao.adminFindEquipmentBySn(device_sn);
		shareDealMethod(equipments);
		long count = pondDao.adminFindEquipmentCountSn(device_sn);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
	}

	private Map<String, Object> companyConditionQuery(String companyName, int from, int number) {
		List<Company> companies = companyDao.companies(companyName);
		List<Equipment> equipments = pondDao.adminFindEquipmentByCo(companies, from, number);
		shareDealMethod(equipments);
		long count = pondDao.adminFindEquipmentCountCo(companies);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
	}

	private List<Equipment> shareDealMethod(List<Equipment> equipments){
		for(Equipment equipment : equipments){
			type = equipment.getDevice_sn().substring(0, 2);
			switch(type){
				case "01" : 
					aio = aioDao.findAIOByDeviceSns(equipment.getDevice_sn());
					relation = aio.getRelationId();
					if(relation != null && relation.contains("CO")){
						company = companyDao.findCompanyByRelationId(relation);
						equipment.setCompanyName(company.getName());
						equipment.setCompanyId(company.getId());
					}else{
						equipment.setCompanyName("");
						equipment.setCompanyId(0);
					}
					break;
				case "02" : 
					aio = aioDao.findAIOByDeviceSns(equipment.getDevice_sn());
					relation = aio.getRelationId();
					if(relation != null && relation.contains("CO")){
						company = companyDao.findCompanyByRelationId(relation);
						equipment.setCompanyName(company.getName());
						equipment.setCompanyId(company.getId());
					}else{
						equipment.setCompanyName("");
						equipment.setCompanyId(0);
					}
					break;
				case "03" : 
					sensor = sensorDao.findSensorByDeviceSns(equipment.getDevice_sn());
					relation = sensor.getRelationId();
					if(relation != null && relation.contains("CO")){
						company = companyDao.findCompanyByRelationId(relation);
						equipment.setCompanyName(company.getName());
						equipment.setCompanyId(company.getId());
					}else{
						equipment.setCompanyName("");
						equipment.setCompanyId(0);
					}
					break;
				case "04" : 
					controller = controllerDao.findControllerByDeviceSns(equipment.getDevice_sn());
					relation = controller.getRelationId();
					if(relation != null && relation.contains("CO")){
						company = companyDao.findCompanyByRelationId(relation);
						equipment.setCompanyName(company.getName());
						equipment.setCompanyId(company.getId());
					}else{
						equipment.setCompanyName("");
						equipment.setCompanyId(0);
					}
					break;
				default : break;
			}
		}
		return equipments;
	}
	
	private Map<String, Object> noConditionsQuery(int from,  int number){
		List<Equipment> equipments = pondDao.adminFindEquipmentAll(from, number);
		shareDealMethod(equipments);
		long count = pondDao.adminFindEquipmentCountAll();
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
	}

	public Map<String, Object> companyFindEquipment(String device_sn, String relationId, int page, int number) {
		int from = (page - 1) * number;
		Company company = companyDao.findCompanyByRelationId(relationId);
		if(company == null){
			return RESCODE.NOT_FOUND.getJSONRES();
		}else{
			List<Company> companies = new ArrayList<>();
			companies.add(company);
			if(device_sn == null || device_sn.length()<0){
				List<Equipment> equipments = pondDao.adminFindEquipmentByCo(companies, from, number);
				long count = pondDao.adminFindEquipmentCountCo(companies);
				int size = (int) Math.ceil(count / (double) number);
				return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
			}else{
				List<Equipment> equipments = pondDao.adminFindEquipmentDouble(device_sn, companies, from, number);
				long count = pondDao.adminFindEquipmentCountDouble(device_sn, companies);
				int size = (int) Math.ceil(count / (double) number);
				return RESCODE.SUCCESS.getJSONRES(equipments, size, count);
			}
		}
	}

	public Map<String, Object> dataToday(String device_sn) {
		List<Sensor_Data> list = sensor_DataDao.today(device_sn);
		List<PH> phs = new ArrayList<>();
		List<Oxygen> oxygens = new ArrayList<>();
		List<Temperature> temperatures = new ArrayList<>();
		PH ph = null;
		Oxygen oxygen = null;
		Temperature temperature = null;
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		for(Sensor_Data sensor_Data : list){
			ph = new PH(sensor_Data.getpH_value(), format.format(sensor_Data.getReceiveTime()));
			oxygen = new Oxygen(sensor_Data.getOxygen(), format.format(sensor_Data.getReceiveTime()));
			temperature = new Temperature(sensor_Data.getWater_temperature(), format.format(sensor_Data.getReceiveTime()));
			phs.add(ph);
			oxygens.add(oxygen);
			temperatures.add(temperature);
		}
		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
		map.put("phs", phs);
		map.put("oxygens",oxygens);
		map.put("temperatures", temperatures);
		return map;//
	}

	public Map<String, Object> dataAll(String device_sn) {
		List<Sensor_Data> list = sensor_DataDao.sevenData(device_sn);
		List<PH> phs = new ArrayList<>();
		List<Oxygen> oxygens = new ArrayList<>();
		List<Temperature> temperatures = new ArrayList<>();
		PH ph = null;
		Oxygen oxygen = null;
		Temperature temperature = null;
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		for(Sensor_Data sensor_Data : list){
			ph = new PH(sensor_Data.getpH_value(), format.format(sensor_Data.getReceiveTime()));
			oxygen = new Oxygen(sensor_Data.getOxygen(), format.format(sensor_Data.getReceiveTime()));
			temperature = new Temperature(sensor_Data.getWater_temperature(), format.format(sensor_Data.getReceiveTime()));
			phs.add(ph);
			oxygens.add(oxygen);
			temperatures.add(temperature);
		}
		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
		map.put("phs", phs);
		map.put("oxygens",oxygens);
		map.put("temperatures", temperatures);
		return map;//
	}

	public Map<String, Object> pcDataToday(String device_sn) {
		List<Sensor_Data> list = sensor_DataDao.today(device_sn);
		List<PH> phs = new ArrayList<>();
		List<Oxygen> oxygens = new ArrayList<>();
		List<Temperature> temperatures = new ArrayList<>();
		PH ph = null;
		Oxygen oxygen = null;
		Temperature temperature = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(Sensor_Data sensor_Data : list){
			ph = new PH(sensor_Data.getpH_value(), format.format(sensor_Data.getReceiveTime()));
			oxygen = new Oxygen(sensor_Data.getOxygen(), format.format(sensor_Data.getReceiveTime()));
			temperature = new Temperature(sensor_Data.getWater_temperature(), format.format(sensor_Data.getReceiveTime()));
			phs.add(ph);
			oxygens.add(oxygen);
			temperatures.add(temperature);
		}
		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
		map.put("phs", phs);
		map.put("oxygens",oxygens);
		map.put("temperatures", temperatures);
		return map;//
	}

	public Map<String, Object> pcDataAll(String device_sn) {
		List<Sensor_Data> list = sensor_DataDao.sevenData(device_sn);
		List<PH> phs = new ArrayList<>();
		List<Oxygen> oxygens = new ArrayList<>();
		List<Temperature> temperatures = new ArrayList<>();
		PH ph = null;
		Oxygen oxygen = null;
		Temperature temperature = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(Sensor_Data sensor_Data : list){
			ph = new PH(sensor_Data.getpH_value(), format.format(sensor_Data.getReceiveTime()));
			oxygen = new Oxygen(sensor_Data.getOxygen(), format.format(sensor_Data.getReceiveTime()));
			temperature = new Temperature(sensor_Data.getWater_temperature(), format.format(sensor_Data.getReceiveTime()));
			phs.add(ph);
			oxygens.add(oxygen);
			temperatures.add(temperature);
		}
		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
		map.put("phs", phs);
		map.put("oxygens",oxygens);
		map.put("temperatures", temperatures);
		return map;//
	}
}
