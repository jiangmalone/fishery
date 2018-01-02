import React from 'react';
import './sensorDetail.less'
import { Popover, Icon, NavBar } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import offline from '../../img/equ_offline.png'
import online from '../../img/equ_online.png'
const Item = Popover.Item;
import btn_list from '../../img/btn_list.png';
import correct from '../../img/btn_correct.png';
import back_img from '../../img/back.png';
import { Chart, Geom, Axis, Tooltip, Legend, Coord } from 'bizcharts';


const data = [
    { year: "1991", value: 3 },
    { year: "1992", value: 4 },
    { year: "1993", value: 3.5 },
    { year: "1994", value: 5 },
    { year: "1995", value: 4.9 },
    { year: "1996", value: 6 },
    { year: "1997", value: 7 },
    { year: "1998", value: 9 },
    { year: "1999", value: 13 }
];
const cols = {
    'value': { min: 0 },
    'year': { range: [0, 1] }
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
            this.setState({
                isSelectToday: !this.state.isSelectToday
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
            <NavBar
                mode="light"
                icon={<img src={back_img} style={{width: '.2rem', height: '.35rem'}} /> }
                rightContent={
                    <Popover mask
                        overlayClassName="fortest"
                        overlayStyle={{ color: 'currentColor' }}
                        visible={this.state.isShowMore}
                    
                        overlay={
                            overlayAry
                        }
                        align={{
                            overflow: { adjustY: 0, adjustX: 0 },
                            offset: [-10, 0],
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
                            <img src={btn_list} style={{ height: '.4rem' }} />
                        </div>
                    </Popover>
                }
            >
                {this.state.title}
            </NavBar>
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
                <i className={"edit"} ></i>
            </div>
            <Popover mask
                        overlayClassName="fortest"
                        overlayStyle={{ color: 'currentColor' }}
                        visible={this.state.isShowMore}
                    
                        overlay={
                            overlayAry
                        }
                        align={{
                            overflow: { adjustY: 0, adjustX: 0 },
                            offset: [-10, 0],
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
                            <img src={btn_list} style={{ height: '.4rem' }} />
                        </div>
                    </Popover>
                }
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
                <Chart height={400} data={data} scale={cols} forceFit>
                    <Axis name="year" />
                    <Axis name="value" />
                    <Tooltip crosshairs={{ type: "y" }} />
                    <Geom type="line" position="year*value" size={2} />
                    <Geom type='point' position="year*value" size={4} shape={'circle'} style={{ stroke: '#fff', lineWidth: 1 }} />
                </Chart>
            </div>
            <div className='chart-div'>
                <p>PH变化曲线</p>
                <Chart height={400} data={data} scale={cols} forceFit>
                    <Axis name="year" />
                    <Axis name="value" />
                    <Tooltip crosshairs={{ type: "y" }} />
                    <Geom type="line" position="year*value" size={2} />
                    <Geom type='point' position="year*value" size={4} shape={'circle'} style={{ stroke: '#fff', lineWidth: 1 }} />
                </Chart>
            </div>
            <div className='chart-div'>
                <p>PH变化曲线</p>
                <Chart height={400} data={data} scale={cols} forceFit>
                    <Axis name="year" />
                    <Axis name="value" />
                    <Tooltip crosshairs={{ type: "y" }} />
                    <Geom type="line" position="year*value" size={2} />
                    <Geom type='point' position="year*value" size={4} shape={'circle'} style={{ stroke: '#fff', lineWidth: 1 }} />
                </Chart>
            </div>
        </div>);
    }
}

export default withRouter(SensorDetail);
