import post from '../utils/post';
import get from '../utils/get';
export default {

    pondWithSensorOrAIO: (options, params) => {         //一体机或传感器与塘口间的 绑定 o-o
        return post('api/bind/pondWithSensorOrAIO', options, params)
    },

    delSensorOrAIOBind: (options, params) => {          //一体机或传感器与塘口间的 解绑 oxo
        return post('api/bind/delSensorOrAIOBind', options, params)
    },

    delBind: (options, params) => {                     //传感器与控制器的 解绑 oxo
        return post('api/bind/delBind', options, params)
    },

    sensorWithController: (options, params) => {         //控制器与传感器间的 绑定 o-o
        return post('api/bind/sensorWithController', options, params)
    },

}