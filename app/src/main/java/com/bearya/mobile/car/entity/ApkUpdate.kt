package com.bearya.mobile.car.entity

import com.kelin.apkUpdater.UpdateInfoImpl
import com.kelin.apkUpdater.UpdateType

data class ServerApkUpdateInfo(
    val apk: List<ApkInfo>?,
    val info: Info?
) {
    fun toCheckUpdateInfoImpl(): UpdateInfoImpl? =
        apk?.takeIf { it.isNotEmpty() }?.let {
            UpdateInfoImpl(
                downLoadsUrl = apk[0].url,
                versionCode = info?.version_code ?: 0,
                versionName = info?.version,
                updateType = if (info?.force_update == 0) UpdateType.UPDATE_NORMAL else UpdateType.UPDATE_FORCE,
                updateMessageTitle = "更新内容如下:",
                updateMessage = info?.tips,
                signatureType = null,
                signature = null,
                forceUpdateVersionCodes = null
            )
        }
}

data class ApkInfo(
    val appid: String? = null,
    val version: Int = 0,
    val version_code: Int = 0,
    val min_version: Int = 0,
    val tips: String? = null,
    val url: String? = null,
    val force_reboot: Int = 0,
    val pack_size: Int = 0
)

data class Info(
    val version_code: Int = 0,
    val version: String? = null,
    val force_update: Int = -1,
    val tips: String? = null,
    val hardware_version: String? = null,
    val create_time: Long = 0L
)