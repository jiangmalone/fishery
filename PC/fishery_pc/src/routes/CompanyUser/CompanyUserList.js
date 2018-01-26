import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Table, Row, Col, Card, Input, Icon, Button, InputNumber, Modal, message, Popconfirm } from 'antd';
const Search = Input.Search;
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import styles from "./companyUserList.less"
import AddCompanyUser from './AddCompanyUser';
import AddAccount from './AddAccount';

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
            modifyId2: '',
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
        if (!id && id != 0) {    //新增的话，清空之前可能有的数据
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

    openAccount = (record, index) => {
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
                formData2: { fields: formData }
            }
        })
        if (!record.hasAccount) {
            this.showAddModal2('add', index, record.id)
        } else {
            this.showAddModal2('modify', index, record.id)
        }
    }

    showAddModal2 = (mode = 'add', index, id) => {
        if (!id && id != 0) {    //新增的话，清空之前可能有的数据
            this.props.dispatch({
                type: 'companyUser/changeModal',
                payload: {
                    formData2: { fields: {} }
                }
            })
        }
        this.setState({
            showAddModal2: true,
            mode2: mode,
            index2: index,
            companyId: id,
        })
    }


    onDelete = (idArray) => {
        this.setState({
            selectedRowKeys:[]
        })
        if (idArray.length <= 0) {
            message.warn('请选择需要删除的企业！');
            return;
        }
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

    onOk2 = (values) => {
        console.log(values,this.state.mode2)
        values.companyId = this.state.companyId;
        values.type = 1;
        delete values.password2
        delete values.name
        if (this.state.mode2 == 'add') {
     
            this.props.dispatch({
                type: 'companyUser/addAccount',
                payload: {
                    index2: this.state.index2,
                    data: values
                },
            });
            console.log(22)
        } else {
            values.adminId =window.localStorage.getItem('adminId')
            this.props.dispatch({
                type: 'companyUser/modifyAccount',
                payload: {
                    index2: this.state.index2,
                    data: values
                },
            });
        }
        this.setState({
            showAddModal2: false
        })
    }

    onCancel = () => {
        this.setState({
            showAddModal: false
        })
    }

    onCancel2 = () => {
        this.setState({
            showAddModal2: false
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
                    return <Link to={`company-user/${redcord.id}/${redcord.relation}`}>{text}</Link>
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
                title: '邮箱地址',
                dataIndex: 'mail_address',
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
                        <span onClick={() => { this.modifyInfo(record, index) }}>
                            <a href="javascript:void(0);" style={{ marginRight: '15px' }}>修改</a>
                        </span>
                        <Popconfirm title="确认要删除嘛?" onConfirm={() => this.onDelete([record.id + ''])}>
                            <a href="javascript:void(0);">删除</a>
                        </Popconfirm>
                        <span onClick={() => { this.openAccount(record, index) }}>
                            <a href="javascript:void(0);" style={{ marginLeft: '15px' }}>{record.hasAccount ? '更户' : '开户'}</a>
                        </span>
                    </span>
                }
            },
        ];

        const modalProps1 = {
            modifyId: this.state.modifyId,
            visible: this.state.showAddModal,
            onOk: this.onOk,
            wrapClassName: 'vertical-center-modal',
            onCancel: this.onCancel
        }
        const modalProps2 = {
            modifyId: this.state.modifyId2,
            visible: this.state.showAddModal2,
            onOk: this.onOk2,
            wrapClassName: 'vertical-center-modal',
            onCancel: this.onCancel2
        }
        return (
            <PageHeaderLayout >
                <Card bordered={false}>
                    <Row>
                        <Col>企业名称: &nbsp;
                            <Search
                                style={{ width: 200 }}
                                placeholder=""
                                enterButton="查询"
                                onSearch={(value) => this.onSearch(value)}
                            />
                        </Col>
                    </Row>
                </Card>
                <Card bordered={false}>
                    <div>
                        <div >
                            <Button onClick={this.showAddModal}>
                                新建企业
                            </Button>
                            <Popconfirm title="确认要删除嘛?" onConfirm={() =>this.onDelete(this.state.selectedRowKeys)}>
                            <Button
                                className={styles.button}
                            >
                                删除企业
                            </Button>
                            </Popconfirm>
                        </div>
                        <Table
                            loading={this.props.loading}
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
                    {...modalProps1}
                />
                <AddAccount {...modalProps2} />
            </PageHeaderLayout>
        );
    }
}
