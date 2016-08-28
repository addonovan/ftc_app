/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Austin Donovan (addonovan)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package addonovan.kftc.config

import addonovan.kftc.Activity
import addonovan.kftc.UtilityContainer
import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.robotcore.internal.TelemetryImpl

/**
 * A fake HardwareMap class for the sole purpose of faking a configuration map for
 * the purposed of configuration.
 *
 * @author addonovan
 * @since 8/28/16
 */
internal class FalseHardwareMap private constructor() : HardwareMap( Activity )
{

    companion object
    {
        /** FalseHardwareMap used to spoof the utility container. */
        private val hardwareMap = FalseHardwareMap();

        /** Gamepad used to spoof the utility container. */
        private val gamepad = Gamepad();

        /** Telemetry used to spoof the utility container. */
        private val telemetry = TelemetryImpl( null );

        /**
         * Spoofs the utility container with false information.
         */
        fun spoofUtilityContainer()
        {
            UtilityContainer.HardwareMap = hardwareMap;
            UtilityContainer.Gamepad1 = gamepad;
            UtilityContainer.Gamepad2 = gamepad;
            UtilityContainer.Telemetry = telemetry;
        }


    }

    //
    // The Land of blank interface implementations
    //

    // formatter:off

    private val emptyMotorController = object : DcMotorController
    {
        override fun getConnectionInfo(): String = "";
        override fun resetDeviceConfigurationForOpMode() {}
        override fun getDeviceName(): String = "";
        override fun getVersion(): Int = 0;
        override fun close() {}
        override fun getManufacturer(): HardwareDevice.Manufacturer? = null;
        override fun setMotorMaxSpeed(motor: Int, encoderTicksPerSecond: Int) {}
        override fun setMotorPower(motor: Int, power: Double) {}
        override fun isBusy(motor: Int): Boolean = false;
        override fun getMotorMaxSpeed(motor: Int): Int = 0;
        override fun getMotorPowerFloat(motor: Int): Boolean = false;
        override fun setMotorTargetPosition(motor: Int, position: Int) {}
        override fun setMotorMode(motor: Int, mode: DcMotor.RunMode?) {}
        override fun setMotorZeroPowerBehavior(motor: Int, zeroPowerBehavior: DcMotor.ZeroPowerBehavior?) {}
        override fun getMotorCurrentPosition(motor: Int): Int = 0;
        override fun getMotorZeroPowerBehavior(motor: Int): DcMotor.ZeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE;
        override fun getMotorPower(motor: Int): Double = 0.0;
        override fun getMotorMode(motor: Int): DcMotor.RunMode = DcMotor.RunMode.RUN_TO_POSITION;
        override fun getMotorTargetPosition(motor: Int): Int = 0;
    }

    private val emptyServoController = object : ServoController
    {
        override fun resetDeviceConfigurationForOpMode() {}
        override fun getManufacturer(): HardwareDevice.Manufacturer? = null;
        override fun pwmEnable() {}
        override fun pwmDisable() {}
        override fun getPwmStatus() = null;
        override fun setServoPosition( i: Int, v: Double ) {}
        override fun getServoPosition( i: Int ) = 0.0;
        override fun getDeviceName() = null;
        override fun getConnectionInfo() = null;
        override fun getVersion() = 0;
        override fun close() {}
    }

