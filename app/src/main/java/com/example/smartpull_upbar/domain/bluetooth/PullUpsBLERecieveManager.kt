package com.example.smartpull_upbar.domain.bluetooth

import CCCD_DESCRIPTOR_UUID
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import com.example.smartpull_upbar.domain.Resource
import isIndicatable
import isNotifiable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import printGattTable
import java.util.UUID
import javax.inject.Inject

@Suppress("DEPRECATION", "SpellCheckingInspection")
@SuppressLint("MissingPermission")

class PullUpsBLEReceiveManager
@Inject constructor
(private val bluetoothAdapter: BluetoothAdapter, private val context: Context)
: PullUpsReceiveManager
{

    override val data: MutableSharedFlow<Resource<PullUpsResult>> = MutableSharedFlow()

    private val bleScanner by lazy {bluetoothAdapter.bluetoothLeScanner}
    private val scanSettings = ScanSettings.Builder()  // Настраиваем параметры сканирования
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build()

    private val DEVICE_NAME = "ESP32"
    private val SERVICE_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
    private val CHARACTERISTIC_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8"

    private var gatt: BluetoothGatt? = null
    private var couroutineScope = CoroutineScope(Dispatchers.Default)
    private var isScanning = true

    private var currentConnectionAttempt = 1
    private var MAX_CONNECTION_ATTEMPT = 5

    init { findCharacteristics(SERVICE_UUID, CHARACTERISTIC_UUID)}

    private val scanCallback: ScanCallback = object : ScanCallback() {
        // крадём функцию "При возврате результата сканирования"
        override fun onScanResult(callbackType: Int, result: ScanResult) {
                if (result.device.name == DEVICE_NAME) {
                    couroutineScope.launch {
                        data.emit(Resource.Loading(topLoadingMessage = "Поиск устройства...", bottomLoadingMessage = "Обнаружен ${result.device.name}"))
                    }
                    if (isScanning) { // если функция startConnect() -> StartScan() сработала (скан начался)
                        result.device.connectGatt(context, false, gattCallBack, BluetoothDevice.TRANSPORT_LE) // коннектимся к Серверу на ESP32,
                        Log.d("onScanResult", "-> connectGatt сработало")   // используя (контекст, gattCallback, вид транспортировки данных)
                        isScanning = false
                        bleScanner.stopScan(this)
                    }
                }
            } // нужно создать GattCallback

            /* override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }*/

    }
    private val gattCallBack = object : BluetoothGattCallback() {

//      1) Законнектиться и начать поиск сервиса (отправить запрос серверу, callback обработает onServicesDiscovered)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
         if(status == BluetoothGatt.GATT_SUCCESS) // Если к gatt-серверу получилось подключиться (paired) - (not connected)
         {
             if (newState == BluetoothProfile.STATE_CONNECTED) // Если есть connection
             {
                 gatt.discoverServices() // найти сервис и перейти в onServicesDiscovered
                 Log.d("GattCallback", "Сервис обнаружен")
                 this@PullUpsBLEReceiveManager.gatt = gatt // инциализировать gatt-переменную сверху
                 couroutineScope.launch {
                     data.emit (Resource.Loading(topLoadingMessage = "Устанавливаем соединение...")) }
             }
             else if (newState == BluetoothProfile.STATE_DISCONNECTED)   // Если нет connection (чтобы справиться с потерей сети)
             { couroutineScope.launch { data.emit(Resource.Success(topSuccessMessage = "Требуется повторное \n подключение",
                                          data = PullUpsResult(0, 0, ConnectionState.Disconnected))) }
             }
        }
         else // ( Gatt_Failure , Gatt_Insufiicient_Authorization etc - почему-то фейлим)
         {
           gatt.close() // Клиент оторвался
           currentConnectionAttempt += 1
           couroutineScope.launch { data.emit(Resource.Loading(topLoadingMessage = "Восстанавливаем соединение", bottomLoadingMessage = "Попытка соединения $currentConnectionAttempt/$MAX_CONNECTION_ATTEMPT")) }

           if (currentConnectionAttempt <= MAX_CONNECTION_ATTEMPT)
           { startConnect() }
           else
           {  couroutineScope.launch { data.emit (Resource.Error (errorMessage = "Не удалось подключиться") ) } }
         }
}
//      2) Получить инфу про все сервисы, которые имеем, и запросить MTU (отправить запрос серверу, callback обработает onMtuChanged)
        // This function is called when the device reports on its available services.
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            with(gatt)
            {
                printGattTable()
                couroutineScope.launch { data.emit(Resource.Loading(topLoadingMessage = "Устанавливаем соединение"))  }
                requestMtu(517)
            }
        }
