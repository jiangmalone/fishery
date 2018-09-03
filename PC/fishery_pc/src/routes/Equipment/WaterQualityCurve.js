import React, {PureComponent} from 'react';
import {connect} from 'dva';
import {Link} from 'dva/router';
import {Row, Col, Card, Input, Icon, Button, Table, Radio, DatePicker, message} from 'antd';

const {RangePicker} = DatePicker;
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import {TimelineChart} from '../../components/Charts';
import Trend from '../../components/Trend';
import NumberInfo from '../../components/NumberInfo';
import {getDataToday, getDataAll, getRealTimeData} from '../../services/equipment';
import {Chart, Geom, Axis, Tooltip, Legend, Coord} from 'bizcharts';
import ReactEcharts from 'echarts-for-react';
import moment from 'moment';
import numeral from 'numeral';

const now = new Date();
const todayStr = formartDate(now);
const yesterdayStr = formartDate(new Date(now - 24 * 60 * 60 * 1000));
const beforeYesterdayStr = formartDate(new Date(now - 2 * 24 * 60 * 60 * 1000));
now.setDate(now.getDate() + 1);
const tomorrowStr = formartDate(now);

function formartDate(date) {

  let year = date.getFullYear();
  let month = (((date.getMonth() + 1) < 10) ? "0" + (date.getMonth() + 1) : (date.getMonth() + 1));
  let day = ((date.getDate() < 10) ? "0" + date.getDate() : date.getDate());

  return year + '/' + month + '/' + day + ' 00:00:00';
}

export default class WaterQualityCurve extends React.Component {

  constructor(props) {
    super(props);
    const data = JSON.parse(this.props.match.params.data);
    this.state = {
      oxygens: [],
      phs: [],
      temperatures: [],
      device_sn: data.device_sn,
      name: data.deviceName,
      status: data.status,
      selectTime: 'today',
      way: data.way,
      up_limit: 10,
      low_limit: 5,
      high_limit: 15,
      Limit: null
    }
  }

  componentDidMount() {
    this.getDataToday();
    this.getRealTimeData();
  }

  getRealTimeData = () => {
    getRealTimeData({
      device_sn: this.state.device_sn
    }).then((res) => {
      if (res) {
        const data = res.data;
        const high = res.high_limit;
        const up = res.up_limit;
        const low = res.low_limit;
        if (data) {
          this.setState({
            up_limit: up,
            low_limit: low,
            high_limit: high
          });
        }
      } else {
        message.error(res.msg, 1);
      }
    }).catch((error) => {
      message.error('请求失败', 1);
      console.log(error)
    });
  }

  getDataToday = () => {
    getDataToday({
      device_sn: this.state.device_sn,
      way: 1
    }).then((res) => {
      if (res) {
        this.setState({
          oxygens: res.DO,
          phs: res.pH,
          temperatures: res.WT,
          Limit: res.Limit
        })
      } else {
        message.error(res.msg, 1);
      }
    }).catch((error) => {
      message.error('请求失败', 1);
      console.log(error)
    });
  }

  getDataSevent = () => {
    getDataAll({
      device_sn: this.state.device_sn,
      way: 1
    }).then((res) => {
      if (res) {
        this.setState({
          oxygens: res.DO,
          phs: res.pH,
          temperatures: res.WT,
          Limit: res.Limit
        })
      } else {
        message.error(res.msg, 1);
      }
    }).catch((error) => {
      message.error('请求失败', 1);
      console.log(error)
    });
  }

  handleTimeChange = (value) => {
    this.setState({selectTime: value});
    if (value == 'today') {
      this.getDataToday();
    } else if (value == 'sevent') {
      this.getDataSevent()
    }
  }

