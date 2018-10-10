import React, { PureComponent } from 'react';
import moment from 'moment';
import { connect } from 'dva';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { Table, Card, Row, Col, Input, Button } from 'antd'
import { Link } from 'react-router-dom'

@connect(state => ({
    list: state.allequipment.list,
    loading: state.allequipment.loading,
    pagination: state.allequipment.pagination
}))

class AllEquipmentQuery extends PureComponent {

    constructor(props) {
        super(props);
        this.state = {
            device_sn: null,
            name: null
        }
    }

    componentDidMount() {
        this.onSearch()
    }

    onSearch = (page = 1) => {
        let obj = {}
        if(this.state.device_sn) {
            obj.device_sn = this.state.device_sn
        }
        if(this.state.name){
            obj.userName = this.state.name
        }
        obj.page = page;
        obj.number = 10;
        this.props.dispatch({
            type: 'allequipment/fetch',
            payload: obj
        })
    }

    handleTableChange = (pagination) => {
        const pager = { ...this.props.pagination };
        pager.current = pagination.current;
        this.onSearch(pagination.current)
        // this.props.dispatch({
        //     type: 'allequipment/changeLoading',
        //     payload: { pagination: pager }
        // })
    }


    render() {
        const { list, loading } = this.props;

        const columns = [
            {
                key: 'index',
                title: '序号',
                dataIndex: 'index',
                render: (text, record, index) => {
                    return <span>{index + 1}</span>
                }
            },
            {
                key: 'device_sn',
                title: '设备编号',
                dataIndex: 'device_sn',
                render: (text, record, index) => {
                    return <Link to={`/equipment/detail/${text}/${record.relation}/${record.type}`}>{text}</Link>
                  // return <Link to={`/equipment/detail/${text}/${record.relation}/${3}`}>{text}</Link>
                },
            },
            {
                key: 'name',
                title: '设备名称',
                dataIndex: 'name',
            },
            {
                key: 'type',
                title: '设备类型',
                dataIndex: 'type',
                render: (text, record, index) => {
                    let str = ''
                    switch (text) {
                        case 1: str = '传感器'; break;
                        case 3: str = '控制器'; break;
                    }
                    return <span>{str}</span>
                }
            },
            {
                key: 'userName',
                title: '所属者',
                dataIndex: 'userName',
                render:(text,record,index)=>{
                    if(record.relation&&record.relation.slice(0,2)=='CO'){
                        return <Link to={`/userManage/company-user/${record.relation.slice(2)}/${record.relation}`}>{record.userName}</Link>
                    } else if (record.relation&&record.relation.slice(0,2)=='WX') {
                        return <Link to={`/userManage/common-user/${record.relation}`}>{record.userName}</Link>
                    } else {
                        return <span>{text}</span>
                    }
                }
            },
            {
                key: 'status',
                title: '设备状态',
                dataIndex: 'status',
                render: (text, record, index) => {
                    let str = ''
                    if (record.type==1) {
                        switch (text) {
                            case 0: str = '离线'; break;
                            case 1: str = '在线'; break;
                            case 2: str = '异常'; break;
                        }
                    }else{
                        switch (text) {
                            case 0: str = '离线'; break;
                            case 1: str = '在线'; break;
                            case 2: str = '异常'; break;
                        }
                    }
                    return <span>{str}</span>
                }
            }
        ];
        return (
            <PageHeaderLayout>
                <Card bordered={false}>
                    <Row style={{ marginBottom: '48px' }}>
                        <Col span={8}>设备编号：<Input style={{ width: 200 }} onChange={(e) => {
                            this.setState({
                                device_sn: e.target.value
                            })
                        }} /></Col>
                        <Col span={8}>所有者名称：<Input style={{ width: 200 }} onChange={(e) => {
                            this.setState({
                                name: e.target.value
                            })
                        }} /></Col>
                        <Button type="primary" onClick={() => { this.onSearch(1) }}>查询</Button>
                    </Row>
                    <Table loading={loading}
                        dataSource={this.props.list}
                        columns={columns}
                        rowKey={record=>record.dataIndex}
                        pagination={this.props.pagination}
                        bordered
                        onChange={this.handleTableChange}
                    />
                </Card>
            </PageHeaderLayout>
        );
    }
}

export default AllEquipmentQuery
