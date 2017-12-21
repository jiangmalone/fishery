import React from 'react';
import { Flex } from 'antd-mobile';
import NotFound from '../img/404.png'

class OrderDetail extends React.Component {

    constructor(props) {
        super(props)
        this.state = {

        }
    }
    render() {
        return <div className="body-bac" style={{height:'100%'}} >
        <Flex style={{height:'100%'}}>
            <Flex.Item>
                <img src={NotFound} alt="404" style={{width:'100%'}}/>
            </Flex.Item>
        </Flex>
        </div>
    }
}

export default OrderDetail