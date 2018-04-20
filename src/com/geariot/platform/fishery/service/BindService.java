/**
 * 
 */
package com.geariot.platform.fishery.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geariot.platform.fishery.dao.AIODao;
import com.geariot.platform.fishery.dao.ControllerDao;
import com.geariot.platform.fishery.dao.PondDao;
import com.geariot.platform.fishery.dao.SensorDao;
import com.geariot.platform.fishery.dao.Sensor_ControllerDao;
import com.geariot.platform.fishery.entities.AIO;
import com.geariot.platform.fishery.entities.Controller;
import com.geariot.platform.fishery.entities.Pond;
import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.entities.Sensor_Controller;
import com.geariot.platform.fishery.model.BindState;
import com.geariot.platform.fishery.model.PortBind;
import com.geariot.platform.fishery.model.RESCODE;

/**
 * @author mxy940127
 *
 */
@Service
@Transactional
public class BindService {

	private Logger logger = LogManager.getLogger(BindService.class);

	@Autowired
	private PondDao pondDao;

	@Autowired
	private SensorDao sensorDao;

	@Autowired
	private AIODao aioDao;

	@Autowired
	private ControllerDao controllerDao;

	@Autowired
	private Sensor_ControllerDao sensor_ControllerDao;

	public Map<String, Object> bindPondWithSensor(String device_sn, int pondId) {
		logger.debug("塘口Id:" + pondId + "尝试与传感器设备,设备编号为:" + device_sn + "进行绑定...");
		Pond pond = pondDao.findPondByPondId(pondId);
		if (pond == null) {
			logger.debug("塘口Id:" + pondId + "在数据库中无记录!!!");
			return RESCODE.NOT_FOUND.getJSONRES();
		} else {
			Sensor sensor = sensorDao.findSensorByDeviceSns(device_sn);
			if (sensor == null) {
				logger.debug("传感器设备,设备编号:" + device_sn + "在数据库中无记录!!!");
				return RESCODE.NOT_FOUND.getJSONRES();
			} else {
				if (sensor.getPondId() != 0) {
					return RESCODE.EQUIPMENT_ALREADY_BIND_WITH_ONE_POND.getJSONRES();
				}
				sensor.setPondId(pondId);
				logger.debug("塘口Id:" + pondId + "与传感器设备,设备编号:" + device_sn + "绑定成功。。。");
				return RESCODE.SUCCESS.getJSONRES();
			}
		}
	}

	public Map<String, Object> bindPondWithAIO(String device_sn, int pondId) {
		logger.debug("塘口Id:" + pondId + "尝试与一体机设备,设备编号为:" + device_sn + "进行绑定...");
		Pond pond = pondDao.findPondByPondId(pondId);
		if (pond == null) {
			logger.debug("塘口Id:" + pondId + "在数据库中无记录!!!");
			return RESCODE.NOT_FOUND.getJSONRES();
		} else {
			AIO aio = aioDao.findAIOByDeviceSns(device_sn);
			if (aio == null) {
				logger.debug("一体机设备,设备编号:" + device_sn + "在数据库中无记录!!!");
				return RESCODE.NOT_FOUND.getJSONRES();
			} else {
				if (aio.getPondId() != 0) {
					return RESCODE.EQUIPMENT_ALREADY_BIND_WITH_ONE_POND.getJSONRES();
				}
				aio.setPondId(pondId);
				logger.debug("塘口Id:" + pondId + "与一体机设备,设备编号为:" + device_sn + "绑定成功。。。");
				return RESCODE.SUCCESS.getJSONRES();
			}
		}
	}

	public Map<String, Object> delPondWithSensorBind(String device_sn) {
		Controller controller = null;
		logger.debug("传感器设备,设备编号:" + device_sn + "尝试与相关塘口解除绑定...");
		Sensor sensor = sensorDao.findSensorByDeviceSns(device_sn);
		if (sensor == null) {
			logger.debug("传感器设备,设备编号:" + device_sn + "在数据库中无记录!!!");
			return RESCODE.NOT_FOUND.getJSONRES();
		} else {
			if (sensor.getPondId() == 0) {
				logger.debug("传感器设备,设备编号:" + device_sn + "并未与任何塘口有绑定");
				return RESCODE.NOT_BINDED.getJSONRES();
			} else {
				logger.debug("传感器设备,设备编号:" + device_sn + "与塘口,Id:" + sensor.getPondId() + "有绑定");
				sensor.setPondId(0);
				sensor.setPort_status("00");
				List<Sensor_Controller> sensor_Controllers = sensor_ControllerDao.list(sensor.getId());
				for(Sensor_Controller sensor_Controller : sensor_Controllers){
					controller = controllerDao.findControllerById(sensor_Controller.getControllerId());
					if(controller == null){
						continue;
					}else{
						changeControllerPortStatusClose(controller, sensor_Controller.getController_port());
					}
				}
				int count = sensor_ControllerDao.delete(sensor.getId());
				logger.debug("传感器设备,设备编号:" + device_sn + "已和塘口解除绑定,数据库中删除传感器与控制器绑定关系共" + count + "条。。。");
				return RESCODE.SUCCESS.getJSONRES();
			}
		}
	}

