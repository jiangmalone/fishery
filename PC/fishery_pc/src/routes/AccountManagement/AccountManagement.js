import React, { PureComponent } from 'react';
import { Row, Col, Card, Input, Button, message } from 'antd';
import update from 'immutability-helper'
const { TextArea } = Input;
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import { modify } from '../../services/admin'

export default class CompanyUserDetail extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            name: "杨威",
            account: window.localStorage.getItem('account'),
            password: "",
            passwordAgain: "",
        }
    }
    handleOk = () => {
        const account = this.state;
        if (!account.password) {
            message.warn("请输入密码！");
            return;
        } else if (!account.passwordAgain) {
            message.warn("请再次输入密码！");
            return;
        } else if (!(account.password == account.passwordAgain)) {
            message.warn("两次输入的密码不相同，请重新输入");
            return;
        } else {
            modify({
                password: this.state.password,
                adminId: 1
            }).then(response => {
                console.log(response);
                if (response.code == 0) {
                    message.success("修改成功！");
                    this.setState({
                        password: '',
                        passwordAgain: ''
                    });
                } else {
                    message.error(response.msg);
                }
            })
        }
    }

    handleCancal = () => {
        this.setState({
            password: '',
            passwordAgain: ''
        });
    }

    handleInputChange = (value, key) => {
        if (key) {
            this.setState({
                key: value
            })
        }
    }

    render() {
        return (
            <PageHeaderLayout>
                <Card>
                    <Row style={{ height: 60, fontSize: 20 }} align="middle">
                        <Col span={3} offset={7}>
                            账号：
                       </Col>
                        <Col span={6}>
                            {this.state.account}
                        </Col>
                    </Row>
                    <Row style={{ height: 60, fontSize: 20 }} align="middle">
                        <Col span={3} offset={7}>
                            名称：
                       </Col>
                        <Col span={6} >
                            {this.state.name}
                        </Col>
                    </Row>
                    <Row style={{ height: 60, fontSize: 20 }} align="middle">
                        <Col span={3} offset={7}>
                            密码：
                       </Col>
                        <Col span={6} >
                            <Input type="password" value={this.state.password} onChange={e => this.handleInputChange(e.target.value, "password")} />
                        </Col>
                    </Row>
                    <Row style={{ height: 60, fontSize: 20 }} align="middle">
                        <Col span={3} offset={7}>
                            密码确认：
                       </Col>
                        <Col span={6}>
                            <Input type="password" value={this.state.passwordAgain} onChange={e => this.handleInputChange(e.target.value, "passwordAgain")} />
                        </Col>
                    </Row>
                    {/* <Row style={{ height: 150, fontSize: 20 }} align="middle">
                        <Col span={3} offset={7}>
                            备注：
                        </Col>
                        <Col span={6} >
                            <TextArea rows={3} value={this.state.accountInfo.remark} onChange={e => this.handleInputChange(e.target.value, "remark")} />
                        </Col>
                    </Row> */}
                    <Row style={{ height: 60, fontSize: 20 }} align="middle">
                        <Col span={3} offset={8}>
                            <Button size="large" onClick={this.handleCancal}>取消</Button>
                        </Col>
                        <Col span={3} offset={2}>
                            <Button type="primary" size="large" onClick={this.handleOk}>确定</Button>
                        </Col>
                    </Row>
                </Card>
            </PageHeaderLayout>
        );
    }
}
