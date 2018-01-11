import React, { PureComponent } from 'react';
import moment from 'moment';
import { connect } from 'dva';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { Table, Card, Row, Col, Input, Button, Popconfirm } from 'antd'
import { Link } from 'react-router-dom'

const Search = Input.Search;
@connect(state => ({
    list: state.pond.list,
    loading: state.pond.loading,
    pagination: state.pond.pagination,
    formData: state.pond.formData
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
        const { list, loading, formData } = this.props;

        const columns = [{
            title: '序号',
            dataIndex: 'index',
            key: 'index',
            render: (text, record, index) => {
                return <span>{index + 1}</span>
            }
        }, {
            title: '设备编号',
            dataIndex: 'owner',
            key: 'owner',
            render: (text, record, index) => {
                return <Link to={`${index}`}>{text}</Link>
            }
        }, {
            title: '设备名称',
            dataIndex: 'gender',
            key: 'gender',
        }, {
            title: '设备状态',
            dataIndex: 'contact',
            key: 'contact',
        }, {
            title: '绑定时间',
            dataIndex: 'time',
            key: 'time',
        }, {
            title: '操作',
            dataIndex: 'keyword',
            render: (text, record, index) => {
                return <span>
                    <Popconfirm title="确认要解绑嘛?" onConfirm={() => this.onDelete([record.account])}>
                        <a href="javascript:void(0);">解绑</a>
                    </Popconfirm>
                </span>
            }
        }];
        return (
            <PageHeaderLayout>
                <Card title="塘口信息" bordered={false} style={{ marginBottom: '20px' }}>
                    <Row type="flex" justify="space-between" style={{ marginBottom: '15px' }}>
                        <Col span={4}>塘口名称：{formData.fields.name}</Col>
                        <Col span={4}>面积（亩）：{formData.fields.area}</Col>
                        <Col span={4}>深度（m）：{formData.fields.depth}</Col>
                    </Row>
                    <Row type="flex" justify="space-between" style={{ marginBottom: '15px' }}>
                        <Col span={4}>养殖品种：{formData.fields.fish_categorys}</Col>
                        <Col span={4}>池塘水源：{formData.fields.water_source}</Col>
                        <Col span={4}>底泥厚度(cm)：{formData.fields.sediment_thickness}</Col>
                    </Row>
                    <Row type="flex" justify="space-between" style={{ marginBottom: '15px' }}>
                        <Col span={4}>塘口密度(㎏/㎡)：{formData.fields.density}</Col>
                        <Col span={4}>塘口位置：{formData.fields.address}</Col>
                        <Col span={4}></Col>
                    </Row>
                </Card>
                <Card title="绑定设备" bordered={false} style={{ marginBottom: '20px' }}>
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