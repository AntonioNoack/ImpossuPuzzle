package me.antonio.noack.gl

import kotlin.concurrent.thread

open class Initializable(private val threadSafeInit: (initializing: Initializable) -> Unit){

    private var inited = false

    init {
        thread(true){
            inits.add(this)
            threadUnsafeInit()
        }
    }

    fun init(){
        // println("gonna init? ${!inited}")
        if(inited) return
        inited = true
        threadSafeInit(this)
    }

    open fun threadUnsafeInit(){}

    companion object {
        private val inits = ArrayList<Initializable>()
        fun reset(){
            for(init in inits){
                init.inited = false
                init.threadUnsafeInit()
            }
        }
    }

}