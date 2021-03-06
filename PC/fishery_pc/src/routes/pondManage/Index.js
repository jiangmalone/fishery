import React, { PureComponent } from 'react';
import moment from 'moment';
import { connect } from 'dva';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { Table, Card, Row, Col, Input, Button, Popconfirm } from 'antd';
import { Link } from 'react-router-dom';
import Addmodal from './Addmodal.js';
import Mapmoal from './MapModal.js';
import { pondFish } from '../../services/pond';

const Search = Input.Search;
@connect(state => ({
    list: state.pond.list,
    loading: state.pond.loading,
    pagination: state.pond.pagination,
    modalVisible: state.pond.modalVisible,
    mapVisible: state.pond.mapVisible,
    address: state.pond.address,
    fishCategories: state.pond.fishCategories,
    user: state.pond.user
}))

class PondList extends PureComponent {
    constructor(props) {
        super(props);
        this.state = {
            view: false,
            selectedRows: [],
            selectedRowKeys: [],
            mode: 'add',
            index: '',
            modifyId: '',
        }
    }

    componentDidMount() {
        this.onSearch()
        this.props.dispatch({
            type: 'pond/fetchFishList'
        })
    }


    modifyInfo = (record, index, isDetail) => {
        let formData = {}
        for (let key in record) {
            formData[key] = {
                value: record[key],
                name: key
            }
            if (key = 'pondFishs') {
                let modifyFishes = []
                for (let item of record['pondFishs']) {
                    modifyFishes.push(item.fish_name + '-' + item.type)
                }
                formData[key] = {
                    value: modifyFishes,
                    name: key
                }
            }
        }
        this.props.dispatch({
            type: 'pond/changeModal',
            payload: {
                formData: { fields: formData }
            }
        })
        this.showAddModal('modify', index, record)
    }

    showAddModal = (mode = 'add', index, record) => {
        if (mode == 'add') {
            this.props.dispatch({
                type: 'pond/changeModal',
                payload: {
                    formData: { fields: {} },
                    address: ''
                }
            })
        } else {
            this.props.dispatch({
                type: 'pond/changeModal',
                payload: {
                    address: {
                        address: record.address, location: {
                            lat: record.latitude,
                            lng: record.longitude
                        }
                    }
                }
            })
        }
        this.props.dispatch({
            type: 'pond/changeModal',
            payload: {
                modalVisible: true,
            },
        });
        this.setState({
            mode: mode,
            index: index,
            modifyId: record ? record.id : ''
        })
    }

    onSearch = (value) => {
        this.props.dispatch({
            type: 'pond/fetch',
            payload: {
                name: value,
                relation: this.props.match.params ? this.props.match.params.relation : '',
                number: 10,
                page: 1
            },
        })
    }

    onDelete = (idArray) => {
        this.setState({
            selectedRowKeys: []
        })
        this.props.dispatch({
            type: 'pond/deletePond',
            payload: {
                pondIds: idArray,
                relation: this.props.match.params ? this.props.match.params.relation : '',
                pagination: this.props.pagination
            },
        });
    }

    handleTableChange = (pagination) => {
        const pager = { ...this.props.pagination };
        pager.current = pagination.current;
        this.props.dispatch({
            type: 'pond/fetch',
            payload: {
                number: 10,
                relation: this.props.match.params ? this.props.match.params.relation : '',
                page: pagination.current,
            },
        });
        this.props.dispatch({
            type: 'pond/changeModal',
            payload: { pagination: pager }
        })
    }

