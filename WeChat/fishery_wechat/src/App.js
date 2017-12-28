
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
import Main from './routes/Main/Main';
import AutoOrxygenationSetting from './routes/AutoOxygenationSetting/AutoOxygenationSetting';
import AddEquipment from './routes/Equipment/AddEquipment';
import MyPond from './routes/Mypond/MyPond/MyPond';
import MyEquipment from './routes/Equipment/MyEquipment';
import EquipmentManagement from './routes/Equipment/EquipmentManagement'
import BindEquipment from './routes/Equipment/BindEquipment'


const App = (props) => {
    return (true ? <div style={{ height: '100%' }}>
        <Switch>
            <Route exact path="/" component={IndexPage} />
            <Route exact path="/userInfo" component={UserInfo} />
            <Route exact path="/alarm" component={Alarm} />
            <Route path="/center" component={PersonalCenter} />
            <Route path="/addPond" component={addPond} />
            <Route path="/addFish" component={addFish} />
            <Route path="/MyPond" component={MyPond} />
            <Route exact path="/main" component={Main} />
            <Route exact path="/autoOrxygenationSetting" component={AutoOrxygenationSetting} />
            <Route exact path="/addEquipment" component={AddEquipment} />
            <Route exact path="/myEquipment" component={MyEquipment} />
            <Route exact path="/equipmentManagement" component={EquipmentManagement} />
            <Route exact path="/bindEquipment" component={BindEquipment} />

            {/* <Route component={NotFound}/> */}
        </Switch>
    </div> : <Redirect to={{
        pathname: '/login',
        state: { from: props.location }
    }} />
    )
};

module.exports = App;