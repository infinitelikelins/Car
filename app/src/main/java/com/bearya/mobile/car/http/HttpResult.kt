package com.bearya.mobile.car.http

data class HttpResult<T>(
    /** 服务器返回结果状态码  */
    var status: Int,
    /** 服务器返回的具体业务数据，为了统一处理，此处是指成功返回的结果模型  */
    var data: T?,
    /** 服务器信息  */
    var text: String,
    /** 状态码 */
    var status_code: Int?) {

    override fun toString(): String {
        return "HttpResult = { status=${status} , data=${data} , text=${text} , statusCode=${status_code}'}"
    }

}