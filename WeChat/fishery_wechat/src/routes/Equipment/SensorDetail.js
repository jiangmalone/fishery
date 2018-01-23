import React from 'react';
import './sensorDetail.less'
import { Popover, Icon, NavBar, List, ActivityIndicator, Toast } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import moment from 'moment';
import offline from '../../img/equ_offline.png'
import { connect } from 'dva'
import online from '../../img/equ_online.png'
const Item = Popover.Item;
// import btn_list from '../../img/btn_list.png';
import correct from '../../img/btn_correct.png';
import back_img from '../../img/back.png';
import { Chart, Geom, Axis, Tooltip, Legend, Coord } from 'bizcharts';
import { getDataToday, getRealTimeData, myEquipment, getDataSevenday } from '../../services/equipment.js'; //接口

const cols = {
    'ph': { min: 0 },
    'receiveTime': { }
};
const oCols = {
    'o': { min: 0 },
    'receiveTime': {}
};

const waterCols = {
    '温度': { min: 0 },
    'receiveTime': { }
};

class SensorDetail extends React.Component {

    constructor(props) {
        super(props)
        const device_sn = this.props.match.params.device_sn;
        this.state = {
            animating: false,
            realTimeData: {
                oxygen: '',
                water_temperature: '',
                pH_value: ''
            },
            visible: false,
            isShowMore: false,
            isShowDetail: false,
            isSelectToday: true,
            phData: [],
            oData: [],
            waterData: [],
            sensors: [],
            //以下是设备的一些信息
            device_sn: device_sn,
            name: '传感器',
            status: '',
        }
    }

    componentDidMount() {
        this.getDataToday();
        this.getRealTimeData();
        this.getAllEquipment();
    }

    // getTime = (type) => {       //0 今天   1近七天
    //     let today = new Date(), startTime, endTime = moment(today).format('YYYY-MM-DD HH:mm');
    //     today.setHours(0);
    //     today.setMinutes(0);
    //     today.setSeconds(0);
    //     today.setMilliseconds(0);
    //     if (type == 0) {
    //         startTime = moment(today).format('YYYY-MM-DD HH:mm');
    //     } else {
    //         let times = (Date.parse(today) / 1000) - (24 * 60 * 60 * 6);
    //         let newDate = new Date();
    //         startTime = moment(newDate.setTime(times * 1000)).format('YYYY-MM-DD HH:mm');
    //     }
    //     return { startTime, endTime }
    // }


    getDataToday = () => {
        this.setState({ animating: true })
        getDataToday({
            device_sn: this.state.device_sn,
        }).then((res) => {
            console.log(res);
            this.setState({ animating: false })
            if (res.data && res.data.code == 0) {
                this.setState({
                    oData : res.data.oxygens,
                    phData : res.data.phs,
                    waterData: res.data.temperatures
                })
            } else {
                Toast.fail(res.data.msg, 1);
            }
        }).catch((error) => {
            this.setState({ animating: false });
            Toast.fail('请求失败', 1);
            console.log(error)
        });
    }

    getDataSevenday = () => {
        this.setState({ animating: true })
        getDataToday({
            device_sn: this.state.device_sn,
        }).then((res) => {
            console.log(res);
            this.setState({ animating: false })
            if (res.data && res.data.code == 0) {
                this.setState({
                    oData : res.data.oxygens,
                    phData : res.data.phs,
                    waterData: res.data.temperatures
                })
            } else {
                Toast.fail(res.data.msg, 1);
            }
        }).catch((error) => {
            this.setState({ animating: false });
            Toast.fail('请求失败', 1);
            console.log(error)
        });
    }

    getRealTimeData = () => {
        this.setState({ animating: true })
        getRealTimeData({
            device_sn: this.state.device_sn,
        }).then((res) => {
            this.setState({ animating: false })
            if (res.data && res.data.code == 0) {
                const data = res.data.data;
                if (data) {
                    this.setState({ realTimeData: data });
                }
                this.setState({
                    name: res.data.name,
                    status: res.data.status
                })
            } else {
                Toast.fail(res.data.msg, 1);
            }
        }).catch((error) => {
            this.setState({ animating: false });
            Toast.fail('请求失败', 1);
            console.log(error)
        });
    }

    getAllEquipment = () => {
        this.setState({ animating: true })
        myEquipment({
            relationId: 'WX4',
        }).then((res) => {
            this.setState({ animating: false })
            if (res.data && res.data.code == 0) {
                const data = res.data;
                if (data.sensor && data.sensor.length > 0) {
                    let sensors = [];
                    data.sensor.map((item, index) => {
                        sensors.push({
                            name: item.name,
                            device_sn: item.device_sn
                        })
                    })
                    this.setState({ sensors: sensors })
                }
            } else {
                Toast.fail(res.data.msg, 1);
            }
        }).catch((error) => {
            this.setState({ animating: false });
            Toast.fail('请求失败', 1);
            console.log(error);
        });
    }

