package kelegram.server.utils

import kelegram.server.domain.CommonResult

fun handleCommonResult(result: CommonResult) =
    when(result) {
        CommonResult.Inconsistent -> ErrorResponse.unprocessableContent
        CommonResult.NotFound -> ErrorResponse.notFound
        CommonResult.Forbidden -> ErrorResponse.forbidden
    }