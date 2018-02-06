import { queryEquipment, companyFindEquipment, addEquipment, modifyEquipment, delEquipments} from '../services/equipment'
import update from 'immutability-helper'
import {message} from 'antd';
export default {
  namespace: 'equipment',

  state: {
    list: [],
    loading: false,
    pagination: { current: 1 },
    modalvisible: false,
    formData: { fields: {} },
    showAddModal: false,
  },

  effects: {
    *fetch({ payload }, { call, put }) {
      yield put({
        type: 'changeLoading',
        payload: true,
      });
      const response = yield call(queryEquipment, payload);
      if (response.code == "0") {
        for (let item of response.data) {
          item.key = item.device_sn
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
    *companyFindEquipment({ payload }, { call, put }) {
      yield put({
        type: 'changeLoading',
        payload: true,
      });
      const response = yield call(companyFindEquipment, payload);
      if (response.code == "0") {
        for (let item of response.data) {
          item.key = item.device_sn
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
    *addEquipment({ payload }, { call, put }) {
      const response = yield call(addEquipment, payload);
      if (response.code == '0') {
        message.success('新增设备成功', 1);
        yield put({
          type: 'companyFindEquipment',
          payload: {
            page: payload.current,
            number: 10,
            relation: payload.relation
          }
        })
        yield put({
          type: 'changeModal',
          payload: {
            showAddModal: false,
          }
        })
      } else {
        message.error(response.msg, 1);
      }
    },
    *modifyEquipment({ payload }, { call, put }) {
      const response = yield call(modifyEquipment, payload.data);
      if (response.code == '0') {
        message.success('修改设备成功', 1);
        yield put({
          type: 'modifyList',
          payload: {
            index: payload.index,
            data: response.sensor,
          },
        });

        yield put({
          type: 'changeModal',
          payload: {
            showAddModal: false,
          }
        })
      } else {
        message.error(response.msg, 1);
      }
    },
    *delEquipments({ payload}, {call, put}) {
      const response = yield call(delEquipments, { device_sns: payload.device_sns });
      if (response.code == '0') {
        message.success('删除设备成功', 1);
        yield put({
          type: 'companyFindEquipment',
          payload: {
            page: payload.pagination.current,
            number: 10,
            relation: payload.relation
          }
        })
      } else {
        message.error(response.msg ? response.msg : '删除设备发成错误！', 1);
      }
    }
  },

  reducers: {
    appendList(state, action) {
      return {
        ...state,
        list: action.payload.list,
        pagination: { ...state.pagination, ...action.payload.pagination }
      };
    },
    addList(state, action) {
      let list = state.list;
      if(action.payload) {
        const item = action.payload;
        item.key = item.device_sn;
        list.unshift(item);
        return {
          ...state,
          list: list,
          pagination: { ...state.pagination, total: state.pagination.total + 1 },
          formData: { fields: {} }
        };
      }
      
    },
    modifyList(state, action) {
      return {
        ...state,
        list: update(state.list, { [action.payload.index]: { $set: action.payload.data } }),
        formData: { fields: {} }
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
