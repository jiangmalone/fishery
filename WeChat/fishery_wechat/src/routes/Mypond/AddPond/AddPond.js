import React from 'react';
import './addPond.less';
import { List, InputItem, Picker } from 'antd-mobile';
import NavBar from '../../../components/NavBar';
import { createForm } from 'rc-form';
const Item = List.Item;
const Brief = Item.Brief;

function AddPond(props) {
  const { getFieldProps, getFieldError } = props.form;
  const district = [{ label: '淡水', value: '淡水' }, { label: '盐水', value: '盐水' }, { label: '淡盐水', value: '淡盐水' }]
  return (
    <form className="body-bac">
      <NavBar title={"添加塘口"} />
      <List className="addPond-list">
        <InputItem
          {...getFieldProps('account', {
            // initialValue: 'little ant',
            rules: [
              { required: true, message: '请输入塘口名称' }
            ],
          }) }
          clear
          className="addpond-input"
          error={!!getFieldError('account')}
          placeholder="请输入塘口名称"
        >塘口名称</InputItem>
        <InputItem
          {...getFieldProps('account') }
          clear
          className="addpond-input"
          labelNumber='5'
          error={!!getFieldError('account')}
          placeholder="请输入塘口面积"
        >面积(亩)</InputItem>
        <InputItem
          {...getFieldProps('account') }
          clear
          className="addpond-input"
          labelNumber='5'
          error={!!getFieldError('account')}
          placeholder="请输入塘口深度"
        >深度(m)</InputItem>
        <InputItem
          {...getFieldProps('account') }
          clear
          className="addpond-input"
          labelNumber='5'
          error={!!getFieldError('account')}
          placeholder="请输入底泥厚度"
        >底泥厚度(m)</InputItem>
        <Picker data={district} cols={1} {...getFieldProps('district3') } className="forss">
          <List.Item arrow="horizontal">池塘水源</List.Item>
        </Picker>
        <Item arrow="horizontal" extra={<span>鲤鱼，鲫鱼鲤鱼，鲫鱼鲤鱼，鲫鱼鲤鱼，鲫鱼鲤鱼，鲫鱼鲤鱼，鲫鱼鲤鱼，鲫鱼鲤鱼，鲫鱼</span>} onClick={() => { }}>
          品种
        </Item>
        <Item extra={<img src={require('../../../img/earth.png')} />} onClick={() => { }}>
          塘口位置
        </Item>
      </List>
      <div className="addPond-btn">确认提交</div>
    </form>
  );
}

const AddPondForm = createForm()(AddPond);
export default AddPondForm;
