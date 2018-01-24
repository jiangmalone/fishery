import React, { PureComponent } from 'react';
import { Map, MouseTool } from 'react-amap';
import { Input } from 'antd-mobile';
import NavBar from '../../../components/NavBar';
import './addFish.less'
import { connect } from 'dva';

class MapModal extends PureComponent {
    constructor(props) {
        super(props);
        this.state = {
            address: {
                formattedAddress: this.props.address,
                position: {
                    lng: this.props.longitude,
                    lat: this.props.latitude
                },
            }
        };
        this.amapEvents = {
            created: (map) => {
                let geolocation, autocomplete, placeSearch
                AMap.plugin(['AMap.Geolocation', 'AMap.Autocomplete', 'AMap.PlaceSearch'], () => {
                    geolocation = new AMap.Geolocation({
                        enableHighAccuracy: true,//是否使用高精度定位，默认:true
                        timeout: 10000,          //超过10秒后停止定位，默认：无穷大
                        maximumAge: 0,           //定位结果缓存0毫秒，默认：0
                        convert: true,           //自动偏移坐标，偏移后的坐标为高德坐标，默认：true
                        showButton: true,        //显示定位按钮，默认：true
                        buttonPosition: 'LB',    //定位按钮停靠位置，默认：'LB'，左下角
                        buttonOffset: new AMap.Pixel(10, 20),//定位按钮与设置的停靠位置的偏移量，默认：Pixel(10, 20)
                        showMarker: true,        //定位成功后在定位到的位置显示点标记，默认：true
                        showCircle: true,        //定位成功后用圆圈表示定位精度范围，默认：true
                        panToLocation: true,     //定位成功后将定位到的位置作为地图中心点，默认：true
                        zoomToAccuracy: true      //定位成功后调整地图视野范围使定位位置及精度范围视野内可见，默认：false
                    });
                    var autoOptions = {
                        input: "keyword"//使用联想输入的input的id
                    };
                    autocomplete = new AMap.Autocomplete(autoOptions);
                    placeSearch = new AMap.PlaceSearch({
                        map: map
                    })
                })

                map.addControl(geolocation);
                geolocation.getCurrentPosition();
                AMap.event.addListener(geolocation, 'complete', (geo) => {
                    this.setState({
                        address: geo
                    })
                });//返回定位信息
                AMap.event.addListener(geolocation, 'error', (err) => {
                    console.log(err)
                });      //返回定位出错信息
                AMap.event.addListener(autocomplete, "select", (e) => {
                    //TODO 针对选中的poi实现自己的功能
                    placeSearch.setCity(e.poi.adcode);
                    placeSearch.search(e.poi.name);
                    this.setState({
                        address: {
                            formattedAddress:e.poi.district+e.poi.address+e.poi.name,
                            position:e.poi.location,
                        }
                    })
                });
            }
        };
    }

    saveAddress = () => {
        this.props.dispatch({
            type: 'pond/changeState',
            payload: {
                address: this.state.address.formattedAddress,
                longitude: this.state.address.position.lng,
                latitude: this.state.address.position.lat
            }
        })
        history.back()
    }

    render() {
        return (
            <div style={{ width: '100%', height: '100%' }}>
                <NavBar title={"我的地图"} />
                <Map amapkey={'ae721b47f9c198651a4bfecec901aa8c'} version={'1.4.2'}
                    events={this.amapEvents} >
                </Map>
                <div style={{ position: 'absolute', top: '0.88rem', zIndex: '1000', background: 'rgba(0,0,0,0.5)', color: '#fff', width: '100%' }}>
                    <textarea id="keyword" ref="keyword" name="keyword" style={{ padding: '.2rem 0', marginLeft: '.1rem', verticalAlign: 'middle', background: 'none', border: 'none', display: 'inline-block', width: '6rem', color: '#FFF' }} placeholder={`定位地址：${this.state.address.formattedAddress ? this.state.address.formattedAddress : '无'} `} />
                    <div style={{ width: '1rem', marginLeft: '.1rem', height: '.6rem', display: 'inline-block', background: '#fff', verticalAlign: 'middle', borderRadius: '.1rem', color: '#1f1f1f', lineHeight: '.6rem', textAlign: 'center' }} onClick={() => { this.saveAddress() }}>确定</div>
                </div>
            </div>
        )
    }
}
export default connect((state => {
    return ({
        address: state.pond.address,
        longitude: state.pond.longitude,
        latitude: state.pond.latitude
    })
}))(MapModal)