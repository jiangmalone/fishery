import React, { PureComponent } from 'react';
import moment from 'moment';
import { connect } from 'dva';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { Table, Card, Row, Col, Input, Button } from 'antd'
import { Link } from 'react-router-dom'

@connect(state => ({
    list: state.equipment.list,
    loading: state.equipment.loading,
    pagination: state.equipment.pagination
}))

class EquipmentQuery extends PureComponent {

    constructor(props) {
        super(props);
        this.state = {
            device_sn: '',
            name: '',
        }
    }

    componentDidMount() {
        this.onSearch()
    }

    onSearch = (device_sn, name, relation) => {
        this.props.dispatch({
            type: 'equipment/fetch',
            payload: {
                device_sn: device_sn,
                name: name,
                relation: relation,
                number: 10,
                page: 1
            },
        })
    }

    render() {
        const { list, loading } = this.props;

        const columns = [
            {
                title: '序号',
                dataIndex: 'index'
            },
            {
                title: '设备编号',
                dataIndex: 'number',
                render: (text, record, index) => {
                    return <Link to="/equipment/equipment-detail">{text}</Link>
                },
            },
            {
                title: '设备名称',
                dataIndex: 'name',
            },
            {
                title: '所属企业',
                dataIndex: 'enterprise',
            },
            {
                title: '设备状态',
                dataIndex: 'state',
            }
        ];
        return (
            <PageHeaderLayout>
                <Card bordered={false}>
                    <Row style={{ marginBottom: '48px' }}>
                        <Col span={8}>设备编号：<Input style={{ width: 200 }} onChange={(e) => {
                            this.setState({
                                device_sn: e.target.value
                            })
                        }} /></Col>
                        <Col span={8}>企业名称：<Input style={{ width: 200 }} onChange={(e) => {
                            this.setState({
                                name: e.target.value
                            })
                        }} /></Col>
                        <Button type="primary" onClick={() => { this.onSearch(this.state.device_sn, this.state.name) }}>查询</Button>
                    </Row>
                    <Table loading={loading}
                        dataSource={this.props.list}
                        columns={columns}
                        pagination={this.props.pagination}
                        bordered
                    />
                </Card>
            </PageHeaderLayout>
        );
    }
}


export default EquipmentQuery