import React from 'react';
import './accordion.less'
import online from '../img/state-online.png';
import {Icon} from 'antd-mobile'
import {withRouter} from "react-router-dom";
import offline from '../img/state-offline.png';
class Accordion extends React.Component {

    constructor(props) {
        super(props)
        let isShowChildren = this.props.isShow ? true : false
        let isShowState = true;
        if(this.props.isShowState != undefined && !this.props.isShowState) {
            isShowState = false;
        }
        this.state = {
            isShowChildren: isShowChildren,
            isShowState: isShowState
        }
    }

    handlaClick = () => {
        this.setState({
            isShowChildren: !this.state.isShowChildren
        })
    }

    render() {
        return(<div className = 'accordion-item'>
            <div className='name-line' onClick={() => {this.handlaClick()}}>
                <div className='name'>{this.props.title}</div>
                {this.state.isShowState && <img className='state-img' src={this.props.isOnline? offline : online} />}
                {this.props.children ? <Icon type={this.state.isShowChildren? 'up' : 'down'} className='icon'></Icon> : 
                <span  className='icon'>无</span>}
            </div>
            {this.state.isShowChildren && <div>
                {this.props.children}
            </div>}
        </div>)
    }
}

export default Accordion;
