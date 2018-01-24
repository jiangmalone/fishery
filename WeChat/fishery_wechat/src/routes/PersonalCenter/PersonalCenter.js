import React from 'react';
import './personalCenter.less';
import { List, Toast, Modal } from 'antd-mobile';
import BottomTabBar from '../../components/TabBar';
import { connect } from 'dva';
import { LogOut } from '../../services/sms'
const Item = List.Item;
const Brief = Item.Brief;
const alert = Modal.alert;
class PersonalCenter extends React.Component {

  constructor(props) {
    super(props)
    this.state = {

    }
  }
  logOut = () => {
    alert('退出登录', '你确定吗?', [
      { text: '取消', onPress: () => console.log('cancel') },
      {
        text: '确定',
        onPress: () => {

          LogOut({ phone: window.localStorage.getItem('phone') }).then((res) => {
            if (res.data.code == '0') {
              this.props.history.push('/login')
              this.props.dispatch({
                type: 'global/changeState',
                payload: { login: false }
              }); 
            }
          })
        }
      },
    ])
  }
  render() {
    return (<div className="body-fill">
      <div className="personInfo-box">
        <div className="backgroundImageFilter">
        </div>
        <div className="out-phone-btn clear">
          <div className="out-btn" onClick={() => { this.logOut() }} />
          <a href="tel:15105182270" className="phone-btn" />
        </div>
        <div className="avatar">
          <img src={require('../../img/avatar.jpg')} />
        </div>
        <div className="name" onClick={()=>{this.props.history.push('/userInfo')}}>
          {window.localStorage.getItem('name')}
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
            this.props.history.push('/MyEquipment');
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
           window.location.href = 'http://v1.rabbitpre.com/m/NuqyzFRIK?mobile=1&openid=o7kepwR53UD0UUW-ycj-CXyhgeGY&access_token=6_9P-2-tOCdBXe9KyaTR9oaspqzBDLg7J9Wm0L6icFtPNXVdxChXrRwBXy6d6Lxh11ybjrg7lNAYO9weEm0hv2ug'
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


export default connect((state) => ({
  transitionName: global.transitionName,
  login: global.login
}))(PersonalCenter);
