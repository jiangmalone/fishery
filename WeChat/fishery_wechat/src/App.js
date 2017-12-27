
const React = require('react');
const Router = require('react-router-dom');
const Route = Router.Route;
const Switch = Router.Switch;
const Redirect = Router.Redirect;
import IndexPage from './routes/IndexPage';
import Login from './routes/Login/Login';
import UserInfo from './routes/Login/UserInfo'
import Alarm from './routes/Alarm/Alarm';

import PersonalCenter from './routes/PersonalCenter/PersonalCenter';
import addPond from './routes/Mypond/AddPond/AddPond';
import addFish from './routes/Mypond/AddPond/AddFish';
import Main from './routes/Main/Main'
import AutoOrxygenationSetting from './routes/AutoOxygenationSetting/AutoOxygenationSetting'

const App = (props) =>{
    return ( true?<div style={{height:'100%'}}>
        <Switch>
            <Route exact path="/" component={IndexPage}/>
            <Route exact path="/userInfo" component={UserInfo} />
            <Route exact path="/alarm" component={Alarm} />
            <Route path="/center" component={PersonalCenter} />
            <Route path="/addPond" component={addPond} />
            <Route path="/addFish" component={addFish} />
            <Route exact path="/main" component={Main} />
            <Route exact path="/autoOrxygenationSetting" component={AutoOrxygenationSetting} />
            {/* <Route component={NotFound}/> */}
        </Switch>
    </div>: <Redirect to={{
        pathname: '/login',
        state: { from: props.location }
      }}/>
)};

module.exports = App;