	public Map<String, Object> delPondWithAIOBind(String device_sn) {
		logger.debug("一体机设备,设备编号:" + device_sn + "尝试与相关塘口解除绑定...");
		AIO aio = aioDao.findAIOByDeviceSns(device_sn);
		if (aio == null) {
			logger.debug("一体机设备,设备编号:" + device_sn + "在数据库中无记录!!!");
			return RESCODE.NOT_FOUND.getJSONRES();
		} else {
			if (aio.getPondId() == 0) {
				logger.debug("一体机设备,设备编号:" + device_sn + "并未与任何塘口有绑定");
				return RESCODE.NOT_BINDED.getJSONRES();
			} else {
				logger.debug("一体机设备,设备编号:" + device_sn + "与塘口,Id:" + aio.getPondId() + "有绑定");
				aio.setPondId(0);
				logger.debug("一体机设备,设备编号:" + device_sn + "已和塘口解除绑定。。。");
				return RESCODE.SUCCESS.getJSONRES();
			}
		}
	}

	public Map<String, Object> delSensorControllerBind(int sensorId, int sensor_port) {
		Sensor sensor = sensorDao.findSensorById(sensorId);
		if (sensor == null) {
			return RESCODE.NOT_FOUND.getJSONRES();
		} else {
			Sensor_Controller sensor_Controller = sensor_ControllerDao.findBySensorIdAndPort(sensorId, sensor_port);
			if (sensor_Controller == null) {
				return RESCODE.NO_BIND_RELATION.getJSONRES();
			} else {
				int controllerId = sensor_Controller.getControllerId();
				int controllerPort = sensor_Controller.getController_port();
				sensor_ControllerDao.deleteRecord(sensor_Controller.getId());
				changeSensorPortStatusClose(sensor, sensor_port);
				Controller controller = controllerDao.findControllerById(controllerId);
				if (controller == null) {
					return RESCODE.NOT_FOUND.getJSONRES();
				} else {
					changeControllerPortStatusClose(controller, controllerPort);
				}
				return RESCODE.SUCCESS.getJSONRES();
			}
		}
	}

