package com.example.mumar.ledblinker

import android.app.Activity
import android.os.Bundle
import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.things.pio.PeripheralManagerService
import com.google.android.things.pio.Gpio
import android.content.ContentValues.TAG
import java.io.IOException
import android.content.ContentValues.TAG
import com.google.android.things.pio.GpioCallback
import android.content.ContentValues.TAG
import android.content.ContentValues.TAG
import android.content.ContentValues.TAG
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import android.content.ContentValues.TAG
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_SPACE
import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.Button.LogicState
import android.content.ContentValues.TAG
import android.content.ContentValues.TAG
import android.graphics.Color
import com.google.android.things.contrib.driver.ht16k33.Ht16k33
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat
import com.google.android.things.contrib.driver.apa102.Apa102




/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class HomeActivity : Activity() {
    private val TAG = "HomeActivity"
    private val BUTTON_PIN_NAME = "GPIO_174"
    private val LED_PIN_NAME = "GPIO_34"
    private val LED_PIN_NAME2 = "GPIO_33"
    private val LED_PIN_NAME3 = "GPIO_32"

    private var mButtonInputDriver: ButtonInputDriver? = null
    // GPIO connection to button input
    private var mButtonGpio: Gpio? = null
    private var mLedGpio: Gpio? = null
    private var mLedGpio2: Gpio? = null
    private var mLedGpio3: Gpio? = null

    private val mCallback = object : GpioCallback() {
        override fun onGpioEdge(gpio: Gpio?): Boolean {
            try {
                val buttonValue = gpio?.getValue()
                buttonValue?.let {
                    mLedGpio?.setValue(it)
                }
            } catch (e: IOException) {
                Log.w(TAG, "Error reading GPIO")
            }


            // Return true to keep callback active.
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val service = PeripheralManagerService()
        Log.d(TAG, "Available GPIO: " + service.gpioList)

        try {
            // Initialize button driver to emit SPACE key events
            mButtonInputDriver = ButtonInputDriver(
                    BUTTON_PIN_NAME,
                    Button.LogicState.PRESSED_WHEN_LOW,
                    KeyEvent.KEYCODE_SPACE)
            // Register with the framework
            mButtonInputDriver?.register()
        } catch (e: IOException) {
            Log.e(TAG, "Error opening button driver", e)
        }


        try {
//            // Create GPIO connection.
//            mButtonGpio = service.openGpio(BUTTON_PIN_NAME)
//
//            // Configure as an input, trigger events on every change.
//            mButtonGpio?.setDirection(Gpio.DIRECTION_IN)
//            mButtonGpio?.setEdgeTriggerType(Gpio.EDGE_BOTH)
//            // Value is true when the pin is LOW
//            mButtonGpio?.setActiveType(Gpio.ACTIVE_LOW)

            mLedGpio = service.openGpio(LED_PIN_NAME);
            // Configure as an output.
            mLedGpio?.setDirection(Gpio.ACTIVE_HIGH);

            mLedGpio2 = service.openGpio(LED_PIN_NAME2);
            // Configure as an output.
            mLedGpio2?.setDirection(Gpio.ACTIVE_HIGH);

            mLedGpio3 = service.openGpio(LED_PIN_NAME3);
            // Configure as an output.
            mLedGpio3?.setDirection(Gpio.ACTIVE_HIGH);

//            mButtonGpio?.registerGpioCallback(mCallback)
        } catch (e: IOException) {
            Log.w(TAG, "Error opening GPIO", e)
        }

        val segment = RainbowHat.openDisplay()
        segment.setBrightness(Ht16k33.HT16K33_BRIGHTNESS_MAX)
        segment.display("WIN!")
        segment.setEnabled(true)
// Close the device when done.
        segment.close()


    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister the driver and close
        if (mButtonInputDriver != null) {
            mButtonInputDriver?.unregister()
            try {
                mButtonInputDriver?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error closing Button driver", e)
            }

        }

        // Close the button
//        if (mButtonGpio != null) {
//            mButtonGpio?.unregisterGpioCallback(mCallback)
//            try {
//                mButtonGpio?.close()
//            } catch (e: IOException) {
//                Log.w(TAG, "Error closing GPIO", e)
//            }
//
//        }

        if (mLedGpio != null) {
            try {
                mLedGpio?.close()
                mLedGpio2?.close()
                mLedGpio3?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error closing GPIO", e)
            }

        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            // Turn on the LED
            setLedValue(true)
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            // Turn off the LED
            setLedValue(false)
            return true
        }

        return super.onKeyUp(keyCode, event)
    }

    /**
     * Update the value of the LED output.
     */
    private fun setLedValue(value: Boolean) {
        try {
            mLedGpio?.value = value
            mLedGpio2?.value = value
            mLedGpio3?.value = value
        } catch (e: IOException) {
            Log.e(TAG, "Error updating GPIO value", e)
        }

    }
}
