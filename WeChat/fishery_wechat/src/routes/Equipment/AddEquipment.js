
import { Button, Toast } from 'antd-mobile';
import NavBar from '../../components/NavBar';
import { withRouter } from "react-router-dom";
import { connect } from 'dva';
import './addEquipment.less';
import scan from '../../img/scan_QR.png'
import { addEquipment } from '../../services/equipment.js'; //接口
import { getWXConfig } from '../../services/wxConfig.js'; //接口

class AddEquipment extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            equipmentCode: ''
        }
    }

    componentDidMount() {
        getWXConfig({
            targetUrl: window.location.href
        }).then((res) => {
            let data = res.data;
            //先注入配置JSSDK信息
            wx.config({
                debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                appId: data.appId, // 必填，公众号的唯一标识
                timestamp: data.timestamp, // 必填，生成签名的时间戳
                nonceStr: data.nonceStr, // 必填，生成签名的随机串
                signature: data.signature,// 必填，签名，见附录1
                jsApiList: [
                    'checkJsApi',
                    'scanQRCode'
                ] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
            });
            wx.ready(function () {
                console.log("验证微信接口成功");
            });
        })
    }

    scanEquipment = () => {
        wx.scanQRCode({
            desc: 'scanQRCode desc',
            needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
            scanType: ["barCode"], // 可以指定扫二维码还是一维码，默认二者都有
            success: function (res) {
            var result = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
            alert(result);
            this.setState({equipmentCode: result});
        }
        });
    }

    doAddEquipment = () => {
        if (!this.state.equipmentCode) {
            Toast.info('请输入设备编号!', 1);
            return;
        }
        this.props.dispatch({
            type: 'global/changeState',
            payload: {
                transitionName: 'left'
            }
        })
        this.props.history.push(`/addEquipmentDetail/${this.state.equipmentCode}`);

    }

    render() {
        return (<div className='addEqu_bg' >
            <NavBar
                title='添加设备'
            />
            <div className='add-line'>
                <input
                    placeholder='请输入设备编号'
                    value={this.state.equipmentCode}
                    onChange={e => this.setState({
                        equipmentCode: e.target.value.replace(/ /g, '')
                    })}
                />
                <div onClick={this.doAddEquipment}>添加</div>
            </div>
            <div className='scan-block' onClick={this.scanEquipment}>
                <img src={scan} />
                <p>扫描设备条码添加</p>
            </div>
        </div>)
    }
}

export default connect()(AddEquipment);
