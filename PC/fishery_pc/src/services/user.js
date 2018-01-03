import request from '../utils/request';
import { stringify } from 'qs';

export async function query() {
  return request('/api/users');
}

export async function queryCurrent() {
  return request('/api/currentUser');
}

export async function queryWXUser(params){
  return request(`/api/usermanagement/queryWXUser?${stringify(params)}`);
}