package com.example.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseCleanupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val dao = AppDatabase.getDatabase(applicationContext).financialNewsDao()
            // Calculate 15 days ago in milliseconds
            val threshold = System.currentTimeMillis() - 15L * 24 * 60 * 60 * 1000
            dao.deleteOldNews(threshold)
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("DatabaseCleanupWorker", "Error cleaning up DB: ${e.message}")
            Result.failure()
        }
    }
}
