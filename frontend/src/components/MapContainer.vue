<template>
    <link href="https://cache.amap.com/lbs/static/main1119.css" rel="stylesheet"/>
    <div id="container">

        <div id="searchTop" class='text'>
            <table>
                <tr><label>搜索地点：</label></tr>
                <tr><input id="tipinput"/></tr>
            </table>
        </div>
        <div id="markposition" class="text">
            <table>
                <tr>
                    <td><label id="mark-tips">选择一处地点，为此处添加标记</label></td>
                    <td>
                        <button id='defineLine' class="but" @click="defineLine">{{ drawlinebutton }}</button>
                    </td>
                    <td>
                        <button id='deleteLine' class="but" @click="deleteLine">删除连线</button>
                    </td>
                    <td>
                        <button id='mark' class="but" @click="mark">添加标记</button>
                    </td>
                    <td>
                        <button id="'unmark" class="but" @click="unmark">删除标记</button>
                    </td>
                    <td>
                        <button id="drawline" class="but" @click="drawline">顺序连线</button>
                    </td>
                    <td>
                        <button id="reLabel" class="but" @click="reLable">标点重命名</button>
                    </td>
                </tr>
            </table>
        </div>

        <div class="input-card" style="width:6rem">
            <h4>地图样式</h4>
            <div id="map-styles">
                <div class="input-item"><input name='mapStyle' type='radio' value='normal'
                                               @click="setStyle('normal')"><span>标准</span></div>
                <div class="input-item"><input name='mapStyle' type='radio' value='dark'
                                               @click="setStyle('dark')"><span>幻影黑</span></div>
                <div class="input-item"><input name='mapStyle' type='radio' value='light'
                                               @click="setStyle('light')"><span>月光银</span></div>
                <div class="input-item"><input name='mapStyle' type='radio' value='whitesmoke'
                                               @click="setStyle('whitesmoke')"><span>远山黛</span></div>
                <div class="input-item"><input name='mapStyle' type='radio' value='fresh'
                                               @click="setStyle('fresh')"><span>草色青</span></div>
                <div class="input-item"><input name='mapStyle' type='radio' value='grey'
                                               @click="setStyle('grey')"><span>雅士灰</span></div>
                <div class="input-item"><input name='mapStyle' type='radio' value='graffiti'
                                               @click="setStyle('graffiti')"><span>涂鸦</span></div>
                <div class="input-item"><input name='mapStyle' type='radio' value='macaron'
                                               @click="setStyle('macaron')"><span>马卡龙</span></div>
                <div class="input-item"><input name='mapStyle' type='radio' value='blue'
                                               @click="setStyle('blue')"><span>靛青蓝</span></div>
                <div class="input-item"><input name='mapStyle' type='radio' value='darkblue'
                                               @click="setStyle('darkblue')"><span>极夜蓝</span></div>
                <div class="input-item"><input name='mapStyle' type='radio' value='wine'
                                               @click="setStyle('wine')"><span>酱籽</span></div>
            </div>
        </div>

        <div id="window" ref="windowInfo" style="display:none">
            <div id="imageContainer">
                <table>
                    <tr>
                        <img id='image' ref='imageInput' :src=windowInfo.imageUrl />
                    </tr>
                    <tr>
                        <input ref="fileInput" accept="image/*" class="image" type="file"
                               @change="updateSelectedFiles"/>
                        <button @click="uploadFiles(this.selectedMarker._opts.extData.id,this.url)">上传</button>
                    </tr>
                </table>
            </div>
            <div id="textContainer">
                <textarea id="windowTitle" :value="
                `名称:${windowInfo.name},等级:${windowInfo.level==null?'':windowInfo.level+'|'}${windowInfo.levelChar},时期:${windowInfo.epoch1==windowInfo.epoch2?windowInfo.epoch2:windowInfo.epoch1+'|'+windowInfo.epoch2},类别:${windowInfo.class==null?'无':windowInfo.class},`" border="none" outline="none" readonly type="text"/>
                <textarea id="windowText" :value="`描述：${windowInfo.text}`" border="none" outline="none" readonly type="text"/>
                <p>
                    <a>
                        <button @click="enableEdit">编辑</button>
                    </a>
                    <a>
                        <button @click="saveEdit">保存</button>
                    </a>
                </p>

            </div>
        </div>

      <div class="checkbox-list">
        <label >
          <input id="checkbox1" type="checkbox" value="国家级爱国主义教育基地" >
          北京市国家级爱国主义教育基地
        </label>
        <label >
          <input id="checkbox2" type="checkbox" value="31处中国共产党早期北京革命活动旧址" >
          中国共产党早期北京革命活动旧址
        </label>
        <label >
          <input id="checkbox3" type="checkbox" value="102处北京市文旅局红色景点" >
          北京市文旅局红色景点
        </label>
        <label >
          <input id="checkbox4" type="checkbox" value="北京市级爱国主义教育基地" >
          北京市级爱国主义教育基地
        </label>
        <label>
          <button @click="this.requestPoints()">确定</button>
        </label>
      </div>

    </div>
