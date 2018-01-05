import post from '../utils/post';
import get from '../utils/get';
export default {
    
    updateWXUser: (options, params) => {
        return post('api/user/updateWXUser', options, params)
    },

    logout: (params) => {
        return get('api/user/logout', params)
    },
}