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
  // params = stringify(params).replace(/\%.*?$/,'')
  // console.log(params)
  let str = ''
  for(let item of params.WXUserIds) {
    str= 'WXUserIds='+item+'&'+str
  }
  str = str.slice(0,-1)
  return request(`/api/usermanagement/delWXUser?${str}`)
}

//企业用户相关

export async function queryCompany(params) {
  return request(`/api/usermanagement/queryCompany?${stringify(params)}`);
}

export async function addCompany(params) {
  return request('/api/usermanagement/addCompany', {
    method: 'POST',
    body: params
  });
}

export async function modifyCompany(params) {
  return request('/api/usermanagement/modifyCompany', {
    method: 'POST',
    body: params
  });
}

export async function delCompany(params) {
  console.log('network dele')
  console.log(params)
  let str = ''
  for(let item of params.companyIds) {
    str= 'companyIds='+item+'&'+str
  }
  str = str.slice(0,-1)
  console.log(str)
  return request(`/api/usermanagement/delCompany?${str}`)
}

