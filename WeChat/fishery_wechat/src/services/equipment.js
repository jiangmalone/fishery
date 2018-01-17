import post from '../utils/post';
import get from '../utils/get';
export default {

    queryEquipment: (params) => {
        return get('api/equipment/query', params);
    },

    addEquipment: (params) => {
        return get('api/equipment/add',  params);
    },

    deleteEquipment: (options, params) => {
        return post('api/equipment/delEquipments', options, params);
    },

    getData: (params) => {          //获得设备数据汇总  （曲线图数据）
        return get('api/equipment/data',  params);
    },

    getRealTimeData: (params) => {          //获得设备实时数据 
        return get('api/equipment/realTimeData',  params);
    },

    exportData: (params) => {
        return get('api/equipment/exportData',  params);
    },

    timer: (params) => {
        return get('api/equipment/timer', params);
    },

    setLimit: (params) => {
        return get('api/equipment/limit', params);
    },

    myEquipment: (params) => {
        return get('api/equipment/myEquipment', params);
    }


}