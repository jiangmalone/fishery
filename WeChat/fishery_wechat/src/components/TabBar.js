import React from 'react';
import { TabBar } from 'antd-mobile';
import {withRouter} from "react-router-dom";
import './tabBar.less'
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
             this.props.history.push('/indexPage')
            }}
        >
        </TabBar.Item>
        <TabBar.Item
            icon={<div className="Index-TabBar-2"  />}
            selectedIcon={<div className="Index-TabBar-2 Index-TabBar-active" />}
            title="订单"
            key="订单"
            selected= {this.props.nowTab==2?true:false}
            onPress = {()=>{
                dplus.track('订单底栏');
                this.props.history.push('/serviceCard')
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
                this.props.history.push('/center')
            }}
        >
        </TabBar.Item>
    </TabBar>    
    }
}

export default withRouter(BottomTabBar);
