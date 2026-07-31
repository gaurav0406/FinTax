package com.example.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val dao = AppDatabase.getDatabase(applicationContext).financialNewsDao()
            val repository = NewsRepository(dao)
            repository.fetchLiveNewsFromSupabase(applicationContext)
            android.util.Log.d("NewsSyncWorker", "Successfully executed periodic 45-min financial news fetch.")
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("NewsSyncWorker", "Periodic 45-min fetch error: ${e.message}")
            Result.retry()
        }
    }
}
