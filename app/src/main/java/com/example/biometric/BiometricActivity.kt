package com.example.biometric

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricConstants.ERROR_NO_BIOMETRICS
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import com.example.utils.JsonUtils
import com.example.utils.SPUtil
import kotlinx.android.synthetic.main.activity_biometric.*
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import javax.crypto.Cipher


/**
 * @author wanglun
 * @date 2022/03/04
 * @description
 */
class BiometricActivity : AppCompatActivity() {

    companion object {
        private const val SP_LAST_USER_NAME = "last_user_name"
        private const val SP_LOCAL_USER_LIST = "local_user_list"
    }

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var currentState: BiometricHelper.BiometricState =
        BiometricHelper.BiometricState(0, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biometric)

        etUsername.setText(SPUtil.getString(SP_LAST_USER_NAME, null) ?: "")

        initBiometricInfo()

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    SPUtil.putString(SP_LAST_USER_NAME, username)
                    if (login(username, password)) {
                        AlertDialog.Builder(this@BiometricActivity)
                            .setTitle("是否设置指纹登录")
                            .setPositiveButton("确认设置") { _, _ ->
                                currentState.data = UserInfo(username = username, password = password)
                                if (BiometricHelper.checkFingerprintAvailable(this@BiometricActivity)) {
                                    biometricPrompt.authenticate(promptInfo,
                                        BiometricPrompt.CryptoObject(BiometricHelper.getInitializedCipherForEncryption(
                                            currentState = currentState)))
                                }
                            }
                            .create()
                            .show()

                    }
                }
            }
        }

        ivFingerprint.setOnClickListener {
            if (BiometricHelper.checkFingerprintAvailable(this)) {
                val username = etUsername.text.toString()
                if (username.isEmpty()) {
                    Toast.makeText(this, "请先输入用户名！", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val localUserInfo = getLocalUserInfo(username)
                if (localUserInfo?.secret?.isNotEmpty() == true && localUserInfo.cipherIv?.isNotEmpty() == true) {
                    val cipher = BiometricHelper.getInitializedCipherForEncryption(localUserInfo.cipherIv, currentState)
                    currentState.data = UserInfo(username = username)
                    biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
                } else {
                    Toast.makeText(this, "请先使用普通登录方式记录指纹信息！", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private suspend fun login(username: String, password: String): Boolean {
        return if (username.isNotEmpty() && password.isNotEmpty()) {
            Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show()
            true
        } else {
            Toast.makeText(this, "登录失败！当前用户名是${username},密码是${password}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun getLocalUserInfo(username: String): UserInfo? {
        val userList = JsonUtils.fromJsonArr<UserInfo>(SPUtil.getString(SP_LOCAL_USER_LIST, null),
            UserInfo::class.java) ?: mutableListOf()
        return userList.find { it.username == username }
    }

    private fun saveCurrUser(userInfo: UserInfo) {
        var userList = JsonUtils.fromJsonArr<UserInfo>(SPUtil.getString(SP_LOCAL_USER_LIST, null),
            UserInfo::class.java) ?: mutableListOf()
        val oldUserInfo = userList.find { it.username == userInfo.username }
        if (oldUserInfo != null) {
            userList.remove(oldUserInfo)
        }
        userList.add(userInfo)
        SPUtil.putString(SP_LOCAL_USER_LIST, JsonUtils.toJson(userList))
    }


    private fun initBiometricInfo() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                @SuppressLint("RestrictedApi")
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence,
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                    if (errorCode == ERROR_NO_BIOMETRICS) {
                        BiometricHelper.toFingerSettingActivity(this@BiometricActivity)
                    }
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult,
                ) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT)
                        .show()
                    onFingerprintCheckSuccess(result.cryptoObject)

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()
    }


    private fun onFingerprintCheckSuccess(cryptoObject: BiometricPrompt.CryptoObject?) {
        val cipher = cryptoObject?.cipher ?: return
        when (currentState.opMode) {
            Cipher.ENCRYPT_MODE -> {
                val username = (currentState.data as? UserInfo)?.username ?: return
                val password = (currentState.data as? UserInfo)?.password ?: return
                val secret = cipher.doFinal(password.toByteArray())
                saveCurrUser(UserInfo(username = username, secret = secret, cipherIv = cipher.iv))
            }
            Cipher.DECRYPT_MODE -> {
                val username = (currentState.data as? UserInfo)?.username ?: return
                val encryptedPassword = getLocalUserInfo(username)?.secret ?: return
                val password = String(cipher.doFinal(encryptedPassword))
                lifecycleScope.launch {
                    login(username, password)
                }
            }
        }
        currentState = BiometricHelper.BiometricState(0, null)

    }
}