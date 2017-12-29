import React, { Component } from 'react';
import './addFish.less';
import { List, InputItem, Picker } from 'antd-mobile';
import NavBar from '../../../components/NavBar';
import { createForm } from 'rc-form';

class AddFish extends Component {

    constructor(props) {
        super(props)
        this.state = {
        }
    }
    render() {
        return (
            <div className="body-bac">
                <NavBar title={"添加养殖品种"} />
                <div className="fish-selected"><span>当前添加的种类:</span><span>鲤鱼，鲫鱼</span></div>
                <div ref={(div) => { this.getDom = div }} className="fish-type" onTouchStart={() => {
                    this.getDom.className = "fish-type active"
                }} onTouchEnd={() => {
                    this.getDom.className = "fish-type"
                }}>
                    <div className="fish-name">
                        白鱼
                        <span className="fish-name-selected">✓</span>
                    </div>
                </div>
              
                <div className="fish-white"></div>
                <div className="addFish-btn">确认提交</div>
            </div>
        );
    }

}

export default AddFish;
