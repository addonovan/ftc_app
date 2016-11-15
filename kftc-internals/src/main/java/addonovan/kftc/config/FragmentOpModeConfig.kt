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
import android.preference.*

/**
 * A fragment used to configure the OpModeConfig class.
 *
 * @author addonovan
 * @since 8/27/16
 */
class FragmentOpModeConfig : CustomFragment()
{

    //
    // Vals
    //

    /** The resource for inflation */
    override val PreferenceResource: Int = R.xml.prefs_opmode_config;

    /** The name of the OpMode we're displaying profiles for. */
    private val OpModeName: String by lazy()
    {
        arguments[ OPMODE_NAME ]!! as String;
    }

    //
    // Actions
    //

    override fun onBackPressed(): Boolean
    {
        switchTo( FragmentConfigurations() ); // we have no arguments to pass
        return true;
    }

    override fun onCreate()
    {
        setTitle( "$OpModeName" );

        val config = Configurations.opModeConfigFor( OpModeName ); // the OpModeConfig object for the name
        val profiles = config.getProfiles();

        // add action for clicking activate profile
        val chooseProfile = findPreference( "choose_profile" ) as ListPreference;
        chooseProfile.entries = Array( profiles.size, { i -> profiles[ i ].name } ); // create a list of all profiles to select from
        chooseProfile.entryValues = chooseProfile.entries; // so I don't have to deal with any nonsense.
        chooseProfile.value = config.activeProfile.name;

        // change the active profile when the preference is updated
        chooseProfile.setOnPreferenceChangeListener { preference, value ->
            config.setActiveProfile( value as String );
            true;
        };

        // add an action for creating a profile
        val createProfile = findPreference( "create_profile" ) as EditTextPreference;
        createProfile.setOnPreferenceChangeListener { preference, name -> config.addProfile( name as String ); }; // TODO tell them if there was a conflict?
        createProfile.text = ""; // the default text should always be blank

        // the section for profiles
        val profileList = findPreference( "profile_list" ) as PreferenceCategory;

        for( profile in profiles )
        {
            val profileScreen = preferenceManager.createPreferenceScreen( activity );
            profileScreen.title = profile.name;

            // when clicked...
            profileScreen.setOnPreferenceClickListener {

                // ...switch to the profile fragment
                switchTo( FragmentProfile(), OPMODE_NAME to OpModeName, PROFILE_NAME to profile.name);

                true;
            };

            profileList.addPreference( profileScreen ); // add it to the list
        }
    }

}
