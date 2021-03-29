<template>
  <div class="main-div">
    <div class="info-div">
      <div v-for="car in carInfoMap" :key="car.name" class="info-list">
        <p><span><a-icon type="car" theme="twoTone" twoToneColor="#fcc41a"/></span><span>{{ car.name }}</span></p>
        <p><span>状态：</span><span>{{ car.state }}</span></p>
        <p><span>载货：</span><span>{{ car.loadState == 'load'? '有货': '空车' }}</span></p>
        <p><span>位置：</span><span>{{ car.position }}</span></p>
      </div>
    </div>
    <div class="map_div">
      <ul class="tag_color">
<!--        <li><span class="blank" /><label>空地</label></li>-->
        <li><a-button type="primary" @click="addOrderRandom">随机添加订单</a-button></li>
        <li><span class="agv-car"><a-icon type="car" theme="twoTone" twoToneColor="#fcc41a"/></span><label>agv车辆(空车)</label></li>
        <li><span class="agv-car"><a-icon type="car" theme="twoTone" twoToneColor="#000000"/></span><label>agv车辆(有货)</label></li>
<!--        <li><span class="point" /><label>点</label></li>-->
        <li><span class="location1" /><label>位置(空置)</label></li>
        <li><span class="location2"><a-icon type="gold" /></span><label>位置(有货)</label></li>
        <span v-show="false" ref="goods"><a-icon type="gold" theme="twoTone" twoToneColor="#000000" style="font-size: 40px;"/></span>
        <span v-show="false" ref="car_empt"><a-icon type="car" theme="twoTone" twoToneColor="#fcc41a" style="font-size: 40px;"/></span>
        <span v-show="false" ref="car_full"><a-icon type="car" theme="twoTone" twoToneColor="#000000" style="font-size: 40px"/></span>
      </ul>
<!--      <table ref="mapTable" class="dispatch-view">-->
<!--        <tr v-for="i in Number(mapObj.width)" :key="i">-->
<!--          <td v-for="j in Number(mapObj.height)" :key="j" :ref="getOne(i, j).name" :name="getOne(i, j).realName" :hasGood="hasGood(i, j)" :class="getTdClass(i, j)">-->
<!--            <a-icon type="gold" v-if="getTdClass(i, j) == 'tdclass_22'" :style="{ fontSize: '40px' }" />-->
<!--          </td>-->
<!--        </tr>-->
<!--      </table>-->
      <svg width="80%" height="80%" id="factoryMap" viewBox="0,0,20000,20000">
      </svg>
    </div>
  </div>
</template>