  getOption = (type, isSelectToday) => {
    let data;
    let anchor;
    let title = ''
    if (isSelectToday) {
      anchor = [
        {name: todayStr, value: [todayStr, 0]},
        {name: tomorrowStr, value: [tomorrowStr, 0]}
      ];
    } else {
      anchor = [
        {name: beforeYesterdayStr, value: [beforeYesterdayStr, 0]},
        {name: yesterdayStr, value: [yesterdayStr, 0]},
        {name: todayStr, value: [todayStr, 0]},
        {name: tomorrowStr, value: [tomorrowStr, 0]}
      ]
    }
    if (type == 'phs') {
      data = this.state.phs;
      title = "PH变化曲线";
    } else if (type == 'oxygens') {
      data = this.state.oxygens;
      title = "溶氧变化曲线";
    } else if (type == 'temperatures') {
      data = this.state.temperatures;
      title = "水温变化曲线";
    }
    let option = {
      tooltip: {
        trigger: 'axis',
        formatter: function (params) {
          return '时间：'+ params[0].name + '<br />数值：'+(params[0].value?params[0].value:'暂无数值');
        },
        axisPointer: {
          animation: false
        }
      },
      animation: false,
      xAxis: [{
        type: 'category',
        boundaryGap: false,
        data: data.at,
        splitLine: {
          show: false
        }
      }],
      yAxis: [{
        type: 'value',
        splitLine: {
          lineStyle: {
            color: '#e9e7de',
            type: 'dashed'
          }
        },
      }],
      series: [{
        type: 'line',
        smooth: true,
        data: data.value
      }]
    }
    if (type == 'oxygens') {
        option.series.markLine = {
        symbol: '',
          itemStyle: {
          lineStyle: {type: 'solid'},
          label: {show: true, position: 'left', formatter: 'aaa'}
        },
        data: [
          { yAxis: this.state.Limit?this.state.Limit.low_limit:4,
            label: {
              show: true,
              formatter: '下限',
              color: '#beac00'
            },
            itemStyle: {
              color: '#beac00'
            }
          },
          { yAxis: this.state.Limit?this.state.Limit.up_limit:6,
            label: {
              show: true,
              formatter: '上限',
              color: '#45795b'
            },
            itemStyle: {
              color: '#45795b'
            }
          },
          { yAxis: this.state.Limit?this.state.Limit.high_limit:20,
            label: {
              show: true,
              formatter: '高限',
              color: '#b3675b'
            },
            itemStyle: {
              color: '#b3675b'
            }
          }
        ]
      }
      console.log(option)
    }
    return option;
  }

  render() {
    return (
      <PageHeaderLayout>
        <Card bordered={false}>
          <Row style={{fontSize: 17}}>
            <Col span={10}>设备名称: &nbsp;&nbsp; {this.state.name}（编号:{this.state.device_sn}）</Col>
            <Col span={5}>设备状态: &nbsp; {this.state.status}</Col>
          </Row>
          <Row style={{marginTop: 20}}>
            <Col span={7}>
              <Radio.Group value={this.state.selectTime} onChange={e => this.handleTimeChange(e.target.value)}>
                <Radio.Button value="today">今日</Radio.Button>
                <Radio.Button value="sevent">三日</Radio.Button>
              </Radio.Group>
            </Col>
          </Row>
          <Row style={{fontSize: 25, paddingTop: 25}}>
            PH变化曲线
          </Row>
          <Row style={{padding: 30, paddingTop: 0}}>
            <Col span={20}>
              {this.state.phs ?
                <ReactEcharts option={this.getOption('phs', this.state.selectTime === 'today')} height={500}/>
                :
                <span>暂无数据</span>}
            </Col>
          </Row>
          <Row style={{fontSize: 25, paddingTop: 25}}>
            溶氧变化曲线
          </Row>
          <Row style={{padding: 30, paddingTop: 0}}>
            <Col span={20}>
              {this.state.oxygens ?
                <ReactEcharts option={this.getOption('oxygens', this.state.selectTime === 'today')} height={500}/>
                : <span>暂无数据</span>}
            </Col>
          </Row>
          <Row style={{fontSize: 25, paddingTop: 25}}>
            水温变化曲线
          </Row>
          <Row style={{padding: 30, paddingTop: 0}}>
            <Col span={20}>
              {this.state.temperatures ?
                <ReactEcharts option={this.getOption('temperatures', this.state.selectTime === 'today')} height={500}/>
                : <span>暂无数据</span>}
            </Col>
          </Row>
        </Card>
        <Button type="primary" style={{float: 'right'}} onClick={() => {
          history.back()
        }}>
          返回上一页
        </Button>
      </PageHeaderLayout>
    );
  }
}
