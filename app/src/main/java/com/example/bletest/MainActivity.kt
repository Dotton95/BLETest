package com.example.bletest


import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bletest.Constants.Companion.PERMISSION_LIST
import com.example.bletest.Constants.Companion.PERMISSION_REQUEST_CODE
import com.example.bletest.Constants.Companion.SERVICE_STRING
import com.example.bletest.Constants.Companion.TAG
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initCheckPermission(){
        if(!checkPermissions(PERMISSION_LIST)) requestPermissions(PERMISSION_LIST,PERMISSION_REQUEST_CODE)
    }
    private fun checkPermissions(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this,it) == PackageManager.PERMISSION_GRANTED
        }
    }
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

    //scan results
    var scanResult:ArrayList<BluetoothDevice>? = ArrayList()
    //ble adapter
    private var bleAdapter:BluetoothAdapter? = null
    //repository.bleAdapter

    //ble Gatt
    private var bleGatt:BluetoothGatt?=null





    //스캔 구현
    private var scanning: Boolean = false
    private var devicesArr = ArrayList<BluetoothDevice>()
    private val SCAN_PERIOD = 3000
    private val handler = Handler()
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recyclerViewAdapter : RecyclerViewAdapter
    private var bluetoothAdapter: BluetoothAdapter? = null
    private val mLeScanCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object:ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d(TAG,"4444444444444")
            Log.d("scanCallback", "BLE Scan Failed : $errorCode")
        }
        @SuppressLint("MissingPermission")
        override fun onBatchScanResults(results: MutableList<ScanResult > ?) {
            super.onBatchScanResults(results)
            Log.d(TAG,"555555555555")
            results?.let {
                // results is not null
                for(result in it) {
                    if(!devicesArr.contains(result.device) && result.device.name!=null) devicesArr.add(result.device)
                }
            }
        }
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            result?.let {
                // result is not null
                Log.d(TAG,"666666666666")
                if(!devicesArr.contains(it.device) && it.device.name!=null) {
                    Log.d(TAG,"7777777777777")
                    devicesArr.add(it.device)
                }else {
                    Log.d(TAG,"888888888888")
                }
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }
    }
    var main_rcv:RecyclerView?=null
    var progressbar:ProgressBar?=null
    var statusTv:TextView?=null
    var scanResults: ArrayList<BluetoothDevice>? = ArrayList()
    var scanBtn :Button?=null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initCheckPermission()
        //BLE 미지원시 앱 종료
        if(!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) finish()
/*
        setBLEAdapter()

        scanBtn.setOnClickListener {
            if(bleAdapter==null||!bleAdapter?.isEnabled!!){
                requestEnableBLE()
                statusTv.text = "스캔 실패 : BLE를 사용할 수 없습니다."
                return@setOnClickListener
            }

            val filters:MutableList<ScanFilter> = ArrayList()
            val scanFilter:ScanFilter = ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(UUID.fromString(SERVICE_STRING)))
                .build()
            filters.add(scanFilter)
            //.setServiceUuid()대신 .setDeviceAddress(MAC_ADDR)를 사용해 Uuid 말고 특정 mac address만 스캔할 수 있습니다.
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build()

//            bleAdapter?.bluetoothLeScanner?.startScan(filters,settings,BLEScanCallback)
        }
        */

        scanBtn = findViewById(R.id.main_btn_scan)
        statusTv = findViewById(R.id.statusText)

        main_rcv = findViewById(R.id.main_rcv)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        viewManager = LinearLayoutManager(this)
        recyclerViewAdapter = RecyclerViewAdapter(devicesArr)


        main_rcv!!.layoutManager = viewManager
        main_rcv!!.adapter = recyclerViewAdapter

        scanBtn!!.setOnClickListener {
            if(scanBtn!!.text == "SCAN"){
                devicesArr.clear()
                recyclerViewAdapter.notifyDataSetChanged()
                scanDevice(true)
            }else{
                scanDevice(false)
            }
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun scanDevice(state:Boolean) = if(state) {
        scanBtn!!.text = "STOP"
        handler.postDelayed({ //SCAN_PERIOD 정해진 시간까지만 SCAN
            scanning = false
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(mLeScanCallback)
            scanBtn!!.text = "SCAN"
        }, SCAN_PERIOD.toLong())
        scanning = true
        devicesArr.clear()
        bluetoothAdapter?.bluetoothLeScanner?.startScan(mLeScanCallback)
    }else {
        scanBtn!!.text = "SCAN"
        scanning = false
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(mLeScanCallback)
    }



    /*

    fun setBLEAdapter(){
        val bleManager:BluetoothManager? = applicationContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bleAdapter = bleManager?.adapter
    }
    private fun requestEnableBLE() {
        val bleEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        getResult.launch(bleEnableIntent)
    }
    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d(TAG, "it : $it")
            Log.d(TAG, "it.resultCode : ${it.resultCode}")
            Log.d(TAG, "Activity.RESULT_OK : ${Activity.RESULT_OK}")
        }
    private val BLEScanCallback:ScanCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object :ScanCallback(){
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.d(TAG,"Remote device name:${result!!.device.name}")
            addScanResult(result!!)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            for(result in results) addScanResult(result)
        }

        override fun onScanFailed(errorCode: Int) {
            Log.d(TAG,"BLE scan Failed with code $errorCode")
        }
    }
    @SuppressLint("MissingPermission")
    private fun addScanResult(result: ScanResult) {
        val device = result.device         // get scanned device
        val deviceAddress = device.address     // get scanned device MAC address
        val deviceName = device.name

        // 중복 체크
        for (dev in scanResults!!) {
            if (dev.address == deviceAddress) return
        }
        // add arrayList
        scanResults?.add(result.device)
        // status text UI update
        statusTv.text = "add scanned device: $deviceAddress"
        // scanlist update 이벤트
//        _listUpdate.value = Event(true)
    }
}
*/
}