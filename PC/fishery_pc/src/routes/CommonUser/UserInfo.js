import React, { PureComponent } from 'react';
import moment from 'moment';
import { connect } from 'dva';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { Table, Card, Row, Col, Input, Button } from 'antd'

const Search = Input.Search;
@connect(state => ({
    list: state.userDetail.list,
    loading: state.userDetail.loading,
    pagination: state.userDetail.pagination
}))

class UserInfo extends PureComponent {
    componentDidMount() {
        this.onSearch()
    }

    onSearch = (value) => {
        this.props.dispatch({
            type: 'userDetail/fetch',
            payload: {
                name: value,
                number: 10,
                page: 1,
                // relation:'wx4'
            },
        })
    }

    render() {
        const { list, loading } = this.props;

        const columns = [{
            title: '序号',
            dataIndex: 'index',
            key: 'index',
            render: (text, record, index) => {
                return <span>{index + 1}</span>
            }
        }, {
            title: '塘口名称',
            dataIndex: 'name',
            key: 'name',
        }, {
            title: '面积（亩）',
            dataIndex: 'area',
            key: 'area',
        }, {
            title: '深度（m）',
            dataIndex: 'depth',
            key: 'depth',
        }, {
            title: '品种',
            dataIndex: 'type',
            key: 'type',
        }, {
            title: '池塘水源',
            dataIndex: 'waterSource',
            key: 'waterSource',
        }, {
            title: '泥底厚度',
            key: 'houdu',
            dataIndex: 'houdu'
        }, {
            title: '塘口密度（kg/㎡）',
            key: 'm',
            dataIndex: 'm'
        }];
        return (
            <PageHeaderLayout>
                <Card title="用户信息" bordered={false} style={{ marginBottom:'20px'}}>
                    <Row type="flex" justify="space-between" style={{ marginBottom: '15px' }}>
                        <Col span={4}>用户名称：涵抱抱</Col>
                        <Col span={4}>性别：女</Col>
                        <Col span={4}>联系方式：18362981113</Col>
                    </Row>
                    <Row type="flex" justify="space-between" style={{ marginBottom: '15px' }}>
                        <Col span={4}>养殖年限：12</Col>
                        <Col span={4}>塘口位置：南京市玄武大道1号</Col>
                        <Col span={4}></Col>
                    </Row>

                </Card>
                <Card title="塘口信息" bordered={false} style={{ marginBottom:'20px'}}>
                    <Table loading={loading}
                        dataSource={this.props.list}
                        columns={columns}
                        pagination={this.props.pagination}
                        bordered
                    />
                </Card>

                <Card title="设备信息" bordered={false} style={{ marginBottom:'20px'}}>
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


export default UserInfo