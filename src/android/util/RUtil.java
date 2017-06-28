package com.mcnc.bizmoblite.util;

import android.content.Context;
import android.util.Log;

public class RUtil {
    private static final String TAG = "ResourceUtil";
    private static String packageName = "";

    public RUtil() {
    }

    public static void setPackageName(String name) {
        Log.i("ResourceUtil", "packageName : " + name);
        packageName = name;
    }

    public static String getPackageName() {
        Log.i("ResourceUtil", "packageName : " + packageName);
        return packageName;
    }

    public static int getR(Context context, String defType, String name) {
        if (packageName.length() == 0) {
            packageName = context.getPackageName();
        }

        int id = context.getResources().getIdentifier(name, defType, packageName);
        return id;
    }

    public static int getLayoutR(Context context, String layoutName) {
        if (packageName.length() == 0) {
            packageName = context.getPackageName();
        }

        int id = context.getResources().getIdentifier(layoutName, "layout", packageName);
        return id;
    }

    public static int getDrawableR(Context context, String drawableName) {
        if (packageName.length() == 0) {
            packageName = context.getPackageName();
        }

        int id = context.getResources().getIdentifier(drawableName, "drawable", packageName);
        return id;
    }

    public static int getStringR(Context context, String stringName) {
        if (packageName.length() == 0) {
            packageName = context.getPackageName();
        }

        int id = context.getResources().getIdentifier(stringName, "string", packageName);
        return id;
    }

    public static int getIdR(Context context, String idName) {
        if (packageName.length() == 0) {
            packageName = context.getPackageName();
        }

        int id = context.getResources().getIdentifier(idName, "id", packageName);
        return id;
    }
}