import post from '../utils/post';
import get from '../utils/get';
export default {

    queryAlarm: (params) => {
        return get('api/equipment/queryAlarm', params);
    },

    AlarmIsRead: (params) => {
        return get('api/equipment/alarmIsRead', params)
    },
    
    queryEquipment: (params) => {
        return get('api/equipment/query', params);
    },

    addEquipment: (params) => {
        return get('api/equipment/add', params);
    },

    deleteEquipment: (params) => {
        let str = ''
        for (let item of params.device_sns) {
            str = 'device_sns=' + item + '&' + str
        }
        str = str.slice(0, -1)
        return get(`/api/equipment/delEquipments?${str}`)
        // return get('api/equipment/delEquipments', params);
    },

    getDataToday: (params) => {          //获得设备数据汇总  （曲线图数据）
        return get('api/equipment/dataToday', params);
    },

    getDataSevenday: (params) => {          //获得设备数据汇总  （曲线图数据）
        return get('api/equipment/dataAll', params);
    },

    getRealTimeData: (params) => {          //获得设备实时数据 
        return get('api/equipment/realTimeData', params);
    },

    exportData: (params) => {
        return get('api/equipment/exportData', params);
    },

    timer: (params) => {
        return get('api/equipment/timer', params);
    },

    setLimit: (params) => {
        return get('api/equipment/limit', params);
    },

    myEquipment: (params) => {
        return get('api/equipment/myEquipment', params);
    },

    serverCheck: (params) => {
        return get('api/equipment/serverCheck', params);
    },

    autoSet: (params) => {
        return post('api/equipment/autoSet', params)
    },

    queryAeratorData: (params) => {
        return get('api/equipment/queryAeratorData', params)
    },

}