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

import addonovan.kftc.hardware.HardwareExtension
import com.qualcomm.robotcore.hardware.*
import dalvik.system.DexFile
import java.lang.reflect.Constructor
import java.lang.reflect.Modifier
import java.util.*

/**
 * !Description!
 *
 * @author addonovan
 * @since 8/27/16
 */
internal object ClassFinder : ILog by getLog( ClassFinder::class )
{

    //
    // Vals
    //

    /** The classes we're left with */
    private val classes: LinkedHashSet< Class< * > > = linkedSetOf();

    /**
     * The classes that fit all of the criteria to be an OpMode.
     */
    val OpModeClasses: ArrayList< Class< out KAbstractOpMode > > by lazy {
        val list = ArrayList< Class< out KAbstractOpMode > >();

        for ( clazz in classes )
        {
            // it's a subclass
            if ( !KAbstractOpMode::class.java.isAssignableFrom( clazz ) ) continue;

            if ( !KOpMode::class.java.isAssignableFrom( clazz ) && !KLinearOpMode::class.java.isAssignableFrom( clazz ) )
            {
                // log it just to let them know if they forgot something
                w( "${clazz.canonicalName} illegally subclasses KAbstractOpMode but not KOpMode or KLinearOpMode!" );

                continue;
            }

            @Suppress( "unchecked_cast" )
            val casted = clazz as Class< out KAbstractOpMode >;

            // if it's missing the @Teleop and/or @Autnomous annotations or has @Disabled
            if ( casted.isNotRegisterable() ) continue;

            // checking is done at the first step
            list.add( casted );
            d( "Found OpMode: ${casted.simpleName} (${casted.getAnnotatedName()})" );
        }

        list;
    }

    class HardwareExtensionMap : HashMap< Class< out HardwareExtension< * > >, Constructor< out HardwareExtension< * > > >();

    /**
     * The classes that fit all of the criteria to be hardware extensions.
     */
    val HardwareExtensions: HardwareExtensionMap by lazy {
        val map = HardwareExtensionMap();

        for ( clazz in classes )
        {
            // ensure it's a subclass of HardwareExtension
            if ( !HardwareExtension::class.java.isAssignableFrom( clazz ) ) continue;

            // cast it for the rest
            @Suppress( "unchecked_cast" )
            val casted = clazz as Class< out HardwareExtension< HardwareDevice > >;

            // verify that is has an acceptable constructor
            try
            {
                for ( constructor in casted.declaredConstructors )
                {
                    val params = constructor.parameterTypes;

                    if ( params.size != 2 )
                    {
                        w( "${casted.name} has an invalid constructor!" );
                        continue;
                    }

                    // check to make sure that the constrcutro has the correct parameters
                    if ( !params[ 0 ].isOfType( HardwareExtension::class ) ) continue;
                    if ( !params[ 1 ].isOfType( String::class ) ) continue;

                    // cast everything and add it to the map
                    @Suppress( "unchecked_cast" )
                    val castedConstructor = constructor as Constructor< out HardwareExtension< * > >;
                    map[ casted ] = castedConstructor;
                    d( "Loaded HardwareExtension: ${clazz.simpleName}" );

                    break;
                }

            }
            catch ( nsme: NoSuchMethodException )
            {
                w( "${clazz.canonicalName} did not have a valid constructor to be a HardwareExtension", nsme );
                continue;
            }
        }

        map;
    }

    /** A list of packages that are blacklisted to save time when loading the baseClasses */
    private val blackList: LinkedHashSet< String > =
            linkedSetOf( "com.google", "com.android", "dalvik", "android", // android packages
                    "java", "kotlin",                                      // language packages
                    "com.ftdi", "addonovan" );                             // some FTC packages

    //
    // Constructors
    //

    init
    {
        // all the classes in this dex file
        val classNames = Collections.list( DexFile( Context.packageCodePath ).entries() );

        // modifiers that we won't allow
        val prohibitedModifiers = Modifier.ABSTRACT or Modifier.INTERFACE;

        // find the classes that are okay
        for ( name in classNames )
        {
            if ( isBlacklisted( name ) ) continue; // skip classes that are in blacklisted packages

            try
            {
                val c = Class.forName( name, false, Context.classLoader );

                if ( c.modifiers and Modifier.PUBLIC == 0         // not public
                        || c.modifiers and prohibitedModifiers != 0 )   // has a prohibited modifier
                {
                    continue;
                }

                classes.add( c );
            }
            catch ( e: Exception )
            {
                // then this class wasn't instantiable, so don't bother doing anything
            }
        }
    }

    /**
     * @param[name]
     *          The full name of the class (package included).
     * @return If the class name is in a blacklisted package.
     */
    private fun isBlacklisted( name: String ): Boolean
    {
        if ( name.contains( "$" ) ) return true;

        for ( blacklisted in blackList )
        {
            if ( name.startsWith( blacklisted ) ) return true;
        }
        return false;
    }

}