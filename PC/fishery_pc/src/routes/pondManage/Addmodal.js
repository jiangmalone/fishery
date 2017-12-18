import React from 'react';
import { connect } from 'dva';
import { Form, Modal, Input, Select } from 'antd';

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
 function AddModal ({ visible, form, onOk, onCancel, wrapClassName }) {
    const children = [];
    const { getFieldDecorator } = form;
    for (let i = 10; i < 36; i++) {
        children.push(<Option key={i.toString(36) + i}>{i.toString(36) + i}</Option>);
    }
    return <Modal title="新增塘口"
        visible={visible}
        onOk={onOk}
        onCancel={onCancel}
        wrapClassName={wrapClassName}
        okText="确认"
        cancelText="取消">
        <Form style={{ width: '100%' }}>
            <FormItem label="会员卡号" {...formItemLayout} style={{ width: '100%' }}>
                <span>南京鱼儿欢欢有限公司</span>
            </FormItem>
            <FormItem label="塘口名称" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('pondName', {
                    rules: [
                        { required: true, message: '请填写塘口名称' },
                    ],
                })(<Input style={{ width: 200 }} />)}
            </FormItem>
            <FormItem label="面积" {...formItemLayout} style={{ width: '100%' }}>
                <Input style={{ width: 200 }} addonAfter="亩" />
            </FormItem>
            <FormItem label="塘口位置" {...formItemLayout} style={{ width: '100%' }}>
                <Input style={{ width: 180 }} />&nbsp;
                <i className="iconfont icon-address" style={{fontSize:'20px'}}></i>
            </FormItem>
            <FormItem label="深度" {...formItemLayout} style={{ width: '100%' }}>
                <Input style={{ width: 200 }} addonAfter="m" />
            </FormItem>
            <FormItem label="养殖品种" {...formItemLayout} style={{ width: '100%' }}>
                <Select
                    mode="tags"
                    placeholder="Please select"
                    defaultValue={['a10', 'c12']}
                    style={{ width: '200px' }}
                >
                    {children}
                </Select>
            </FormItem>
            <FormItem label="池塘水源" {...formItemLayout} style={{ width: '100%' }}>
                <Input style={{ width: 200 }} />
            </FormItem>
            <FormItem label="底泥厚度" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('cardNo', {
                    rules: [
                        { type: 'float', message: '请填写塘口名称' },
                    ],
                })(<Input style={{ width: 200 }} addonAfter="cm" />)}
            </FormItem>
            <FormItem label="塘口密度" {...formItemLayout} style={{ width: '100%' }}>
                {getFieldDecorator('cardNo', {
                    rules: [
                        { type: 'float', message: '请填写塘口名称' },
                    ],
                })(<Input style={{ width: 200 }} addonAfter="kg/㎡" />)}
            </FormItem>
        </Form>
    </Modal >
}

export default Form.create()(AddModal)