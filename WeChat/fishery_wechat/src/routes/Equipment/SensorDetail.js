import React from 'react';
import './sensorDetail.less'
import { Popover, Icon, NavBar, List } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import offline from '../../img/equ_offline.png'
import online from '../../img/equ_online.png'
const Item = Popover.Item;
// import btn_list from '../../img/btn_list.png';
import correct from '../../img/btn_correct.png';
import back_img from '../../img/back.png';
import { Chart, Geom, Axis, Tooltip, Legend, Coord } from 'bizcharts';

const dayPHData = [
    { time: "9:00", ph: 3 },
    { time: "10:00", ph: 4 },
    { time: "11:00", ph: 3.5 },
    { time: "12:00", ph: 5 },
    { time: "13:00", ph: 4.9 },
    { time: "14:00", ph: 6 },
    { time: "15:00", ph: 7 },
    { time: "16:00", ph: 9 },
    { time: "17:00", ph: 13 }
];
const monthPHData = [
    { time: "星期一", ph: 9 },
    { time: "星期二", ph: 9.9 },
    { time: "星期三", ph: 9.5 },
    { time: "星期四", ph: 7 },
    { time: "星期五", ph: 8 },
    { time: "星期六", ph: 9 },
    { time: "星期日", ph: 7 },
];
const cols = {
    'ph': { min: 0 },
    'time': { range: [0, 1] }
};

const dayOData = [
    { time: "9:00", o: 7 },
    { time: "10:00", o: 3 },
    { time: "11:00", o: 3 },
    { time: "12:00", o: 9 },
    { time: "13:00", o: 7 },
    { time: "14:00", o: 0 },
    { time: "15:00", o: 4 },
    { time: "16:00", o: 9 },
    { time: "17:00", o: 7 }
];
const monthOData = [
    { time: "星期一", o: 9 },
    { time: "星期二", o: 9.9 },
    { time: "星期三", o: 9.5 },
    { time: "星期四", o: 7 },
    { time: "星期五", o: 8 },
    { time: "星期六", o: 9 },
    { time: "星期日", o: 7 },
];
const oCols = {
    'o': { min: 0 },
    'time': { range: [0, 1] }
};

const dayWaterData = [
    { time: "9:00", '温度': 3 },
    { time: "10:00", '温度': 4 },
    { time: "11:00", '温度': 3.5 },
    { time: "12:00", '温度': 5 },
    { time: "13:00", '温度': 4.9 },
    { time: "14:00", '温度': 6 },
    { time: "15:00", '温度': 7 },
    { time: "16:00", '温度': 9 },
    { time: "17:00", '温度': 13 }
];
const monthWaterData = [
    { time: "星期一",  '温度': 9 },
    { time: "星期二",  '温度': 9.9 },
    { time: "星期三",  '温度': 9.5 },
    { time: "星期四",  '温度': 7 },
    { time: "星期五",  '温度': 8 },
    { time: "星期六",  '温度': 9 },
    { time: "星期日",  '温度': 7 },
];
const waterCols = {
    '温度': { min: 0 },
    'time': { range: [0, 1] }
};



