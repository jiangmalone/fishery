import React from 'react';
import './bindEquipment.less'
import { Picker, List, ActivityIndicator, Toast } from 'antd-mobile'
import { createForm } from 'rc-form';
import { connect } from 'dva';
import { withRouter } from "react-router-dom";
import NavBar from '../../components/NavBar';
import { queryEquipment, myEquipment } from '../../services/equipment.js';         //接口
import { sensorWithController } from '../../services/bind.js'; //接口
const testData = [
    {
        label: '2013',
        value: '2013',
    },
    {
        label: '2014',
        value: '2014',
    },
]
class BindEquipment extends React.Component {

    constructor(props) {
        super(props)
        let data = this.props.match.params.data;
        data = JSON.parse(data);

        this.state = {
            animating: false,
            id: data.id,
            portIndex: data.portIndex,
            controllers: [],   //设备列表
            ports: [],      //和设备联动的端口号
            bindEquipment: '',
            bindPort: '',
        }
    }

    componentDidMount() {
        this.queryEquipment();
    }

    queryEquipment = () => {
        this.setState({animating: true});
        myEquipment({
            relationId: window.localStorage.getItem('relationId'),
        }).then((res) => {
            this.setState({animating: false});
            console.log(res);
            if (res.data && res.data.code == 0) {
                if (res.data.controller) {
                    const data = res.data.controller;
                    let controllers = [];
                    // 组装可用的端口 
                    data.map((item, index) => {
                        let controller = {}, usefulPorts = []
                        const allPorts = item.port_status.split('');
                        // console.log(allPorts);
                        allPorts.map((post, index) => {
                            const portNum = index + 1;
                            if (post == 0) {
                                let postData = {
                                    label: ('端口' + portNum),
                                    value: portNum,
                                };
                                usefulPorts.push(postData);
                            }
                        })
                        // console.log(usefulPorts);
                        controller = {label: item.name, value: item.id, ports: usefulPorts};
                        controllers.push(controller);
                    })
                    this.setState({controllers : controllers})
                }
            } else {
                Toast.fail(res.data.msg, 1);
            }
        }).catch((error) => {
            this.setState({ animating: false });
            Toast.fail('请求失败!', 1);
            console.log(error)
        });
    }

    doBindEquipment = () => {
        if (this.state.bindEquipment.length <= 0) {
            Toast.info('请选择控制器!', 1);
            return;
        }
        if (this.state.bindPort.length <= 0) {
            Toast.info('请选择端口!', 1);
            return;
        }
        let data = this.props.match.params.data;
        data = JSON.parse(data); //{{equipmentId: this.props.match.params.equipmentId, port: data.port}}
        this.setState({animating: true});
        sensorWithController({
            sensorId: this.state.id,
            sensor_port: this.state.portIndex,
            controllerId: this.state.bindEquipment[0],
            controller_port: this.state.bindPort[0]
        }).then(res => {
            this.setState({animating: false});
            if (res.data && res.data.code == 0) {
                Toast.success('绑定成功！', 1);
                setTimeout(() => {
                    this.props.dispatch({
                        type: 'global/changeState',
                        payload: {
                            transitionName: 'right'
                        }
                    })
                    history.back();
                }, 1000);
            } else {
                Toast.fail(res.data.msg, 1)
            }
        }).catch(error => {
            this.setState({animating: false});
            Toast.success('绑定失败！', 1)
            console.log(error);
        })
    }

    selectController = (id) => {
        console.log(this.state.controllers)
        const controllers = this.state.controllers;
        controllers.map((controller, index) => {
            if(controller.value == id) {
                this.setState({ports: controller.ports})
            }
        })
    }

    render() {
        return <div className='bind-equipment-bg' style={{ minHeight: window.document.body.clientHeight }}>
            <NavBar title={"设备绑定"} />
            <div className='header-line' >
                <div className='left'>
                    端口名称
                </div>
                <div className='name' >
                    端口{this.state.portIndex}
                </div>
            </div>
            <div className='bind-info' >
                <List>
                    <Picker 
                    data={this.state.controllers} 
                    cols={1} 
                    className="forss" 
                    extra="请选择"
                    onOk={e => {this.setState({ bindEquipment: e })}}
                    onChange={v => {this.selectController(v)}}
                    value={this.state.bindEquipment}
                    >
                        <List.Item arrow="horizontal" key='1'>绑定设备：</List.Item>
                    </Picker>
                    <Picker 
                    data={this.state.ports} 
                    cols={1} 
                    className="forss" 
                    extra="请选择"
                    onOk={e => this.setState({ bindPort: e })}
                    value={this.state.bindPort}
                    >
                        <List.Item arrow="horizontal" key='1'>绑定端口：</List.Item>
                    </Picker>
                </List>
            </div>
            <div className='save-button' onClick={() => { this.doBindEquipment() }} >
                保  存
            </div>
            <ActivityIndicator
                toast
                text="Loading..."
                animating={this.state.animating}
            />
        </div>
    }
}

export default connect()(BindEquipment);
