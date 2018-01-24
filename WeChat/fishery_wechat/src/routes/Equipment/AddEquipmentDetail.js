import React from 'react';
import { List, InputItem, Toast } from 'antd-mobile';
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
        addEquipment({
            name: this.state.name,
            device_sn: this.props.match.params.equipmentCode,
            relationId: window.localStorage.getItem('relationId'),
        }).then((res) => {
            console.log(res);
            if (res.data.code == 0) {
                Toast.success('新增设备成功', 1);
                setTimeout(() => {
                    this.props.dispatch({
                        type: 'global/changeState',
                        payload: {
                            transitionName: 'right'
                        }
                    })
                    this.props.history.push('/myEquipment');
                }, 1000);
            } else {
                Toast.fail(res.data.msg, 1);
            }
        }).catch((error) => {
            Toast.fail('新增设备失败', 1);
            console.log(error); 
        });
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
                    {this.props.match.params.equipmentCode ? this.props.match.params.equipmentCode : ''}
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