    private val emptyLegacyModule = object : LegacyModule
    {
        override fun enableI2cWriteMode(physicalPort: Int, i2cAddress: I2cAddr?, memAddress: Int, length: Int) {}
        override fun enableI2cReadMode(physicalPort: Int, i2cAddress: I2cAddr?, memAddress: Int, length: Int) {}
        override fun readAnalogVoltage(physicalPort: Int): Double = 0.0;
        override fun getMaxAnalogInputVoltage(): Double = 0.0;
        override fun readAnalogRaw(physicalPort: Int): ByteArray = ByteArray( 0 );
        override fun resetDeviceConfigurationForOpMode() {}
        override fun getManufacturer(): HardwareDevice.Manufacturer? = null;
        override fun isArmed(): Boolean = false;
        override fun clearI2cPortActionFlag(p0: Int) {}
        override fun enableAnalogReadMode( i: Int ) {}
        override fun enable9v( i: Int, b: Boolean ) {}
        override fun setDigitalLine( i: Int, i1: Int, b: Boolean ) {}
        override fun getSerialNumber() = null;
        override fun getCopyOfReadBuffer( i: Int ) = ByteArray( 0 );
        override fun getCopyOfWriteBuffer( i: Int ) = ByteArray( 0 );
        override fun copyBufferIntoWriteBuffer( i: Int, bytes: ByteArray ) {}
        override fun setI2cPortActionFlag( i: Int ) {}
        override fun isI2cPortActionFlagSet( i: Int ) = false;
        override fun readI2cCacheFromController( i: Int ) {}
        override fun writeI2cCacheToController( i: Int ) {}
        override fun writeI2cPortFlagOnlyToController( i: Int ) {}
        override fun isI2cPortInReadMode( i: Int ) = false;
        override fun isI2cPortInWriteMode( i: Int ) = false;
        override fun isI2cPortReady( i: Int ) = false;
        override fun getI2cReadCacheLock( i: Int ) = null;
        override fun getI2cWriteCacheLock( i: Int ) = null;
        override fun getI2cReadCache( i: Int ) = ByteArray( 0 );
        override fun getI2cWriteCache( i: Int ) = ByteArray( 0 );
        override fun registerForI2cPortReadyCallback( i2cPortReadyCallback: I2cController.I2cPortReadyCallback, i: Int ) {}
        override fun getI2cPortReadyCallback( i: Int ) = null;
        override fun deregisterForPortReadyCallback( i: Int ) {}
        override fun registerForPortReadyBeginEndCallback( i2cPortReadyBeginEndNotifications: I2cController.I2cPortReadyBeginEndNotifications, i: Int ) {}
        override fun getPortReadyBeginEndCallback( i: Int ) = null;
        override fun deregisterForPortReadyBeginEndCallback( i: Int ) {}
        @Deprecated( "required" )
        override fun readI2cCacheFromModule( i: Int ) {}
        @Deprecated( "required" )
        override fun writeI2cCacheToModule( i: Int ) {}
        @Deprecated( "required" )
        override fun writeI2cPortFlagOnlyToModule( i: Int ) {}
        override fun getDeviceName() = null;
        override fun getConnectionInfo() = null;
        override fun getVersion() = 0;
        override fun close() {}
    }

    private val emptyTouchSensorMultiplexer = object : TouchSensorMultiplexer
    {
        override fun resetDeviceConfigurationForOpMode() {}
        override fun getManufacturer(): HardwareDevice.Manufacturer? = null;
        override fun isTouchSensorPressed( i: Int ) = false;
        override fun getSwitches() = 0;
        override fun getDeviceName() = null;
        override fun getConnectionInfo() = null;
        override fun getVersion() = 0;
        override fun close() {}
    }

