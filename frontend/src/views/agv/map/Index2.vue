<template>
  <div class="main-div">
    <div class="info-div">
      <div v-for="car in carInfo" :key="car.name" class="info-list">
        <p><span><a-icon type="car" theme="twoTone" twoToneColor="#fcc41a"/></span><span>{{ car.name }}</span></p>
        <p><span>状态：</span><span>{{ car.state }}</span></p>
        <p><span>载货：</span><span>{{ car.loadState == 'load'? '有货': '空车' }}</span></p>
        <p><span>位置：</span><span>{{ car.position }}</span></p>
      </div>
    </div>
    <div class="map_div">
      <ul class="tag_color">
        <li><span class="blank" /><label>空地</label></li>
        <li><span class="agv-car"><a-icon type="car" theme="twoTone" twoToneColor="#fcc41a"/></span><label>agv车辆(空车)</label></li>
        <li><span class="agv-car"><a-icon type="car" theme="twoTone" twoToneColor="#000000"/></span><label>agv车辆(有货)</label></li>
        <li><span class="point" /><label>点</label></li>
        <li><span class="location1" /><label>位置(空置)</label></li>
        <li><span class="location2"><a-icon type="gold" /></span><label>位置(有货)</label></li>
        <span v-show="false" ref="goods"><a-icon type="gold" theme="twoTone" twoToneColor="#000000" style="font-size: 40px;"/></span>
        <span v-show="false" ref="car_empt"><a-icon type="car" theme="twoTone" twoToneColor="#fcc41a" style="font-size: 40px;"/></span>
        <span v-show="false" ref="car_full"><a-icon type="car" theme="twoTone" twoToneColor="#000000" style="font-size: 40px"/></span>
      </ul>
      <table ref="mapTable" class="dispatch-view">
        <tr v-for="i in Number(mapObj.width)" :key="i">
          <td v-for="j in Number(mapObj.height)" :key="j" :ref="getOne(i, j).name" :name="getOne(i, j).realName" :hasGood="hasGood(i, j)" :class="getTdClass(i, j)">
            <a-icon type="gold" v-if="getTdClass(i, j) == 'tdclass_22'" :style="{ fontSize: '40px' }" />
          </td>
        </tr>
      </table>
    </div>
  </div>
</template>

