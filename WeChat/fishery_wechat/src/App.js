
const React = require('react');
const Router = require('react-router-dom');
const Route = Router.Route;
const Switch = Router.Switch;

import PersonalCenter from './routes/PersonalCenter/PersonalCenter';
import IndexPage from './routes/IndexPage';
import Login from './routes/Login/Login';
import UserInfo from './routes/Login/UserInfo'
import Alarm from './routes/Alarm/Alarm';
import Main from './routes/Main/Main'
import AutoOrxygenationSetting from './routes/AutoOxygenationSetting/AutoOxygenationSetting'

const App = () => (
    <div style={{height:'100%'}}>
        <Switch>
            <Route exact path="/" component={IndexPage}/>
            <Route path="/center" component={PersonalCenter} />
            <Route exact path="/login" component={Login} />
            <Route exact path="/userInfo" component={UserInfo} />
            <Route exact path="/alarm" component={Alarm} />
            <Route exact path="/main" component={Main} />
            <Route exact path="/autoOrxygenationSetting" component={AutoOrxygenationSetting} />
            {/* <Route component={NotFound}/> */}
        </Switch>
    </div>
);

module.exports = App;