    onSelect = (opt) => {
        console.log(opt.props.value);
        this.setState({
            device_sn: opt.props.value,
            isShowMore: false,
            isSelectToday: true,
        }, () => {
            this.getDataToday();
            this.getRealTimeData();
        });
    };

    handleVisibleChange = (isShowMore) => {
        this.setState({
            isShowMore,
        });
    };

    changeDetailShowState = () => {
        this.setState({
            isShowDetail: !this.state.isShowDetail
        })
    }

    selectTime = (state) => {
        if (state == this.state.isSelectToday) {
            return;
        } else {
            if (!this.state.isSelectToday) {
                // this.getData(this.getTime(0));
                this.getDataToday();
            } else {
                // this.getData(this.getTime(1));
                this.getDataSevenday();
            }
            this.setState({
                isSelectToday: !this.state.isSelectToday,
            })
        }
    }

    calibration = () => {   //校准
        console.log('calibration');
        console.log(this.state.device_sn);
    }

    render() {
        const overlayAry = [];
        const sensors = this.state.sensors;
        sensors.map((sensor, index) => {
            let item = <Item key={sensor.device_sn} value={sensor.device_sn} >
                {sensor.name}
            </Item>
            overlayAry.push(item);
        })
        return (<div className='sensorDetail-bg' >
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
                {this.state.name}
                <i className="right-item-none list" onClick={() => {
                    this.setState({ isShowMore: !this.state.isShowMore })
                }} ></i>
            </div>
            <Popover
                mask
                overlayClassName="fortest"
                overlayStyle={{ color: 'currentColor' }}
                visible={this.state.isShowMore}
                overlay={overlayAry}
                align={{
                    overflow: { adjustY: 0, adjustX: 0 },
                    offset: [-26, 50],
                }}
                onVisibleChange={this.handleVisibleChange}
                onSelect={this.onSelect}
            >
                <div style={{
                    height: '100%',
                    padding: '0 15px',
                    marginRight: '-15px',
                    display: 'flex',
                    alignItems: 'center',
                }}
                >
                </div>
            </Popover>
            <div className='state-head'  >
                <div className='state-div' onClick={this.changeDetailShowState}>
                    <img src={this.state.status == 0 ? offline : online} style={{ marginLeft: 0 }} />
                    <span>当前状态</span>
                    <Icon type={this.state.isShowDetail ? 'up' : 'down'} className='icon' ></Icon>
                </div>
                <img src={correct} className='correct' onClick={() => {this.calibration()}} />
            </div>
            {this.state.isShowDetail && <div className='detail'>
                <div>实时溶氧：&nbsp;&nbsp; {this.state.realTimeData.oxygen}</div>
                <div>实时水温：&nbsp;&nbsp; {this.state.realTimeData.water_temperature}℃</div>
                <div>实时PH值：&nbsp;&nbsp; {this.state.realTimeData.pH_value}</div>
            </div>}

            <div className='button-line' >
                <div className={(this.state.isSelectToday ? 'selected' : '') + ' left'} onClick={() => this.selectTime(true)} >
                    今 日
                </div>
                <div className={(!this.state.isSelectToday ? 'selected' : '') + ' right'} onClick={() => this.selectTime(false)}  >
                    七 日
                </div>
            </div>
            <div className='chart-div'>
                <p>PH变化曲线</p>
                <Chart height={400} data={this.state.phData} scale={cols} forceFit>
                    <Axis name="time" />
                    <Axis name="ph" />
                    <Tooltip crosshairs={{ type: "y" }} />
                    <Geom type="line" position="receiveTime*ph" size={2} />
                    <Geom type='point' position="receiveTime*ph" size={4} shape={'circle'} style={{ stroke: '#fff', lineWidth: 1 }} />
                </Chart>
            </div>
            <div className='chart-div'>
                <p>溶氧变化曲线</p>
                <Chart height={400} data={this.state.oData} scale={oCols} forceFit>
                    <Axis name="time" />
                    <Axis name="o" />
                    <Tooltip crosshairs={{ type: "y" }} />
                    <Geom type="line" position="receiveTime*oxygen" size={2} />
                    <Geom type='point' position="receiveTime*oxygen" size={4} shape={'circle'} style={{ stroke: '#fff', lineWidth: 1 }} />
                </Chart>
            </div>
            <div className='chart-div'>
                <p>水温变化曲线</p>
                <Chart height={400} data={this.state.waterData} scale={waterCols} forceFit>
                    <Axis name="time" />
                    <Axis name="温度" />
                    <Tooltip crosshairs={{ type: "y" }} />
                    <Geom type="line" position="receiveTime*temperature" size={2} />
                    <Geom type='point' position="receiveTime*temperature" size={4} shape={'circle'} style={{ stroke: '#fff', lineWidth: 1 }} />
                </Chart>
            </div>
            <ActivityIndicator
                toast
                text="Loading..."
                animating={this.state.animating}
            />
        </div>);
    }
}

export default connect()(SensorDetail);
