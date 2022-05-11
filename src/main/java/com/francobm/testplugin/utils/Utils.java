package com.francobm.testplugin.utils;

import org.bukkit.Bukkit;

public class Utils {

    public static boolean getOldNMS(){
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        if (packageName.contains("1_16_") || packageName.contains("1_15_") || packageName.contains("1_14_") || packageName.contains("1_13_") || packageName.contains("1_12_") || packageName.contains("1_11_") || packageName.contains("1_10_") || packageName.contains("1_9_") || packageName.contains("1_8_")) {
            return true;
        }else if (packageName.contains("1_17_") || packageName.contains("1_18_")){
            return false;
        }
        return false;
    }

    public static boolean superiorVersion(){
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.contains("1_18_");
    }
}
