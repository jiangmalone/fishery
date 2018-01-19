import React, { PureComponent } from 'react';
import moment from 'moment';
import { connect } from 'dva';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { Table, Card, Row, Col, Input, Button } from 'antd'
import {queryPond} from '../../services/pond.js'

const Search = Input.Search;
@connect(state => ({
    list: state.userDetail.list,
    loading: state.userDetail.loading,
    pagination: state.userDetail.pagination
}))

class UserInfo extends PureComponent {
    constructor(props) {
        super(props)
        this.state = {
            pondList:[],
            equipmentList:[],
            userInfo:{},
            pondPagination:{},
            euipPagination:{}
        }
    }
    componentDidMount() {
        this.onSearchUserDetail();
        this.onSearchUserPond();
        this.onSearchUserEquipment();
    }

    onSearchUserDetail = () => {
        
    }
    onSearchUserPond = ()=>{
        queryPond({
            relation:this.props.match.id,
            name:'',
            page:1,
            number:10
        }).then((response)=>{
            if(response.code =='0') {
                console.log(response.data)
                this.setState({
                    pondList:response.data,
                    pondPagination:{
                        total:response.realSize
                    }
                })
            }
        })
    }
    onSearchUserEquipment = ()=>{

    }

    render() {
        const { list, loading } = this.props;

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
                render:(text,record,index)=>{
                    return <span>{index+1}</span>
                }
            },
            {
                title: '设备编号',
                dataIndex: 'device_sn',
                render: (text, record, index) => {
                    return <Link to={`/equipment/detail/${text}`}>{text}</Link>
                },
            },
            {
                title: '设备名称',
                dataIndex: 'name',
            },
            {
                title: '所属企业',
                dataIndex: 'companyName',
            },
            {
                title: '设备状态',
                dataIndex: 'status',
                render:(text,record,index)=>{
                     
                    switch(text) {
                        case 0:text='正常';break;
                        case 1:text = '离线';break;
                        case 2:text = '断电';break;
                        case 3:text = '缺相';break;
                        case 4:text = '数据异常';break;
                    }
                    return <span>{text}</span>
                }
            }
        ];
        return (
            <PageHeaderLayout>
                <Card title="用户信息" bordered={false} style={{ marginBottom:'20px'}}>
                    <Row type="flex" justify="space-between" style={{ marginBottom: '15px' }}>
                        <Col span={4}>用户名称：涵抱抱</Col>
                        <Col span={4}>性别：女</Col>
                        <Col span={4}>联系方式：18362981113</Col>
                    </Row>
                    <Row type="flex" justify="space-between" style={{ marginBottom: '15px' }}>
                        <Col span={4}>养殖年限：12</Col>
                        <Col span={4}>塘口位置：南京市玄武大道1号</Col>
                        <Col span={4}></Col>
                    </Row>

                </Card>
                <Card title="塘口信息" bordered={false} style={{ marginBottom:'20px'}}>
                    <Table loading={loading}
                        dataSource={this.state.pondList}
                        columns={columns}
                        pagination={this.state.pondPagination}
                        bordered
                    />
                </Card>

                <Card title="设备信息" bordered={false} style={{ marginBottom:'20px'}}>
                    <Table loading={loading}
                        dataSource={this.state.equipmentList}
                        columns={columns1}
                        pagination={this.state.euipPagination}
                        bordered
                    />
                </Card>
            </PageHeaderLayout>
        );
    }
}


export default UserInfo