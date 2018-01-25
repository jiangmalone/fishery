// 单选框组件
// props ： handlaClick(type)  0 点击了左边的 1 点击了右边的 
//          defaultClick       默认0 
//          items[]            长度必为2， 自定义字 如： 男 女 

import React from 'react';
import { TabBar } from 'antd-mobile';
import {withRouter} from "react-router-dom";
import selectedImg from '../img/radio-selected.png'
import unselectedImg from '../img/radio-unselected.png'
class SexRadio extends React.Component{
    constructor (props) {
        super(props);
        let defaultClick = 0;
        if(props.defaultClick) {
            defaultClick = props.defaultClick
        }
        this.state = {
            selectType: defaultClick
        }
    }

    handlaClick = (type) => {
        this.setState({
            selectType: type
        }, function () {
            if(this.props.handlaClick instanceof Function) {
                this.props.handlaClick(type);
            }
        })
    }
    render(){
        return <div style={{width: '100%'}}>
            <div onClick={() => this.handlaClick('男')} style={{width: '50%', display: 'inline-block'}}>
                <img src={!this.state.selectType? selectedImg : unselectedImg} style={{ verticalAlign:'middle', height:'.3rem'}}/>
                <span style={{ verticalAlign:'middle'}}> {(this.props.items && this.props.items.length == 2)? this.props.items[0] : '男'}</span>
            </div>
            <div onClick={() => this.handlaClick('女')} style={{width: '50%', display: 'inline-block'}}>
                <img src={this.state.selectType? selectedImg : unselectedImg} style={{verticalAlign:'middle', height:'.3rem'}}/>
                <span style={{verticalAlign:'middle'}}> {(this.props.items && this.props.items.length == 2)? this.props.items[1] : '女'}</span>
            </div>

        </div> 
    }
}

export default withRouter(SexRadio);
