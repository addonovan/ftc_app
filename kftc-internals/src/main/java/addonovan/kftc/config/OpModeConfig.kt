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

import addonovan.kftc.ILog
import addonovan.kftc.getLog
import android.util.JsonWriter
import android.util.MalformedJsonException
import org.json.JSONObject
import java.util.*

/**
 * A container for the profiles for a given OpMode.
 *
 * Serializes to
 * ```json
 * {
 *   name: "name",
 *   activeProfile: "activeProfileName",
 *   profiles: [
 *     {
 *       name: "name",
 *       config: [
 *         [ "name", type, value ],
 *         ...
 *       ]
 *     },
 *     ...
 *   ]
 * }
 * ```
 *
 * @param[Name]
 *          The name of the OpMode.
 *
 * @author addonovan
 * @since 8/17/16
 */
class OpModeConfig private constructor( val Name: String ) : Jsonable, ILog by getLog( OpModeConfig::class, Name )
{

    companion object
    {
        /**
         * Creates a new OpModeConfig from the given JSONObject.
         *
         * @param[json]
         *          The json to parse into an OpModeConfig.
         *
         * @throws MalformedJsonException
         *          If the json was set up in an incorrect manner.
         *
         * @return The resultant OpModeConfig.
         */
        fun fromJson( json: JSONObject ): OpModeConfig
        {
            // check for the length of the object
            if ( json.length() != 3 ) throw MalformedJsonException( "Expected 3 children, received ${json.length()}" );

            // check for the tags
            if ( !json.has( "name" ) ) throw MalformedJsonException( "Missing name tag" );
            if ( !json.has( "activeProfile" ) ) throw MalformedJsonException( "Missing activeProfile tag" );
            if ( !json.has( "profiles" ) ) throw MalformedJsonException( "Missing profiles tag" );


            val name = json.getString( "name" );
            val opModeConfig = OpModeConfig( name );

            // load the profiles
            val profiles = json.getJSONArray( "profiles" );
            for ( i in 0..profiles.length() - 1 )
            {
                val profile = Profile.fromJson( opModeConfig, profiles.getJSONObject( i ) );

                // if it's the default profile, add it to the very beginning, as it should always be index 0
                if ( profile.name == Profile.DEFAULT_NAME )
                {
                    opModeConfig.profiles.add( 0, profile );
                }
                // otherwise, throw them on at the end, it doesn't matter
                else
                {
                    opModeConfig.profiles += profile;
                }

                opModeConfig.v( "Loaded Profile: ${profile.name}" );
            }

            // if, somehow, we don't have the default profile, add it to the initial position
            if ( opModeConfig.insertDefaultProfile() )
            {
                opModeConfig.w( "Had to create new default profile. Did someone mess around in the config?" );
            }

            opModeConfig.i( "Loaded ${profiles.length()} profiles" );

            // active the profile as listed in the json
            val activeProfileName = json.getString( "activeProfile" );
            opModeConfig.i( "Setting active profile to: $activeProfileName" );
            opModeConfig.setActiveProfile( activeProfileName );

            return opModeConfig;
        }

        /**
         * Creates a new OpModeConfig completely from scratch.
         *
         * @param[name]
         *          The name of the OpMode this services.
         *
         * @return The new OpModeConfig.
         */
        fun fromRaw( name: String ): OpModeConfig
        {
            val config = OpModeConfig( name );
            config.insertDefaultProfile();
            return config;
        }

    }

    //
    // Vals
    //

    /** The list of profiles that are available */
    private val profiles = ArrayList< Profile >();

    /** The index of the active profile. 0=&#91;default&#93; */
    private var activeProfileName = Profile.DEFAULT_NAME;

    /** The profile that's active and will be used by the OpMode. */
    val activeProfile: Profile
        get()
        {
            // find the matching one
            for ( profile in profiles )
            {
                if ( profile.name == activeProfileName )
                {
                    return profile;
                }
            }

            return profiles[ 0 ]; // return [default] by default
        }

