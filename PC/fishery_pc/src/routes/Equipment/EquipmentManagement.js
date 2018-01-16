import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Table, Row, Col, Card, Input, Button, Popconfirm } from 'antd';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import styles from "./equipmentManagement.less"
import update from 'immutability-helper'
import AddEquipment from './AddEquipment';

@connect(state => ({
    list: state.equipment.list,
    loading: state.equipment.loading,
    pagination: state.equipment.pagination
}))

export default class EquipmentManagement extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            name: '',   //查询用
            device_sn: '',   //查询用
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
            type: 'equipment/fetch',
            payload: {
                number: 10,
                page: 1,
                relation: this.props.match.params.id
            },
        });
    }

    showAddModal = (mode = 'add', index, id) => {
        if (!id && id != 0) {    //新增的话，清空之前可能有的数据
            this.props.dispatch({
                type: 'equipment/changeModal',
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
        if (idArray.length <= 0) {
            message.warn('请选择需要删除的设备！');
            return;
        }
        this.props.dispatch({
            type: 'equipment/delEquipments',
            payload: {
                equipmentIds: idArray,
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
            type: 'equipment/changeModal',
            payload: {
                formData: { fields: formData }
            }
        })
        this.showAddModal('modify', index, record.id)
    }

    onOk = (values) => {
        if (isNaN(this.state.index)) {
            this.props.dispatch({
                type: 'equipment/addEquipment',
                payload: values,
            });
        } else {
            values.id = this.state.modifyId
            this.props.dispatch({
                type: 'equipment/modifyEquipment',
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

    handleInputChange = (type, value) => {
        if (type) {
            if (type == 'name') {
                this.setState({ name: value });
            } else if (type == 'device_sn') {
                this.setState({ device_sn: value });
            }
        }
    }

    doSearch = () => {
        this.props.dispatch({
            type: 'equipment/fetch',
            payload: {
                device_sn: this.state.device_sn,
                name: this.state.name,
                page: 1,
                number: 10,
                relation: this.props.match.params.id
            },
        });
    }


    handleTableChange = (pagination) => {
        const pager = { ...this.props.pagination };
        pager.current = pagination.current;
        this.props.dispatch({
            type: 'equipment/fetch',
            payload: {
                number: 10,
                page: pagination.current,
            },
        });
        this.props.dispatch({
            type: 'equipment/changeModal',
            payload: { pagination: pager }
        })
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
                dataIndex: 'index'
            },
            {
                title: '设备编号',
                dataIndex: 'number',
                render: (text, record, index) => {
                    return <Link to={`equipment/${record.id}`}>{text}</Link>
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
                title: '设备状态',
                dataIndex: 'state',
            },
            {
                title: '创建时间',
                dataIndex: 'createTime',
            },
            {
                title: '操作',
                dataIndex: 'keyword',
                render: (text, record, index) => {
                    return <span>
                        <span
                            onClick={() => { this.modifyInfo(record, index) }}>
                            <a href="javascript:void(0);" style={{ marginRight: '15px' }}>修改</a>
                        </span>
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
                        <Col span={7}>
                            <div style={{ marginBottom: 16 }}>
                                设备编号：&nbsp;
                                <Input
                                    style={{ width: '200px' }}
                                    value={this.state.device_sn}
                                    onChange={e => this.handleInputChange('device_sn', e.target.value)}
                                />
                            </div>
                        </Col>
                        <Col span={7}>
                            <div style={{ marginBottom: 16 }}>
                                设备名称：&nbsp;
                                <Input
                                    style={{ width: '200px' }}
                                    value={this.state.name}
                                    onChange={e => this.handleInputChange('name', e.target.value)}
                                />
                            </div>
                        </Col>
                        <Col span={2}>
                            <Button type="primary" onClick={this.doSearch} >查询</Button>
                        </Col>
                    </Row>
                </Card>
                <Card bordered={false}>
                    <div>
                        <div >
                            <Button onClick={this.showAddModal}>
                                新建设备
                            </Button>
                            <Button
                                className={styles.deletebutton}
                                onClick={() => this.onDelete(this.state.selectedRowKeys)}
                            >
                                删除设备
                            </Button>
                        </div>
                        <Table
                            loading={this.props.loading}
                            rowSelection={rowSelection}
                            dataSource={this.props.list}
                            columns={columns}
                            className={styles.table}
                            pagination={this.props.pagination}
                            onChange={this.handleTableChange}
                            bordered
                        />
                    </div>
                </Card>
                <AddEquipment
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
