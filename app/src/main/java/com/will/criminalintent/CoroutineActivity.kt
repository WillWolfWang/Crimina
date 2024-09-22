package com.will.criminalintent

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.will.criminalintent.databinding.ActivityCoroutineBinding
import com.will.criminalintent.viewmodel.CrimeDetailViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.concurrent.thread

class CoroutineActivity: AppCompatActivity() {

    val detailViewMode: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCoroutineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnTest.setOnClickListener() {
//            runBlocking {
//                fetchTwoDoc()
//            }
//            test()
//            dispatchTest()
//            runBlocking {
//                getDocs()
//            }
//            coroutineName();
            cancelTest()
//            cannotCancelTest()
//            tryTest()
//            nonCancelableTest()
//            parentAndChildJobTest()
//            parentAndChildCancel()
//            withTimeOutTest()
//            fetchDoc()
//            catchException()
//            supervisorJobTest()

//            lifecycleScope.launch {
//                log("lifecycleScope")
//            }

//            lifecycle.coroutineScope.launch {
//                log("coroutineScope")
//            }
        }
//        detailViewMode.num.observe(this) {
//            log("it--> $it")
//        }
    }

    // 启动协程的方法
    suspend fun start() {
        Log.e("WillWolf", "start-->" + Thread.currentThread().name)
        val value =  runBlocking {
//            delay(1000)
            Log.e("WillWolf", "runBlocking  " + Thread.currentThread().name)
            "blocking value"
        }
        val job = GlobalScope.launch {
//            delay(1000)
            Log.e("WillWolf", "launch  " + Thread.currentThread().name)
        }
        val deferred =  GlobalScope.async {
//            delay(1000)
            Log.e("WillWolf", "async  " + Thread.currentThread().name)
            "Deferred value"
        }
        Log.e("WillWolf", "end-->" + Thread.currentThread().name)

//        Log.e("WillWolf", "value-->$value, $job, $deferred")
    }

    suspend fun fetchTwoDoc() = coroutineScope {
        Log.e("WillWolf", "all start")
        val deferredOne = async {
            delay(4000)
            Log.e("WillWolf", "deferredOne done-->")
        }
        val deferredTwo = async {
            delay(3000)
            Log.e("WillWolf", "deferredTwo done-->")
        }
        // 使用 await
//        deferredOne.await()
//        deferredTwo.await()
        val deferreds = listOf(deferredOne, deferredTwo)
        // 或者 集合的 awaitAll 来等待任务完成，执行后面的语句
        deferreds.awaitAll()
        Log.e("WillWolf", "all done")
    }

    val job = Job()
    // 使用 + 号拼接协程上下文，因为协程上下文包含 4 个组成部分
    val scope = CoroutineScope(job + Dispatchers.IO)
    fun test() {
        runBlocking {
            Log.e("WillWolf", "job-->$job")
            scope.launch {
                try {
                    delay(3000)
                } catch (e: CancellationException) {
                    Log.e("WillWolf", "job is cancel")
                    throw e
                }
                Log.e("WillWolf", "end-->")
            }

            delay(1000)
            Log.e("WillWolf", "scope[job]-->${scope.coroutineContext.get(Job.Key)}")
            scope.coroutineContext.get(Job.Key)?.cancel()
        }
    }

    fun dispatchTest() {
        runBlocking {
            launch {
                log("main runBlocking")
            }

            launch(Dispatchers.Default) {
                log("Default")
                launch(Dispatchers.Unconfined) {
                    log("Unconfined 1")
                }
            }

            launch(Dispatchers.IO) {
                log("IO")
                // Unconfined 会使用最近的线程执行
                launch(Dispatchers.Unconfined) {
                    log("Unconfined 2")
                }
            }
            launch(newSingleThreadContext("MyThread")) {
                log("new single thread")
                launch(Dispatchers.Unconfined) {
                    log("Unconfined 4")
                }
            }
            launch(Dispatchers.Unconfined) {
                log("Unconfined 3")
            }
            GlobalScope.launch {
                log("Global Scope")
            }
        }
    }


    suspend fun getDocs() {
        val result = withContext(Dispatchers.IO) {
            log("with Context-->")
            delay(1000)
            "MyResult"
        }
        log("with context out $result")
    }

    fun coroutineName() {
        runBlocking {
            launch(CoroutineName("My")) {
                launch(CoroutineName("CoroutineA") + Dispatchers.Default) {
                    delay(400)
                    log("launch A")
                }

                launch(CoroutineName("CoroutineB")) {
                    delay(300)
                    log("launch B")
                }
            }
        }
    }

    fun cancelTest() {
        runBlocking {
            val job = launch(newSingleThreadContext("nn")) {
                repeat(10) { num ->
                    launch {
                        log("job: I'm sleep $num")
                        delay(500)
                    }
                }
            }
            delay(5000)
            log("main try to waiting!")
            job.cancel()
            job.join()
            log("main now quit")
        }

    }
    fun cannotCancelTest() {
        runBlocking {
            val startTime = System.currentTimeMillis()
            val job = launch(Dispatchers.Default) {
                var nextTime = startTime
                var i = 0
                // 使用 while 就是不会退出
                while (i < 5) {
                    if (!isActive) {
                        return@launch
                    }
                    if (System.currentTimeMillis() > nextTime) {
                        log("job i'm sleep $i ..")
                        i++
                        nextTime += 500
                    }
                }
            }
            delay(1300)
            log("main i'm try to wait")
            job.cancelAndJoin()
            log("main i can quit")
        }
    }

    fun tryTest() {
        runBlocking {
            val job = launch {
                try {
                    repeat(100){i ->
                        log("job I'm sleeping $i ...")
                        delay(1000)
                    }
                } catch (e: Throwable) {
                    // 这里会抛出异常
                    e.message?.let { log(it) }
                } finally {
                    log("job i'm finally")
                }
            }

            delay(1300)
            log("main i am try to waiting")
            job.cancelAndJoin()
            log("main i am quit")
        }
    }

    fun nonCancelableTest() {
        runBlocking {
            log("start")
            val launchA = launch {
                try {
                    repeat(5){
                        delay(50)
                        log("launchA - $it")
                    }
                } finally {
                    delay(50)
                    log("launchA is completed")
                }
            }
            val launchB = launch {
                try {
                    repeat(5) {
                        delay(50)
                        log("launchB - $it")
                    }
                } finally {
                    withContext(NonCancellable) {
                        delay(50)
                        log("launchB is completed")
                    }
                }
            }

            delay(200)
            launchA.cancel()
            launchB.cancel()
            log("end")
        }
    }

    fun parentAndChildJobTest() {
        runBlocking {
            val parentJob = launch {
                repeat(3) {i ->
                    launch {
                        delay(200)
                        log("Coroutine $i is done")
                    }
                }
                log("parent done")
            }
        }
    }

    fun parentAndChildCancel() {
        runBlocking {
            var parent = launch {
                var job1 = launch {
                    repeat(10){
                        delay(300)
                        log("job1 $it")
                        if (it == 2) {
                            log("job1 cancel")
                            cancel()
                        }
                    }
                }

                var job2 = launch {
                    repeat(10) {
                        delay(300)
                        log("job2 $it")
                    }
                }
            }

            delay(1600)
            log("parent job cancel")
            parent.cancel()
            delay(1000)
        }
    }

    fun withTimeOutTest() {
        runBlocking {
            log("start")
            val result = withTimeoutOrNull(300) {
                repeat(5) {
                    log("repeat $it")
                    delay(100)
                }
                200
            }
            log("result-->$result")
            log("end")
        }
    }

    fun fetchDoc() {
        runBlocking {
            val deferred = CoroutineScope(Dispatchers.IO).async {
                delay(500)
                log("task A throw Error")
                throw AssertionError()
            }
            val result = deferred.await()
            log("result--> $result")
        }

    }

    fun catchException() {
        runBlocking {
            val catchHandler = CoroutineExceptionHandler() {context, throwable ->
                log("catch exception-->" + throwable.message)
            }

            val job = GlobalScope.launch(catchHandler) {
                log("job-->")
                throw AssertionError("message1")
            }

            val deferred = GlobalScope.async(catchHandler) {
                log("deferred-->")
                throw ArithmeticException("message2")
            }
//            joinAll(job, deferred)
            job.join()
            deferred.await()
        }
    }

    fun supervisorJobTest() {
        runBlocking {
            var supervisor = SupervisorJob()
            var scope = CoroutineScope(coroutineContext + supervisor)

            with(scope) {
                var first = launch(/*CoroutineExceptionHandler() { context, throwable->
                    log("catch throwable")
                }*/) {
                    throw AssertionError("first child error")
                }

                var second = launch {
                    log("second: first cancelled ${first.isCancelled}")
                    try {
                        delay(5000)
                    } finally {
                        log("second child cancel")
                    }
                }
                first.join()
                log("cancel supervisor")
                supervisor.cancel()

            }
        }
    }



    fun log(msg: String) {
        Log.e("WillWolf", "${Thread.currentThread().name}-->$msg")
    }
}