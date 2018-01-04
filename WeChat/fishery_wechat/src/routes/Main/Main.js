import React from 'react';
import './main.less'
import { Flex, Toast, List, Switch, Button, ActionSheet } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import BottomTabBar from '../../components/TabBar';
import Accordion from '../../components/Accordion';
import online from '../../img/state-online.png';
import offline from '../../img/state-offline.png';

import { queryPonds, pondEquipment } from '../../services/pond.js'; //接口

class Main extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            waterCheck1: false,
            waterCheck2: false,
            ponds: [],
        }
    }

    componentDidMount() {
        this.queryPonds();
    }

    queryPonds = () => {
        queryPonds({
            page: 1,
            number: 99
        }).then((res) => {
            console.log(res);
            if (res.data.code == '0') {

            }
        }).catch((error) => { console.log(error) });
    }

    showActionSheet = () => {
        const BUTTONS = ['确认关闭', '取消', '自动增氧设置'];
        ActionSheet.showActionSheetWithOptions({
            options: BUTTONS,
            cancelButtonIndex: BUTTONS.length - 1,
            destructiveButtonIndex: BUTTONS.length - 3,
            title: '你是否确定关闭自动增氧？',
            maskClosable: true,
            'data-seed': 'logId',
        },
            (buttonIndex) => {
                console.log(buttonIndex);
                this.setState({ clicked: BUTTONS[buttonIndex] });
            });
    }

    render() {

        const ponds = this.state.ponds.map((pond, index) => {
            
        } );
        return <div className='main-bg' style={{ minHeight: window.document.body.clientHeight }}>
            <div className='weather-div'>
                <i className='weather-icon iconfont icon-tianqi'> </i>
                10-12℃
            </div>
            <div className='fishpond-item'>
                <Accordion title='小渔塘'>
                    <div className='equipment'>
                        <div className='line border-line' >
                            <div className='name' >
                                传感器1
                            </div>
                            <div className='right-text normal-state'>
                                正常
                            </div>
                        </div>
                        <div className='line' >
                            <div className='name' >
                                溶氧
                            </div>
                            <div className='right-text'>
                                10.25
                            </div>
                        </div>
                        <div className='line' >
                            <div className='name' >
                                水温
                            </div>
                            <div className='right-text'>
                                25.6℃
                            </div>
                        </div>

                        <div className='line' >
                            <div className='name' >
                                PH值
                            </div>
                            <div className='right-text'>
                                7
                            </div>
                        </div>

                        <div className='line' >
                            <div className='name' >
                                控制器1
                            </div>
                            <button className='auto-button do-auto' onClick={this.showActionSheet} >自动</button>
                            <Switch
                                nanme='watertem'
                                checked={this.state.waterCheck1}
                                onClick={(checked) => { console.log(checked); this.setState({ waterCheck1: !this.state.waterCheck1 }) }}
                                className='state-switch'
                            />
                        </div>

                        <div className='line' >
                            <div className='name' >
                                控制器2
                            </div>
                            <button className='auto-button no-auto'>自动</button>
                            {/* <Switch className='state-switch'></Switch> */}
                            <Switch
                                nanme='watertem'
                                checked={this.state.waterCheck2}
                                onClick={(checked) => { console.log(checked); this.setState({ waterCheck2: !this.state.waterCheck2 }) }}
                                className='state-switch'
                            />
                        </div>
                    </div>
                </Accordion>
                <Accordion title='小渔塘'>
                    <div className='equipment'>
                        <div className='line border-line' >
                            <div className='name' >
                                传感器1
                            </div>
                            <div className='right-text normal-state'>
                                正常
                            </div>
                        </div>
                        <div className='line' >
                            <div className='name' >
                                溶氧
                            </div>
                            <div className='right-text'>
                                10.25
                            </div>
                        </div>
                        <div className='line' >
                            <div className='name' >
                                水温
                            </div>
                            <div className='right-text'>
                                25.6℃
                            </div>
                        </div>
                    </div>
                </Accordion>
            </div>
            <BottomTabBar nowTab={1} />
        </div>
    }
}

export default withRouter(Main);
