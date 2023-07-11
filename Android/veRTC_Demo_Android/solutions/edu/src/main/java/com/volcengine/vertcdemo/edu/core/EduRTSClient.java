// Copyright (c) 2023 Beijing Volcano Engine Technology Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.edu.core;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.ss.bytertc.engine.RTCVideo;
import com.volcengine.vertcdemo.common.AbsBroadcast;
import com.volcengine.vertcdemo.common.AppExecutors;
import com.volcengine.vertcdemo.core.SolutionDataManager;
import com.volcengine.vertcdemo.core.eventbus.SolutionDemoEventManager;
import com.volcengine.vertcdemo.core.net.IRequestCallback;
import com.volcengine.vertcdemo.core.net.rts.RTSBaseClient;
import com.volcengine.vertcdemo.core.net.rts.RTSBizInform;
import com.volcengine.vertcdemo.core.net.rts.RTSBizResponse;
import com.volcengine.vertcdemo.core.net.rts.RTSInfo;
import com.volcengine.vertcdemo.edu.bean.EduResponse;
import com.volcengine.vertcdemo.edu.bean.EduUserInfo;
import com.volcengine.vertcdemo.edu.bean.GetUserListResponse;
import com.volcengine.vertcdemo.edu.bean.JoinClassResult;
import com.volcengine.vertcdemo.edu.bean.ReconnectResponse;
import com.volcengine.vertcdemo.edu.event.EduClassEvent;
import com.volcengine.vertcdemo.edu.event.EduGroupSpeechEvent;
import com.volcengine.vertcdemo.edu.event.EduLoginElseWhereEvent;
import com.volcengine.vertcdemo.edu.event.EduStuMicEvent;
import com.volcengine.vertcdemo.edu.event.EduTeacherCameraStatusEvent;
import com.volcengine.vertcdemo.edu.event.EduTeacherMicStatusEvent;
import com.volcengine.vertcdemo.edu.event.EduVideoInteractEvent;
import com.volcengine.vertcdemo.edu.event.GroupStudentJoinEvent;
import com.volcengine.vertcdemo.edu.event.GroupStudentLeaveEvent;
import com.volcengine.vertcdemo.edu.event.UpdateActiveClassListEvent;
import com.volcengine.vertcdemo.edu.event.UpdateHistoryClassListEvent;
import com.volcengine.vertcdemo.edu.event.UpdateHistoryListOfClassEvent;

import java.util.UUID;

public class EduRTSClient extends RTSBaseClient {

    private static final String CMD_EDU_GET_ACTIVE_CLASS = "eduGetActiveClass";
    private static final String CMD_EDU_JOIN_CLASS = "eduJoinClass";
    private static final String CMD_EDU_LEAVE_CLASS = "eduLeaveClass";
    private static final String CMD_EDU_HANDS_UP = "eduHandsUp";
    private static final String CMD_EDU_CANCEL_HANDS_UP = "eduCancelHandsUp";
    private static final String CMD_EDU_GET_HISTORY_ROOM_LIST = "eduGetHistoryRoomList";
    private static final String CMD_EDU_GET_HISTORY_RECORD_LIST = "eduGetHistoryRecordList";
    private static final String CMD_EDU_GET_USER_LIST = "eduGetUserList"; // 获取房间内所有用户列表
    private static final String CMD_RECONNECT = "eduReconnect";

    public static final String ON_BEGIN_CLASS = "onBeginClass";
    public static final String ON_END_CLASS = "onEndClass";
    public static final String ON_OPEN_GROUP_SPEECH = "onOpenGroupSpeech";
    public static final String ON_CLOSE_GROUP_SPEECH = "onCloseGroupSpeech";
    public static final String ON_OPEN_VIDEO_INTERACT = "onOpenVideoInteract";
    public static final String ON_CLOSE_VIDEO_INTERACT = "onCloseVideoInteract";
    public static final String ON_TEACHER_MIC_ON = "onTeacherMicOn";
    public static final String ON_TEACHER_MIC_OFF = "onTeacherMicOff";
    public static final String ON_TEACHER_CAMERA_ON = "onTeacherCameraOn";
    public static final String ON_TEACHER_CAMERA_OFF = "onTeacherCameraOff";
    public static final String ON_TEACHER_JOIN_ROOM = "onTeacherJoinRoom";
    public static final String ON_TEACHER_LEAVE_ROOM = "onTeacherLeaveRoom";
    public static final String ON_STUDENT_JOIN_GROUP_ROOM = "onStudentJoinGroupRoom";
    public static final String ON_STUDENT_LEAVE_GROUP_ROOM = "onStudentLeaveGroupRoom";
    public static final String ON_STUDENT_MIC_ON = "onStuMicOn";
    public static final String ON_STUDENT_MIC_OFF = "onStuMicOff";
    public static final String ON_APPROVE_MIC = "onApproveMic";
    public static final String ON_CLOSE_MIC = "onCloseMic";
    public static final String ON_LOG_IN_ELSE_WHERE = "onLogInElsewhere";

