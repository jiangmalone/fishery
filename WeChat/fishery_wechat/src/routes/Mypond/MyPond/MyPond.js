import React, { PureComponent } from 'react';
import { List, InputItem, Picker, ActionSheet, ActivityIndicator } from 'antd-mobile';
import NavBar from '../../../components/NavBar';
import { createForm } from 'rc-form';
import { connect } from 'dva';
import './Mypond.less';

class MyPond extends PureComponent {

    constructor(props) {
        super(props)
        this.state = {
            edit: this.props.list.length == 0 ? false : true,
            loading: this.props.loading
        }
    }

    componentDidMount(){
        this.props.dispatch({
            type:'pond/query',
            payload: { relationId: 'wx3', page: 1, number: 99 }
        })
    }

    componentWillReceiveProps(newProps) {
        if (newProps.list.length !== this.props.list.length) {
            this.setState({
                list: newProps.list,
                edit: newProps.list.length == 0 ? false : true
            })
        }
        if (newProps.loading != this.state.loading) {
            this.setState({
                loading: newProps.loading
            })
        }
    }

    showActionSheet = (index, id) => {
        const BUTTONS = ['删除', '取消'];
        ActionSheet.showActionSheetWithOptions({
            options: BUTTONS,
            cancelButtonIndex: BUTTONS.length - 1,
            destructiveButtonIndex: BUTTONS.length - 2,
            // title: 'title',
            message: '您是否确定删除该塘口？',
            maskClosable: true,
            'data-seed': 'mypond',
            // wrapProps,
        },
            (buttonIndex) => {
                console.log(buttonIndex)
                if (buttonIndex == 0) {
                    this.deletePond(index, id)
                }
                this.setState({ clicked: BUTTONS[buttonIndex], edit: !this.state.edit });
            });
    }

    addPond = () => {
        this.props.dispatch({
            type: 'changeState', 
            payload: {
                formData: {
                    fields: {}
                },
                selectedFishes: [],
                address: ''
            }
        })
        this.props.dispatch({
            type: 'global/changeState',
            payload: {
                transitionName: 'fade'
            }
        })
        // this.props.dispatch({
        //     type: 'pond/changeState',
        //     payload: {
        //         formData: { fields: formData }
        //     }
        // })
        this.props.history.push('/addPond');
    }

    deletePond = (index, id) => {
        this.props.dispatch({
            type: 'pond/deletePond',
            payload: {
                index: index,
                id: id
            }
        })
    }

    gotoDetail = (record) => {
        let formData = {}
        console.log(record)
        for (let key in record) {
            console.log(key)
            formData[key] = {
                value: record[key] + '',
                name: key
            }
        }
        this.props.dispatch({
            type: 'pond/changeState',
            payload: {
                formData: { fields: formData },
                address: record.address,
                latitude: record.latitude,
                longitude: record.longitude
            }
        })
        this.props.dispatch({
            type: 'global/changeState',
            payload: {
                transitionName: 'left'
            }
        })
        this.props.history.push(`/addPond/${record.id}`);

    }
    render() {
        let ponds = ''
        if (this.props.list.length > 0) {
            ponds = this.props.list.map((item, index) => {
                return <div className="mypond-content" onClick={() => { this.gotoDetail(item) }} key={index} >
                    <div className="content-info-box">
                        <div className="content-title">{item.name}</div>
                        <div>
                            <span className="content-info">
                                <i className="content-info-img area-img" />
                                <span className="content-info-value">
                                    {item.area}亩
                                </span>
                            </span>
                            <span className="content-info">
                                <i className="content-info-img waterDepth-img" />
                                <span className="content-info-value">
                                    {item.depth}m
                                </span>
                            </span>
                            <span className="content-info">
                                <i className="content-info-img poolWater-img" />
                                <span className="content-info-value">
                                    {item.water_source}
                                </span>
                            </span>
                            <span className="content-info">
                                <i className="content-info-img poolThickness-img" />
                                <span className="content-info-value">
                                    {item.sediment_thickness}cm
                                </span>
                            </span>

                            <span className="content-info">
                                <i className="content-info-img density-img" />
                                <span className="content-info-value">
                                    {item.density}kg/㎡
                                </span>
                            </span>
                            <span className="content-info">
                                <i className="content-info-img poolFish-img" />
                                <span className="content-info-value">
                                    {item.fish_categorys.length > 0 ? `${item.fish_categorys[0]},${item.fish_categorys[1]}...` : ''}
                                </span>
                            </span>
                            <span className="content-info">
                                <i className="content-info-img address-img" />
                                <span className="content-info-value">
                                    {item.address}
                                </span>
                            </span>
                        </div>
                    </div>
                    {!this.state.edit && <div className="mypond-delete" onClick={(e) => { e.stopPropagation(); this.showActionSheet(index, item.id) }}>
                        <img src={require('../../../img/btn_remove.png')} />
                    </div>}
                </div>
            })
        }
        return (
            <div className="body-bac">
                <div className="nav-bar-title" >
                    <i className="back" onClick={() => {
                        history.back();
                        this.props.dispatch({
                            type: 'global/changeState',
                            payload: {
                                transitionName: 'right'
                            }
                        })
                    }}></i>
                    我的塘口
                    <i className={this.state.edit ? 'edit' : 'right-item-none'} onClick={() => { this.setState({ edit: !this.state.edit }) }}></i>
                </div>
                {this.props.list.length > 0 && <div className="mypond-bac"></div>}
                {this.props.list.length > 0 && ponds}
                {!this.state.edit && this.props.list.length > 0 && <div onClick={() => { this.setState({ edit: !this.state.edit }) }} className="addPond-btn">取消</div>}
                {this.props.list.length > 0 && <div className="btn_add" onClick={() => {
                    this.props.history.push('/addPond');
                    this.props.dispatch({
                        type: 'global/changeState',
                        payload: {
                            transitionName: 'fade'
                        }
                    })
                }}>
                </div>}
                {this.props.list.length == 0 && <div className="none-list">
                    <img src={require('../../../img/pool_error.png')} />
                    <div>您还没有添加鱼塘呢~</div>
                    <div className="btn_add1" onClick={() => { this.addPond() }}>
                    </div>
                </div>}
                <ActivityIndicator
                    toast
                    text="等待中..."
                    animating={this.state.loading}
                />
            </div>
        );
    }

}

export default connect((state) => {
    return ({
        loading: state.pond.loading,
        list: state.pond.list
    })
})(MyPond);