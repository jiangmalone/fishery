import request from '../utils/request';
import { stringify } from 'qs';

export async function query() {
  return request('/api/users');
}

export async function queryCurrent() {
  return request('/api/currentUser');
}

export async function queryWXUser(params) {
  return request(`/api/usermanagement/queryWXUser?${stringify(params)}`);
}

export async function addWXUser(params) {
  return request('/api/usermanagement/addWXUser', {
    method: 'POST',
    body: params
  });
}

export async function modifyWXUser(params) {
  return request('/api/usermanagement/modifyWXUser', {
    method: 'POST',
    body: params
  });
}

export async function delWXUser(params) {
  return request(`/api/usermanagement/delWXUser?${stringify(params)}`);
}