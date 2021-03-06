package com.rain.remynd.alarm

import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.rain.remynd.alarm.scheduler.AlarmScheduler
import com.rain.remynd.common.dependency
import com.rain.remynd.data.RemyndDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

internal const val BOOT_JOB_ID = 2000

class BootIntentService : JobIntentService() {
    @Inject
    internal lateinit var remyndDao: RemyndDao
    @Inject
    internal lateinit var alarmScheduler: AlarmScheduler

    companion object {
        val tag: String = BootIntentService::class.java.simpleName
    }

    override fun onCreate() {
        super.onCreate()
        applicationContext.dependency(AlarmComponent::class)
            .inject(this)
    }

    override fun onHandleWork(intent: Intent) {
        Log.d(tag, "${Thread.currentThread().name}: onHandleWork")
        runBlocking {
            remyndDao.observe()
                .first()
                .map { it.toAlarm() }
                .forEach { alarmScheduler.schedule(it) }
        }
    }
}
