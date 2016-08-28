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
package addonovan.kftc.hardware

import addonovan.kftc.*
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.HardwareMap.DeviceMapping
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * !Description!
 *
 * @author addonovan
 * @since 8/27/16
 */

/** Type alias for satan. */
private class DeviceClassMap : HashMap< Class< out HardwareDevice >, DeviceMapping< out HardwareDevice > >()
{
    inline fun < reified T : HardwareDevice > addEntry( mapping: DeviceMapping< out T > ) = put( T::class.java, mapping );
}

/**
 * This exists for logging and keeping track of device class maps.
 */
private object HardwareMapExtension : ILog by getLog( HardwareMapExtension::class )
{

    /** Maps the deviceClassMap to the HardwareMap. */
    private val deviceClassMapMap = HashMap< HardwareMap, DeviceClassMap >();

    /**
     * Gets (or creates) the single DeviceClassMap for the instance of the hardware map given.
     *
     * @param[hardwareMap]
     *          The hardware map to fetch an instance of a DeviceClassMap for.
     * @return The instance of the DeviceClassMap for the hardware map.
     */
    fun getDeviceClassMap( hardwareMap: HardwareMap ): DeviceClassMap
    {
        if ( !deviceClassMapMap.containsKey( hardwareMap ) )
        {
            deviceClassMapMap[ hardwareMap ] = DeviceClassMap();
        }

        return deviceClassMapMap[ hardwareMap ]!!;
    }

}

/** A map of the base hardware class versus the  */
private val HardwareMap.deviceClassMap: DeviceClassMap
    get()
    {
        val map = HardwareMapExtension.getDeviceClassMap( this );

        // initialize the map the first time around
        if ( map.isEmpty() )
        {
            map.addEntry( dcMotorController );
            map.addEntry( dcMotor );
            map.addEntry( servoController );
            map.addEntry( servo );
            map.addEntry( legacyModule );
            map.addEntry( touchSensorMultiplexer );
            map.addEntry( deviceInterfaceModule );
            map.addEntry( analogInput );
            map.addEntry( digitalChannel );
            map.addEntry( opticalDistanceSensor );
            map.addEntry( touchSensor );
            map.addEntry( pwmOutput );
            map.addEntry( i2cDevice );
            map.addEntry( analogOutput );
            map.addEntry( colorSensor );
            map.addEntry( led );
            map.addEntry( accelerationSensor );
            map.addEntry( compassSensor );
            map.addEntry( gyroSensor );
            map.addEntry( irSeekerSensor );
            map.addEntry( lightSensor );
            map.addEntry( ultrasonicSensor );
            map.addEntry( voltageSensor );
        }

        return map;
    }

/**
 * The world's shittiest guess and check! This makes it slightly nicer looking for
 * java users to actually use the method, but it's far less efficient. Oh well.
 *
 * @param[name]
 *          The name of the device to fetch.
 */
@Suppress( "unchecked_cast" )
fun < T : HardwareDevice > HardwareMap.getDeviceByGuess( name: String ): T
{
    ClassFinder.HardwareExtensions.forEach { extensionClass ->
        try
        {
            return getDeviceByType( extensionClass as Class< out HardwareDevice >, name ) as T;
        }
        catch ( e: Exception ){} // ignore these exceptions, this is trial by error after all
    };

    throw NullPointerException( "No Hardware Device with the name '$name' with the required type!" );
}

/**
 * Gets the correct device from the Hardware device mappings in the HardwareMap by
 * using the type of HardwareDevice to find the correct hardware device mapping
 * and the name to get the correct value out of it.
 *
 * @param[type]
 *          The type of hardware device to find.
 * @param[name]
 *          The name of the hardware device to return.
 *
 * @return The hardware device with the given type and name.
 *
 * @throws IllegalArgumentException
 *          If [type] had no DeviceMapping associated with it.
 * @throws NullPointerException
 *          If there was no entry for [name] with the type [type].
 * @throws IllegalAnnotationValueException
 *          If the [type] class had a [HardwareExtension] annotation on it that had
 *          an invalid value (that is, one that doesn't have a value in the backing
 *          map of classes and `DeviceMapping`s) assigned to the `hardwareMapType`
 *          parameter.
 *          This should *never* happen to an end-user, that would mean that the
 *          developer who wrote the extension did so wrongly.
 * @throws IllegalClassSetupException
 *          If [type] is a [HardwareExtension] that has been insufficiently
 *          set up, which could be for a multitude of reasons, check the
 *          error message if this occurs for more details.
 */
