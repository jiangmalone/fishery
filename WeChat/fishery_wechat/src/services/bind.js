import post from '../utils/post';
import get from '../utils/get';
export default {

    pondWithSensorOrAIO: (params) => {         //一体机或传感器与塘口间的 绑定 o-o
        return get('api/bind/pondWithSensorOrAIO', params)
    },

    delSensorOrAIOBind: (params) => {          //一体机或传感器与塘口间的 解绑 oxo
        return get('api/bind/delSensorOrAIOBind', params)
    },

    delBind: (params) => {                     //传感器与控制器的 解绑 oxo
        return get('api/bind/delSensorControllerBind', params)
    },

    sensorWithController: (params) => {         //控制器与传感器间的 绑定 o-o
        return get('api/bind/bindSensorController', params)
    },

    bindState: (params) => {
        return get('api/bind/bindState', params)
    }

}