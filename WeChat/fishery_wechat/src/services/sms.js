import request from '../utils/request';

export default {
  //短信验证接口
  identifyCode: (params) => {
    return post('api/sms/request', params);
  },
  loginBind: (params) => {
    return post('api/sms/bind', params)
  },
  verification:(options,params)=>{
    return request('api/sms/verification', options,params)
  },
  verifySmsCode:(options,params)=>{
    return request('api/sms/verifySmsCode', options,params)
  }
}