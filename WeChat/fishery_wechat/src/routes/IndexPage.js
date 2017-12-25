import React from 'react';
import { connect } from 'dva';
import './IndexPage.less';
import Example from '../components/Example.js'
function IndexPage() {
   console.log(document.documentElement.clientWidth)
  return (
    <div className="normal">
      <div className="welcome"/>
      <Example> </Example>
    </div>
  );
}

IndexPage.propTypes = {
};

export default connect()(IndexPage);
