package com.dicoding.todoapp.setting

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.dicoding.todoapp.R
import com.dicoding.todoapp.notification.NotificationWorker
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Notifications permission granted")
            } else {
                showToast("Notifications will not show without permission")
            }
        }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val prefNotification =
                findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
            prefNotification?.setOnPreferenceChangeListener { _, newValue ->
                val channelName = getString(R.string.notify_channel_name)
                //TODO 13 : Schedule and cancel daily reminder using WorkManager with data channelName
/*                val notificationWorkRequest =
                    PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
                        .addTag(channelName)
                        .build()

                val workManager = WorkManager.getInstance(requireContext())

                if (newValue as Boolean) {
                    workManager.enqueueUniquePeriodicWork(
                        channelName,
                        ExistingPeriodicWorkPolicy.UPDATE, notificationWorkRequest
                    )
                } else {
                    workManager.cancelAllWorkByTag(channelName)
                    workManager.pruneWork()
                }

                true*/
                // baru
                val workManager = WorkManager.getInstance(requireContext())

                val dataBuilder = Data.Builder()
                    .putString(NOTIFICATION_CHANNEL_ID, channelName)
                    .build()

                val notificationWorkRequest = PeriodicWorkRequest.Builder(NotificationWorker::class.java,1,
                    TimeUnit.DAYS)
                    .setInputData(dataBuilder)
                    .build()

                if (newValue as Boolean) {
                    workManager.enqueue(notificationWorkRequest)
                } else {
                    workManager.cancelAllWorkByTag(NotificationWorker::class.java.simpleName)
                }

                true
            }

        }
    }
}