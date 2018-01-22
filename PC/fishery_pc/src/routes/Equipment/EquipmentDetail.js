import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Row, Col, Card, Input, Icon, Button, Table, message } from 'antd';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { realTimeData } from '../../services/equipment'
export default class EquipmentDetail extends React.Component {

    constructor(props) {
        super(props);
        this.state={
            loading:false,  
            realTimeData: {
                receiveTime: '',
                oxygen: '',
                water_temperature: '',
                pH_value: ''
            }
        }
    }

    componentDidMount() {
        this.getRealTimeData();
    }

    getRealTimeData = () => {
        this.setState({loading: true});
        realTimeData({
            device_sn: this.props.match.params.device_sn
        }).then(res => {
            this.setState({loading: false});
            console.log(res);
            if (res.data && res.data.code == 0) {
                this.setState({realTimeData: res.data.data})
            } else {
                message(res.data.msg, 2);
            }
        }).catch(error => {
            this.setState({loading: false});
            message('请求失败，请重试！', 2);
            console.log(error);
        })
    }
   
    render() {
        const realTimeColumns = [
            {
                title: '时间',
                dataIndex: 'receiveTime'
            },
            {
                title: '溶氧(mg/L)',
                dataIndex: 'oxygen'
            },
            {
                title: '水温(℃)',
                dataIndex: 'water_temperature',
            },
            {
                title: 'PH',
                dataIndex: 'pH_value',
            }
        ];
        const realTimeData = [{
            time: "2017-08-17 12:56:33",
            DO: 6.8,
            waterT: 28,
            ph: 9.0,
            key: 1
        }]
        return (
            <PageHeaderLayout >
                <Card bordered={false}>
                    <Row style={{fontSize: 17}}>
                        <Col span={7}>设备编号: &nbsp;&nbsp; {this.props.match.params.device_sn}</Col>
                        <Col span={5}>设备名称: &nbsp; 传感器01</Col>
                        <Col span={5}>设备状态: &nbsp; 在线</Col>
                    </Row>
                </Card>
                <Card
                    bodyStyle={{ marginTop: 15 }}
                    title="最新数据"
                    bordered
                >
                    <Col span={18}>
                        <Table
                            loading={this.state.loading}
                            dataSource={realTimeData}
                            columns={realTimeColumns}
                            bordered
                            pagination={false}
                        />
                    </Col>
                    <Col span={4} offset={2} style={{paddingTop: 40}}>
                        <Link to={`/equipment/water-quality/${0}`}><Button size="large">水质曲线</Button></Link>
                    </Col>
                </Card>
            </PageHeaderLayout>
        );
    }
}
