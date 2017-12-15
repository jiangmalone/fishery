import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Table, Row, Col, Card, Input, Icon, Button, InputNumber, Modal, message, Popconfirm } from 'antd';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import styles from "./equipmentManagement.less"
export default class EquipmentManagement extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            loading: false,
            showAddModal: false
        }
    }
    state = {
        loading: false,
        showAddModal: false
    }
    

    showAddModal = () => {
        console.log("showAddModal");
        this.setState = ({ 
            showAddModal: true
        },()=> {console.log(this.showAddModal)})
    }

    addCompany = () => {
        console.log("addCompany");
    }

    render() {
        const columns = [
            {
                title: '序号',
                dataIndex: 'index'
            },
            {
                title: '设备编号',
                dataIndex: 'number',
                // render: (text, record, index) => {
                //     <Link to="/userManage/step-form">{text}</Link>
                // },
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
                            <Button className={styles.button}>删除设备</Button>
                        </div>
                        <Table
                            loading={this.state.loading}
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
                            名称 ：
                                </Col>
                        <Col span={8}>
                            <Input  onChange={(e) => this.onValueChange('parkingLocation', e.target.value)} />
                        </Col>
                    </Row>
                </Modal>
            </PageHeaderLayout>
        );
    }
}
