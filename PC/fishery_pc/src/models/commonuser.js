import { queryFakeList ,addUser} from '../services/api';
import { queryWXUser } from '../services/user'

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
    *addUser( {payload},{call,put}) {
      const response = yield call(addUser,payload);
      if(response.code=='0') {
        yield put({
          type: 'addList',
          payload: response.data,
        });
      }
    }
  },

  reducers: {
    appendList(state, action) {
      return {
        ...state,
        list: action.payload,
        pagination:{
          total:action.payload.length,
          pageSize:10
        } 
      };
    },
    addList(state,action){
      let list = state.list;
      list.unshift(action.payload);
      return {
        ...state,
        list: list ,
        pagination:{
          total:state.pagination.length+1,
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
