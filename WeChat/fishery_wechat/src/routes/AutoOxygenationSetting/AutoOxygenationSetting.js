
import { List, InputItem, Button, DatePicker, Modal, Toast } from 'antd-mobile';
import { createForm } from 'rc-form';
import NavBar from '../../components/NavBar';
import { withRouter } from "react-router-dom";
import { connect } from 'dva';
import addImg from '../../img/add.png';
import question from '../../img/question.png';
import moment from 'moment';
import './autoOxygenationSetting.less';
import { autoSet, queryAeratorData } from '../../services/equipment.js'; //接口

const Item = List.Item;
const CustomChildren = ({ extra, onClick, children }) => (
    <div
      onClick={onClick}
      style={{ backgroundColor: '#fff', height: '37px', lineHeight: '37px', padding: '0 0' }}
    >
      {children}
      <span style={{ float: 'right', color: '#888' }}>{extra}</span>
    </div>
  );
class AutoOxygenationSetting extends React.Component {

    constructor(props) {
        super(props);
        const data = JSON.parse(this.props.match.params.data);
        const device_sn = data.device_sn;
        const way = data.way
        this.state = {
            device_sn: device_sn,
            way: way,
            time: '',
            timeSections: [['', '']],
            isShowDeclare: false
        }
    }
    

    onSubmit = () => {

        let timeSections = this.state.timeSections;
        // if(timeSections.length <= 0) {
        //     Toast.fail('必须选择至少一个时间段！', 1);
        //     return;
        // }
        if (timeSections.length == 1) {
            if (timeSections[0][0] == '' && timeSections[0][1] == '') {
                timeSections = [];
            }   
        }
        for (let i = 0; i < timeSections.length; i++) {
            if (!timeSections[i][0] || !timeSections[i][1]) {
                Toast.fail('有未填写完整的时间段，请填写后提交！', 1);
                return;
            }
            for (let j = i + 1; j < timeSections.length; j++) {
                if (timeSections[i][0] < timeSections[j][0] && timeSections[i][1] > timeSections[j][0] || 
                    timeSections[j][0] < timeSections[i][0] && timeSections[j][1] > timeSections[i][0] 
                ) {
                    Toast.fail('时间段不能交错，请修改后提交！', 1);
                    return;
                }
           }
        }

        this.props.form.validateFields({ force: true }, (error) => {
            if (!error) {
                let form = this.props.form.getFieldsValue(), timers = [];
                form.device_sn = this.state.device_sn;
                form.way = this.state.way;
                timeSections.map((item, index) => {
                    timers.push({
                        startTime: item[0],
                        endTime: item[1],
                        way: this.state.way,
                        device_sn: this.state.way,                                                                       
                    })
                })

                // console.log(this.props.form.getFieldsValue());
                autoSet({
                    limit_Install:form,
                    timers: timers
                }).then(res => {
                    if (res.data.code == 0) {
                        Toast.success('设置成功', 1);
                        setTimeout(() => {
                            this.props.dispatch({
                                type: 'global/changeState',
                                payload: {
                                    transitionName: 'right'
                                }
                            })
                            this.props.history.push('/main');
                        }, 1000);
                    } else {
                        Toast.fail(res.data.msg, 1)
                    }
                }).catch(error => {
                    console.log(error)
                    Toast.fail('设置失败，请重试!', 1)
                })
            } else {
                // alert('Validation failed');
            }
        });
    }
    getTaskTime = (strDate) => {  
        console.log("原始时间格式："+strDate);  
        var date = new Date(strDate);  
        var y = date.getFullYear();   
        var m = date.getMonth() + 1;    
        m = m < 10 ? ('0' + m) : m;    
        var d = date.getDate();    
        d = d < 10 ? ('0' + d) : d;    
        var h = date.getHours();    
        var minute = date.getMinutes();    
        minute = minute < 10 ? ('0' + minute) : minute;  
        var str = y+"-"+m+"-"+d+" "+h+":"+minute;  
        console.log("转换时间格式："+str);  
        return str;  
    };  
    
    queryAeratorData = () => {
        queryAeratorData({
            device_sn: this.state.device_sn,
            way: this.state.way
        }).then(res => {
            if (res.data.code == 0) {
                this.props.form.setFieldsValue({
                    low_limit: res.data.oxyLowLimit || 0,
                    up_limit: res.data.oxyUpLimit || 0,
                    high_limit: res.data.oxyHighLimit || 0,
                })
                if (res.data.timerList && res.data.timerList.length > 0) {
                    
                    // this.setState({timeSections: res.data.timerList});
                    let timeArray = []
                    res.data.timerList.map((item, index) => {
                        let now = new Date();
                        let startTime = now.getFullYear() + '/' + (now.getMonth() + 1) +'/' + now.getDay() + ' ' + item.startTime;
                        let endTime = now.getFullYear() + '/' + (now.getMonth() + 1) +'/' + now.getDay() + ' ' + item.endTime;
                        startTime = moment(startTime);
                        endTime = moment(endTime);
                        let arr = [startTime , endTime];
                        timeArray.push(arr);
                    })
                    this.setState({timeSections: timeArray});
                } else {
                    this.setState({timeSections: [['', '']]});
                }
            } else {
                Toast.fail(res.data.msg, 1);
            }
        }).catch(error => {
            console.log(error);
            Toast.fail('获取失败，请重试!', 1);
        })
    }
    onReset = () => {
        this.props.form.resetFields();
    }

