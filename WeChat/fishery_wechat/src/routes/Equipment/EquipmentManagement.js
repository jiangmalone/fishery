import React from 'react';
import './equipmentManagement.less'
import { ActionSheet, Toast, ActivityIndicator } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import { connect } from 'dva';
import NavBar from '../../components/NavBar';
import online from '../../img/state-online.png';
import offline from '../../img/state-offline.png';
import { delSensorOrAIOBind, delBind } from '../../services/bind.js'; //接口
import { queryEquipment } from '../../services/equipment.js';         //接口

class EquipmentManagement extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            animating: false
        }
    }

    componentDidMount() {
        // console.log( this.props.match.params.storeId)
        queryEquipment({
            device_sn: this.props.match.params.storeId,
            page: 1,
            number: 1,
            // relation: 14,
        }).then((res) => {
            console.log(res);
            this.setState({animating: false})
            if (res.data && res.data.code == 0) {
                
            } else {
                Toast.fail(res.data.msg, 1);
            }

        }).catch((error) => {
            this.setState({animating: false});
            Toast.fail('请求失败!', 1);
            console.log(error) 
        });
    }

    unlockEquipment = (port) => {
        const BUTTONS = ['解绑', '取消'];
        ActionSheet.showActionSheetWithOptions({
            options: BUTTONS,
            cancelButtonIndex: BUTTONS.length - 1,
            destructiveButtonIndex: BUTTONS.length - 2,
            message: '您是否确定解绑该端口？',
            maskClosable: true,
            'data-seed': 'myEquipment',
            // wrapProps,
        }, (buttonIndex) => {
            if (buttonIndex == 0) {
                this.doUnbindEquipment(port);
            }
        });
    }

    doUnbindEquipment = (port) => {
        this.setState({animating: true});
        delSensorOrAIOBind({
            sensorId: this.props.match.params.storeId,
            sensor_port: port
        }).then(res => {
            this.setState({animating: false});
            if (res.data && res.data.code == 0) {
                Toast.success('解绑成功！', 1);

            } else {
                Toast.fail(res.data.msg, 1);
            }
        }).catch((error) => {
            this.setState({animating: false});
            Toast.fail('解绑失败，请重试！', 1);
            console.log(error); 
        });
    }

    bindEquipment = () => {
        console.log('lockEquipment');
        this.props.dispatch({
            type: 'global/changeState',
            payload: {
                transitionName: 'left'
            }
        })
        this.props.history.push(`/bindEquipment/${this.props.match.params.storeId}`);
    }

    render() {
        return <div className='equipment-management-bg' style={{ minHeight: window.document.body.clientHeight }}>
            <NavBar title={"设备管理"} />
            <div className='header-line' >
                <div className='name'>
                    传感器1(编号:88888888)
                </div>
                <div className='state' >
                    <img src={offline} />
                    <span  className='offline'  >
                        离线
                    </span>
                </div>
            </div>
            <div className='port-content' >
                <div className='prot-name-line' >
                    <div className='left'>端口名称：端口1（未绑定）</div>
                    <div className='right binded'>
                    绑定
                    </div>
                </div>
            </div>

            <div className='port-content' >
                <div className='prot-name-line' >
                    <div className='left '>端口名称：端口2（已绑定）</div>
                    <div className='right unbinded' onClick={() => this.unlockEquipment(2)} >
                    解绑
                    </div>
                </div>
                <div className='prot-info' >
                    <div>设备绑定：控制器2</div>
                    <div>设备端口：端口2</div>
                </div>
                <div className='prot-name-line' >
                    <div className='left '>端口名称：端口2（未绑定）</div>
                    <div className='right binded' onClick={() => {this.bindEquipment()}} >
                    绑定
                    </div>
                </div>
            </div>
            <ActivityIndicator
                toast
                text="Loading..."
                animating={this.state.animating}
              />
        </div>
    }
}

export default connect()(EquipmentManagement);
