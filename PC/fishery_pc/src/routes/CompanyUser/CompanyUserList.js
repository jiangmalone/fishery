import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Table, Row, Col, Card, Input, Icon, Button, InputNumber, Modal, message, Popconfirm } from 'antd';
const Search = Input.Search;
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import styles from "./companyUserList.less"
import AddCompanyUser from './AddCompanyUser';

@connect(state => ({
    list: state.companyUser.list,
    loading: state.companyUser.loading,
    pagination: state.companyUser.pagination
}))
export default class CompanyUserList extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            selectedRows: [],
            selectedRowKeys: [],
            showAddModal: false,
            modifyId: '',
            mode: 'add',
            index: '',
        }
    }

    componentDidMount() {
        this.props.dispatch({
            type: 'companyUser/fetch',
            payload: {
                number: 10,
                page: 1
            },
        });
    }

    showAddModal = (mode = 'add', index, id) => {
        if(!id) {    //新增的话，清空之前可能有的数据
            this.props.dispatch({
                type: 'companyUser/changeModal',
                payload: {
                    formData: { fields: {} }
                }
            })
        }
        
        this.setState({
            showAddModal: true,
            mode: mode,
            index: index,
            modifyId: id
        })
    }

    onDelete = (idArray) => {
        console.log(idArray);
        this.props.dispatch({
            type: 'companyUser/delCompany',
            payload: {
                companyIds: idArray,
                pagination: this.props.pagination
            },
        });
        this.setState({
            selectedRows: [],
            selectedRowKeys: []
        })
    }

    modifyInfo = (record, index) => {
        let formData = {}
        for (let key in record) {
            formData[key] = {
                value: record[key],
                name: key
            }
        }
        this.props.dispatch({
            type: 'companyUser/changeModal',
            payload: {
                formData: { fields: formData }
            }
        })
        this.showAddModal('modify', index, record.id)
    }

    onOk = (values) => {
        console.log(values);
        console.log('onOk');
        if (isNaN(this.state.index)) {
            
            this.props.dispatch({
                type: 'companyUser/addCompany',
                payload: values,
            });
        } else {
            values.id = this.state.modifyId
            this.props.dispatch({
                type: 'companyUser/modifyCompany',
                payload: {
                    index: this.state.index,
                    data: values
                },
            });
        }
        this.setState({
            showAddModal: false
        })
    }

    onCancel = () => {
        this.setState({
            showAddModal: false
        })
    }

    handleTableChange = (pagination) => {
        const pager = { ...this.props.pagination };
        pager.current = pagination.current;
        this.props.dispatch({
            type: 'companyUser/fetch',
            payload: {
                number: 10,
                page: pagination.current,
            },
        });
        this.props.dispatch({
            type: 'companyUser/changeModal',
            payload: { pagination: pager }
        })
    }

    onSearch = (value) => {
        this.props.dispatch({
            type: 'companyUser/fetch',
            payload: {
                number: 10,
                page: 1,
                name: value
            },
        });
    }

    render() {
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
        const columns = [
            {
                title: '序号',
                dataIndex: 'index',
                key: 'index',
                render: (text, record, index) => {
                    return <span>{index + 1}</span>
                }
            },
            {
                title: '名称',
                dataIndex: 'name',
                render: (text, redcord, index) => {
                    return <Link to={`company-user/${index}`}>{text}</Link>
                },
            },
            {
                title: '联系方式',
                dataIndex: 'phone',
            },
            {
                title: '联系地址',
                dataIndex: 'address',
            },
            {
                title: '养殖年限',
                dataIndex: 'life',
            },
            {
                title: '创建时间',
                dataIndex: 'createDate',
            },
            {
                title: '操作',
                dataIndex: 'keyword',
                render: (text, record, index) => {
                    return <span>

                        <span onClick={() => { this.modifyInfo(record, index) }}> <a href="javascript:void(0);" style={{ marginRight: '15px' }}>修改</a></span>
                        <Popconfirm title="确认要删除嘛?" onConfirm={() => this.onDelete([record.id + ''])}>
                            <a href="javascript:void(0);">删除</a>
                        </Popconfirm>
                    </span>
                }
            },
        ];
        return (
            <PageHeaderLayout >
                <Card bordered={false}>
                    <Row>

                        <Col>企业名称: &nbsp;<Search style={{ width: 200 }} placeholder="" enterButton="查询" onSearch={(value) => this.onSearch(value)} /></Col>
                    </Row>
                </Card>
                <Card bordered={false}>
                    <div>
                        <div >
                            <Button onClick={this.showAddModal}>
                                新建企业
                            </Button>
                            <Button className={styles.button} onClick={() => this.onDelete(this.state.selectedRowKeys)} >删除企业</Button>
                        </div>
                        <Table
                            loading={this.state.loading}
                            dataSource={this.props.list}
                            columns={columns}
                            rowSelection={rowSelection}
                            className={styles.table}
                            pagination={this.props.pagination}
                            onChange={this.handleTableChange}
                            bordered
                        />
                    </div>
                </Card>
                <AddCompanyUser
                    modifyId={this.state.modifyId}
                    visible={this.state.showAddModal}
                    onOk={this.onOk}
                    wrapClassName='vertical-center-modal'
                    onCancel={this.onCancel}
                />

            </PageHeaderLayout>
        );
    }
}
