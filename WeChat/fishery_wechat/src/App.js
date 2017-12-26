
const React = require('react');
const Router = require('react-router-dom');
const Route = Router.Route;
const Switch = Router.Switch;

import PersonalCenter from './routes/PersonalCenter/PersonalCenter'

import IndexPage from './routes/IndexPage';

import Login from './routes/Login/Login';

const App = () => (
    <div style={{height:'100%'}}>
        <Switch>
            <Route exact path="/" component={IndexPage}/>
            <Route path="/personalCenter" component={PersonalCenter} />
            <Route exact path="/login" component={Login} />
            {/* <Route component={NotFound}/> */}
        </Switch>
    </div>
);

module.exports = App;