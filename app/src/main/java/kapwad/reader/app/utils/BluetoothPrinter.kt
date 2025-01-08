import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.UUID

class BluetoothPrinter(
    private val context: Context,
    private val macAddress: String
) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null

    // Standard SPP UUID for Bluetooth Serial Port Profile
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun connect(): Boolean {
        // Check if Bluetooth is available and enabled
        if (bluetoothAdapter == null) {
            println("Bluetooth not supported")
            return false
        }

        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled) {
            println("Bluetooth is not enabled")
            return false
        }

        // Check Bluetooth permissions
        if (!hasBluetoothPermissions()) {
            println("Bluetooth permissions not granted")
            return false
        }

        try {
            // Get the Bluetooth device by MAC address
            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress)

            // Create a socket connection with permission checks
            bluetoothSocket = if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED) {
                device.createRfcommSocketToServiceRecord(SPP_UUID)
            } else {
                println("Bluetooth Connect permission not granted")
                return false
            }

            // Connect with permission check
            bluetoothSocket?.connect()
            return true
        } catch (e: SecurityException) {
            println("Security exception during Bluetooth connection: ${e.message}")
            return false
        } catch (e: IOException) {
            println("Connection failed: ${e.message}")
            return false
        }
    }

    private fun hasBluetoothPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun printText(text: String): Boolean {
        if (bluetoothSocket == null || !bluetoothSocket!!.isConnected) {
            println("Not connected to printer")
            return false
        }

        try {
            // Verify permission before printing
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED) {
                println("Bluetooth Connect permission not granted")
                return false
            }

            // Get the output stream and write the text
            val outputStream = bluetoothSocket?.outputStream
            outputStream?.write(text.toByteArray())
            outputStream?.flush()
            println("Print job sent successfully")
            return true
        } catch (e: SecurityException) {
            println("Security exception during printing: ${e.message}")
            return false
        } catch (e: IOException) {
            println("Error printing: ${e.message}")
            return false
        }
    }

    fun disconnect(): Boolean {
        try {
            // Verify permission before disconnecting
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED) {
                println("Bluetooth Connect permission not granted")
                return false
            }

            bluetoothSocket?.close()
            println("Bluetooth connection closed")
            return true
        } catch (e: SecurityException) {
            println("Security exception during disconnection: ${e.message}")
            return false
        } catch (e: IOException) {
            println("Error closing connection: ${e.message}")
            return false
        }
    }

    // Example usage function
    fun printExample() {
        if (connect()) {
            // Example print commands (may need to be adjusted for XP-380PT)
            printText("\nHELLO WORLD\n")
            printText("Bluetooth Printing Test\n")
            printText("------------------------\n")

            // Don't forget to disconnect
            disconnect()
        }
    }
}

// Usage in an Android activity or service
class MainActivity : AppCompatActivity() {
    private val BLUETOOTH_PERMISSION_REQUEST_CODE = 1

    private fun requestBluetoothPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
            BLUETOOTH_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with Bluetooth operations
                val printer = BluetoothPrinter(this, "86:67:7A:E6:54:76")
                printer.printExample()
            } else {
                // Permission denied, handle accordingly
                println("Bluetooth permission was denied")
            }
        }
    }

    private fun printToXP380PT() {
        // Check and request permission if not granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED) {
            requestBluetoothPermission()
            return
        }

        // If permission is already granted
        val printer = BluetoothPrinter(this, "86:67:7A:E6:54:76")
        printer.printExample()
    }
}