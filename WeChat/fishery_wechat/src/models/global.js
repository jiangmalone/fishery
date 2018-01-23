import verifyIsLogin from '../services/sms.js';
export default {
    namespace: 'global',
    state: {
        transitionName: 'right',
        login: true
    },
    reducers: {
        changeState(state, action) {
            return { ...state, ...action.payload };
        },
    },
};
