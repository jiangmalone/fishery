
const React = require('react');
const Router = require('react-router-dom');
const Route = Router.Route;
const Switch = Router.Switch;
import IndexPage from './routes/IndexPage';

import Login from './routes/Login/Login';
import UserInfo from './routes/Login/UserInfo'
const App = () => (
    <div style={{height:'100%'}}>
        <Switch>
            <Route exact path="/" component={IndexPage}/>
           
            <Route exact path="/login" component={Login} />
            <Route exact path="/userInfo" component={UserInfo} />
            {/* <Route component={NotFound}/> */}
        </Switch>
    </div>
);

module.exports = App;