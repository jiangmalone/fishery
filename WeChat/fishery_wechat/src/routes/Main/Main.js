import React from 'react';
import './main.less'
import { Flex, Toast, List, Switch, Button } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import BottomTabBar from '../../components/TabBar';
import Accordion from '../../components/Accordion';
import online from '../../img/state-online.png';
import offline from '../../img/state-offline.png';
class Main extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            waterCheck1: false,
            waterCheck2: false,

        }
    }

    render() {
        return <div className='bg' style={{ minHeight: window.document.body.clientHeight }} >
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
                                传感器1
                            </div>
                            <button className='auto-button do-auto'>自动</button>
                            <Switch
                                nanme='watertem'
                                checked={this.state.waterCheck1}
                                onClick={(checked) => { console.log(checked); this.setState({waterCheck1: !this.state.waterCheck1}) }}
                                className='state-switch'
                            />
                        </div>

                        <div className='line' >
                            <div className='name' >
                            传感器2
                            </div>
                            <button className='auto-button no-auto'>自动</button>
                            {/* <Switch className='state-switch'></Switch> */}
                            <Switch
                                nanme='watertem'
                                checked={this.state.waterCheck2}
                                onClick={(checked) => { console.log(checked); this.setState({waterCheck2: !this.state.waterCheck2}) }}
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
