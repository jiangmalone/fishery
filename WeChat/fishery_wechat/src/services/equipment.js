import post from '../utils/post';
import get from '../utils/get';
export default {
    
    updateWXUser: (options, params) => {
        return post('api/user/updateWXUser', options, params)
    },

    queryEquipment: (params) => {
        return get('api/equipment/query', params)
    },
}