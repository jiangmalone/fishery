import React from 'react';
import './login.less'
import { Flex, Toast } from 'antd-mobile'
import { verification, verifySmsCode } from '../../services/sms.js'
import { withRouter } from "react-router-dom";
import getParameterByName from '../../utils/getParam.js'
import update from 'immutability-helper'
import login_logo from '../../img/logo.png';
import login_password_logo from '../../img/password-logo.png';
import login_user_logo from '../../img/user-logo.png';
import { connect } from 'dva';
import { verifyIsLogin } from '../../services/sms'
class LoginIndex extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            allowSend: true,
            isphone: false,
            wait: 60,
            phone: ''
        }
    }

    sendCode = () => {
        let myHeaders = new Headers({
            "Content-Type": "form-data",
        })
        if (this.state.allowSend) {
            verification({
                phone: this.state.phone
            }).then((res) => {
                if (res.data.code == '0') {
                    this.setState({
                        allowSend: false
                    })
                    this.timer = setInterval(() => {
                        let wait = this.state.wait
                        this.setState({
                            wait: wait - 1
                        })
                        if (this.state.wait < 1) {
                            this.setState({
                                allowSend: true,
                                wait: 60
                            })
                            clearInterval(this.timer)
                        }
                    }, 1000)
                }
            }).catch((error) => {
                console.log(error)
            })
        }
    }

    componentDidMount(){
        window.localStorage.setItem('headimgurl', getParameterByName('headimgurl'));
        window.localStorage.setItem('openid', getParameterByName('openid'));
    }

    componentWillUnmount() {
        clearInterval(this.timer)
    }

    onlogin = () => {
        let myHeaders = new Headers({
            "Content-Type": "form-data",
        })

        verifySmsCode({
            // openId: '112221',
            openId: getParameterByName('openid'),
            phone: this.state.phone,
            smscode: this.state.smscode,
            headimgurl: getParameterByName('headimgurl'),
        }).then((res) => {
            if (res.data.code == '0') {
            
                window.localStorage.setItem('id', res.data.data.id);
                window.localStorage.setItem('relation', res.data.data.relation);
                if( res.data.data.name) {
                    window.localStorage.setItem('name', res.data.data.name);
                }
                window.localStorage.setItem("phone", res.data.data.phone);
                window.localStorage.setItem("sex", res.data.data.sex ? res.data.data.sex : '');
                window.localStorage.setItem("life", res.data.data.life ? res.data.data.life : '');
                this.props.dispatch({
                    type: 'global/changeState',
                    payload: {
                        login: true
                    }
                })
                if (res.data.data.name) {

                    this.props.history.push(`/main`)
                } else {
                    this.props.history.push(`/userInfo`)
                }
            } else {
                Toast.fail(res.data.msg, 2)
            }
        })
    }
    render() {
        return <div className='loginbg'  >
            <Flex justify='center'>
                <img className='logo' src={login_logo} />
            </Flex>
            <div className='input-up'>
                <div className="input-up-box"><img src={login_user_logo} /></div>
                <input className='no-border phone-input-box'
                    onChange={(e) => {
                        if ((/^1(3|4|5|7|8)\d{9}$/.test(e.target.value))) {
                            this.setState({
                                phone: e.target.value,
                                isphone: true
                            })
                        } else {
                            this.setState({
                                phone: e.target.value,
                                isphone: false
                            })
                        }
                    }}
                    placeholder="请输入手机号码"
                    value={this.state.phone}
                />
            </div>
            <div className='input-up'>
                <div className="input-up-box"><img src={login_password_logo} /></div>
                <input className='no-border code-input-box '
                    placeholder="请输入验证码"
                    onChange={(e) => { this.setState({ smscode: e.target.value }) }}
                />
                <div className='no-border getcode-box'
                    style={{ color: '#35b4e8' }}
                    onClick={() => { if (this.state.isphone) { this.sendCode(); } }}
                >
                    {this.state.allowSend ? '获取短信验证码' : `${this.state.wait}s后重发`}
                </div>
            </div>
            <div className='login-button' onClick={() => { this.onlogin() }}>
                登&nbsp;&nbsp;录
            </div>
            <div style={{ textAlign: 'center', color: "#8f8f8f", lineHeight: '0.8rem', fontSize: '.28rem' }}>
                为了防止用户信息被盗,请使用本机号码
            </div>
        </div>
    }
}
export default connect((state) => {
    return ({
        login: state.global.login,
        transitionName: state.global.transitionName
    })
})(LoginIndex);