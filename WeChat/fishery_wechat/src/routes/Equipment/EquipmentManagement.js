import React from 'react';
import './equipmentManagement.less'
import { ActionSheet } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import offline from '../../img/equ_offline.png'
import online from '../../img/equ_online.png'
import NavBar from '../../components/NavBar';

class EquipmentManagement extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            isEdit: false
        }
    }

    unlockEquipment = () => {
        const BUTTONS = ['解绑', '取消'];
        ActionSheet.showActionSheetWithOptions({
            options: BUTTONS,
            cancelButtonIndex: BUTTONS.length - 1,
            destructiveButtonIndex: BUTTONS.length - 2,
            // title: 'title',
            message: '您是否确定解绑该端口？',
            maskClosable: true,
            'data-seed': 'mypond',
            // wrapProps,
        },
        (buttonIndex) => {
            console.log(buttonIndex)
            this.setState({ clicked: BUTTONS[buttonIndex] });
        });
    }

    lockEquipment = () => {
        console.log('lockEquipment');
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
                    <div className='right unbinded' onClick={this.unlockEquipment} >
                    解绑
                    </div>
                </div>
                <div className='prot-info' >
                    <div>设备绑定：控制器2</div>
                    <div>设备端口：端口2</div>
                </div>
                <div className='prot-name-line' >
                    <div className='left '>端口名称：端口2（未绑定）</div>
                    <div className='right binded'>
                    绑定
                    </div>
                </div>
            </div>
        </div>
    }
}

export default withRouter(EquipmentManagement);