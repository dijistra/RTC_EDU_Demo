#import "EduBreakoutRTCManager.h"
#import <VolcEngineRTC/objc/rtc/ByteRTCDefines.h>
#import <VolcEngineRTC/objc/rtc/ByteRTCRoom.h>

NS_ASSUME_NONNULL_BEGIN

@interface EduBreakoutRTCManager : EduRTCManager
/*
 * RTC Manager Singletons
 */
+ (EduBreakoutRTCManager *_Nullable)shareRtc;

#pragma mark - Base Method

/**
 * Create RTCEngine instance
 * @param appID The unique identifier of each application is randomly generated by the VRTC console. Instances generated by different AppIds are completely independent for audio and video calls in VRTC and cannot communicate with each other.
 */
- (void)createEngine:(NSString *)appID;
- (void)joinHostRoomWithToken:(NSString *)token roomID:(NSString *)roomID uid:(NSString *)uid;
- (void)joinGroupRoomWithToken:(NSString *)token roomID:(NSString *)roomID uid:(NSString *)uid;

- (void)openGroupSpeech:(BOOL)open;
- (void)openVideoInteract:(BOOL)open;

- (void)setHostRoomRemoteVideo:(ByteRTCVideoCanvas *)videoCanvas;
- (void)setGroupRoomRemoteVideo:(ByteRTCVideoCanvas *)videoCanvas;

/*
 * Switch local audio capture
 * @param enable ture:Turn on audio capture false：Turn off audio capture
 */
- (void)enableLocalAudio:(BOOL)enable;

/*
 * Switch local audio capture
 * @param mute ture:Turn on audio capture false：Turn off audio capture
 */
- (void)muteLocalAudio:(BOOL)mute;

/*
 * Switch audioInteract
 * @param enable ture:Turn on audioInteract false：Turn off audioInteract
 */
- (void)enableAudioInteract:(BOOL)enable;

/*
 * Switch videoInteract
 * @param enable ture:Turn on videoInteract false：Turn off videoInteract
 */
- (void)enableVideoInteract:(BOOL)enable;

/*
 * Leave the room
 */
- (void)leaveChannel;

/*
 * destroy
 */
- (void)destroy;

/*
 * get Sdk Version
 */
- (NSString *_Nullable)getSdkVersion;

/*
 * Bind the display window of the local video stream
 * @param videoCanvas Video attributes
 */
- (void)setupLocalVideo:(ByteRTCVideoCanvas *_Nullable)videoCanvas;

@end

NS_ASSUME_NONNULL_END
