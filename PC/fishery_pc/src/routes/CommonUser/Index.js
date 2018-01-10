import React, { PureComponent } from 'react';
import moment from 'moment';
import { connect } from 'dva';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { Table, Card, Row, Col, Input, Button, Popconfirm } from 'antd'
import { Link } from 'react-router-dom'
import AddUser from './AddUser'

const Search = Input.Search;
@connect(state => ({
    list: state.commonUser.list,
    loading: state.commonUser.loading,
    pagination: state.commonUser.pagination
}))

class UserList extends PureComponent {
    constructor(props) {
        super(props);
        this.state = {
            view: false,
            selectedRows: [],
            selectedRowKeys: [],
            mode: 'add',
            index:'',
            modifyId:''
        }
    }

    componentDidMount() {
        console.log(this.props.list)
        this.props.dispatch({
            type: 'commonUser/fetch',
            payload: {
                number: 10,
                page: 1
            },
        });
    }


    showAddModal = (mode = 'add', index, id) => {
        this.setState({
            view: true,
            mode: mode,
            index: index,
            modifyId: id
        })
    }

    modifyInfo = (record, index) => {
        let formData = {}
        console.log(record)
        for (let key in record) {
            console.log(key)
            formData[key] = {
                value: record[key],
                name: key
            }
        }
        console.log(formData)
        this.props.dispatch({
            type: 'commonUser/changeModal',
            payload: {
                formData: { fields: formData }
            }
        })
        this.showAddModal('modify', index, record.id)
    }

    onOk = (values) => {
        console.log(this.state.index)
        let tmp = this.state.index
        if (!tmp && typeof (tmp) != "undefined" && tmp != 0) {
            this.props.dispatch({
                type: 'commonUser/addUser',
                payload: values,
            });
        } else {
            values.id = this.state.modifyId
            this.props.dispatch({
                type: 'commonUser/modifyWXUser',
                payload: {
                    index: this.state.index,
                    data: values
                },
            });
        }
        this.setState({
            view: false
        })
    }


    onCancel = () => {
        this.setState({
            view: false
        })
    }

    handleTableChange = (pagination) => {
        const pager = { ...this.props.pagination };
        pager.current = pagination.current;
        this.props.dispatch({
            type: 'commonUser/fetch',
            payload: {
                number: 10,
                page: pagination.current,
                pagination: pager
            },
        });
    }

    onDelete = (idArray) => {
        this.props.dispatch({
            type: 'commonUser/deleteUser',
            payload: {
                WXUserIds: idArray,
                pagination: this.props.pagination
            },
        });
    }


    render() {
        const { list, loading } = this.props;
        const rowSelection = {
            //针对全选
            onSelectAll: (selected, selectedRows, changeRows) => {
                let origKeys = this.state.selectedRowKeys;
                let origRows = this.state.selectedRows;
                if (selected) {
                    origRows = [...origRows, ...changeRows];
                    for (let item of changeRows) {
                        origKeys.push(item.id);
                    }
                } else {
                    for (let change of changeRows) {
                        origKeys = origKeys.filter((obj) => {
                            return obj !== change.key;
                        });
                        origRows = origRows.filter((obj) => {
                            return obj.key !== change.key;
                        });
                    }
                }
                this.setState({
                    selectedRowKeys: origKeys,
                    selectedRows: origRows,
                });

            },
            selectedRowKeys: this.state.selectedRowKeys,
            onSelect: (changableRow, selected, selectedRows) => {
                //state里面记住这两个变量就好
                let origKeys = this.state.selectedRowKeys;
                let origRows = this.state.selectedRows;
                if (selected) {
                    origKeys = [...origKeys, changableRow.key];
                    origRows = [...origRows, changableRow];
                } else {
                    origKeys = origKeys.filter((obj) => {
                        return obj !== changableRow.key;
                    });
                    origRows = origRows.filter((obj) => {
                        return obj.key !== changableRow.key;
                    });
                }
                console.log(origKeys)
                this.setState({
                    selectedRowKeys: origKeys,
                    selectedRows: origRows
                });
            }
        }
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
            render: (text, record, index) => {
                return <Link to={`common-user/${index}`}>{text}</Link>
            }
        }, {
            title: '性别',
            dataIndex: 'sex',
            key: 'sex',
        }, {
            title: '联系方式',
            dataIndex: 'phone',
            key: 'phone',
        }, {
            title: '养殖年限',
            dataIndex: 'life',
            key: 'life',
        }, {
            title: '联系地址',
            dataIndex: 'address',
            key: 'address',
        }, {
            title: '创建时间',
            key: 'createDate',
            dataIndex: 'createDate'
        }, {
            title: '操作',
            dataIndex: 'keyword',
            render: (text, record, index) => {
                return <span>
                    <span > <a href="javascript:void(0);" style={{ marginRight: '15px' }} onClick={() => { this.modifyInfo(record, index) }}>修改</a></span>
                    <Popconfirm title="确认要删除嘛?" onConfirm={() => this.onDelete([record.id + ''])}>
                        <a href="javascript:void(0);">删除</a>
                    </Popconfirm>
                </span>
            }
        },];
        return (
            <PageHeaderLayout>
                <Card bordered={false}>
                    <Row style={{ marginBottom: '48px' }}>
                        <Col>用户名称：<Search style={{ width: 200 }} enterButton="查询" /></Col>
                    </Row>
                    <Row style={{ marginBottom: '15px' }}>
                        <Button onClick={this.showAddModal}>新建用户</Button>
                        <Button style={{ marginLeft: '10px' }} onClick={() => this.onDelete(this.state.selectedRowKeys)}>删除用户</Button>
                    </Row>
                    <Table loading={loading}
                        dataSource={this.props.list}
                        columns={columns}
                        rowSelection={rowSelection}
                        pagination={this.props.pagination}
                        onChange={this.handleTableChange}
                        bordered
                    />
                    <AddUser modifyId={this.state.modifyId} visible={this.state.view} onOk={this.onOk} wrapClassName='vertical-center-modal' onCancel={this.onCancel} />
                </Card>
            </PageHeaderLayout>
        );
    }
}


export default UserList