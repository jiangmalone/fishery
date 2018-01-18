import request from '../utils/request';
import { stringify } from 'qs';

export async function queryPond(params) {
    return request(`/api/pond/query?${stringify(params)}`);
}

export async function addPond(params) {
    return request(`/api/pond/addPond`, {
        method: 'POST',
        body: params
    });
}

export async function pondEquipment(params) {
    return request(`/api/pond/pondEquipment?${stringify(params)}`);
}

export async function modifyPond(params) {
    return request('/api/pond/modifyPond', {
        method: 'POST',
        body: params
    });
}

export async function delPonds(params) {
    let str = ''
    for (let item of params.pondIds) {
        str = 'pondIds=' + item + '&' + str
    }
    str = str.slice(0, -1)
    return request(`/api/pond/delPonds?${str}`)
}

export async function pondFish() {
    return request(`/api/pond/fish`)
}