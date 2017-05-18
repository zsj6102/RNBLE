'use strict';
var React = require('react-native');
var bleManager = React.NativeModules.BleManager;

class BleManager  {
  start(){
    return new Promise((fulfill, reject) => {
      bleManager.start((error) => {
        if(error){
          reject(error)
        }else{
          fulfill();
        }
      })
    })
  }
  
  scan(){
    bleManager.scan();
  }
  stopScan(){
    bleManager.stopScan()
  }
  checkState() {
    bleManager.checkState();
  }  
}

module.exports = new BleManager();
