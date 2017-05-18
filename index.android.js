/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import
{
    AppRegistry,
    Text,
    View,
    TouchableHighlight,
    NativeAppEventEmitter,
    Platform,
    PermissionsAndroid,
    Alert
} from 'react-native';
import BleManager from 'rn_ble'; 
class BLe extends Component
{

    constructor()
    {
        super()
    }

    componentDidMount()
    {
        BleManager.start().then((msg) => console.warn('msg', msg)).catch((err) => console.warn('err', err));
        this.scanning = setInterval(() => this.handleScan(), 2000);
        this.handleDiscoverPeripheral = this.handleDiscoverPeripheral.bind(this);

        NativeAppEventEmitter
            .addListener('Ble', this.handleDiscoverPeripheral);

    }

    componentWillUnMount()
    {
        clearInterval(this.scanning);
        BleManager.stopScan();
    }
    handleScan()
    {
        BleManager.scan();
    }

    toggleScanning(bool)
    {
        if (bool)
        {
            this.setState({ scanning: true })

        } else
        {
            this.setState({ scanning: false, ble: null })
            clearInterval(this.scanning);
            BleManager.stopScan();
        }
    }

    handleDiscoverPeripheral(data)
    {
        if (data.Name == 'Fmxy_20858' && parseFloat(data.Distance) < 0.5 )
        { 
            Alert.alert("提示", "检测到新版本,是否更新？", [
                { text: "是", onPress: () => { } }, { text: "取消" }
            ])
        }
    }

    render()
    {

        const container = {
            flex: 1,
            justifyContent: 'center',
            alignItems: 'center',
            backgroundColor: '#F5FCFF',
        }



        return (
            <View style={container}>
            </View>
        );
    }
}
AppRegistry.registerComponent('BLe', () => BLe);
