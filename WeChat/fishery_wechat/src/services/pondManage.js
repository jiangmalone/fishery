import post from '../utils/post';
import get from '../utils/get';

export default {
    pondQuery: (params) => {
        console.log(params)
        return get('api/pond/query', params)
    },
    addPond:(params)=>{
        return post('api/pond/addPond',params)
    },
    pondEquipment:(params)=>{
        return get('api/pond/pondEquipment',params)
    },
    delPonds:(params)=>{
        return post('api/pond/delPonds',params)
    },
    modifyPond:(params)=>{
        return post('api/pond/modifyPond',params)
    }
}