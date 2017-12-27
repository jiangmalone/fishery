import React from 'react';
import { List, InputItem, Picker } from 'antd-mobile';
import NavBar from '../../../components/NavBar';
import { createForm } from 'rc-form';
import './Mypond.less'

function MyPond() {

    return (
        <div className="body-bac">
            <div className="nav-bar-title">
                <i className="back" onClick={() => { history.back() }}></i>
                我的塘口
                <i className="edit"></i>
            </div>
            <div className="mypond-bac"></div>
            <div className="mypond-content">
                <div className="content-title">小鱼塘</div>
                <div>
                    <span className="content-info">
                        <i className="content-info-img" />
                        <span className="content-info-value">
                            20亩
                        </span>
                    </span>
                    <span className="content-info">
                        <i className="content-info-img" />
                        <span className="content-info-value">
                            20亩
                        </span>
                    </span>
                </div>
                <div>
                    <span className="content-info">
                        <i className="content-info-img" />
                        <span className="content-info-value">
                            20亩
                        </span>
                    </span>
                    <span className="content-info">
                        <i className="content-info-img" />
                        <span className="content-info-value">
                            20亩
                        </span>
                    </span>
                </div>
            </div>
        </div>
    );
}

export default MyPond;
