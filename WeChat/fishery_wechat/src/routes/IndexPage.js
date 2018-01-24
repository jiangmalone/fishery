import React from 'react';
import { connect } from 'dva';
import './IndexPage.less';
import Example from '../components/Example.js'
import CSSTransitionGroup from "react-addons-css-transition-group";
import style from "./IndexPage.less";
export default class Container extends React.Component {
  constructor(props) {
    super(props);
  }

  componentWillMount() {
    document.body.style.margin = "0px";
    // 这是防止页面被拖拽
    document.body.addEventListener('touchmove', (ev) => {
      ev.preventDefault();
    });
  }

  render() {
    return (
      <CSSTransitionGroup
        transitionName="transitionWrapper"
        component="div"
        className={style.transitionWrapper}
        transitionEnterTimeout={300}
        transitionLeaveTimeout={300}>
        <div key={this.props.location.pathname} style={{ position: "absolute", width: "100%" }}>
          {
            this.props.children
          }
        </div>
      </CSSTransitionGroup>
    );
  }

}
