import React, { PureComponent } from 'react';
import moment from 'moment';
import { connect } from 'dva';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { Table, Card, Row, Col, Input, Button } from 'antd'
import {Link} from 'react-router-dom'

@connect(state => ({
    list: state.commonUser.list,
    loading: state.commonUser.loading,
    pagination: state.commonUser.pagination
}))

class UserList extends PureComponent {
    componentDidMount() {
        this.props.dispatch({
            type: 'commonUser/fetch',
            payload: {
                count: 10,
            },
        });
    }

    render() {
        console.log(this.props)
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
                title: '设备类型',
                dataIndex: 'type',
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
            },
            {
                title: '创建时间',
                dataIndex: 'createTime',
            }
        ];
        return (
            <PageHeaderLayout>
                <Card bordered={false}>
                    <Row style={{ marginBottom: '48px' }}>
                        <Col>设备编号：<Input style={{ width: 200 }}/></Col>
                        <Col>企业名称：<Input style={{ width: 200 }}/></Col>
                        <Button onClick={this.showAddModal}>查询</Button>
                    </Row>
                    <Row style={{ marginBottom: '15px' }}>
                        <Button onClick={this.showAddModal}>新建用户</Button>
                        <Button style={{ marginLeft: '10px' }}>删除用户</Button>
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


export default UserList