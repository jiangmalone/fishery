import React, { PureComponent } from 'react';
import { Modal, Input, Button } from 'antd';
import { Map, MouseTool } from 'react-amap';

class MapModal extends PureComponent {
    constructor() {
        super();
        this.state = {
            what: '点击下方按钮开始绘制'
        };
        this.amapEvents = {
            created: (map) => {
                AMap.plugin(['AMap.Autocomplete', 'AMap.PlaceSearch'], () => {
                    var autoOptions = {
                        input: "keyword"//使用联想输入的input的id
                    };
                    var autocomplete = new AMap.Autocomplete(autoOptions);
                    console.log(autocomplete)
                    var placeSearch = new AMap.PlaceSearch({
                        city: '',
                        map: map
                    })
                    AMap.event.addListener(autocomplete, "select", function (e) {
                        //TODO 针对选中的poi实现自己的功能
                        placeSearch.setCity(e.poi.adcode);
                        placeSearch.search(e.poi.name)
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
            onOk={this.props.onMapOk}
            onCancel={this.props.onMapCancel}
            wrapClassName={this.props.wrapClassName}
            width='80%'
            okText="确认"
            cancelText="取消">
            {/* <div id="container" style={{ width: '100%', height: '600px' }}></div> */}
            <div style={{ height: '800px' }}>
                <Map amapkey={'ae721b47f9c198651a4bfecec901aa8c'} version={'1.4.2'}
                    plugins={this.mapPlugins}
                    events={this.amapEvents} >
                    <MouseTool events={this.toolEvents} >

                    </MouseTool>
                </Map>
            </div>
            <div style={{ position: 'absolute', top: '0' }}>
                <Input id="keyword" ref="keyword" name="keyword" placeholder="请输入关键字：(选定后搜索)" onChange={(e) => { this.setState({ address: e.target.value }) }} />
                <Button onClick={() => { this.drawMarker() }}>Draw Marker</Button>
            </div>
            <div>{this.state.what}</div>
        </Modal>)
    }

}
export default MapModal