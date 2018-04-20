import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Row, Col, Card, Input, Icon, Button, Table, Radio, DatePicker, message } from 'antd';
const { RangePicker } = DatePicker;
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { TimelineChart } from '../../components/Charts';
import Trend from '../../components/Trend';
import NumberInfo from '../../components/NumberInfo';
import { getDataToday, getDataAll, getRealTimeData } from '../../services/equipment';
import { Chart, Geom, Axis, Tooltip, Legend, Coord } from 'bizcharts';
import ReactEcharts from 'echarts-for-react';
import moment from 'moment';
import numeral from 'numeral';

const now = new Date();
const todayStr = formartDate(now);
const yesterdayStr = formartDate(new Date(now - 24 * 60 * 60 * 1000));
const beforeYesterdayStr = formartDate(new Date(now - 2 * 24 * 60 * 60 * 1000));
now.setDate(now.getDate() + 1);
const tomorrowStr = formartDate(now);
function formartDate(date) {

    let year = date.getFullYear();
    let month = (((date.getMonth() + 1) < 10) ? "0" + (date.getMonth() + 1) : (date.getMonth() + 1));
    let day = ((date.getDate() < 10) ? "0" + date.getDate() : date.getDate());

    return year + '/' + month + '/' + day + ' 00:00:00';
}

export default class WaterQualityCurve extends React.Component {

    constructor(props) {
        super(props);
        const data = JSON.parse(this.props.match.params.data);
        this.state = {
            oxygens: [],
            phs: [],
            temperatures: [],
            device_sn: data.device_sn,
            name: data.deviceName,
            status: data.status,
            selectTime: 'today',
            way: data.way,
            up_limit: 10,
            low_limit: 5,
            high_limit: 15
        }
    }

    componentDidMount() {
        this.getDataToday();
        this.getRealTimeData();
    }

    getRealTimeData = () => {
        getRealTimeData({
            device_sn: this.state.device_sn,
            way: this.state.way
        }).then((res) => {
            if (res && res.code == 0) {
                const data = res.data;
                const high = res.high_limit;
                const up = res.up_limit;
                const low = res.low_limit;
                if (data) {
                    this.setState({
                        up_limit: up,
                        low_limit: low,
                        high_limit: high
                    });
                }
            } else {
                message.error(res.msg, 1);
            }
        }).catch((error) => {
            message.error('请求失败', 1);
            console.log(error)
        });
    }

    getDataToday = () => {
        getDataToday({
            device_sn: this.state.device_sn,
            way: this.state.way
        }).then((res) => {
            if (res && res.code == 0) {
                this.setState({
                    oxygens: this.TransformationData(res.oxygens, 'oxygens'),
                    phs: this.TransformationData(res.phs, 'phs'),
                    temperatures: this.TransformationData(res.temperatures, 'temperatures')
                })
            } else {
                message.error(res.msg, 1);
            }
        }).catch((error) => {
            message.error('请求失败', 1);
            console.log(error)
        });
    }

    TransformationData = (data, type) => {
        let realData = [];
        let length = 0;
        if (type == 'oxygens') {
            data.map((item, index) => {
                if (item.oxygen) {
                    realData[length] = {
                        name: item.receiveTime,
                        value: [item.receiveTime, item.oxygen]
                    }
                    length++;
                } else {
                    length++;
                }
            })
        } else if (type == 'phs') {
            data.map((item, index) => {
                if (item.ph) {
                    realData[length] = {
                        name: item.receiveTime,
                        value: [item.receiveTime, item.ph]
                    }
                    length++;
                } else {
                    length++;
                }
            })
        } else if (type == 'temperatures') {
            data.map((item, index) => {
                if (item.temperature) {
                    realData[length] = {
                        name: item.receiveTime,
                        value: [item.receiveTime, item.temperature]
                    }
                    length++;
                } else {
                    length++;
                }
            })
        }
        // console.log(realData);
        return realData;
    }

    getDataSevent = () => {
        getDataAll({
            device_sn: this.state.device_sn,
            way: this.state.way
        }).then((res) => {
            if (res && res.code == 0) {
                this.setState({
                    oxygens: this.TransformationData(res.oxygens, 'oxygens'),
                    phs: this.TransformationData(res.phs, 'phs'),
                    temperatures: this.TransformationData(res.temperatures, 'temperatures')
                })
            } else {
                message.error(res.msg, 1);
            }
        }).catch((error) => {
            message.error('请求失败', 1);
            console.log(error)
        });
    }