    private val emptyDeviceInterfaceModule = object : DeviceInterfaceModule
    {
        override fun getAnalogInputVoltage(channel: Int): Double = 0.0;
        override fun enableI2cWriteMode(physicalPort: Int, i2cAddress: I2cAddr?, memAddress: Int, length: Int) {}
        override fun enableI2cReadMode(physicalPort: Int, i2cAddress: I2cAddr?, memAddress: Int, length: Int) {}
        override fun getMaxAnalogInputVoltage(): Double = 0.0;
        override fun resetDeviceConfigurationForOpMode() {}
        override fun getManufacturer(): HardwareDevice.Manufacturer? = null;
        override fun isArmed(): Boolean = false;
        override fun clearI2cPortActionFlag(p0: Int) {}
        override fun getDigitalInputStateByte() = 0;
        override fun setDigitalIOControlByte( b: Byte ) {}
        override fun getDigitalIOControlByte() = 0.toByte();
        override fun setDigitalOutputByte( b: Byte ) {}
        override fun getDigitalOutputStateByte() = 0.toByte();
        override fun getLEDState( i: Int ) = false;
        override fun setLED( i: Int, b: Boolean ) {}
        override fun getSerialNumber() = null;
        override fun setAnalogOutputVoltage( i: Int, i1: Int ) {}
        override fun setAnalogOutputFrequency( i: Int, i1: Int ) {}
        override fun setAnalogOutputMode( i: Int, b: Byte ) {}
        override fun getDigitalChannelMode( i: Int ) = null;
        override fun setDigitalChannelMode( i: Int, mode: DigitalChannelController.Mode ) {}
        override fun getDigitalChannelState( i: Int ) = false;
        override fun setDigitalChannelState( i: Int, b: Boolean ) {}
        override fun getCopyOfReadBuffer( i: Int ) = ByteArray( 0 );
        override fun getCopyOfWriteBuffer( i: Int ) = ByteArray( 0 );
        override fun copyBufferIntoWriteBuffer( i: Int, bytes: ByteArray ) {}
        override fun setI2cPortActionFlag( i: Int ) {}
        override fun isI2cPortActionFlagSet( i: Int ) = false;
        override fun readI2cCacheFromController( i: Int ) {}
        override fun writeI2cCacheToController( i: Int ) {}
        override fun writeI2cPortFlagOnlyToController( i: Int ) {}
        override fun isI2cPortInReadMode( i: Int ) = false;
        override fun isI2cPortInWriteMode( i: Int ) = false;
        override fun isI2cPortReady( i: Int ) = false;
        override fun getI2cReadCacheLock( i: Int ) = null;
        override fun getI2cWriteCacheLock( i: Int ) = null;
        override fun getI2cReadCache( i: Int ) = ByteArray( 0 );
        override fun getI2cWriteCache( i: Int ) = ByteArray( 0 );
        override fun registerForI2cPortReadyCallback( i2cPortReadyCallback: I2cController.I2cPortReadyCallback, i: Int ) {}
        override fun getI2cPortReadyCallback( i: Int ) = null;
        override fun deregisterForPortReadyCallback( i: Int ) {}
        override fun registerForPortReadyBeginEndCallback( i2cPortReadyBeginEndNotifications: I2cController.I2cPortReadyBeginEndNotifications, i: Int ) {}
        override fun getPortReadyBeginEndCallback( i: Int ) = null;
        override fun deregisterForPortReadyBeginEndCallback( i: Int ) {}
        @Deprecated( "required" )
        override fun readI2cCacheFromModule( i: Int ) {}
        @Deprecated( "required" )
        override fun writeI2cCacheToModule( i: Int ) {}
        @Deprecated( "required" )
        override fun writeI2cPortFlagOnlyToModule( i: Int ) {}
        override fun setPulseWidthOutputTime( i: Int, i1: Int ) {}
        override fun setPulseWidthPeriod( i: Int, i1: Int ) {}
        override fun getPulseWidthOutputTime( i: Int ) = 0;
        override fun getPulseWidthPeriod( i: Int ) = 0;
        override fun getDeviceName() = null;
        override fun getConnectionInfo() = null;
        override fun getVersion() = 0;
        override fun close() {}
    }

    private val emptyOpticalDistanceSensor = object : OpticalDistanceSensor
    {
        override fun getRawLightDetected(): Double = 0.0;
        override fun getRawLightDetectedMax(): Double = 0.0;
        override fun resetDeviceConfigurationForOpMode() {}
        override fun getManufacturer(): HardwareDevice.Manufacturer? = null;
        override fun getLightDetected() = 0.0;
        override fun enableLed( b: Boolean ) {}
        override fun status() = null;
        override fun getDeviceName() = null;
        override fun getConnectionInfo() = null;
        override fun getVersion() = 0;
        override fun close() {}
    }

    private val emptyTouchSensor = object : TouchSensor
    {
        override fun resetDeviceConfigurationForOpMode() {}
        override fun getManufacturer(): HardwareDevice.Manufacturer? = null;
        override fun getValue() = 0.0;
        override fun isPressed() = false;
        override fun getDeviceName() = null;
        override fun getConnectionInfo() = null;
        override fun getVersion() = 0;
        override fun close() {}
    }

