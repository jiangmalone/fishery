import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Table, Row, Col, Card, Input, Icon, Button, InputNumber, Modal, message, Popconfirm } from 'antd';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import styles from "./equipmentManagement.less"
import update from 'immutability-helper'
export default class EquipmentManagement extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            loading: false,
            showAddModal: false,
            rowSelection: [],
            addForm: {
                name: "",
                number: "",
                type: "",
            }
        }
    }

    showAddModal = () => {
        this.setState({ 
            showAddModal: true
        })
    }

    addCompany = () => {
        const addForm = this.state.addForm;
        if(!addForm.name) {
            message.warn("请输入设备名称");
        } else if (!addForm.number) {
            message.warn("请输入设备编号");
        } else if (!addForm.type) {
            message.warn("请输入设备类型");
        } else {
            message.success("添加成功");
            this.setState({
                addForm: {
                    name: "",
                    number: "",
                    type: "",
                },
                showAddModal: false,
            })
        }
    }

    onValueChange = (key, value) => {
        if(key) {
            this.setState({
                addForm: update(this.state.addForm, { [key]: { $set: value } })
            })
        }
    }

    render() {
        const rowSelection = {
            onChange: (selectedRowKeys, selectedRows) => {
                //selectedRowKeys  key-->id
                this.setState({
                    selectedRowKeys: selectedRowKeys
                })
            }
        };
        const columns = [
            {
                title: '序号',
                dataIndex: 'index'
            },
            {
                title: '设备编号',
                dataIndex: 'number',
                render: (text, record, index) => {
                    return <Link to={`equipment/${index}`}>{text}</Link>
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

                        <span onClick={() => { this.modifyInfo(record, index) }}> <a href="javascript:void(0);" style={{ marginRight: '15px' }}>修改</a></span>
                        <Popconfirm title="确认要删除嘛?" onConfirm={() => this.onDelete([record.account])}>
                            <a href="javascript:void(0);">删除</a>
                        </Popconfirm>
                    </span>
                }
            },
        ];
        const data = [{
            name: '涡轮增压机',
            index: "1",
            number: 88888888,
            type: '反人类设备',
            state: "即将开启",
            createTime: "2017-12-14",
            key: 1
        }];
        return (
            <PageHeaderLayout >
                <Card bordered={false}>
                <Row>
                <Col span={7}>
                    <div style={{ marginBottom: 16 }}>
                        设备编号：&nbsp;
                        <Input style={{ width: '200px' }} />
                    </div>
                </Col>
                <Col span={7}>
                    <div style={{ marginBottom: 16 }}>
                        设备名称：&nbsp;
                        <Input style={{ width: '200px' }} />
                    </div>
                </Col>
                <Col span={2}>
                    <Button type="primary" >查询</Button>
                </Col>
            </Row>
                </Card>
                <Card bordered={false}>
                    <div>
                        <div >
                            <Button onClick={this.showAddModal}>
                                新建设备
                            </Button>
                            <Button className={styles.deletebutton}>删除设备</Button>
                        </div>
                        <Table
                            loading={this.state.loading}
                            rowSelection={rowSelection}
                            dataSource={data}
                            columns={columns}
                            className={styles.table}
                            bordered
                        />
                    </div>
                </Card>
                <Modal
                    title="新增设备"
                    visible={this.state.showAddModal}
                    onOk={() => this.addCompany()}
                    onCancel={() => this.setState({ showAddModal: false })}
                >
                    <Row gutter={16} style={{ marginBottom: '10px' }}>
                        <Col span={8} style={{ textAlign: 'right' }}>
                            设备名称 ：
                                </Col>
                        <Col span={8}>
                            <Input value={this.state.addForm.name}  onChange={(e) => this.onValueChange('name', e.target.value)} />
                        </Col>
                    </Row>
                    <Row gutter={16} style={{ marginBottom: '10px' }}>
                        <Col span={8} style={{ textAlign: 'right' }}>
                            设备编号 ：
                                </Col>
                        <Col span={8}>
                            <Input value={this.state.addForm.number}  onChange={(e) => this.onValueChange('number', e.target.value)} />
                        </Col>
                    </Row>
                    <Row gutter={16} style={{ marginBottom: '10px' }}>
                        <Col span={8} style={{ textAlign: 'right' }}>
                            设备类型 ：
                                </Col>
                        <Col span={8}>
                            <Input value={this.state.addForm.type}  onChange={(e) => this.onValueChange('type', e.target.value)} />
                        </Col>
                    </Row>
                </Modal>
            </PageHeaderLayout>
        );
    }
}
