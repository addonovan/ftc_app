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
import com.qualcomm.robotcore.eventloop.opmode.*

/**
 * The entry point for the kftc library.
 *
 * @author addonovan
 * @since 8/21/16
 */
class AddOpModeRegister : OpModeRegister, ILog by getLog( AddOpModeRegister::class )
{

    //
    // OpModeRegister
    //

    val a: Int by loggedVariable( 5 );

    override fun register( manager: OpModeManager )
    {
        initSystems(); // initialize all of the systems that need to be

        i( "Registering OpModes" );
        for ( clazz in ClassFinder.OpModeClasses )
        {
            val flavor =
                    if ( clazz.isAutonomous() )
                    {
                        OpModeMeta.Flavor.AUTONOMOUS;
                    }
                    else if ( clazz.isTeleOp() )
                    {
                        OpModeMeta.Flavor.TELEOP;
                    }
                    else
                    {
                        throw IllegalStateException( "OpMode has neither an Autonomous nor TeleOp Annotation!" );
                    }

            val name = clazz.getAnnotatedName();
            val group = clazz.getAnnotatedGroup();

            // register it with Ftc
            manager.register( OpModeMeta( name, flavor, group ), wrap( clazz ) );

            // register it with the configurator
            Configurations.RegisteredOpModes += clazz;
        }
        d( "OpModes registered" );
    }

    //
    // Actions
    //

    /**
     * Initializes the other systems in this framework.
     */
    private fun initSystems()
    {
        i( "Initializing kftc systems" );

        d( "Loading OpModes..." );
        // reset the previous OpModes
        currentOpMode = null;
        currentLinearOpMode = null;
        d( "Loaded ${ClassFinder.OpModeClasses.size} opmodes" );

        d( "Loading Hardware Extensions..." );
        d( "Loaded ${ClassFinder.HardwareExtensions.size} hardware extensions" );

        d( "Init Configurations..." );
        Configurations.RegisteredOpModes.clear(); // just in case some things have already been registered
        Configurations.load();

        d( "Hooking Robot icon..." );
        hookRobotIcon();

        d( "kftc systems initialized" );
    }

    //
    // Wrapping Nonsense
    //

    /**
     * Wraps the given class in the correct wrapper based on its supertype.
     *
     * @param[clazz]
     *          The class to wrap in a OpMode wrapper.
     *
     * @throws IllegalArgumentException
     *          If, somehow, the class isn't a subclass of either KOpMode or KLinearOpMode.
     *
     * @return The OpMode wrapper for registration.
     */
    @Suppress( "unchecked_cast" ) // checked via reflections
    private fun wrap( clazz: Class< out KAbstractOpMode> ): OpMode
    {
        if ( KOpMode::class.java.isAssignableFrom( clazz ) ) return KOpModeWrapper( clazz as Class< out KOpMode > );
        if ( KLinearOpMode::class.java.isAssignableFrom( clazz ) ) return KLinearOpModeWrapper( clazz as Class< out KLinearOpMode > );

        throw IllegalArgumentException( "How did this even happen? It was already checked before this?" );
    }

    //
    // Companion object
    //

    /**
     * Used to keep track of the currently running opmode.
     */
    internal companion object
    {

        /** The current running [KOpModeWrapper] */
        var currentOpMode: KOpModeWrapper? = null;

        /** The currently running [KLinearOpModeWrapper] */
        var currentLinearOpMode: KLinearOpModeWrapper? = null;
    }

}