import React from 'react';
import { connect } from 'dva';
import { Form, Modal, Input, Select, InputNumber } from 'antd';

const Option = Select.Option;
const FormItem = Form.Item;
const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 8 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
    },
};
function AddModal({ modifyId, visible, form, onOk, onCancel, wrapClassName, showMapModal, address }) {
    const children = [];
    const { getFieldDecorator, validateFieldsAndScroll } = form;
    for (let i = 10; i < 36; i++) {
        children.push(<Option key={i.toString(36) + i}>{i.toString(36) + i}</Option>);
    }
    return <Modal title={modifyId ? "新增塘口" : '修改塘口'}
        visible={visible}
        onOk={() => {
            let obj = {}
            validateFieldsAndScroll((err, values) => {
                if (!err) {
                    obj = values
                }
            })
            console.log(obj)
            onOk(obj)

        }}
        onCancel={onCancel}
        wrapClassName={wrapClassName}
        okText="确认"
        cancelText="取消">
        <Form style={{ width: '100%' }}>
            <FormItem label="塘口名称" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('name', {
                    rules: [
                        { required: true, message: '请填写塘口名称' },
                    ],
                })(<Input style={{ width: 200 }} disabled={modifyId?true:false}  />)}
            </FormItem>
            <FormItem label="面积" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('area', { rules: [{ type: 'number', message: '请填写正确值' }] })(<InputNumber style={{ width: 200 }} addonAfter="亩" />)}
            </FormItem>
            <FormItem label="塘口位置" {...formItemLayout} style={{ width: '100%' }} >
                <div onClick={() => { showMapModal() }} style={{ cursor: 'pointer' }}>
                    <span>{address ? address.district + address.address + address.name : '(点击地图图标选取地理位置)'}</span>&nbsp;
                <i className="iconfont icon-address" style={{ fontSize: '20px', cursor: 'pointer', color: address ? '' : '#40a9ff' }} ></i>
                </div>
            </FormItem>
            <FormItem label="深度" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('depth', { rules: [{ type: 'number', message: '请填写正确值' }] })(<InputNumber style={{ width: 200 }} addonAfter="m" />)}
            </FormItem>
            <FormItem label="养殖品种" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('fish_categorys')(<Select
                    mode="tags"
                    placeholder="Please select"
                    style={{ width: '200px' }}
                >
                    {children}
                </Select>)}
            </FormItem>
            <FormItem label="池塘水源" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('water_source')(<Input style={{ width: 200 }} />)}
            </FormItem>
            <FormItem label="底泥厚度" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('sediment_thickness', { rules: [{ type: 'number', message: '请填写正确值' }] })(<InputNumber style={{ width: 200 }} addonAfter="cm" />)}
            </FormItem>
            <FormItem label="塘口密度" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('density', { rules: [{ type: 'number', message: '请填写正确值' }] })(<InputNumber style={{ width: 200 }} addonAfter="kg/㎡" />)}
            </FormItem>
        </Form>
    </Modal >
}

let AddModalForm = Form.create({
    mapPropsToFields: (props) => {
        return {
            name: Form.createFormField({
                ...props.formData.fields.name
            }),
            area: Form.createFormField({
                ...props.formData.fields.area
            }),
            depth: Form.createFormField({
                ...props.formData.fields.depth
            }),
            fish_categorys: Form.createFormField({
                ...props.formData.fields.fish_categorys
            }),
            address: Form.createFormField({
                ...props.formData.fields.address
            }),
            water_source: Form.createFormField({
                ...props.formData.fields.water_source
            }),
            sediment_thickness: Form.createFormField({
                ...props.formData.fields.sediment_thickness
            }),
            density: Form.createFormField({
                ...props.formData.fields.density
            }),
        }
    },
    onFieldsChange: (props, fields) => {
        props.dispatch({
            type: 'pond/changeModal',
            payload: { formData: { fields: { ...props.formData.fields, ...fields } } }
        })
    }
})(AddModal)


export default connect((state => {
    return ({
        loading: state.pond.loading,
        error: state.pond.error,
        formData: state.pond.formData
    })
}))(AddModalForm)