</template>
<script>
import config from '../../api-key.json';
import axios from 'axios';
import AMapLoader from '@amap/amap-jsapi-loader';
import {ref} from 'vue';
import defaultImage from '/src/components/default.jpg';
//必须要使用密钥才能使用插件
window._AMapSecurityConfig = {
    securityJsCode: config.securityJsCode
}

export default {
    name: "MapContainer",
    data() {
        return {
            that: this,
            scope: 'fenjifenlei',
            map: null,  //地图对象
            autoOptions: {   //提示框参数
                input: 'tipinput',
            },
            drawlinebutton: '连线标点',
            auto: null,   //提示
            autoBegin: null,  //起点提示
            autoEnd: null,  //终点提示
            placeSearch: null,  //搜索地点
            geolocation: null,  //定位
            begin: {input: 'begin'}, //起点
            end: {input: 'end'},  //终点
            lnglat: null,  //标记地点
            marker: null,  //标记
            AMap: null,  //高德地图
            markers: [],  //标记数组
            selectedMarker: null,  //选中的标记
            url: 'http://localhost:3000',
            //url:'http://8.130.93.92:3000',
            mapStyle: 'normal',
            polyline: [],
            selectedPolyline: null,
            infoWindow: null,
            infoIsOpen: false,
            flag: '',
            lines: {
                path: [],
            },
            windowInfo: {
                name: '无',
                level: '无',
                levelChar: '无',
                class: '无',
                batch: '无',
                epoch1: '无',
                epoch2: '无',
                text: '暂无描述~',
            },
          selectedOptions:[],
          marker_image: {
            marker_A: require('./marker_A.png'),
            marker_B: require('./marker_B.png'),
            marker_C: require('./marker_C.png'),
            marker_D: require('./marker_D.png'),
            marker_E: require('./marker_E.png'),
            default: require('./default.jpg'),
          }
        }
    },
    methods: {
        initMap() {
            //加载地图和标记点
            AMapLoader.load({
                key: "0906ba5b7f33785fa31b38140b207a92",             // 申请好的Web端开发者Key，首次调用 load 时必填
                version: "2.0",      // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
                plugins: ['AMap.PlaceSearch', 'AMap.AutoComplete', 'AMap.ToolBar', 'AMap.Scale', 'AMap.MapType', 'AMap.Geolocation', 'AMap.HawkEye'],       // 需要使用的的插件列表，如比例尺'AMap.Scale'等
            }).then((AMap) => {
                this.AMap = AMap;
                this.map = new AMap.Map("container", {  //设置地图容器id
                    viewMode: "3D",    //是否为3D地图模式
                    zoom: 11,           //设置地图显示的缩放级别
                });

                // //添加定位控件
                // {
                //     this.geolocation = new AMap.Geolocation({
                //         enableHighAccuracy: true,//是否使用高精度定位，默认:true
                //         offset: [10, 20],       // 定位按钮的停靠位置的偏移量
                //         showButton: true,        //显示定位按钮，默认：true
                //         timeout: 10000,          //超过10秒后停止定位，默认：无穷大
                //         buttonPosition: 'RB',    //定位按钮的停靠位置
                //         zoomToAccuracy: false,   //定位成功后是否自动调整地图视野到定位点
                //     });
                //     this.map.addControl(this.geolocation);  //加上这句才能显示定位按钮
                //     this.geolocation.getCurrentPosition(function (status) {
                //         if (status == 'complete') {
                //             //onComplete(result)
                //         } else {
                //             console.log('定位失败');
                //         }
                //     });
                //     this.map.addControl(new AMap.Scale());
                //     this.map.addControl(new AMap.MapType());
                // }

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
                this.initializePolylines();
                //this.initializeMarkers();
            }).catch(e => {
                console.log(e);
            })
        },
        initializeMarkers(cols) {
          this.clearAllMarkers();
              axios.get(this.url + "/selectMarker/selectByKeyword",{params:{cols:encodeURIComponent(cols)}}).then(({ data }) => {
                // 遍历所有标记数据
                const markers = data.map((markerData) => {
                  //console.log(markerData);
                  let color;
                  let marker_image;
                  switch(markerData.levelChar) {
                    case "E":
                      marker_image = this.marker_image.marker_E;
                      break;
                    case "D":
                      marker_image = this.marker_image.marker_D;
                      break;
                    case "C":
                      marker_image = this.marker_image.marker_C;
                      break;
                    case "B":
                      marker_image = this.marker_image.marker_B;
                      break;
                    case "A":
                      marker_image = this.marker_image.marker_A;
                      break;
                    default:
                      color = "#FFC0CB"
                  }
                  const icon = new this.AMap.Icon({
                    size: new this.AMap.Size(30, 43),
                    image: marker_image,
                    imageOffset: new this.AMap.Pixel(0, 0),
                    imageSize: new this.AMap.Size(30, 43)
                  });
                    // 将标记数据转换为 AMap.Marker 对象
                    const marker = new this.AMap.Marker({
                        position: [markerData.lng_GCJ02, markerData.lat_GCJ02],
                        offset: new this.AMap.Pixel(0, 0),
                        extData: { ...markerData },
                        title: markerData.name,
                        icon: icon,
                        label: {
                            offset: new this.AMap.Pixel(0, 0),
                            content: markerData.name,
                            direction: "bottom",
                        },
                      zoom:13,
                    });

                    marker.setMap(this.map);
                    marker.on("click", (e) => this.addMarkerListener(e));
                    return marker;
                });

                // 将新创建的标记对象合并到原有的标记列表中
                this.markers = [...this.markers, ...markers];
            }).catch((error) => {
                alert(error);
                console.log(error);
            });
        },
      // 定义方法清除所有标记
      clearAllMarkers() {
        const markers = this.map.getAllOverlays("marker");
        markers.forEach((marker) => this.map.remove(marker));
        this.markers = [];
      },
        initializePolylines() {
            axios.get(this.url+"/selectLine").then(({data}) => {
                //console.log(data);
                const polylines = data.map(({id, sta_lng, sta_lat, end_lng, end_lat}) => {
                    const path = [[sta_lng, sta_lat], [end_lng, end_lat]];
                    const polyline = new this.AMap.Polyline({
                        path,
                        isOutline: true,
                        strokeColor: "#3366FF",
                        strokeOpacity: 1,
                        strokeWeight: 6,
                        strokeStyle: "solid",
                        strokeDasharray: [10, 5],
                        showDir: true,
                        extData: {id}
                    });
                    this.map.add(polyline);
                    this.addPolylineEventListener(polyline);
                    return polyline;
                });

                this.polyline = [...this.polyline, ...polylines];
            }).catch(error => {
                alert(error);
                console.log(error);
            });
        },
        addMarkerListener(e) {
            if (this.flag === 'line') {   //连线状态
                this.lines.path.push([e.target._opts.extData.lng_GCJ02, e.target._opts.extData.lat_GCJ02]);
                //console.log(e.target._opts.extData.lat_GCJ02);
                var polyline = new this.AMap.Polyline({
                    path: this.lines.path,
                    isOutline: true,
                    strokeColor: "#3366FF",
                    strokeOpacity: 1,
                    strokeWeight: 6,
                    strokeStyle: "solid",
                    strokeDasharray: [10, 6],
                    showDir: true,
                });
                console.log(polyline);
                this.map.add(polyline);
                this.addPolylineEventListener(polyline);
            } else {   //查看信息窗体状态
                this.$refs.windowInfo.style.display = 'block';
                const {target: {image, _opts: {extData}}} = e;
                const img = e.target._opts.extData.images;
                let url = null;
                console.log(img);
                //if (img == null) url = 'https://cdn.pixabay.com/photo/2013/05/07/08/46/paper-109277_1280.jpg';
                if (img == null) url = defaultImage;
                else {
                    const binaryString = window.atob(img);
                    const len = binaryString.length;
                    const bytes = new Uint8Array(len);
                    for (let i = 0; i < len; ++i) {
                        bytes[i] = binaryString.charCodeAt(i);
                    }
                    const blob = new Blob([bytes], {type: "image/jpeg"});
                    url = URL.createObjectURL(blob);
                }
                if (this.selectedMarker && this.selectedMarker._opts.title === e.target._opts.title && this.infoIsOpen) {
                    this.infoWindow.close();
                    this.infoIsOpen = false;
                } else {
                    this.infoIsOpen = true;
                    this.selectedMarker = e.target;
                    this.windowInfo.imageUrl = url;
                    this.windowInfo.text = extData.description;
                    this.windowInfo.name = extData.name;
                    this.windowInfo.batch = extData.batch;
                    this.windowInfo.class = extData.class;
                    this.windowInfo.epoch1 = extData.epoch1;
                    this.windowInfo.epoch2 = extData.epoch2;
                    this.windowInfo.level = extData.level;
                    this.windowInfo.levelChar = extData.levelChar;
                    this.infoWindow.setContent(this.$refs.windowInfo);
                    this.infoWindow.open(this.map, e.target.getPosition());
                }
            }
        },
        // initializePolylines() {
        //     const query = 'select * from polylines';
        //
        //     axios.get(this.url, {
        //         params: {sql: query}
        //     }).then(({data}) => {
        //         const polylines = data.map(({id, sta_lng, sta_lat, end_lng, end_lat}) => {
        //             const path = [[sta_lng, sta_lat], [end_lng, end_lat]];
        //             const polyline = new this.AMap.Polyline({
        //                 path,
        //                 isOutline: true,
        //                 strokeColor: "#3366FF",
        //                 strokeOpacity: 1,
        //                 strokeWeight: 6,
        //                 strokeStyle: "solid",
        //                 strokeDasharray: [10, 5],
        //                 showDir: true,
        //                 extData: {id}
        //             });
        //             this.map.add(polyline);
        //             this.addPolylineEventListener(polyline);
        //             return polyline;
        //         });
        //
        //         this.polyline = [...this.polyline, ...polylines];
        //     }).catch(error => {
        //         alert(error);
        //         console.log(error);
        //     });
        // },
        addPolylineEventListener(polyline){
            polyline.on('click', () => {
                this.selectedPolyline = polyline;
            });

            polyline.on("mouseover", () => {
                this.map.setDefaultCursor("pointer");
            });

            polyline.on("mouseout", () => {
                this.map.setDefaultCursor("default");
            });
        },
        select(e) {
            this.placeSearch.setCity(e.poi.adcode);
            this.placeSearch.search(e.poi.name); // 关键字查询
        },
        mark() {
            //标记地点
            let that = this;
            const len = this.markers.length;
            const id = this.markers[len - 1]._opts.extData.id + 1;
            //console.log(id);
            that.map.on("click", (e) => {
                // 获取鼠标单击位置的经纬度
                that.lnglat = e.lnglat;
                const {lng, lat} = e.lnglat;
                that.lng = lng;
                that.lat = lat;
            });
            let clickHandler = function () {
                that.marker = new that.AMap.Marker({
                    position: that.lnglat,
                    map: that.map
                });

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
                    params: {sql: 'insert into points values(' + id + ',' + that.lng + ',' + that.lat + ',' + '\'' + that.lng + ',' + that.lat + '\'' + ',\'暂无描述\'' + ',null' + ')'},
                })
                    .then(res => {
                        console.log(res);
                    }).catch(error => {
                    alert(error);
                    console.log(error);
                });
                that.marker.on('click', e => this.markerClick(e));
                // that.marker.on("click", (e) => {
                //     if (this.flag == 'line') {
                //       this.lines.path.push([e.lnglat.lng, e.lnglat.lat]);
                //       var polyline = new that.AMap.Polyline({
                //           path: that.lines.path,
                //           isOutline: true,
                //           strokeColor: "#3366FF",
                //           strokeOpacity: 1,
                //           strokeWeight: 6,
                //           strokeStyle: "solid",
                //           strokeDasharray: [10, 5],
                //           showDir: true,
                //       });
                //       that.map.add(polyline);
                //     } else {
                //         if (that.selectedMarker === e.target) {
                //             that.map.clearnInfoWindow();
                //         }
                //         that.selectedMarker = e.target;
                //    }
                // });
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
                    params: {sql: Query},
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
                        params: {sql: Query},
                    })
                        .then(() => {
                            this.selectedMarker.setLabel({
                                content: newContent
                            });
                        }).catch(error => {
                        if (error.code === 'ERR_BAD_REQUEST') {
                            alert("标签内容不能重复");
                        } else {
                            alert(error);
                        }
                        console.log(error);
                    });

                }
            }
        },
        drawline() {
            //自动按顺序连接标记点
            var path = [];
            this.markers.forEach(element => {
                path.push([element.getPosition().lng, element.getPosition().lat]);
            });
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
        defineLine() {
            //绘制两点之间的线
            let that = this;
            if (this.flag == 'line') {
                this.flag = 'none';
                this.drawlinebutton = '连线标点';
                //处理path，将path中存储的路径保存到数据库中
                for (let i = 0; i < this.lines.path.length - 1; i++) {
                    const Query = 'insert into polylines(sta_lng,sta_lat,end_lng,end_lat) values(' + '' + this.lines.path[i][0] + ',' + this.lines.path[i][1] + ',' + this.lines.path[i + 1][0] + ',' + this.lines.path[i + 1][1] + ')';
                    //console.log(Query);
                    axios.default.request({
                        url: that.url,   //服务器端口号
                        responseType: 'json',
                        method: 'post',
                        params: {sql: Query},
                    })
                        .then(() => {
                        }).catch(error => {
                        alert(error);
                        console.log(error);
                    });
                }

            } else {
                this.flag = 'line';
                this.drawlinebutton = '取消连线';
                this.lines.path = [];
            }

            //点击地图，获取坐标，连接坐标
            // let clickHandler = function (e) {
            //   path.push([e.lnglat.lng, e.lnglat.lat]);
            //   console.log(path)
            //   if (path.length === 2) {
            //     that.map.off("click", clickHandler);
            //     var polyline = new that.AMap.Polyline({
            //       path: path,
            //       isOutline: true,
            //       strokeColor: "#3366FF",
            //       strokeOpacity: 1,
            //       strokeWeight: 6,
            //       strokeStyle: "solid",
            //       strokeDasharray: [10, 5],
            //       showDir: true,
            //     });
            //     that.polyline.push(polyline);
            //     //存入数据库
            //     const Query = 'insert into polylines(sta_lng,sta_lat,end_lng,end_lat) values(' + '' + path[0][0] + ',' + path[0][1] + ',' + path[1][0] + ',' + path[1][1] + ')';
            //     axios.default.request({
            //       url: that.url,   //服务器端口号
            //       responseType: 'json',
            //       method: 'post',
            //       params: {sql: Query},
            //     })
            //         .then(() => {
            //         }).catch(error => {
            //       alert(error);
            //       console.log(error);
            //     });
            //     polyline.on("mouseover", function () {
            //       that.map.setDefaultCursor("pointer");
            //     });
            //     polyline.on("mouseout", function () {
            //       that.map.setDefaultCursor("default");
            //     });
            //     polyline.on("click", function () {
            //       that.selectedPolyline = polyline;
            //     });
            //     that.map.add(polyline);
            //   }
            // };
            // that.map.on("click", clickHandler);
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
                    params: {sql: Query},
                })
                    .then(() => {
                    }).catch(error => {
                    alert(error);
                    console.log(error);
                });
                this.selectedPolyline = null;
            }
        },
        setStyle(mapStyle) {
            var styleName = "amap://styles/" + mapStyle;
            this.map.setMapStyle(styleName);
        },
        enableEdit() {
            var input = document.getElementById("windowText");
            //取消只读状态
            input.removeAttribute("readonly");
        },
        saveEdit() {
            var input = document.getElementById("windowText");
            //设置只读状态
            input.setAttribute("readonly", "readonly");
            //获取输入框的值
            var text = input.value;
            //console.log(this.selectedMarker)
            //保存到数据库中
            const Query = 'update points set description="' + text + '" where id=' + this.selectedMarker._opts.extData.id;
            //console.log(Query)
            axios.default.request({
                url: this.url,   //服务器端口号
                method: 'post',
                params: {sql: Query},
            })
                .then(() => {
                }).catch(error => {
                alert(error);
                console.log(error);
            });
        },
        getPoints(){
          //console.log(this.selectedOptions);
          this.initializeMarkers();
        },
        requestPoints(){
          const cols = [];
          const checkbox1 = document.getElementById('checkbox1');
          if(checkbox1.checked) cols.push("comment1");
          const checkbox2 = document.getElementById('checkbox2');
          if(checkbox2.checked) cols.push("comment2");
          const checkbox3 = document.getElementById('checkbox3');
          if(checkbox3.checked) cols.push("comment3");
          const checkbox4 = document.getElementById('checkbox4');
          if(checkbox4.checked) cols.push("comment4");
          if(cols.length==0)
            this.clearAllMarkers()
          else this.initializeMarkers(cols);
        }
    },
    mounted() {
        //DOM初始化完成进行地图初始化
        this.initMap();
    },
    setup() {
        const selectedFiles = ref([]);

        function updateSelectedFiles(event) {
            const files = event.target.files;
            selectedFiles.value = files;
        }

        async function uploadFiles(id, url) {
            const files = selectedFiles.value;
            console.log('上传以下文件：', files);
            for (const file of files) {
                const fileReader = new FileReader();
                const binaryData = await new Promise(resolve => {
                    fileReader.onload = event => {
                        resolve(event.target.result);
                    };
                    fileReader.readAsArrayBuffer(file);
                });
                const formData = new FormData();
                formData.append('file', new Blob([binaryData], {type: file.type}), file.name)
                const response = await axios.post(url + '/uploadImages', formData, {
                    responseType: 'json',
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    },
                    params: {
                        id: id,
                    }
                });
                console.log('上传成功：', response.data);
            }
            alert('文件上传成功！');
        }

        return {
            updateSelectedFiles,
            uploadFiles
        };
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

#searchTop {
    position: absolute;
    top: 10px;
    left: 10px;
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
    bottom: 20%;
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

#window {
    width: 400px;
    height: 460px;
    padding: 10px;
    text-align: center;
    background-color: #fff;
    opacity: 0.9;
    z-index: 999;
}

#imageContainer {
    width: 100%;
    height: 60%;
    display: flex;
    justify-content: center;
    align-items: center;
}

#imageContainer img {
    width: 300px;
    height: 200px;
    object-fit: cover;
    border-radius: 5px;
}

#textContainer {
    width: 100%;
    height: 40%;
    padding: 10px;
    font-size: 16px;
    color: #333;
    align-items: center;
}

#textContainer textarea {
    width: 80%;
    height: 40px;
    border: none;
    outline: none;
    resize: none;
    font-size: 16px;
    color: #333;
    padding: 10px;
    align-items: center;
}

#textContainer button {
    width: 30%;
    height: 10%;
    font-size: 16px;
    align-items: center;
    margin: 10px;
}

.checkbox-list {
  display: flex;
  position: fixed;
  top: 0;
  left: 40%;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  margin: auto;
  max-width: 500px;
  z-index: 999;
  background-color: rgba(255, 255, 255, 0.8);
}

</style>