    handleTimeChange = (value) => {
        this.setState({ selectTime: value });
        if (value == 'today') {
            this.getDataToday();
        } else if (value == 'sevent') {
            this.getDataSevent()
        }
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
            data = this.state.phs;
            title = "PH变化曲线";
        } else if (type == 'oxygens') {
            data = this.state.oxygens;
            title = "溶氧变化曲线";
        } else if (type == 'temperatures') {
            data = this.state.temperatures;
            title = "水温变化曲线";
        }
        let option = {
            // title: {
            //     text: title,
            //     left: "10%"
            // },
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
                name: 'data',
                type: 'line',
                // showSymbol: false,
                symbolSize: 3,
                hoverAnimation: false,
                data: data,
                smooth: true
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
        if (type == 'oxygens') {
            option.series = [{
                name: 'data',
                type: 'line',
                // showSymbol: false,
                symbolSize: 3,
                hoverAnimation: false,
                data: data,
                smooth: true
            },
            {
                name:'.anchor',
                type:'line', 
                showSymbol:false, 
                data:anchor,
                itemStyle:{normal:{opacity:0}},
                lineStyle:{normal:{opacity:0}}
            },{
                name:'up_limit',
                type:'line', 
                markLine: {
                    silent: true,
                    data: [{
                        yAxis: this.state.up_limit
                    }],
                    symbolSize: 0,
                    label: {
                        show: false
                    },
                    lineStyle: {
                        type: 'solid',
                        color: 'blue'
                    }
                }
            },{
                name:'low_limit',
                type:'line', 
                markLine: {
                    silent: true,
                    data: [{
                        yAxis: this.state.low_limit
                    }],
                    symbolSize: 0,
                    label: {
                        show: false
                    },
                    lineStyle: {
                        type: 'solid',
                        color: 'red'
                    }
                }
            },{
                name:'high_limit',
                type:'line', 
                markLine: {
                    silent: true,
                    data: [{
                        yAxis: this.state.high_limit
                    }],
                    symbolSize: 0,
                    label: {
                        show: false
                    },
                    lineStyle: {
                        type: 'solid',
                        color: 'green'
                    }
                }
            }]
        }
        return option;
    }
    
    render() {
        return (
            <PageHeaderLayout >
                <Card bordered={false}>
                    <Row style={{ fontSize: 17 }}>
                        <Col span={10}>设备名称: &nbsp;&nbsp; {this.state.name}（编号:{this.state.device_sn}）</Col>
                        <Col span={5}>设备状态: &nbsp; {this.state.status}</Col>
                    </Row>
                    <Row style={{ marginTop: 20 }}>
                        <Col span={7}>
                            <Radio.Group value={this.state.selectTime} onChange={e => this.handleTimeChange(e.target.value)} >
                                <Radio.Button value="today" >今日</Radio.Button>
                                <Radio.Button value="sevent">七日</Radio.Button>
                            </Radio.Group>
                        </Col>
                    </Row>
                    <Row style={{ fontSize: 25, paddingTop: 25 }}>
                        PH变化曲线
                    </Row>
                    <Row style={{ padding: 30, paddingTop: 0 }}>
                        <Col span={20}>
                            {this.state.phs && this.state.phs.length > 0 ?
                                <ReactEcharts option={this.getOption('phs', this.state.selectTime === 'today')} height={500}/>
                                :
                                <span>暂无数据</span>}
                        </Col>
                    </Row>
                    <Row style={{ fontSize: 25, paddingTop: 25 }}>
                        溶氧变化曲线
                    </Row>
                    <Row style={{ padding: 30, paddingTop: 0 }}>
                        <Col span={20}>
                            {(this.state.oxygens && this.state.oxygens.length > 0) ?
                                <ReactEcharts option={this.getOption('oxygens', this.state.selectTime === 'today')} height={500} />
                                : <span>暂无数据</span>}
                        </Col>
                    </Row>
                    <Row style={{ fontSize: 25, paddingTop: 25 }}>
                        水温变化曲线
                    </Row>
                    <Row style={{ padding: 30, paddingTop: 0  }}>
                        <Col span={20}>
                            {(this.state.temperatures && this.state.temperatures.length > 0) ?
                                <ReactEcharts option={this.getOption('temperatures', this.state.selectTime === 'today')} height={500} />
                                : <span>暂无数据</span>}
                        </Col>
                    </Row>
                </Card>
                <Button type="primary" style={{ float: 'right' }} onClick={() => { history.back() }}>
                    返回上一页
                </Button>
            </PageHeaderLayout>
        );
    }
}
