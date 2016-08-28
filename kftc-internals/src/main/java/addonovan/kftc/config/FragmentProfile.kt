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
import addonovan.kftc.showToast
import android.preference.*
import android.text.InputType
import com.qualcomm.robotcore.eventloop.opmode.OpMode

/**
 * !Description!
 *
 * @author addonovan
 * @since 8/27/16
 */
class FragmentProfile : CustomFragment()
{
    //
    // Values
    //

    override val PreferenceResource: Int = R.xml.prefs_profile;

    /** The name of the opmode to configure. */
    private val OpModeName: String by lazy()
    {
        arguments[ OPMODE_NAME ]!! as String;
    }

    /** The name of the profile we're configuring. */
    private val ProfileName: String by lazy()
    {
        arguments[ PROFILE_NAME ]!! as String;
    }

    /** The OpModeconfig holding the profile. */
    private val CurrentOpModeConfig: OpModeConfig by lazy()
    {
        Configurations.opModeConfigFor( OpModeName );
    }

    /** The profile being configured. */
    private val CurrentProfile: Profile by lazy()
    {
        CurrentOpModeConfig.getProfile( ProfileName );
    }

    //
    // Actions
    //

    override fun onBackPressed(): Boolean
    {
        switchTo( FragmentOpModeConfig(), OPMODE_NAME to OpModeName );
        return true;
    }

    override fun onCreate()
    {
        setTitle( ProfileName );

        // set up the delete button
        val deleteProfile = findPreference( "delete_profile" ) as PreferenceScreen;
        deleteProfile.setOnPreferenceClickListener {

            // TODO ask the user to confirm

            CurrentProfile.delete(); // default will be added back if need be

            // switch back up to the previous screen
            onBackPressed();

            true;
        };
        deleteProfile.isEnabled = CurrentProfile.Name != Profile.DEFAULT_NAME; // disabled for default profiles

        // set up the reset button
        val resetProfile = findPreference( "reset_profile" ) as PreferenceScreen;
        resetProfile.setOnPreferenceClickListener {

            // TODO ask the user to confirm

            CurrentProfile.config.clear();

            // just create a new fragment, it'll be easier
            switchTo( FragmentProfile(), OPMODE_NAME to OpModeName, PROFILE_NAME to ProfileName );

            true;
        };

        setDefaults();

        // set up all the entries
        val configList = findPreference( "config_list" ) as PreferenceCategory;

        // iterate over the config map
        for ( entry in CurrentProfile.config.values )
        {
            configList.addPreference( createPreferenceFor( entry ) );
        }
    }

    //
    // Preference Fragments
    //

    /**
     * Creates the correct preference editor for the given DataEntry type.
     *
     * @param[entry]
     *          The entry to configure.
     *
     * @return the preference editor for the given entry.
     */
    private fun createPreferenceFor( entry: DataEntry< * > ) =
            when ( entry.Value )
            {
                is Boolean -> createBooleanPreference( entry.Name, entry.Value );
                is Long    -> createNumericPreference( entry.Name, entry.Value );
                is Double  -> createNumericPreference( entry.Name, entry.Value );
                is String  -> createStringPreference(  entry.Name, entry.Value );
                else       -> throw IllegalArgumentException( "This literally shouldn't happen???" );
            };


    /**
     * Creates a checkbox preference for the given value.
     *
     * @param[key]
     *          The name of the preference.
     * @param[value]
     *          The value of the preference.
     *
     * @return A CheckBoxPreference to configure the value.
     */
    private fun createBooleanPreference( key: String, value: Boolean ): CheckBoxPreference
    {
        val checkbox = CheckBoxPreference( activity );
        checkbox.title = key;
        checkbox.isChecked = value;
        checkbox.setOnPreferenceChangeListener { preference, newValue ->

            CurrentProfile.setValue( key, newValue as Boolean );
            CurrentProfile.i( "Changed $key (boolean) to $value" );

            true;
        };

        return checkbox;
    }

    /**
     * Creates a numeric preference for the given number.
     *
     * If the number is a double, decimals will be allowed in the text field.
     *
     * @param[key]
     *          The name of the preference.
     * @param[value]
     *          The value of the preference.
     *
     * @return An EditTextPreference to configure the value.
     */
    private fun createNumericPreference( key: String, value: Number ): EditTextPreference
    {
        // create the textbox
        val textbox = EditTextPreference( activity );
        textbox.title = key;
        textbox.summary = "\t$value";
        textbox.text = "$value";

        // allow only signed numbers
        textbox.editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED;

        // allow decimals if it's a double
        if ( value is Double )
        {
            textbox.editText.inputType = textbox.editText.inputType or InputType.TYPE_NUMBER_FLAG_DECIMAL;
        }

        // set up the change listener
        textbox.setOnPreferenceChangeListener { preference, newValue ->

            if ( value is Long )
            {
                try
                {
                    CurrentProfile.setValue( key, ( newValue as String ).toLong() );
                    CurrentProfile.i( "Changed $key (long) to $newValue" );
                    textbox.summary = "$newValue";
                    true; // this was a valid value
                }
                catch ( e: NumberFormatException )
                {
                    // this was an invalid value
                    CurrentProfile.e( "Failed to change $key (long) to $newValue!", e );
                    false;
                }
            }
            else
            {
                try
                {
                    CurrentProfile.setValue( key, ( newValue as String ).toDouble() );
                    CurrentProfile.i( "Changed $key (double) to $newValue" );
                    textbox.summary = "$newValue";
                    true; // this was a valid change
                }
                catch ( e: NumberFormatException )
                {
                    // this was invalid
                    CurrentProfile.e( "Failed to change $key (double) to $newValue!", e );
                    false;
                }
            }

        };

        return textbox;
    }

    /**
     * Creates a string preference for the given value.
     *
     * @param[key]
     *          The name of the preference.
     * @param[value]
     *          The initial value of the preference.
     *
     * @return An EditTextPreference to configure the value.
     */
    private fun createStringPreference( key: String, value: String ): EditTextPreference
    {
        val textbox = EditTextPreference( activity );
        textbox.title = key;
        textbox.summary = "\t$value";
        textbox.text = value;

        textbox.setOnPreferenceChangeListener { preference, newValue ->
            CurrentProfile.setValue( key, newValue as String );
            CurrentProfile.i( "Changed $key (string) to $value" );
            textbox.summary = "\t$newValue";

            true;
        };

        return textbox;
    }

    //
    // Don't look here
    //

    /**
     * Hacks around with awful methods to get the default values from an
     * OpMode.
     */
    private fun setDefaults()
    {
        System.setProperty( "kftc.inConfig", "true" ); // tell the created OpMode that it shouldn't try to do some things
        val realActiveProfile = CurrentOpModeConfig.ActiveProfile; // saved for the end

        // active the profile so that it's chosen in initialization
        CurrentProfile.activate();

        // when the OpMode is instantiated, all the default values of the configs will
        // be set if they aren't already
        try
        {
            Configurations.RegisteredOpModes[ OpModeName ]!!.newInstance(); // create the instance of the OpMode
        }
        catch ( e: Throwable )
        {
            CurrentOpModeConfig.e( "Error instantiating OpMode for configuration!", e );
            CurrentOpModeConfig.e( "Some values may not be present in the configurator!" );
            showToast( "Error created OpMode for configuration!" );
            showToast( "Some values may not be present in the profile editor" );
            showToast( ":(" );
        }

        realActiveProfile.activate(); // undo this
        System.setProperty( "kftc.inConfig", "false" ); // also undo this
    }

}
