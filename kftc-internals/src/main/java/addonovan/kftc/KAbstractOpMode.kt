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
import android.text.Editable
import android.text.TextWatcher
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry

/**
 * !Description!
 *
 * @author addonovan
 * @since 8/22/2016
 */
abstract class KAbstractOpMode : IConfigurable, ILog
{

    //
    // Constructor
    //

    init
    {
        // if we aren't configuring outselves, then add the [profile] text to
        // the end of the OpModeLabel
        if ( !System.getProperty( "kftc.inConfig", "false" ).toBoolean() )
        {
            val name = javaClass.getAnnotatedName();
            val profileName = Configurations.profileFor( javaClass ).Name;

            OpModeLabel.addTextChangedListener( object : TextWatcher
            {
                // unused
                override fun afterTextChanged( s: Editable? ){}
                override fun beforeTextChanged( s: CharSequence?, start: Int, count: Int, after: Int ){}

                // whenever the label updates, add the profile to the end
                override fun onTextChanged( s: CharSequence, start: Int, before: Int, count: Int )
                {
                    val text = s.toString();

                    // if the OpMode isn't running, remove this listener so we don't get bogged down
                    if ( !text.contains( name ) )
                    {
                        OpModeLabel.removeTextChangedListener( this );
                    }

                    // if it doesn't have the configuration details, add them
                    if ( !text.contains( "[" ) )
                    {
                        // run on the ui thread so we don't get yelled at
                        Activity.runOnUiThread {

                            // when this is created, update the OpMode label to also show the active profile
                            OpModeLabel.text = "OpMode: $name [$profileName]";
                        }
                    }
                }

            } );
        }
    }

    //
    // UtilityContainer mirror
    //

    /**
     * Provides access to all of the hardware devices loaded.
     */
    @Suppress( "unused" )
    val HardwareMap: HardwareMap
        get() = UtilityContainer.HardwareMap;

    /**
     * The first controller's digital representation.
     */
    @Suppress( "unused" )
    val Gamepad1: Gamepad
        get() = UtilityContainer.Gamepad1;

    /**
     * The second controller's digital representation.
     */
    @Suppress( "unused" )
    val Gamepad2: Gamepad
        get() = UtilityContainer.Gamepad2;

    /**
     * Telemetry object used to relay information back to the controller.
     */
    @Suppress( "unused" )
    val Telemetry: Telemetry
        get() = UtilityContainer.Telemetry;

    //
    // TaskManager Instance
    //

    /**
     * The [TaskManager] instance for this OpMode, used for handling
     * asynchronous tasks that would normally clutter up the [loop][KOpMode.loop]
     * and [runOpMode][KLinearOpMode.runOpMode] methods with an excess
     * of conditions.
     */
    @Suppress( "unused" )
    val TaskManager: TaskManager = TaskManager( this );

    //
    // IConfigurable
    //

    /**
     * The configuration profile for this opmode. Use of this
     * is generally discourages as the other `get` methods should
     * be used instead for simpler access.
     */
    override val ConfigProfile: Profile = Configurations.profileFor( javaClass );

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
