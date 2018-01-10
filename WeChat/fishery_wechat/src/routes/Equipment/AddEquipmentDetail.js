import React from 'react';
import { List, InputItem } from 'antd-mobile';
import './addEquipmentDetail.less'
import { connect } from 'dva';
import { withRouter, Link } from "react-router-dom";
import { addEquipment } from '../../services/equipment.js'; //接口
import NavBar from '../../components/NavBar';
const Item = List.Item;

class BindEquipment extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            name: '',
        }
    }

    doAddEquipment = () => {
        console.log(this.state.name)
        addEquipment({
            name: this.state.name,
            device_sn: this.props.match.device_sn,
            
        })  
    }

    inputChange = (value) => {
        value = value.replace( / /g, '');  //remove all black
        this.setState({name: value})
    }

    render() {
        return <div className='add-equipment-detail-bg' style={{ minHeight: window.document.body.clientHeight }}>
            <NavBar title={"添加设备"} />
            <div className='row' >
                <div className='key' >
                    设备编号
                </div>
                <div className='value'>
                    8832323
                </div>
            </div>
            <div className='row' >
                <div className='key' >
                    设备名称<span style={{color:'red'}} >*</span>
                </div>
                <div  className='value' >
                    <input className='value-input' placeholder='请输入设备名称' value={this.state.name} onChange={(e) => {this.inputChange(e.target.value)}}
                    />
                </div>
            </div>

            <div className={'add-btn ' + (this.state.name ? '' : 'disable')} onClick={() => {this.doAddEquipment()}} >添  加</div>
        </div>
    }
}

export default connect()(BindEquipment);