class SensorDetail extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            title: '小渔塘-传感器1',
            visible: false,
            isShowMore: false,
            selected: '',
            isShowDetail: false,
            isSelectToday: true,
            phData: dayPHData,
            oData: dayOData,
            waterData: dayWaterData
        }
    }

    onSelect = (opt) => {
        console.log(opt.props.value);

        this.setState({
            isShowMore: false,
            selected: opt.props.value,
            title: '小渔塘-' + opt.props.value
        });
    };
    handleVisibleChange = (isShowMore) => {
        this.setState({
            isShowMore,
        });
    };

    changeDetailShowState = () => {
        this.setState({
            isShowDetail: !this.state.isShowDetail
        })
    }

    selectTime = (state) => {
        if (state == this.state.isSelectToday) {
            return;
        } else {
            let phData = [];
            let oData = [];
            let waterData = [];
            if(!this.state.isSelectToday) {
                phData = dayPHData;
                oData = dayOData;
                waterData = dayWaterData;
            } else {
                phData = monthPHData;
                oData = monthOData;
                waterData = monthWaterData;
            }
            this.setState({
                isSelectToday: !this.state.isSelectToday,
                phData: phData,
                oData: oData,
                waterData: waterData
            })
        }
    }

    render() {
        const overlayAry = [];
        for (let i = 0; i < 3; i++) {
            let item = <Item key={i + 1} value={'传感器' + (i + 1)} >
                传感器{i + 1}
            </Item>
            overlayAry.push(item);
        }
        return (<div className='sensorDetail-bg' >

            <div className="nav-bar-title">
                <i className="back" onClick={() => {
                    history.back();
                    this.props.dispatch({
                        type: 'global/changeState',
                        payload: {
                            transitionName: 'right'
                        }
                    })
                }}></i>
                {this.state.title}
                <i className="right-item-none list" onClick={() => {
                    this.setState({ isShowMore: !this.state.isShowMore })
                }} ></i>
            </div>
            <Popover
                mask
                overlayClassName="fortest"
                overlayStyle={{ color: 'currentColor' }}
                visible={this.state.isShowMore}
                overlay={ overlayAry }
                align={{
                    overflow: { adjustY: 0, adjustX: 0 },
                    offset: [-26, 50],
                }}
                onVisibleChange={this.handleVisibleChange}
                onSelect={this.onSelect}
            >
                <div style={{
                    height: '100%',
                    padding: '0 15px',
                    marginRight: '-15px',
                    display: 'flex',
                    alignItems: 'center',
                }}
                >
                </div>
            </Popover>
            <div className='state-head' onClick={this.changeDetailShowState} >

                <img src={offline} style={{ marginLeft: 0 }} />
                <span>当前状态</span>
                <Icon type={this.state.isShowDetail ? 'up' : 'down'} className='icon' ></Icon>
                <img src={correct} className='correct' />
            </div>
            {this.state.isShowDetail && <div className='detail'>
                <div>实时溶氧：&nbsp;&nbsp; 10.25</div>
                <div>实时水温：&nbsp;&nbsp; 25℃</div>
                <div>实时PH值：&nbsp;&nbsp; 7</div>
            </div>}

            <div className='button-line' >
                <div className={(this.state.isSelectToday ? 'selected' : '') + ' left'} onClick={() => this.selectTime(true)} >
                    今 日
                </div>
                <div className={(!this.state.isSelectToday ? 'selected' : '') + ' right'} onClick={() => this.selectTime(false)}  >
                    七 日
                </div>
            </div>
            <div className='chart-div'>
                <p>PH变化曲线</p>
                <Chart height={400} data={this.state.phData} scale={cols} forceFit>
                    <Axis name="time" />
                    <Axis name="ph" />
                    <Tooltip crosshairs={{ type: "y" }} />
                    <Geom type="line" position="time*ph" size={2} />
                    <Geom type='point' position="time*ph" size={4} shape={'circle'} style={{ stroke: '#fff', lineWidth: 1 }} />
                </Chart>
            </div>
            <div className='chart-div'>
                <p>溶氧变化曲线</p>
                <Chart height={400} data={this.state.oData} scale={oCols} forceFit>
                    <Axis name="time" />
                    <Axis name="o" />
                    <Tooltip crosshairs={{ type: "y" }} />
                    <Geom type="line" position="time*o" size={2} />
                    <Geom type='point' position="time*o" size={4} shape={'circle'} style={{ stroke: '#fff', lineWidth: 1 }} />
                </Chart>
            </div>
            <div className='chart-div'>
                <p>水温变化曲线</p>
                <Chart height={400} data={this.state.waterData} scale={waterCols} forceFit>
                    <Axis name="time" />
                    <Axis name="温度" />
                    <Tooltip crosshairs={{ type: "y" }} />
                    <Geom type="line" position="time*温度" size={2} />
                    <Geom type='point' position="time*温度" size={4} shape={'circle'} style={{ stroke: '#fff', lineWidth: 1 }} />
                </Chart>
            </div>
        </div>);
    }
}

export default withRouter(SensorDetail);
