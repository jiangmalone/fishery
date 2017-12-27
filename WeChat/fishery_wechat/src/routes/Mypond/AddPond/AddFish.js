import React from 'react';
import './addFish.less';
import { List, InputItem, Picker } from 'antd-mobile';
import NavBar from '../../../components/NavBar';
import { createForm } from 'rc-form';

function AddFish() {
    
    return (
        <div className="body-bac">
            <NavBar title={"添加养殖品种"} />
            <div className="fish-selected"><span>当前添加的种类:</span><span>鲤鱼，鲫鱼</span></div>
            <div className="fish-type">
                <div className="fish-name">白鱼</div>
            </div>
            <div className="addPond-btn">确认提交</div>
        </div>
    );
}

export default AddFish;
