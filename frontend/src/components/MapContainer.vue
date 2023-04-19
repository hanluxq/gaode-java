
<template>
    <link rel="stylesheet" href="https://cache.amap.com/lbs/static/main1119.css" />
    <div id="container">

        <div id="myPageTop" class='text'>
            <table>
                <tr><label>搜索地点：</label></tr>
                <tr><input id="tipinput" /></tr>
            </table>
        </div>

        <div id="findpath" class="text">
            <table>
                <tr>
                    <td><label>起点：</label></td>
                    <td><input id="begin" /></td>
                    <td><label>终点：</label></td>
                    <td><input id="end" /></td>
                    <td><button @click="route('keyword')" class="but">导航</button></td>
                </tr>
                <tr>
                    <td><label>起点坐标：</label></td>
                    <td><input id="beginLngLat" /></td>
                    <td><label>终点坐标：</label></td>
                    <td><input id="endLngLat" /></td>
                    <td><button @click="route('lnglat')" class="but">导航</button></td>
                </tr>
            </table>
            <hr />
        </div>


        <div id="markposition" class="text">
            <table>
                <tr>
                    <td><label id="mark-tips">选择一处地点，为此处添加标记</label></td>
                    <td><button @click="defineLine" id='defineLine' class="but">绘制连线</button></td>
                    <td><button @click="deleteLine" id='deleteLine' class="but">删除连线</button></td>
                    <td><button @click="mark" id='mark' class="but">添加标记</button></td>
                    <td><button @click="unmark" id="'unmark" class="but">删除标记</button></td>
                    <td><button @click="drawline" id="drawline" class="but">顺序连线</button></td>
                    <td><button @click="reLable" id="reLabel" class="but">标点重命名</button></td>
                </tr>
            </table>
        </div>

        <div class="input-card" style="width:6rem">
            <h4>地图样式</h4>
            <div id="map-styles">
                <div class="input-item"><input type='radio' name='mapStyle' value='normal'
                        @click="setStyle('normal')"><span>标准</span></div>
                <div class="input-item"><input type='radio' name='mapStyle' value='dark'
                        @click="setStyle('dark')"><span>幻影黑</span></div>
                <div class="input-item"><input type='radio' name='mapStyle' value='light'
                        @click="setStyle('light')"><span>月光银</span></div>
                <div class="input-item"><input type='radio' name='mapStyle' value='whitesmoke'
                        @click="setStyle('whitesmoke')"><span>远山黛</span></div>
                <div class="input-item"><input type='radio' name='mapStyle' value='fresh'
                        @click="setStyle('fresh')"><span>草色青</span></div>
                <div class="input-item"><input type='radio' name='mapStyle' value='grey'
                        @click="setStyle('grey')"><span>雅士灰</span></div>
                <div class="input-item"><input type='radio' name='mapStyle' value='graffiti'
                        @click="setStyle('graffiti')"><span>涂鸦</span></div>
                <div class="input-item"><input type='radio' name='mapStyle' value='macaron'
                        @click="setStyle('macaron')"><span>马卡龙</span></div>
                <div class="input-item"><input type='radio' name='mapStyle' value='blue'
                        @click="setStyle('blue')"><span>靛青蓝</span></div>
                <div class="input-item"><input type='radio' name='mapStyle' value='darkblue'
                        @click="setStyle('darkblue')"><span>极夜蓝</span></div>
                <div class="input-item"><input type='radio' name='mapStyle' value='wine'
                        @click="setStyle('wine')"><span>酱籽</span></div>
            </div>
        </div>

    </div>
