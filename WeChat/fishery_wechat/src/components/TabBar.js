import React from 'react';
import { TabBar } from 'antd-mobile';
import {withRouter} from "react-router-dom";
import './tabBar.less'
import { connect } from 'dva';

class BottomTabBar extends React.Component{
    render(){
        return <TabBar
        unselectedTintColor="#949494"
        tintColor="#5dc3ed"
        barTintColor="white"
    >
        <TabBar.Item
            title="首页"
            key="首页"
          
            icon={<div className="Index-TabBar-1"  />
            }
            selectedIcon={<div className="Index-TabBar-1 Index-TabBar-active" />
            }
            selected= {this.props.nowTab==1?true:false}
            onPress = {()=>{
             this.props.history.push('/main');
             this.props.dispatch({
                type: 'global/changeState',
                payload: {
                    transitionName: 'fade'
                }
            })
            }}
        >
        </TabBar.Item>
        <TabBar.Item
            icon={<div className="Index-TabBar-2"  />}
            selectedIcon={<div className="Index-TabBar-2 Index-TabBar-active" />}
            title="告警"
            key="告警"
            selected= {this.props.nowTab==2?true:false}
            onPress = {()=>{
                this.props.history.push('/alarm');
                this.props.dispatch({
                    type: 'global/changeState',
                    payload: {
                        transitionName: 'fade'
                    }
                })
            }}
        >
        </TabBar.Item>
        <TabBar.Item
            icon={<div className="Index-TabBar-3"  />}
            selectedIcon={<div className="Index-TabBar-3 Index-TabBar-active" />}
            title="我的"
            key="我的"
            selected= {this.props.nowTab==3?true:false}
            onPress = {()=>{
                this.props.history.push('/center');
                this.props.dispatch({
                    type: 'global/changeState',
                    payload: {
                        transitionName: 'fade'
                    }
                })
            }}
        >
        </TabBar.Item>
    </TabBar>    
    }
}

export default connect()(withRouter(BottomTabBar));
