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

import addonovan.kftc.R
import android.os.Bundle
import android.preference.PreferenceCategory

/**
 * A fragment that represents the Configurations class.
 *
 * @author addonovan
 * @since 8/23/16
 */
class FragmentConfigurations : CustomFragment()
{

    /** Use this to inflate the preference layout. */
    override val PreferenceResource: Int = R.xml.prefs_configurations;

    // we didn't handle this, so the activity should
    override fun onBackPressed(): Boolean
    {
        return false;
    }

    override fun onCreate()
    {
        setTitle( "KOpModes" );

        val opModeList = findPreference( "opmode_list" ) as PreferenceCategory;

        for ( name in Configurations.RegisteredOpModes.values )
        {
            // create a button for each opmode
            val opModeScreen = preferenceManager.createPreferenceScreen( activity );
            opModeScreen.title = name;
            opModeScreen.setOnPreferenceClickListener {

                // switches to the next fragment to configure the chosen opmode
                switchTo( FragmentOpModeConfig(), OPMODE_NAME to name )

                true; // click handled? idk
            };

            opModeList.addPreference( opModeScreen ); // add it to the list
        }
    }

}