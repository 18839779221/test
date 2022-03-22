package com.example.biometric

/**
 * @author wanglun
 * @date 2022/03/07
 * @description 提供一种ByteArray和String的转换机制，并不通用
 */
//fun ByteArray.toHexString(): String {
//    val sb = StringBuffer()
//    if (this.isEmpty()) return ""
//    this.forEach {
//        sb.append("$it ")
//    }
//    return sb.substring(0, sb.length - 1)
//}
//
//fun String.fromHexStringToByteArray(): ByteArray {
//    val byteList = mutableListOf<Byte>()
//    this.split(" ").forEach {
//        byteList.add(it.toByte())
//    }
//    return byteList.toByteArray()
//}