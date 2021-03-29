import Vue from 'vue'
import Vuex from 'vuex'
import account from './modules/account'
import setting from './modules/setting'
import dict from './modules/dict'
import agv from './modules/agv'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    account,
    setting,
    dict,
    agv
  }
})
