import post from '../utils/post';
import get from '../utils/get';
export default {

    queryPonds: (params) => {
        return get('api/pond/query',params)
    },

    addPond: (options, params) => {
        return post('api/pond/addPond', options, params)
    },

    pondEquipment: (params) => {
        return get('api/pond/pondEquipment',params)
    },

    delPonds: (options, params) => {
        return post('api/pond/delPonds', options, params);
    },

    modifyPond:(options, params) => {
        return post('api/pond/modifyPond', options, params);
    }

}