import React from 'react';
import './main.less'
import { Flex, Toast, List, Switch, Button, ActionSheet, ActivityIndicator, Modal } from 'antd-mobile'
import { Map } from 'react-amap';
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
import equipment, { autoSet } from '../../services/equipment.js'; //接口
import isEmpty from '../../utils/isEmpty';

const alert = Modal.alert;
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
            isEmpty: false,
        }
    }

    componentDidMount() {
        if(isEmpty(window.localStorage.getItem('openid'))||isEmpty(window.localStorage.getItem('relation'))){
            window.location.href="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx9871d8699143d59e&redirect_uri=http%3a%2f%2fwww.fisherymanager.net%2fapi%2fwebService%2fwechatlogin%3fhtmlPage%3dmain%26isAuth%3dtrue&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect"+'?v='+(new Date().getTime());

            // window.location.reload();
        }
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
                let equipmentNum = 0;
                if (res.data.data.length == 0) {
                    this.setState({ isEmpty: true });
                } else {
                    this.setState({ isEmpty: false });
                }
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

    checkEquipment = (device_sn, way) => {
        this.props.dispatch({
            type: 'global/changeState',
            payload: {
                transitionName: 'left'
            }
        })
        this.props.history.push(`/sensorDetail/${device_sn}/${way}`);
    }

    sureChangeAeretorOnOff = (device_sn, way, openOrclose, pondIndex, aioIndex) => {
        let content = '';
        if (openOrclose) {
            content = '确认打开增氧机？';
        } else {
            content = '确认关闭增氧机？';
        }
        alert('提示', content, [
            { text: '取消', onPress: () => console.log('cancel') },
            {
                text: '确定',
                onPress: () => {
                    this.changeAeratorOnOff(device_sn, way, openOrclose, pondIndex, aioIndex)
                },
            },
        ])
    }

    changeAeratorOnOff = (device_sn, way, openOrclose, pondIndex, aioIndex) => {
        this.setState({ animating: true });
        aeratorOnOff({
            device_sn: device_sn,
            way: way,
            openOrclose: openOrclose ? 1 : 0
        }).then((res) => {
            this.setState({ animating: false });
            if (res.data.code == '0') {
                let ponds = this.state.ponds
                ponds[pondIndex].aio[aioIndex].on_off = openOrclose;
                this.setState({ ponds: ponds })
                if (openOrclose) {
                    Toast.success('开启增氧机成功!', 1)
                } else {
                    Toast.success('关闭增氧机成功', 1)
                }
            } else {
                Toast.fail(res.data.msg, 1)
            }
        }).catch((error) => {
            if (openOrclose) {
                Toast.error('开启增氧机失败!', 1)
            } else {
                Toast.error('关闭增氧机失败', 1)
            }
            this.setState({ animating: false });
            console.log(error);
        });
    }

    showActionSheet = (device_sn, way, state, pondIndex, aioIndex) => {
        //两种情况   已经打开，和没有打开
        let BUTTONS = [], title = '', cancelButtonIndex
        if (state) {
            BUTTONS = ['确认关闭', '取消', '自动增氧设置'];
            cancelButtonIndex = BUTTONS.length - 1;
            title = '你是否确定关闭自动增氧？'
        } else {
            BUTTONS = ['取消', '自动增氧设置'];
            cancelButtonIndex = 0;
            title = '你是否确定打开自动增氧？'
        }
        ActionSheet.showActionSheetWithOptions({
            options: BUTTONS,
            cancelButtonIndex: cancelButtonIndex,
            destructiveButtonIndex: BUTTONS.length - 3,
            title: title,
            maskClosable: true,
            'data-seed': 'logId',
        }, (buttonIndex) => {
            if (buttonIndex == (BUTTONS.length - 1)) {
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
            } else if (state && buttonIndex == 0) {
                this.closeTimeOrxygen(device_sn, way, pondIndex, aioIndex);
            }
        });
    }

    getStatusString = (status) => {
        switch (status) {
            case 0:
                return '正常';
            case 1:
                return '离线';
            case 2:
                return '断电';
            case 3:
                return '缺相';
            case 4:
                return '数据异常';
            default:
                return '异常';
        }
    }

    closeTimeOrxygen = (device_sn, way) => {
        autoSet({
            limit_Install: {
                device_sn: device_sn,
                way: way
            },
            timers: []
        }).then(res => {
            if (res.data.code == 0) {
                let ponds = this.state.ponds
                ponds[pondIndex].aio[aioIndex].timed = true;
                this.setState({ ponds: ponds })
                Toast.success('关闭定时增氧成功!', 1);
            } else {
                Toast.fail(res.data.msg, 1);
            }
        }).catch(error => {
            console.log(error);
            Toast.fail('关闭失败，请重试!', 1);
        })
    }

    getEquipment = (sensor) => {

        return <div className='equipment' key={sensor.id}  >
            <div onClick={() => this.checkEquipment(sensor.device_sn, 0)} >
                <div className='line border-line' >
                    <div className='name' >
                        {sensor.name}
                    </div>
                    <div className={sensor.status ? 'right-text unnormal-state' : 'right-text normal-state'}>
                        {this.getStatusString(sensor.status)}
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
            <div onClick={() => this.checkEquipment(aio.device_sn, aio.way)}>
                <div className='line border-line' >
                    <div className='name' >
                        {aio.name}
                    </div>
                    <div className={aio.wayStatus ? 'right-text unnormal-state' : 'right-text normal-state'}>
                        {this.getStatusString(aio.wayStatus)}
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
                    增氧机({aio.way}路)
                </div>
                <button className={aio.timed ? 'auto-button do-auto' : 'auto-button'} onClick={() => this.showActionSheet(aio.device_sn, aio.way, aio.timed, pondIndex, aioIndex)} >定时</button>
                <Switch
                    nanme='watertem'
                    checked={aio.onoff}
                    onClick={() => { this.sureChangeAeretorOnOff(aio.device_sn, aio.way, !aio.onoff, pondIndex, aioIndex) }}
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
            return (<Accordion title={pond.name ? pond.name : ''} key={pond.id} isShow={true} isOnline={pond.pondstatus} >
                {sensorNode}
                {aioNode}
            </Accordion>)
        })
    }

    gotoAddPond = () => {
        this.props.dispatch({
            type: 'global/changeState',
            payload: {
                transitionName: 'left'
            }
        })
        this.props.history.push(`/addPond`);
    }

    render() {
        const ponds = this.getPondsAccordion();
        return <div className='main-bg' style={{ minHeight: window.document.body.clientHeight }}>
            <div className='weather-div'>
                <div>
                    <i className={`weather-icon iconfont ${this.state.weatherIcon}`}> </i>
                    {this.state.temp}
                </div>

                <Button style={{color: '#35b4e8', float: 'right', width: 100, height: 30}} onClick={this.queryPonds} icon >数据刷新</Button>
            </div>
            {!this.state.isEmpty ? <div className='fishpond-item'>
                {ponds}
            </div> : <div className='nodata' onClick={this.gotoAddPond} >
                    <div className='img-404' />
                    <span className='add-span' >您还没有添加鱼塘呢~</span>
                    <div className='img-add' ></div>
                </div>}
            <BottomTabBar nowTab={1} />
            <ActivityIndicator
                toast
                text="Loading..."
                animating={this.state.animating}
            />
            <Modal
                visible={this.state.modalShow}
                transparent
                maskClosable={true}
                onClose={() => this.onCloseModal()}
                title="提示"
                footer={[{ text: '知道了', onPress: () => { this.onCloseModal() } }]}
            >

            </Modal>
        </div>
    }
}

export default connect()(Main);
