import React from 'react';
import { connect } from 'dva';

function NavBar(props) {
    console.log(props)
    return <div className="nav-bar-title">
        <i className="back" onClick={() => {
            history.back();
            props.dispatch({
                type: 'global/changeState',
                payload: {
                    transitionName: 'right'
                }
            })
        }}></i>
        {props.title}
        <i className="right-item-none"></i>
    </div>
}
export default connect()(NavBar)