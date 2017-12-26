import React from 'react';
import './addPond.less';
import { List } from 'antd-mobile';
import NavBar from '../../../components/NavBar';
const Item = List.Item;
const Brief = Item.Brief;

function AddPond() {
  return (
    <div>
      <NavBar title={"新增塘口"}/>
    </div>
  );
}


export default AddPond;
