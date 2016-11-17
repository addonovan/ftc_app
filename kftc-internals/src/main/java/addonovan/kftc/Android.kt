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

import addonovan.kftc.config.ConfigActivity
import addonovan.kftc.config.Configurations
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import org.firstinspires.ftc.robotcore.internal.AppUtil

/**
 * File for assorted android access things.
 *
 * @author addonovan
 * @since 6/17/16
 */

/**
 * The current activity. Now with 100% less shit directly viewable!
 */
val activity: Activity
    get() = AppUtil.getInstance().activity;

/**
 * Shows a toast to the user.
 *
 * @param[message]
 *          The message to show.
 * @param[time]
 *          The time to show it for, defaults to [Toast.LENGTH_SHORT].
 */
fun showToast( message: String, time: Int = Toast.LENGTH_SHORT )
{
    activity.runOnUiThread {
        Toast.makeText(activity, message, time ).show();
    }
}

//
// View finding tricks
//

/** The com.qualcomm.ftcrobotcontroller.R.id class. */
private val rIdClass: Class< * > by lazy()
{
    android.util.Log.v( "ftcext.R", "Locating com.qualcomm.ftcrobotcontroller.R.id" );

    val rClass = Class.forName( "com.qualcomm.ftcrobotcontroller.R" )!!;
    val classes = rClass.declaredClasses;

    var idClass: Class< * >? = null; // find the idClass from the declared classes

    // search for the "id" class
    for ( clazz in classes )
    {
        if ( clazz.simpleName == "id" )
        {
            idClass = clazz;
            break;
        }
    }

    idClass!!;
}

/**
 * Gets the view from the Qualcomm `R.id` class that has the
 * given name.
 *
 * @param[name]
 *          The name of the field to get.
 * @return The View with the given name.
 */
private fun getView( name: String ): View
{
    val field = rIdClass.getDeclaredField( name );
    field.isAccessible = true; // just in case
    val viewId = field.get( null ) as Int; // should be static, so no instance is required

    return activity.findViewById( viewId );
}

/** The robot icon ImageView */
internal val robotIcon: ImageView by lazy()
{
    getView( "robotIcon" ) as ImageView;
}

/** The label for the current OpMode.s */
internal val opModeLabel: TextView by lazy()
{
    getView( "textOpMode" ) as TextView;
}

//
// Robot Icon Hooks
//

/**
 * !!DO NOT CALL THIS!!
 *
 * Adds a click listener onto the robot icon that allows [ConfigActivity]
 * to be started when the icon is pressed.
 */
fun hookRobotIcon()
{
    robotIcon.setOnClickListener { view ->
        activity.startActivity( Intent(activity, ConfigActivity::class.java ) );
    }

    activity.runOnUiThread {
        robotIcon.setBackgroundResource( R.drawable.animated_robot_icon );

        ( robotIcon.background as AnimationDrawable ).start();
    }
}

/**
 * !!DO NOT CALL THIS!!
 *
 * Removes the click listener from the robot icon so that [ConfigActivity]
 * can not be opened.
 */
fun unhookRobotIcon()
{
    robotIcon.setOnClickListener { view ->
        // do nothing!
    };

    activity.runOnUiThread {
        robotIcon.setBackgroundResource( R.drawable.robot_icon_off );
    }
}

//
// OpMode Profile Hooks
//

/** The name of the active profile for the running opmode. */
private var opModeProfile: String = "";

/**
 * !!DO NOT CALL THIS!!
 *
 * Attaches a TextListener to the OpMode label to enforce the
 * current [opModeProfile] name to be shown.
 */
fun hookOpModeLabel()
{
    opModeLabel.addTextChangedListener( object : TextWatcher
    {
        override fun afterTextChanged( s: Editable? ) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged( s: CharSequence, start: Int, before: Int, count: Int )
        {
            if ( s.contains( "Stop Robot" ) )
            {
                opModeProfile = "";
                return;
            }

            if ( !s.contains( "]" ) )
            {
                if ( opModeProfile.isBlank() )
                {
                    // get the active opmode from it
                    val opModeName = s.substring( "Op Mode: ".length ).trim();
                    opModeProfile = Configurations.opModeConfigFor( opModeName ).activeProfile.name;
                }

                opModeLabel.text = "$s [$opModeProfile]";
            }
        }

    } );
}