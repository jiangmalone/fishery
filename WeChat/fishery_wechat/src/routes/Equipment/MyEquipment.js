import React from 'react';
import './myEquipment.less'
import { Flex, Toast, ActivityIndicator, List, Button, Modal, ActionSheet } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import { connect } from 'dva';
import Accordion from '../../components/Accordion';
import { myEquipment, deleteEquipment } from '../../services/equipment.js'; //接口

class MyEquipment extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            list: this.props.list,
            aios: [],
            controllers: [],
            sensors: [],
            isEdit: false,
            animating: false
        }
    }

    componentDidMount() {
        this.setState({ animating: true })
        myEquipment({
            relation: 'WX4',
        }).then((res) => {
            this.setState({ animating: false })
            if (res.data && res.data.code == 0) {
                const data = res.data;
                console.log(data);
                if (data.aio && data.aio.length > 0) {
                    this.setState({ aios: data.aio })
                }
                if (data.controller && data.controller.length > 0) {
                    this.setState({ controllers: data.controller })
                }
                if (data.sensor && data.sensor.length > 0) {
                    this.setState({ sensors: data.sensor })
                }
            } else {
                Toast.fail(res.data.msg, 1);
            }
        }).catch((error) => {
            this.setState({ animating: false });
            Toast.fail('请求失败', 1);
            console.log(error)
        });
    }

    wouldDelete = (e, device_sn) => {
        const BUTTONS = ['删除', '取消'];
        ActionSheet.showActionSheetWithOptions({
            options: BUTTONS,
            cancelButtonIndex: BUTTONS.length - 1,
            destructiveButtonIndex: BUTTONS.length - 2,
            message: '您是否确定删除该设备？',
            maskClosable: true,
        }, (buttonIndex) => {
            if (buttonIndex == 0) {
                this.doDelete(device_sn)
            }
        });
        event.stopPropagation();
        event.stopImmediatePropagation();
    }

    doDelete = (device_sn) => {
        console.log(device_sn);
        this.setState({ animating: true });
        deleteEquipment({
            device_sns: [device_sn]
        }).then(res => {

            this.setState({ animating: false });
            if(res.data && res.data.code == 0) {
                Toast.success('删除设备成功', 1);
            } else {
                Toast.fail(res.msg, 1);
            }
        }).catch(error => {
            console.log(error);
            this.setState({ animating: false });
            Toast.success('删除设备失败', 1);
        })
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

    checkDetail = (device_sn) => {
        this.props.dispatch({
            type: 'global/changeState',
            payload: {
                transitionName: 'left'
            }
        })
        this.props.history.push(`/equipmentManagement/${device_sn}`);
    }

    getController = (controllers) => {
        let cl = controllers.map((controller, index) => {
            // const ports = controller.port_status.split("");
            const ports = [0, 1, 1, 0];
            return (
                <div>
                    {this.state.isEdit && <div className='delete-button' onClick={(e) => this.wouldDelete(e, controller.device_sn)} >
                        </div>}
                    <div className={this.state.isEdit ? 'line editLine' : 'line'} key={controller.id} onClick={() => this.checkDetail(controller.device_sn)} >
                        <div className='name' >
                            {controller.name}
                        </div>
                        <div className='right-imgs'>
                            <div className={ports[0] ? 'online' : 'offline'} >1
                    </div>
                            <div className={ports[1] ? 'online' : 'offline'} >2
                    </div>
                            <div className={ports[2] ? 'online' : 'offline'} >3
                    </div>
                            <div className={ports[3] ? 'online' : 'offline'} >4
                    </div>
                        </div>
                    </div>
                </div>
            )
        })
        return cl
    }

    getSensor = (sensors) => {
        let ss = sensors.map((sensor, index) => {
            // const ports = sensor.port_status.split("");
            const ports = [0, 1];
            return (
                <div>
                    {this.state.isEdit && <div className='delete-button' onClick={() => this.wouldDelete(sensor.device_sn)} >
                    </div>}
                    <div className={this.state.isEdit ? 'line editLine' : 'line'} key={sensor.id} onClick={() => this.checkDetail(sensor.device_sn)} >

                        <div className='name' >
                            {sensor.name}
                        </div>
                        <div className='right-imgs'>
                            <div className={ports[0] ? 'online' : 'offline'} >1
                            </div>
                            <div className={ports[1] ? 'online' : 'offline'} >2
                            </div>
                        </div>
                    </div></div>)
        })
        return ss;
    }

    getAllInOne = (allInOnes) => {
        let aio = allInOnes.map((allInOne, index) => {
            return (
                <div>
                    {this.state.isEdit && <div className='delete-button' onClick={() => this.wouldDelete(allInOne.device_sn)} >
                    </div>}
                    <div className={this.state.isEdit ? 'line editLine' : 'line'} key={allInOne.id} onClick={() => this.checkDetail(allInOne.device_sn)} >

                        <div className='name' >
                            {allInOne.name}
                        </div>
                        <div className='right-imgs'>
                            <div className={allInOne.status ? 'online' : 'offline'} >
                            </div>
                        </div>
                    </div>
                </div>
            )
        })
        return aio;
    }

    render() {
        let controller = this.getController(this.state.controllers);
        let sensor = this.getSensor(this.state.sensors);
        let allInOne = this.getAllInOne(this.state.aios);
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
                <i className={this.state.isEdit ? 'right-item-none' : "edit"}
                    onClick={() => this.setState({ isEdit: !this.state.isEdit })} >
                    {this.state.isEdit && "取消"}
                </i>
            </div>
            <div className='equipment-type'>
                <Accordion title='传感器' isShowState={false} isShow={true} >
                    <div className='equipment'>
                        {sensor}
                    </div>
                </Accordion>
                <Accordion title='控制器' isShowState={false} isShow={true} >
                    <div className='equipment'>
                        {controller}
                    </div>
                </Accordion>
                <Accordion title='一体机' isShowState={false} isShow={true} >
                    <div className='equipment'>
                        {allInOne}
                    </div>
                </Accordion>
            </div>
            <div className='add-button' onClick={this.addEquipment} >
            </div>
            <ActivityIndicator
                toast
                text="Loading..."
                animating={this.state.animating}
            />
        </div>
    }
}

export default connect()(MyEquipment);
