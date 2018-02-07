import { logout } from '../services/user.js'
export default {

    namespace: 'center',

    state: {
        list: [],
        score: []
    },

    //   subscriptions: {
    //     setup({ dispatch, history }) {  // eslint-disable-line
    //     	history.listen((location) => {
    //         const pathname = location.pathname;
    //         if(pathname == '/center') {
    //           dispatch({ type: 'query'},{payload:{}})
    //         }else if(pathname == '/pointsDetail') {
    //         	dispatch({ type: 'score'},{payload:{}})
    //         }
    //       });
    //     },
    //   },

    effects: {
        *fetch({ payload }, { call, put }) {  // eslint-disable-line
            yield put({ type: 'save' });
        },
        *query({ payload }, { call, put }) {
            const data = yield call(query, payload)
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
            const data = yield call(score, payload)
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
