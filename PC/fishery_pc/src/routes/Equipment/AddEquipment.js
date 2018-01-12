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
function AddEquipment({ visible, form, onOk, onCancel, wrapClassName,modifyId }) {
    const { getFieldDecorator, validateFieldsAndScroll } = form;

    return <Modal title={modifyId?"新增设备":'修改设备'}
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
            <FormItem label="设备名称" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('name', {
                    rules: [
                        { required: true, message: '请填写设备名称' },
                    ],
                })(<Input style={{ width: 200 }} />)}
            </FormItem>
            <FormItem label="设备编号" {...formItemLayout} style={{ width: '100%' }} >
                {getFieldDecorator('device_sn', {
                    rules: [
                        { required: true, message: '请填写设备编号' },
                    ],
                })(<Input style={{ width: 200 }} />)}
            </FormItem>
        </Form>
    </Modal >
}

let EquipmentForm = Form.create({
    mapPropsToFields: (props) => {
        return {
            name: Form.createFormField({
                ...props.formData.fields.name
            }),
            device_sn: Form.createFormField({
                ...props.formData.fields.device_sn
            })
        }
    },
    onFieldsChange: (props, fields) => {
        props.dispatch({
            type: 'equipment/changeModal',
            payload: { formData: { fields: { ...props.formData.fields, ...fields } } }
        })
    }
})(AddEquipment)
export default connect((state => {
    return ({
        loading: state.equipment.loading,
        error: state.equipment.error,
        formData: state.equipment.formData
    })
}))(EquipmentForm)