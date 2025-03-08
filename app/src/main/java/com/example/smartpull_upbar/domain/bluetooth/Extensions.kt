@file:Suppress("SpellCheckingInspection")

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.util.Log
import java.util.Locale

// Extension function - function, which basically acts as it was already in class. We "extend" the class
// cause it was insufficient. And then can use it, pretending it was already in API
const val CCCD_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805F9B34FB"

// Характеристики
fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean =
        properties and property != 0
// Bitwise проверка на каждое свойство Характеристики с помощью: bitmask & [current]property
fun BluetoothGattCharacteristic.isReadable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_READ) // 2
fun BluetoothGattCharacteristic.isWritableWithoutResponse(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) // 4
fun BluetoothGattCharacteristic.isWritable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE) // 8
fun BluetoothGattCharacteristic.isNotifiable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_NOTIFY) // 16
fun BluetoothGattCharacteristic.isIndicatable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_INDICATE) // 32
// Напечатать свойства характеристик
fun BluetoothGattCharacteristic.printProperties(): String
        = mutableListOf<String>().apply {
    if ( isReadable() ) add ("READABLE")
    if ( isWritable() ) add ("WRITABLE")
    if ( isWritableWithoutResponse() ) add ("WRITABLE WITHOUT RESPONSE")
    if ( isIndicatable() ) add ("INDICATABLE")
    if ( isNotifiable() ) add ("NOTIFIABLE")
    if (isEmpty()) add ("EMPTY")
}.joinToString() // всё что "add" в одну строчку

// Дескрипторы
fun BluetoothGattDescriptor.containsPermission(permission: Int): Boolean =
        permissions and permission != 0
// Bitwise проверка на каждое свойство Дескриптора с помощью: bitmask & [current]property
fun BluetoothGattDescriptor.isReadable(): Boolean =
        containsPermission(BluetoothGattDescriptor.PERMISSION_READ)
fun BluetoothGattDescriptor.isWritable(): Boolean =
        containsPermission(BluetoothGattDescriptor.PERMISSION_WRITE)
// Напечатать свойства дескрипторов
fun BluetoothGattDescriptor.printProperties(): String = mutableListOf<String>().apply {
    if (isReadable()) add("READABLE")
    if (isWritable()) add("WRITABLE")
    if (isEmpty()) add("EMPTY")
}.joinToString()

// Вывести информацию
fun BluetoothGatt.printGattTable() {
    if (services.isEmpty()) {
        Log.d("BluetoothGatt","No service and characteristic available, call discoverServices() first?")
        return
    }

    services.forEach { service -> // for each service
        val characteristicsTable = service.characteristics // assign its characteristic to characteristicTable variable
        .joinToString (separator = "\n|--", prefix = "|--") // the separated newlined string
        { characteristic ->                                // of all characteristic
            var description = "${characteristic.uuid}: ${characteristic.printProperties()}"

            if (characteristic.descriptors.isNotEmpty())   // This checks if the characteristic has any descriptors (true/false)
            {
                description += "\n" + characteristic.descriptors.joinToString(
                        separator = "\n|------",
                        prefix = "|------"
                ) { descriptor ->
                    "${descriptor.uuid}: ${descriptor.printProperties()}"
                }
            }
            description
        }
        Log.d("BluetoothGatt","Service ${service.uuid}\nCharacteristics:\n$characteristicsTable")
    }
}

fun BluetoothGattDescriptor.isCccd() =
        uuid.toString().uppercase(Locale.US) == CCCD_DESCRIPTOR_UUID.uppercase(Locale.US)
fun ByteArray.toHexString(): String =
        joinToString(separator = " ", prefix = "0x") { String.format("%02X", it) }