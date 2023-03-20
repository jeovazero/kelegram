package kelegram.server.domain

sealed interface CommonResult: InviteResult, MessagesResult {
    object Inconsistent: CommonResult
    object Forbidden: CommonResult
    object NotFound: CommonResult
}