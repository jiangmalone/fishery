package com.geariot.platform.fishery.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geariot.platform.fishery.dao.AIODao;
import com.geariot.platform.fishery.dao.ControllerDao;
import com.geariot.platform.fishery.dao.LimitDao;
import com.geariot.platform.fishery.dao.SensorDao;
import com.geariot.platform.fishery.dao.Sensor_DataDao;
import com.geariot.platform.fishery.dao.TimerDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.entities.Sensor_Data;
import com.geariot.platform.fishery.entities.Timer;
import com.geariot.platform.fishery.model.ExcelData;
import com.geariot.platform.fishery.model.RESCODE;
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
	
	

	public Map<String, Object> setLimit(Limit_Install limit_Install) {
		String deviceSn;
		try {
			deviceSn = limit_Install.getDevice_sn().substring(0, 2);
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

	public Map<String, Object> setTimer(Timer timer) {
		String deviceSn;
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

		timerDao.updateTimer(timer);
		return RESCODE.SUCCESS.getJSONRES();
	}

	public Map<String, Object> queryEquipment(String device_sn, String relation, String name, int page, int number) {
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

	}

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

	public Map<String, Object> addEquipment(String device_sn ,String name, String relation) {
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
				aio.setRelationId(relation);
			aioDao.save(aio);
			return RESCODE.SUCCESS.getJSONRES();
		} else if (deviceSn.equals("03")) {
			Sensor exist=sensorDao.findSensorByDeviceSns(device_sn);
			if(exist!=null) 
				return RESCODE.SENSOR_EXIST.getJSONRES();
			Sensor sensor=new Sensor();
			sensor.setDevice_sn(device_sn);
			sensor.setName(name);
			sensor.setRelationId(relation);
			sensorDao.save(sensor);
			return RESCODE.SUCCESS.getJSONRES();
		} else if (deviceSn.equals("04")) {
			Controller exist=controllerDao.findControllerByDeviceSns(device_sn);
			if(exist!=null) 
				return RESCODE.SENSOR_EXIST.getJSONRES();
			Controller controller=new Controller();
			controller.setDevice_sn(device_sn);
			controller.setName(name);
			controller.setRelationId(relation);
			controllerDao.save(controller);
			return RESCODE.SUCCESS.getJSONRES();
		} else {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}

	}

	public Map<String, Object> realTimeData(String device_sn) {
		String deviceSn;
		try {
			deviceSn = device_sn.trim().substring(0, 2);
		} catch (Exception e) {
			return RESCODE.DEVICESNS_INVALID.getJSONRES();
		}
		if (deviceSn.equals("01")|| deviceSn.equals("02")) {
			if (aioDao.findAIOByDeviceSns(device_sn) == null) {
				return RESCODE.DEVICESNS_INVALID.getJSONRES();
			}
		} else if (deviceSn.equals("03")) {
			if (sensorDao.findSensorByDeviceSns(device_sn) == null) {
				return RESCODE.DEVICESNS_INVALID.getJSONRES();
			}
		} else 
			return RESCODE.DEVICESNS_INVALID.getJSONRES();

		Sensor_Data data=sensor_DataDao.findDataByDeviceSns(device_sn);
		return RESCODE.SUCCESS.getJSONRES(data);
	}

	public Map<String, Object> dataAll(String device_sn, String startTime, String endTime) {
		// TODO Auto-generated method stub
		return null;
	}

}
