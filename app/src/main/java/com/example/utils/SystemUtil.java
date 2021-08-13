package com.example.utils;

import android.os.Build;
import android.os.Process;


/**
 * @author wanglun
 * @date 2021/08/09
 * @description
 */
public class SystemUtil {

    /**
     * 获取手机cpu架构，支持的指令集
     */
    public static String getCpuAbi() {
        String[] abis;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abis = Build.SUPPORTED_ABIS;
        } else {
            abis = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
        StringBuilder abiStr = new StringBuilder();
        for (String abi : abis) {
            abiStr.append(abi);
            abiStr.append(',');
        }
        return abiStr.toString();
    }

    /**
     * 获取当前应用是32/64位包
     */
    public static String getAppAbi() {
        boolean is64Bit = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            is64Bit = Process.is64Bit();
        } else {
            String arch = System.getProperty("os.arch");
            is64Bit = (arch != null && arch.contains("64"));
        }
        return is64Bit ? "x64" : "x32";
    }
}