    //
    // Actions
    //

    /**
     * @return `true` if a new default profile had to be created and inserted.
     */
    private fun insertDefaultProfile(): Boolean
    {
        // if we need to insert the profile
        // (there're no profiles, or the first one isn't the default)
        if ( profiles.size == 0 || profiles[ 0 ].name != Profile.DEFAULT_NAME )
        {
            v( "Inserting default profile!" );
            profiles.add( 0, Profile.fromRaw( this, Profile.DEFAULT_NAME ) );
            return true;
        }

        return false;
    }

    /**
     * @return The profiles in this OpModeConfig.
     */
    internal fun getProfiles(): Array< Profile > = profiles.toTypedArray();

    /**
     * @param[name]
     *          The name of the profile to fetch.
     *
     * @return The profile, if one exists by the name.
     */
    internal fun getProfile( name: String ): Profile
    {
        for ( profile in profiles )
        {
            if ( profile.name == name )
            {
                return profile;
            }
        }

        e( "No profile for name $name!" );
        throw IllegalArgumentException( "No profile exists for name: $name" );
    }

    /**
     * Creates a new blank profile if none exist by this name.
     *
     * @param[name]
     *          The name of the profile to add.
     *
     * @return `true` if the profile was created, `false` if there was a name conflict.
     */
    internal fun addProfile( name: String ): Boolean
    {
        // check with all the other profiles for a name conflict
        for ( profile in profiles )
        {
            // if there's a match, the name is taken
            if ( profile.name == name )
            {
                return false;
            }

            // just warn them; hopefully, they'll realize this is a bad idea on their own
            if ( profile.name.equals( name, ignoreCase = true ) )
            {
                w( "Creating a profile with the same name but different case as another!" );
            }
        }

        profiles.add( Profile.fromRaw( this, name ) );
        return true; // no name conflict!
    }

    /**
     * Sets the active profile to the one with the name [name].
     *
     * If no profile was found with the given name, then this will
     * instead switch to the default profile.
     *
     * @param[name]
     *          The name of the profile to switch to.
     *
     * @return `true` if the new active profile has the name [name],
     *         `false` if it had to be set to the [DEFAULT_PROFILE_NAME] instead.
     */
    internal fun setActiveProfile( name: String ): Boolean
    {
        for ( profile in profiles )
        {
            // we found a match, so switch the profile and exit
            if ( profile.name == name )
            {
                v( "Switching active profile to: $name" );
                activeProfileName = name;
                return true;
            }
        }

        // if we got to here, then there was no active profile by the name
        // set it to default and return false
        e( "Failed to switch to profile: $name! Defaulting to $Profile.DEFAULT_NAME instead!" );
        activeProfileName = Profile.DEFAULT_NAME;
        return false;
    }

    /**
     * Removes the profile by the name from the profile list.
     *
     * @param[name]
     *          The name of the profile to remove.
     *
     * @return `true` if the profile was removed, `false` if no profile
     *          by the name could be found.
     */
    internal fun deleteProfile( name: String ): Boolean
    {
        for ( profile in profiles )
        {
            if ( profile.name == name )
            {
                i( "Deleting profile $name" );
                profiles.remove( profile );

                // add the default profile if need be
                insertDefaultProfile();

                // if we're the active profile, switch to default
                if ( activeProfileName == name )
                {
                    setActiveProfile( Profile.DEFAULT_NAME );
                }

                return true;
            }
        }

        w( "Couldn't delete profile with name: $name, as it didn't exist" );
        return false;
    }

    //
    // Overrides
    //

    override fun toJson( writer: JsonWriter )
    {
        writer.beginObject();
        writer.name( "name" ).value( Name );
        writer.name( "activeProfile" ).value( activeProfileName );

        // write the profiles
        writer.name( "profiles" ).beginArray();
        profiles.forEach { it.toJson( writer ); };
        writer.endArray();

        writer.endObject();
    }

}
