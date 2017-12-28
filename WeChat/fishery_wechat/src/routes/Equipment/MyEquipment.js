import React from 'react';
import './myEquipment.less'
import { Flex, Toast, List, Button, Modal } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import Accordion from '../../components/Accordion';
import offline from '../../img/equ_link_off.png'
import online from '../../img/equ_link_on.png'
const alert = Modal.alert;

class MyEquipment extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            isEdit: false
        }
    }

    edit = () => {
        this.setState({ isEdit: !this.state.isEdit })
    }



    wouldDelete = () => {
        console.log('wouldDelete');
        alert('删除', '确定删除该设备吗？', [
            { text: '取消', onPress: () => console.log('取消') },
            { text: '确定', onPress: () => this.doDelete() },
          ])
    }

    doDelete = () => {
        console.log('doDelete');
    }

    addEquipment = () => {
        console.log('add');
    }

    render() {
        return <div className='my-equipment-bg' style={{ minHeight: window.document.body.clientHeight }}>
            <div className="nav-bar-title">
                <i className="back" onClick={() => { history.back() }}></i>
                我的设备
                <i className={this.state.isEdit ? 'right-item-none' : "edit"} onClick={this.edit} >{this.state.isEdit && "取消"}</i>
            </div>
            <div className='equipment-type'>
                <Accordion title='传感器' isShowState={false} isShow={true} >
                    <div className='equipment'>
                        <div className='line' >
                            {this.state.isEdit && <div className='delete-button' onClick={this.wouldDelete} >
                            </div>}
                            <div className='name' >
                                传感器01
                            </div>
                            <div className='right-imgs'>
                                <div className='online' >1
                                </div>
                                <div className='offline' >2
                                </div>
                            </div>
                        </div>
                        <div className='line' >
                            {this.state.isEdit && <div className='delete-button' onClick={this.wouldDelete} >
                            </div>}
                            <div className='name' >
                                传感器02
                            </div>
                            <div className='right-imgs'>
                                <div className='online' >1
                                </div>
                                <div className='offline' >2
                                </div>
                            </div>
                        </div>
                    </div>
                </Accordion>
                <Accordion title='控制器' isShowState={false} isShow={true} >
                    <div className='equipment'>
                        <div className='line' >
                            {this.state.isEdit && <div className='delete-button' onClick={this.wouldDelete} >
                            </div>}
                            <div className='name' >
                                控制器01
                    </div>
                            <div className='right-imgs'>
                                <div className='online' >1
                        </div>
                                <div className='offline' >2
                        </div>
                                <div className='offline' >3
                        </div>
                                <div className='offline' >4
                        </div>

                            </div>
                        </div>
                        <div className='line' >
                            {this.state.isEdit && <div className='delete-button' onClick={this.wouldDelete} >
                            </div>}
                            <div className='name' >
                                控制器02
                            </div>
                            <div className='right-imgs'>
                                <div className='online' >1
                                </div>
                                <div className='offline' >2
                                </div>
                                <div className='offline' >3
                                </div>
                                <div className='offline' >4
                                </div>

                            </div>
                        </div>
                    </div>
                </Accordion>
                <Accordion title='一体机' isShowState={false} isShow={true} >
                    <div className='equipment'>
                        <div className='line' >
                            {this.state.isEdit && <div className='delete-button' onClick={this.wouldDelete} >
                            </div>}
                            <div className='name' >
                                一体机01
                            </div>
                            <div className='right-imgs'>
                                <div className='online' >
                                </div>
                            </div>
                        </div>
                        <div className='line' >
                            {this.state.isEdit && <div className='delete-button' onClick={this.wouldDelete} >
                            </div>}
                            <div className='name' >
                                一体机02
                            </div>
                            <div className='right-imgs'>
                                <div className='offline' >
                                </div>
                            </div>
                        </div>
                    </div>
                </Accordion>
            </div>
            <div className='add-button' onClick={this.addEquipment} >
            </div>
        </div>
    }
}

export default withRouter(MyEquipment);
