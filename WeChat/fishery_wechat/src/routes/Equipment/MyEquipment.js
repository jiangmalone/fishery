import React from 'react';
import './myEquipment.less'
import { Flex, Toast, List, Button, Modal, ActionSheet } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import { connect } from 'dva';
import Accordion from '../../components/Accordion';
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

    showActionSheet = () => {
        
    }

    wouldDelete = () => {
        const BUTTONS = ['删除', '取消'];
        ActionSheet.showActionSheetWithOptions({
            options: BUTTONS,
            cancelButtonIndex: BUTTONS.length - 1,
            destructiveButtonIndex: BUTTONS.length - 2,
            // title: 'title',
            message: '您是否确定删除该设备？',
            maskClosable: true,
            'data-seed': 'mypond',
            // wrapProps,
        },
        (buttonIndex) => {
            console.log(buttonIndex)
            this.setState({ clicked: BUTTONS[buttonIndex] });
        });
    }

    doDelete = () => {
        console.log('doDelete');
    }

    addEquipment = () => {
        this.props.dispatch({
            type: 'global/changeState',
            payload: {
                transitionName: 'left'
            }
        })
        this.props.history.push('/addEquipment');
    }

    render() {
        return <div className='my-equipment-bg' style={{ minHeight: window.document.body.clientHeight }}>
            <div className="nav-bar-title">
            <i className="back" onClick={() => {
                        history.back();
                        this.props.dispatch({
                            type: 'global/changeState',
                            payload: {
                                transitionName: 'right'
                            }
                        })
                    }}></i>
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

export default connect()(MyEquipment);