<script>
import Snap from 'snapsvg-cjs'
// import Snap from 'snapsvg'
// import { getEvent } from '../../api/agv'
// import { DingtalkOutlined } from '@ant-design/icons'
export default {
  name: 'Index',
  data () {
    var mapArr = []
    for (let i = 0; i < 10; i++) {
      mapArr[i] = []
      for (let j = 0; j < 10; j++) {
        mapArr[i][j] = 0
      }
    }
    return {
      svgObj: null,
      vehicleSvg: {
        load: null,
        unload: null,
        location_load: null
      },
      mapObj: {pointArr: [], locationArr: [], pathArr: [], step: 1},
      carTimeer: null,
      drawTimeer: null,
      carInfoMap: {'Vehicle-0001': {
        id: 1371698204959105000,
        loadState: 'load',
        name: 'Vehicle-0001',
        position: 'Point-0001',
        state: 'IDLE',
        xPosition: '1662',
        yPOsition: '2969'
      }},
      minSequenceNo: -1,
      drawCarLock: false,
      updateLock: {
        location: false,
        vehicle: false
      },
      sequenceCar: [],
      locationState: [], // 0-空，1-有货
      carAgvEmpt: null,
      carAgvFull: null,
      goods: null,
      carLine: []
    }
  },
  components: {
    // DingtalkOutlined: DingtalkOutlined
  },
  computed: {
  },
  created () {
    var _this = this
    Snap.load('../../static/svg/car_load.svg', function (svgCar) {
      _this.vehicleSvg.load = svgCar
    })
    Snap.load('../../static/svg/car_null.svg', function (svgCar) {
      _this.vehicleSvg.unload = svgCar
    })
    Snap.load('../../static/svg/location_load.svg', function (svgLocation) {
      _this.vehicleSvg.location_load = svgLocation
    })
    this.getMapData('/agv2/factory.xml')
  },
  mounted () {
    // var _this = this
    this.initMap()
    this.initEventMessageListener()
  },
  methods: {
    addOrderRandom () {
      this.$store.dispatch('agv/CreateTorder').then(function (res) {
        console.log(res)
      })
    },
    initEventMessageListener () {
      var _this = this
      _this.loadCarInfo()
      this.carTimeer = setInterval(function () {
        // _this.clearCar()
        _this.loadCarInfo()
      }, 1000)
      this.drawTimeer = setInterval(function () {
        // _this.clearCar()
        _this.driveCar()
      }, 100)
    },
    initMap () {
      document.body.style.setProperty('--main-div-height', document.getElementsByClassName('ant-layout-content')[0].clientHeight + 'px')
      document.body.style.setProperty('--main-div-width', document.getElementsByClassName('single-page')[0].clientWidth + 'px')
      var s = Snap('#factoryMap')
      this.svgObj = s
      this.drawPath(s)
      this.drawPoint(s)
      this.drawLocation(s)
      this.initCarSvg(s)
    },
    drawPoint (svg) {
      var pointGroup = svg.paper.group()
      for (var pointTemp of this.mapObj.pointArr) {
        pointGroup.add(svg.paper.circle(pointTemp.xPosition, pointTemp.yPosition, 100).attr({
          fill: '#bada55',
          stroke: '#000',
          strokeWidth: 5
        }))
      }
    },
    drawLocation (svg) {
      var locationGroup = svg.paper.group()
      for (var pointTemp of this.mapObj.locationArr) {
        let xposition = pointTemp.xPosition || pointTemp.xposition
        let yposition = pointTemp.xPosition || pointTemp.yposition
        locationGroup.add(svg.paper.rect(xposition - 300, yposition - 300, 600, 600).attr({
          fill: '#3fe0d3',
          stroke: '#000',
          strokeWidth: 5,
          class: 'location'
        }))
        if (Number(pointTemp.type) === 22) {
          let goodsAgv = svg.path('M226.41,170.09l-19.9-12.82,19.59-12.64c10.19-6.52,10.31-17,.31-23.48l-19.9-12.82L226.1,95.71c10.16-6.55,10.28-17.09.31-23.54L150.23,23.1c-10-6.45-26.37-6.37-36.53.18L33.39,75c-10.16,6.55-10.31,17.09-.31,23.52L53,111.36,33.39,124c-10.16,6.55-10.31,17.09-.31,23.54L53,160.33,33.39,172.94c-10.16,6.55-10.31,17.09-.31,23.54l76.18,49.07c10,6.45,26.37,6.38,36.53-.18l80.31-51.74C236.26,187.08,236.38,176.54,226.41,170.09ZM51.73,88.94c-2.55-1.75-3.5-5.53-2.29-8.64a6.83,6.83,0,0,1,2.94-5.09L120,32.37c2.75-1.74,6.2-.49,7.66,2.77s.42,7.36-2.33,9.11L65,82.48l4.39,3c2.7,1.85,3.64,6,2.08,9.2s-5,4.31-7.74,2.46Zm147.06,99-61.91,39.88c-5.08,3.26-13.26,3.31-18.25.08l-58-37.4c-5-3.24-4.92-8.49.16-11.78L71.11,172l38.18,24.6c10,6.45,26.37,6.37,36.53-.18l42.29-27.25,10.83,7C204,179.36,203.87,184.62,198.79,187.91Zm0-49-61.91,39.88c-5.08,3.29-13.26,3.32-18.25.11l-58-37.4c-5-3.21-4.92-8.49.16-11.76l10.37-6.68,38.18,24.6c10,6.43,26.37,6.37,36.53-.18l42.32-27.24,10.83,7C204,130.39,203.87,135.65,198.79,138.91Z')
          goodsAgv.attr({
            stroke: '#ffffff',
            strokeWidth: 0,
            fill: '#ffffff',
            strokeLinejoin: 'round',
            strokeLinecap: 'round',
            class: pointTemp.name
          })
          locationGroup.add(goodsAgv)
          let m = new Snap.Matrix()
          m.translate(xposition - 260, yposition - 260)
          m.scale(2, 2)
          goodsAgv.transform(m)
        }
      }
    },
    drawGood (name, m, svg) {
      let goodsAgv = svg.path('M342 472h342c.4 0 .9 0 1.3-.1 4.4-.7 7.3-4.8 6.6-9.2l-40.2-248c-.6-3.9-4-6.7-7.9-6.7H382.2c-3.9 0-7.3 2.8-7.9 6.7l-40.2 248c-.1.4-.1.9-.1 1.3 0 4.4 3.6 8 8 8zm91.2-196h159.5l20.7 128h-201l20.8-128zm2.5 282.7c-.6-3.9-4-6.7-7.9-6.7H166.2c-3.9 0-7.3 2.8-7.9 6.7l-40.2 248c-.1.4-.1.9-.1 1.3 0 4.4 3.6 8 8 8h342c.4 0 .9 0 1.3-.1 4.4-.7 7.3-4.8 6.6-9.2l-40.2-248zM196.5 748l20.7-128h159.5l20.7 128H196.5zm709.4 58.7l-40.2-248c-.6-3.9-4-6.7-7.9-6.7H596.2c-3.9 0-7.3 2.8-7.9 6.7l-40.2 248c-.1.4-.1.9-.1 1.3 0 4.4 3.6 8 8 8h342c.4 0 .9 0 1.3-.1 4.3-.7 7.3-4.8 6.6-9.2zM626.5 748l20.7-128h159.5l20.7 128H626.5z')
      goodsAgv.attr({
        stroke: '#000000',
        strokeWidth: 0,
        fill: '#0a0a0a',
        strokeLinejoin: 'round',
        strokeLinecap: 'round',
        class: name
      })
      // let m = new Snap.Matrix()
      // m.translate(xPosition - 260, yPosition - 260)
      m.scale(0.6, 0.6)
      m.translate(260, 260)
      goodsAgv.transform(m)
    },
    drawPath (svg) {
      var pathGroup = svg.paper.group()
      for (let pathTemp of this.mapObj.pathArr) {
        pathGroup.add(svg.paper.line(pathTemp.startX, pathTemp.startY, pathTemp.endX, pathTemp.endY).attr({
          fill: '#bada55',
          stroke: '#8d8d8d',
          strokeWidth: 10
        }))
      }
    },
    initCarSvg (svg) {
      var _this = this
      this.$store.dispatch('agv/vehicles', { 'procState': 'IDLE' }).then(function (res) {
        if (res) {
          _this.$store.dispatch('agv/updateVehiclesByName', {
            'vehicleArr': res
          }).then(function (res2) {
            for (let car of res2.data) {
              car.xPosition = _this.mapObj.pointMap[car.position].xPosition
              car.yPOsition = _this.mapObj.pointMap[car.position].yPosition
              _this.drawCarPath(car.xPosition, car.yPOsition, svg, car.name, car.loadState)
              _this.carInfoMap[car.name] = car
            }
          })
        }
      })
    },
    drawCarPath (xPosition, yPOsition, svg, carName, state) {
      let carGroup = svg.paper.group()
      carGroup.attr({
        class: carName
      })
      carGroup.append(this.vehicleSvg.unload)
      let m = new Snap.Matrix()
      m.translate(xPosition - 700, yPOsition - 700)
      m.scale(0.07, 0.07)
      // m.rotate(point.alpha - 90) // 使飞机总是朝着曲线方向。point.alpha：点的切线和水平线形成的夹角
      carGroup.transform(m)
    },
    getMapData (xmlFile) {
      var xPositionMin, xPositionMax, yPositionMin, yPositionMax
      var pointListResult = []
      var locationListResult = []
      var pathListResult = []
      var pointMap = {}
      var mapInfo = new DOMParser().parseFromString(this.loadXML(xmlFile), 'text/xml')
      // point
      var pointArr = mapInfo.getElementsByTagName('point')
      xPositionMin = pointArr[0].getAttribute('xPosition')
      xPositionMax = xPositionMin
      yPositionMin = pointArr[0].getAttribute('yPosition')
      yPositionMax = yPositionMin
      for (let i = 0; i < pointArr.length; i++) {
        xPositionMin = Number(pointArr[i].getAttribute('xPosition')) > Number(xPositionMin) ? Number(xPositionMin) : Number(pointArr[i].getAttribute('xPosition'))
        xPositionMax = Number(pointArr[i].getAttribute('xPosition')) < Number(xPositionMax) ? Number(xPositionMax) : Number(pointArr[i].getAttribute('xPosition'))
        yPositionMin = Number(pointArr[i].getAttribute('yPosition')) > Number(yPositionMin) ? Number(yPositionMin) : Number(pointArr[i].getAttribute('yPosition'))
        yPositionMax = Number(pointArr[i].getAttribute('yPosition')) < Number(yPositionMax) ? Number(yPositionMax) : Number(pointArr[i].getAttribute('yPosition'))
      }
      var step = 18000 / Number(xPositionMax - xPositionMin)
      this.mapObj.step = step
      for (let i = 0; i < pointArr.length; i++) {
        var pointObj = {}
        pointObj.type = 1
        pointObj.name = pointArr[i].getAttribute('name')
        pointObj.realName = pointArr[i].getAttribute('name')
        pointObj.xPosition = ((Number(pointArr[i].getAttribute('xPosition')) - xPositionMin + 1000) * step).toFixed(0)
        pointObj.yPosition = (20000 - (Number(pointArr[i].getAttribute('yPosition')) - yPositionMin + 1000) * step).toFixed(0)
        pointListResult.push(pointObj)
        pointMap[pointObj.name] = pointObj
      }
      var locationArr = mapInfo.getElementsByTagName('location')
      for (let i = 0; i < locationArr.length; i++) {
        var locationObj = {}
        locationObj.name = locationArr[i].getAttribute('name')
        locationObj.realName = locationArr[i].getAttribute('name')
        locationObj.xPosition = ((Number(locationArr[i].getAttribute('xPosition')) - xPositionMin + 1000) * step).toFixed(0)
        locationObj.yPosition = (20000 - (Number(locationArr[i].getAttribute('yPosition')) - yPositionMin + 1000) * step).toFixed(0)
        locationObj.type = locationArr[i].getAttribute('empty') === '0' ? 22 : 2
        locationListResult.push(locationObj)
      }
      var pathArr = mapInfo.getElementsByTagName('path')
      for (let i = 0; i < pathArr.length; i++) {
        let pathObj = {}
        pathObj.startX = pointMap[pathArr[i].getAttribute('sourcePoint')].xPosition
        pathObj.startY = pointMap[pathArr[i].getAttribute('sourcePoint')].yPosition
        pathObj.endX = pointMap[pathArr[i].getAttribute('destinationPoint')].xPosition
        pathObj.endY = pointMap[pathArr[i].getAttribute('destinationPoint')].yPosition
        pathListResult.push(pathObj)
      }
      this.mapObj.pointMap = pointMap
      this.mapObj.pointArr = pointListResult
      // this.mapObj.locationArr = locationListResult
      this.mapObj.pathArr = pathListResult
      var _this = this
      this.$store.dispatch('agv/getMapData').then(function (res) {
        if (res.data.locationArr.length === 0) {
          _this.$store.dispatch('agv/setMapData', _this.mapObj).then(function (res2) {
          })
          _this.mapObj.locationArr = locationListResult
        } else {
          _this.mapObj.locationArr = res.data.locationArr
        }
        _this.drawLocation(_this.svgObj)
      })
    },
    driveCar () {
      if (this.sequenceCar.length > 0) {
        var tempInfo = this.sequenceCar.shift()
        // 找到执行任务的小车svg
        if (tempInfo.type === 'Vehicle') {
          let carSvg = this.svgObj.select('.' + tempInfo.vehicleName)
          // 得到终点信息
          let pointInfo = this.mapObj.pointMap[tempInfo.position]
          let m2 = new Snap.Matrix()
          m2.translate(pointInfo.xPosition - 700, pointInfo.yPosition - 700)
          m2.scale(0.07, 0.07)
          carSvg.animate({transform: m2}, 350, function () {
          })
          // 画出路径
          // var path = this.svgObj.paper.path('M150 150L3000 150L3000 3000').attr({
          //   stroke: '#000000',
          //   strokeWidth: 20,
          //   fill: 'none'
          // })
          // 执行动画
          // var length = Snap.path.getTotalLength(path) // 获取path的长度
          // Snap.animate(0, length, function (val) {
          //   var point = Snap.path.getPointAtLength(path, val) // 根据path长度变化获取坐标
          //   var m = new Snap.Matrix()
          //   m.translate(point.x - 500, point.y - 500)
          //   // m.rotate(point.alpha - 90) // 使飞机总是朝着曲线方向。point.alpha：点的切线和水平线形成的夹角
          //   carSvg.transform(m)
          // }, 3000, function () {
          //   console.log('animation end')
          // })
        }
        if (tempInfo.type === 'TransportOrder' && tempInfo.destination && tempInfo.destination.state === 'FINISHED') {
          let carInfo = this.carInfoMap[tempInfo.processingVehicleName] || {}
          carInfo.loadState = tempInfo.destination.operation || ''
          let carSvg = this.svgObj.select('.' + tempInfo.processingVehicleName)
          if (this.svgObj.select('.' + tempInfo.destination.locationName)) {
            this.svgObj.select('.' + tempInfo.destination.locationName).remove()
          }
          if (tempInfo.destination.operation === 'unload') {
            console.log(carSvg)
            if (carSvg) {
              carSvg.node.children[0].remove()
              carSvg.append(this.vehicleSvg.unload)
            }
            this.drawGood(tempInfo.destination.locationName, carSvg.matrix, this.svgObj)
          } else {
            if (carSvg) {
              carSvg.node.children[0].remove()
              carSvg.append(this.vehicleSvg.load)
            }
          }
          // carSvg.add(this.svgObj.select('.' + tempInfo.destination.locationName))
          // document.getElementsByName(tempInfo.destination.locationName)[0].setAttribute('hasGood', tempInfo.destination.operation === 'unload' ? 'has' : 'dothas')
          if (!this.updateLock.location) {
            this.updateLock.location = true
            this.$store.dispatch('agv/updateLocationByName', {
              'type': tempInfo.destination.operation === 'unload' ? '22' : '2',
              'realName': tempInfo.destination.locationName

            }).then(() => {
              setTimeout(() => {
                this.updateLock.location = false
              }, 2000)
            })
          }
          if (!this.updateLock.vehicle) {
            this.updateLock.vehicle = true
            this.$store.dispatch('agv/updateVehicleByName', {
              'position': carInfo.position,
              'state': carInfo.state,
              'name': carInfo.name,
              'energyLevel': carInfo.energyLevel + '',
              'loadState': carInfo.loadState
            }).then(() => {
              setTimeout(() => {
                this.updateLock.vehicle = false
              }, 2000)
              // this.updateLock.vehicle = false
            })
          }
        }
      }
    },
    loadCarInfo () {
      var _this = this
      // 如果别的请求未完成，不进入下一次请求
      if (!_this.drawCarLock) {
        _this.drawCarLock = true
        // 第一次加载
        if (_this.minSequenceNo === -1) {
          _this.$store.dispatch('agv/getEvent', { minSequenceNo: 0 }).then(function (res) {
            _this.minSequenceNo = res.statusMessages.pop().sequenceNumber + 1
          }).then(function () {
            _this.drawCarLock = false
          })
        } else {
          _this.$store.dispatch('agv/getEvent', { minSequenceNo: _this.minSequenceNo }).then(function (res) {
            var eventList = res.statusMessages
            if (eventList.length > 0) {
              // 将事件列表整理成消息队列的样子
              for (var event of eventList) {
                if (event.type === 'TransportOrder' && event.orderState === 'BEING_PROCESSED') {
                  for (var i = event.destinations.length - 1; i >= 0; i--) {
                    if (event.destinations[i].state === 'FINISHED') {
                      event.destination = event.destinations[i]
                      break
                    }
                  }
                }
                _this.sequenceCar.push(event)
              }
              // console.log(_this.sequenceCar)
              _this.minSequenceNo = res.statusMessages.pop().sequenceNumber + 1
            }
          }).then(function () {
            _this.drawCarLock = false
          })
        }
      }
    },
    getOne (i, j) {
      return this.mapObj.mapArr[i - 1][j - 1]
    },
    getTdClass (i, j) {
      return 'tdclass_' + this.mapObj.mapArr[i - 1][j - 1].type
    },
    hasGood (i, j) {
      return this.mapObj.mapArr[i - 1][j - 1].type === '22' ? 'has' : 'dothas'
    },
    loadXML (xmlFile) {
      var xmltext = new XMLHttpRequest()
      xmltext.open('GET', xmlFile, false)
      xmltext.send()
      return xmltext.response
    }
  }
}
</script>

