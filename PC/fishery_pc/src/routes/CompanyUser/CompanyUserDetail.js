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
    constructor(props) {
        super(props);
        this.state = {
            markers: [],
            isShowMap: true,
            companyData: {},
            ponds: [],
            selectMarker: '',        //选择地图上的某个点
            relation: props.match.params.relation ? props.match.params.relation : window.localStorage.getItem('relationId'),
            companyId: props.match.params.id ? props.match.params.id : window.localStorage.getItem('companyId')
        }
    }

    componentDidMount() {
        this.getCompanyDetail();
        this.queryPond();
    }

    getCompanyDetail = () => {
        CompanyDetail({
            id: this.state.companyId
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
            relation: this.state.relation,
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

    getMarker = () => {
        return this.state.ponds.map((item, index) => {
            // if (!(item.longitude == 0 && item.latitude == 0)) {

            // } 
            let offset = (0, 0)
            if (this.state.selectMarker == item.id) {
                offset = { x: 0, y: -20 }
            }
            const position = {
                longitude: item.longitude,
                latitude: item.latitude
            }
            return (
                <Marker
                    position={position}
                    clickable='true'
                    visible={(item.longitude == 0 && item.latitude == 0) ? false : true}
                    key={index}
                >
                    <div className={styles.mapMakerIcon} onClick={() => {
                        if (this.state.selectMarker == item.id) {
                            this.setState({ selectMarker: 0 })
                        } else {
                            this.setState({ selectMarker: item.id })
                        }
                    }} ></div>
                    {this.state.selectMarker == item.id && <div className={styles.mapMaker} onClick={() => { this.props.history.push(`/userManage/pondManage/detail/${item.id}`) }}>塘口名称: <a >{item.name}</a></div>}
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
                dataIndex: 'pondFishs',
                key: 'pondFishs',
                render: (text, record, index) => {
                    let fish = ''
                    for (let item of text) {
                        fish = fish + item.fish_name + "、";
                    }
                    return <span>{fish ? fish.slice(0, -1) : ''}</span>
                }
            }
        ];

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
                            <Radio.Group value={this.state.isShowMap} onChange={this.handleShowChange}>
                                <Radio.Button value={true}>地图查看</Radio.Button>
                                <Radio.Button value={false}>列表查看</Radio.Button>
                            </Radio.Group>
                        </Col>

                        <Col span={3} offset={10}>
                            <Link to={`/userManage/pondManage/${this.state.relation}`}><Button type="primary">管理塘口</Button></Link>
                        </Col>
                        <Col span={3}>
                            <Link to={`/equipment/${this.state.relation}`}><Button type="primary">管理设备</Button></Link>
                        </Col>
                    </Row>
                    <Row style={{ marginTop: 30 }}>
                        {this.state.isShowMap ? <div style={{ width: '100%', height: 400 }}>
                            <Map plugins={['ToolBar']} >
                                {/* <Markers
                                    markers={this.state.markers}
                                /> */}
                                {markers}
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
