import post from '../utils/post';
import get from '../utils/get';
export default {
    
    getWeather: (params) => {
        return get('api/webService/weather', params)
    },
}