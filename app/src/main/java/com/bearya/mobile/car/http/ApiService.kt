package com.bearya.mobile.car.http

import com.bearya.mobile.car.entity.ServerApkUpdateInfo
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

internal interface ApiService {

    /**
     * 检查后台的版本更新
     */
    @POST("/v1/source/app/update")
    @FormUrlEncoded
    suspend fun checkAppUpdate(@Field("dtype") type: Int,
                               @Field("sn") sn: String,
                               @Field("version") version: String,
                               @Field("source_version") sourceVersion: Int): HttpResult<ServerApkUpdateInfo>

}
