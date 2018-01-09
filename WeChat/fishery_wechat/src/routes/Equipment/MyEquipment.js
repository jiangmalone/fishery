import React from 'react';
import './myEquipment.less'
import { Flex, Toast, List, Button, Modal, ActionSheet } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import { connect } from 'dva';
import Accordion from '../../components/Accordion';
import { query } from '../../services/equipment.js'; //接口
const alert = Modal.alert;
const sensors = [{
    name: '传感器一号',
    id: 1
}]
const controllers = [{
    name: '控制器一号',
    id: 2
}, {
    name: '控制器二号',
    id: 4
}]
const allInOnes = [{
    name: '一体机一号',
    id: 3
}]
class MyEquipment extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            list: this.props.list,
            isEdit: false
        }
    }

    edit = () => {
        this.setState({ isEdit: !this.state.isEdit })
    }

    showActionSheet = () => {

    }

    wouldDelete = (e,id) => {

        const BUTTONS = ['删除', '取消'];

        ActionSheet.showActionSheetWithOptions({
            options: BUTTONS,
            cancelButtonIndex: BUTTONS.length - 1,
            destructiveButtonIndex: BUTTONS.length - 2,
            // title: 'title',
            message: '您是否确定删除该设备？',
            maskClosable: true,
            'data-seed': 'mypond',
            // wrapProps,
        }, (buttonIndex) => {
            if (buttonIndex == 0) {
                this.doDelete(id)
            }
            console.log(buttonIndex)
            this.setState({ clicked: BUTTONS[buttonIndex] });
        });

        console.log(event)
        console.log('wouldDelete')
        event.stopPropagation();
        event.stopImmediatePropagation();
    }

    doDelete = (id) => {
        console.log('doDelete');
        console.log(id);
    }

    addEquipment = () => {
        this.props.dispatch({
            type: 'global/changeState',
            payload: {
                transitionName: 'left'
            }
        })
        this.props.history.push('/addEquipment');
    }

    checkDetail = (id) => {
        event.preventDefault();
        console.log('checkDetail')
        this.props.dispatch({
            type: 'global/changeState',
            payload: {
                transitionName: 'left'
            }
        })
        this.props.history.push(`/equipmentManagement/${id}`);

    }

    getController = (controllers) => {
        let cl = controllers.map((controller, index) => {
            return (<div className='line' key={controller.id} onClick={() => this.checkDetail(controller.id)} >
                {this.state.isEdit && <div className='delete-button' onClick={(e) => this.wouldDelete(e,controller.id)} >
                </div>}
                <div className='name' >
                    {controller.name}
                </div>
                <div className='right-imgs'>
                    <div className='online' >1
                    </div>
                    <div className='offline' >2
                    </div>
                    <div className='offline' >3
                    </div>
                    <div className='offline' >4
                    </div>
                </div>
            </div>)
        })
        return cl
    }

    getSensor = (sensors) => {
        let ss = sensors.map((sensor, index) => {
            return (<div className='line' key={sensor.id} onClick={() => this.checkDetail(sensor.id)} >
                {this.state.isEdit && <div className='delete-button' onClick={() => this.wouldDelete(sensor.id)} >
                </div>}
                <div className='name' >
                    {sensor.name}
                </div>
                <div className='right-imgs'>
                    <div className='online' >1
                </div>
                    <div className='offline' >2
                </div>
                </div>
            </div>)
        })
        return ss;
    }

    getAllInOne = (allInOnes) => {
        let aio = allInOnes.map((allInOne, index) => {
            return (<div className='line' key={allInOne.id} onClick={() => this.checkDetail(allInOne.id)} >
                {this.state.isEdit && <div className='delete-button' onClick={() => this.wouldDelete(allInOne.id)} >
                </div>}
                <div className='name' >
                    {allInOne.name}
                </div>
                <div className='right-imgs'>
                    <div className='online' >
                    </div>
                </div>
            </div>)
        })
        return aio;
    }
    render() {
        //todo   把list分类成不同种的设备
        let controller = this.getController(controllers);
        let sensor = this.getSensor(sensors);
        let allInOne = this.getAllInOne(allInOnes);
        return <div className='my-equipment-bg' style={{ minHeight: window.document.body.clientHeight }}>
            <div className="nav-bar-title">
                <i className="back" onClick={() => {
                    history.back();
                    this.props.dispatch({
                        type: 'global/changeState',
                        payload: {
                            transitionName: 'right'
                        }
                    })
                }}></i>
                我的设备
                <i className={this.state.isEdit ? 'right-item-none' : "edit"} onClick={this.edit} >{this.state.isEdit && "取消"}</i>
            </div>
            <div className='equipment-type'>
                <Accordion title='传感器' isShowState={false} isShow={true} >
                    <div className='equipment'>
                        {/* <div className='line' >
                            {this.state.isEdit && <div className='delete-button' onClick={this.wouldDelete} >
                            </div>}
                            <div className='name' >
                                传感器01
                            </div>
                            <div className='right-imgs'>
                                <div className='online' >1
                                </div>
                                <div className='offline' >2
                                </div>
                            </div>
                        </div>
                        <div className='line' >
                            {this.state.isEdit && <div className='delete-button' onClick={this.wouldDelete} >
                            </div>}
                            <div className='name' >
                                传感器02
                            </div>
                            <div className='right-imgs'>
                                <div className='online' >1
                                </div>
                                <div className='offline' >2
                                </div>
                            </div>
                        </div> */}
                        {sensor}
                    </div>
                </Accordion>
                <Accordion title='控制器' isShowState={false} isShow={true} >
                    <div className='equipment'>
                        {/* <div className='line' >
                            {this.state.isEdit && <div className='delete-button' onClick={this.wouldDelete} >
                            </div>}
                            <div className='name' >
                                控制器01
                            </div>
                            <div className='right-imgs'>
                                <div className='online' >1
                                </div>
                                <div className='offline' >2
                                </div>
                                <div className='offline' >3
                                </div>
                                <div className='offline' >4
                                </div>
                            </div>
                        </div>
                        <div className='line' >
                            {this.state.isEdit && <div className='delete-button' onClick={this.wouldDelete} >
                            </div>}
                            <div className='name' >
                                控制器02
                            </div>
                            <div className='right-imgs'>
                                <div className='online' >1
                                </div>
                                <div className='offline' >2
                                </div>
                                <div className='offline' >3
                                </div>
                                <div className='offline' >4
                                </div>

                            </div>
                        </div> */}
                        {controller}
                    </div>
                </Accordion>
                <Accordion title='一体机' isShowState={false} isShow={true} >
                    <div className='equipment'>
                        {/* <div className='line' >
                            {this.state.isEdit && <div className='delete-button' onClick={this.wouldDelete} >
                            </div>}
                            <div className='name' >
                                一体机01
                            </div>
                            <div className='right-imgs'>
                                <div className='online' >
                                </div>
                            </div>
                        </div>
                        <div className='line' >
                            {this.state.isEdit && <div className='delete-button' onClick={this.wouldDelete} >
                            </div>}
                            <div className='name' >
                                一体机02
                            </div>
                            <div className='right-imgs'>
                                <div className='offline' >
                                </div>
                            </div>
                        </div> */}
                        {allInOne}
                    </div>
                </Accordion>
            </div>
            <div className='add-button' onClick={this.addEquipment} >
            </div>
        </div>
    }
}

export default connect()(MyEquipment);
