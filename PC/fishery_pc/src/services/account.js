import request from '../utils/request';
import { stringify } from 'qs';

export async function addAccount2(params) {
    return request('/api/admin/add', {
        method: 'POST',
        body: params
    });
}

export async function modifyAccount(params) {
    return request(`/api/admin/modify?${stringify(params)}`)
}

export async function logout(params) {
    return request(`/api/admin/logout?${stringify(params)}`)
}

export async function login(params) {
    return request(`/api/admin/login?${stringify(params)}`)
}


export async function modifyCompanyAccount(params) {
    return request(`/api/admin/modifyCompany?${stringify(params)}`)
}