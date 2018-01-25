import React from 'react';
import './userInfo.less'
import { Flex, Toast } from 'antd-mobile'
import { withRouter } from "react-router-dom";
import getParameterByName from '../../utils/getParam.js'

import defaultAvater from '../../img/default-avater.jpg';
import SexRadio from '../../components/SexRadio';
import { modifyWXUser } from '../../services/user.js'; //接口
class UserInfo extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            form: {
                name: window.localStorage.getItem('name') ? window.localStorage.getItem('name') : '',
                sex: window.localStorage.getItem('sex')?window.localStorage.getItem('sex'):'',  //0 man 1 faleman
                phone: window.localStorage.getItem('phone') ? window.localStorage.getItem('phone') : '',
                years: window.localStorage.getItem('life') ? window.localStorage.getItem('life') : ''
            }
        }
    }

    handlaSexClick = (type) => {
        let form = this.state.form;
        form['sex'] = type ;
        this.setState({
            form: form
        })
    }

    handlaInput = (type, value) => {
        if (type) {
            const form = this.state.form;
            switch (type) {
                case 'name':
                    form['name'] = value;
                    this.setState({
                        form: form
                    })
                    break;
                case 'phone':
                    form['phone'] = value.replace(/\D/g, '');
                    this.setState({
                        form: form
                    })
                    break;
                case 'years':
                    form['years'] = value.replace(/\D/g, '');
                    this.setState({
                        form: form
                    })
                    break;
                default: break;
            }
        }
    }

    saveInfo = () => {
        const form = this.state.form;
        if (form.name == '') {   //请输入名称
            Toast.fail('请输入您的姓名！', 1);
        } else {
            const form = this.state.form;
            modifyWXUser({
                id: window.localStorage.getItem('id'),
                openId: window.localStorage.getItem('openid'),
                name: form.name,
                phone: form.phone,
                sex: form.sex,
                life: form.years
            }).then((res) => {
                if (res.data.code == 0) {
                    window.localStorage.setItem("name", form.name);
                    window.localStorage.setItem("phone", form.phone);
                    window.localStorage.setItem("sex", form.sex);
                    window.localStorage.setItem("life", form.life);
                    this.props.history.push(`/main`)
                } else {
                    Toast.fail(res.data.msg, 1);
                }
            }).catch((error) => { console.log(error) });
        }
    }

    render() {
        return <div className='user-info-bg' style={{ height: window.document.body.clientHeight }} >
            <Flex justify='center'>
                <img className='avater' src={window.localStorage.getItem('headimgurl')} />
            </Flex>
            <div style={{ marginBottom: '.74rem' }}>
                <div className='input-line'>
                    <div className='left-item'>
                        姓名<span className='red-start'>*</span>
                    </div>
                    <div className='right-item'>
                        <input className='input' placeholder='请输入您的真实姓名' value={this.state.form.name} onChange={(e) => { this.handlaInput('name', e.target.value) }} />
                    </div>
                </div>
                <div className='input-line'>
                    <div className='left-item'>
                        性别
                    </div>
                    <div className='right-item'>
                        <SexRadio value={this.state.form.sex} handlaClick={this.handlaSexClick} ></SexRadio>
                    </div>
                </div>
                <div className='input-line'>
                    <div className='left-item'>
                        手机号码
                    </div>
                    <div className='right-item'>
                        <input maxLength='11' className='input' placeholder='请输入您的手机号码' value={window.localStorage.getItem('phone')} />
                    </div>
                </div>
                <div className='input-line'>
                    <div className='left-item'>
                        养殖年限(年)
                    </div>
                    <div className='right-item'>
                        <input className='input' placeholder='请输入您的养殖年限' value={this.state.form.years} onChange={(e) => { this.handlaInput('years', e.target.value) }} />
                    </div>
                </div>
            </div>
            <div className='save-button' onClick={() => { this.saveInfo() }} >
                保  存
            </div>
        </div>
    }
}

export default withRouter(UserInfo);
