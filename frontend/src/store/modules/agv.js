// import db from 'utils/localstorage'
import requestagv from '../../utils/requestagv'
import request from '../../utils/request'
export default {
  namespaced: true,
  state: {
    // dicts: db.get('DICTS')
  },
  mutations: {
    // setDicts (state, val) {
    //   db.save('DICTS', val)
    //   state.dicts = val
    // }
  },
  actions: {
    vehicles () {
      return new Promise((resolve, reject) => {
        requestagv.get('/v1/vehicles').then(response => {
          console.log(response)
          // commit('SET_VEHICLES', response)
          resolve(response.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getEvent ({ commit }, params) {
      return new Promise((resolve, reject) => {
        requestagv.get('/v1/events', { minSequenceNo: params.minSequenceNo }).then(response => {
          // commit('SET_VEHICLES', response)
          resolve(response.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    // tOrderData (data) {
    //   console.log(data)
    //   return new Promise((resolve, reject) => {
    //     tOrderDataAPI(state.token).then(response => {
    //       resolve(response)
    //     }).catch(error => {
    //       reject(error)
    //     })
    //   })
    // }
    CreateTorder ({ commit }, params) {
      // console.log(data)
      return new Promise((resolve, reject) => {
        request.post('order/sendOrderRandom').then(response => {
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getMapData () {
      return new Promise((resolve, reject) => {
        request.get('map/getMapData').then(response => {
          console.log(response)
          // commit('SET_VEHICLES', response)
          resolve(response.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    getLocationData () {
      return new Promise((resolve, reject) => {
        request.get('location/list').then(response => {
          console.log(response)
          // commit('SET_VEHICLES', response)
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },
    setMapData ({ commit }, params) {
      return new Promise((resolve, reject) => {
        request.post('map/saveMap', params).then(response => {
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },
    updateLocationByName ({ commit }, params) {
      return new Promise((resolve, reject) => {
        request.post('location/updateByName', params).then(response => {
          resolve(response.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    updateVehicleByName ({ commit }, params) {
      return new Promise((resolve, reject) => {
        request.post('vehicle/updateByName', params).then(response => {
          resolve(response.data)
        }).catch(error => {
          reject(error)
        })
      })
    },
    updateVehiclesByName ({ commit }, params) {
      return new Promise((resolve, reject) => {
        request.post('vehicle/updateManyByName', params).then(response => {
          resolve(response.data)
        }).catch(error => {
          reject(error)
        })
      })
    }
  }
}