<script>
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
      mapObj: { width: 10, height: 10, mapArr: mapArr },
      carTimeer: null,
      drawTimeer: null,
      carInfo: [],
      minSequenceNo: -1,
      drawCarLock: false,
      sequenceCar: [],
      locationState: [], // 0-空，1-有货
      carAgvEmpt: null,
      carAgvFull: null,
      goods: null
    }
  },
  components: {
    // DingtalkOutlined: DingtalkOutlined
  },
  computed: {
  },
  created () {
  },
  mounted () {
    this.goods = this.$refs['goods']
    this.carAgvEmpt = this.$refs['car_empt']
    this.carAgvFull = this.$refs['car_full']
    // 初始化地图，包括point，location等
    document.body.style.setProperty('--main-div-height', document.getElementsByClassName('ant-layout-content')[0].clientHeight + 'px')
    document.body.style.setProperty('--main-div-width', document.getElementsByClassName('single-page')[0].clientWidth + 'px')
    var _this = this
    // 从数据库获取地图信息
    this.$store.dispatch('agv/getMapData').then(function (res) {
      // 如果数据库没有地图信息，就读取xml文件，并且将数据持久化进数据库
      if (res.data.mapArr && res.data.mapArr.length !== 0) {
        _this.mapObj = res.data
        _this.fixScreen()
      } else { // 如果数据库有数据，直接初始化
        _this.mapData('/agv2/factory.xml')
      }
    })

    // // 轮询接口画小车
    _this.loadCarInfo()
    this.carTimeer = setInterval(function () {
      // _this.clearCar()
      _this.loadCarInfo()
    }, 1000)
    //
    // // 轮询执行事件
    this.drawTimeer = setInterval(function () {
      // _this.clearCar()
      _this.drawCar()
    }, 100)
  },
  methods: {
    clearCar () {
      for (var carPart of document.getElementsByClassName('car-in')) {
        if (carPart) {
          carPart.setAttribute('class', carPart.getAttribute('name'))
        }
      }
    },
    drawCar () {
      if (this.sequenceCar.length > 0) {
        var tempInfo = this.sequenceCar.shift()
        if (tempInfo.type === 'Vehicle') {
          var loadState = ''
          // 清空前一步的图形
          for (const carInfo of this.carInfo) {
            if (carInfo.name === tempInfo.vehicleName) {
              if (this.$refs[carInfo.position][0].getAttribute('hasGood') === 'has') {
                this.$refs[carInfo.position][0].innerHTML = this.goods.innerHTML
                // this.$refs[carInfo.position][0].setAttribute('emptyLocation', '1')
              } else {
                this.$refs[carInfo.position][0].innerHTML = ''
              }
              carInfo.position = tempInfo.position
              carInfo.state = tempInfo.state
              loadState = carInfo.loadState
            }
          }
          // 画出下一步的图形
          if (loadState === 'load') {
            this.$refs[tempInfo.position][0].innerHTML = this.carAgvFull.innerHTML
          } else {
            this.$refs[tempInfo.position][0].innerHTML = this.carAgvEmpt.innerHTML
          }
        }
        // 记录小车执行完订单的状态，有货状态load，没货状态unload
        if (tempInfo.type === 'TransportOrder' && tempInfo.orderState === 'BEING_PROCESSED') {
          for (const carInfo of this.carInfo) {
            if (carInfo.name === tempInfo.processingVehicleName) {
              carInfo.loadState = tempInfo.destination.operation || ''
              document.getElementsByName(tempInfo.destination.locationName)[0].setAttribute('hasGood', tempInfo.destination.operation === 'unload' ? 'has' : 'dothas')
              this.$store.dispatch('agv/updateLocationByName', {
                'type': tempInfo.destination.operation === 'unload' ? '22' : '2',
                'realName': tempInfo.destination.locationName
              })
              this.$store.dispatch('agv/updateVehicleByName', {
                'position': carInfo.position,
                'state': carInfo.state,
                'name': carInfo.name,
                'energyLevel': carInfo.energyLevel + '',
                'loadState': carInfo.loadState
              })
            }
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
            _this.$store.dispatch('agv/vehicles', { 'procState': 'IDLE' }).then(function (res) {
              if (res) {
                _this.$store.dispatch('agv/updateVehiclesByName', {
                  'vehicleArr': res
                }).then(function (res2) {
                  _this.carInfo = res2.data
                  _this.clearCar()
                  for (var car of res2.data) {
                    if (car.position != null) {
                      var position = car.position
                      // _this.$refs[position][0].setAttribute('class', 'car-in')
                      if (car.loadState === 'load') {
                        _this.$refs[position][0].innerHTML = _this.carAgvFull.innerHTML
                      } else {
                        _this.$refs[position][0].innerHTML = _this.carAgvEmpt.innerHTML
                      }
                    }
                  }
                })
              }
            })
          }).then(function () {
            _this.drawCarLock = false
          })
        } else {
          _this.$store.dispatch('agv/getEvent', { minSequenceNo: _this.minSequenceNo }).then(function (res) {
            var eventList = res.statusMessages
            if (eventList.length > 0) {
              // 将事件列表整理成消息队列的样子
              for (var carInfo of eventList) {
                if (carInfo.type === 'TransportOrder' && carInfo.orderState === 'BEING_PROCESSED') {
                  for (var i = carInfo.destinations.length - 1; i >= 0; i--) {
                    if (carInfo.destinations[i].state === 'FINISHED') {
                      carInfo.destination = carInfo.destinations[i]
                      break
                    }
                  }
                }
                _this.sequenceCar.push(carInfo)
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
    fixScreen () {
      // 计算每个td的宽高
      var tdHeight = (document.getElementsByClassName('main-div')[0].clientHeight - 15) / this.mapObj.height
      document.body.style.setProperty('--td-height', tdHeight + 'px')
    },
    mapData (xmlFile) {
      var xPositionMin, xPositionMax, yPositionMin, yPositionMax
      var step = 5000
      var mapInfo = new DOMParser().parseFromString(this.loadXML(xmlFile), 'text/xml')
      // point
      var pointArr = mapInfo.getElementsByTagName('point')
      // var locationArr = mapInfo.getElementsByTagName('location')
      // 计算横竖格子数
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
      this.mapObj.width = Math.round(Number((xPositionMax - xPositionMin) / step + 6))
      this.mapObj.height = Math.round(Number((yPositionMax - yPositionMin) / step + 6))
      var mapArr = []
      for (let i = 0; i < this.mapObj.width; i++) {
        mapArr[i] = []
        for (let j = 0; j < this.mapObj.height; j++) {
          mapArr[i][j] = { type: 0, name: 'blank', realName: 'blank' }
        }
      }
      // 计算点的位置
      for (let i = 0; i < pointArr.length; i++) {
        const tempPoint = pointArr[i]
        const trNum = Math.abs(Math.round((Number(tempPoint.getAttribute('yPosition')) - yPositionMin) / step + 3) - this.mapObj.height) - 1
        const tdNum = Math.round((Number(tempPoint.getAttribute('xPosition')) - xPositionMin) / step + 3)
        mapArr[trNum][tdNum].type = 1
        mapArr[trNum][tdNum].name = tempPoint.getAttribute('name')
        mapArr[trNum][tdNum].realName = tempPoint.getAttribute('name')
      }
      // 计算location对应的格子位置
      var locationArr = mapInfo.getElementsByTagName('location')
      for (let i = 0; i < locationArr.length; i++) {
        const tempLocation = locationArr[i]
        // console.log(tempLocation.getElementsByTagName('link')[0].getAttribute('point'))
        const trNum = Math.abs(Math.round((Number(tempLocation.getAttribute('yPosition')) - yPositionMin) / step + 3) - this.mapObj.height) - 1
        const tdNum = Math.round((Number(tempLocation.getAttribute('xPosition')) - xPositionMin) / step + 3)
        mapArr[trNum][tdNum].type = tempLocation.getAttribute('empty') === '0' ? 22 : 2
        mapArr[trNum][tdNum].realName = tempLocation.getAttribute('name')
        if (mapArr[trNum][tdNum].name === 'blank') {
          mapArr[trNum][tdNum].name = tempLocation.getAttribute('name')
        }
      }
      this.fixScreen()
      this.mapObj.mapArr = mapArr
      this.$store.dispatch('agv/setMapData', this.mapObj).then(function (res) {
        console.log(res)
      })
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
    background-color: #87c7ff;
    margin-right: 10px;
  }
  .tag_color .location2{
    display: inline-block;
    width: 20px;
    height: 20px;
    line-height: 20px;
    text-align: center;
    background-color: #87c7ff;
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
