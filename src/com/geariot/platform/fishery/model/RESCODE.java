package com.geariot.platform.fishery.model;


import java.util.HashMap;
import java.util.Map;

import com.geariot.platform.fishery.exception.ForRollbackException;
import com.geariot.platform.fishery.utils.Constants;

public enum RESCODE {

	SUCCESS(0, "成功"), 
	WRONG_PARAM(1, "参数错误"), 
	NOT_FOUND(2, "无该条记录"),
	ACCOUNT_NOT_EXIST(3, "登录账户不存在"), 
	CREATE_ERROR(4, "存储数据错误"), 
	POND_NAME_EXIST(5, "该用户下塘口名称重复"),
	POND_NOT_EXIST(6, "数据库无此塘口"), 
	DUPLICATED_ERROR(7,"重复数据"),
	DELETE_ERROR(8, "删除错误"),
	AIO_EXIST(9,"该用户下一体机设备码重复"),
	PSW_ERROR(10, "密码错误"), 
	SENSOR_EXIST(11,"该用户下传感器设备码重复"),
	CONTROLLER_EXIST(12,"该用户下控制器设备码重复"),
	ALREADY_LOGIN(13, "已经登录"), 
	ACCOUNT_EXIST(14, "该账号已存在"),
	DEVICESNS_INVALID(15,"该设备码无效"),
	NOT_OPEN(16,"设备尚未开机"),
	CONNECTION_CLOSED(17,"与终端的连接已断开"),
	SEND_FAILED(18,"服务器下发命令失败"),
	NOT_RECEIVED(19,"长时间未收到设备反馈"),
	NOT_BINDED(20,"所选设备与塘口并无绑定关系"),
	NO_BIND_RELATION(21,"所选传感器与控制器无绑定关系"),
	ALREADY_BIND_SENSOR_WITH_CONTROLLER(22,"所选控制器及端口已经存在绑定关系"),
	EQUIPMENT_ALREADY_BIND_WITH_ONE_POND(23,"所选设备已经与塘口绑定了,请勿重复绑定"),
	FOR_EXCEPTION
	;
	
	
	// 定义私有变量
	private int nCode;

	private String nMsg;

	// 构造函数，枚举类型只能为私有
	private RESCODE(int _nCode, String _nMsg) {

		this.nCode = _nCode;
		this.nMsg = _nMsg;
	}
	
	private RESCODE(){
	}

	public String getMsg() {

		return nMsg;
	}

	public int getValue() {

		return nCode;
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.nCode);
	}
	
	

	/**
	 * 最新的返回json
	 */
	public Map<String,Object> getJSONRES(){
		Map<String,Object> map = new HashMap<>();
		map.put(Constants.RESPONSE_CODE_KEY, this.nCode);
		map.put(Constants.RESPONSE_MSG_KEY, this.nMsg);
		return map;
	}
	
	
	public Map<String,Object> getJSONRES(Object entity){
		Map<String, Object> jsonres = getJSONRES();
		jsonres.put(Constants.RESPONSE_DATA_KEY, entity);
		return jsonres;
	}
	
	public Map<String,Object> getJSONRES(Object entity,int pages,long count){
		Map<String, Object> jsonres = getJSONRES();
		jsonres.put(Constants.RESPONSE_DATA_KEY, entity);
		jsonres.put(Constants.RESPONSE_SIZE_KEY, pages);
		jsonres.put(Constants.RESPONSE_REAL_SIZE_KEY, count);
		return jsonres;
	}
	
	public Map<String,Object> getJSONRES(ForRollbackException e){
		Map<String,Object> map = new HashMap<>();
		map.put(Constants.RESPONSE_CODE_KEY, e.getErrorCode());
		map.put(Constants.RESPONSE_MSG_KEY, e.getMessage());
		return map;
	}
	
}