<style scoped>
  body{
  }
  .car-in{
    background-color: #e7f108;
  }
  .tdclass_0{
    background-color: #727272;
  }
  .tdclass_1{
    background-color: #2ac06d;
  }
  .tdclass_2{
    background-color: #87c7ff;
    content: "×";
  }
  .tdclass_22{
    background-color: #87c7ff;
    content: "×";
  }
  .info-div{
    width: 20%;
    height: 100%;
    display: block;
    /*align-items: center;*/
    min-width: 400px;
    max-width: 450px;
  }
  .info-list{
    width: 80%;
    height: 140px;
    border: solid 1px #9aaabf;
    border-radius: 10px;
    box-shadow: 5px 5px #cdcdcd;
    padding: 10px 10px;
    margin-top: 10px;
    margin-left: 10%;
  }
  .info-list p{
    display: inline-flex;
    justify-content: left;
    width: 100%;
    margin: 5px 0;
  }
  .info-list p span:nth-of-type(1){
    width: 20%;
    min-width: 50px;
    text-align: left;
  }
  .map_div{
    width: 80%;
    height: 100%;
    display: inline-flex;
    justify-content: center;
    align-items: center;
    border: solid 3px #9aaabf;
  }
  .main-div{
    width: var(--main-div-width,1000px);
    height: var(--main-div-height,1000px);
    display: flex;
    align-items: center;
  }
  .dispatch-view{
  }
  .dispatch-view td{
    border: 1px solid #ffffff;
    text-align: center;
    height: var(--td-height,100px);
    width: var(--td-height,100px)!important;
  }
  .tag_color{
    /*background-color: #9aaabf;*/
    margin-right: 20px;
  }
  .tag_color li{
    list-style: none;
    line-height: 40px;
    display: flex;
    /*justify-content: center;*/
    align-items: center;
  }
  .tag_color .agv-car{
    display: inline-block;
    width: 20px;
    height: 20px;
    line-height: 20px;
    /*background-color: #e7f108;*/
    margin-right: 10px;
    vertical-align: center;
  }
  .tag_color .point{
    display: inline-block;
    width: 20px;
    height: 20px;
    background-color: #2ac06d;
    margin-right: 10px;
  }
  .tag_color .location1{
    display: inline-block;
    width: 20px;
    height: 20px;
    background-color: #3fe0d3;
    margin-right: 10px;
  }
  .tag_color .location2{
    display: inline-block;
    width: 20px;
    height: 20px;
    line-height: 20px;
    text-align: center;
    background-color: #3fe0d3;
    margin-right: 10px;
  }
  .tag_color .blank{
    display: inline-block;
    width: 20px;
    height: 20px;
    background-color: #727272;
    margin-right: 10px;
  }
</style>
