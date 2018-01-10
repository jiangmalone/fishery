import React from 'react';
import { connect } from 'dva';
import { Form, Modal, Input} from 'antd';

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
function AddUser({ visible, form, onOk, onCancel, wrapClassName,modifyId }) {
    const { getFieldDecorator, validateFieldsAndScroll } = form;

    return <Modal title={modifyId?"新增企业":'修改企业'}
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
        onCancel={() => { onCancel() }}
        wrapClassName={wrapClassName}
        okText="确认"
        cancelText="取消">
        <Form style={{ width: '100%' }}>
            <FormItem label="名称" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('name', {
                    rules: [
                        { required: true, message: '请填写企业名称' },
                    ],
                })(<Input style={{ width: 200 }} />)}
            </FormItem>
            <FormItem label="联系方式" {...formItemLayout} style={{ width: '100%' }} >
                {getFieldDecorator('phone', {
                    rules: [
                        { required: true, message: '请填写企业联系方式' },
                    ],
                })(<Input style={{ width: 200 }} />)}
            </FormItem>
            <FormItem label="养殖年限" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('life', {
                    rules: [
                        { required: true, message: '请填写企业养殖年限' },
                    ],
                })(<Input style={{ width: 200 }} />)}
            </FormItem>
            <FormItem label="联系地址" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('address', {
                    rules: [
                        { required: true, message: '请填写企业联系地址' },
                    ],
                })(<Input style={{ width: 200 }} />)}
            </FormItem>
        </Form>
    </Modal >
}

let CompanyForm = Form.create({
    mapPropsToFields: (props) => {
        return {
            name: Form.createFormField({
                ...props.formData.fields.name
            }),
            phone: Form.createFormField({
                ...props.formData.fields.phone
            }),
            life: Form.createFormField({
                ...props.formData.fields.life
            }),
            address: Form.createFormField({
                ...props.formData.fields.address
            }),
        }
    },
    onFieldsChange: (props, fields) => {
        props.dispatch({
            type: 'companyUser/changeModal',
            payload: { formData: { fields: { ...props.formData.fields, ...fields } } }
        })
    }
})(AddUser)
export default connect((state => {
    return ({
        loading: state.companyUser.loading,
        error: state.companyUser.error,
        formData: state.companyUser.formData
    })
}))(CompanyForm)