	public Map<String, Object> bindSensorController(int sensorId, int sensor_port, int controllerId,
			int controller_port) {
		logger.debug("传感器Id:" + sensorId + "尝试与控制器设备,Id为:" + controllerId + "进行绑定...");
		Sensor sensor = sensorDao.findSensorById(sensorId);
		if (sensor == null) {
			logger.debug("传感器Id:" + sensorId + "不存在");
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		Controller controller = controllerDao.findControllerById(controllerId);
		if (controller == null) {
			logger.debug("控制器Id:" + controllerId + "不存在");
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		Sensor_Controller sensorRecord = sensor_ControllerDao.findBySensorIdAndPort(sensorId, sensor_port);
		if (sensorRecord != null) {
			return RESCODE.ALREADY_BIND_SENSOR_WITH_CONTROLLER.getJSONRES();
		}
		Sensor_Controller record = sensor_ControllerDao.findByControllerIdAndPort(controllerId, controller_port);
		if (record != null) {
			return RESCODE.ALREADY_BIND_SENSOR_WITH_CONTROLLER.getJSONRES();
		}
		Sensor_Controller sensor_Controller = new Sensor_Controller();
		sensor_Controller.setSensorId(sensorId);
		sensor_Controller.setSensor_port(sensor_port);
		sensor_Controller.setControllerId(controllerId);
		sensor_Controller.setController_port(controller_port);
		sensor_ControllerDao.save(sensor_Controller);
		logger.debug("绑定记录Id:" + sensor_Controller.getId() + "绑定状态:((传感器Id及端口:" + sensorId + "、" + sensor_port
				+ ")&&(控制器Id及端口:" + controllerId + "、" + controller_port + "))...");
		changeSensorPortStatusOn(sensor, sensor_port);
		changeControllerPortStatusOn(controller, controller_port);
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

	public Map<String, Object> bindState(String device_sn) {
		if (device_sn == null || device_sn.isEmpty() || device_sn.length() < 2) {
			return RESCODE.WRONG_PARAM.getJSONRES();
		} else {
			String type = device_sn.substring(0, 2);
			switch (type) {
			case "01":
				AIO aio = aioDao.findAIOByDeviceSns(device_sn);
				if (aio == null) {
					return RESCODE.NOT_FOUND.getJSONRES();
				} else {
					BindState bindState = new BindState();
					bindState.setDeviceName(aio.getName());
					bindState.setStatus(aio.getStatus());
					if (aio.getPondId() > 0) {
						bindState.setPondId(aio.getPondId());
						bindState.setPondName(pondDao.findPondByPondId(aio.getPondId()).getName());
					} else {
						bindState.setPondId(aio.getPondId());
					}
					return RESCODE.SUCCESS.getJSONRES(bindState);
				}
			case "02":
				AIO aio2 = aioDao.findAIOByDeviceSns(device_sn);
				if (aio2 == null) {
					return RESCODE.NOT_FOUND.getJSONRES();
				} else {
					BindState bindState = new BindState();
					bindState.setDeviceName(aio2.getName());
					bindState.setStatus(aio2.getStatus());
					if (aio2.getPondId() > 0) {
						bindState.setPondId(aio2.getPondId());
						bindState.setPondName(pondDao.findPondByPondId(aio2.getPondId()).getName());
					} else {
						bindState.setPondId(aio2.getPondId());
					}
					return RESCODE.SUCCESS.getJSONRES(bindState);
				}
			case "03":
				Sensor sensor = sensorDao.findSensorByDeviceSns(device_sn);
				if (sensor == null) {
					return RESCODE.NOT_FOUND.getJSONRES();
				} else {
					BindState bindState = new BindState();
					bindState.setDeviceName(sensor.getName());
					bindState.setStatus(Integer.toString(sensor.getStatus()));
					PortBind bind = null;
					Controller controller = null;
					List<Sensor_Controller> list = sensor_ControllerDao.list(sensor.getId());
					Set<PortBind> portBinds = new HashSet<>();
					for (Sensor_Controller sensor_Controller : list) {
						bind = new PortBind();
						controller = controllerDao.findControllerById(sensor_Controller.getControllerId());
						bind.setPort(sensor_Controller.getSensor_port());
						bind.setBindId(controller.getId());
						bind.setBindName(controller.getName());
						bind.setBindDeviceSn(controller.getDevice_sn());
						bind.setBindPort(sensor_Controller.getController_port());
						portBinds.add(bind);
					}
					if (sensor.getPondId() > 0) {
						bindState.setPondId(sensor.getPondId());
						bindState.setPondName(pondDao.findPondByPondId(sensor.getPondId()).getName());
					} else {
						bindState.setPondId(sensor.getPondId());
					}
					bindState.setPortBinds(portBinds);
					return RESCODE.SUCCESS.getJSONRES(bindState);
				}
			case "04":
				Controller controller1 = controllerDao.findControllerByDeviceSns(device_sn);
				if (controller1 == null) {
					return RESCODE.NOT_FOUND.getJSONRES();
				} else {
					BindState bindState = new BindState();
					bindState.setDeviceName(controller1.getName());
					bindState.setStatus(Integer.toString(controller1.getStatus()));
					PortBind bind = null;
					Sensor sensor2 = null;
					List<Sensor_Controller> list = sensor_ControllerDao.controller(controller1.getId());
					Set<PortBind> portBinds = new HashSet<>();
					for (Sensor_Controller sensor_Controller : list) {
						bind = new PortBind();
						sensor2 = sensorDao.findSensorById(sensor_Controller.getSensorId());
						bind.setPort(sensor_Controller.getController_port());
						bind.setBindId(sensor_Controller.getSensorId());
						bind.setBindName(sensor2.getName());
						bind.setBindDeviceSn(sensor2.getDevice_sn());
						bind.setBindPort(sensor_Controller.getSensor_port());
						portBinds.add(bind);
					}
					if (controller1.getPondId() > 0) {
						bindState.setPondId(controller1.getPondId());
						bindState.setPondName(pondDao.findPondByPondId(controller1.getPondId()).getName());
					} else {
						bindState.setPondId(controller1.getPondId());
					}
					bindState.setPortBinds(portBinds);
					return RESCODE.SUCCESS.getJSONRES(bindState);
				}
			default:
				return RESCODE.WRONG_PARAM.getJSONRES();
			}
		}
	}
}
