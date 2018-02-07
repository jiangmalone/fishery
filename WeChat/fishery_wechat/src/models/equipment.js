import { queryEquipment, addEquipment } from '../services/equipment.js'
import update from 'immutability-helper'
const delay = timeout => new Promise(resolve => setTimeout(resolve, timeout));
export default {

    namespace: 'equipment',

    state: {
        list: [],
        loading: false,
    },

    subscriptions: {
        setup({ dispatch, history }) {  // eslint-disable-line
            history.listen((location) => {
                const pathname = location.pathname;
                if (pathname == '/MyEquipment') {
                    dispatch({ type: 'query', payload: { relation: '1', page: 1, number: 99 } })
                }
            });
        },
    },

    effects: {
        *query({ payload }, { call, put }) {
            yield put({ type: 'showLoginLoading' });
            yield call(delay, 1000);
            const data = yield call(pondQuery, payload)
            if (data) {
                yield put({
                    type: 'changeState',
                    list: {
                        list: data.data.data
                    },
                })
            }
            yield put({ type: 'hideLoginLoading' })
        },
        *addEquipment({ payload }, { call, put }) {
            yield put({ type: 'showLoginLoading' });
            let data = yield call(addEquipment, payload);
            yield call(delay, 1000);
            if(data.data.code !='0') {
                yield put({ type: 'errorShow',error:data.data.msg })
                yield call(delay, 1000);
                yield put({ type: 'errorShow',error:false })
            } else {
                yield put({ type: 'errorShow',error:false })
            }
            yield put({ type: 'hideLoginLoading' })
        },
        *deletePond({ payload },{ call,put}) {
            yield put({type: 'showLoginLoading'})
            let data = yield call(delPonds, {pondIds:[payload.id]});
            yield call(delay, 1000);
            if(data.data.code !='0') {
                yield put({ type: 'errorShow',error:data.data.msg })
                yield call(delay, 1000);
                yield put({ type: 'errorShow',error:false })
            } else {
                yield put({ type: 'updatePond',index:payload.index })
                yield put({ type: 'errorShow',error:false })
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
        updatePond(state,action){
            return { ...state,list:update(state.list,{$splice:[[action.index,1]]})}
        },
        showLoginLoading(state, action) {
            return {
                ...state, 
                loading: true
            }
        },
        errorShow(state,action) {
            return {
                ...state, 
                error:action.error
            }
        }
    },

};
