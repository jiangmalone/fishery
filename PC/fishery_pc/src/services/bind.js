import request from '../utils/request';
import { stringify } from 'qs';

export async function pondWithSensorOrAIO(params) {
    return request(`/api/bind/pondWithSensorOrAIO?${stringify(params)}`);
}
 
export async function  delSensorOrAIOBind(params) {
    return request(`/api/bind/delSensorOrAIOBind?${stringify(params)}`);    
}

export async function bindState(params) {
    return request(`/api/bind/bindState?${stringify(params)}`)
}

export async function sensorWithController(params) {   //控制器与传感器间的 绑定 o-o
    return request(`/api/bind/bindSensorController?${stringify(params)}`)
}

export async function delBind(params) {   //控制器与传感器间的 绑定 o-o
    return request(`/api/bind/delSensorControllerBind?${stringify(params)}`)
}