    private val emptyColorSensor = object : ColorSensor
    {
        override fun getI2cAddress(): I2cAddr? = null;
        override fun setI2cAddress(newAddress: I2cAddr?) {}
        override fun resetDeviceConfigurationForOpMode() {}
        override fun getManufacturer(): HardwareDevice.Manufacturer? = null;
        override fun red() = 0;
        override fun green() = 0;
        override fun blue() = 0;
        override fun alpha() = 0;
        override fun argb() = 0;
        override fun enableLed( b: Boolean ) {}
        override fun getDeviceName() = null;
        override fun getConnectionInfo() = null;
        override fun getVersion() = 0;
        override fun close() {}
    }

    private val emptyDigitalChannelController = object : DigitalChannelController
    {
        override fun resetDeviceConfigurationForOpMode() {}
        override fun getManufacturer(): HardwareDevice.Manufacturer? = null;
        override fun getSerialNumber() = null;
        override fun getDigitalChannelMode( i: Int ) = null;
        override fun setDigitalChannelMode( i: Int, mode: DigitalChannelController.Mode ) {}
        override fun getDigitalChannelState( i: Int ) = false;
        override fun setDigitalChannelState( i: Int, b: Boolean ) {}
        override fun getDeviceName() = null;
        override fun getConnectionInfo() = null;
        override fun getVersion() = 0;
        override fun close() {}
    }

    private val emptyAccelerationSensor = object : AccelerationSensor
    {
        override fun resetDeviceConfigurationForOpMode() {}
        override fun getManufacturer(): HardwareDevice.Manufacturer? = null;
        override fun getAcceleration() = null;
        override fun status() = null;
        override fun getDeviceName() = null;
        override fun getConnectionInfo() = null;
        override fun getVersion() = 0;
        override fun close() {}
    }

    private val emptyCompassSensor = object : CompassSensor
    {
        override fun resetDeviceConfigurationForOpMode() {}
        override fun getManufacturer(): HardwareDevice.Manufacturer? = null;
        override fun getDirection() = 0.0;
        override fun status() = null;
        override fun setMode( compassMode: CompassSensor.CompassMode ) {}
        override fun calibrationFailed() = false;
        override fun getDeviceName() = null;
        override fun getConnectionInfo() = null;
        override fun getVersion() = 0;
        override fun close() {}
    }

    private val emptyGyroSensor = object : GyroSensor
    {
        override fun getRotationFraction(): Double = 0.0;
        override fun resetDeviceConfigurationForOpMode() {}
        override fun getManufacturer(): HardwareDevice.Manufacturer? = null;
        override fun calibrate() {}
        override fun isCalibrating() = false;
        override fun getHeading() = 0;
        override fun rawX() = 0;
        override fun rawY() = 0;
        override fun rawZ() = 0;
        override fun resetZAxisIntegrator() {}
        override fun status() = null;
        override fun getDeviceName() = null;
        override fun getConnectionInfo() = null;
        override fun getVersion() = 0;
        override fun close() {}
    }

    private val emptyIrSeekerSensor = object : IrSeekerSensor
    {
        override fun getI2cAddress(): I2cAddr? = null;
        override fun setI2cAddress(newAddress: I2cAddr?) {}
        override fun resetDeviceConfigurationForOpMode() {}
        override fun getManufacturer(): HardwareDevice.Manufacturer? = null;
        override fun setSignalDetectedThreshold( v: Double ) {}
        override fun getSignalDetectedThreshold() = 0.0;
        override fun setMode( mode: IrSeekerSensor.Mode ) {}
        override fun getMode() = null;
        override fun signalDetected() = false;
        override fun getAngle() = 0.0;
        override fun getStrength() = 0.0;
        override fun getIndividualSensors() = arrayOfNulls< IrSeekerSensor.IrSeekerIndividualSensor >( 0 );
        override fun getDeviceName() = null;
        override fun getConnectionInfo() = null;
        override fun getVersion() = 0;
        override fun close() {}
    }

