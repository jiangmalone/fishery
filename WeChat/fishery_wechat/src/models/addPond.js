import { pondQuery, addPond, delPonds, modifyPond, fishType } from '../services/pondManage.js'
import update from 'immutability-helper'
import { routerRedux } from 'dva/router'
const delay = timeout => new Promise(resolve => setTimeout(resolve, timeout));
export default {

    namespace: 'pond',

    state: {
        list: [],
        loading: false,
        formData: { fields: {} },
        address: '',
        longitude: '',
        latitude: '',
        fishes: [],
        selectedFishes: []
    },

    // subscriptions: {
    //     setup({ dispatch, history }) {  // eslint-disable-line
    //         history.listen((location) => {
    //             const pathname = location.pathname;
    //            if (pathname == '/addFish') {
    //                 dispatch({ type: 'queryFish' })
    //             }
    //         });
    //     },
    // },

    effects: {
        *query({ payload }, { call, put }) {
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
        *queryFish({ payload }, { call, put }) {
            const fishes = yield call(fishType);
            if (fishes.data.code == '0') {
                yield put({ type: 'changeState', payload: { fishes: fishes.data.data } })
            }
        },
        *addPond({ payload }, { call, put }) {
            yield put({ type: 'showLoginLoading' });
            let data = yield call(addPond, payload);
            yield call(delay, 1000);
            if (data.data.code != '0') {
                yield put({ type: 'errorShow', error: data.data.msg });
                yield call(delay, 1000);
                yield put({ type: 'errorShow', error: false });
            } else {
                yield put(routerRedux.push('/MyPond'))
                yield put({
                    type: 'changeState', payload: {
                        formData: {
                            fields: {}
                        },
                        selectedFishes: [],
                        address: ''
                    }
                })
                yield put({ type: 'errorShow', error: false });
            }

            yield put({ type: 'hideLoginLoading' })
        },
        *modifyPond({ payload }, { call, put }) {
            yield put({ type: 'showLoginLoading' });
            let data = yield call(modifyPond, payload);
            yield call(delay, 1000);
            if (data.data.code != '0') {
                yield put({ type: 'errorShow', error: data.data.msg });
                yield call(delay, 1000);
                yield put({ type: 'errorShow', error: false });
            } else {
                yield put(routerRedux.push('/MyPond'))
                yield put({ type: 'errorShow', error: false });
            }
            yield put({ type: 'hideLoginLoading' })
        },
        *deletePond({ payload }, { call, put }) {
            yield put({ type: 'showLoginLoading' })
            let data = yield call(delPonds, { pondIds: [payload.id] });
            yield call(delay, 1000);
            if (data.data.code != '0') {
                yield put({ type: 'errorShow', error: data.data.msg })
                yield call(delay, 1000);
                yield put({ type: 'errorShow', error: false })
            } else {
                yield put({ type: 'updatePond', index: payload.index })
                yield put({ type: 'errorShow', error: false })
            }
            yield put({ type: 'hideLoginLoading' })
        }
    },

    reducers: {
        changeState(state, action) {
            return { ...state, ...action.payload, ...action.list };
        },
        hideLoginLoading(state, action) {
            return {
                ...state,
                loading: false,
            }
        },
        updatePond(state, action) {
            return { ...state, list: update(state.list, { $splice: [[action.index, 1]] }) }
        },
        showLoginLoading(state, action) {
            return {
                ...state,
                loading: true
            }
        },
        errorShow(state, action) {
            return {
                ...state,
                error: action.error
            }
        }
    },

};
