import React from 'react';
import './personalCenter.less';
import { List } from 'antd-mobile';
import BottomTabBar from '../../components/TabBar';
const Item = List.Item;
const Brief = Item.Brief;

function PersonalCenter() {
  return (
    <div>
      <div className="personInfo-box">
        <div className="backgroundImageFilter">
        </div>
        <div className="out-phone-btn clear">
          <div className="out-btn" />
          <div className="phone-btn" />
        </div>
        <div className="avatar">
          <img src={require('../../img/avatar.jpg')} />
        </div>
        <div className="name">
          张蕾西
          {/* 登录/注册 */}
          </div>
      </div>

      <List className="center-list">
        <Item
          thumb={require("../../img/mine_pond.png")}
          arrow="horizontal"
          onClick={() => { }}
        >我的塘口</Item>
      </List>
      <List className="center-list">
        <Item
          thumb={require("../../img/mine_equipment.png")}
          arrow="horizontal"
          onClick={() => { }}
        >我的设备</Item>
      </List>
      <List className="center-list">
        <Item
          thumb={require("../../img/mine_about.png")}
          arrow="horizontal"
          onClick={() => { }}
        >关于渔管在线</Item>
      </List>

      <BottomTabBar  nowTab={3}/>
    </div>
  );
}


export default PersonalCenter;
