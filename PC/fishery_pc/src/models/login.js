import { routerRedux } from 'dva/router';
import { fakeAccountLogin } from '../services/api';
import { login } from '../services/admin'
export default {
  namespace: 'login',

  state: {
    status: undefined,
  },

  effects: {
    *login({ payload }, { call, put }) {
      yield put({
        type: 'changeSubmitting',
        payload: true,
      });
      const response = yield call(login, payload);
      console.log(response)
      yield put({
        type: 'changeLoginStatus',
        payload: response,
      });
      // Login successfully
    
      if (response.code == 0) {
        window.localStorage.setItem('account',payload.account)
        yield put(routerRedux.push('/'));
      }
    },
    *logout(_, { put }) {
      window.localStorage.setItem('account','')
      yield put({
        type: 'changeLoginStatus',
        payload: {
          status: false,
        },
      });
      yield put(routerRedux.push('/user/login'));
    },
  },

  reducers: {
    changeLoginStatus(state, { payload }) {
      return {
        ...state,
        status: payload.status,
        type: payload.type,
        submitting: false,
      };
    },
    changeSubmitting(state, { payload }) {
      return {
        ...state,
        submitting: payload,
      };
    },
  },
};
