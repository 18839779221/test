package com.example.biometric

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/**
 * @author wanglun
 * @date 2022/03/04
 * @description
 */
object BiometricHelper {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val YOUR_SECRET_KEY_NAME = "Y0UR_3CR3TK3YN"
    private const val KEY_SIZE = 128
    private const val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private const val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES

    fun checkFingerprintAvailable(context: Activity): Boolean {
        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
//                Toast.makeText(context, "可使用指纹", Toast.LENGTH_SHORT).show()
                return true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Toast.makeText(context, "此设备生物信息不可用", Toast.LENGTH_SHORT).show()
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Toast.makeText(context, "当前设备生物信息不可用，请稍后再试", Toast.LENGTH_SHORT).show()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                toFingerSettingActivity(context)
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getOrCreateSecretKey(keyName: String): SecretKey {
        // 1
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null) // Keystore must be loaded before it can be accessed
        keyStore.getKey(keyName, null)?.let { return it as SecretKey }

        // 2
        val paramsBuilder = KeyGenParameterSpec.Builder(
            keyName,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
        paramsBuilder.apply {
            setBlockModes(ENCRYPTION_BLOCK_MODE)
            setEncryptionPaddings(ENCRYPTION_PADDING)
            setKeySize(KEY_SIZE)
            setUserAuthenticationRequired(true)
        }

        // 3
        val keyGenParams = paramsBuilder.build()
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        keyGenerator.init(keyGenParams)

        return keyGenerator.generateKey()
    }

    fun getInitializedCipherForEncryption(
        cipherIv: ByteArray? = null,
        currentState: BiometricState?
    ): Cipher {
        val transformation = "$ENCRYPTION_ALGORITHM/$ENCRYPTION_BLOCK_MODE/$ENCRYPTION_PADDING"

        val cipher = Cipher.getInstance(transformation)
        val secretKey = getOrCreateSecretKey(YOUR_SECRET_KEY_NAME)
        if (cipherIv == null) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            currentState?.opMode = Cipher.ENCRYPT_MODE
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(cipherIv))
            currentState?.opMode = Cipher.DECRYPT_MODE
        }

        return cipher
    }

    fun toFingerSettingActivity(activity: Activity) {
        try {
            when (Build.BRAND.lowercase()) {
                "huawei" -> {
                    val intent = Intent()
                    val pcgName = "com.android.settings"
                    val clsName = "com.android.settings.fingerprint.FingerprintSettingsActivity"
                    val componentName = ComponentName(pcgName, clsName)
                    intent.action = Intent.ACTION_VIEW
                    intent.component = componentName
                    activity.startActivity(intent)
                }
                "xiaomi" -> {
                    val intent = Intent()
                    val pcgName = "com.android.settings"
                    val clsName = "com.android.settings.NewFingerprintActivity"
                    val componentName = ComponentName(pcgName, clsName)
                    intent.component = componentName
                    intent.action = "android.settings.FINGERPRINT_SETUP"
                    activity.startActivity(intent)
                }
                "oppo" -> {
                    val intent = Intent()
                    val pcgName = "com.coloros.fingerprint"
                    val clsName = "com.coloros.fingerprint.FingerprintSettings"
                    val componentName = ComponentName(pcgName, clsName)
                    intent.action = Intent.ACTION_VIEW
                    intent.component = componentName
                    activity.startActivity(intent)
                }
                "oneplus" -> {
                    val intent: Intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        Intent(Settings.ACTION_FINGERPRINT_ENROLL)
                    } else {
                        Intent(Settings.ACTION_SECURITY_SETTINGS)
                    }
                    activity.startActivity(intent)
                }
            }
        } catch (e: Exception) { }

        Toast.makeText(activity, "请先保证设备具有指纹识别功能，并前往设置页设置用户指纹", Toast.LENGTH_SHORT).show()
    }

    class BiometricState(
        var opMode: Int,
        var data: Any?,
    )
}