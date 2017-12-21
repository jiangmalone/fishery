import { queryFakeList } from '../services/api';

export default {
  namespace: 'commonUser',

  state: {
    list: [],
    loading: false,
    pagination:{},
    modalvisible:false,
    mapVisible:false,
    address:''
  },

  effects: {
    *fetch({ payload }, { call, put }) {
      yield put({
        type: 'changeLoading',
        payload: true,
      });
      const response = yield call(queryFakeList, payload);
      yield put({
        type: 'appendList',
        payload: Array.isArray(response) ? response : [],
      });
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
        list: state.list.concat(action.payload),
        pagination:{
          total:state.list.concat(action.payload).length,
          pageSize:10
        } 
      };
    },
    changeLoading(state, action) {
      return {
        ...state,
        loading: action.payload,
      };
    },
    changeModal(state, action) {
      console.log(state,action)
      return { ...state, ...action.payload };
    },
  },
};
