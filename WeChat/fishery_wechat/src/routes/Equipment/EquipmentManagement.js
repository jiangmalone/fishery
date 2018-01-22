import React from 'react';
import './equipmentManagement.less'
import { ActionSheet, Toast, ActivityIndicator, Picker } from 'antd-mobile'
import { connect } from 'dva';
import NavBar from '../../components/NavBar';
import online from '../../img/state-online.png';
import offline from '../../img/state-offline.png';
import { queryEquipment } from '../../services/equipment.js';         //接口
import { wxQuery } from '../../services/pondManage.js'; //接口
import { delBind, pondWithSensorOrAIO, delSensorOrAIOBind, bindState } from '../../services/bind.js';         //接口
//delBind   传感器与控制器的 解绑 oxo
//pondWithSensorOrAIO 一体机或传感器与塘口间的 绑定 o-o
//delSensorOrAIOBind 一体机或传感器与塘口间的 解绑 oxo

// 如果不是使用 List.Item 作为 children
const CustomChildren = props => (
    <div
        onClick={props.onClick}
        style={{ backgroundColor: '#fff'}}
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
        if(type == '01' || type == '02') {
            type = 2;
        } else if (type == '03') {
            type = 1;
        } else {
            type = 0;
        }
        // type  0 控制器 1 传感器 2 一体机
        let device_sn = equipmentData.device_sn;
        let id = equipmentData.id;
        this.state = {
            animating: false,
            type: type,
            id: id,
            device_sn: device_sn,
            ponds: [],      // 所有的塘口
            pickerValue:[], // 选择需要绑定的塘口
            bindPond: {},   // 现在绑定的塘口
            portsData: []
        }
    }

    componentDidMount() {
        if (this.state.id) {
            this.bindState();
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

    bindState = () => {
        this.setState({ animating: true });
        bindState({
            device_sn: this.state.device_sn
        }).then(res => {
            this.setState({ animating: false });
            console.log(res);
            if (res.data && res.data.code == 0) {

                console.log('111');
                let portBinds = res.data.data.portBinds;
                let standardPorts = [];
                let bindPorts = [];
                if (this.state.type == 1) {
                    standardPorts = [1, 2];
                } else {
                    standardPorts = [1, 2, 3, 4];
                }
                portBinds.map((item, index) => {
                    bindPorts.push(item.port);
                })

                console.log(bindPorts)
                let difference = standardPorts.filter(x => bindPorts.indexOf(x) == -1).concat(bindPorts.filter(x => standardPorts.indexOf(x) == -1));
                console.log(difference)
                difference.map((item, index) => {
                    portBinds.push({port: item});
                })
                console.log(portBinds)
                res.data.data.portBinds = portBinds;

                this.setState({portsData : res.data.data.portBinds});
                if (res.data.data.pondName && res.data.data.pondId) {
                    this.setState({
                        bindPond: {
                            name: res.data.data.pondName,
                            id: res.data.data.pondId
                        }
                    })
                }
            } else {
                Toast.fail(res.data.msg, 1);
            }
        }).catch(error => {
            this.setState({ animating: false });
            Toast.fail('请求失败!', 1);
        })
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

    // 获得绑定和未绑定的端口信息
    getPorts = (portsData) => {
        return portsData.map((port, index) => {
            if (port.bindId) {
                return this.getBindedPort(port);
            } else {
                return this.getUnbindPort(port);
            }
        })
    }

    getBindedPort = (portData) => {
        return <div >
            <div className='prot-name-line' >
                <div className='left '>端口名称：端口{portData.port}（已绑定）</div>
                {(this.state.type == 1) && <div className='right unbinded' onClick={() => this.unlockEquipment(portData.port)} >
                    解绑
                </div>}
            </div>
            <div className='prot-info' >
                <div>设备绑定：{portData.bindName}</div>
                <div>设备端口：端口{portData.port}</div>
            </div>
        </div>
    }

    getUnbindPort = (portData) => {
        return <div className='prot-name-line' >
            <div className='left '>端口名称：端口{portData.port}（未绑定）</div>
            {(this.state.type == 1) && <div className='right binded' onClick={() => { this.bindEquipment(portData.port) }} >
                绑定
            </div>}
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
            sensorId: this.state.id,
            sensor_port: port
        }).then(res => {
            this.setState({ animating: false });
            if (res.data && res.data.code == 0) {
                Toast.success('解绑成功！', 1);
                this.setState({pickerValue: []})
                setTimeout(() => {
                    this.bindState();
                }, 1000);
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
        let data = { id: this.state.id, portIndex: port };
        data = JSON.stringify(data);
        this.props.history.push(`/bindEquipment/${data}`);
    }

     // 获得塘口的绑定信息
     getPond = () => {
        return <div className='port-content' >
            <div className='prot-name-line' >
                <div className='pondLeft'>绑定塘口</div>
                <div className='pondName' >
                {Object.keys(this.state.bindPond).length == 0 ?  (<Picker
                        title="选择地区"
                        extra="请选择(可选)"
                        data={this.state.ponds}
                        cols={1}
                        value={this.state.pickerValue}
                        onChange={v => this.setState({ pickerValue: v })}
                        onOk={v => this.setState({ pickerValue: v })}
                    >
                        <CustomChildren></CustomChildren>
                    </Picker>) : this.state.bindPond.name}
                </div>
                {Object.keys(this.state.bindPond).length == 0  ? 
                    (<div className='right binded' onClick={() => { this.bindPond()}} > 绑定 </div>) : 
                    (<div className='right unbinded' onClick={() => {this.unBindPond()}} > 解绑 </div>)
                    
                }
            </div>
        </div>
    }

    // 绑定渔塘
    bindPond = () => {
        const pickerValue = this.state.pickerValue;
        if (pickerValue.length < 1) {
            Toast.fail('请选择渔塘！', 1);
        } else {
            this.setState({ animating: true });
            pondWithSensorOrAIO({
                device_sn: this.state.device_sn,
                pondId: pickerValue[0],
                type: this.state.type
            }).then(res => {
                this.setState({ animating: false });
                if (res.data && res.data.code == 0) {
                    Toast.success('绑定成功！', 1);
                    this.setState({pickerValue: []})
                    setTimeout(() => {
                        this.bindState();
                    }, 1000);
                } else {
                    Toast.success(res.data.msg, 1);
                }
            }).catch(error => {
                this.setState({ animating: false });
                console.log(error)
            })
        }
    }

    // 解绑渔塘
    unBindPond = () => {
        const BUTTONS = ['解绑', '取消'];
        ActionSheet.showActionSheetWithOptions({
            options: BUTTONS,
            cancelButtonIndex: BUTTONS.length - 1,
            destructiveButtonIndex: BUTTONS.length - 2,
            message: '您是否确定解绑该渔塘？',
            maskClosable: true,
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
            device_sn: this.state.device_sn,
            type: this.state.type,
            pondId: this.bindPond.id
        }).then(res => {
            this.setState({ animating: false });
            if (res.data && res.data.code == 0) {
                Toast.success('解绑成功！', 1);
                this.setState({bindPond: {}});
                
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
        const state = this.state;
        const pondLine = this.getPond();
        const ports = this.getPorts(this.state.portsData);
        return <div className='equipment-management-bg' style={{ minHeight: window.document.body.clientHeight }}>
            <NavBar title={"设备管理"} />
            <div className='header-line' >
                <div className='name'>
                    传感器1(编号:88888888)
                </div>
                <div className='state' >
                    <img src={offline} />
                    <span className='offline'>
                        离线
                    </span>
                </div>
            </div>
            {(this.state.type != 0) && pondLine}
            {(this.state.type != 2) && <div className='port-content' >{ports}</div>}
            <ActivityIndicator
                toast
                text="Loading..."
                animating={this.state.animating}
            />
        </div>
    }
}

export default connect()(EquipmentManagement);
