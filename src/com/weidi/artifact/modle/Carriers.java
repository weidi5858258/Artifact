package com.weidi.artifact.modle;

import com.weidi.dbutil.ClassVersion;
import com.weidi.dbutil.Primary;

/***
 /data/data/com.android.providers.telephony/databases/telephony.db

 CREATE TABLE carriers(_id INTEGER PRIMARY KEY,name TEXT,numeric TEXT,mcc TEXT,mnc TEXT,apn TEXT,
 user TEXT,server TEXT,password TEXT,proxy TEXT,port TEXT,mmsproxy TEXT,mmsport TEXT,mmsc TEXT,
 authtype INTEGER,type TEXT,current INTEGER,protocol TEXT,roaming_protocol TEXT,
 carrier_enabled BOOLEAN,bearer INTEGER);

 1380|COMCEL MMS|732101|732|101|mms.comcel.com.co|COMCELMMS||*********|||198.228.90.225|9201|http://mms.comcel.com.co/mms/|-1|mms||IP|IP|1|0
 1418|EE MMS|23486|234|86|eezone|eesecure||secure|||149.254.201.135|8080|http://mms/|1|mms||IP|IP|1|0
 */
@ClassVersion(version = 1)
public class Carriers {

    @Primary
    public int id;
    public int _id;
    public String name;
    public String numeric;
    public String mcc;
    public String mnc;
    public String apn;
    public String user;
    public String server;
    public String password;
    public String proxy;
    public String port;
    public String mmsproxy;
    public String mmsport;
    public String mmsc;
    public int authtype;
    public String type;
    public int current;
    public String protocol;
    public String roaming_protocol;
    public boolean carrier_enabled;
    public int bearer;

}