</template>
<script>
import config from '../../api-key.json';
//必须要使用密钥才能使用插件
window._AMapSecurityConfig = {
  securityJsCode: config.securityJsCode
}
import axios from 'axios';
import AMapLoader from '@amap/amap-jsapi-loader';
export default {
    name: "MapContainer",
    data() {
        return {
            that: this,
            map: null,  //地图对象
            autoOptions: {   //提示框参数
                input: 'tipinput',
            },
            auto: null,   //提示
            autoBegin: null,  //起点提示
            autoEnd: null,  //终点提示
            placeSearch: null,  //搜索地点
            geolocation: null,  //定位
            begin: { input: 'begin' }, //起点
            end: { input: 'end' },  //终点
            lnglat: null,  //标记地点
            marker: null,  //标记
            AMap: null,  //高德地图
            markers: [],  //标记数组
            selectedMarker: null,  //选中的标记
            url: 'http://localhost:3000',
            //url:'http://39.106.226.33:3000',
            mapStyle: 'normal',
            polyline: [],
            selectedPolyline: null,
            infoWindow: null,
            infoIsOpen: false,

        }
    },
    methods: {
        initMap() {
            //加载地图和标记点
            AMapLoader.load({
                key: "0906ba5b7f33785fa31b38140b207a92",             // 申请好的Web端开发者Key，首次调用 load 时必填
                version: "2.0",      // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
                plugins: ['AMap.PlaceSearch', 'AMap.AutoComplete', 'AMap.ToolBar', 'AMap.Scale', 'AMap.MapType', 'AMap.Geolocation',],       // 需要使用的的插件列表，如比例尺'AMap.Scale'等
            }).then((AMap) => {
                this.AMap = AMap;
                this.map = new AMap.Map("container", {  //设置地图容器id
                    viewMode: "3D",    //是否为3D地图模式
                    zoom: 11,           //设置地图显示的缩放级别
                });

                //添加定位控件
                {
                    this.geolocation = new AMap.Geolocation({
                        enableHighAccuracy: true,//是否使用高精度定位，默认:true
                        offset: [10, 20],       // 定位按钮的停靠位置的偏移量
                        showButton: true,        //显示定位按钮，默认：true
                        timeout: 10000,          //超过10秒后停止定位，默认：无穷大
                        buttonPosition: 'RB',    //定位按钮的停靠位置
                        zoomToAccuracy: false,   //定位成功后是否自动调整地图视野到定位点
                    });
                    this.map.addControl(this.geolocation);  //加上这句才能显示定位按钮
                    this.geolocation.getCurrentPosition(function (status) {
                        if (status == 'complete') {
                            //onComplete(result)
                        } else {
                            console.log('定位失败');
                        }
                    });
                }

                //根据提示搜索地点
                {
                    this.auto = new AMap.AutoComplete(this.autoOptions);
                    this.autoBegin = new AMap.AutoComplete(this.begin);
                    this.autoEnd = new AMap.AutoComplete(this.end);
                    this.placeSearch = new AMap.PlaceSearch({
                        map: this.map,
                    }); // 构造地点查询类
                    this.auto.on('select', this.select); // 注册监听，当选中某条记录时会触发
                }

                //初始化信息窗口
                this.infoWindow = new AMap.InfoWindow({
                    offset: new AMap.Pixel(0, -120),
                    isCustom: true,  //使用自定义窗体
                });

                //初始化折线
                {
                    //读取数据库表中的线，绘制出来
                    const Query = 'select * from polylines';
                    let that = this;
                    axios.default.request({
                        url: this.url,   //服务器端口号
                        responseType: 'json',
                        method: 'get',
                        params: { sql: Query },
                    })
                        .then(res => {
                            res = res.data;
                            //console.log(res);
                            for (let i = 0; i < res.length; i++) {
                                let path = [];
                                path.push([res[i].sta_lng, res[i].sta_lat]);
                                path.push([res[i].end_lng, res[i].end_lat]);
                                var polyline = new this.AMap.Polyline({
                                    path: path,
                                    isOutline: true,
                                    strokeColor: "#3366FF",
                                    strokeOpacity: 1,
                                    strokeWeight: 6,
                                    strokeStyle: "solid",
                                    strokeDasharray: [10, 5],
                                    showDir: true,
                                    extData: { id: res[i].id },
                                });
                                this.polyline.push(polyline);
                                this.map.add(polyline);
                                polyline.on('click', () => {
                                    this.selectedPolyline = polyline;
                                });
                                polyline.on("mouseover", function () {
                                    that.map.setDefaultCursor("pointer");
                                });
                                polyline.on("mouseout", function () {
                                    that.map.setDefaultCursor("default");
                                });
                            }
                        }).catch(error => {
                            alert(error);
                            console.log(error);
                        });
                }

                //初始化标记点
                {
                    axios.default.request({
                        url: this.url,   //服务器端口号
                        responseType: 'json',
                        method: 'get',
                        params: { sql: 'select * from points' },
                    })
                        .then(res => {
                            const message = res.data;   //必须这么写才能获取到真正的值
                            //console.log(message);
                            for (var i = 0; i < message.length; i++) {
                                var lnglat = [Number(message[i].lng), Number(message[i].lat)];
                                var marker = new AMap.Marker({
                                    position: lnglat,   //标记点位置
                                    offset: new AMap.Pixel(0, 0),  //设置标记点偏移量
                                    extData: {
                                        id: message[i].id,
                                        description: message[i].description,
                                        lng: message[i].lng,
                                        lat: message[i].lat,
                                        label: message[i].label,
                                    }
                                });
                                marker.content = message[i].description;
                                marker.setLabel({
                                    offset: new this.AMap.Pixel(20, 20),  //设置文本标注偏移量
                                    content: message[i].label, //设置文本标注内容
                                    direction: 'bottom' //设置文本标注方位
                                });
                                this.markers.push(marker);
                                marker.setMap(this.map);
                                marker.on('click', (e) => {
                                    if (this.selectedMarker != null && this.selectedMarker._opts.label.content == e.target._opts.label.content && this.infoIsOpen == true) {
                                        this.infoWindow.close();
                                        this.infoIsOpen = false;
                                    }
                                    else {
                                        this.infoIsOpen = true;
                                        this.selectedMarker = e.target;
                                        if (e.target.content == null) {
                                            e.target.content = "暂无描述";
                                        }
                                        if (e.target.images == null) {
                                            e.target.images = 'https://cdn.pixabay.com/photo/2013/05/07/08/46/paper-109277_1280.jpg';
                                        }
                                        var content = '<div style="width:400px;height:300px;padding:10px;text-align: center;background-color:#fff;opacity: 0.9;"> ' +
                                            '<div style="width:100%;height:80%;display:flex;justify-content:center;align-items:center;">' +
                                            '<img src="https://cdn.pixabay.com/photo/2013/05/07/08/46/paper-109277_1280.jpg" style="width:100%;height:100%;object-fit:cover;border-radius:5px;"/>' +
                                            '</div>' +
                                            '<div style="width:100%;height:20%;padding:10px;font-size:16px;color:#333;">' + e.target.content + '</div>' +
                                            '</div>';
                                        this.infoWindow.setContent(content);
                                        this.infoWindow.open(this.map, e.target.getPosition());
                                    }
                                });
                            }
                        }).catch(error => {
                            alert(error);
                            console.log(error);
                        });
                }
            }).catch(e => {
                console.log(e);
            })
        },
        select(e) {
            this.placeSearch.setCity(e.poi.adcode);
            this.placeSearch.search(e.poi.name); // 关键字查询
        },
        mark() {
            //标记地点
            let that = this;
            that.map.on("click", (e) => {
                // 获取鼠标单击位置的经纬度
                that.lnglat = e.lnglat;
                const { lng, lat } = e.lnglat;
                that.lng = lng;
                that.lat = lat;
            });
            let clickHandler = function () {
                that.marker = new that.AMap.Marker({
                    position: that.lnglat,
                    map: that.map
                });
                const len = that.markers.length;
                that.marker.setMap(that.map);
                that.marker.setLabel({
                    offset: new that.AMap.Pixel(0, 0),  //设置文本标注偏移量
                    //content: "第" + (parseInt(len) + 1) + "个地点", //设置文本标注内容
                    content: that.lng + ',' + that.lat, //设置文本标注内容
                    direction: 'bottom' //设置文本标注方位
                });
                that.markers.push(that.marker);
                axios.default.request({
                    url: that.url,   //服务器端口号
                    responseType: 'json',
                    method: 'post',
                    params: { sql: 'insert into points values(' + (len + 1) + ',' + that.lng + ',' + that.lat + ',' + '\'' + that.lng + ',' + that.lat + '\'' + ',\'暂无描述\'' + ',null' + ')' },
                })
                    .then(res => {
                        console.log(res);
                    }).catch(error => {
                        alert(error);
                        console.log(error);
                    });
                that.marker.on("click", (e) => {
                    if (that.selectedMarker === e.target) {
                        that.map.clearnInfoWindow();
                    }
                    that.selectedMarker = e.target;
                });
                that.map.off("click", clickHandler);
            }
            that.map.on("click", clickHandler);
        },
        unmark() {
            //删除标记
            if (this.selectedMarker) {
                const oldLabel = this.selectedMarker._opts.label.content;
                const Query = 'delete from points where label=' + '\'' + oldLabel + '\'';
                axios.default.request({
                    url: this.url,   //服务器端口号
                    responseType: 'json',
                    method: 'post',
                    params: { sql: Query },
                })
                    .then(() => {
                    }).catch(error => {
                        alert(error);
                        console.log(error);
                    });
                this.selectedMarker.setMap(null);
                this.markers.pop(this.selectedMarker);
            }
        },
        reLable() {
            //修改标记
            if (this.selectedMarker) {
                const newContent = prompt("请输入标签内容", "");
                const oldLabel = this.selectedMarker._opts.label.content;
                if (newContent !== null && newContent !== "") {
                    //console.log(this.selectedMarker._opts.label.content);
                    const Query = 'update points set label=' + '\'' + newContent + '\'' + ' where label=' + '\'' + oldLabel + '\'';
                    axios.default.request({
                        url: this.url,   //服务器端口号
                        responseType: 'json',
                        method: 'post',
                        params: { sql: Query },
                    })
                        .then(() => {
                            this.selectedMarker.setLabel({
                                content: newContent
                            });
                        }).catch(error => {
                            if (error.code === 'ERR_BAD_REQUEST') {
                                alert("标签内容不能重复");
                            }
                            else {
                                alert(error);
                            }
                            console.log(error);
                        });

                }
            }
        },
        drawline() {
            //画线
            var path = [];
            this.markers.forEach(element => {
                path.push([element.getPosition().lng, element.getPosition().lat]);
                //console.log(element.getPosition().pos);
            });
            //console.log(path);
            var polyline = new this.AMap.Polyline({
                path: path,          //设置线覆盖物路径
                isOutline: true,             //是否显示描边
                strokeColor: "#3366FF", //线颜色
                strokeOpacity: 1,       //线透明度
                strokeWeight: 6,        //线宽
                strokeStyle: "solid",   //线样式
                strokeDasharray: [10, 5], //补充线样式
                showDir: true,          //是否显示方向箭头
            });
            this.map.add(polyline);
            this.map.setFitView();
        },
        route(method) {
            //寻路功能
            let that = this;
            this.map.plugin('AMap.Driving', function () {
                var driving = new that.AMap.Driving({
                    // 驾车路线规划策略，AMap.DrivingPolicy.LEAST_TIME是最快捷模式
                    policy: that.AMap.DrivingPolicy.LEAST_TIME,
                    map: that.map,
                    panel: 'panel'
                })
                that.autoBegin.on('select', that.select); // 注册监听，当选中某条记录时会触发
                that.autoEnd.on('select', that.select); // 注册监听，当选中某条记录时会触发
                if (method === 'keyword') {
                    var points = [
                        { keyword: that.begin },
                        { keyword: that.end }
                    ]
                    driving.search(points, function (status, result) {
                        if (status === 'complete') {
                            console.log('绘制驾车路线完成')
                            console.log(result.routes)
                        } else {
                            console.log('获取驾车数据失败：' + result)
                        }
                        // 未出错时，result即是对应的路线规划方案
                    })
                } else if (method === 'lnglat') {
                    driving.search(that.startLngLat, that.endLngLat, function (status, result) {
                        if (status === 'complete') {
                            console.log('绘制驾车路线完成')
                            console.log(result.routes)
                        } else {
                            console.log('获取驾车数据失败：' + result)
                        }
                    })
                }
            })
        },
        defineLine() {
            //自定义线
            let that = this;
            let path = [];
            let clickHandler = function (e) {
                path.push([e.lnglat.lng, e.lnglat.lat]);
                if (path.length === 2) {
                    that.map.off("click", clickHandler);
                    var polyline = new that.AMap.Polyline({
                        path: path,
                        isOutline: true,
                        strokeColor: "#3366FF",
                        strokeOpacity: 1,
                        strokeWeight: 6,
                        strokeStyle: "solid",
                        strokeDasharray: [10, 5],
                        showDir: true,
                    });
                    that.polyline.push(polyline);
                    //存入数据库
                    const Query = 'insert into polylines(sta_lng,sta_lat,end_lng,end_lat) values(' + '' + path[0][0] + ',' + path[0][1] + ',' + path[1][0] + ',' + path[1][1] + ')';
                    axios.default.request({
                        url: that.url,   //服务器端口号
                        responseType: 'json',
                        method: 'post',
                        params: { sql: Query },
                    })
                        .then(() => {
                        }).catch(error => {
                            alert(error);
                            console.log(error);
                        });
                    polyline.on("mouseover", function () {
                        that.map.setDefaultCursor("pointer");
                    });
                    polyline.on("mouseout", function () {
                        that.map.setDefaultCursor("default");
                    });
                    polyline.on("click", function () {
                        that.selectedPolyline = polyline;
                    });
                    that.map.add(polyline);
                }
            };
            that.map.on("click", clickHandler);
        },
        deleteLine() {
            //删除当前选中的折线
            if (this.selectedPolyline != null) {
                this.map.remove(this.selectedPolyline);
                //从数据库中删除
                const Query = 'delete from polylines where id=' + this.selectedPolyline.getExtData().id;
                axios.default.request({
                    url: this.url,   //服务器端口号
                    responseType: 'json',
                    method: 'post',
                    params: { sql: Query },
                })
                    .then(() => {
                    }).catch(error => {
                        alert(error);
                        console.log(error);
                    });
                this.selectedPolyline = null;
            }
        },
        //设置地图样式
        setStyle(mapStyle) {
            var styleName = "amap://styles/" + mapStyle;
            this.map.setMapStyle(styleName);
        },
    },
    mounted() {
        //DOM初始化完成进行地图初始化
        this.initMap();
    }
}
</script>

