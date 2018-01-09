import React, { PureComponent } from 'react';
import moment from 'moment';
import { connect } from 'dva';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { Table, Card, Row, Col, Input, Button, Popconfirm } from 'antd';
import { Link } from 'react-router-dom';
import Addmodal from './Addmodal.js';
import Mapmoal from './MapModal.js';

const Search = Input.Search;
@connect(state => ({
    list: state.pond.list,
    loading: state.pond.loading,
    pagination: state.pond.pagination,
    modalVisible: state.pond.modalVisible,
    mapVisible: state.pond.mapVisible,
    address: state.pond.address
}))

class PondList extends PureComponent {
    componentDidMount() {
        this.props.dispatch({
            type: 'pond/fetch',
            payload: {
                count: 10,
            },
        });
    }

    componentWillReceiveProps(newProps) {
        // console.log(newProps)
    }

    showAddModal = () => {
        this.props.dispatch({
            type: 'pond/changeModal',
            payload: {
                modalVisible: true,
            },
        });
    }

    render() {
        const { list, loading } = this.props;
        const modalProps = {
            visible: this.props.modalVisible,
            wrapClassName: 'vertical-center-modal',
            address:this.props.address,
            onCancel: () => {
                this.props.dispatch({
                    type: 'pond/changeModal',
                    payload: {
                        modalVisible: false,
                    },
                });
            },
            showMapModal: () => {
                this.props.dispatch({
                    type: 'pond/changeModal',
                    payload: {
                        mapVisible: true,
                    },
                });
            },
            onOk: (address) => {
                this.props.dispatch({
                    type: 'pond/changeModal',
                    payload: {
                        modalVisible: false,
                    },
                });
            }
        };
        const mapModalProps = {
            visible: this.props.mapVisible,
            wrapClassName: 'vertical-center-map',
            onMapCancel: () => {
                this.props.dispatch({
                    type: 'pond/changeModal',
                    payload: {
                        mapVisible: false,
                    },
                });
            },
            onMapOk: (address) => {
                this.props.dispatch({
                    type: 'pond/changeModal',
                    payload: {
                        mapVisible: false,
                        address:address
                    },
                });
            },
        };
        const rowSelection = {
            onChange: (selectedRowKeys, selectedRows) => {
                //selectedRowKeys  key-->id
                this.setState({
                    selectedRowKeys: selectedRowKeys
                })
            }
        };
        const columns = [{
            title: '序号',
            dataIndex: 'index',
            key: 'index',
            render: (text, record, index) => {
                return <span>{index + 1}</span>
            }
        }, {
            title: '塘口名称',
            dataIndex: 'owner',
            key: 'owner',
            render: (text, record, index) => {
                return <Link to={`pondManage/${index}`}>{text}</Link>
            }
        }, {
            title: '面积（亩）',
            dataIndex: 'gender',
            key: 'gender',
        }, {
            title: '深度（m）',
            dataIndex: 'contact',
            key: 'contact',
        }, {
            title: '品种',
            dataIndex: 'time',
            key: 'time',
        }, {
            title: '池塘水源',
            dataIndex: 'address',
            key: 'address',
        }, {
            title: '泥底厚度（cm）',
            key: 'createTime',
            dataIndex: 'createTime'
        }, {
            title: '塘口密度(kg/㎡)',
            key: 'createTime1',
            dataIndex: 'createTime1'
        }, {
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
        }];
        return (
            <PageHeaderLayout>
                <Card bordered={false}>
                    <Row style={{ marginBottom: '48px' }}>
                        <Col>塘口名称：<Search style={{ width: 200 }} enterButton="查询" /></Col>
                    </Row>
                    <Row style={{ marginBottom: '15px' }}>
                        <Button onClick={this.showAddModal}>新增塘口</Button>
                        <Button style={{ marginLeft: '10px' }}>删除塘口</Button>
                    </Row>
                    <Addmodal {...modalProps} />
                    <Table loading={loading}
                        rowSelection={rowSelection}
                        dataSource={list}
                        columns={columns}
                        pagination={this.props.pagination}
                        bordered
                    />
                    <Mapmoal {...mapModalProps}/>
                </Card>
            </PageHeaderLayout>
        );
    }
}


export default PondList