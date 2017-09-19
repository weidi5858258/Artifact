package com.weidi.artifact.modle;

/***

 CREATE TABLE downloads(_id INTEGER PRIMARY KEY AUTOINCREMENT,uri TEXT,
 method INTEGER, entity TEXT, no_integrity BOOLEAN, hint TEXT,
 otaupdate BOOLEAN, _data TEXT, mimetype TEXT, destination INTEGER,
 no_system BOOLEAN, visibility INTEGER, control INTEGER, status INTEGER,
 numfailed INTEGER, lastmod BIGINT, notificationpackage TEXT,
 notificationclass TEXT, notificationextras TEXT, cookiedata TEXT,
 useragent TEXT, referer TEXT, total_bytes INTEGER, current_bytes INTEGER,
 etag TEXT, uid INTEGER, otheruid INTEGER, title TEXT, description TEXT,
 scanned BOOLEAN, is_public_api INTEGER NOT NULL DEFAULT 0,
 allow_roaming INTEGER NOT NULL DEFAULT 0, allowed_network_types INTEGER NOT NULL DEFAULT 0,
 is_visible_in_downloads_ui INTEGER NOT NULL DEFAULT 1,
 bypass_recommended_size_limit INTEGER NOT NULL DEFAULT 0,
 mediaprovider_uri TEXT, deleted BOOLEAN NOT NULL DEFAULT 0,
 errorMsg TEXT, allow_metered INTEGER NOT NULL DEFAULT 1);


 1|https://alissl.ucdl.pp.uc.cn/fs01/union_pack/PPAssistant_76181_PP_75.apk|0|||file:///storage/emulated/0/Download/PPAssistant_76181_PP_75.apk||/storage/emulated/0/Download/PPAssistant_76181_PP_75.apk|application/vnd.android.package-archive|4||0||200|0|1484921766878|com.android.browser||||||9305325|9305325|"46E26B2C0F487A4F524B2A22E105A3FD-2"|10004||PPAssistant_76181_PP_75.apk|alissl.ucdl.pp.uc.cn|1|1|1|-1|1|0|content://media/external/file/57296|0||1
 2|https://alissl.ucdl.pp.uc.cn/fs01/union_pack/PPAssistant_76181_PP_75.apk|0|||file:///storage/emulated/0/Download/PPAssistant_76181_PP_75.apk||/storage/emulated/0/Download/PPAssistant_76181_PP_75-1.apk|application/vnd.android.package-archive|4||0||200|0|1484921977523|com.android.browser||||||9305325|9305325|"46E26B2C0F487A4F524B2A22E105A3FD-2"|10004||PPAssistant_76181_PP_75-1.apk|alissl.ucdl.pp.uc.cn|1|1|1|-1|1|0|content://media/external/file/57297|0||1

 */

public class Downloads {
}