<style>
#container {
    padding: 0px;
    margin: 0px;
    width: 100%;
    height: 1000px;
}

#myPageTop {
    position: absolute;
    top: 10px;
    right: 10px;
    background-color: white;
    z-index: 999;
}

#findpath {
    position: absolute;
    top: 10px;
    left: 10px;
    height: 15%;
    background-color: white;
    z-index: 999;
}

#markposition {
    background-color: white;
    position: absolute;
    bottom: 10px;
    right: 50px;
    z-index: 999;
}

#mark-tips {
    padding: 10px 20px;
    text-align: center;
    margin: 10px;
}

.text {
    background-color: #000;
    border: 1px solid #000;
    filter: alpha(opacity=30);
    opacity: 0.8;
}

.but {
    background-color: #4CAF50;
    border: none;
    color: white;
    padding: 10px 20px;
    text-align: center;
    font-size: 16px;
    margin: 10px;
    border-radius: 5px;
    cursor: pointer;
    display: inline-block;
}

.but:hover {
    background-color: #03a9f4
}

.input-card {
    position: absolute;
    bottom: 10px;
    left: 10px;
    z-index: 999;
    background-color: #ffffff;
    border: 1px solid #ffffff;
    filter: alpha(opacity=30);
    opacity: 0.8;
}

#map-styles .input-item {
    height: 2rem;
}

#map-styles .input-item span {
    display: inline-block;
    width: 4rem;
}
</style>