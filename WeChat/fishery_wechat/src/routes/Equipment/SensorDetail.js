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

const cols = {
    'ph': { min: 0 },
    'receiveTime': {}
};
const oCols = {
    'o': { min: 0 },
    'receiveTime': {}
};

const waterCols = {
    '温度': { min: 0 },
    'receiveTime': {}
};

class SensorDetail extends React.Component {

    constructor(props) {
        super(props)
        const device_sn = this.props.match.params.device_sn;
        const way = this.props.match.params.way;
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

    getDataSevenday = () => {
        this.setState({ animating: true })
        getDataSevenday({
            device_sn: this.state.device_sn,
            way: this.state.way,
        }).then((res) => {
            this.setState({ animating: false })
            if (res.data && res.data.code == 0) {
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

    serverCheck = () => {   //校准
        console.log('calibration');
        console.log(this.state.device_sn);
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
                {this.state.name + (this.state.way != 0 ? ('(' + this.state.way + '路)') : '')}
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

                    // onVisibleChange={this.handleVisibleChange}
                    onSelect={this.onSelect}
                >
                    <div style={{
                        height: '100%',
                        padding: '0 15px',
                        marginRight: '-15px',
                        display: 'flex',
                        maxHeight:'.3rem',
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
                 <img src={correct} className='correct' onClick={() => {this.serverCheck()}} />}
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
