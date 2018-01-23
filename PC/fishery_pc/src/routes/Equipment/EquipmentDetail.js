import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Row, Col, Card, Input, Icon, Button, Table, message, Select } from 'antd';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { realTimeData } from '../../services/equipment';
import { bindState } from '../../services/bind';
import { queryPond } from '../../services/pond';
import { pondWithSensorOrAIO ,delSensorOrAIOBind} from "../../services/bind";
const Option = Select.Option;

export default class EquipmentDetail extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            loading: false,
            ponds: [],
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
        this.getInfo();
        this.getPonds()
    }

    getInfo = () => {
        bindState({ device_sn: this.props.match.params.device_sn }).then((res) => {
            if (res.code == '0') {
                let state = '正常';
                switch (res.data.status) {
                    case 0: state = '正常'; break;
                    case 1: state = '离线'; break;
                    case 2: state = '断电'; break;
                    case 3: state = '缺相'; break;
                    case 4: state = '数据异常'; break;
                }
                this.setState({
                    deviceName: res.data.deviceName,
                    pondId: res.data.pondId,
                    status: state
                })
            }
        }).catch((err) => {
            console.log(err)
        })
    }

    getPonds = () => {
        queryPond({
            relationId: this.props.match.params.relation,
            page: 1,
            number: 99
        }).then((res) => {
            console.log(res)
            if (res.code == '0') {
                this.setState({
                    ponds: res.data
                })
            }
        }).catch((error) => {
            console.log(error)
        })
    }
    getRealTimeData = () => {
        realTimeData({
            device_sn: this.props.match.params.device_sn
        }).then(res => {
            console.log(res);
            if (res.code == 0) {
                this.setState({ realTimeData: res.data })
            } else {
                message(res.msg, 2);
            }
        }).catch(error => {
            console.log(error);
        })
    }

    bindPond = (v) => {
    
        let type = 1;
        if (this.props.match.params.device_sn.slice(0, 2) == '01' || this.props.match.params.device_sn.slice(0, 2) == '02') {
            type = 2
        } else if (this.props.match.params.device_sn.slice(0, 2) == '03') {
            type = 1
        }
        if(v==0) {
            
        } else {
            this.setState({
                pondId: v
            })
            pondWithSensorOrAIO({
                device_sn: this.props.match.params.device_sn,
                pondId: v,
                type: type
            }).then((res) => {
                if (res.code !== 0) {
                    message.error(res.msg, 2);
                } else {
                    message.success(res.msg, 2)
                }
            }).catch((error) => {
                console.log(error)
            })
        }
    }

    disBind=()=>{
        let type = 1;
        if (this.props.match.params.device_sn.slice(0, 2) == '01' || this.props.match.params.device_sn.slice(0, 2) == '02') {
            type = 2
        } else if (this.props.match.params.device_sn.slice(0, 2) == '03') {
            type = 1
        }
        delSensorOrAIOBind({
            device_sn: this.props.match.params.device_sn,
            pondId: this.state.pondId,
            type: type
        }).then((res)=>{
            console.log(res)
            this.getInfo()
        }).catch(()=>{

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
        }];

        let pondOptions = this.state.ponds.map((item, index) => {
            return <Option key={index} value={item.id}>{item.name}</Option>
        })
        return (
            <PageHeaderLayout >
                <Card bordered={false}>
                    <Row style={{ fontSize: 17 }}>
                        <Col span={8}>设备编号: &nbsp;&nbsp; {this.props.match.params.device_sn}</Col>
                        <Col span={8}>设备名称: &nbsp; {this.state.deviceName}</Col>
                        <Col span={8}>设备状态: &nbsp; {this.state.status}</Col>
                    </Row>
                    {Number(this.props.match.params.device_sn.slice(0, 2)) < 4 && <Row style={{ fontSize: 17, marginTop: 20 }}>
                        <Col span={12}>绑定塘口: &nbsp;  <Select
                            showSearch
                            style={{ width: 200 }}
                            placeholder="选择一个塘口"
                            optionFilterProp="children"
                            onChange={(v) => {
                                this.bindPond(v)

                            }}
                            value={this.state.pondId}
                            filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                        >
                            {pondOptions}
                            <Option value={0}>无</Option>
                        </Select></Col><Col  span={8}>{this.state.pondId>0&&<Button onClick={()=>{this.disBind()}}>解绑</Button>}</Col></Row>}
                        
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
                    <Col span={4} offset={2} style={{ paddingTop: 40 }}>
                        <Link to={`/equipment/water-quality/${0}`}><Button size="large">水质曲线</Button></Link>
                    </Col>
                </Card>
                <Card
                    bodyStyle={{ marginTop: 15 }}
                    title="绑定关系"
                    bordered
                >


                </Card>
            </PageHeaderLayout>
        );
    }
}
