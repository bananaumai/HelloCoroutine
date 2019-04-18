package dev.bananaumai.hello.coroutine

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

fun main() {
    println("Before first runBlocking block, ${Thread.currentThread().name}")
    runBlocking(CoroutineName("First runBlocking")) {
        delay(100)
        println("Inside coroutine scope: $coroutineContext , ${Thread.currentThread().name}")
    }
    println("After first runBlocking block, ${Thread.currentThread().name}")

    println("Before second runBlocking block, ${Thread.currentThread().name}")
    runBlocking(CoroutineName("Second runBlocking") + Dispatchers.Default) {
        println("Beginning of runBlocking block: $coroutineContext , ${Thread.currentThread().name}")
        val job =launch {
            delay(100)
            println("Inside launch block: $coroutineContext , ${Thread.currentThread().name}")
            launch { doSomething("launch") }
            println("After doSomething call inside launch block")
        }
        println("Just after job launched: $job , ${Thread.currentThread().name}")
        job.join()
        println("Job is joined: $job , ${Thread.currentThread().name}")

        callSuspendFunSynchronously()
        callSuspendFunAsynchronously(this)
    }
    println("After second runBlocking block, ${Thread.currentThread().name}")

    println("GlobalScope is ${GlobalScope}")
    GlobalScope.launch {
        delay(100)
        println("Inside global scope launch: $coroutineContext , ${Thread.currentThread().name}")

        coroutineScope {
            println("Inside coroutineScope of global scope fun: $coroutineContext , ${Thread.currentThread().name}")
        }
    }

    Thread.sleep(1000)

    // this is equivalent with GlobalScope.launch
    val myCoroutineScope = CoroutineScope(EmptyCoroutineContext)
    println("my coroutineScope is ${myCoroutineScope}")
    myCoroutineScope.launch {
        delay(100)
        println("Inside myCoroutineScope: $coroutineContext , ${Thread.currentThread().name}")

        coroutineScope {
            println("Inside coroutineScope myCoroutineScope scope fun: $coroutineContext , ${Thread.currentThread().name}")
        }
    }

    // this doesn't work in usual jvm
    // this scope is expected to be used with UI component like android
//    MainScope().launch {
//        delay(100)
//        println("Inside MainScope launch: $coroutineContext , ${Thread.currentThread().name}")
//    }


    Thread.sleep(1000)

    println("Here is end of the main func")
}

suspend fun doSomething(caller: String) {
    delay(100)
    println("Inside doSomething function called by $caller: $coroutineContext , ${Thread.currentThread().name}")
}

suspend fun callSuspendFunSynchronously() {
    println("Synchronous: before call suspending functions")
    val one = one()
    val two = two()
    println("Synchronous: after call suspending functions")
    println("Synchronous: one() + two() = ${one + two}")
}

suspend fun callSuspendFunAsynchronously(scope: CoroutineScope) {
    if (scope != coroutineContext) {
        println("Asynchronous: scope: $scope")
        println("Asynchronous: coroutineContext $coroutineContext")
    }

    scope.launch {
        println("Asynchronous: before call suspending functions")
        val one = async { one() }
        val two = async { two() }
        println("Asynchronous: after call suspending functions")
        println("Asynchronous: one() + two() = ${one.await() + two.await()}")
    }
}

suspend fun one(): Int {
    delay(100)
    println("Inside one function")
    return 1
}

suspend fun two(): Int {
    delay(100)
    println("Inside two function")
    return 2
}
