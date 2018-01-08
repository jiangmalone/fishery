import post from '../utils/post';
import get from '../utils/get';
export default {
    modifyWXUser: (params) => {
        return post('api/usermanagement/modifyWXUser', params)
    }
}