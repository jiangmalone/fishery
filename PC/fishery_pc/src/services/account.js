import request from '../utils/request';
import { stringify } from 'qs';

export async function addAccount(params) {
    return request('/api/admin/add', {
        method: 'POST',
        body: params,
    });
}

export async function modifyAccount(params) {
    return request(`/api/admin/modify?${params}`)
}

export async function logout(params) {
    return request(`/api/admin/logout?${params}`)
}

export async function login(params) {
    return request(`/api/admin/login?${params}`)
}