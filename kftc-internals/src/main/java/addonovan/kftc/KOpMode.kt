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
import android.support.annotation.CallSuper
import android.text.Editable
import android.text.TextWatcher
import com.qualcomm.robotcore.eventloop.opmode.*

/**
 * A Kotlin based OpMode.
 *
 * This allows for direct access to the active profile's configuration, as it
 * already directly implements all of the [IConfigurable] methods.
 *
 * Another nice feature is that when a KOpMode is extended, when it is initialized
 * and running, the active profile will be displayed in brackets after the the
 * OpMode's name.
 *
 * @author addonovan
 * @since 8/21/16
 */
abstract class KOpMode : OpMode(), IConfigurable, ILog
{

    //
    // Vals
    //

    /** The name of the OpMode as it's registered with the system. */
    val Name: String by lazy()
    {
        javaClass.getAnnotation( TeleOp::class.java )?.name ?: javaClass.getAnnotation( Autonomous::class.java )!!.name;
    }

    /** The profile for the current OpMode (the subclass). */
    private val profile = Configurations.profileFor( javaClass.kotlin );

    /** If the system is being configured at the moment or not. */
    private val inConfig = System.getProperty( "kftc.inconfig", "false" ).toBoolean();

    //
    // OpMode
    //

    @CallSuper
    override fun init()
    {
        if ( inConfig ) return; // don't try to mess with the non-existent gui

        // force the label to always say the profile name as well
        OpModeLabel.addTextChangedListener( object : TextWatcher
        {
            override fun afterTextChanged( s: Editable? ){}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}

            override fun onTextChanged( s: CharSequence, start: Int, before: Int, count: Int )
            {
                val string = s.toString();

                // remove our listener whenever the OpMode is over
                if ( !string.contains( Name ) )
                {
                    OpModeLabel.removeTextChangedListener( this );
                }

                // if it doesn't have the configuration details, add them
                if ( !string.contains( "[" ) )
                {
                    val profileName = Configurations.profileFor( this@KOpMode.javaClass.kotlin );

                    // run on the ui thread so we don't get yelled at
                    Activity.runOnUiThread {
                        // when this is created, update the opmode label to also show
                        // the active variant

                        val text = "Op Mode: $Name [$profileName]";
                        this@KOpMode.v( "Updating OpMode label to read: \"$text\"" );

                        OpModeLabel.text = text;
                    }
                }
            }
        } );
    }

    //
    // IConfigurable
    //

    /**
     * Gets the boolean value from the active profile.
     *
     * @param[name]
     *          The name of the boolean.
     * @param[default]
     *          The default value of the boolean, if it isn't found.
     *          This can be excluded for a default value of `false`.
     *
     * @return The boolean value associated with the name, or the default
     *         if no value was found.
     */
    final override fun get( name: String, default: Boolean ) = profile[ name, default ];

    /**
     * Gets the long value from the active profile.
     *
     * @param[name]
     *          The name of the long.
     * @param[default]
     *          The default value of the long, if it isn't found.
     *          This can be excluded for a default value of `false`.
     *
     * @return The long value associated with the name, or the default
     *         if no value was found.
     */
    final override fun get( name: String, default: Long ) = profile[ name, default ];

    /**
     * Gets the double value from the active profile.
     *
     * @param[name]
     *          The name of the double.
     * @param[default]
     *          The default value of the double, if it isn't found.
     *          This can be excluded for a default value of `false`.
     *
     * @return The double value associated with the name, or the default
     *         if no value was found.
     */
    final override fun get( name: String, default: Double ) = profile[ name, default ];

    /**
     * Gets the string value from the active profile.
     *
     * @param[name]
     *          The name of the string.
     * @param[default]
     *          The default value of the string, if it isn't found.
     *          This can be excluded for a default value of `false`.
     *
     * @return The string value associated with the name, or the default
     *         if no value was found.
     */
    final override fun get( name: String, default: String ) = profile[ name, default ];

}
