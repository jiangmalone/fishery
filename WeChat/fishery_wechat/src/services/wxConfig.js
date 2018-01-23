import post from '../utils/post';
import get from '../utils/get';
export default {
    
    getWXConfig: (params) => {
        return get('api/webService/weather', params)
    },
}