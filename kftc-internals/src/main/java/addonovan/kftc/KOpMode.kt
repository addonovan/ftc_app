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
import addonovan.kftc.config.Profile
import com.qualcomm.robotcore.eventloop.opmode.OpMode

/**
 * A KOpMode is a special type of OpMode that manages a few extra
 * things for the user.
 *
 * @author addonovan
 * @since 11/14/16
 */
abstract class KOpMode : OpMode(), IConfigurable, ILog by getLog( KOpMode::class )
{

    init
    {
        // TODO hook the label and show our profile name
    }

    //
    // OpMode
    //

    final override fun loop()
    {
        // ensure that the TaskManager gets ticked, then tick the OpMode
        TaskManager.tick();
        tick();
    }

    //
    // IConfigurable
    //

    /**
     * The configuration profile used for the configuration methods.
     */
    override val ConfigProfile: Profile = Configurations.profileFor( javaClass );

    //
    // Abstract
    //

    abstract fun tick();

}
