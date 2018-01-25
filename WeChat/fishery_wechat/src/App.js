import React from 'react';
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
import AddEquipmentDetail from './routes/Equipment/AddEquipmentDetail';
import MyPond from './routes/Mypond/MyPond/MyPond';
import MyEquipment from './routes/Equipment/MyEquipment';
import EquipmentManagement from './routes/Equipment/EquipmentManagement';
import BindEquipment from './routes/Equipment/BindEquipment';
import SensorDetail from './routes/Equipment/SensorDetail';
import AddAddress from './routes/Mypond/AddPond/AddAddress';

import { verifyIsLogin } from './services/sms'
let styles = {}

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
};


class App extends React.Component {
    constructor(props) {
        super(props)
        this.state = {}
    }

    componentDidMount() {
        verifyIsLogin({
            phone: '18362981113'
        }).then((res) => {
            if (res.data.code == '0') {
                this.props.dispatch({
                    type: 'global/changeState',
                    payload: { login: true }
                })
            } else {
                this.props.dispatch({
                    type: 'global/changeState',
                    payload: { login: false }
                })
            }
        }).catch((error) => {
            console.log(error)
        })
    }


    render() {
        return (this.props.login ? <div><CSSTransitionGroup
            transitionName={this.props.transitionName}
            style={styles.content}
            transitionEnterTimeout={400}
            transitionLeaveTimeout={400}
        // transitionAppear = {true}
        >
            <div key={this.props.location.pathname} style={styles.content} >
                <Route location={this.props.location} path="/alarm" component={Alarm} />
                <Route location={this.props.location} path="/main" component={Main} />
                <Route location={this.props.location} exact path="/" component={IndexPage} />
                <Route location={this.props.location} path="/userInfo" component={UserInfo} />
                <Route location={this.props.location} path="/center" component={PersonalCenter} />
                <Route location={this.props.location} path="/addPond" component={addPond} />
                <Route location={this.props.location} path="/addPond/:id" component={addPond} />
                <Route location={this.props.location} path="/addFish" component={addFish} />
                <Route location={this.props.location} path="/MyPond" component={MyPond} />
                <Route location={this.props.location} path="/autoOrxygenationSetting/:data" component={AutoOrxygenationSetting} />
                <Route location={this.props.location} path="/addEquipment" component={AddEquipment} />
                <Route location={this.props.location} path="/addEquipmentDetail/:equipmentCode" component={AddEquipmentDetail} />
                <Route location={this.props.location} path="/myEquipment" component={MyEquipment} />
                <Route location={this.props.location} exact path="/equipmentManagement/:data" component={EquipmentManagement} />
                <Route location={this.props.location} exact path="/bindEquipment/:data" component={BindEquipment} />
                <Route location={this.props.location} exact path="/sensorDetail/:device_sn" component={SensorDetail} />
                <Route location={this.props.location} exact path="/address" component={AddAddress} />
                {/* <Route component={NotFound}/> */}
            </div>
        </CSSTransitionGroup>
        </div> : <Redirect to={{
            pathname: '/login',
            state: { from: this.props.location },
        }} />
        )
    }
}

export default connect((state) => {
    return ({
        login: state.global.login,
        transitionName: state.global.transitionName
    })
})(App);