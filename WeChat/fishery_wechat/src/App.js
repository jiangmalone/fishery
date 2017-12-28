
const React = require('react');
const Router = require('react-router-dom');
const Route = Router.Route;
const Switch = Router.Switch;
const Redirect = Router.Redirect;
import { connect } from 'dva';
import CSSTransitionGroup from "react-addons-css-transition-group";
import IndexPage from './routes/IndexPage';
import Login from './routes/Login/Login';
import UserInfo from './routes/Login/UserInfo'
import Alarm from './routes/Alarm/Alarm';
import PersonalCenter from './routes/PersonalCenter/PersonalCenter';
import addPond from './routes/Mypond/AddPond/AddPond';
import addFish from './routes/Mypond/AddPond/AddFish';
import Main from './routes/Main/Main';
import AutoOrxygenationSetting from './routes/AutoOxygenationSetting/AutoOxygenationSetting';
import AddEquipment from './routes/Equipment/AddEquipment';
import MyPond from './routes/Mypond/MyPond/MyPond';
import MyEquipment from './routes/Equipment/MyEquipment';
import EquipmentManagement from './routes/Equipment/EquipmentManagement'
import BindEquipment from './routes/Equipment/BindEquipment'

const styles = {}

styles.fill = {
    position: 'absolute',
    left: 0,
    right: 0,
    top: 0,
    bottom: 0,
    height: '100%'
}

styles.content = {
    ...styles.fill
}

const App = (props) => {
    console.log(props)
    return (true ? <div><CSSTransitionGroup
        transitionName={props.transitionName}
        style={styles.content}
        transitionEnterTimeout={400}
        transitionLeaveTimeout={400}
    // transitionAppear = {true}
    >
        {/* <Switch> */}
        <div key={props.location.pathname} style={styles.content} >
            <Route location={props.location} path="/alarm" component={Alarm} />
            <Route location={props.location} path="/main" component={Main} />
            <Route location={props.location} exact path="/" component={IndexPage} />
            <Route location={props.location} path="/userInfo" component={UserInfo} />
            <Route location={props.location} path="/center" component={PersonalCenter} />
            <Route location={props.location} path="/addPond" component={addPond} />
            <Route location={props.location} path="/addFish" component={addFish} />
            <Route location={props.location} path="/MyPond" component={MyPond} />
            <Route location={props.location} path="/autoOrxygenationSetting" component={AutoOrxygenationSetting} />
            <Route location={props.location} path="/addEquipment" component={AddEquipment} />
            <Route location={props.location} path="/myEquipment" component={MyEquipment} />
            <Route location={props.location} exact path="/equipmentManagement" component={EquipmentManagement} />
            <Route location={props.location} exact path="/bindEquipment" component={BindEquipment} />

            {/* <Route component={NotFound}/> */}
            {/* </Switch> */}
        </div>
    </CSSTransitionGroup>
    </div> : <Redirect to={{
        pathname: '/login',
        state: { from: props.location }
    }} />
    )
};

module.exports = connect((state) => {
    return ({
        transitionName: state.global.transitionName
    })
})(App);