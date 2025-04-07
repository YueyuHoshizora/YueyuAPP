package com.yueyuhoshizora.app3253

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.appupdate.AppUpdateOptions

class SplashActivity : AppCompatActivity() {

    private lateinit var appUpdateManager: AppUpdateManager
    private val splashDelayMillis: Long = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        appUpdateManager = AppUpdateManagerFactory.create(this)

        // 檢查更新
        checkForUpdate()
    }

    private fun checkForUpdate() {
        val updateInfoTask = appUpdateManager.appUpdateInfo

        updateInfoTask.addOnSuccessListener { updateInfo ->
            if (
                updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // 使用新版 API 啟動更新流程
                val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                appUpdateManager.startUpdateFlow(
                    updateInfo,
                    this,
                    updateOptions
                )

                // 監聽進度變化
                appUpdateManager.registerListener { state ->
                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        showCompleteUpdateSnackbar()
                    }
                }
            } else {
                proceedToMain()
            }
        }.addOnFailureListener {
            proceedToMain()
        }
    }

    private fun showCompleteUpdateSnackbar() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "更新已下載，請重新啟動應用程式以完成安裝",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("重新啟動") {
            appUpdateManager.completeUpdate()
        }.show()
    }

    private fun proceedToMain() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            startActivity(intent, options.toBundle())
            finish()
        }, splashDelayMillis)
    }

    override fun onResume() {
        super.onResume()

        // 若更新已下載但未完成，繼續提示
        appUpdateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
            if (updateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                showCompleteUpdateSnackbar()
            }
        }
    }
}
