package com.wizpizz.reddidnt.service

import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

/** Process-wide connection to the modern Xposed service. */
object ModuleService {
    private val initialized = AtomicBoolean(false)
    private val _service = MutableStateFlow<XposedService?>(null)
    val service = _service.asStateFlow()

    fun initialize() {
        if (!initialized.compareAndSet(false, true)) return

        XposedServiceHelper.registerListener(object : XposedServiceHelper.OnServiceListener {
            override fun onServiceBind(service: XposedService) {
                _service.value = service
            }

            override fun onServiceDied(service: XposedService) {
                if (_service.value === service) _service.value = null
            }
        })
    }
}
