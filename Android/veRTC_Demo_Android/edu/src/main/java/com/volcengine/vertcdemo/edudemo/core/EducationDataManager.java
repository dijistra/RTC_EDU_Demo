package com.volcengine.vertcdemo.edudemo.core;

import com.volcengine.vertcdemo.core.SolutionDataManager;

public class EducationDataManager {

    private EducationDataManager() {
    }

    public static void init() {

    }

    public static void release() {

    }

    public static String getRoomId() {
        return "";
    }

    public static String getUid() {
        return SolutionDataManager.ins().getUserId();
    }
}
