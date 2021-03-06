import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Table, Row, Col, Card, Input, Button, Popconfirm, message } from 'antd';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import styles from "./equipmentManagement.less"
import update from 'immutability-helper'
import AddEquipment from './AddEquipment';
import Switch from 'antd/lib/switch';

@connect(state => ({
    list: state.equipment.list,
    loading: state.equipment.loading,
    pagination: state.equipment.pagination,
    showAddModal: state.equipment.showAddModal,
    user: state.equipment.user
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
            type: 'equipment/companyFindEquipment',
            payload: {
                number: 10,
                page: 1,
                relation: this.props.match.params.relation
                // relation: 'CO1'
            },
        });
    }

    showAddModal = (mode = 'add', index, device_sn) => {
        if (!device_sn && device_sn != 0) {    //新增的话，清空之前可能有的数据
            this.props.dispatch({
                type: 'equipment/changeModal',
                payload: {
                    formData: { fields: {} }
                }
            })
        }
        this.props.dispatch({
            type: 'equipment/changeModal',
            payload: {
                showAddModal: true,
            }
        })
        this.setState({
            showAddModal: true,
            mode: mode,
            index: index,
            modifyId: device_sn
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
                device_sns: idArray,
                pagination: this.props.pagination,
                relation: this.props.match.params.relation
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
                formData: { fields: formData },
            }
        })
        this.showAddModal('modify', index, record.device_sn)
    }

    onOk = (values) => {
        if (isNaN(this.state.index)) {
            values.current = this.props.pagination.current
            values.relation = this.props.match.params.relation;
            this.props.dispatch({
                type: 'equipment/addEquipment',
                payload: values,
            });
        } else {
            // console.log(values)
            // values.device_sn = this.state.modifyId
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
        this.props.dispatch({
            type: 'equipment/changeModal',
            payload: {
                showAddModal: false,
            }
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
            type: 'equipment/companyFindEquipment',
            payload: {
                device_sn: this.state.device_sn,
                name: this.state.name,
                page: 1,
                number: 10,
                relation: this.props.match.params.relation
            },
        });
    }


    handleTableChange = (pagination) => {
        const pager = { ...this.props.pagination };
        pager.current = pagination.current;
        this.props.dispatch({
            type: 'equipment/companyFindEquipment',
            payload: {
                number: 10,
                page: pagination.current,
                relation: this.props.match.params.relation
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
                        origKeys.push(item.device_sn);
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
                title: '设备编号',
                dataIndex: 'device_sn',
                render: (text, record, index) => {
                    return <Link to={`/equipment/detail/${record.device_sn}/${this.props.match.params.relation}/${record.sensorId}`}>{text}</Link>
                },
            },
            {
                title: '设备类型',
                // dataIndex: 'type',
                render: (text, record, index) => {
                    let str = '';
                    if (record.device_sn != undefined) {
                        const type = record.device_sn.substring(0, 2);
                        switch (type) {
                            case '01':
                            case '02':
                                str = '一体机';
                                break;
                            case '03':
                                str = '传感器';
                                break;
                            case '04':
                                str = '控制器';
                                break;
                            default:
                                break;
                        }
                    }
                    return (<span>{str}</span>)
                }
            },
            {
                title: '设备名称',
                dataIndex: 'name'
            },
            {
                title: '设备状态',
                dataIndex: 'wayStatus',
                render: (text, record, index) => {
                    let str = '';
                    if (record.wayStatus != undefined) {
                        switch (record.wayStatus) {
                            case 0:
                                str = '正常';
                                break;
                            case 1:
                                str = '离线';
                                break;
                            case 2:
                                str = '断电';
                                break;
                            case 3:
                                str = '缺相';
                                break;
                            case 4:
                                str = '数据异常';
                                break;
                            default:
                                break;
                        }
                    }
                    return (<span>{str}</span>)
                }
            },
            {
                title: '操作',
                // dataIndex: 'keyword',
                render: (text, record, index) => {
                    return <span>
                        <span
                            onClick={() => { this.modifyInfo(record, index) }}>
                            <a href="javascript:void(0);" style={{ marginRight: '15px' }}>修改</a>
                        </span>
                        <Popconfirm title="确认要删除嘛?" onConfirm={() => this.onDelete([record.device_sn + ''])}>
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
                        <Col span={2}>
                            <Button type="primary" onClick={this.doSearch} >查询</Button>
                        </Col>

                    </Row>
                    <Row>
                        <Col span={5} style={{ height: '32px', lineHeight: '32px' }}>
                            所有者：{this.props.user}
                        </Col>
                    </Row>
                </Card>
                <Card bordered={false}>
                    <div>
                        <div >
                            <Button onClick={this.showAddModal}>
                                新建设备
                            </Button>
                            <Popconfirm title="确认要删除嘛?" onConfirm={() => this.onDelete(this.state.selectedRowKeys)}>
                                <Button className={styles.deletebutton} > 删除设备</Button>
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
                <AddEquipment
                    modifyId={this.state.modifyId}
                    visible={this.props.showAddModal}
                    onOk={this.onOk}
                    wrapClassName='vertical-center-modal'
                    onCancel={this.onCancel}
                />
                <Button type="primary" style={{ float: 'right', marginTop: '10px' }} onClick={() => { history.back() }}>
                    返回上一页
                </Button>
            </PageHeaderLayout>
        );
    }
}
