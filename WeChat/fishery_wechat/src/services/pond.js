import post from '../utils/post';
import get from '../utils/get';
export default {

    queryPonds: (params) => {
        return get('pond/query',params)
    },

    addPond: (options, params) => {
        return post('pond/addPond', options, params)
    },

    pondEquipment: (params) => {
        return get('pond/pondEquipment',params)
    },

    delPonds: (options, params) => {
        return post('pond/delPonds', options, params);
    },

    modifyPond:(options, params) => {
        return post('pond/modifyPond', options, params);
    }

}