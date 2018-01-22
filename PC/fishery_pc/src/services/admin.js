import request from '../utils/request';
import { stringify } from 'qs';


export async function login(params) {
    return request(`/api/admin/login?${stringify(params)}`);
}

export async function modify(params) {
    return request(`/api/admin/modify?${stringify(params)}`);
}

export async function add(params) {
    return request('/api/admin/add', {
        method: 'POST',
        body: params
    });
}


