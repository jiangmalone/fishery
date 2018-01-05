import React from 'react';
import './addPond.less';
import { List, InputItem, Picker } from 'antd-mobile';
import NavBar from '../../../components/NavBar';
import { createForm } from 'rc-form';
import { connect } from 'dva';
const Item = List.Item;
const Brief = Item.Brief;

class AddPond extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            edit: false
        }
    }

    submit=()=>{
        this.props.form.validateFields((error,value)=>{
            if(!error) {
                console.log(value)    
            }
        })
    }
    render() {
        const { getFieldProps, getFieldError ,validateFields} = this.props.form;
        const district = [{ label: '淡水', value: '淡水' }, { label: '盐水', value: '盐水' }, { label: '淡盐水', value: '淡盐水' }]
        return (
            <form className="body-bac">
                <NavBar title={"添加塘口"} />
                <List className="addPond-list">
                    <InputItem
                        {...getFieldProps('name', {
                            // initialValue: 'little ant',
                            rules: [
                                { required: true, message: '请输入塘口名称' }
                            ],
                        }) }
                        clear
                        className="addpond-input"
                        error={!!getFieldError('name')}
                        placeholder="请输入塘口名称"
                    >塘口名称</InputItem>
                    <InputItem
                        {...getFieldProps('area') }
                        clear
                        className="addpond-input"
                        labelNumber='5'
                        error={!!getFieldError('area')}
                        placeholder="请输入塘口面积"
                    >面积(亩)</InputItem>
                    <InputItem
                        {...getFieldProps('depth') }
                        clear
                        className="addpond-input"
                        labelNumber='5'
                        error={!!getFieldError('depth')}
                        placeholder="请输入塘口深度"
                    >深度(m)</InputItem>
                    <InputItem
                        {...getFieldProps('density') }
                        clear
                        className="addpond-input"
                        labelNumber='5'
                        error={!!getFieldError('density')}
                        placeholder="请输入塘口密度"
                    >塘口密度(kg/㎡)</InputItem>
                    <InputItem
                        {...getFieldProps('sediment_thickness') }
                        clear
                        className="addpond-input"
                        labelNumber='5'
                        error={!!getFieldError('sediment_thickness')}
                        placeholder="请输入底泥厚度"
                    >底泥厚度(m)</InputItem>
                    <Picker data={district} cols={1} {...getFieldProps('water_source') } className="forss">
                        <List.Item arrow="horizontal">池塘水源</List.Item>
                    </Picker>
                    <Item arrow="horizontal" onClick={() => {
                        this.props.history.push('/addFish');
                        this.props.dispatch({
                            type: 'global/changeState',
                            payload: {
                                transitionName: 'left'
                            }
                        })
                    }} extra={<span>鲤鱼，鲫鱼鲤鱼，鲫鱼鲤鱼，鲫鱼鲤鱼，鲫鱼鲤鱼，鲫鱼鲤鱼，鲫鱼鲤鱼，鲫鱼鲤鱼，鲫鱼</span>} >
                        品种
                    </Item>
                    <Item extra={<span><img src={require('../../../img/earth.png')} /></span>} onClick={() => {
                        this.props.history.push('/address');
                        this.props.dispatch({
                            type: 'global/changeState',
                            payload: {
                                transitionName: 'left'
                            }
                        })
                    }}>
                        塘口位置
                    </Item>
                </List>
                <div className="addPond-btn" onClick={()=>{this.submit()}}>确认提交</div>
            </form>
        );
    }

}

const AddPondForm = createForm()(AddPond);
export default connect()(AddPondForm);
