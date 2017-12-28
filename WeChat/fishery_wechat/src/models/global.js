export default {
    namespace: 'global',
    state: {
        transitionName: 'right'
    },
    // effects: {
    //     *login({ payload }, { call, put }) {  //put 用来发起一条action、call 以异步的方式调用函数、select从 state中获取相关的数据，take获取发送的数据
    //         yield put({ type: 'showLoginLoading' });
    //         yield call(delay, 2000)
    //         yield put({ type: 'hideLoginLoading' })
    //         yield call(delay, 1000)
    //         yield put(routerRedux.push('/main'))
    //     },
    // },//接收数据
    reducers: {
        changeState(state, action) {
            return { ...state, ...action.payload };
        },
    },
};
