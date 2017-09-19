package com.weidi.artifact.modle;

import com.weidi.dbutil.ClassVersion;
import com.weidi.dbutil.Primary;

/***
 /data/data/com.android.providers.downloads/databases/downloads.db

 CREATE TABLE request_headers(id INTEGER PRIMARY KEY AUTOINCREMENT,
 download_id INTEGER NOT NULL,header TEXT NOT NULL,value TEXT NOT NULL);

 1|1|User-Agent|Mozilla/5.0 (Linux; U; Android 4.2.2; zh-cn; GT-N7100 Build/JDQ39E) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30 CyanogenMod/10.1.3/n7100
 2|1|Referer|http://z.25pp.com/android.html?from=bdpz&product_id=&channel=PP_75
 3|1|cookie|
 4|2|User-Agent|Mozilla/5.0 (Linux; U; Android 4.2.2; zh-cn; GT-N7100 Build/JDQ39E) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30 CyanogenMod/10.1.3/n7100
 5|2|Referer|http://z.25pp.com/android.html?from=bdpz&product_id=&channel=PP_75
 6|2|cookie|
 */
@ClassVersion(version = 1)
public class RequestHeaders {

    @Primary
    public int _id;
    public int id;
    public int download_id;
    public String header;
    public String value;

}
