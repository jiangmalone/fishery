import React, { Component } from 'react';
import './addFish.less';
import { List, InputItem, Picker } from 'antd-mobile';
import NavBar from '../../../components/NavBar';
import { createForm } from 'rc-form';
import index, { connect } from 'dva';
import update from 'immutability-helper'
class AddFish extends Component {

    constructor(props) {
        super(props)
        this.state = {
            selectedFish: this.props.selectedFishes
        }
    }

    componentDidMount() {
        this.props.dispatch({
            type: 'pond/queryFish'
        })
        let indexArray = []
        for (let item of this.state.selectedFish) {
            indexArray.push(this.props.fishes.findIndex((value) => {
                return value.fish_name == item
            }))
        }
        for (let item of indexArray) {
            this[`span${item}`].className = "fish-name-selected active"
        }
    }


    selectFish(item, index) {
        let existIndex = this.state.selectedFish.findIndex((value) => {
            return value == item.fish_name;
        });
        if (existIndex >= 0) {
            this.setState({
                selectedFish: update(this.state.selectedFish, { $splice: [[existIndex, 1]] })
            })
            this[`span${index}`].className = "fish-name-selected "
        } else {
            this.setState({
                selectedFish: update(this.state.selectedFish, { $push: [item.fish_name] })
            })
            this[`span${index}`].className = "fish-name-selected active"
        }
    }

    onSubmit() {
        this.props.dispatch({
            type: 'pond/changeState',
            payload: {
                selectedFishes: this.state.selectedFish
            }
        })
        history.back();

    }
    render() {
        let hasSelected = ''
        for (let item of this.state.selectedFish) {
            hasSelected = hasSelected + item + '、';
        }
        hasSelected = hasSelected.slice(0, -1)
        const fishs = this.props.fishes.map((item, index) => {
            return <div key={index} ref={(div) => { this[`dom${index}`] = div }} className="fish-type" onTouchStart={() => {
                this[`dom${index}`].className = "fish-type active"
            }} onTouchEnd={() => {
                this[`dom${index}`].className = "fish-type"
            }} onClick={() => {
                this.selectFish(item, index)
            }}
            >
                <div className="fish-name">
                    {item.fish_name}
                    <span ref={(span) => { this[`span${index}`] = span }} className="fish-name-selected">✓</span>
                </div>
            </div>
        })
        return (
            <div className="body-bac">
                <NavBar title={"添加养殖品种"} />
                <div className="fish-selected">
                    <div className="fish-title">当前添加的种类:</div>
                    <div className="fish-name">{hasSelected}</div>
                </div>
                {fishs}
                <div className="fish-white"></div>
                <div className="addFish-btn" onClick={() => { this.onSubmit() }}>确认提交</div>
            </div>
        );
    }

}
export default connect((state) => {
    return ({
        selectedFishes: state.pond.selectedFishes,
        fishes: state.pond.fishes
    })
})(AddFish);