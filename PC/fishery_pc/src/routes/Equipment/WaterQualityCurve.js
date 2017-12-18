import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Row, Col, Card, Input, Icon, Button, Table } from 'antd';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
export default class WaterQualityCurve extends React.Component {

    state={
        loading:false,  
    }
    render() {
        const realTimeColumns = [
            {
                title: '时间',
                dataIndex: 'time'
            },
            {
                title: '溶氧(mg/L)',
                dataIndex: 'DO'
            },
            {
                title: '水温(℃)',
                dataIndex: 'waterT',
            },
            {
                title: 'PH',
                dataIndex: 'ph',
            }
        ];
        const realTimeData = [{
            time: "2017-08-17 12:56:33",
            DO: 6.8,
            waterT: 28,
            ph: 9.0,
            key: 1
        }]
        return (
            <PageHeaderLayout >
                <Card bordered={false}>
                    <Row style={{fontSize: 17}}>
                        <Col span={7}>设备编号: &nbsp;&nbsp; 南京鱼儿欢欢有限公司</Col>
                        <Col span={5}>设备名称: &nbsp; 传感器01</Col>
                        <Col span={5}>设备状态: &nbsp; 在线</Col>
                    </Row>
                </Card>
                <Card
                    bodyStyle={{ marginTop: 15 }}
                    title="最新数据"
                    bordered
                >
                    <Col span={18}>
                        <Table
                            loading={this.state.loading}
                            dataSource={realTimeData}
                            columns={realTimeColumns}
                            bordered
                            pagination={false}
                        />
                    </Col>
                    <Col span={4} offset={2} style={{paddingTop: 40}}>
                        <Button size="large">我日哦哦</Button>
                    </Col>
                </Card>
            </PageHeaderLayout>
        );
    }
}
