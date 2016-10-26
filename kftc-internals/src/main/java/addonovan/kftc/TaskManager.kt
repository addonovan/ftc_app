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

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import java.util.*

/**
 * Not to be confused with the Windows `taskmgr.exe` program,
 * the Task Manager is the asynchronous system for managing
 * specific tasks.
 *
 * The life cycle of a task is rather simple. Before a task's
 * [tick][Task.tick] method can be called, it must first be
 * ready, which means the Task Manager will wait until it
 * receives a `true` back from the task's [canStart][Task.canStart]
 * method. The task will continue to be ticked until the
 * [isFinished][Task.isFinished] method returns `true`, at
 * which point, the task will be removed from the registered
 * tasks list and it's [onFinish][Task.onFinish] method will
 * be invoked.
 *
 * It should be noted that all methods, except `onFinish` have the
 * possibility of being called more than once. 'canStart' will be
 * called however many times it takes for the method to return
 * 'true', and 'tick' and 'isFinished' will be called the same number
 * of times, until 'isFinished' eventually returns 'true' (n.b. they
 * will both be called *at least* one time, as the task is ticked
 * before it is checked for completion).
 *
 * In regular OpModes, the Task Manager will call the [tick][Task.tick]
 * methods of the registered tasks as often as the [loop][KOpMode.loop]
 * method is by the system.
 *
 * In a [KLinearOpMode], the Task Manager will _not_ schedule events
 * or allow them to be put off until later; they're entire lifetime
 * will happen before the [registerTask] method returns.
 *
 * @author addonovan
 * @since 9/17/16
 */
object TaskManager : ILog by getLog( TaskManager::class )
{

    //
    // Vals
    //

    /** If this is a linear opmode, then we'll need to treat some things differently. */
    private var isLinear: Boolean = false;

    /** The tasks enqueued in the manager for execution later. */
    private val tasks: LinkedList< TaskWrapper > = LinkedList();

    //
    // Updating
    //

    /**
     * Prepares the Task Manager to use the given opmode.
     */
    internal fun prepareFor( opMode: KAbstractOpMode )
    {
        isLinear = opMode is KLinearOpMode;
        // TODO anything else that requires initialization now
    }

    //
    // Registration
    //

    /**
     * Registers a task to be executed asynchronously as the match proceeds. If the
     * OpMode this is running for is a [KLinearOpMode], then this task will be executed
     * immediately, from onStart to finish.
     *
     * @param[task]
     *          The task to complete.
     * @param[name]
     *          A short description of the task (for debugging).
     *
     *  @return The [TaskWrapper] associated with this registered task. This
     *          will be null if this is a [KLinearOpMode].
     */
    fun registerTask( task: Task, name: String ): TaskWrapper?
    {
        if ( isLinear )
        {
            runTaskLinearly( task, name );
            return null;
        }
        else
        {
            i( "Registered task: \"$name\"" );
            val wrapper = TaskWrapper( task, name );
            tasks += wrapper;
            return wrapper;
        }
    }

    //
    // Handling
    //

    /**
     * Runs the given task from onStart to end, for a linear OpMode.
     *
     * @param[task]
     *          The task to complete.
     * @param[name]
     *          The name of the task.
     */
    private fun runTaskLinearly( task: Task, name: String )
    {
        i( "Running task: \"$name\"" );

        v( "Waiting until task can start..." );
        // every 10 milliseconds, check to see if the task can onStart or not
        while ( !task.canStart() )
        {
            Thread.sleep( 10 );
        }

        task.onStart();

        v( "Running task until completion..." );
        // continually tick it until it's finished
        while ( !task.isFinished() )
        {
            task.tick();
            Thread.sleep( 10 );
        }

        v( "Task finished" );
        task.onFinish();

        i( "Completed task: \"$name\"" );
    }

    /**
     * Ticks every enqueued task in order. If the task's `onFinished` method
     * returns `true`, then it will be removed from the queue and its `onFinish`
     * method invoked.
     *
     * This method cannot be run for [KLinearOpMode]s, and _will_ throw an exception.
     */
    internal fun tick()
    {
        if ( isLinear ) throw UnsupportedOperationException( "TaskManagers cannot be ticked in LinearOpModes!" );

        val iter = tasks.iterator();
        while ( iter.hasNext() )
        {
            val wrapper = iter.next();

            // ease of access / slightly more efficient
            val task = wrapper.Task;
            val name = wrapper.Name;

            // wrapped in a try-catch as the task might throw an exception
            try
            {
                if ( !wrapper.Started )
                {
                    if ( !task.canStart() )
                    {
                        v( "Task \"$name\" cannot onStart, skipping." );
                        continue; // skip to the next task
                    }

                    i( "Task \"$name\" has started" );
                    wrapper.Started = true; // once the task is started, this method won't be called anymore
                    task.onStart();
                }

                v( "Ticking task: \"$name\"" );
                task.tick();

                // remove it if it's finished
                if ( task.isFinished() )
                {
                    i( "Task \"$name\" has been completed" );
                    iter.remove();

                    task.onFinish(); // let the task clean up
                }
            }
            catch ( e: Exception )
            {
                // log it to the user, but don't crash the program
                e( "Error while ticking task: \"$name\"!", e );
            }
        }
    }

    //
    // Inner classes
    //

    /**
     * Wraps around a task and contains some extra meta-data for the task, such as
     * the name it's registered with, and if the task has been started or not.
     */
    class TaskWrapper( val Task: Task, val Name: String )
    {
        var Started = false;
    }

}