import React from 'react';
import './bindEquipment.less'
import { Picker, List } from 'antd-mobile'
import { createForm } from 'rc-form';
import { connect } from 'dva';
import { withRouter } from "react-router-dom";
import NavBar from '../../components/NavBar';
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
            bindEquipment: '',
            bindPort: '',
        }
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

            <div className='save-button' onClick={() => { this.saveInfo() }} >
                保  存
            </div>
        </div>
    }
}

export default connect()(BindEquipment);
