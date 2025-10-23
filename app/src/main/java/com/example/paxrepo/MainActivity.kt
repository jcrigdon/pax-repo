package com.example.paxrepo

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.example.paxrepo.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val scope = MainScope()
    private lateinit var pax: PaxInitializerMinimal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pax = PaxInitializerMinimal(this)

        binding.tvLog.movementMethod = ScrollingMovementMethod()

        binding.btnInit.setOnClickListener {
            scope.launch {
                append("INIT…")
                val r = pax.init()
                withContext(Dispatchers.Main) {
                    append("INIT → code=${r.Code} msg=${r.Msg}")
                }
            }
        }

        binding.btnSetVar.setOnClickListener {
            scope.launch {
                append("SET_VAR…")
                val r = pax.setVars()
                withContext(Dispatchers.Main) {
                    append("SET_VAR → code=${r.Code} msg=${r.Msg}")
                }
            }
        }

        binding.btnGetPed.setOnClickListener {
            val keyType = binding.etKeyType.text?.toString()?.ifBlank { PaxConstants.Pin.DES_DUKPT_KEY }
                ?: PaxConstants.Pin.DES_DUKPT_KEY
            val keySlot = binding.etKeySlot.text?.toString()?.ifBlank { PaxConstants.Pin.PIN_KEY_SLOT }
                ?: PaxConstants.Pin.PIN_KEY_SLOT

            scope.launch {
                append("GET_PED_INFORMATION (keyType=$keyType keySlot=$keySlot)…")
                val r = pax.getPedInformation(keyType, keySlot)
                withContext(Dispatchers.Main) {
                    append("GET_PED_INFORMATION → code=${r.Code} msg=${r.Msg} responses=${r.Responses}")
                }
            }
        }
    }

    private fun append(s: String) {
        binding.tvLog.append("\n$s")
        binding.tvLog.post {
            val l = binding.tvLog.layout ?: return@post
            val scrollAmount = l.getLineTop(binding.tvLog.lineCount) - binding.tvLog.height
            if (scrollAmount > 0) binding.tvLog.scrollTo(0, scrollAmount) else binding.tvLog.scrollTo(0, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
