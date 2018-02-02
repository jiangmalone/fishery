import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Row, Col, Card, Input, Icon, Button, Table, message, Select, Modal, Popconfirm, Radio, Switch, TimePicker } from 'antd';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { realTimeData, getAllEquipment, aeratorOnOff, autoSet } from '../../services/equipment';
import { bindState } from '../../services/bind';
import { queryPond } from '../../services/pond';
import { pondWithSensorOrAIO, delSensorOrAIOBind, sensorWithController, delBind } from "../../services/bind";
import { userInfo } from 'os';
const Option = Select.Option;
const format = 'HH:mm';
export default class EquipmentDetail extends React.Component {

    constructor(props) {
        super(props);
        let type = this.props.match.params.device_sn.slice(0, 2), way;
        if (type == '01' || type == '02') {
            type = 2;
            way = 1;
        } else if (type == '03') {
            type = 1;
            way = 0;
        } else {
            type = 0;
            way = 0;
        }
        // type  0 控制器 1 传感器 2 一体机
        this.state = {
            loading: false,
            ponds: [],
            realTimeData: [
            ],
            bindList: [],
            type: type,
            portIndex: '',
            controllers: [],   //设备列表
            ports: [],      //和设备联动的端口号
            selectControllerId: 0,
            selectPort: 0,
            way: way,
            oxygenVisible: false,
            oxygenSetForm: {
                low_limit: '',
                up_limit: '',
                high_limit: ''
            },
            timeSections: [['', '']],
            timeSectionsString: [['', '']],
            isOpen: false  //当前路的增氧是否打开
        }
    }

    componentDidMount() {
        this.getRealTimeData();
        this.getInfo();
        this.getPonds();
        this.queryEquipment();
    }

