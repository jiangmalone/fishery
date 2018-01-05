import { pondQuery } from '../services/pondManage.js'
export default {

    namespace: 'pond',

    state: {
        list: [],
        score: []
    },

    subscriptions: {
        setup({ dispatch, history }) {  // eslint-disable-line
            history.listen((location) => {
                const pathname = location.pathname;
                if (pathname == '/addPond') {
                    dispatch({ type: 'query' }, { payload: {relation:'',page:99,number:10} })
                }
            });
        },
    },

    effects: {
        *fetch({ payload }, { call, put }) {  // eslint-disable-line
            yield put({ type: 'save' });
        },
        *query({ payload }, { call, put }) {
            const data = yield call(pondQuery, payload)
            if (data) {
                yield put({
                    type: 'changeState',
                    list: {
                        list: data.data
                    },
                })
            }
        },
        *score({ payload }, { call, put }) {
            console.log(payload)
            const data = yield call(score, payload)
            console.log(data.data)
            if (data) {
                yield put({
                    type: 'changeScore',
                    list: {
                        score: data.data
                    },
                })
            }
        },
    },

    reducers: {
        changeState(state, action) {
            return { ...state, ...action.payload, ...action.list };
        },
        changeScore(state, action) {
            return { ...state, ...action.payload, ...action.list };
        },
    },

};
