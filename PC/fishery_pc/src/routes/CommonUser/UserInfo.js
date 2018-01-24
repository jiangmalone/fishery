import React, { PureComponent } from 'react';
import moment from 'moment';
import { connect } from 'dva';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { Table, Card, Row, Col, Input, Button } from 'antd'
import { queryPond } from '../../services/pond.js'
import { wxuserDetail, relationDetail } from '../../services/user.js'
import { myEquipment } from '../../services/equipment.js'
import { Link } from 'react-router-dom'
import update from 'immutability-helper'

const Search = Input.Search;
@connect(state => {
    console.log(state); return ({
        list: state.userDetail.list,
        loading: state.userDetail.loading,
        pagination: state.userDetail.pagination
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
            euipPagination: {}
        }
    }
    componentDidMount() {

        this.onSearchUserDetail();
        this.onSearchUserPond();
        this.onSearchUserEquipment();
    }

    onSearchUserDetail = () => {
        relationDetail({
            relationId: this.props.match.params.id
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
                console.log(response.data)
                for (let item of response.data) {
                    item.key = item.id
                }
                this.setState({
                    pondList: response.data,
                    pondPagination:update(this.state.pondPagination, { total: { $set: response.realSize } })
                })
            }
        }).catch((error)=>{console.log(error)})
    }


    onSearchUserEquipment = (page = 1) => {
        myEquipment({
            relationId: this.props.match.params.id,
            page: page,
            number: 10
        }).then((res) => {
            console.log(res)
            for(let item of res.data) {
                item.key= item.id
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

    handleTableChange1 = (pagination)=>{
        const pager = {...this.state.pondPagination};
        pager.current = pagination.current;
        this.onSearchUserPond(pagination.current);
        this.setState({
            pondPagination:pager
        })
    }


    render() {
        let { list, loading } = this.props;

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
            dataIndex: 'fish_categorys',
            key: 'fish_categorys',
            render:(text,record,index)=>{
                return <div>{text?text.join(','):''}</div>
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
            title: '塘口密度（kg/㎡）',
            key: 'density',
            dataIndex: 'density'
        }];


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
                    return <Link to={`/equipment/detail/${text}/${ this.props.match.params.id}/${record.id}`}>{text}</Link>
                },
            },
            {
                title: '设备名称',
                dataIndex: 'name',
            },
            {
                title: '设备状态',
                dataIndex: 'status',
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
                        <Col span={4}>塘口位置：{this.state.userInfo.address}</Col>
                        <Col span={4}></Col>
                    </Row>

                </Card>
                <Card title="塘口信息" bordered={false} style={{ marginBottom: '20px' }}>
                    <Table loading={loading}
                        dataSource={this.state.pondList}
                        columns={columns}
                        pagination={this.state.pondPagination}
                        onChange={this.handleTableChange1}
                        bordered
                    />
                </Card>

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