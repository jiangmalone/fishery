import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Row, Col, Card, Input, Icon, Button, Table, Radio, DatePicker } from 'antd';
const {RangePicker} = DatePicker;
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { TimelineChart } from '../../components/Charts';
import Trend from '../../components/Trend';
import NumberInfo from '../../components/NumberInfo';
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

    render() {
        return (
            <PageHeaderLayout >
                <Card bordered={false}>
                    <Row style={{ fontSize: 17 }}>
                        <Col span={7}>设备编号: &nbsp;&nbsp; 南京鱼儿欢欢有限公司</Col>
                        <Col span={5}>设备名称: &nbsp; 传感器01</Col>
                        <Col span={5}>设备状态: &nbsp; 在线</Col>
                    </Row>
                    <Row style={{marginTop: 20}}>
                        <Col span={7}>
                            <Radio.Group >
                                <Radio.Button value="large">今日</Radio.Button>
                                <Radio.Button value="default">七日平均</Radio.Button>
                                <Radio.Button value="small">区间</Radio.Button>
                            </Radio.Group>
                        </Col>

                        <Col>
                        <RangePicker />
                        </Col>

                    </Row>
                    <Row style={{ fontSize: 25, paddingTop: 25 }}>
                        PH变化曲线
                    </Row>
                    <Row style={{ padding: 30, paddingTop: 0 }}>
                        <Col span={20}>
                            <TimelineChart
                                height={300}
                                data={chartData}
                                titleMap={{ y1: 'PH变化曲线' }}
                            />
                        </Col>
                    </Row>
                    <Row style={{ fontSize: 25, paddingTop: 25 }}>
                        溶氧变化曲线
                    </Row>
                    <Row style={{ padding: 30, paddingTop: 0 }}>
                        <Col span={20}>
                            <TimelineChart
                                height={300}
                                data={chartData}
                                titleMap={{ y1: '溶氧变化曲线' }}
                            />
                        </Col>
                    </Row>
                    <Row style={{ fontSize: 25, paddingTop: 25 }}>
                        水温变化曲线
                    </Row>
                    <Row style={{ padding: 30 }}>
                        <Col span={20}>
                            <TimelineChart
                                height={300}
                                data={chartData}
                                titleMap={{ y1: '水温变化曲线' }}
                            />
                        </Col>
                    </Row>
                </Card>


            </PageHeaderLayout>
        );
    }
}
