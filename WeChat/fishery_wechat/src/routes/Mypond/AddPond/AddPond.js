import React from 'react';
import './addPond.less';
import { List, InputItem, Picker, ActivityIndicator, Toast } from 'antd-mobile';
import NavBar from '../../../components/NavBar';
import { createForm } from 'rc-form';
import { connect } from 'dva';
const Item = List.Item;
const Brief = Item.Brief;

class AddPond extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            edit: false,
            loading: this.props.loading
        }
    }

    componentWillReceiveProps(newProps) {
        if (newProps.loading != this.state.loading) {
            this.setState({
                loading: newProps.loading
            })
        }
        if (newProps.error) {
            Toast.fail(newProps.error, 2);
        }
    }


    submit = () => {
        this.props.form.validateFields((error, value) => {
            if (!error) {
                console.log(value)
            }
            value.relation = 'wx3'
            this.props.dispatch({
                type: 'pond/addPond',
                payload: value
            })
        })
    }
    render() {
        const { getFieldProps, getFieldError, validateFields } = this.props.form;
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
                        {...getFieldProps('area', {
                            rules: [
                                { types: ['float', 'int'], message: '请输入正确数据' }
                            ]
                        }) }
                        clear
                        className="addpond-input"
                        labelNumber='5'
                        error={!!getFieldError('area')}
                        placeholder="请输入塘口面积"
                    >面积(亩)</InputItem>
                    <InputItem
                        {...getFieldProps('depth', {
                            rules: [
                                { types: ['float', 'int'], message: '请输入正确数据' }
                            ]
                        }) }
                        clear
                        className="addpond-input"
                        labelNumber='5'
                        error={!!getFieldError('depth')}
                        placeholder="请输入塘口深度"
                    >深度(m)</InputItem>
                    <InputItem
                        {...getFieldProps('density', {
                            rules: [
                                { types: ['float', 'int'], message: '请输入正确数据' }
                            ]
                        }) }
                        clear
                        className="addpond-input"
                        labelNumber='5'
                        error={!!getFieldError('density')}
                        placeholder="请输入塘口密度"
                    >塘口密度(kg/㎡)</InputItem>
                    <InputItem
                        {...getFieldProps('sediment_thickness', {
                            rules: [
                                { types: ['float', 'int'], message: '请输入正确数据' }
                            ]
                        }) }
                        clear
                        className="addpond-input"
                        labelNumber='5'
                        error={!!getFieldError('sediment_thickness')}
                        placeholder="请输入底泥厚度"
                    >底泥厚度(m)</InputItem>
                    <InputItem
                        {...getFieldProps('water_source') }
                        clear
                        className="addpond-input"
                        labelNumber='5'
                        error={!!getFieldError('water_source')}
                        placeholder="请输入池塘水源"
                    >池塘水源</InputItem>
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
                <div className="addPond-btn" onClick={() => { this.submit() }}>确认提交</div>
                <ActivityIndicator
                    toast
                    text="等待中..."
                    animating={this.state.loading}
                />
            </form>
        );
    }

}


const AddPondForm = createForm({
    mapPropsToFields: (props) => {
        return { ...props.formData.fields }

    },
    onFieldsChange: (props, fields) => {
        props.dispatch({
            type: 'pond/changeState',
            payload: { formData: { fields: { ...props.formData.fields, ...fields } } }
        })
    }
})(AddPond);
export default connect((state => {
    return ({
        loading: state.pond.loading,
        error: state.pond.error,
        formData: state.pond.formData
    })
}))(AddPondForm);
