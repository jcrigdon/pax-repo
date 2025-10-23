package com.example.paxrepo

import android.content.Context
import android.util.Log
import com.pax.poslink.CommSetting
import com.pax.poslink.ManageRequest
import com.pax.poslink.PosLink
import com.pax.poslink.ProcessTransResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PaxInitializerMinimal(
    private val context: Context,
    private val queue: PosLinkCommandQueue = PosLinkCommandQueue(),
) {
    private val posLink = PosLink(context).apply {
        // disable or redirect POSLink logs to writable dir
        try {
            javaClass.getMethod("setLogEnabled", Boolean::class.java).invoke(this, false)
        } catch (_: Throwable) {}

        try {
            val logDir = File(context.filesDir, "poslink-logs").apply { mkdirs() }
            javaClass.getMethod("setLogPath", String::class.java).invoke(this, logDir.absolutePath)
        } catch (_: Throwable) {}

        // required: CommSetting must be configured for AIDL
        setCommForAidl(this)
    }

    private fun setCommForAidl(pl: PosLink) {
        try {
            val cs = CommSetting()

            // timeout (string form)
            try {
                cs.javaClass.getMethod("setTimeOut", String::class.java).invoke(cs, "30000")
            } catch (_: Throwable) {
                try { cs.javaClass.getField("TimeOut").set(cs, "30000") } catch (_: Throwable) {}
            }

            // type = AIDL
            var set = false
            try {
                val aidlConst = CommSetting::class.java.getField("AIDL").getInt(null)
                cs.javaClass.getMethod("setType", Int::class.javaPrimitiveType).invoke(cs, aidlConst)
                set = true
            } catch (_: Throwable) {}

            if (!set) {
                try {
                    cs.javaClass.getMethod("setType", String::class.java).invoke(cs, "AIDL")
                    set = true
                } catch (_: Throwable) {}
            }

            if (!set) {
                try {
                    cs.javaClass.getMethod("setType", Int::class.javaPrimitiveType).invoke(cs, 11)
                    set = true
                } catch (_: Throwable) {}
            }

            pl.SetCommSetting(cs)
        } catch (t: Throwable) {
            Log.w("PAX", "Failed to set CommSetting for AIDL", t)
        }
    }

    // run all ProcessTrans calls off the main thread
    suspend fun init(): ProcessTransResult = queue.run {
        withContext(Dispatchers.IO) {
            val req = ManageRequest().apply { TransType = ParseTransType("INIT") }
            posLink.ManageRequest = req
            logTx("A00/INIT")
            val r = posLink.ProcessTrans()
            logRx("A01", r)
            r
        }
    }

    suspend fun setVars(
        quickChip: String = PaxConstants.YES,
        contactRetryBeforeFallback: String = "3",
        swipeTapPriority: String = PaxConstants.TAP_FIRST
    ): ProcessTransResult = queue.run {
        withContext(Dispatchers.IO) {
            val req = ManageRequest().apply {
                TransType = ParseTransType("SETVAR")
                VarName = PaxConstants.QUICK_CHIP_MODE
                VarValue = quickChip
                VarName1 = PaxConstants.CONTACT_RETRY_BEFORE_FALLBACK
                VarValue1 = contactRetryBeforeFallback
                VarName2 = PaxConstants.SWIPE_TAP_PRIORITY
                VarValue2 = swipeTapPriority
            }
            posLink.ManageRequest = req
            logTx("A04/SETVAR")
            val r = posLink.ProcessTrans()
            logRx("A05", r)
            r
        }
    }

    suspend fun getPedInformation(keyType: String, keySlot: String): ProcessTransResult = queue.run {
        withContext(Dispatchers.IO) {
            val req = ManageRequest().apply {
                TransType = ParseTransType("GETPEDINFORMATION")
                KeyType = keyType
                KeySlot = keySlot
            }
            posLink.ManageRequest = req
            logTx("A86/GETPEDINFORMATION keyType=$keyType keySlot=$keySlot")
            val r = posLink.ProcessTrans()
            logRx("A87", r)
            r
        }
    }

    private fun logTx(tag: String) = Log.d("PAX-TX", "Sending: $tag")
    private fun logRx(tag: String, r: ProcessTransResult) =
        Log.d("PAX-RX", "Recv: $tag code=${r.Code} msg=${r.Msg} resp=${r.Responses}")
}
