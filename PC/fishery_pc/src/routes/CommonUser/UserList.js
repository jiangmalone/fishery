import React, { PureComponent } from 'react';
import moment from 'moment';
import { connect } from 'dva';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { Table, Card, Row, Col,Input } from 'antd'
import styles from './UserList.less';
import { Button } from '../../../node_modules/_antd@3.0.1@antd/lib/radio';

const Search = Input.Search;
@connect(state => ({
    list: state.list,
}))

class UserList extends PureComponent {
    componentDidMount() {
        this.props.dispatch({
            type: 'commonUser/fetch',
            payload: {
                count: 5,
            },
        });
    }

    componentWillReceiveProps(newProps) {
        console.log(newProps.list)
    }

    render() {
        console.log(this.props)
        const { list: { list, loading } } = this.props;
        const paginationProps = {
            showSizeChanger: true,
            showQuickJumper: true,
            pageSize: 5,
            total: 50,
        };
        const columns = [{
            title: '序号',
            dataIndex: 'index',
            key: 'index',
            render: (text, record, index) => {
                return <span>{index + 1}</span>
            }
        }, {
            title: '名称',
            dataIndex: 'name',
            key: 'name',
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
                <Card>
                    <Row style={{marginBottom:'30px'}}>
                        <Col>用户名称：<Search style={{ width: 200 }}  enterButton="查询" /></Col>  
                    </Row>
                    <Table loading={loading}
                        dataSource={this.props.list}
                        columns={columns}
                        pagination={paginationProps} />
                </Card>
            </PageHeaderLayout>
                );
    }
}


export default UserList