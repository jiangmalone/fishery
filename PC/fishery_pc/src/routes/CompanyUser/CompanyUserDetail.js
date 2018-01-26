import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Row, Col, Card, Input, Icon, Button, Radio, Table, Popconfirm } from 'antd';
const ButtonGroup = Button.Group;
import { Map, Markers, InfoWindow, Marker } from 'react-amap';
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { CompanyDetail } from '../../services/user';
import { queryPond, pondDetail } from '../../services/pond';
import styles from "./companyUserDetail.less"
import Select from 'antd/lib/select';

const randomPosition = () => ({
    longitude: 100 + Math.random() * 20,
    latitude: 30 + Math.random() * 20
})
const randomMarker = (len) => (
    Array(len).fill(true).map((e, idx) => ({
        position: randomPosition()
    }))
);

export default class CompanyUserDetail extends React.Component {
    constructor() {
        super();
        this.state = {
            markers: [],
            isShowMap: true,
            companyData: {},
            ponds: [],
            selectMarker: ''//选择地图上的某个点
        }

        this.markerEvents = {
            created: (instance) => {
              console.log('Marker 实例创建成功；如果你需要对原生实例进行操作，可以从这里开始；');
              console.log(instance);
            },
            click: (e) => {
              console.log(e);
              this.setState({selectMarker: 38})
            },
            // ... 支持绑定所有原生的高德 Marker 事件
          }
    }

    componentDidMount() {
        this.getCompanyDetail();
        this.queryPond();
    }

    getCompanyDetail = () => {
        CompanyDetail({
            id: this.props.match.params.id
        }).then(res => {
            if (res && res.code == 0) {
                let data = res.data;
                data.pondCount = res.pondCount;
                data.equip = res.equip;
                this.setState({ companyData: res.data });
            }
        }).catch(error => {
            console.log(error);
        })
    }

    queryPond = () => {
        queryPond({
            relation: this.props.match.params.relation,
            name: '',
            page: 1,
            number: 100
        }).then(res => {
            if (res && res.code == 0) {
                let markers = [];
                res.data.map((pond, index) => {
                    markers.push({
                        position: {
                            longitude: pond.longitude,
                            latitude: pond.latitude
                        },
                    })
                    res.data[index].key = pond.id;
                })
                this.setState({
                    ponds: res.data,
                    markers: markers,
                })
            }
        }).catch(error => {
            console.log(error);
        })
    }

    randomMarkers() {
        this.setState({
            markers: randomMarker(2)
        })
    }

    handleShowChange = (e) => {
        this.setState({
            isShowMap: e.target.value
        })
    }

    getInfoWindows = () => {
        return this.state.ponds.map((item, index) => {
            const position = {
                longitude: item.longitude,
                latitude: item.latitude
            }
            return (<InfoWindow
                position={position}
                visible={this.state.selectMarker == item.id ? true : false}
                isCustom={false}
                key={index}
                offset={[0, -30]}
            >
                <div>hahahah</div>
                {/* <Link to={`/userManage/pondManage/detail/${item.id}`}>{item.name}</Link> */}
            </InfoWindow>)
        })
    }

    getMarker = () => {
        return this.state.ponds.map((item, index) => {
    
            const position = {
                longitude: item.longitude,
                latitude: item.latitude
            }
            return (<Marker
                position={position}
                clickable
                events={this.markerEvents}
                onClick={() => this.setState({selectMarker: item.id})}
            >
            </Marker>)
        })
    }

    render() {
        const columns = [
            {
                title: '序号',
                dataIndex: 'index',
                render: (text, record, index) => {
                    return <span>{index + 1}</span>
                }
            },
            {
                title: '塘口名称',
                dataIndex: 'name',
                render: (text, record, index) => {
                    return <Link to={`/userManage/pondManage/detail/${record.id}`}>{text}</Link>
                },
            },
            {
                title: '塘口位置',
                dataIndex: 'address',
            },
            {
                title: '面积（亩）',
                dataIndex: 'area',
            },
            {
                title: '深度（m）',
                dataIndex: 'depth',
            },
            {
                title: '品种',
                dataIndex: 'fish_categorys',
                render: (text, record, index) => {
                    return <span>{record.fish_categorys.join(',')}</span>
                },
            }
        ];

        // const infoWindows = this.getInfoWindows();
        const markers = this.getMarker();
        return (
            <PageHeaderLayout >
                <Card bordered={false}>
                    <Row>
                        <Col span={8}>企业名称: &nbsp;&nbsp; {this.state.companyData.name}</Col>
                        <Col span={3} offset={10}>塘口数: &nbsp; {this.state.companyData.pondCount}</Col>
                        <Col span={3} >设备在线数: &nbsp; {this.state.companyData.equip}</Col>
                    </Row>

                </Card>
                <Card>
                    <Row>
                        <Col span={8}>
                            <Radio.Group onChange={this.handleShowChange}>
                                <Radio.Button value={true}>地图查看</Radio.Button>
                                <Radio.Button value={false}>列表查看</Radio.Button>
                            </Radio.Group>
                        </Col>

                        <Col span={3} offset={10}>
                            <Link to={`/userManage/pondManage/${this.props.match.params.relation}`}><Button type="primary">管理塘口</Button></Link>
                        </Col>
                        <Col span={3}>
                            <Link to={`/equipment/${this.props.match.params.relation}`}><Button type="primary">管理设备</Button></Link>
                        </Col>
                    </Row>
                    <Row style={{ marginTop: 30 }}>
                        {this.state.isShowMap ? <div style={{ width: '100%', height: 400 }}>
                            <Map plugins={['ToolBar']} >
                                {/* <Markers
                                    markers={this.state.markers}
                                /> */}
                                {markers}
                                {/* {infoWindows} */}
                            </Map>
                        </div> : <Table
                                dataSource={this.state.ponds}
                                columns={columns}
                                bordered
                            />}
                    </Row>
                </Card>
            </PageHeaderLayout>
        );
    }
}
