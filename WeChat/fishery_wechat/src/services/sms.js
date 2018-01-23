import request from '../utils/request';
import get from '../utils/get';
export default {
  //短信验证接口
  verification:(params)=>{
    return get('api/webService/verification',params)
  },
  verifySmsCode:(params)=>{
    return get('api/webService/verifySmsCode', params)
  },
  verifyIsLogin:(params)=>{
    return get('api/webService/checkLogin', params)
  },
  LogOut:(params)=>{
    return get('api/webService/logout', params)
  }
}