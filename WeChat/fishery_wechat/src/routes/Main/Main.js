import React from 'react';
import './main.less'
import { Flex, Toast, List, Switch, Button, ActionSheet, ActivityIndicator } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import BottomTabBar from '../../components/TabBar';
import Accordion from '../../components/Accordion';
import online from '../../img/state-online.png';
import offline from '../../img/state-offline.png';
import fetch from 'dva/fetch';
import { connect } from 'dva';
import { wxQuery } from '../../services/pondManage.js'; //接口
import { getWeather } from '../../services/weather.js'; //接口
import { aeratorOnOff } from '../../services/oxygenControl.js'; //接口

class Main extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            animating: false,
            waterCheck1: false,
            waterCheck2: false,
            ponds: [],            //池塘列表
            temp: '** ~ ** ℃',   //天气的温度区间
            weatherIcon: '',      //天气的icon名称
        }
    }

    componentDidMount() {
        this.getLocation(); //获得当前地理位置
        this.queryPonds();
    }

    getLocation = () => {
        this.getWeather('320100');
    }

    queryPonds = () => {
        this.setState({ animating: true });
        wxQuery({
            relation: window.localStorage.getItem('relation'),
        }).then((res) => {
            this.setState({ animating: false });
            if (res.data.code == '0') {
                this.setState({ ponds: res.data.data })
            }
        }).catch((error) => {
            this.setState({ animating: false });
            console.log(error);
        });
    }

    getWeather = (city) => {
        getWeather({
            city: city
        }).then((res) => {
            if (res.status = 1) {
                let data = res.data;
                if (data.forecasts &&
                    data.forecasts.length >= 1 &&
                    data.forecasts[0].casts &&
                    data.forecasts[0].casts.length >= 1) {
                    let today = data.forecasts[0].casts[0];
                    let temp = today.nighttemp + ' ~ ' + today.daytemp + ' ℃';
                    let dayweather = this.getWeatherIconName(today.dayweather);
                    this.setState({ temp: temp, weatherIcon: dayweather });
                }
            }
        }).catch((error) => {
            console.log(error)
        });
    }

    getWeatherIconName = (dayweather) => {
        if (dayweather) {
            if (dayweather.indexOf('晴') >= 0) {
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
            return '';
        }
    }

    checkEquipment = (device_sn) => {
        this.props.dispatch({
            type: 'global/changeState',
            payload: {
                transitionName: 'left'
            }
        })
        this.props.history.push(`/sensorDetail/${device_sn}`);
    }

    changeAeratorOnOff = (device_sn, way, onOff, pondIndex, aioIndex) => {
        this.setState({ animating: true });
        aeratorOnOff({
            device_sn: device_sn,
            way: way,
            onOff: onOff
        }).then((res) => {
            this.setState({ animating: false });
            if (res.data.code == '0') {
                let ponds = this.state.ponds
                ponds[pondIndex].aio[aioIndex].openState = onOff;
                this.setState({ ponds: ponds })
                if (onOff) {
                    Toast.success('开启增氧机成功!', 1)
                } else {
                    Toast.success('关闭增氧机成功', 1)
                }
            }
        }).catch((error) => {
            this.setState({ animating: false });
            console.log(error);
        });
    }

    showActionSheet = (device_sn, way ,state) => {
        //两种情况   已经打开，和没有打开
        // let BOTTONS = [], title = ''
        // if (state) {
        //     BOTTONS = ['确认关闭', '取消', '自动增氧设置'];
            // title = '你是否确定关闭自动增氧？'
        // } else {
        //     BOTTONS = ['取消', '自动增氧设置'];
            // title = '你是否确定打开自动增氧？'
        // }
        const BUTTONS = ['确认关闭', '取消', '自动增氧设置'];
        ActionSheet.showActionSheetWithOptions({
            options: BUTTONS,
            cancelButtonIndex: BUTTONS.length - 1,
            destructiveButtonIndex: BUTTONS.length - 3,
            title: '你是否确定关闭自动增氧？',
            maskClosable: true,
            'data-seed': 'logId',
        }, (buttonIndex) => {
            if (buttonIndex == 2) {
                this.props.dispatch({
                    type: 'global/changeState',
                    payload: {
                        transitionName: 'left'
                    }
                })
                const data = {
                    device_sn: device_sn,
                    way: way
                }
                const str = JSON.stringify(data)
                this.props.history.push(`/autoOrxygenationSetting/${str}`);
            }
        });
    }

    getEquipment = (sensor) => {
        return <div className='equipment' key={sensor.id}  >
            <div onClick={() => this.checkEquipment(sensor.device_sn)} >
                <div className='line border-line' >
                    <div className='name' >
                        {sensor.name}
                    </div>
                    <div className={sensor.status ? 'right-text normal-state' : 'right-text unnormal-state'}>
                        {sensor.status ? '正常' : '异常'}
                    </div>
                </div>
                <div className='line' >
                    <div className='name' >
                        溶氧
                    </div>
                    <div className='right-text'>
                        {sensor.oxygen}
                    </div>
                </div>
                <div className='line' >
                    <div className='name' >
                        水温
                    </div>
                    <div className='right-text'>
                        {sensor.water_temperature}℃
                    </div>
                </div>

                <div className='line' >
                    <div className='name' >
                        PH值
                    </div>
                    <div className='right-text'>
                        {sensor.pH_value}
                    </div>
                </div>
            </div>
        </div>
    }

    getAioNode = (aio, pondIndex, aioIndex) => {
        return <div className='equipment' key={aio.id}  >
            <div >
                <div className='line border-line' >
                    <div className='name' >
                        {aio.name}
                    </div>
                    <div className={aio.status ? 'right-text normal-state' : 'right-text unnormal-state'}>
                        {aio.status ? '正常' : '异常'}
                    </div>
                </div>
                <div className='line' >
                    <div className='name' >
                        溶氧
                    </div>
                    <div className='right-text'>
                        {aio.oxygen}
                    </div>
                </div>
                <div className='line' >
                    <div className='name' >
                        水温
                    </div>
                    <div className='right-text'>
                        {aio.water_temperature}℃
                    </div>
                </div>

                <div className='line' >
                    <div className='name' >
                        PH值
                    </div>
                    <div className='right-text'>
                        {aio.pH_value}
                    </div>
                </div>
            </div>

            <div className='line' >
                <div className='name' >
                    增氧机（{aio.way}路）
                </div>
                <button className='auto-button do-auto' onClick={() => this.showActionSheet(aio.device_sn, aio.way, aio.timeState)} >定时</button>
                <Switch
                    nanme='watertem'
                    checked={aio.openState}
                    onClick={() => { this.changeAeratorOnOff(aio.device_sn, aio.way, !aio.openState, pondIndex, aioIndex) }}
                    className='state-switch'
                />
            </div>
        </div>
    }

    getPondsAccordion = () => {
        const ponds = this.state.ponds;
        return ponds.map((pond, pondIndex) => {
            // let equipemnts = this.getEquipment();
            let sensors = pond.sensors ? pond.sensors : [];
            let aios = pond.aios ? pond.aios : [];
            let sensorNode = sensors.map((sensor, index) => {
                return this.getEquipment(sensor);
            })
            let aioNode = aios.map((aio, aioIndex) => {
                return this.getAioNode(aio, pondIndex, aioIndex)
            })
            
            return (<Accordion title={pond.name ? pond.name : ''} key={pond.id} >
                {sensorNode}
                {aioNode}
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
            <ActivityIndicator
                toast
                text="Loading..."
                animating={this.state.animating}
            />
        </div>
    }
}

export default connect()(Main);
