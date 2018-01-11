import React, { PureComponent } from 'react';
import moment from 'moment';
import { connect } from 'dva';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { Table, Card, Row, Col, Input, Button, Popconfirm } from 'antd'
import { Link } from 'react-router-dom'

const Search = Input.Search;
@connect(state => ({
    pondList: state.pond.pondList,
    loading: state.pond.loading,
    pagination2: state.pond.pagination2,
}))

class PondDetail extends PureComponent {
    componentDidMount() {
        console.log(this.props.match)
        this.props.dispatch({
            type: 'pond/fetchEquipment',
            payload: {
                pondId: this.props.match.params.id,
                page: 1,
                number: 10
            },
        });
    }

    render() {
        const { pondList, loading, pagination2 } = this.props;

        const columns = [{
            title: '序号',
            dataIndex: 'index',
            key: 'index',
            render: (text, record, index) => {
                return <span>{index + 1}</span>
            }
        }, {
            title: '设备编号',
            dataIndex: 'device_sn',
            key: 'device_sn',
            render: (text, record, index) => {
                return <Link to={`${index}`}>{text}</Link>
            }
        }, {
            title: '设备名称',
            dataIndex: 'name',
            key: 'name',
        }, {
            title: '设备状态',
            dataIndex: 'status',
            key: 'status',
        }, {
            title: '操作',
            dataIndex: 'keyword',
            render: (text, record, index) => {
                return <span>
                    <Popconfirm title="确认要解绑嘛?" onConfirm={() => this.onDelete([record.account])}>
                        <a href="javascript:void(0);">解绑</a>
                    </Popconfirm>
                </span>
            }
        }];
        return (
            <PageHeaderLayout>
                <Card title="塘口信息" bordered={false} style={{ marginBottom: '20px' }}>
                    <Row type="flex" justify="space-between" style={{ marginBottom: '15px' }}>
                        <Col span={4}>塘口名称：{'value' || ''}</Col>
                        <Col span={4}>面积（亩）：{'value' || ''}</Col>
                        <Col span={4}>深度（m）：{'value' || ''}</Col>
                    </Row>
                    <Row type="flex" justify="space-between" style={{ marginBottom: '15px' }}>
                        {/* <Col span={4}>养殖品种：{formData.fields.fish_categorys}</Col> */}
                        <Col span={4}>池塘水源：{'value' || ''}</Col>
                        <Col span={4}>底泥厚度(cm)：{'value' || ''}</Col>
                    </Row>
                    <Row type="flex" justify="space-between" style={{ marginBottom: '15px' }}>
                        <Col span={4}>塘口密度(㎏/㎡)：{'value' || ''}</Col>
                        <Col span={4}>塘口位置：{'value' || ''}</Col>
                        <Col span={4}></Col>
                    </Row>
                </Card>
                <Card title="绑定设备" bordered={false} style={{ marginBottom: '20px' }}>
                    <Table loading={loading}
                        dataSource={pondList}
                        columns={columns}
                        pagination={pagination2}
                        bordered
                    />
                </Card>
            </PageHeaderLayout>
        );
    }
}


export default PondDetail