import React from 'react';
import './main.less'
import { Flex, Toast, List, Switch, Button, ActionSheet } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import BottomTabBar from '../../components/TabBar';
import Accordion from '../../components/Accordion';
import online from '../../img/state-online.png';
import offline from '../../img/state-offline.png';
import fetch from 'dva/fetch';
import { pondQuery, pondEquipment } from '../../services/pondManage.js'; //接口
import { getWeather } from '../../services/weather.js'; //接口

class Main extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            waterCheck1: false,
            waterCheck2: false,
            ponds: [],
            temp: '** ~ ** ℃',
            weatherIcon: ''
        }
    }

    componentDidMount() {
        this.queryPonds();
        this.getWeather();
    }

    queryPonds = () => {
        pondQuery({
            page: 1,
            number: 99
        }).then((res) => {
            console.log(res);
            if (res.data.code == '0') {
                this.setState({ ponds: res.data.data })
            }
        }).catch((error) => { console.log(error) });
    }

    getWeather = () => {
        getWeather({
            city:'320100'
        }).then((res) => {
            if(res.status = 1) {
                let data = res.data;
                if( data.forecasts && data.forecasts.length >= 1 && data.forecasts[0].casts && data.forecasts[0].casts.length >= 1) {
                    console.log('2');
                    let today = data.forecasts[0].casts[0];
                    let temp = today.nighttemp + ' ~ ' + today.daytemp + ' ℃';
                    let dayweather = this.getWeatherIconName(today.dayweather);
                    this.setState({temp : temp, weatherIcon: dayweather});
                }
            }
        }).catch((error) => { console.log(error) });
    }

    getWeatherIconName = (dayweather) => {
        if(dayweather) {
            if(dayweather.indexOf('晴') >= 0) {
                return 'icon-tianqi1';
            } else if (dayweather.indexOf('云') >= 0) {
                return 'icon-tianqi2';
            } else if (dayweather.indexOf('雨') >= 0) {
                return 'icon-tianqizhenyu';
            } else if (dayweather.indexOf('雪') >= 0) {
                return 'icon-Icon';
            } else if (dayweather.indexOf('雨') >= 0 && dayweather.indexOf('雷') >= 0) {
                return 'icon-tianqi3';
            } else {
                return 'icon-tianqi1';
            }
        } else {
            return ''
        }
    }



    queryEquipment = () => {

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

    getPondsAccordion = () => {
        console.log('here')
        const ponds = this.state.ponds;
        return ponds.map((pond, index) => {
            return (<Accordion title={pond.name ? pond.name : ''} key={pond.id} >
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
            </Accordion>)
        })
    }

    render() {

        const ponds = this.getPondsAccordion();
        return <div className='main-bg' style={{ minHeight: window.document.body.clientHeight }}>
            <div className='weather-div'>
                <i className={`weather-icon iconfont ${this.state.weatherIcon}`}> </i> 
                {this.state.temp}
            </div>
            <div className='fishpond-item'>
                {ponds}
            </div>
            <BottomTabBar nowTab={1} />
        </div>
    }
}

export default withRouter(Main);
