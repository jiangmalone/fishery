import React, { PureComponent } from 'react';
import { Modal, Input, Button, AutoComplete,message } from 'antd';
import { Map, MouseTool } from 'react-amap';

class MapModal extends PureComponent {
    constructor() {
        super();
        this.state = {
            address:''
        };
        this.amapEvents = {
            created: (map) => {
                console.log(map)
                AMap.plugin(['AMap.Autocomplete', 'AMap.PlaceSearch'], () => {
                    var autoOptions = {
                        input: "keyword"//使用联想输入的input的id
                    };
                    var autocomplete = new AMap.Autocomplete(autoOptions);
                    console.log(autocomplete)

                    var placeSearch = new AMap.PlaceSearch({
                        map: map
                    })
                    AMap.event.addListener(autocomplete, "select",  (e)=> {
                        //TODO 针对选中的poi实现自己的功能
                        placeSearch.setCity(e.poi.adcode);
                        placeSearch.search(e.poi.name);
                        if(e.poi.location) {
                            this.setState({
                                address:e.poi
                            })
                        } else {
                            message.error('您输入的地点不够准确请重新输入', 1)
                        }
                    });
                })
                console.log('高德地图 Map 实例创建成功；如果你要亲自对实例进行操作，可以从这里开始。比如：');
            }
        };
        this.mapPlugins = ['ToolBar'];
        this.toolEvents = {
            created: (tool) => {
                console.log(tool)
                this.tool = tool;
            },
            draw: ({ obj }) => {
                this.drawWhat(obj);
            }
        }
    }
    drawWhat(obj) {
        let text = '';
        switch (obj.CLASS_NAME) {
            case 'AMap.Marker':
                text = `你绘制了一个标记，坐标位置是 {${obj.getPosition()}}`;
                break;
            case 'AMap.Polygon':
                text = `你绘制了一个多边形，有${obj.getPath().length}个端点`;
                break;
            case 'AMap.Circle':
                text = `你绘制了一个圆形，圆心位置为{${obj.getCenter()}}`;
                break;
            default:
                text = '';
        }
        this.setState({
            what: text
        });
    }

    drawMarker() {
        if (this.tool) {
            this.tool.marker();
            this.setState({
                what: '准备绘制坐标点'
            });
        }
    }
    render() {
        return (<Modal visible={this.props.visible}
            onOk={()=>this.props.onMapOk(this.state.address)}
            onCancel={this.props.onMapCancel}
            wrapClassName={this.props.wrapClassName}
            width='70%'
            okText="确认"
            cancelText="取消">
            <div style={{ height: '500px' }}>
                <Map amapkey={'ae721b47f9c198651a4bfecec901aa8c'} version={'1.4.2'}
                    plugins={this.mapPlugins}
                    events={this.amapEvents} >
                    <MouseTool events={this.toolEvents} >
                    </MouseTool>
                </Map>
            </div>
            <div style={{ position: 'absolute', top: '0' }}>
                <Input id="keyword" ref="keyword" name="keyword" style={{width:'300px'}} placeholder="请输入关键字：(选定后搜索)" />
            </div>
        </Modal>)
    }

}
export default MapModal