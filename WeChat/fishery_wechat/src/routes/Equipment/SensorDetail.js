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
import { getDataToday, getRealTimeData, myEquipment, getDataSevenday, serverCheck } from '../../services/equipment.js'; //接口

const todayTimes = ["00:00", "03:00",
    "06:00", "09:00",
    "12:00", "15:00",
    "18:00", "21:00", "24:00"];

class SensorDetail extends React.Component {

    constructor(props) {
        super(props)
        const device_sn = this.props.match.params.device_sn;
        const way = this.props.match.params.way;

        const now = new Date();
        const last = new Date(now - 24 * 60 * 60 * 1000);
        const lastlast = new Date(now - 2 * 24 * 60 * 60 * 1000)
        const nowStr = (((now.getMonth() + 1) < 10)?"0"+ (now.getMonth() + 1): (now.getMonth() +1)) +'-' +  ((now.getDate() < 10)?"0"+ now.getDate() : now.getDate());
        const lastStr = (((last.getMonth() +1) < 10)?"0"+ (last.getMonth() + 1): (last.getMonth() +1 )) +'-' +  ((last.getDate() < 10)?"0"+ last.getDate() : last.getDate());
        const lastlastStr = (((lastlast.getMonth() + 1) < 10)?"0"+ (lastlast.getMonth() + 1): (lastlast.getMonth() +1 )) +'-' +  ((lastlast.getDate() < 10)?"0"+ lastlast.getDate() : lastlast.getDate());
        let ary = [nowStr, lastStr, lastlastStr];

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
            way: way,
            name: '',
            status: '',
            cols: {
                'ph': { min: 0 },
                'receiveTime': { ticks: todayTimes }
            },
            oCols: {
                'oxygen': { min: 0 },
                'receiveTime': { ticks: todayTimes }
            },
            waterCols: {
                'temperature': { min: 0 },
                'receiveTime': { ticks: todayTimes }
            },
            colsT: {
                'ph': { min: 0 },
                'receiveTime': { ticks: ary }
            },
            oColsT: {
                'oxygen': { min: 0 },
                'receiveTime': { ticks: ary }
            },
            waterColsT: {
                'temperature': { min: 0 },
                'receiveTime': { ticks: ary }
            }

        }
    }

    componentDidMount() {
        this.getDataToday();
        this.getRealTimeData();
        this.getAllEquipment();
        window.scrollTo(0, 0);
    }

    getDataToday = () => {
        this.setState({ animating: true })
        getDataToday({
            device_sn: this.state.device_sn,
            way: this.state.way
        }).then((res) => {
            this.setState({ animating: false })
            if (res.data && res.data.code == 0) {
                console.log(typeof res.data.oxygens[0]);
                this.setState({
                    oData: this.formartTodayData(res.data.oxygens),
                    phData: this.formartTodayData(res.data.phs),
                    waterData: this.formartTodayData(res.data.temperatures)
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

    formartTodayData = (data) => {
        let formartData = data;
        let times = ["00:00", "03:00",
            "06:00", "09:00",
            "12:00", "15:00",
            "18:00", "21:00", "24:00"];

        for (let i = 1; i < formartData.length; i++) {
            if (formartData[i - 1]["receiveTime"] > times[0]) {
                times.shift();
            } else if (formartData[i - 1]["receiveTime"] < times[0] && formartData[i]["receiveTime"] > times[0]) {
                if (formartData[i]["ph"]) {
                    let value = (formartData[i - 1]["ph"] + formartData[i]["ph"]) / 2;
                    let item = { 'ph': value, 'receiveTime': times[0] };
                    formartData.splice(i, 0, item);
                    i++;
                    times.shift();
                } else if (formartData[i]["oxygen"]) {
                    let value = (formartData[i - 1]["oxygen"] + formartData[i]["oxygen"]) / 2;
                    let item = { 'oxygen': value, 'receiveTime': times[0] };
                    formartData.splice(i, 0, item);
                    i++;
                    times.shift();
                } else if (formartData[i]["temperature"]) {
                    let value = (formartData[i - 1]["temperature"] + formartData[i]["temperature"]) / 2;
                    let item = { 'temperature': value, 'receiveTime': times[0] };
                    formartData.splice(i, 0, item);
                    i++;
                    times.shift();
                }
            }
        }
        return formartData;
    }

    getDataSevenday = () => {
        this.setState({ animating: true })
        getDataSevenday({
            device_sn: this.state.device_sn,
            way: this.state.way,
        }).then((res) => {
            this.setState({ animating: false })
            if (res.data && res.data.code == 0) {

                this.getColsData(res.data.oxygens, res.data.phs, res.data.temperatures);
                this.setState({
                    oData: res.data.oxygens,
                    phData: res.data.phs,
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

    getColsData = (oxygens, phs, temperatures) => {
        let timesAry = [];
        oxygens.map((item, index) => {
            if (item.receiveTime.length < 7) {
                timesAry.push(item.receiveTime + '');
            }
        })
        let cols = this.state.colsT;
        let oCols = this.state.oColsT;
        let waterCols = this.state.waterColsT;
        cols.receiveTime = { ticks: timesAry }
        oCols.receiveTime = { ticks: timesAry }
        waterCols.receiveTime = { ticks: timesAry }
        console.log(cols)
        this.setState({
            colsT: cols,
            oColsT: oCols,
            waterColsT: waterCols
        }, () => {
            this.setState({
                oData: oxygens,
                phData: phs,
                waterData: temperatures
            })
        })
    }

    getRealTimeData = () => {
        this.setState({ animating: true })
        getRealTimeData({
            device_sn: this.state.device_sn,
            way: this.state.way
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
            relation: window.localStorage.getItem('relation'),
        }).then((res) => {
            this.setState({ animating: false })
            if (res.data && res.data.code == 0) {
                const data = res.data;
                let equipments = [];
                if (data.sensor && data.sensor.length > 0) {
                    data.sensor.map((item, index) => {
                        equipments.push({
                            name: item.name,
                            device_sn: item.device_sn + '0'
                        })
                    })
                }
                if (data.aio && data.aio.length > 0) {
                    data.aio.map((item, index) => {
                        equipments.push({
                            name: item.name + '(1路)',
                            device_sn: item.device_sn + '1',
                        })
                        equipments.push({
                            name: item.name + '(2路)',
                            device_sn: item.device_sn + '2',
                        })
                    })
                }
                this.setState({ sensors: equipments })
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
        this.setState({
            device_sn: opt.props.value.substr(0, opt.props.value.length - 1),
            isShowMore: false,
            isSelectToday: true,
            way: opt.props.value.charAt(opt.props.value.length - 1)
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
            this.setState({
                isSelectToday: !this.state.isSelectToday,
            }, () => {
                if (this.state.isSelectToday) {
                    let cols = this.state.cols;
                    let oCols = this.state.oCols;
                    let waterCols = this.state.waterCols;
                    cols.receiveTime = { ticks: todayTimes }
                    oCols.receiveTime = { ticks: todayTimes }
                    waterCols.receiveTime = { ticks: todayTimes }
                    this.setState({
                        cols: cols,
                        oCols: oCols,
                        waterCols: waterCols
                    })
                    this.getDataToday();
                } else {
                    this.getDataSevenday();
                }
            })
        }
    }

    serverCheck = () => {   //校准
        serverCheck({
            device_sn: this.state.device_sn,
            way: this.state.way,
        }).then(res => {
            if (res.data && res.data.code == 0) {
                Toast.success('校准成功', 1);
            } else {
                Toast.fail(res.data.msg, 1);
            }
        }).catch(error => {
            Toast.fail('校准失败', 1);
            console.log(error)
        })
    }

    render() {
        console.log(this.state.waterColsT)
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
                <span>
                    {this.state.name + ((this.state.way != 0) ? ('-' + this.state.way + '路') : '')}
                </span>
                <i className="right-item-none list" onClick={() => {
                    this.setState({ isShowMore: !this.state.isShowMore })
                }} ></i>
                <Popover
                    mask
                    overlayStyle={{ color: 'currentColor', position: 'fixed' }}
                    visible={this.state.isShowMore}
                    overlay={overlayAry}
                    align={{
                        overflow: { adjustY: 0, adjustX: 0 },
                        offset: [-20, -10],
                    }}
                    onSelect={this.onSelect}
                >
                    <div style={{
                        height: '100%',
                        padding: '0 15px',
                        marginRight: '-15px',
                        display: 'flex',
                        maxHeight: '.3rem',
                        alignItems: 'center',
                    }}
                    >
                    </div>
                </Popover>
            </div>

            <div className='state-head'  >
                <div className='state-div' onClick={this.changeDetailShowState}>
                    <img src={this.state.status == 0 ? online : offline} style={{ marginLeft: 0 }} />
                    <span>最新数据</span>
                    <Icon type={this.state.isShowDetail ? 'up' : 'down'} className='icon' ></Icon>
                </div>
                {(this.state.way != 0 && this.state.status == 0) &&
                    <img src={correct} className='correct' onClick={() => { this.serverCheck() }} />}
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
                    三 日
                </div>
            </div>
            <div className='chart-div'>
                <p>PH变化曲线</p>
                <Chart height={400} data={this.state.phData} scale={this.state.isSelectToday ? this.state.cols : this.state.colsT} forceFit>
                    <Axis name="time" />
                    <Axis name="ph" />
                    <Tooltip crosshairs={{ type: "y" }} />
                    <Geom type="line" position="receiveTime*ph" size={2} shape={'smooth'} />
                </Chart>
            </div>
            <div className='chart-div'>
                <p>溶氧变化曲线</p>
                <Chart height={400} data={this.state.oData} scale={this.state.isSelectToday ? this.state.oCols: this.state.oColsT} forceFit>
                    <Axis name="time" />
                    <Axis name="o" />
                    <Tooltip crosshairs={{ type: "y" }} />
                    <Geom type="line" position="receiveTime*oxygen" size={2} shape={'smooth'} />
                </Chart>
            </div>
            <div className='chart-div'>
                <p>水温变化曲线</p>
                <Chart height={400} data={this.state.waterData} scale={this.state.isSelectToday ? this.state.waterCols : this.state.waterColsT} forceFit>
                    <Axis name="time" />
                    <Axis name="温度" />
                    <Tooltip crosshairs={{ type: "y" }} />
                    <Geom type="line" position="receiveTime*temperature" size={2} shape={'smooth'} />
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
