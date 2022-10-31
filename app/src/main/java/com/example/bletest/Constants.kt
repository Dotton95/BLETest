package com.example.bletest

import android.Manifest

class Constants {
    companion object{
        var TAG = "dotton95"

        var PERMISSION_REQUEST_CODE = 10001 // 아래 권한들에 대한 request 변수

        val PERMISSION_LIST = arrayOf( // 공통으로 받는 초기 권한 목록
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
//            Manifest.permission.BLUETOOTH_ADMIN,
//            Manifest.permission.BLUETOOTH_CONNECT,
//            Manifest.permission.BLUETOOTH_ADVERTISE
        )

        //사용자 BLE UUID Service/Rx/Tx
        //churiven admin - 519df228-a60c-441b-be9b-b10e5f228a3b
        const val SERVICE_STRING = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E"
        const val CHARACTERISTIC_COMMAND_STRING = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E"
        const val CHARACTERISTIC_RESPONSE_STRING = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"

        //BluetoothGattDescriptor 고정
        const val CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"
    }
}