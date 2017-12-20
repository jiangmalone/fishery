import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Row, Col, Card, Input, Icon, Button, Radio, Table, Popconfirm } from 'antd';
const ButtonGroup = Button.Group;
import { Map, Markers } from 'react-amap';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import styles from "./companyUserDetail.less"

const randomPosition = () => ({
    longitude: 100 + Math.random() * 20,
    latitude: 30 + Math.random() * 20
})
const randomMarker = (len) => (
    Array(len).fill(true).map((e, idx) => ({
        position: randomPosition()
    }))
);

export default class CompanyUserDetail extends React.Component {
    constructor() {
        super();
        this.state = {
            markers: randomMarker(2),
            center: randomPosition(),
            isShowMap: true
        }
        this.randomMarkers = this.randomMarkers.bind(this)
    }

    randomMarkers() {
        this.setState({
            markers: randomMarker(2)
        })
    }

    handleShowChange = (e) => {
        console.log(e.target.value)
        this.setState({
            isShowMap: e.target.value
        })
    }

    render() {
        const columns = [
            {
                title: '序号',
                dataIndex: 'index'
            },
            {
                title: '设备编号',
                dataIndex: 'number',
                render: (text, record, index) => {
                    return <Link to={`/equipment/${index}`}>{text}</Link>
                },
            },
            {
                title: '设备类型',
                dataIndex: 'type', 
            },
            {
                title: '设备名称',
                dataIndex: 'name',
            },
            {
                title: '设备状态',
                dataIndex: 'state',
            },
            {
                title: '创建时间',
                dataIndex: 'createTime',
            }
        ];
        const data = [{
            name: '涡轮增压机',
            index: "1",
            number: 88888888,
            type: '反人类设备',
            state: "即将开启",
            createTime: "2017-12-14",
            key: 1
        }];

        return (
            <PageHeaderLayout >
                <Card bordered={false}>
                    <Row>
                        <Col span={8}>企业名称: &nbsp;&nbsp; 南京鱼儿欢欢有限公司</Col>
                        <Col span={3} offset={10}>塘口数: &nbsp; 52</Col>
                        <Col span={3} >设备在线数: &nbsp;35/40</Col>
                    </Row>

                </Card>
                <Card>
                    <Row>
                        <Col span={8}>
                            <Radio.Group onChange={this.handleShowChange}>
                                <Radio.Button value={true}>地图查看</Radio.Button>
                                <Radio.Button value={false}>列表查看</Radio.Button>
                            </Radio.Group>
                        </Col>

                        <Col span={3} offset={10}>
                            <Link to=""><Button type="primary">管理塘口</Button></Link>
                        </Col>
                        <Col span={3}>
                            <Link to="/equipment"><Button type="primary">管理设备</Button></Link>
                        </Col>
                    </Row>
                    <Row style={{ marginTop: 30 }}>
                        {this.state.isShowMap ? <div style={{ width: '100%', height: 400}}>
                            <Map plugins={['ToolBar']} center={this.state.center} zoom={6}>
                                <Markers
                                    markers={this.state.markers}
                                />
                            </Map>
                        </div> : <Table
                            dataSource={data}
                            columns={columns}
                            bordered
                        />}
                    </Row>
                </Card>
            </PageHeaderLayout>
        );
    }
}
