import request from '../utils/request';
import { stringify } from 'qs';

export async function queryEquipment(params) {
    return request(`/api/equipment/query?${stringify(params)}`);
}

export async function addEquipment(params) {
    return request(`/api/equipment/add`, {
        method: 'POST',
        body: params
    });
}

export async function modifyEquipment(params) {
    return request('/api/equipment/modify', {
        method: 'POST',
        body: params
    });
}

export async function delEquipments(params) {
    let str = ''
    for (let item of params.equipmentIds) {
        str = 'equipmentIds=' + item + '&' + str
    }
    str = str.slice(0, -1)
    return request(`/api/equipment/delEquipments?${str}`)
}