    addTimeSection = () => {
        const timeSections = this.state.timeSections
        timeSections.push(['', '']);
        this.setState({
            timeSections: timeSections
        })
    }

    handleTimeChange = (time, index, aryIndex) => {
        let timeSections = this.state.timeSections;
        let timeSection = timeSections[index];
        const min = time.minute();
        if (min < 30) {
            time.minute(0);
        } else {
            time.minute(30);
        }
        if (aryIndex == 0) {
            if(timeSection[1]) {
                if(timeSection[1] < time) {
                    Toast.fail('开始时间不能大于等于结束时间', 1);
                    return;
                }
            }
        } else if (aryIndex == 1) {
            if(timeSection[0]) {
                if(timeSection[0] > time) {
                    Toast.fail('开始时间不能大于等于结束时间', 1);
                    return;
                }
            }
        }
        timeSection[aryIndex] = time;
        this.setState({timeSections: timeSections});
    }
    render() {
        const { getFieldProps, getFieldError } = this.props.form;
        const timeSections = this.state.timeSections;
        const times = timeSections.map((item, index) => {
            return <Item className='timeItem' key={index} extra={<div className='time' >
                <DatePicker
                    mode="time"
                    minuteStep={30}
                    value={item[0]}
                    onChange={time => this.handleTimeChange(time, index, 0 )}
                    extra='开始时间'
                    className=''
                >
                    {/* <span>开始时间&nbsp;</span> */}
                    <CustomChildren></CustomChildren>
                </DatePicker>
                -
                    <DatePicker
                    mode="time"
                    minuteStep={30}
                    value={item[1]}
                    extra='结束时间'
                    onChange={time => this.handleTimeChange(time, index, 1 )}
                    className=''
                >
                    {/* <span>&nbsp;结束时间</span> */}
                    <CustomChildren></CustomChildren>
                </DatePicker>
            </div>}>定时增氧</Item>
        })
        return (<form className='oxygen-set-bg' >
            <div className="nav-bar-title">
                <i className="back" onClick={() => {
                    this.props.dispatch({
                        type: 'global/changeState',
                        payload: {
                            transitionName: 'right'
                        }
                    });
                    history.back();
                }}></i>
                设置自动增氧
                <i className="right-item-none" onClick={() => { this.setState({ isShowDeclare: true }) }} ><img src={question} style={{ width: '.44rem', verticalAlign: 'middle' }} /></i>
            </div>
            <Modal
                visible={this.state.isShowDeclare}
                transparent
                maskClosable={true}
                onClose={() => { this.setState({ isShowDeclare: false }) }}
                title="说明"
                footer={[{ text: '知道了', onPress: () => { this.setState({ isShowDeclare: false }) } }]}
            >
                <div style={{ height: 100, textAlign: 'left' }}>
                    1.下限（低于下限自动开启增氧）；<br />
                    2.上限（达到上限停止增氧）；<br />
                    3.高限（高于高限时增氧2小时）；<br />
                    4.定时增氧（设定时段，定时强制增氧）；<br />
                </div>
            </Modal>
            <List className='os-list'>
                <InputItem
                    {...getFieldProps('low_limit', {
                        rules: [
                            { required: true }
                        ],
                    }) }
                    className="os-input"
                    error={!!getFieldError('low_limit')}
                    extra={<span>mg/L</span>}
                >增氧下限</InputItem>
                <InputItem
                    {...getFieldProps('up_limit', {
                        rules: [
                            { required: true }
                        ],
                    }) }
                    className="os-input"
                    error={!!getFieldError('up_limit')}
                    extra={<span>mg/L</span>}
                >增氧上限</InputItem>
                <InputItem
                    {...getFieldProps('high_limit', {
                        rules: [
                            { required: true }
                        ],
                    }) }
                    className="os-input"
                    error={!!getFieldError('high_limit')}
                    extra={<span>mg/L</span>}
                >增氧高限</InputItem>
                {times}
            </List>
            <div className='add-block' onClick={this.addTimeSection} >
                <span className='add-span'>定时</span><img className='add-img' src={addImg} />
            </div>
            <div className='buttons'>
                <div className='left-button' onClick={this.queryAeratorData} >
                    从设备获得
                </div>
                <div className='right-button' onClick={this.onSubmit}>
                    设置到设备
                </div>
            </div>
        </form>);
    }
}

const AutoOxygenationSettingForm = createForm()(AutoOxygenationSetting);
export default connect()(AutoOxygenationSettingForm);