@Suppress( "unchecked_cast" )
fun HardwareMap.getDeviceByType( type: Class< out HardwareDevice >, name: String ): HardwareDevice
{
    var baseType = type;
    val isExtension = type.isHardwareExtension();
    var constructor: Constructor< out HardwareDevice >? = null;

    if ( isExtension )
    {
        constructor = findExtensionConstructor( type );
        baseType = type.getHardwareMapType().java;
    }
    else
    {
        // keep going up the hierarchy for hardware devices until:
        // 1. the base type is in the class map
        // 2. the base type's superclass is HardwareDevice itself
        //
        // after the loop breaks we know that:
        // if there's a key for "baseType" that we can find the correct device mapping for the type
        // otherwise, we need to throw an exception because there's no map for the given type

        while ( !deviceClassMap.containsKey( baseType ) && baseType.superclass != HardwareDevice::class.java )
        {
            baseType = baseType.superclass as Class< out HardwareDevice >;
        }
    }

    // the second condition was met, but the device is still not a
    // direct descendant of anything in the hardware device mappings
    // so we can't find the correct map for it
    if ( !deviceClassMap.containsKey( baseType ) )
    {
        HardwareMapExtension.e( "Can't find a device mapping for the given type: ${type.name}" );
        HardwareMapExtension.e( "This means that there's no way to get the hardware device from the hardware map!" );
        throw IllegalArgumentException( "No hardware device mapping for type: ${type.canonicalName}!" );
    }

    val deviceMapping = deviceClassMap[ type ]!!; // we're ensured that one exists by the previous block

    try
    {
        val device = deviceMapping[ name ]!!;

        if ( isExtension )
        {
            // if it's an extension, create the extension object and return it

            // 1 parameter is just the hardware device
            if ( constructor!!.typeParameters.size == 1 )
            {
                return constructor.newInstance( device );
            }
            // 2 parameters is the hardware device AND its name
            else if ( constructor.typeParameters.size == 2 )
            {
                return constructor.newInstance( device, name );
            }
            else
            {
                throw IllegalStateException( "huh but how?" ); // not descriptive, but will probably never occur
            }
        }
        else
        {
            // if it's not an extension, just return the value from the map
            return device;
        }
    }
    // catch an IllegalArgumentException from the deviceMapping.get() method
    catch ( ex: IllegalArgumentException )
    {
        HardwareMapExtension.e( "Failed to find the device by name $name!", ex );
        throw NullPointerException( "No device with the type ${type.simpleName} by the name \"$name\"" );
    }
    // catch the other exceptions
    catch ( ex: Throwable )
    {
        // if they're from the newInstance invocation
        if ( ex is InstantiationError ) throw IllegalStateException( "${type.canonicalName} is not instantiable!" );
        if ( ex is IllegalAccessException ) throw IllegalStateException( "${type.canonicalName}: constructor is inaccessible!" );
        // IllegalArgumentException can't happen as the constructor has already been chosen for the specified arguments

        if ( ex is InvocationTargetException ) HardwareMapExtension.e( "Exception while invoking HardwareExtension constructor: ${ex.javaClass.name}" );

        throw ex; // throw it again
    }
}

/**
 * Finds the correct Hardware extension constructor.
 *
 * @param[type]
 *          The hardware extension class.
 *
 * @return The constructor for the extension, if one exists.
 *
 * @throws IllegalAnnotationValueException
 *          If the [type] class had a [HardwareExtension] annotation on it that had
 *          an invalid value (that is, one that doesn't have a value in the backing
 *          map of classes and `DeviceMapping`s) assigned to the `hardwareMapType`
 *          parameter.
 *          This should *never* happen to an end-user, that would mean that the
 *          developer who wrote the extension did so wrongly.
 * @throws IllegalClassSetupException
 *          If [type] is a [HardwareExtension] that has been insufficiently
 *          set up, which could be for a multitude of reasons, check the
 *          error message if this occurs for more details.
 */
private fun HardwareMap.findExtensionConstructor( type: Class< out HardwareDevice > ): Constructor< out HardwareDevice >
{
    var constructor: Constructor< out HardwareDevice >? = null;

    // If the class is an @HardwareExtension, we can get the base type right out of the
    // annotation parameters

    val baseType = type.getHardwareMapType().java;

    // ensure the base type is a valid one
    if ( !deviceClassMap.containsKey( baseType ) )
    {
        throw IllegalAccessException( "The @HardwareExtension.hardwareMapType value for class \"${type.simpleName}\" isn't supported!" );
    }

    // ensure that there's a correct constructor for the extension class
    try
    {
        constructor = type.getConstructor( baseType );
    }
    catch ( nsme: NoSuchMethodException ) {}

    // try to select a more specific one, but fall back to the other one
    try
    {
        constructor = type.getConstructor( baseType, String::class.java );
    }
    catch ( nsme: NoSuchMethodException ) {}

    // if it's still null at this point
    if ( constructor == null )
    {
        throw IllegalStateException( "Failed to find a valid constructor in the hardware extension class: ${type.canonicalName}!" );
    }

    return constructor;
}