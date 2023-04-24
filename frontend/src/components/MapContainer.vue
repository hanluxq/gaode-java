<template>
  <link href="https://cache.amap.com/lbs/static/main1119.css" rel="stylesheet"/>
  <div id="container">

    <div id="myPageTop" class='text'>
      <table>
        <tr><label>搜索地点：</label></tr>
        <tr><input id="tipinput"/></tr>
      </table>
    </div>

    <!--        <div id="findpath" class="text">-->
    <!--            <table>-->
    <!--                <tr>-->
    <!--                    <td><label>起点：</label></td>-->
    <!--                    <td><input id="begin" /></td>-->
    <!--                    <td><label>终点：</label></td>-->
    <!--                    <td><input id="end" /></td>-->
    <!--                    <td><button @click="route('keyword')" class="but">导航</button></td>-->
    <!--                </tr>-->
    <!--                <tr>-->
    <!--                    <td><label>起点坐标：</label></td>-->
    <!--                    <td><input id="beginLngLat" /></td>-->
    <!--                    <td><label>终点坐标：</label></td>-->
    <!--                    <td><input id="endLngLat" /></td>-->
    <!--                    <td><button @click="route('lnglat')" class="but">导航</button></td>-->
    <!--                </tr>-->
    <!--            </table>-->
    <!--            <hr />-->
    <!--        </div>-->


    <div id="markposition" class="text">
      <table>
        <tr>
          <td><label id="mark-tips">选择一处地点，为此处添加标记</label></td>
          <td>
            <button id='defineLine' class="but" @click="defineLine">绘制连线</button>
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

    <div id="window" ref="windowInfo">
      <div id="imageContainer">
        <table>
          <tr>
            <img id='image' ref='imageInput' :src=windowInfo.imageUrl @change="handelUpload" @click="editImage"/>
          </tr>
          <tr>
            <input ref="fileInput" accept="image/*" type="file" @change="updateSelectedFiles"/>
            <button @click="uploadFiles(this.selectedMarker._opts.extData.id,this.url)">上传</button>
          </tr>
        </table>
      </div>
      <div id="textContainer">
        <textarea id="windowText" :value=windowInfo.text border="none" outline="none" readonly type="text"/>
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
    ;

  </div>
</template>
<script>
import config from '../../api-key.json';
import axios from 'axios';
import AMapLoader from '@amap/amap-jsapi-loader';
import {ref} from 'vue';
//必须要使用密钥才能使用插件
window._AMapSecurityConfig = {
  securityJsCode: config.securityJsCode
}

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
      begin: {input: 'begin'}, //起点
      end: {input: 'end'},  //终点
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
      windowInfo: {
        window: null,
        title: '暂无标题~',
        imageUrl: "https://cdn.pixabay.com/photo/2013/05/07/08/46/paper-109277_1280.jpg",
        text: '暂无描述~',
        images: [],
      },
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

          //this.map.addControl(new AMap.ToolBar());
          this.map.addControl(new AMap.Scale());
          this.map.addControl(new AMap.MapType());
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
            params: {sql: Query},
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
                    extData: {id: res[i].id},
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
        //this.initializeMarkers();
        //初始化标记点
        {
          axios.default.request({
            url: this.url,   //服务器端口号
            responseType: 'json',
            method: 'get',
            params: {sql: 'select * from points'},
          })
              .then(res => {
                const message = res.data;   //必须这么写才能获取到真正的值
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
                      images: message[i].images,
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
                    } else {
                      this.infoIsOpen = true;
                      this.selectedMarker = e.target;
                      if (e.target.image != null) this.windowInfo.imageUrl = e.target.images;
                      if (e.target._opts.extData.description != null) this.windowInfo.text = e.target._opts.extData.description;
                      if (e.target._opts.extData.images != null) {
                        this.windowInfo.images = e.target._opts.extData.images;
                        const binary = atob(this.windowInfo.images);
                        const uint8Array = new Uint8Array(binary.length);
                        for (let i = 0; i < binary.length; i++) {
                          uint8Array[i] = binary.charCodeAt(i);
                        }
                        // 创建 Blob 对象以包含二进制数据
                        const blob = new Blob([uint8Array]);
                        // 如果需要，使用 URL.createObjectURL() 将 Blob 转换为 URL 字符串
                        const url = URL.createObjectURL(blob);
                        this.windowInfo.imageUrl = url;
                        console.log(url);
                      }else {
                        this.windowInfo.imageUrl = "https://cdn.pixabay.com/photo/2013/05/07/08/46/paper-109277_1280.jpg";
                      }
                      this.infoWindow.setContent(this.$refs.windowInfo);
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
    initializeMarkers() {
      axios.get(this.url, {
        params: { sql: 'select * from points' }
      }).then(({ data }) => {
        const markers = data.map(({ id, lng, lat, label, description, images }) => {
          const lnglat = [lng, lat];
          const marker = new this.AMap.Marker({
            position: lnglat,
            offset: new this.AMap.Pixel(0, 0),
            extData: { id, lng, lat, label, description, images }
          });

          marker.content = description;

          marker.setLabel({
            offset: new this.AMap.Pixel(20, 20),
            content: label,
            direction: 'bottom'
          });

          marker.setMap(this.map);

          marker.on('click', (e) => {
            const { target: { image, _opts: { extData: { description, images } } } } = e;

            if (this.selectedMarker && this.selectedMarker._opts.label.content === e.target._opts.label.content && this.infoIsOpen) {
              this.infoWindow.close();
              this.infoIsOpen = false;
            } else {
              this.infoIsOpen = true;
              this.selectedMarker = e.target;
              this.windowInfo.imageUrl = images ?? 'https://cdn.pixabay.com/photo/2013/05/07/08/46/paper-109277_1280.jpg';
              this.windowInfo.text = description;
              this.infoWindow.setContent(this.$refs.windowInfo);
              this.infoWindow.open(this.map, e.target.getPosition());
            }
          });

          return marker;
        });

        this.markers = [...this.markers, ...markers];
      }).catch(error => {
        alert(error);
        console.log(error);
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
      const id = this.markers[len-1]._opts.extData.id + 1;
      console.log(id);
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
            {keyword: that.begin},
            {keyword: that.end}
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
            params: {sql: Query},
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
    //设置地图样式
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
      console.log(this.selectedMarker)
      //保存到数据库中
      const Query = 'update points set description="' + text + '" where id=' + this.selectedMarker._opts.extData.id;
      console.log(Query)
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

#myPageTop {
  position: absolute;
  top: 10px;
  left: 50%;
  width: 10%;
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
  height: 400px;
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
  width: 80%;
  height: 40%;
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
  height: 60px;
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

</style>