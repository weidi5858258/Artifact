#
# Copyright (C) 2008 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src) $(call all-renderscript-files-under, src) \
    armeabi/libKyClientModel.so \
    armeabi/libkyImageResizeTools.so

# LOCAL_STATIC_JAVA_LIBRARIES += org.cyanogenmod.support
LOCAL_STATIC_JAVA_LIBRARIES := a b c d e f g h i j k l m n o p q

LOCAL_SRC_FILES := $(call all-java-files-under, src) $(call all-renderscript-files-under, src) \
    aidl/com/sony/dtv/irbservice/IIrbServiceCallback.aidl \
    aidl/com/sony/dtv/irbservice/IIrbService.aidl \
    aidl/com/sony/dtv/tvx/tvplayer/legacy/ITvDecimateListener.aidl \
    aidl/com/sony/dtv/tvx/tvplayer/legacy/ITvDecimateService.aidl \
    aidl/com/sony/dtv/tvx/tvplayer/legacy/ITvPlayerService.aidl \
    aidl/com/sony/dtv/tvx/tvplayer/legacy/ITvStateSetMultiScreenModeListener.aidl \
    aidl/com/sony/dtv/tvx/tvplayer/legacy/ITvStateSetPapScreenSizeListener.aidl \
    aidl/com/sony/dtv/tvx/tvplayer/legacy/ITvStateSetPipSubScreenPositionListener.aidl \
    armeabi/libKyClientModel.so \
    armeabi/libkyImageResizeTools.so

LOCAL_AIDL_INCLUDES := $(LOCAL_PATH)/aidl

LOCAL_PACKAGE_NAME := ChinaInteractiveTV
LOCAL_CERTIFICATE := shared

LOCAL_OVERRIDES_PACKAGES := ChinaInteractiveTV
LOCAL_CERTIFICATE := platform

#LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)
##################################################
include $(CLEAR_VARS)

LOCAL_PREBUILT_LIBS :=libKyClientModel:libs/armeabi/libKyClientModel.so
LOCAL_PREBUILT_LIBS :=libkyImageResizeTools:libs/armeabi/libkyImageResizeTools.so
LOCAL_MODULE_TAGS := optional

include $(BUILD_MULTI_PREBUILT)
##################################################
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
     a:libs/android-support-v7-recyclerview.jar \
     b:libs/android-support-v13.jar \
     c:libs/android-support-v17-leanback.jar \
     d:libs/apache-mime4j-0.6.jar \
     e:libs/bouncycastle.jar \
     f:libs/gson-2.2.2.jar \
     g:libs/httpmime-4.1.jar \
     h:libs/ImageResizeTools.jar \
     i:libs/ky_resource_card_sdk_3.3_1003_20150717.jar \
     j:libs/KyTvIdentifyClient_sdk201506231831.jar \
     k:libs/locSDK_4.0.jar \
     l:libs/ModelVariationUtil.jar \
     m:libs/msgpack-0.6.7.jar \
     n:libs_external/InteractiveTvUtil_Client.jar \
     o:libs_external/libitvplatform_client.jar \
     p:libs_external/libitvplatform_event.jar \
     q:libs_external/libitvplatform_sharedmem.jar \

include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))
