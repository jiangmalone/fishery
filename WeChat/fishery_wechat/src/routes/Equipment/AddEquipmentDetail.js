import React from 'react';
import './addEquipmentDetail.less'
import { connect } from 'dva';
import { withRouter } from "react-router-dom";
import NavBar from '../../components/NavBar';

class BindEquipment extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            name: '',
        }
    }

    doAddEquipment = () => {

    }

    render() {
        return <div className='add-equipment-detail-bg' style={{ minHeight: window.document.body.clientHeight }}>
            <NavBar title={"添加设备"} />
            <div className='line' >
                <div className='left'>
                    设备编号
                </div>
                <div className='name' >
                    99996666
                </div>
            </div>
            <div className='line' >
                <div className='left'>
                    设备编号
                </div>
                <div className='name' >
                    99996666
                </div>
            </div>
            
            <div className='save-button' onClick={() => { this.saveInfo() }} >
                保  存
            </div>
        </div>
    }
}

export default connect()(BindEquipment);
