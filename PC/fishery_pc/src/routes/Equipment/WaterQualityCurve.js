import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Row, Col, Card, Input, Icon, Button, Table, Radio, DatePicker, message } from 'antd';
const { RangePicker } = DatePicker;
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { TimelineChart } from '../../components/Charts';
import Trend from '../../components/Trend';
import NumberInfo from '../../components/NumberInfo';
import { getDataToday, getDataAll } from '../../services/equipment';
import { Chart, Geom, Axis, Tooltip, Legend, Coord } from 'bizcharts';
import moment from 'moment';
import numeral from 'numeral';
const chartData = [];
const cols = {
    'ph': { min: 0 },
    'receiveTime': {tickCount: 18}
};
const oCols = {
    'o': { min: 0 },
    'receiveTime': {tickCount: 18}
};

const waterCols = {
    '温度': { min: 0 },
    'receiveTime': {tickCount: 18},
    // sales:{
    //     type:"linear",
    //     tickCount:10,
    //   },
};
for (let i = 0; i < 20; i += 1) {
    chartData.push({
        x: (new Date().getTime()) + (1000 * 60 * 30 * i),
        y1: Math.floor(Math.random() * 10) + 10,
    });
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
            way: data.way
        }
    }

    componentDidMount() {
        this.getDataToday();
    }

    getDataToday = () => {
        getDataToday({
            device_sn: this.state.device_sn,
            way: this.state.way
        }).then((res) => {
            if (res && res.code == 0) {
                this.setState({
                    oxygens: res.oxygens,
                    phs: res.phs,
                    temperatures: res.temperatures
                })
            } else {
                message.error(res.msg, 1);
            }
        }).catch((error) => {
            message.error('请求失败', 1);
            console.log(error)
        });
    }

    getDataSevent = () => {
        getDataAll({
            device_sn: this.state.device_sn,
            way: this.state.way
        }).then((res) => {
            if (res && res.code == 0) {
                // let oxygens = [], phs = [], temperatures = [];
                // res.oxygens.map((oxygen, index) => {
                //     oxygens.push({
                //         // x: new Date(Date.parse(oxygen.receiveTime.replace(/-/g, "/"))),
                //         x: new Date(oxygen.receiveTime),
                //         y1: oxygen.oxygen
                //     })
                // })
                // res.phs.map((ph, index) => {
                //     phs.push({
                //         // x: new Date(Date.parse(ph.receiveTime.replace(/-/g, "/"))),
                //         x: new Date(ph.receiveTime),
                //         y1: ph.ph
                //     })
                // })
                // res.temperatures.map((temperature, index) => {
                //     temperatures.push({
                //         // x: new Date(Date.parse(temperature.receiveTime.replace(/-/g, "/"))),
                //         x: new Date(temperature.receiveTime),
                //         y1: temperature.temperature
                //     })
                // })
                this.setState({
                    oxygens: res.oxygens,
                    phs: res.phs,
                    temperatures: res.temperatures
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
                                {/* <Radio.Button value="small">区间</Radio.Button> */}
                            </Radio.Group>
                        </Col>

                        {/* <Col> */}
                        {/* <RangePicker /> */}
                        {/* </Col> */}

                    </Row>
                    <Row style={{ fontSize: 25, paddingTop: 25 }}>
                        PH变化曲线
                    </Row>
                    <Row style={{ padding: 30, paddingTop: 0 }}>
                        <Col span={20}>
                            {this.state.phs && this.state.phs.length > 0 ?
                                //  <TimelineChart
                                //     height={300}
                                //     data={this.state.phs}
                                //     titleMap={{ y1: 'PH值' }}
                                // /> 
                                <Chart height={400} data={this.state.phs} scale={cols} forceFit>
                                    <Axis name="time" />
                                    <Axis name="ph" />
                                    <Tooltip crosshairs={{ type: "y" }} />
                                    <Geom type="line" position="receiveTime*ph" size={2} shape={'smooth'} />
                                    {/* <Geom type='point' position="receiveTime*ph" size={4} shape={'circle'} style={{ stroke: '#fff', lineWidth: 1 }} /> */}
                                </Chart>
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
                                //  <TimelineChart
                                //     height={300}
                                //     data={this.state.oxygens}
                                //     titleMap={{ y1: '溶氧' }}
                                // />
                                <Chart height={400} data={this.state.oxygens} scale={oCols} forceFit>
                                    <Axis name="time" />
                                    <Axis name="o" />
                                    <Tooltip crosshairs={{ type: "y" }} />
                                    <Geom type="line" position="receiveTime*oxygen" size={2} shape={'smooth'} />
                                    {/* <Geom type='point' position="receiveTime*oxygen" size={4} shape={'circle'} style={{ stroke: '#fff', lineWidth: 1 }} /> */}
                                </Chart>
                                : <span>暂无数据</span>}
                        </Col>
                    </Row>
                    <Row style={{ fontSize: 25, paddingTop: 25 }}>
                        水温变化曲线
                    </Row>
                    <Row style={{ padding: 30 }}>
                        <Col span={20}>
                            {(this.state.temperatures && this.state.temperatures.length > 0) ?
                                //  <TimelineChart
                                //     height={300}
                                //     data={this.state.temperatures}
                                //     titleMap={{ y1: '水温' }}
                                // /> 
                                <Chart height={400} data={this.state.temperatures} scale={waterCols} forceFit>
                                    <Axis name="time" />
                                    <Axis name="温度" />
                                    <Tooltip crosshairs={{ type: "y" }} />
                                    <Geom type="line" position="receiveTime*temperature" size={2} shape={'smooth'} />
                                    {/* <Geom type='point' position="receiveTime*temperature" size={4} shape={'circle'} style={{ stroke: '#fff', lineWidth: 1 }} /> */}
                                </Chart>
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
