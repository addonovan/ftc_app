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
package addonovan.kftc

import addonovan.kftc.config.Configurations
import addonovan.kftc.config.Profile
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry

/**
 * !Description!
 *
 * @author addonovan
 * @since 8/22/2016
 */
abstract class AbstractKOpMode : IConfigurable, ILog
{

    //
    // Annotation Data
    //

    /**
     * The name of the KOpMode as it is written in the annotation (either TeleOp or Autonomous).
     *
     * @throws IllegalArgumentException
     *          If the class has neither annotation.
     */
    val AnnotatedName: String by lazy()
    {
        if ( javaClass.isAnnotationPresent( TeleOp::class.java ) )
        {
            d( "Grabbing annotated name from TeleOp annotation" );

            return@lazy javaClass.getAnnotation( TeleOp::class.java )!!.name;
        }
        else if ( javaClass.isAnnotationPresent( Autonomous::class.java ) )
        {
            d( "Grabbing annotated name from Autonomous annotation" );

            return@lazy javaClass.getAnnotation( Autonomous::class.java )!!.name;
        }
        else
        {
            e( "Dude, there are no annotations on this class. Why?" );
            throw IllegalArgumentException( "No annotations (TeleOp or Autonomous) on OpMode!" );
        }
    }

    /**
     * The group of the KOpMode as it is written in the annotation (either TeleOp or Autonomous).
     *
     * @throws IllegalArgumentException
     *          If the class has neither annotation.
     */
    val AnnotatedGroup: String by lazy()
    {
        if ( javaClass.isAnnotationPresent( TeleOp::class.java ) )
        {
            d( "Grabbing annotated group from TeleOp annotation" );

            return@lazy javaClass.getAnnotation( TeleOp::class.java )!!.group;
        }
        else if ( javaClass.isAnnotationPresent( Autonomous::class.java ) )
        {
            d( "Grabbing annotated group from Autonomous annotation" );

            return@lazy javaClass.getAnnotation( Autonomous::class.java )!!.group;
        }
        else
        {
            e( "Dude, there are no annotations on this class. Why?" );
            throw IllegalArgumentException( "No annotations (TeleOp or Autonomous) on OpMode!" );
        }
    }

    //
    // UtilityContainer mirror
    //

    /**
     * Provides access to all of the hardware devices loaded.
     */
    val HardwareMap: HardwareMap
        get() = UtilityContainer.HardwareMap;

    /**
     * The first controller's digital representation.
     */
    val Gamepad1: Gamepad
        get() = UtilityContainer.Gamepad1;

    /**
     * The second controller's digital representation.
     */
    val Gamepad2: Gamepad
        get() = UtilityContainer.Gamepad2;

    /**
     * Telemetry object used to relay information back to the controller.
     */
    val Telemetry: Telemetry
        get() = UtilityContainer.Telemetry;

    //
    // IConfigurable
    //

    /**
     * The configuration profile for this opmode. Use of this
     * is generally discourages as the other `get` methods should
     * be used instead for simpler access.
     */
    override val ConfigProfile: Profile = Configurations.profileFor( javaClass.kotlin );

    //
    // ILog
    //

    /**
     * The tag used for logging. This follows the following format:
     * ```kotlin
     * "kftc.${YourOpModeClassName}.${ActiveProfileName}"
     * ```
     */
    override val LogTag: String = getLogTag( javaClass.kotlin, ConfigProfile.Name );

}
