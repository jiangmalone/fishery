import post from '../utils/post';
import get from '../utils/get';
export default {

    aeratorOnOff: (params) => {         //一体机或传感器与塘口间的 绑定 o-o
        return get('api/equipment/aeratorOnOff', params)
    },

    setTimer: (params) => {          //一体机或传感器与塘口间的 解绑 oxo
        return get('api/equipment/timer', params)
    },


}