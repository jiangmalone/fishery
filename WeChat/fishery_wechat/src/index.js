import dva from 'dva';
import './index.css';
import {browserHistory} from 'dva/router'
const app = dva({
    history:browserHistory
});

// 2. Plugins
app.use({});

// 3. Model
app.model(require('./models/addPond'));
app.model(require('./models/global'));
// 4. Router
app.router(require('./router'));

// 5. Start
app.start('#root');