    public EduRTSClient(@NonNull RTCVideo rtcVideo, @NonNull RTSInfo rtmInfo) {
        super(rtcVideo, rtmInfo);
        initEventListener();
    }

    public void reconnect(String roomId, IRequestCallback<ReconnectResponse> callback) {
        JsonObject params = getCommonParams(CMD_RECONNECT);
        params.addProperty("login_token", SolutionDataManager.ins().getToken());
        sendServerMessageOnNetwork(roomId, params, ReconnectResponse.class, callback);
    }

    private JsonObject getCommonParams(String cmd) {
        JsonObject params = new JsonObject();
        params.addProperty("app_id", mRTSInfo.appId);
        params.addProperty("room_id", "");
        params.addProperty("user_id", SolutionDataManager.ins().getUserId());
        params.addProperty("event_name", cmd);
        params.addProperty("request_id", UUID.randomUUID().toString());
        params.addProperty("device_id", SolutionDataManager.ins().getDeviceId());
        return params;
    }

    private void initEventListener() {
        putEventListener(new AbsBroadcast<>(ON_BEGIN_CLASS, EduClassEvent.class, (data) -> {
            data.isStart = true;
            SolutionDemoEventManager.post(data);
        }));

        putEventListener(new AbsBroadcast<>(ON_END_CLASS, EduClassEvent.class, (data) -> {
            data.isStart = false;
            SolutionDemoEventManager.post(data);
        }));

        putEventListener(new AbsBroadcast<>(ON_OPEN_GROUP_SPEECH, EduResponse.class,
                (data) -> SolutionDemoEventManager.post(new EduGroupSpeechEvent(true))));

        putEventListener(new AbsBroadcast<>(ON_CLOSE_GROUP_SPEECH, EduResponse.class,
                (data) -> SolutionDemoEventManager.post(new EduGroupSpeechEvent(false))));

        putEventListener(new AbsBroadcast<>(ON_OPEN_VIDEO_INTERACT, EduResponse.class,
                (data) -> SolutionDemoEventManager.post(new EduVideoInteractEvent(true))));

        putEventListener(new AbsBroadcast<>(ON_CLOSE_VIDEO_INTERACT, EduResponse.class,
                (data) -> SolutionDemoEventManager.post(new EduVideoInteractEvent(false))));

        putEventListener(new AbsBroadcast<>(ON_TEACHER_MIC_ON, EduUserInfo.class,
                (data) -> SolutionDemoEventManager.post(new EduTeacherMicStatusEvent(data.userId, true))));

        putEventListener(new AbsBroadcast<>(ON_TEACHER_MIC_OFF, EduUserInfo.class,
                (data) -> SolutionDemoEventManager.post(new EduTeacherMicStatusEvent(data.userId, false))));

        putEventListener(new AbsBroadcast<>(ON_TEACHER_CAMERA_ON, EduUserInfo.class,
                (data) -> SolutionDemoEventManager.post(new EduTeacherCameraStatusEvent(data.userId, true))));

        putEventListener(new AbsBroadcast<>(ON_TEACHER_CAMERA_OFF, EduUserInfo.class,
                (data) -> SolutionDemoEventManager.post(new EduTeacherCameraStatusEvent(data.userId, false))));

        putEventListener(new AbsBroadcast<>(ON_TEACHER_JOIN_ROOM, EduUserInfo.class, (data) -> {
        }));

        putEventListener(new AbsBroadcast<>(ON_TEACHER_LEAVE_ROOM, EduUserInfo.class, (data) -> {
        }));

        putEventListener(new AbsBroadcast<>(ON_STUDENT_JOIN_GROUP_ROOM, EduUserInfo.class,
                (data) -> SolutionDemoEventManager.post(new GroupStudentJoinEvent(data))));

        putEventListener(new AbsBroadcast<>(ON_STUDENT_LEAVE_GROUP_ROOM, EduUserInfo.class,
                (data) -> SolutionDemoEventManager.post(new GroupStudentLeaveEvent(data))));

        putEventListener(new AbsBroadcast<>(ON_STUDENT_MIC_ON, EduUserInfo.class,
                (data) -> SolutionDemoEventManager.post(new EduStuMicEvent(true, data))));

        putEventListener(new AbsBroadcast<>(ON_STUDENT_MIC_OFF, EduUserInfo.class,
                (data) -> SolutionDemoEventManager.post(new EduStuMicEvent(false, data))));

        putEventListener(new AbsBroadcast<>(ON_APPROVE_MIC, EduResponse.class, (data) -> {
        }));

        putEventListener(new AbsBroadcast<>(ON_CLOSE_MIC, EduResponse.class, (data) -> {
        }));

        putEventListener(new AbsBroadcast<>(ON_LOG_IN_ELSE_WHERE, EduResponse.class,
                (data) -> SolutionDemoEventManager.post(new EduLoginElseWhereEvent())));
    }