    getInfo = () => {
        bindState({
            device_sn: this.props.match.params.device_sn
        }).then((res) => {
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


                let portBinds = res.data.portBinds;
                let standardPorts = [];
                let bindPorts = [];
                if (this.state.type == 1) {
                    standardPorts = [1, 2];
                } else {
                    standardPorts = [1, 2, 3, 4];
                }
                portBinds.map((item, index) => {
                    bindPorts.push(item.port);
                })
                let difference = standardPorts.filter(x => bindPorts.indexOf(x) == -1).concat(bindPorts.filter(x => standardPorts.indexOf(x) == -1));
                difference.map((item, index) => {
                    portBinds.push({ port: item });
                })
                res.data.portBinds = portBinds;
                this.setState({
                    bindList: res.data.portBinds,
                });

            }
        }).catch((err) => {
            console.log(err)
        })
    }

    getPonds = () => {
        queryPond({
            relation: this.props.match.params.relation,
            page: 1,
            number: 99
        }).then((res) => {
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
            device_sn: this.props.match.params.device_sn,
            way: this.state.way
        }).then(res => {
            if (res.code == 0) {
                if (res.data) {
                    this.setState({ realTimeData: [res.data] })
                } else {
                    this.setState({ realTimeData: [] })
                }
            } else {
                message(res.msg, 2);
            }
        }).catch(error => {
            console.log(error);
        })
    }

    queryEquipment = () => {
        getAllEquipment({
            relation: this.props.match.params.relation,
        }).then((res) => {
            if (res && res.code == 0) {
                if (res.controller) {
                    const data = res.controller;
                    let controllers = [];
                    // 组装可用的端口 
                    data.map((item, index) => {
                        let controller = {}, usefulPorts = []
                        const allPorts = item.port_status.split('');
                        allPorts.map((post, index) => {
                            const portNum = index + 1;
                            if (post == 0) {
                                let postData = {
                                    name: ('端口' + portNum),
                                    id: portNum,
                                };
                                usefulPorts.push(postData);
                            }
                        })
                        controller = { name: item.name, id: item.id, ports: usefulPorts };
                        controllers.push(controller);
                    })
                    this.setState({ controllers: controllers })
                }
            } else {
                message.error(res.msg, 1);
            }
        }).catch((error) => {
            console.log(error)
        });
    }

    bindPond = (v) => {

        let type = 1;
        if (this.props.match.params.device_sn.slice(0, 2) == '01' || this.props.match.params.device_sn.slice(0, 2) == '02') {
            type = 2
        } else if (this.props.match.params.device_sn.slice(0, 2) == '03') {
            type = 1
        }
        if (v == 0) {

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

    disBind = () => {
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
        }).then((res) => {
            this.getInfo()
        }).catch((error) => {
            console.log(error)
        })
    }


    selectController = (id) => {
        this.setState({ selectControllerId: id })
        const controllers = this.state.controllers;
        controllers.map((controller, index) => {
            if (controller.id == id) {
                this.setState({ ports: controller.ports })
            }
        })
    }

    handleCancel = () => {
        this.setState({
            showBindModal: false,
            selectControllerId: 0,
            selectPort: 0,
            portIndex: ''
        })
    }

    //绑定控制器相关
    doBindEquipment = () => {
        if (this.state.selectControllerId <= 0) {
            message.warn('请选择控制器!', 1)
            return;
        }
        if (this.state.selectPort < 0) {
            message.warn('请选择端口!', 1)
            return;
        }
        sensorWithController({
            sensorId: this.props.match.params.id,
            sensor_port: this.state.portIndex,
            controllerId: this.state.selectControllerId,
            controller_port: this.state.selectPort
        }).then(res => {
            this.setState({ animating: false });
            if (res && res.code == 0) {
                message.success('绑定成功!', 1);
                this.setState({
                    portIndex: '',
                    selectControllerId: 0,
                    selectPort: 0,
                    showBindModal: false,
                })
                this.getInfo();
                this.queryEquipment();
            } else {
                message.error(res.msg, 1)
            }
        }).catch(error => {
            message.error('绑定失败！', 1)
            console.log(error);
        })
    }

    unbindEquipment = (port) => {
        this.setState({ animating: true });
        delBind({
            sensorId: this.props.match.params.id,
            sensor_port: port
        }).then(res => {
            if (res && res.code == 0) {
                message.success('解绑成功！', 1);
                this.getInfo();
                this.queryEquipment();
            } else {
                message.error(res.msg, 1)
            }
        }).catch((error) => {
            message.error('解绑失败，请重试！', 1)
            console.log(error);
        });
    }

    handleWayChange = (way) => {
        this.setState({ way: way }, () => this.getRealTimeData())
    }

    setTimeOxygen = () => {

    }

    onOxygenSetFormChange = (type, value) => {
        if (type) {
            let oxygenSetForm = this.state.oxygenSetForm;
            oxygenSetForm[type] = value;
            this.setState({oxygenSetForm: oxygenSetForm});
        }
    }

    handleOxygenSetCancel = () => {
        this.setState({
            oxygenVisible: false,
            oxygenSetForm: {
                low_limit: '',
                up_limit: '',
                high_limit: ''
            },
            timeSections: [['', '']],
            timeSectionsString: [['', '']],
        })
    }

    handleOxygenSetOk = () => {
        const oxygenSetForm = this.state.oxygenSetForm;
        if (oxygenSetForm.low_limit == "") {
            message.warn('请填写溶氧下限', 1);
            return;
        } else if (oxygenSetForm.up_limit == '') {
            message.warn('请填写溶氧上限', 1);
            return;
        } else if (oxygenSetForm.high_limit == '') {
            message.warn('请填写溶氧高限', 1);
            return;
        }
        let timeSections = this.state.timeSectionsString;
        console.log(timeSections)
        // if(timeSections.length <= 0) {
        //     Toast.fail('必须选择至少一个时间段！', 1);
        //     return;
        // }
        if (timeSections.length == 1) {
            if (timeSections[0][0] == '' && timeSections[0][1] == '') {
                timeSections = [];
            }   
        }
        for (let i = 0; i < timeSections.length; i++) {
            if (!timeSections[i][0] || !timeSections[i][1]) {
                message.warn('有未填写完整的时间段，请填写后提交！', 1);
                return;
            }
            for (let j = i + 1; j < timeSections.length; j++) {
                if (timeSections[i][0] < timeSections[j][0] && timeSections[i][1] > timeSections[j][0] || 
                    timeSections[j][0] < timeSections[i][0] && timeSections[j][1] > timeSections[i][0] 
                ) {
                    message.warn('时间段不能交错，请修改后提交！', 1);
                    return;
                }
           }
        }

        let timers = [];
                oxygenSetForm.device_sn = this.props.match.params.device_sn;
                oxygenSetForm.way = this.state.way;
                timeSections.map((item, index) => {
                    timers.push({
                        startTime: item[0],
                        endTime: item[1],
                        way: this.state.way,
                        device_sn: this.props.match.params.device_sn,                                                                       
                    })
                })

                autoSet({
                    limit_Install:oxygenSetForm,
                    timers: timers
                }).then(res => {
                    if (res.code == 0) {
                        message.success('设置成功', 1);
                        this.setState({
                            oxygenVisible: false,
                            timeSections: [['', '']],
                            timeSectionsString: [['', '']],
                        })
                    } else {
                        message.error(res.msg, 1)
                    }
                    console.log(res);
                }).catch(error => {
                    console.log(error)
                    message.error('设置失败，请重试!', 1)
                })
    }

    handleOxygenOpenOrClose = () => {
        // this.setState({isOpen: !this.state.isOpen})
        aeratorOnOff({
            device_sn: this.props.match.params.device_sn,
            way: this.state.way,
            openOrclose: this.state.isOpen ? 1 : 0
        }).then((res) => {
            if (res.code == '0') {
                this.setState({isOpen: !this.state.isOpen})
                if (onOff) {
                    message.success('开启增氧机成功!', 1)
                } else {
                    message.success('关闭增氧机成功', 1)
                }
            } else {
                message.error(res.msg, 1)
            }
        }).catch((error) => {
            console.log(error);
            message.error('开关发生错误，请重试!', 1);
        });
    }

    timeOnChange = (time, timeString, index, aryIndex) =>  {
        console.log(time, timeString, index, aryIndex);
        let timeSections = this.state.timeSections;
        let timeSectionsString = this.state.timeSectionsString;
        let timeSection = timeSections[index];
        let timeSectionString = timeSectionsString[index];

        if (aryIndex == 0) {
            if(timeSection[1]) {
                if(timeSection[1] < time) {
                    message.error('开始时间不能大于等于结束时间', 1);
                    return;
                }
            }
        } else if (aryIndex == 1) {
            if(timeSection[0]) {
                if(timeSection[0] > time) {
                    message.error('开始时间不能大于等于结束时间', 1);
                    return;
                }
            }
        }
        timeSection[aryIndex] = time;
        timeSectionString[aryIndex] = timeString;
        this.setState({timeSections: timeSections, timeSectionsString: timeSectionsString});
    }
    
    addTimeSection = () => {
        let timeSections = this.state.timeSections
        let timeSectionsString = this.state.timeSectionsString;
        timeSections.push(['', '']);
        timeSectionsString.push(['', '']);
        this.setState({
            timeSections: timeSections,
            timeSectionsString: timeSectionsString,
        })
    }

    deleteTimeSeciton = (index) => {
        let timeSections = this.state.timeSections
        let timeSectionsString = this.state.timeSectionsString;
        timeSections.splice(index,1);
        timeSectionsString.splice(index,1);
        this.setState({
            timeSections: timeSections,
            timeSectionsString: timeSectionsString,
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

        let bindRelationColumns = [
            {
                title: '序号',
                dataIndex: 'index',
                render: (text, record, index) => {
                    return <span>{index + 1}</span>
                }
            },
            {
                title: '端口名称',
                dataIndex: 'port',
                render: (text, record, index) => {
                    return <span>端口{record.port}</span>
                }
            },
            {
                title: '绑定设备',
                dataIndex: 'bindName',
            },
            {
                title: '绑定设备端口',
                dataIndex: 'bindPort',
                render: (text, record, index) => {
                    return (record.bindPort ? <span>端口{record.bindPort}</span> : '')
                }
            },
            {
                title: '增氧方式',
                dataIndex: 'oxygen',
                render: (text, record, index) => {
                    return (record.bindName ? '增氧机' : '')
                }
            },
        ];

        if (this.state.type == 1) {
            bindRelationColumns.push({
                title: '操作',
                dataIndex: 'pH_value',
                render: (text, record, index) => {
                    return <span>
                        {record.bindName ?
                            <Popconfirm title="确认要解绑嘛?" onConfirm={() => this.unbindEquipment(record.port)}>
                                <a href="javascript:void(0);" style={{ marginLeft: '15px' }}>解绑</a>
                            </Popconfirm> :
                            <span onClick={() => { this.setState({ showBindModal: true, portIndex: record.port }) }}>
                                <a href="javascript:void(0);" style={{ marginLeft: '15px' }}>绑定</a>
                            </span>}
                    </span>
                }
            })
        }

        let pondOptions = this.state.ponds.map((item, index) => {
            return <Option key={index} value={item.id}>{item.name}</Option>
        })
        let controllerOptions = this.state.controllers.map((item, index) => {
            return <Option key={item.id} value={item.id}>{item.name}</Option>
        })
        let portOptions = this.state.ports.map((item, index) => {
            return <Option key={item.id} value={item.id}>{item.name}</Option>
        })
        let data = {
            device_sn: this.props.match.params.device_sn,
            deviceName: this.state.deviceName,
            status: this.state.status,
            way: this.state.way
        }
        data = JSON.stringify(data);
       
        const times = this.state.timeSections.map((item, index) => {
            return <Row gutter={18} style={{ marginBottom: '10px' }}>
                    <Col span={4} >
                    {index == 0 ? <Button type="primary" shape="circle" icon="plus" onClick={this.addTimeSection} /> :
                        <Button type='danger' shape="circle" icon='minus' onClick={() => this.deleteTimeSeciton(index)} />
                        }
                    </Col>
                    <Col span={4} style={{ textAlign: 'right', verticalAlign: 'middle' }}>
                    定时开启:
                    </Col>
                    <Col span={7}>
                    <TimePicker format={format} minuteStep={30} value={item[0]} onChange={(time, timeStr) => this.timeOnChange(time, timeStr, index, 0)} />
                    </Col>
                    {/* onChange={time => this.handleTimeChange(time, index, 0 ) */}
                    <Col  span={1} style={{paddingLeft: '5px'}} >
                        —
                        </Col>
                    {/*  onChange={time => this.handleTimeChange(time, index, 1 )} */}
                    <Col  span={7}>
                        <TimePicker format={format} minuteStep={30} value={item[1]} onChange={(time, timeStr) => this.timeOnChange(time, timeStr, index, 1)}  />
                    </Col>
                </Row>
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
                        <Col span={8}>绑定塘口: &nbsp;  <Select
                            showSearch
                            style={{ width: 200 }}
                            disabled={this.state.pondId > 0 ? true : false}
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
                        </Select></Col>
                        <Col span={8}>{this.state.pondId > 0 && <Popconfirm title="确认要解绑嘛?" onConfirm={() => this.disBind()}><Button>解绑</Button></Popconfirm>}</Col>
                        {this.state.way != 0 && <Col span={3}>
                            <Radio.Group value={this.state.way} onChange={e => this.handleWayChange(e.target.value)} >
                                <Radio.Button value={1} >1路</Radio.Button>
                                <Radio.Button value={2}>2路</Radio.Button>
                                {/* <Radio.Button value="small">区间</Radio.Button> */}
                            </Radio.Group>
                        </Col>}

                        {this.state.way != 0 && <Col span={2} offset={2} >
                            <Button onClick={() => {this.setState({oxygenVisible: true})}} >自动增氧设置</Button>
                        </Col>
                        }

                    </Row>}

                </Card>
                {this.state.type != 0 && <Card
                    bodyStyle={{ marginTop: 15 }}
                    title="最新数据"
                    bordered
                >
                    <Col span={18}>
                        <Table
                            loading={this.state.loading}
                            dataSource={this.state.realTimeData}
                            columns={realTimeColumns}
                            bordered
                            pagination={false}
                        />
                    </Col>
                    <Col span={4} offset={2} style={{ paddingTop: 40 }}>
                        <Link to={`/equipment/water-quality/${data}`}><Button size="large">水质曲线</Button></Link>
                    </Col>
                </Card>}
                {this.state.type != 2 && <Card
                    bodyStyle={{ marginTop: 15 }}
                    title="绑定关系"
                    bordered
                >
                    <Table
                        loading={this.state.loading}
                        dataSource={this.state.bindList}
                        columns={bindRelationColumns}
                        bordered
                        pagination={false}
                    />

                </Card>}
                <Modal title='绑定设备'
                    visible={this.state.showBindModal}
                    onOk={this.doBindEquipment}
                    onCancel={this.handleCancel}
                    optionFilterProp="children"
                    okText="确认"
                    cancelText="取消">
                    <Row >绑定设备: &nbsp;
                        <Select
                            showSearch
                            style={{ width: 200 }}
                            placeholder="选择一个设备"
                            optionFilterProp="children"
                            onChange={v => { this.selectController(v) }}
                            value={this.state.selectControllerId}
                        >
                            {controllerOptions}
                            <Option value={0}>无</Option>
                        </Select>
                    </Row>
                    <Row style={{ marginTop: 20 }} >绑定端口: &nbsp;
                        <Select
                            showSearch
                            style={{ width: 200 }}
                            placeholder="选择一个端口"
                            optionFilterProp="children"
                            onChange={e => { this.setState({ selectPort: e }) }}
                            value={this.state.selectPort}
                        >
                            {portOptions}
                            <Option value={0}>无</Option>
                        </Select>
                    </Row>
                </Modal >
                <Modal title='增氧设置'
                    visible={this.state.oxygenVisible}
                    onOk={this.handleOxygenSetOk}
                    onCancel={this.handleOxygenSetCancel}
                    optionFilterProp="children"
                    okText="确认"
                    cancelText="取消">
                    <Row gutter={18} style={{ marginBottom: '10px'}}>
                        <Col span={8} style={{ textAlign: 'right' }}>
                            增氧方式:
                            </Col>
                        <Col span={8}>
                            增氧机{this.state.way}路
                        </Col>
                    </Row>
                    <Row gutter={18} style={{ paddingBottom: '10px', borderBottom: '1px solid #dcdcdc' }}>
                        <Col span={8} style={{ textAlign: 'right' }}>
                            开关控制:
                            </Col>
                        <Col span={8}>
                        <Switch checked={this.state.isOpen}  onChange={this.handleOxygenOpenOrClose} />
                        </Col>
                    </Row>
                    <Row gutter={18} style={{ marginBottom: '10px', marginTop: '20px' }}>
                        <Col span={8} style={{ textAlign: 'right' }}>
                            溶氧下限:
                            </Col>
                        <Col span={8}>
                            <Input value={this.state.oxygenSetForm.low_limit} onChange={(e) => this.onOxygenSetFormChange('low_limit', e.target.value)} />
                        </Col>
                        <Col  span={1}>
                        mg/l
                        </Col>
                    </Row>
                    <Row gutter={18} style={{ marginBottom: '10px' }}>
                        <Col span={8} style={{ textAlign: 'right' }}>
                            溶氧上限:
                            </Col>
                        <Col span={8}>
                            <Input value={this.state.oxygenSetForm.up_limit} onChange={(e) => this.onOxygenSetFormChange('up_limit', e.target.value)} />
                        </Col>
                        <Col  span={1}>
                        mg/l
                        </Col>
                    </Row>
                    <Row gutter={18} style={{ marginBottom: '10px' }}>
                        <Col span={8} style={{ textAlign: 'right' }}>
                            溶氧高限:
                            </Col>
                        <Col span={8}>
                            <Input value={this.state.oxygenSetForm.high_limit} onChange={(e) => this.onOxygenSetFormChange('high_limit', e.target.value)} />
                        </Col>
                        <Col  span={1}>
                        mg/l
                        </Col>
                    </Row>
                    {times}
                    {/* <Row gutter={18} style={{ marginBottom: '10px' }}>
                        <Col span={8} style={{ textAlign: 'right' }}>
                            定时开启:
                            </Col>
                        <Col span={7}>
                        <TimePicker format={format} minuteStep={30} onChange={this.timeOnChange} />
                        </Col>
                        
                        <Col  span={1} style={{paddingLeft: '5px'}} >
                        —
                        </Col>
                        <Col  span={7}>
                        <TimePicker format={format} minuteStep={30} onChange={this.timeOnChange}  />
                        </Col>
                    </Row> */}
                    
                </Modal >
                <Button type="primary" style={{ float: 'right' }} onClick={() => { history.back() }}>
                    返回上一页
                </Button>
            </PageHeaderLayout>
        );
    }
}
