package com.rain.remynd.alarm

import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.rain.remynd.RemyndApp
import com.rain.remynd.data.RemyndDao
import com.rain.remynd.data.RemyndEntity
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import javax.inject.Inject

internal const val ALARM_JOB_ID = 1000

class AlarmIntentService : JobIntentService() {

    @Inject
    internal lateinit var remyndDao: RemyndDao

    companion object {
        val tag: String = AlarmIntentService::class.java.simpleName
    }

    override fun onCreate() {
        super.onCreate()
        (applicationContext as RemyndApp)
            .component
            .inject(this)
    }

    override fun onHandleWork(intent: Intent) {
        runBlocking {
            val entity: RemyndEntity = intent.getParcelableExtra(ENTITY)
            Log.d(tag, "onHandleWork: $entity")
            if (entity.daysOfWeek == null) {
                remyndDao.update(entity.copy(active = false))
            } else {
                val time = Calendar.getInstance().apply { timeInMillis = entity.triggerAt }
                val daySet = entity.daysOfWeek
                    .split(";")
                    .mapNotNull { it.toIntOrNull() }
                    .toSet()

                val today = Calendar.getInstance()
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, time.get(Calendar.MINUTE))
                }
                while (today >= calendar || !daySet.contains(calendar.get(Calendar.DAY_OF_WEEK))) {
                    calendar.add(Calendar.DATE, 1)
                }
                calendar.timeInMillis
                remyndDao.update(entity.copy(triggerAt = calendar.timeInMillis))
            }
        }
    }
}