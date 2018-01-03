import React from 'react';
import { connect } from 'dva';
import { Form, Modal, Input, Select, Radio } from 'antd';

const RadioGroup = Radio.Group;
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
function AddUser({ visible, form, onOk, onCancel, wrapClassName }) {
    const { getFieldDecorator, validateFieldsAndScroll } = form;

    return <Modal title="新增塘口"
        visible={visible}
        onOk={() => {
            let obj = {}
            validateFieldsAndScroll((err, values) => {
                if (!err) {
                    obj = values
                } 
            })
            onOk(obj)
        }}
        onCancel={()=>{onCancel()}}
        wrapClassName={wrapClassName}
        okText="确认"
        cancelText="取消">
        <Form style={{ width: '100%' }}>
            <FormItem label="名称" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('name', {
                    rules: [
                        { required: true, message: '请填写用户名称' },
                    ],
                })(<Input style={{ width: 200 }} />)}
            </FormItem>
            <FormItem label="性别" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('sex')(<RadioGroup>
                    <Radio value={1}>男</Radio>
                    <Radio value={2}>女</Radio>
                </RadioGroup>)}
            </FormItem>
            <FormItem label="联系方式" {...formItemLayout} style={{ width: '100%' }} >
                {getFieldDecorator('phone', {
                    rules: [
                        { required: true, message: '请填写用户联系方式' },
                    ],
                })(<Input style={{ width: 200 }} />)}
            </FormItem>
            <FormItem label="养殖年限" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('life', {
                    rules: [
                        { required: true, message: '请填写用户联系方式' },
                    ],
                })(<Input style={{ width: 200 }} />)}
            </FormItem>
            <FormItem label="联系地址" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('address', {
                    rules: [
                        { required: true, message: '请填写用户联系方式' },
                    ],
                })(<Input style={{ width: 200 }} />)}
            </FormItem>
        </Form>
    </Modal >
}

export default Form.create()(AddUser)