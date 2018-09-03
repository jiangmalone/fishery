import request from '../utils/request';
import { stringify } from 'qs';

export async function pondWithSensorOrAIO(params) {
    return request('/api/equipment/modifySensor?params', {
      method: 'POST',
      body: [params]
    });
}
export async function bindContrallers(params) {
  return request('/api/equipment/modifyController?params', {
    method: 'POST',
    body: [params]
  });
}
export async function  delSensorOrAIOBind(params) {
  return request(`/api/bind/delSensorOrAIOBind?${stringify(params)}`);
}
export async function  delConBind(params) {
  return request(`/api/bind/delControllerBind?${stringify(params)}`);
}


export async function bindState(params) {
    return request(`/api/equipment/getControllersBydevice_sn?${stringify(params)}`)
}

export async function sensorWithController(params) {   //控制器与传感器间的 绑定 o-o
    return request(`/api/bind/bindSensorController?${stringify(params)}`)
}

export async function delBind(params) {   //控制器与传感器间的 绑定 o-o
    return request(`/api/bind/delSensorControllerBind?${stringify(params)}`)
}
