import post from '../utils/post';
import get from '../utils/get';
export default {

    aeratorOnOff: (params) => {       
        return get('api/equipment/aeratorOnOff', params)
    },

    setTimer: (params) => {          
        return get('api/equipment/timer', params)
    },


}