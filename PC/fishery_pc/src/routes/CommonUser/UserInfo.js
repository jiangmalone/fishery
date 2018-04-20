import React, { PureComponent } from 'react';
import moment from 'moment';
import { connect } from 'dva';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { Table, Card, Row, Col, Input, Button, Popconfirm, message } from 'antd';
import { wxuserDetail, relationDetail } from '../../services/user.js';
import { myEquipment } from '../../services/equipment.js';
import { Link } from 'react-router-dom';
import update from 'immutability-helper';
import Addmodal from '../pondManage/Addmodal.js';
import Mapmoal from '../pondManage/MapModal.js';
import { queryPond, addPond, modifyPond, delPonds, pondEquipment, pondFish } from '../../services/pond.js';
const Search = Input.Search;
@connect(state => {
    return ({
        list: state.userDetail.list,
        loading: state.userDetail.loading,
        pagination: state.userDetail.pagination,
        modalVisible: state.pond.modalVisible,
        mapVisible: state.pond.mapVisible,
        address: state.pond.address,
        fishCategories: state.pond.fishCategories
    })
})

class UserInfo extends PureComponent {
    constructor(props) {
        super(props)
        this.state = {
            pondList: [],
            equipmentList: [],
            userInfo: {},
            pondPagination: {},
            euipPagination: {},
            modifyId: ''
        }
    }
    componentDidMount() {
        this.props.dispatch({
            type: 'pond/fetchFishList'
        })
        this.onSearchUserDetail();
        this.onSearchUserPond();
        this.onSearchUserEquipment();
    }

    onSearchUserDetail = () => {
        relationDetail({
            relation: this.props.match.params.id
        }).then((res) => {
            this.setState({
                userInfo: res.data
            })
        }).catch((error) => {
            console.log(error)
        })
    }
    onSearchUserPond = (page = 1) => {
        queryPond({
            relation: this.props.match.params.id,
            name: '',
            page: page,
            number: 10
        }).then((response) => {
            if (response.code == '0') {
                for (let item of response.data) {
                    item.key = item.id
                }
                this.setState({
                    pondList: response.data,
                    pondPagination: update(this.state.pondPagination, { total: { $set: response.realSize } })
                })
            }
        }).catch((error) => { console.log(error) })
    }


    onSearchUserEquipment = (page = 1) => {
        myEquipment({
            relation: this.props.match.params.id,
            page: page,
            number: 10
        }).then((res) => {
            for (let item of res.data) {
                item.key = item.id
            }
            this.setState({
                equipmentList: res.data,
                euipPagination: update(this.state.euipPagination, { total: { $set: res.realSize } })
            })
        }).catch((error) => {
            console.log(error)
        })
    }

    handleTableChange2 = (pagination) => {
        const pager = { ...this.state.euipPagination };
        pager.current = pagination.current;
        this.onSearchUserEquipment(pagination.current);
        this.setState({
            equipPagination: pager
        })
    }

    handleTableChange1 = (pagination) => {
        const pager = { ...this.state.pondPagination };
        pager.current = pagination.current;
        this.onSearchUserPond(pagination.current);
        this.setState({
            pondPagination: pager
        })
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
            if(record.address){
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

    onDelete = (idArray) => {
        delPonds({ pondIds: idArray }).then(() => {
            this.onSearchUserPond();
        }).catch((error) => {
            console.log(error)
        })
    }
    render() {
        let { list, loading, fishCategories, modalVisible, address } = this.props;

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
                return <Link to={`/userManage/pondManage/detail/${record.id}`}>{text}</Link>
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
                let fishs = '';
                if(text.length>0) {
                    for (let item of text) {
                        fishs = fishs + item.fish_name + '、'
                    }
                    fishs = fishs.slice(0,-1);
                }
                return <div>{text ? fishs : ''}</div>
            }
        }, {
            title: '池塘水源',
            dataIndex: 'water_source',
            key: 'water_source',
        }, {
            title: '泥底厚度',
            key: 'sediment_thickness',
            dataIndex: 'sediment_thickness'
        }, {
            title: '塘口密度（kg/亩）',
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
                    values.relation = this.props.match.params.id;
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
                    values.pondFishs = newFishs;
                    addPond(values).then((response) => {
                        if (response.code == '0') {
                            this.onSearchUserPond();
                        } else {
                            message.error(response.msg, 1)
                        }
                    })
                } else {
                    let modifyFishes = []
                    values.id = this.state.modifyId;
                    values.address = (this.props.address.district ? this.props.address.district : '') + (this.props.address.address?this.props.address.address:'') + (this.props.address.name ? this.props.address.name : '');
                    values.latitude = this.props.address.location?this.props.address.location.lat:'';
                    values.longitude = this.props.address.location?this.props.address.location.lng:'';
                    values.relation = this.props.match.params.id;
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
                    modifyPond(values).then((response) => {
                        if (response.code == '0') {
                            this.onSearchUserPond();
                        }
                    })
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

        const columns1 = [
            {
                title: '序号',
                dataIndex: 'index',
                render: (text, record, index) => {
                    return <span>{index + 1}</span>
                }
            },
            {
                title: '设备编号',
                dataIndex: 'device_sn',
                render: (text, record, index) => {
                    console.log(record);
                    return <Link to={`/equipment/detail/${text}/${this.props.match.params.id}/${record.id}`}>{text}</Link>
                },
            },
            {
                title: '设备名称',
                dataIndex: 'name',
            },
            {
                title: '设备状态',
                dataIndex: 'wayStatus',
                render: (text, record, index) => {
                    switch (text) {
                        case 0: text = '正常'; break;
                        case 1: text = '离线'; break;
                        case 2: text = '断电'; break;
                        case 3: text = '缺相'; break;
                        case 4: text = '数据异常'; break;
                    }
                    return <span>{text}</span>
                }
            }
        ];
        return (
            <PageHeaderLayout>
                <Card title="用户信息" bordered={false} style={{ marginBottom: '20px' }}>
                    <Row type="flex" justify="space-between" style={{ marginBottom: '15px' }}>
                        <Col span={4}>用户名称：{this.state.userInfo.name}</Col>
                        <Col span={4}>性别：{this.state.userInfo.sex}</Col>
                        <Col span={4}>联系方式：{this.state.userInfo.phone}</Col>
                    </Row>
                    <Row type="flex" justify="space-between" style={{ marginBottom: '15px' }}>
                        <Col span={4}>养殖年限：{this.state.userInfo.life}年</Col>
                        <Col span={4}>联系地址：{this.state.userInfo.address}</Col>
                        <Col span={4}></Col>
                    </Row>

                </Card>

                <Card title="塘口信息" bordered={false} style={{ marginBottom: '20px' }}>
                    <Button onClick={() => {
                        this.showAddModal()
                    }} style={{ marginBottom: '10px' }}>新增塘口</Button>
                    <Table loading={loading}
                        dataSource={this.state.pondList}
                        columns={columns}
                        pagination={this.state.pondPagination}
                        onChange={this.handleTableChange1}
                        bordered
                    />
                </Card>
                <Addmodal {...modalProps} />
                <Mapmoal {...mapModalProps} />
                <Card title="设备信息" bordered={false} style={{ marginBottom: '20px' }}>
                    <Table loading={loading}
                        dataSource={this.state.equipmentList}
                        columns={columns1}
                        pagination={this.state.euipPagination}
                        bordered
                        onChange={this.handleTableChange2}
                    />
                </Card>
            </PageHeaderLayout>
        );
    }
}


export default UserInfo