
import { Button } from 'antd-mobile';
import NavBar from '../../components/NavBar';
import { withRouter } from "react-router-dom";
import './AddEquipment.less';
import scan from '../../img/scan_QR.png'
import offline from '../../img/equ_link_off.png'
import onling from '../../img/equ_link_on.png'

class AddEquipment extends React.Component {

    constructor(props) {
        super(props);
        this.state={

        }
    }
    render() {
        return (<div className='addEqu_bg'>
            <NavBar
                title='添加设备'
            ></NavBar>
            <div className='add-line'>
                <input placeholder='请输入设备编号' />
                <div>添加</div>
            </div>
        </div>)
    }
}

export default AddEquipment;
