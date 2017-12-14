import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Table, Row, Col, Card, Input, Icon, Button, InputNumber, Modal, message, Popconfirm } from 'antd';
const Search = Input.Search;
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import styles from "./companyUserList.less"
export default class CompanyUserList extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            loading: false,
            showAddModal: false
        }
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
                title: '名称',
                dataIndex: 'name',
                // render: (text, record, index) => {
                //     <Link to="/userManage/step-form">{text}</Link>
                // },
            },
            {
                title: '联系方式',
                dataIndex: 'connect',
            },
            {
                title: '联系地址',
                dataIndex: 'address',
            },
            {
                title: '养殖年限',
                dataIndex: 'years',
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
            name: '杨威的企业',
            index: "1",
            connect: 888988888,
            address: '中南海',
            years: 12,
            createTime: "2017-12-14",
            key: 1
        }];
        return (
            <PageHeaderLayout >
                <Card bordered={false}>
                    <Row>

                        <Col>企业名称: &nbsp;<Search style={{ width: 200 }} placeholder="" enterButton="查询" /></Col> 
                    </Row>
                </Card>
                <Card bordered={false}>
                    <div>
                        <div >
                            <Button onClick={this.showAddModal}>
                                新建企业
                            </Button>
                            <Button className={styles.button}>删除企业</Button>
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
                    title="新增企业"
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
                    <Row gutter={16} style={{ marginBottom: '10px' }}>
                        <Col span={8} style={{ textAlign: 'right' }}>
                            联系方式：
                                </Col>
                        <Col span={8}>
                            <Input  onChange={(e) => this.onValueChange('parkingLocation', e.target.value)} />
                        </Col>
                    </Row>
                    <Row gutter={16} style={{ marginBottom: '10px' }}>
                        <Col span={8} style={{ textAlign: 'right' }}>
                            养殖年限：
                                </Col>
                        <Col span={8}>
                            <Input type="textarea" rows={3}  onChange={(e) => this.onValueChange('comment', e.target.value)} />
                        </Col>
                    </Row>
                    <Row gutter={16} style={{ marginBottom: '10px' }}>
                        <Col span={8} style={{ textAlign: 'right' }}>
                            联系地址:
                                </Col>
                        <Col span={8}>
                            <Input type="textarea" rows={3}  onChange={(e) => this.onValueChange('comment', e.target.value)} />
                        </Col>
                    </Row>
                </Modal>
            </PageHeaderLayout>
        );
    }
}
