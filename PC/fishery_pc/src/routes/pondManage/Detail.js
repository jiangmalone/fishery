import React, { PureComponent } from 'react';
import moment from 'moment';
import { connect } from 'dva';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { Table, Card, Row, Col, Input, Button, Popconfirm } from 'antd'
import { Link } from 'react-router-dom'
import { delSensorOrAIOBind } from '../../services/bind.js'
import { pondFish } from '../../services/pond';


const Search = Input.Search;
@connect(state => ({
    pondList: state.pond.pondList,
    loading: state.pond.loading,
    pagination2: state.pond.pagination2,
    pondInfo: state.pond.pondInfo
}))

class PondDetail extends PureComponent {
    componentDidMount() {
        this.props.dispatch({
            type: 'pond/fetchDetail',
            payload: {
                pondId: this.props.match.params.id
            }
        });
        this.props.dispatch({
            type: 'pond/fetchEquipment',
            payload: {
                pondId: this.props.match.params.id,
                page: 1,
                number: 10
            },
        });
    }

    disconnect = (device_sn, type) => {
        switch (type) {
            case '01': this.yitiQuery(device_sn, 2); break;
            case '02': this.yitiQuery(device_sn, 2); break;
            case '03': this.yitiQuery(device_sn, 1); break;
            case '04': this.kongQuery(); break;
        }
    }

    yitiQuery = (device_sn, type) => {
        delSensorOrAIOBind({
            device_sn: device_sn,
            type: type,
            pondId: this.props.match.params.id
        }).then(
            (res) => {
                if (res.code == '0') {
                    this.props.dispatch({
                        type: 'pond/fetchEquipment',
                        payload: {
                            pondId: this.props.match.params.id,
                            page: 1,
                            number: 10
                        },
                    });
                }
            }
            ).catch((error) => { console.error() });
    }

    handleTableChange = (pagination) => {
        const pager = { ...this.props.pagination2 };
        pager.current = pagination.current;
        this.props.dispatch({
            type: 'pond/fetchEquipment',
            payload: {
                pondId: this.props.match.params.id,
                page: pagination.current,
                number: 10
            },
        });
        this.props.dispatch({
            type: 'pond/changeModal',
            payload: { pagination2: pager }
        })
    }

    render() {
        const { pondList, loading, pagination2, pondInfo } = this.props;

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
                return <Link to={`/equipment/detail/${record.device_sn}/${record.relation}/${record.sensorId}`}>{text}</Link>
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
                    <Popconfirm title="确认要解绑嘛?" onConfirm={() => this.disconnect(record.device_sn, record.device_sn.slice(0, 2))}>
                        <a href="javascript:void(0);">解绑</a>
                    </Popconfirm>
                </span>
            }
        }];
        let pondFishs = ''
        if(pondInfo.pondFishs&&pondInfo.pondFishs.length>0) {
            for(let item of pondInfo.pondFishs) {
                pondFishs = pondFishs + item.fish_name + '、';
            }
        }
  
        return (
            <PageHeaderLayout>
                <Card title="塘口信息" bordered={false} style={{ marginBottom: '20px' }}>
                    <Row type="flex" justify="space-between" style={{ marginBottom: '15px' }}>
                        <Col span={4}>塘口名称：{pondInfo.name || ''}</Col>
                        <Col span={4}>面积（亩）：{pondInfo.area || ''}</Col>
                        <Col span={4}>深度（m）：{pondInfo.depth || ''}</Col>
                    </Row>
                    <Row type="flex" justify="space-between" style={{ marginBottom: '15px' }}>
                        <Col span={4}>养殖品种：{ pondFishs?pondFishs.slice(0,-1):''}</Col>
                        <Col span={4}>池塘水源：{pondInfo.water_source || ''}</Col>
                        <Col span={4}>底泥厚度(cm)：{pondInfo.sediment_thickness || ''}</Col>
                    </Row>
                    <Row type="flex" justify="space-between" style={{ marginBottom: '15px' }}>
                        <Col span={4}>塘口密度(㎏/㎡)：{pondInfo.density || ''}</Col>
                        <Col span={4}>塘口位置：{pondInfo.address || ''}</Col>
                        <Col span={4}></Col>
                    </Row>
                </Card>
                <Card title="绑定设备" bordered={false} style={{ marginBottom: '20px' }}>
                    <Table loading={loading}
                        dataSource={pondList}
                        columns={columns}
                        pagination={pagination2}
                        bordered
                        onChange={this.handleTableChange}
                    />
                </Card>
                <Button type="primary" style={{float:'right'}} onClick={()=>{history.back()}}>
                   返回上一页
                </Button>

            </PageHeaderLayout>
        );
    }
}


export default PondDetail