package com.mediaservice.exception

enum class ErrorCode(code: Int) {
    ROW_DOES_NOT_EXIST(40000),
    ROW_ALREADY_EXIST(40001),
    WRONG_PASSWORD(40002),
    INVALID_SIGN_IN(40003)
}