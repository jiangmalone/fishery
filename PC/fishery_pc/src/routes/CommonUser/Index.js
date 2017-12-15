import React, { PureComponent } from 'react';
import moment from 'moment';
import { connect } from 'dva';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { Table, Card, Row, Col, Input, Button } from 'antd'
import UserInfo from './UserInfo';
import {Link} from 'react-router-dom'

const Search = Input.Search;
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

        const columns = [{
            title: '序号',
            dataIndex: 'index',
            key: 'index',
            render: (text, record, index) => {
                return <span>{index + 1}</span>
            }
        }, {
            title: '名称',
            dataIndex: 'owner',
            key: 'owner',
            render: (text,record,index)=>{
                return <Link to={`common-user/${index}`}>{text}</Link>
            }
        }, {
            title: '性别',
            dataIndex: 'gender',
            key: 'gender',
        }, {
            title: '联系方式',
            dataIndex: 'contact',
            key: 'contact',
        }, {
            title: '养殖年限',
            dataIndex: 'time',
            key: 'time',
        }, {
            title: '联系地址',
            dataIndex: 'address',
            key: 'address',
        }, {
            title: '创建时间',
            key: 'createTime',
            dataIndex: 'createTime'
        }];
        return (
            <PageHeaderLayout>
                <Card bordered={false}>
                    <Row style={{ marginBottom: '48px' }}>
                        <Col>用户名称：<Search style={{ width: 200 }} enterButton="查询" /></Col>
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