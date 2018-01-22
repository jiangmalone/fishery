import React, { PureComponent } from 'react';
import moment from 'moment';
import { connect } from 'dva';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { Table, Card, Row, Col, Input, Button } from 'antd'
import { Link } from 'react-router-dom'

@connect(state => ({
    list: state.allequipment.list,
    loading: state.allequipment.loading,
    pagination: state.allequipment.pagination
}))

class AllEquipmentQuery extends PureComponent {

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
            type: 'allequipment/fetch',
            payload: {
                device_sn: device_sn,
                name: name,
                number: 10,
                page: 1
            },
        })
    }
    handleTableChange = (pagination) => {
        const pager = { ...this.props.pagination2 };
        pager.current = pagination.current;
        this.onSearch(this.state.device_sn, this.state.name)
        this.props.dispatch({
            type: 'allequipment/changeLoading',
            payload: { pagination: pager }
        })
    }


    render() {
        console.log(this.props)
        const { list, loading } = this.props;

        const columns = [
            {
                title: '序号',
                dataIndex: 'index',
                render: (text, record, index) => {
                    return <span>{index + 1}</span>
                }
            },
            {
                title: '设备编号',
                dataIndex: 'device_sn',
                render: (text, record, index) => {
                    return <Link to={`/equipment/detail/${text}`}>{text}</Link>
                },
            },
            {
                title: '设备名称',
                dataIndex: 'name',
            },
            {
                title: '所属企业',
                dataIndex: 'companyName',
            },
            {
                title: '设备状态',
                dataIndex: 'status',
                render: (text, record, index) => {

                    switch (text) {
                        case 0: text = '正常'; break;
                        case 1: text = '离线'; break;
                        case 2: text = '断电'; break;
                        case 3: text = '缺相'; break;
                        case 4: text = '数据异常'; break;
                    }
                    return <span>{text}</span>
                }
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
                        onChange={this.handleTableChange}
                    />
                </Card>
            </PageHeaderLayout>
        );
    }
}

export default AllEquipmentQuery