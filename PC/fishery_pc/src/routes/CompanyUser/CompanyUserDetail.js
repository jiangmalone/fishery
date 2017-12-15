import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Row, Col, Card, Input, Icon, Button } from 'antd';
const ButtonGroup = Button.Group;
import PageHeaderLayout from '../../layouts/PageHeaderLayout';
import styles from "./companyUserDetail.less"
export default class CompanyUserDetail extends React.Component {

    render() {

        return (
            <PageHeaderLayout >
                <Card bordered={false}>
                    <Row>
                        <Col span={8}>企业名称: &nbsp;&nbsp; 南京鱼儿欢欢有限公司</Col>
                        <Col span={3} offset={10}>塘口数: &nbsp; 52</Col>
                        <Col span={3} >设备在线数: &nbsp;35/40</Col>
                    </Row>

                </Card>
                <Card>
                    <Row>
                        <Col span={8}>
                            <ButtonGroup>
                                <Button>地图查看</Button>
                                <Button>列表查看</Button>
                            </ButtonGroup>
                        </Col>

                        <Col span={3} offset={10}>
                            <Link to=""><Button type="primary">管理塘口</Button></Link>
                        </Col>
                        <Col span={3}>
                            <Link to="/equipment/equipment-management"><Button type="primary">管理设备</Button></Link>
                        </Col>
                    </Row>
                </Card>
            </PageHeaderLayout>
        );
    }
}
