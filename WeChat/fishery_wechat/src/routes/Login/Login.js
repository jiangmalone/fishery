import React from 'react';
import './login.less'
import { Flex, Toast } from 'antd-mobile'
import { verification, verifySmsCode } from '../../services/sms.js'
import { withRouter } from "react-router-dom";
import getParameterByName from '../../utils/getParam.js'

import login_logo from '../../img/logo.png';
import login_password_logo from '../../img/password-logo.png';
import login_user_logo from '../../img/user-logo.png';
class Login extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            allowSend: true,
            isphone: false,
            wait: 60
        }
    }

    sendCode() {
        let myHeaders = new Headers({
            "Content-Type": "form-data",
        })
        if (this.state.allowSend) {
            verification({
                method: 'post',
                headers: myHeaders,
                mode: 'cors',
                cache: 'default'
            }, {
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

    componentWillUnmount() {
        clearInterval(this.timer)
    }

    Login() {
        let myHeaders = new Headers({
            "Content-Type": "form-data",
        })

        verifySmsCode({
            method: 'post',
            headers: myHeaders,
            mode: 'cors',
            cache: 'default'
        }, {
                openId: getParameterByName('openid'),
                phone: this.state.phone,
                smscode: this.state.smscode,
                headimgurl: getParameterByName('headimgurl'),
                nickName: getParameterByName('nickname')
            }).then((res) => {
                if (res.data.client.phone) {
                    window.localStorage.setItem('headimgurl', getParameterByName('headimgurl'))
                    window.localStorage.setItem('nickName', getParameterByName('nickname'))
                    window.localStorage.setItem('phone', res.data.client.phone)
                    window.localStorage.setItem('openid', getParameterByName('openid'))
                    window.localStorage.setItem('clientId', res.data.client.id)
                    dplus.track('登录', { 'phone': this.state.phone });
                    //this.props.history.push(`/${getParameterByName('directUrl')  == 'ordertrack' ? 'ordertrack?orderId=$' : getParameterByName('directUrl') }`)
                    if (window.localStorage.getItem('isInfoSaved')) {
                        this.props.history.push(`/indexPage`)
                    } else {
                        this.props.history.push(`/${getParameterByName('directUrl')}`)
                    }
                } else {
                    Toast.fail(res.data.msg, 2)
                }
            })
    }
    render() {
        return <div className='loginbg' style={{ height: window.document.body.clientHeight }} >
            <Flex justify='center'>
                <img className='logo' src={login_logo} />
            </Flex>
            <Flex justify='center' >
                <div className='input-up'>
                    <img src={login_user_logo} style={{ width: '.4rem', verticalAlign: 'middle', backgroundColor: '#fff', padding: 10, opacity: 0.7 }} />
                    <input className='no-border' style={{ width: '6.4rem', paddingLeft: 15, backgroundColor: "#fff",opacity: 0.3  }} onChange={(e) => {
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
                    }} placeholder="请输入手机号码" />
                </div>
            </Flex>
            <Flex justify='center'>
                <div className='input-up'>
                    <img src={login_password_logo} style={{ width: '.4rem', verticalAlign: 'middle', backgroundColor: '#fff', padding: 10, opacity: 0.7 }} />
                    <input className='no-border' placeholder="请输入验证码" style={{ display: 'inner-block', width: '4rem', paddingLeft: 15, backgroundColor: "#fff",opacity: 0.3}} onChange={(e) => { this.setState({ smscode: e.target.value }) }} />
                    <div className='no-border' style={{lineHeight: '.9rem', display: 'inline-block', color: this.state.isphone ? '#5b87e5' : '#35b4e8', verticalAlign: 'middle', padding: '0 .49rem', backgroundColor: "#fff",opacity: 0.3}} onClick={() => { if (this.state.isphone) { this.sendCode() } }}>{this.state.allowSend ? '获取验证码' : `${this.state.wait}s后重发`}</div>
                </div>
            </Flex>
            <div style={{ width: '5.5rem' ,height: '0.8rem', textAlign: 'center', color: "#fff", borderRadius: '10rem', lineHeight: '0.8rem', margin: '0 auto', background: '#35b4e8', marginTop: 15}} onClick={() => { this.Login() }}>
                登 录
            </div>
            <div style={{ textAlign: 'center', color: "#fff", lineHeight: '0.98rem', fontSize: '.18rem' }}>
                为了防止用户信息被盗,请使用本机号码
            </div>
        </div>
    }
}

export default withRouter(Login);
