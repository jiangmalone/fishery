import React from 'react';
import './equipmentManagement.less'
import { ActionSheet, Toast, ActivityIndicator, Picker } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import { connect } from 'dva';
import NavBar from '../../components/NavBar';
import online from '../../img/state-online.png';
import offline from '../../img/state-offline.png';
import { queryEquipment } from '../../services/equipment.js';         //接口
import { wxQuery } from '../../services/pondManage.js'; //接口
import { delBind, pondWithSensorOrAIO, delSensorOrAIOBind } from '../../services/bind.js';         //接口
//delBind   传感器与控制器的 解绑 oxo
//pondWithSensorOrAIO 一体机或传感器与塘口间的 绑定 o-o
//delSensorOrAIOBind 一体机或传感器与塘口间的 解绑 oxo

// 如果不是使用 List.Item 作为 children
const CustomChildren = props => (
    <div
        onClick={props.onClick}
        style={{ backgroundColor: '#fff', paddingLeft: 15 }}
    >
        <div className="test" style={{ display: 'flex', height: '45px', lineHeight: '45px' }}>
            {/* <div style={{ flex: 1, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{props.children}</div> */}
            <div style={{ textAlign: 'right', color: '#888', marginRight: 15 }}>{props.extra}</div>
        </div>
    </div>
);

class EquipmentManagement extends React.Component {

    constructor(props) {
        super(props);
        let equipmentData = JSON.parse(this.props.match.params.data)
        let type = equipmentData.device_sn.substring(0, 2);
        let device_sn = equipmentData.device_sn;
        let id = equipmentData.id;
        this.state = {
            animating: false,
            type: type,
            id: id,
            device_sn: device_sn,
            equipmentData: {},
            ponds: [],
            pickerValue:[],
        }
    }

    componentDidMount() {
        if (this.state.id) {
            this.queryEquipment();
            this.queryPond();
        } else {
            Toast.fail('系统错误，请退出重试', 1);
            setTimeout(() => {
                this.props.dispatch({
                    type: 'global/changeState',
                    payload: {
                        transitionName: 'right'
                    }
                })
                history.back();
            }, 1000)
        }
    }

    queryPond = () => {
        this.setState({ animating: true });
        wxQuery({
            relationId: 'WX4',
        }).then((res) => {
            this.setState({ animating: false });
            if (res.data.code == '0') {
                const pond = res.data.data;
                let ponds = []
                pond.map((item, index) => {
                    ponds.push({ label: item.name, value: item.id });
                })
                this.setState({ ponds: ponds });
            }
        }).catch((error) => {
            this.setState({ animating: false });
            console.log(error);
        });
    }

    queryEquipment = () => {
        this.setState({ animating: true });
        queryEquipment({
            device_sn: this.state.device_sn,
            page: 1,
            number: 1,
            // relation: 14,
        }).then((res) => {
            this.setState({ animating: false });
            console.log(res);
            this.setState({ animating: false })
            if (res.data && res.data.code == 0) {

            } else {
                Toast.fail(res.data.msg, 1);
            }
        }).catch((error) => {
            this.setState({ animating: false });
            Toast.fail('请求失败!', 1);
            console.log(error)
        });
    }

    getPorts = (portsData) => {
        return portsData.map((port, index) => {
            if (port.state) {
                return getBindedPort(port);
            } else {
                return getUnbindPort(port);
            }
        })
    }

    getBindedPort = (portData) => {
        return <div className='port-content' >
            <div className='prot-name-line' >
                <div className='left '>端口名称：端口2（已绑定）</div>
                {(this.state.type == '03') && <div className='right unbinded' onClick={() => this.unlockEquipment(2)} >
                    解绑
                </div>}
            </div>
            <div className='prot-info' >
                <div>设备绑定：控制器2</div>
                <div>设备端口：端口2</div>
            </div>
        </div>
    }

    getUnbindPort = (portData) => {
        return <div className='prot-name-line' >
            <div className='left '>端口名称：端口2（未绑定）</div>
            {(this.state.type == '03') && <div className='right binded' onClick={() => { this.bindEquipment({}) }} >
                绑定
            </div>}
        </div>
    }

