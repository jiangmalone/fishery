package com.geariot.platform.fishery.model;


import java.util.HashMap;
import java.util.Map;

import com.geariot.platform.fishery.exception.ForRollbackException;
import com.geariot.platform.fishery.utils.Constants;

public enum RESCODE {

	SUCCESS(0, "成功"), 
	WRONG_PARAM(1, "参数错误"), 
	NOT_FOUND(2, "无该条记录"),
	UPDATE_ERROR(3, "更新数据错误"), 
	CREATE_ERROR(4, "存储数据错误"), 
	DATE_FORMAT_ERROR(5, "日期格式错误"),
	DELETE_ERROR(6, "删除错误"), 
	DUPLICATED_ERROR(7,"重复数据"),
	FILE_ERROR(8, "上传文件错误"),
	PSW_ERROR(10, "密码错误"), 
	ALREADY_LOGIN(13, "已经登录"), 
	ACCOUNT_EXIST(14, "该账号已存在"),
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
