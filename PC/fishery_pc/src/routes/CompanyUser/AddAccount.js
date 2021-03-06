import React from 'react';
import { connect } from 'dva';
import { Form, Modal, Input } from 'antd';

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

function AddAccount({ visible, form, onOk, onCancel, wrapClassName, modifyId,formData2 }) {
    const { getFieldDecorator, validateFieldsAndScroll } = form;
    let checkPassword = (rule, value, callback) => {
        if (value && value !== form.getFieldValue('password')) {
            callback('两次输入的密码不同');
        } else {
            callback();
        }
    }
    return <Modal title={modifyId ? '修改账户':"新增账户"}
        visible={visible}
        onOk={() => {
            let obj = {}
            validateFieldsAndScroll((err, values) => {
                if (!err) {
                    obj = values;
                    onOk(obj)
                }
            })

        }}
        onCancel={() => { onCancel() }}
        wrapClassName={wrapClassName}
        okText="确认"
        cancelText="取消">
        <Form style={{ width: '100%' }}>
            <FormItem label="账号" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('account', {
                    rules: [
                        { required: true, message: '请填写企业账号名称' },
                    ],
                })(<Input style={{ width: 200 }} disabled={formData2.fields.account&&formData2.fields.account.isSave?true:false}/>)}
            </FormItem>
            <FormItem label="名称" {...formItemLayout} style={{ width: '100%' }}  >
                {getFieldDecorator('name')(<Input style={{ width: 200 }} disabled />)}
            </FormItem>
            <FormItem label="密码" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('password', {
                    rules: [
                        { required: true, message: '请输入密码' },
                    ],
                })(<Input  type="password" style={{ width: 200 }} />)}
            </FormItem>
            <FormItem label="密码确认" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('password2', {
                    rules: [
                        { required: true, message: '请确认密码' },
                        { validator: checkPassword }
                    ],
                })(<Input type="password" style={{ width: 200 }} />)}
            </FormItem>
            <FormItem label="备注" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('comment')(<Input style={{ width: 200 }} />)}
            </FormItem>
        </Form>
    </Modal >
}

let AccountForm = Form.create({
    mapPropsToFields: (props) => {
        return {
            account: Form.createFormField({
                ...props.formData2.fields.account
            }),
            name: Form.createFormField({
                ...props.formData2.fields.name
            }),
            password: Form.createFormField({
                ...props.formData2.fields.password
            }),
            password2: Form.createFormField({
                ...props.formData2.fields.password2
            }),
            comment: Form.createFormField({
                ...props.formData2.fields.comment
            }),
        }
    },
    onFieldsChange: (props, fields) => {
        props.dispatch({
            type: 'companyUser/changeModal',
            payload: { formData2: { fields: { ...props.formData2.fields, ...fields } } }
        })
    }
})(AddAccount)
export default connect((state => {
    return ({
        loading: state.companyUser.loading,
        error: state.companyUser.error,
        formData2: state.companyUser.formData2
    })
}))(AccountForm)