    getPond = () => {
        return <div className='port-content' >
            <div className='prot-name-line' >
                <div className='pondLeft'>绑定塘口</div>

                <div className='pondName' >
                    
                    <Picker
                        title="选择地区"
                        extra="请选择(可选)"
                        data={this.state.ponds}
                        cols={1}
                        value={this.state.pickerValue}
                        onChange={v => this.setState({ pickerValue: v })}
                        onOk={v => this.setState({ pickerValue: v })}
                    >
                        <CustomChildren></CustomChildren>
                    </Picker>
                </div>


                <div className='right unbinded' onClick={() => this.unBindPond(2)} >
                    解绑
                </div>
            </div>
        </div>
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
        this.setState({ animating: true });
        delBind({
            sensorId: this.props.match.params.equipmentId,
            sensor_port: port
        }).then(res => {
            this.setState({ animating: false });
            if (res.data && res.data.code == 0) {
                Toast.success('解绑成功！', 1);
            } else {
                Toast.fail(res.data.msg, 1);
            }
        }).catch((error) => {
            this.setState({ animating: false });
            Toast.fail('解绑失败，请重试！', 1);
            console.log(error);
        });
    }

    bindEquipment = (port) => {
        this.props.dispatch({
            type: 'global/changeState',
            payload: {
                transitionName: 'left'
            }
        })
        //传多个数据过去
        let data = { equipmentId: this.props.match.params.equipmentId, port: port };
        data = JSON.stringify(data);
        this.props.history.push(`/bindEquipment/${data}`);
    }

    unBindPond = () => {
        const BUTTONS = ['解绑', '取消'];
        ActionSheet.showActionSheetWithOptions({
            options: BUTTONS,
            cancelButtonIndex: BUTTONS.length - 1,
            destructiveButtonIndex: BUTTONS.length - 2,
            message: '您是否确定解绑该渔塘？',
            maskClosable: true,
            'data-seed': 'myEquipment',
            // wrapProps,
        }, (buttonIndex) => {
            if (buttonIndex == 0) {
                this.doUnbindPond();
            }
        });
    }

    doUnbindPond = () => {
        this.setState({ animating: true });
        delSensorOrAIOBind({
            device_sn: this.props.match.params.equipmentId,
            type: (this.state.type == '03' ? 1 : 2)
        }).then(res => {
            this.setState({ animating: false });
            if (res.data && res.data.code == 0) {
                Toast.success('解绑成功！', 1);
            } else {
                Toast.fail(res.data.msg, 1);
            }
        }).catch((error) => {
            this.setState({ animating: false });
            Toast.fail('解绑失败，请重试！', 1);
            console.log(error);
        });
    }


    render() {
        const pondLine = this.getPond();
        return <div className='equipment-management-bg' style={{ minHeight: window.document.body.clientHeight }}>
            <NavBar title={"设备管理"} />
            <div className='header-line' >
                <div className='name'>
                    传感器1(编号:88888888)
                </div>
                <div className='state' >
                    <img src={offline} />
                    <span className='offline'  >
                        离线
                    </span>
                </div>
            </div>

            {pondLine}
            {(this.state.type != '01' && this.state.type != '02') && <div className='port-content' >
                <div className='prot-name-line' >
                    <div className='left'>端口名称：端口1（未绑定）</div>
                    {(this.state.type == '03') && <div className='right binded'>
                        绑定
                    </div>}
                </div>
                <div className='prot-name-line' >
                    <div className='left '>端口名称：端口2（已绑定）</div>
                    {(this.state.type == '03') && <div className='right unbinded' onClick={() => this.unlockEquipment(2)} >
                        解绑
                    </div>}
                </div>
                <div className='prot-info' >
                    <div>设备绑定：控制器2</div>
                    <div>设备端口：端口2</div>
                </div>
                <div className='prot-name-line' >
                    <div className='left '>端口名称：端口2（未绑定）</div>
                    {(this.state.type == '03') && <div className='right binded' onClick={() => { this.bindEquipment() }} >
                        绑定
                    </div>}
                </div>
            </div>}
            <ActivityIndicator
                toast
                text="Loading..."
                animating={this.state.animating}
            />
        </div>
    }
}

export default connect()(EquipmentManagement);
