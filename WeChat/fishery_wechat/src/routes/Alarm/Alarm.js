import React from 'react';
import './alarm.less';
import { List } from 'antd-mobile';
import BottomTabBar from '../../components/TabBar';
import { queryAlarm } from '../../services/equipment';
const Item = List.Item;
const Brief = Item.Brief;

class Alarm extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            alarms: []
        }
    }
    componentDidMount() {
        queryAlarm({
            openId: window.localStorage.getItem('openid')
        }).then((res) => {
            console.log(res)
            if (res.data.code == 0) {
                this.setState({
                    alarms: res.data.dataAlarm
                })
            }
        }).catch((error) => {
            console.log(error)
        })
    }
    render() {
        let alarmInfos = this.state.alarms.map((Item, index) => {
            let alarmTitle = ''
            switch (Item.alarmType) {
                case 0: alarmTitle = '溶氧值'; break;
                case 1: alarmTitle = '温度'; break;
                case 2: alarmTitle = 'PH'; break;
            }
            return <div className="alarm-list">
                <div className="list-title">{Item.pondName}-{Item.deviceName}</div>
                <div className="list-info">
                    <div className="info-state"><div className="info-circle"></div>{alarmTitle}数据异常</div>
                    <div className="info-span">{Item.message}</div>
                </div>
            </div>
        })
        return (
            <div className="alarm-box">
                {alarmInfos}
                <BottomTabBar nowTab={2} />
            </div>)
    }
}


export default Alarm;
