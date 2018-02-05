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
            alarms:[]
        }
    }
    componentDidMount() {
        queryAlarm({
            openId:window.localStorage.getItem('openid')
        }).then((res)=>{
            console.log(res)
            if(res.data.code == 0) {
                this.setState({
                   alarms:res.data.alarmMessageList 
                })
            }
        }).catch((error)=>{
            console.log(error)
        })
    }
    render() {
        let alarmInfos =  this.state.alarms.map((Item,index)=>{
            return   <div className="alarm-list">
            <div className="list-title">{Item.pond.name}-</div>
            <div className="list-info">
                <div className="info-state"><div className="info-circle"></div>溶氧数据异常</div>
                <div className="info-span">溶氧数值过低，鱼儿没法生存啦~</div>
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
