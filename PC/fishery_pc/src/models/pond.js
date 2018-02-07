import { queryFakeList, addUser } from '../services/api';
import { queryPond, addPond, modifyPond, delPonds, pondEquipment, pondFish } from '../services/pond'
import update from 'immutability-helper'
import { pondDetail } from '../services/pond.js'
import { message } from 'antd';
export default {
    namespace: 'pond',

    state: {
        list: [],
        loading: false,
        pagination: { current: 1 },
        modalvisible: false,
        mapVisible: false,
        address: '',
        formData: { fields: {} },
        pondList: [],
        pagination2: { current: 1 },
        fishCategories: [],
        pondInfo: {},
        user:''
    },

    effects: {
        *fetchFishList({ payload }, { call, put }) {
            const response = yield call(pondFish);
            if (response.code == '0') {
                yield put({
                    type: 'changeModal',
                    payload: {
                        fishCategories: response.data || []
                    }
                })
            }
            yield put({
                type: 'changeModal',
            })
        },
        *fetch({ payload }, { call, put }) {
            yield put({
                type: 'changeLoading',
                payload: true,
            });
            const response = yield call(queryPond, payload);
            if (response.code == "0") {
                for (let item of response.data) {
                    item.key = item.id
                }
                yield put({
                    type: 'appendList',
                    payload: {
                        list: Array.isArray(response.data) ? response.data : [],
                        user:response.user,
                        pagination: {
                            total: response.realSize,
                        }
                    }
                });
            } else {
                message.error(response.msg, 1);
                yield put({
                    type: 'appendList',
                    payload: {
                        list: [],
                        pagination: {
                            total: 0,
                        }
                    }
                });
            }
            yield put({
                type: 'changeLoading',
                payload: false,
            });
        },
        *fetchDetail({ payload }, { call, put }) {
            const response = yield call(pondDetail, payload);
            if (response.code == "0") {
                yield put({
                    type: 'changeModal',
                    payload: {
                        pondInfo: response.data
                    }
                })
            }
        },
        *fetchEquipment({ payload }, { call, put }) {
            yield put({
                type: 'changeLoading',
                payload: true,
            });
            const response = yield call(pondEquipment, payload);
            if (response.code == "0") {
                for (let item of response.data) {
                    item.key = item.id
                }
                yield put({
                    type: 'changeModal',
                    payload: {
                        pondList: Array.isArray(response.data) ? response.data : [],
                        pagination2: {
                            total: response.realSize,
                        }
                    }
                });
            }
            yield put({
                type: 'changeLoading',
                payload: false,
            });
        },
        *addPond({ payload }, { call, put }) {
            const response = yield call(addPond, payload);
            if (response.code == '0') {
                yield put({
                    type: 'fetch',
                    payload: {
                        page: 1,
                        relation: payload.relation,
                        number: 10
                    }
                })
            } else {
                message.error(response.msg, 1)
            }
        },
        *modifyPond({ payload }, { call, put }) {

            const response = yield call(modifyPond, payload.data);
            if (response.code == '0') {
                response.data.key = response.data.id
                yield put({
                    type: 'modifyList',
                    payload: {
                        index: payload.index,
                        data: response.data,
                    },
                });
                yield put({
                    type: 'changeModal',
                    payload: {
                        address: {
                            address: response.data.address, location: {
                                lat: response.data.latitude,
                                lng: response.data.longitude
                            }
                        },
                    },
                });
            }
        },
        *deletePond({ payload }, { call, put }) {
            const response = yield call(delPonds, { pondIds: payload.pondIds });
            if (response.code == '0') {
                yield put({
                    type: 'fetch',
                    payload: {
                        page: payload.pagination.current,
                        relation: payload.relation,
                        number: 10
                    }
                })
            }
        }
    },

    reducers: {
        appendList(state, action) {
            return {
                ...state,
                list: action.payload.list,
                user:action.payload.user,
                pagination: { ...state.pagination, ...action.payload.pagination }
            };
        },
        addList(state, action) {
            let list = state.list;
            list.unshift(action.payload);
            return {
                ...state,
                list: list,
                pagination: { ...state.pagination, total: state.pagination.total + 1 }
            };
        },
        modifyList(state, action) {
            return {
                ...state,
                list: update(state.list, { [action.payload.index]: { $set: action.payload.data } })
            }
        },
        changeLoading(state, action) {
            return {
                ...state,
                loading: action.payload,
            };
        },
        changeModal(state, action) {
            return { ...state, ...action.payload };
        },
    },
};
