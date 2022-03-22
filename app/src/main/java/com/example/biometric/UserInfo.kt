package com.example.biometric

import javax.crypto.Cipher

/**
 * @author wanglun
 * @date 2022/03/04
 * @description
 */
class UserInfo(
    val username: String? = null,
    val password: String? = null,
    var secret: ByteArray? = null, // 加密后的密码
    var cipherIv: ByteArray? = null, // 指纹相关信息
)