package com.example.bletest

import android.Manifest
import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bletest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var PERMISSION_REQUEST_CODE = 10001 // 아래 권한들에 대한 request 변수
    val PERMISSION_LIST = arrayOf( // 공통으로 받는 초기 권한 목록
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_PHONE_NUMBERS, // 전화 권한 : 디바이스 정보(ID, PHONE NUMBER)
        Manifest.permission.READ_PHONE_STATE, // 전화 권한 : 디바이스 정보(ID, PHONE NUMBER)
        Manifest.permission.READ_SMS // SMS 권한 : 알림
    )

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Toast.makeText(this,"권한이 승인되었습니다.",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"권한이 거부되었습니다.",Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    private fun checkPermissions(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this,it) == PackageManager.PERMISSION_GRANTED
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initCheckPermission(){
        if(!checkPermissions(PERMISSION_LIST)) requestPermissions(PERMISSION_LIST,PERMISSION_REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initCheckPermission()

    }
}