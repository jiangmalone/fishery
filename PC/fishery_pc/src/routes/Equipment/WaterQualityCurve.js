import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Row, Col, Card, Input, Icon, Button, Table, Radio, DatePicker, message } from 'antd';
const {RangePicker} = DatePicker;
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { TimelineChart } from '../../components/Charts';
import Trend from '../../components/Trend';
import NumberInfo from '../../components/NumberInfo';
import { getDataToday, getDataAll } from '../../services/equipment';
import moment from 'moment';
import numeral from 'numeral';
const chartData = [];
for (let i = 0; i < 20; i += 1) {
    chartData.push({
        x: (new Date().getTime()) + (1000 * 60 * 30 * i),
        y1: Math.floor(Math.random() * 10) + 10,
    });
}

export default class WaterQualityCurve extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            oxygens: [],
            phs: [],
            temperatures: [],
            device_sn: '0300001'
        }
    }

    componentDidMount() {
        this.getDataToday();
    }

    getDataToday = () => {
        getDataToday({
            device_sn: this.state.device_sn,
        }).then((res) => {
            if (res && res.code == 0) {
                console.log(res)
                let oxygens = [], phs = [], temperatures = [];
                res.oxygens.map((oxygen, index) => {
                    console.log('111')
                    oxygens.push({
                        x: new Date(Date.parse(oxygen.receiveTime.replace(/-/g, "/"))),
                        // x: (new Date().getTime()) + (1000 * 60 * 30 * index),
                        y1: oxygen.oxygen
                    })
                })
                res.phs.map((ph, index) => {
                    console.log('222')
                    phs.push({
                        // x: new Date().getTime(ph.receiveTime),
                        x: new Date(Date.parse(ph.receiveTime.replace(/-/g, "/"))),
                        // x: (new Date().getTime()) + (1000 * 60 * 30 * index),
                        y1: ph.ph
                    })
                })
                res.temperatures.map((temperature, index) => {
                    temperatures.push({
                        // x: new Date().getTime(temperature.receiveTime),
                        x: new Date(Date.parse(temperature.receiveTime.replace(/-/g, "/"))),
                        // x: (new Date().getTime()) + (1000 * 60 * 30 * index),
                        y1: temperature.temperature
                    })
                })
                this.setState({
                    oxygens : oxygens,
                    phs : phs,
                    temperatures: temperatures
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
        }).then((res) => {
            if (res && res.code == 0) {
                console.log(res)
                let oxygens = [], phs = [], temperatures = [];
                res.oxygens.map((oxygen, index) => {
                    console.log('111')
                    oxygens.push({
                        x: new Date(Date.parse(oxygen.receiveTime.replace(/-/g, "/"))),
                        // x: (new Date().getTime()) + (1000 * 60 * 30 * index),
                        y1: oxygen.oxygen
                    })
                })
                res.phs.map((ph, index) => {
                    console.log('222')
                    phs.push({
                        // x: new Date().getTime(ph.receiveTime),
                        x: new Date(Date.parse(ph.receiveTime.replace(/-/g, "/"))),
                        // x: (new Date().getTime()) + (1000 * 60 * 30 * index),
                        y1: ph.ph
                    })
                })
                res.temperatures.map((temperature, index) => {
                    temperatures.push({
                        // x: new Date().getTime(temperature.receiveTime),
                        x: new Date(Date.parse(temperature.receiveTime.replace(/-/g, "/"))),
                        // x: (new Date().getTime()) + (1000 * 60 * 30 * index),
                        y1: temperature.temperature
                    })
                })
                this.setState({
                    oxygens : oxygens,
                    phs : phs,
                    temperatures: temperatures
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
        console.log(value)
        if (value == 'today') {
            this.getDataToday();
        } else if (value == 'sevent') {
            this.getDataSevent()
        }
    }
    render() {
        console.log(this.state.oxygens)
        return (
            <PageHeaderLayout >
                <Card bordered={false}>
                    <Row style={{ fontSize: 17 }}>
                        {/* <Col span={7}>设备编号: &nbsp;&nbsp; 南京鱼儿欢欢有限公司</Col>
                        <Col span={5}>设备名称: &nbsp; 传感器01</Col>
                        <Col span={5}>设备状态: &nbsp; 在线</Col> */}
                        <Col span={7}>设备名称: &nbsp;&nbsp; 传感器</Col>
                    </Row>
                    <Row style={{marginTop: 20}}>
                        <Col span={7}>
                            <Radio.Group onChange={e => this.handleTimeChange(e.target.value)} >
                                <Radio.Button value="today" >今日</Radio.Button>
                                <Radio.Button value="sevent">七日平均</Radio.Button>
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
                            {this.state.phs && this.state.phs.length > 0 ? <TimelineChart
                                height={300}
                                data={this.state.phs}
                                titleMap={{ y1: 'PH值' }}
                            /> : <span>暂无数据</span>}
                        </Col>
                    </Row>
                    <Row style={{ fontSize: 25, paddingTop: 25 }}>
                        溶氧变化曲线
                    </Row>
                    <Row style={{ padding: 30, paddingTop: 0 }}>
                        <Col span={20}>
                            {this.state.oxygens.length > 0 ?<TimelineChart
                                height={300}
                                data={this.state.oxygens}
                                titleMap={{ y1: '溶氧' }}
                            />: <span>暂无数据</span>}
                        </Col>
                    </Row>
                    <Row style={{ fontSize: 25, paddingTop: 25 }}>
                        水温变化曲线
                    </Row>
                    <Row style={{ padding: 30 }}>
                        <Col span={20}>
                            {this.state.WaterQualityCurve.length > 0 ? <TimelineChart
                                height={300}
                                data={this.state.temperatures}
                                titleMap={{ y1: '水温' }}
                            />: <span>暂无数据</span>}
                        </Col>
                    </Row>
                </Card>


            </PageHeaderLayout>
        );
    }
}
