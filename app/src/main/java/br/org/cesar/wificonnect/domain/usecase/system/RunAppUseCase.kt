package br.org.cesar.wificonnect.domain.usecase.system

import android.content.Intent
import android.content.pm.PackageManager
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RunAppUseCase @Inject constructor(
    private val packageManager: PackageManager
) {
    private val maxSize = 5
    private val appList = linkedSetOf<String>()

    private val excludedPackages = listOf(
        "com.sec.android.app.launcher",
        "com.android.systemui",
        "com.android.settings",
        "br.org.cesar.wificonnect"
    )

    fun addAppPackageName(item: String?) {
        item?.let { packageName ->
            if (!excludedPackages.contains(packageName)) {
                appList.add(packageName)
            }

            if (appList.size > maxSize) {
                appList.remove(appList.first())
            }
        }
    }

    fun getForegroundAppIntent(): Intent? {
        val packageName = appList.lastOrNull()

        return if (packageName != null) {
            packageManager.getLaunchIntentForPackage(packageName)
        } else {
            null
        }
    }
}
