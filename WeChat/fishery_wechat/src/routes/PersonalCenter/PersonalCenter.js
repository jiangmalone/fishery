import React from 'react';
import './personalCenter.less';
import { List } from 'antd-mobile';
import BottomTabBar from '../../components/TabBar';
import { connect } from 'dva';
const Item = List.Item;
const Brief = Item.Brief;

class PersonalCenter extends React.Component {

  constructor(props) {
    super(props)
    this.state = {

    }
  }
  render() {
    return (<div className="body-fill">
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
          onClick={() => {
            this.props.history.push('/MyPond');
            this.props.dispatch({
              type: 'global/changeState',
              payload: {
                transitionName: 'left'
              }
            })
          }}
        >我的塘口</Item>
      </List>
      <List className="center-list">
        <Item
          thumb={require("../../img/mine_equipment.png")}
          arrow="horizontal"
          onClick={() => {
            this.props.history.push('/MyPond');
            this.props.dispatch({
              type: 'global/changeState',
              payload: {
                transitionName: 'left'
              }
            })
          }}
        >我的设备</Item>
      </List>
      <List className="center-list">
        <Item
          thumb={require("../../img/mine_about.png")}
          arrow="horizontal"
          onClick={() => {
            this.props.history.push('/MyPond');
            this.props.dispatch({
              type: 'global/changeState',
              payload: {
                transitionName: 'left'
              }
            })
          }}
        >关于渔管在线</Item>
      </List>
      <BottomTabBar nowTab={3} />
    </div>
    );
  }

}


export default connect((state)=>({
  transitionName:global.transitionName
}))(PersonalCenter);
