package me.antonio.noack.gl

import java.lang.Exception
import java.util.concurrent.ConcurrentLinkedQueue

object GLTaskQueue {

    val taskQueue = ConcurrentLinkedQueue<Pair<Int, () -> Unit>>()

    fun work(capacity: Int = 100){
        var taskWork = 0
        while(taskWork < capacity){
            val nextTask = taskQueue.poll()
            if(nextTask != null){
                val (work, task) = nextTask
                try {
                    task()
                } catch (e: Exception){
                    e.printStackTrace() }
                taskWork += work
            } else break
        }
    }

}