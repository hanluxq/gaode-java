# gaode-demo

## Project setup
### 安装依赖
```
npm install
```

### Compiles and hot-reloads for development
#### 启动前端
```
npm run serve
```
#### 启动后端
```
npm run start
```

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).

### 卡顿问题
地图渲染出来之后，缩放和拖动可能会遇到卡顿问题，需要开启浏览器的硬件加速。

# 系统架构
## 前端
### vue3
### midelware: axios
## 后端
### express

# 功能实现
## 1.搜索地点
## 2.定位当前位置
## 3.寻路功能-待实现
## 4.切换图层功能-待实现
## 5.标记功能
### 5.1 标记地点
### 5.2 按标记顺序连接标记点
### 5.3 自定义两点之间的连线
## 6.显示点信息：文本框和图片
## 7.切换地图样式


# 部署需要修改：
```
1.前端的请求地址,改成服务器ip
2.打包后端mvn package，复制后端target下的jar包.运行后端java -jar ...
3,复制前端fromted,运行前端npm run serve
```

## mysql迁移
### 1.导出数据
``` mysqldump gaode > gaode.sql```
### 2.导入数据
``` 
mysql -u root -p 
create database gaode;
use gaode;
source gaode.sql;
```

## 服务器端
```
nohup npm run serve &
nohup java -jar gaode-0.0.2-SNAPSHOT.jar &
```