    private val emptyLightSensor = object : LightSensor
    {
        override fun getRawLightDetected(): Double = 0.0;
        override fun getRawLightDetectedMax(): Double = 0.0;
        override fun resetDeviceConfigurationForOpMode() {}
        override fun getManufacturer(): HardwareDevice.Manufacturer? = null;
        override fun getLightDetected() = 0.0;
        override fun enableLed( b: Boolean ) {}
        override fun status() = null;
        override fun getDeviceName() = null;
        override fun getConnectionInfo() = null;
        override fun getVersion() = 0;
        override fun close() {}
    }

    private val emptyUltrasonicSensor = object : UltrasonicSensor
    {
        override fun resetDeviceConfigurationForOpMode() {}
        override fun getManufacturer(): HardwareDevice.Manufacturer? = null;
        override fun getUltrasonicLevel() = 0.0;
        override fun status() = null;
        override fun getDeviceName() = null;
        override fun getConnectionInfo() = null;
        override fun getVersion() = 0;
        override fun close() {}
    }

    private val emptyVoltageSensor = object : VoltageSensor
    {
        override fun resetDeviceConfigurationForOpMode() {}
        override fun getManufacturer(): HardwareDevice.Manufacturer? = null;
        override fun getVoltage() = 0.0;
        override fun getDeviceName() = null;
        override fun getConnectionInfo() = null;
        override fun getVersion() = 0;
        override fun close() {}
    }

    // formatter:on

    //
    // Constructors
    //

    init
    {
        dcMotorController = FalseDeviceMapping< DcMotorController >( emptyMotorController );
        dcMotor = FalseDeviceMapping( DcMotorImpl( emptyMotorController, 0 ) );
        servoController = FalseDeviceMapping< ServoController >( emptyServoController );
        servo = FalseDeviceMapping( ServoImpl( emptyServoController, 0 ) );
        legacyModule = FalseDeviceMapping< LegacyModule >( emptyLegacyModule );
        touchSensorMultiplexer = FalseDeviceMapping< TouchSensorMultiplexer >( emptyTouchSensorMultiplexer );
        deviceInterfaceModule = FalseDeviceMapping< DeviceInterfaceModule >( emptyDeviceInterfaceModule );
        analogInput = FalseDeviceMapping( AnalogInput( null, 0 ) );
        digitalChannel = FalseDeviceMapping( DigitalChannel( null, 0 ) );
        opticalDistanceSensor = FalseDeviceMapping< OpticalDistanceSensor >( emptyOpticalDistanceSensor );
        touchSensor = FalseDeviceMapping< TouchSensor >( emptyTouchSensor );
        pwmOutput = FalseDeviceMapping( PWMOutputImpl( null, 0 ) );
        i2cDevice = FalseDeviceMapping( I2cDeviceImpl( null, 0 ) );
        analogOutput = FalseDeviceMapping( AnalogOutput( null, 0 ) );
        colorSensor = FalseDeviceMapping< ColorSensor >( emptyColorSensor );
        led = FalseDeviceMapping( LED( emptyDigitalChannelController, 0 ) );
        accelerationSensor = FalseDeviceMapping< AccelerationSensor >( emptyAccelerationSensor );
        compassSensor = FalseDeviceMapping< CompassSensor >( emptyCompassSensor );
        gyroSensor = FalseDeviceMapping< GyroSensor >( emptyGyroSensor );
        irSeekerSensor = FalseDeviceMapping< IrSeekerSensor >( emptyIrSeekerSensor );
        lightSensor = FalseDeviceMapping< LightSensor >( emptyLightSensor );
        ultrasonicSensor = FalseDeviceMapping< UltrasonicSensor >( emptyUltrasonicSensor );
        voltageSensor = FalseDeviceMapping< VoltageSensor >( emptyVoltageSensor );
    }

    /**
     * A false device mapping that will always return only one variable.
     *
     * @param[instance]
     *          The instance to always return for this map.
     */
    private inner class FalseDeviceMapping< T : HardwareDevice >( private val instance: T ) : DeviceMapping< T >()
    {
        override fun get( deviceName: String ) = instance;
    }

}