    render() {
        const { list, loading, fishCategories, modalVisible, address } = this.props;
        const modalProps = {
            visible: modalVisible,
            wrapClassName: 'vertical-center-modal',
            address: address,
            modifyId: this.state.modifyId,
            fishCategories: fishCategories,
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
            onOk: (values) => {
                if (!this.state.modifyId && this.state.modifyId !== 0) {

                    let newFishs = []
                    values.relation = this.props.match.params.relation;
                    values.address = (this.props.address.district ? this.props.address.district : '') + (this.props.address.address?this.props.address.address:'') + (this.props.address.name ? this.props.address.name : '');
                    values.latitude = this.props.address.location ? this.props.address.location.lat : '';
                    values.longitude = this.props.address.location ? this.props.address.location.lng : '';
                    if (values.pondFishs) {
                        for (let item of values.pondFishs) {
                            let index = item.indexOf('-');
                            newFishs.push({
                                type: Number(item.slice(index + 1)),
                                fish_name: item.slice(0, index)
                            })
                        }
                    }
                    values.pondFishs = newFishs
                    this.props.dispatch({
                        type: 'pond/addPond',
                        payload: values,
                    });
                } else {

                    let modifyFishes = []
                    values.id = this.state.modifyId;
                    values.address = (this.props.address.district ? this.props.address.district : '') + (this.props.address.address ? this.props.address.address : '') + (this.props.address.name ? this.props.address.name : '');
                    values.latitude = this.props.address.location.lat;
                    values.longitude = this.props.address.location.lng;
                    values.relation = this.props.match.params.relation;
                    if (values.pondFishs) {
                        for (let item of values.pondFishs) {
                            let index = item.indexOf('-');
                            modifyFishes.push({
                                type: Number(item.slice(index + 1)),
                                fish_name: item.slice(0, index)
                            })
                        }
                    }

                    values.pondFishs = modifyFishes
                    this.props.dispatch({
                        type: 'pond/modifyPond',
                        payload: {
                            index: this.state.index,
                            data: values,
                        },
                    });
                }
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
                        address: address
                    },
                });
            },
        };
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
        const columns = [{
            title: '序号',
            dataIndex: 'index',
            key: 'index',
            render: (text, record, index) => {
                return <span>{index + 1}</span>
            }
        }, {
            title: '塘口名称',
            dataIndex: 'name',
            key: 'name',
            render: (text, record, index) => {
                return <Link to={`detail/${record.id}`}>{text}</Link>
            }
        }, {
            title: '面积（亩）',
            dataIndex: 'area',
            key: 'area',
        }, {
            title: '深度（m）',
            dataIndex: 'depth',
            key: 'depth',
        }, {
            title: '品种',
            dataIndex: 'pondFishs',
            key: 'pondFishs',
            render: (text, record, index) => {
                let fish = ''
                for (let item of text) {
                    fish = fish + item.fish_name + "、";
                }

                return <span>{fish ? fish.slice(0, -1) : ''}</span>
            }
        }, {
            title: '池塘水源',
            dataIndex: 'water_source',
            key: 'water_source',
        }, {
            title: '泥底厚度（cm）',
            key: 'sediment_thickness',
            dataIndex: 'sediment_thickness'
        }, {
            title: '塘口密度(㎏/亩)',
            key: 'density',
            dataIndex: 'density'
        }, {
            title: '操作',
            dataIndex: 'keyword',
            render: (text, record, index) => {
                return <span>
                    <span> <a href="javascript:void(0);" onClick={() => { this.modifyInfo(record, index) }} style={{ marginRight: '15px' }}>修改</a></span>
                    <Popconfirm title="确认要删除嘛?" onConfirm={() => this.onDelete([record.id + ''])}>
                        <a href="javascript:void(0);">删除</a>
                    </Popconfirm>
                </span>
            }
        }];
        return (
            <PageHeaderLayout>
                <Card bordered={false}>
                    <Row style={{ marginBottom: '20px' }}>
                        <Col span={12}>塘口名称：<Search style={{ width: 200 }} onSearch={value => this.onSearch(value)} enterButton="查询" /></Col>

                    </Row>
                    <Row style={{ marginBottom: '48px' }}>
                        <Col span={12}>所有者：{this.props.user}</Col>
                    </Row>
                    <Row style={{ marginBottom: '15px' }}>
                        <Button onClick={() => this.showAddModal()}>新增塘口</Button>
                        <Popconfirm title="确认要删除嘛?" onConfirm={() => this.onDelete(this.state.selectedRowKeys)}>
                            <Button style={{ marginLeft: '10px' }} >删除塘口</Button>
                        </Popconfirm>
                    </Row>
                    <Addmodal {...modalProps} />
                    <Table loading={loading}
                        rowSelection={rowSelection}
                        dataSource={list}
                        columns={columns}
                        pagination={this.props.pagination}
                        onChange={this.handleTableChange}
                        bordered
                    />
                    <Mapmoal {...mapModalProps} />
                </Card>
                <Button type="primary" style={{ float: 'right', marginTop: '10px' }} onClick={() => { history.back() }}>
                    返回上一页
                </Button>
            </PageHeaderLayout>
        );
    }
}


export default PondList