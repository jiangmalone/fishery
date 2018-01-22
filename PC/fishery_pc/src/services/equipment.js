import request from '../utils/request';
import { stringify } from 'qs';

export async function queryEquipment(params) {
    return request(`/api/equipment/query?${stringify(params)}`);
}

export async function myEquipment(params){
    return request(`/api/pond/relationEquipment?${stringify(params)}`);
}


export async function companyFindEquipment(params) {
    return request(`/api/equipment/companyFindEquipment?${stringify(params)}`);
}


export async function queryAdminEquipment(params) {
    return request(`/api/equipment/adminFindEquipment?${stringify(params)}`);
}

export async function addEquipment(params) {
    return request(`/api/equipment/add?${stringify(params)}`);
}

export async function modifyEquipment(params) {
    return request('/api/equipment/modify', {
        method: 'POST',
        body: params
    });
}

export async function delEquipments(params) {
    let str = ''
    for (let item of params.device_sns) {
        str = 'device_sns=' + item + '&' + str
    }
    str = str.slice(0, -1)
    return request(`/api/equipment/delEquipments?${str}`)
}

export async function realTimeData(params) {
    return request(`/api/equipment/realTimeData?${stringify(params)}`);
}