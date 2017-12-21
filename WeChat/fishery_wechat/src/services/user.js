import post from '../utils/post';
import get from '../utils/get';
import request from '../utils/request';
export default {
    addWxUser: (params) => {
        return post('api/user/addWxUser', params)
    },
    updateWXUser: (options, params) => {
        return post('api/user/updateWXUser', options, params)
    },

    logout: (params) => {
        return get('api/user/logout', params)
    },

    myDiscount: (params) => {
        return post('api/user/myDiscount', params)
    },//客户优惠券信息

    myPoints: (params) => {
        return get('api/user/points', params)
    },

    addCar: (params) => {
        return post('api/user/addCar', params)
    },

    userDetail: (params) => {
        return get('api/user/detail', params)
    },

    quickOrder: (params) => {
        return get('api/user/quickOrder', params)
    },

    delCar: (params) => {
        return get('api/user/delCar', params)
    },

    defaultCar: (params) => {
        return get('api/user/defaultCar', params)
    },

    modifyCarInfo: (params) => {
        return get('api/user/carInfo', params)
    },

    wxInfo: (params) => {
        return get('api/user/wxInfo', params)
    },

    myCard: (params) => {
        return get('api/user/listCard', params)
    },

    myCar: (params) => {
        return get('api/user/listCar', params)
    },
    detailInfo: (params) => {
        return get('api/user/detailInfo', params)
    },
    carDetail: (params) => {
        return get('api/user/carDetail', params)
    },

    annualCheck: (params) => {
        return get('api/user/annualCheck', params)
    },


    //保险开关
    insuranceSwitch: (params) => {
        return get('api/user/insurance', params)
    },

    //年检开关
    annualSwitch: (params) => {
        return get('api/user/annual', params)
    },


}