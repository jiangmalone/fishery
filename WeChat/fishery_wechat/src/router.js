import React from 'react';
import { Router, Route } from 'dva/router';
import App from './App.js'
import './routes/IndexPage.less'
import Login from './routes/Login/Login';

function RouterConfig({ history }) {

  return (
    <Router history={history}>
      <div>
        <Route path="/login" component={Login} />
        <Route path="/" component={App} />
      </div>
    </Router>
  );
}

export default RouterConfig;