//      3) Get all the characteristics, depending on their properties Enable Notify/Indicate
        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            val characteristic = findCharacteristics(SERVICE_UUID, CHARACTERISTIC_UUID)
            if(characteristic == null) {
                couroutineScope.launch { data.emit(Resource.Error(errorMessage = "Не получилось найти устройство")) }
                return
            } // Кончаем с этой функцией
            enableNotification(characteristic)
    couroutineScope.launch { data.emit(Resource.Success(data = PullUpsResult(0, 0, ConnectionState.Connected),
            topSuccessMessage = "Соединение установлено", bottomSuccessMessage = "c ${gatt.device.name}",
            )) }

        }
//      4)
        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic)
        {
            with (characteristic) {    // scope funtion to avoid writing characteric.I'ts_properties
                when (uuid)  // when characteristic.getUUID()
                {
                    UUID.fromString(CHARACTERISTIC_UUID)  // Matches our defined CHARACTERISTIC_UUID private val
                        -> {

                        val isExerciseSelected = value[0]
                        // Extract the currentSet (single byte)
                        val currentSet = value[1].toInt() and 0xFF

                        // Extract the numberOfPullUps (two bytes)
                        val numberOfPullUps = value[2].toInt()

                        val result = PullUpsResult(
                                numberOfPullUps = numberOfPullUps,
                                currentSet = currentSet,
                                connectionState = ConnectionState.Connected
                        )

                        Log.d("BLE", "Received data: $result")
                        couroutineScope.launch {
                            data.emit(Resource.Success(topSuccessMessage = "Соединение установлено",
                                    bottomSuccessMessage = "с ${gatt.device.name}", data = result))
                        }
                    }

                    else -> Unit
                }
            }
        }

    }

    private fun findCharacteristics (serviceUUID: String, characteristicsUUID: String)
    : BluetoothGattCharacteristic?
    {
        return gatt?.services?.find { service -> service.uuid.toString() == serviceUUID }
                   ?.characteristics?.find { characteristics -> characteristics.uuid.toString() == characteristicsUUID }
    }
    private fun enableNotification  (characteristic: BluetoothGattCharacteristic)
    {
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        val payload = when {
//The payload is the specific data written to the CCCD, to change how notifications and indications work for a given client and characteristic combination
            characteristic.isIndicatable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            characteristic.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            else -> return
        } // depending on a property of CCCD we want payload either Enable_Notify or Enable_Indicate
        //Returns a descriptor with a given UUID out of the list of descriptors for this characteristic.
        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            if (gatt?.setCharacteristicNotification(characteristic, true) == false)
            {
                Log.d("BLEReceiveManager","set characteristics notification failed")
                return
            }
            writeDescription(cccdDescriptor, payload)
        }
    }
    private fun writeDescription(descriptor: BluetoothGattDescriptor, payload: ByteArray)
    {
        gatt?.let { gatt ->
            descriptor.value = payload
            gatt.writeDescriptor(descriptor)
        } ?: error("Not connected to a BLE device!")
    }
    private fun disconnectCharacteristic(characteristic: BluetoothGattCharacteristic)
    {
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            if(gatt?.setCharacteristicNotification(characteristic,false) == false){
                Log.d("TempHumidReceiveManager","set charateristics notification failed")
                return
            }
            writeDescription(cccdDescriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
        }
    }

    @SuppressLint("NewApi")
    fun writeNewExerciseStarted(newExerciseSelected: Boolean) {
        val characteristic = findCharacteristics(SERVICE_UUID, CHARACTERISTIC_UUID)
        val dataToSend = byteArrayOf(if (newExerciseSelected) 1 else 0)
        if (characteristic != null) {
            gatt?.writeCharacteristic(characteristic, dataToSend, WRITE_TYPE_NO_RESPONSE)
        }
    }

    override fun startConnect()
    {
        couroutineScope.launch {
            data.emit(Resource.Loading(topLoadingMessage = "Поиск устройства..."))
        } // emitting state "Загрузка..."
        isScanning = true            // Сканирование осуществляется - правда
        bleScanner.startScan(null, scanSettings, scanCallback) // Начинаем скан ()
    }
    override fun reConnect()
    {
        gatt?.connect()
    }
    override fun disConnect()
    {
        gatt?.disconnect()
    }
    override fun closeConnect()
    {
        bleScanner.stopScan(scanCallback)
        val characteristic = findCharacteristics(SERVICE_UUID, SERVICE_UUID)
        if (characteristic != null) {
            disconnectCharacteristic(characteristic)
        }
        gatt?.close()
    }


}