    private void putEventListener(AbsBroadcast<? extends RTSBizInform> absBroadcast) {
        mEventListeners.put(absBroadcast.getEvent(), absBroadcast);
    }

    public void removeAllEventListener() {
        mEventListeners.remove(ON_BEGIN_CLASS);
        mEventListeners.remove(ON_END_CLASS);
        mEventListeners.remove(ON_OPEN_GROUP_SPEECH);
        mEventListeners.remove(ON_CLOSE_GROUP_SPEECH);
        mEventListeners.remove(ON_OPEN_VIDEO_INTERACT);
        mEventListeners.remove(ON_CLOSE_VIDEO_INTERACT);
        mEventListeners.remove(ON_TEACHER_MIC_ON);
        mEventListeners.remove(ON_TEACHER_MIC_OFF);
        mEventListeners.remove(ON_TEACHER_CAMERA_ON);
        mEventListeners.remove(ON_TEACHER_CAMERA_OFF);
        mEventListeners.remove(ON_TEACHER_JOIN_ROOM);
        mEventListeners.remove(ON_TEACHER_LEAVE_ROOM);
        mEventListeners.remove(ON_STUDENT_JOIN_GROUP_ROOM);
        mEventListeners.remove(ON_STUDENT_LEAVE_GROUP_ROOM);
        mEventListeners.remove(ON_STUDENT_MIC_ON);
        mEventListeners.remove(ON_STUDENT_MIC_OFF);
        mEventListeners.remove(ON_APPROVE_MIC);
        mEventListeners.remove(ON_CLOSE_MIC);
        mEventListeners.remove(ON_LOG_IN_ELSE_WHERE);
    }

    private <T extends RTSBizResponse> void sendServerMessageOnNetwork(String roomId, JsonObject content, Class<T> resultClass, IRequestCallback<T> callback) {
        String cmd = content.get("event_name").getAsString();
        if (TextUtils.isEmpty(cmd)) {
            return;
        }
        AppExecutors.networkIO().execute(() -> sendServerMessage(cmd, roomId, content, resultClass, callback));
    }

    public void requestActiveClassList(IRequestCallback<UpdateActiveClassListEvent> callback) {
        JsonObject params = getCommonParams(CMD_EDU_GET_ACTIVE_CLASS);
        sendServerMessageOnNetwork("", params, UpdateActiveClassListEvent.class, callback);
    }

    public void requestHistoryClassList(IRequestCallback<UpdateHistoryClassListEvent> callback) {
        JsonObject params = getCommonParams(CMD_EDU_GET_HISTORY_ROOM_LIST);
        sendServerMessageOnNetwork("", params, UpdateHistoryClassListEvent.class, callback);
    }

    public void requestHistoryListOfClass(String roomId, IRequestCallback<UpdateHistoryListOfClassEvent> callback) {
        JsonObject params = getCommonParams(CMD_EDU_GET_HISTORY_RECORD_LIST);
        params.addProperty("room_id", roomId);
        sendServerMessageOnNetwork(roomId, params, UpdateHistoryListOfClassEvent.class, callback);
    }

    public void joinClass(String roomId, IRequestCallback<JoinClassResult> callback) {
        JsonObject params = getCommonParams(CMD_EDU_JOIN_CLASS);
        params.addProperty("room_id", roomId);
        params.addProperty("user_name", SolutionDataManager.ins().getUserName());
        sendServerMessageOnNetwork(roomId, params, JoinClassResult.class, callback);
    }

    public void leaveClass(String roomId, IRequestCallback<EduResponse> callback) {
        JsonObject params = getCommonParams(CMD_EDU_LEAVE_CLASS);
        params.addProperty("room_id", roomId);
        sendServerMessageOnNetwork(roomId, params, EduResponse.class, callback);
    }

    public void getUserList(String roomId, IRequestCallback<GetUserListResponse> callback) {
        JsonObject params = getCommonParams(CMD_EDU_GET_USER_LIST);
        params.addProperty("room_id", roomId);
        sendServerMessageOnNetwork(roomId, params, GetUserListResponse.class, callback);
    }

    public void handsUp(String roomId, IRequestCallback<EduResponse> callback) {
        JsonObject params = getCommonParams(CMD_EDU_HANDS_UP);
        params.addProperty("room_id", roomId);
        sendServerMessageOnNetwork(roomId, params, EduResponse.class, callback);
    }

    public void cancelHandsUp(String roomId, IRequestCallback<EduResponse> callback) {
        JsonObject params = getCommonParams(CMD_EDU_CANCEL_HANDS_UP);
        params.addProperty("room_id", roomId);
        sendServerMessageOnNetwork(roomId, params, EduResponse.class, callback);
    }
}
