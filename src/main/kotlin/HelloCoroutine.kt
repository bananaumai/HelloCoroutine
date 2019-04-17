package dev.bananaumai.hello.coroutine

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

fun main() = runBlocking<Unit> {
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
