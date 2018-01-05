import { pondQuery, addPond } from '../services/pondManage.js'
const delay = timeout => new Promise(resolve => setTimeout(resolve, timeout));
export default {

    namespace: 'pond',

    state: {
        list: [],
        loading: false,
    },

    subscriptions: {
        setup({ dispatch, history }) {  // eslint-disable-line
            history.listen((location) => {
                const pathname = location.pathname;
                if (pathname == '/myPond') {
                    dispatch({ type: 'query', payload: { relation: '', page: 99, number: 10 } })
                }
            });
        },
    },

    effects: {
        *query({ payload }, { call, put }) {
            console.log(payload)
            const data = yield call(pondQuery, payload)
            if (data) {
                yield put({
                    type: 'changeState',
                    list: {
                        list: data.data.data
                    },
                })
            }
        },
        *addPond({ payload }, { call, put }) {
            yield put({ type: 'showLoginLoading' });
            let data = yield call(addPond, payload);
            yield call(delay, 1000);
            if(data.data.code !='0') {
                yield put({ type: 'errorShow',error:data.data.msg })
            } else {
                yield put({ type: 'errorShow',error:false })
                
            }
            yield put({ type: 'hideLoginLoading' })
        },
    },

    reducers: {
        changeState(state, action) {
            return { ...state, ...action.payload, ...action.list };
        },
        hideLoginLoading(state, action) {
            return {
                loading: false,
            }
        },
        showLoginLoading(state, action) {
            return {
                loading: true
            }
        },
        errorShow(state,action) {
            return {
                error:action.error
            }
        }
    },

};
