import { queryAdminEquipment, addEquipment, modifyEquipment, delEquipments } from '../services/equipment'
import update from 'immutability-helper'
import { message } from 'antd';

export default {
    namespace: 'allequipment',

    state: {
        list: [],
        loading: false,
        pagination: { current: 1 },
    },

    effects: {
        *fetch({ payload }, { call, put }) {
            yield put({
                type: 'changeLoading',
                payload: true,
            });
            const response = yield call(queryAdminEquipment, payload);
            if (response.code == "0") {
                for (let item of response.data) {
                    item.key = item.id
                }
                yield put({
                    type: 'appendList',
                    payload: {
                        list: Array.isArray(response.data) ? response.data : [],
                        pagination: {
                            total: response.realSize,
                        }
                    }
                });
            } else if (response.code) {
                message.error(response.msg, 1);
            }
            yield put({
                type: 'changeLoading',
                payload: false,
            });
        },
    },

    reducers: {
        appendList(state, action) {
            return {
                ...state,
                list: action.payload.list,
                pagination: { ...state.pagination, ...action.payload.pagination }
            };
        },
        changeLoading(state, action) {
            return {
                ...state,
                loading: action.payload,
            };
        },
    },
};
