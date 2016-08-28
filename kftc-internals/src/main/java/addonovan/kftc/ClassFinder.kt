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

import dalvik.system.DexFile
import java.lang.reflect.Modifier
import java.util.*

/**
 * !Description!
 *
 * @author addonovan
 * @since 8/27/16
 */
/**
 * Companion object used to locate the OpMode classes.
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
    val OpModeClasses by lazy {
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
        }

        list;
    }

    /**
     * The classes that fit all of the criteria to be hardware extensions.
     */
    val HardwareExtensions by lazy {

    }

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

    /** A list of packages that are blacklisted to save time when loading the baseClasses */
    private val blackList: LinkedHashSet< String > =
            linkedSetOf( "com.google", "com.android", "dalvik", "android", // android packages
                    "java", "kotlin",                                      // language packages
                    "com.ftdi", "addonovan" );                             // some FTC packages

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