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
        this.state = {
            animating: false,
            equipments: [],   //设备列表
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
            relationId: 'WX4',
        }).then((res) => {
            this.setState({animating: false});
            console.log(res);
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

    doBindEquipment = () => {
        let data = this.props.match.params.data;
        data = JSON.parse(data); //{{equipmentId: this.props.match.params.equipmentId, port: data.port}}
        sensorWithController({
            sensorId: 0,
            sensor_port: 0,
            controllerId: 0,
            controller_port: 0
        }).then(res => {

        }).catch(error => {
            console.log(error);
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
                    端口1
                </div>
            </div>
            <div className='bind-info' >
                <List>
                    <Picker 
                    data={testData} 
                    cols={1} 
                    className="forss" 
                    extra="请选择"
                    onOk={e => this.setState({ bindEquipment: e })}
                    value={this.state.bindEquipment}
                    >
                        <List.Item arrow="horizontal" key='1'>绑定设备：</List.Item>
                    </Picker>
                    <Picker 
                    data={testData} 
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
