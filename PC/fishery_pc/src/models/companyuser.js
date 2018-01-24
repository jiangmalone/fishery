import { queryCompany, addCompany, modifyCompany, delCompany } from '../services/user'
import update from 'immutability-helper'
import { addAccount2, modifyAccount } from '../services/account'
import { message } from 'antd';

export default {
  namespace: 'companyUser',

  state: {
    list: [],
    loading: false,
    pagination: { current: 1 },
    modalvisible: false,
    formData: { fields: {} },
    formData2: { fields: {} }
  },

  effects: {
    *fetch({ payload }, { call, put }) {
      yield put({
        type: 'changeLoading',
        payload: true,
      });
      const response = yield call(queryCompany, payload);
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
    *addCompany({ payload }, { call, put }) {
      const response = yield call(addCompany, payload);
      if (response.code == '0') {
        message.success('新增企业成功', 1);
        yield put({
          type: 'addList',
          payload: response.data,
        });
      } else {
        message.error(response.msg, 1);
      }
    },
    *addAccount({ payload }, { call, put }) {
      console.log(222)
      const response = yield call(addAccount2, payload.data);
      if (response.code == '0') {
        message.success('新增企业账户成功', 1);
        yield put({
          type: 'fetch',
          payload: {
            number: 10,
            page: 1
          },
        });
      } else {
        message.error(response.msg, 1);
      }
    },
    *modifyAccount({ payload }, { call, put }) {
      const response = yield call(modifyAccount, payload.data);
      if (response.code == '0') {
        message.success('修改企业账户成功', 1);
        yield put({
          type: 'fetch',
          payload: {
            number: 10,
            page: 1
          },
        });
      } else {
        message.error(response.msg, 1);
      }
    },
    *modifyCompany({ payload }, { call, put }) {
      const response = yield call(modifyCompany, payload.data);
      if (response.code == '0') {
        message.success('修改企业成功', 1);
        yield put({
          type: 'modifyList',
          payload: {
            index: payload.index,
            data: response.data,
          },
        });
      } else {
        message.error(response.msg, 1);
      }
    },
    *delCompany({ payload }, { call, put }) {
      const response = yield call(delCompany, { companyIds: payload.companyIds });
      if (response.code == '0') {
        message.success('删除企业成功', 1);
        yield put({
          type: 'fetch',
          payload: {
            page: payload.pagination.current,
            number: 10
          }
        })
      } else {
        message.success(response.msg, 1);
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
      const item = action.payload;
      item.key = item.id;
      list.unshift(item);
      return {
        ...state,
        list: list,
        pagination: { ...state.pagination, total: state.pagination.total + 1 },
        formData: { fields: {} }
      };
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
