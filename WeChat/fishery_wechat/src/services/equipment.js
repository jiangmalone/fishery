import post from '../utils/post';
import get from '../utils/get';
export default {

    queryEquipment: (params) => {
        return get('api/equipment/query', params)
    },

    addEquipment: (params) => {
        return get('api/equipment/add',  params)
    },

    deleteEquipment: (options, params) => {
        return post('api/equipment/delEquipments', options, params)
    }
}