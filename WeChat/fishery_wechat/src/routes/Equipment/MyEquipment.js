import React from 'react';
import './myEquipment.less'
import { Flex, Toast, List, Button, } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import Accordion from '../../components/Accordion';
import offline from '../../img/equ_link_off.png'
import online from '../../img/equ_link_on.png'


class MyEquipment extends React.Component {

    constructor(props) {
        super(props)
        this.state = {

        }
    }

    render() {
        return <div className='my-equipment-bg' style={{ minHeight: window.document.body.clientHeight }}>
            <div className="nav-bar-title">
                <i className="back" onClick={() => { history.back() }}></i>
                我的设备
                <i className="edit"></i>
            </div>
            <div className='equipment-type'>
                <Accordion title='传感器' isShowState={false} isShow={true} >
                    <div className='equipment'>
                        <div className='line' >
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
                            <div className='name' >
                            一体机01
                            </div>
                            <div className='right-imgs'>
                                <div className='online' >
                                </div>
                            </div>
                        </div>
                        <div className='line' >
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
            <div className='add-button'>
            </div>
        </div>
    }
}

export default withRouter(MyEquipment);
