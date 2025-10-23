// PosLinkCommandQueue.kt
package com.example.paxrepo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import android.util.Log

class PosLinkCommandQueue {
    private val mutex = Mutex()
    @Volatile private var busy = false

    suspend fun <T> run(block: suspend () -> T): T = mutex.withLock {
        Log.d("PAX-QUEUE", "-> enter (busy=$busy, thread=${Thread.currentThread().name})")
        check(!busy) { "ProcessTrans already in-flight" }
        busy = true
        try {
            // 🔑 ALWAYS hop off main for POSLink work
            withContext(Dispatchers.IO) { block() }
        } finally {
            busy = false
            Log.d("PAX-QUEUE", "<- exit  (busy=$busy, thread=${Thread.currentThread().name})")
        }
    }
}
