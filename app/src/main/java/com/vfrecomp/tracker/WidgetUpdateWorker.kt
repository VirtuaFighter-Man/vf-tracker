package com.vfrecomp.tracker

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class WidgetUpdateWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val client = OkHttpClient()

    override suspend fun doWork(): Result {
        try {
            val request = Request.Builder()
                .url("https://vf-online.pages.dev/api/online")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    val json = JSONObject(body)

                    val searching = json.optInt("searching", 0)
                    val waiting = json.optInt("waiting", 0)
                    val playing = json.optInt("playing", 0)
                    val countries = json.optJSONObject("countries")
                    val krCount = countries?.optInt("KR", 0) ?: 0

                    val timeFormat = SimpleDateFormat("HH:mm", Locale.KOREA)
                    val currentTime = timeFormat.format(Date())

                    context.getSharedPreferences("vf_widget_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putInt("searching", searching)
                        .putInt("waiting", waiting)
                        .putInt("playing", playing)
                        .putInt("kr_count", krCount)
                        .putString("updated_time", currentTime)
                        .apply()

                    VfLobbyWidget().updateAll(context)
                }
            }
        } catch (e: Exception) {
            // 에러 시 무시하고 다음 루프 실행
        } finally {
            scheduleNextRun(context)
        }

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "VfWidgetUpdateWork"

        fun scheduleNextRun(context: Context) {
            val nextWork = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
                .setInitialDelay(5, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                nextWork
            )
        }

        fun startImmediate(context: Context) {
            val immediateWork = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                immediateWork
            )
        }
    }
}
