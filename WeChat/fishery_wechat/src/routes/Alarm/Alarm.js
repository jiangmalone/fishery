import React from 'react';
import './alarm.less';
import { List } from 'antd-mobile';
import BottomTabBar from '../../components/TabBar';
const Item = List.Item;
const Brief = Item.Brief;

function Alarm(props) {
  return (
    <div className="alarm-box"> 
        <div className="alarm-list">
            <div className="list-title">小鱼塘1-传感器1</div>
            <div className="list-info">
                <div className="info-state"><div className="info-circle"></div>溶氧数据异常</div>
                <div className="info-span">溶氧数值过低，鱼儿没法生存啦~</div>                
            </div>
        </div>
        <div className="alarm-list">
            <div className="list-title">小鱼塘1-传感器1</div>
            <div className="list-info">
                <div className="info-state"><div className="info-circle"></div>溶氧数据异常</div>
                <div className="info-span">溶氧数值过低，鱼儿没法生存啦~</div>                
            </div>
        </div>
        <div className="alarm-list">
            <div className="list-title">小鱼塘1-传感器1</div>
            <div className="list-info">
                <div className="info-state"><div className="info-circle"></div>溶氧数据异常</div>
                <div className="info-span">溶氧数值过低，鱼儿没法生存啦~</div>                
            </div>
        </div>
        <div className="alarm-list">
            <div className="list-title">小鱼塘1-传感器1</div>
            <div className="list-info">
                <div className="info-state"><div className="info-circle"></div>溶氧数据异常</div>
                <div className="info-span">溶氧数值过低，鱼儿没法生存啦~</div>                
            </div>
        </div>
        <div className="alarm-list">
            <div className="list-title">小鱼塘1-传感器1</div>
            <div className="list-info">
                <div className="info-state"><div className="info-circle"></div>溶氧数据异常</div>
                <div className="info-span">溶氧数值过低，鱼儿没法生存啦~</div>                
            </div>
        </div>
        <BottomTabBar  nowTab={2}/>
    </div>
    
  );
}


export default Alarm;
