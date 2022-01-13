package com.bearya.mobile.car.http

import com.bearya.mobile.car.BuildConfig
import com.bearya.mobile.car.entity.ServerApkUpdateInfo

object Api : HttpRetrofit() {

    private val api: ApiService? by lazy(mode = LazyThreadSafetyMode.NONE) {
        retrofit.create(ApiService::class.java)
    }

    /**
     * App检测更新信息
     */
    suspend fun checkAppUpdate(): HttpResult<ServerApkUpdateInfo>? =
        api?.checkAppUpdate(BuildConfig.APP_TYPE, "", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

}
