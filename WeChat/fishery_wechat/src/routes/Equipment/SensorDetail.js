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
import ReactEcharts from 'echarts-for-react';


const now = new Date();
const todayStr = formartDate(now);
const yesterdayStr = formartDate(new Date(now - 24 * 60 * 60 * 1000));
const beforeYesterdayStr = formartDate(new Date(now - 2 * 24 * 60 * 60 * 1000));
now.setDate(now.getDate() + 1);
const tomorrowStr = formartDate(now);
function formartDate(date) {
    
    let year = date.getFullYear();
    let month = (((date.getMonth() + 1) < 10)?"0"+ (date.getMonth() + 1): (date.getMonth() +1));
    let day = ((date.getDate() < 10)?"0"+ date.getDate() : date.getDate());
    
     return year + '/' + month + '/' + day + ' 00:00:00';
}
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
            status: ''
        }
       
    }

    componentDidMount() {
        this.getDataToday();
        this.getRealTimeData();
        this.getAllEquipment();
        window.scrollTo(0, 0);
    }

    formartDate = (date) => {
    
        let year = date.getFullYear();
        let month = (((date.getMonth() + 1) < 10)?"0"+ (date.getMonth() + 1): (date.getMonth() +1));
        let day = ((date.getDate() < 10)?"0"+ date.getDate() : date.getDate());
        
         return year + '/' + month + '/' + day + ' 00:00:00';
    }
    
    getOption = (type, isSelectToday) => {
        let data;
        let anchor;
        let title = ''
        if (isSelectToday) {
            anchor = [
                {name:todayStr, value:[todayStr, 0]},
                {name:tomorrowStr, value:[tomorrowStr, 0]}
                ];
        } else {
            anchor = [
                {name:beforeYesterdayStr, value:[beforeYesterdayStr, 0]},
                {name:yesterdayStr, value:[yesterdayStr, 0]},
                {name:todayStr, value:[todayStr, 0]},
                {name:tomorrowStr, value:[tomorrowStr, 0]}
            ]
        }
        if (type == 'phs') {
            data = this.state.phData;
            title = "PH变化曲线";
        } else if (type == 'oxygens') {
            data = this.state.oData;
            title = "溶氧变化曲线";
        } else if (type == 'temperatures') {
            data = this.state.waterData;
            title = "水温变化曲线";
        }
        return {
            title: {
                text: title,
                left: "10%"
            },
            tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                    params = params[0];
                    var date = new Date(params.name);
                    return date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear() + ' : ' + params.value[1];
                },
                axisPointer: {
                    animation: false
                }
            },
            grid: {
                left: '10%',
                right: '10%',
                bottom: '10%',
                containLabel: true
            },
            xAxis: {
                type: 'time',
                splitLine: {
                    show: false
                }
            },
            yAxis: {
                type: 'value',
                boundaryGap: [0, '100%'],
                splitLine: {
                    show: false
                }
            },
            series: [{
                name: '模拟数据',
                type: 'line',
                // showSymbol: false,
                symbolSize: 3,
                hoverAnimation: false,
                data: data,
                smooth: true,
                // lineStyle:{
                //     color: '#1E90FF'
                // }
            },
            {
                name:'.anchor',
                type:'line', 
                showSymbol:false, 
                data:anchor,
                itemStyle:{normal:{opacity:0}},
                lineStyle:{normal:{opacity:0}}
            }]
        }
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
                    oData: this.TransformationData(res.data.oxygens, 'oxygens'),
                    phData: this.TransformationData(res.data.phs, 'phs'),
                    waterData: this.TransformationData(res.data.temperatures, 'temperatures')
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

    TransformationData = (data, type) => {
        let realData = [];
        let length = 0;
        if (type == 'oxygens') {
            data.map((item, index) => {
                if(item.oxygen) {
                    realData[length]= {
                                        name: item.receiveTime,
                                        value:[item.receiveTime, item.oxygen]}
                    length ++;
                } else {
                    length ++ ;
                }
            })
        } else if (type == 'phs') {
            data.map((item, index) => {
                if(item.ph) {
                    realData[length] = {
                        name: item.receiveTime,
                         value:[item.receiveTime, item.ph]}
                         length ++;
                } else {
                    length ++;
                }
            })
        } else if (type == 'temperatures') {
            data.map((item, index) => {
                if(item.temperature) {
                    realData[length] = {
                        name: item.receiveTime,
                         value:[item.receiveTime, item.temperature]}
                         length ++;
                } else {
                    length ++;
                }
            })
        }
        return realData;
        
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
                    oData: this.TransformationData(res.data.oxygens, 'oxygens'),
                    phData: this.TransformationData(res.data.phs, 'phs'),
                    waterData: this.TransformationData(res.data.temperatures, 'temperatures')
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
            this.setState({
                isSelectToday: !this.state.isSelectToday,
            }, () => {
                if (this.state.isSelectToday) {
                   
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
                <ReactEcharts option={this.getOption('phs', this.state.isSelectToday)} height={400} title="haha" />
            </div>
            <div className='chart-div'>
                <ReactEcharts option={this.getOption('oxygens', this.state.isSelectToday)} height={400} />
            </div>
            <div className='chart-div'> 
                <ReactEcharts option={this.getOption('temperatures', this.state.isSelectToday)} height={400} />
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
