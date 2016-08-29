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
import android.os.Bundle
import android.preference.PreferenceFragment

/**
 * A custom preference fragment that has a few additions that are
 * shared between the configuration fragments.
 *
 * @author addonovan
 * @since 8/23/16
 */
abstract class CustomFragment : PreferenceFragment()
{

    companion object
    {
        /** The key to get the name of the current opmode. */
        const val OPMODE_NAME = "addonovan.kftc.config.CustomFragment.OPMODE_NAME";

        /** The key to get the name of the current profile. */
        const val PROFILE_NAME = "addonovan.kftc.config.CustomFragment.PROFILE_NAME";
    }

    //
    // Values
    //

    /** The resource used for the preferences. */
    abstract val PreferenceResource: Int;

    /** The activity we're in, casted to ConfigActivity. */
    private val ConfigActivity by lazy() { activity as ConfigActivity; }

    //
    // Helpers
    //

    /**
     * Switches to the fragment and gives it the arguments.
     *
     * @param[fragment]
     *          The fragment to switch to.
     * @param[data]
     *          The arguments to pass to the fragment via bundle.
     */
    internal fun switchTo( fragment: CustomFragment, vararg data: Pair< String, String > )
    {
        // create the bundle to pass to the fragment
        val bundle = Bundle();
        for ( datum in data )
        {
            bundle.putString( datum.first, datum.second );
        }

        // pass the bundle to the fragment
        fragment.arguments = bundle;

        // switch to the fragment
        ConfigActivity.fragmentManager.beginTransaction().replace( android.R.id.content, fragment ).commit();
    }

    /**
     * Sets the title of the activity.
     *
     * @param[title]
     *          The title to display.
     */
    internal fun setTitle( title: String )
    {
        ConfigActivity.title = title;
        ConfigActivity.supportActionBar?.title = title;
    }

    //
    // On Create
    //

    /**
     * Calls onCreate without arguments.
     */
    final override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState );
        addPreferencesFromResource( PreferenceResource );
        onCreate();

        ConfigActivity.CurrentFragment = this; // mark ourselves as the current fragment
    }

    /**
     * Creates the fragment and performs initialization.
     */
    internal abstract fun onCreate();

    /**
     * Goes up to the parent fragment, if one exists.
     *
     * @return If the back pressed event was handled with a fragment change.
     */
    internal abstract fun onBackPressed(): Boolean;

}