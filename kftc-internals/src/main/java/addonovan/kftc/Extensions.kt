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

import com.qualcomm.robotcore.eventloop.opmode.*

/**
 * A file for extension methods.
 *
 * @author addonovan
 * @since 8/27/16
 */

/**
 * @return If this is marked with [TeleOp].
 */
fun Class< out KAbstractOpMode >.isTeleOp(): Boolean = isAnnotationPresent( TeleOp::class.java );

/**
 * @return If this is marked with [Autonomous].
 */
fun Class< out KAbstractOpMode >.isAutonomous(): Boolean = isAnnotationPresent( Autonomous::class.java );

/**
 * @return If this has neither [Autonomous] nor [TeleOp] or is marked with [Disabled].
 */
fun Class< out KAbstractOpMode >.isNotRegisterable(): Boolean = !isAutonomous() && !isTeleOp() || isAnnotationPresent( Disabled::class.java );

/**
 * @return The value from the "name" parameter on the annotation.
 */
fun Class< out KAbstractOpMode >.getAnnotatedName(): String =
        // return the name from the teleop annotation
        if ( isTeleOp() ) getAnnotation( TeleOp::class.java )!!.name;

        // return the name from the autonomous annotation
        else if ( isAutonomous() ) getAnnotation( Autonomous::class.java )!!.name;

        // throw an exception
        else throw IllegalStateException( "No @TeleOp or @Autonomous annotation on this class? Shame on you. $canonicalName" );

/**
 * @return The value from the "group" parameter on the annotation.
 */
fun Class< out KAbstractOpMode >.getAnnotatedGroup(): String =
        // return the group from the teleop annotation
        if ( isTeleOp() ) getAnnotation( TeleOp::class.java )!!.group;

        // return the group from the autonomous annotation
        else if ( isAutonomous() ) getAnnotation( Autonomous::class.java )!!.group;

        // throw an exception
        else throw IllegalStateException( "No @TeleOp or @Autonomous annotation on this class? Shame on you. $canonicalName" );