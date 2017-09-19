package com.weidi.artifact.modle;

import com.weidi.dbutil.ClassVersion;
import com.weidi.dbutil.Primary;

/***
 短信

 /data/data/com.android.providers.telephony/databases

 String SMS_URI = "content://sms";

 CREATE TABLE Sms (_id INTEGER PRIMARY KEY,thread_id INTEGER,address TEXT,person INTEGER,date
 INTEGER,date_sent INTEGER DEFAULT 0,protocol INTEGER,read INTEGER DEFAULT 0,status INTEGER
 DEFAULT -1,type INTEGER,reply_path_present INTEGER,subject TEXT,body TEXT,service_center
 TEXT,locked INTEGER DEFAULT 0,error_code INTEGER DEFAULT 0,seen INTEGER DEFAULT 0);
 <p>
 如果监听sms数据库变化，发送一条短信要经过type的6,4,2三个状态变化，
 如果只想监听接受到的短信内容
 判断type=1即可，如果判断发送短信，
 判断type=2即可，这样就不会出现重复操作。

 10|9|13758507060|12|1286584994000|0||1|-1|1|||浙江新昌力渊铸造有限公司||0|0|1

 CREATE TABLE Sms ( subject TYPELESSNESS, address TYPELESSNESS, body TYPELESSNESS,
 service_center TYPELESSNESS, person TYPELESSNESS, error_code TYPELESSNESS,
 date TYPELESSNESS, date_sent TYPELESSNESS, locked TYPELESSNESS,
 protocol TYPELESSNESS, read TYPELESSNESS, reply_path_present TYPELESSNESS,
 seen TYPELESSNESS, id TYPELESSNESS PRIMARY KEY, status TYPELESSNESS,
 _id TYPELESSNESS, thread_id TYPELESSNESS, type TYPELESSNESS );
 */
@ClassVersion(version = 1)
public class Sms {

    @Primary
    public int id;
    public int _id;// 短信序号，如100
    public int thread_id;// 对话的序号，如100，与同一个手机号互发的短信，其序号是相同的
    public String address;// 发件人地址，即手机号，如+8613811810000
    public String person;// 发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
    public long date;// 日期，long型，如1256539465022，可以对日期显示格式进行设置
    public long date_sent;// 日期，long型，如1256539465022，可以对日期显示格式进行设置
    public int protocol;// 通信协议，判断是短信还是彩信    integer  0：SMS_RPOTO, 1：MMS_PROTO
    public int read;// 是否阅读0未读，1已读
    public int status;// 短信状态-1接收，0complete,64pending,128failed
    public int type;// 短信类型 integer 1：inbox 2：sent 3：draft 4：outbox 5：failed 6：queued
    public int reply_path_present;//
    public String subject;// 短信具体内容
    public String body;// 短信具体内容
    public String service_center;// 短信服务中心号码编号，如+8613800755500
    public int locked;
    public int error_code;
    public int seen;

    @Override
    public String toString() {
        return "Sms{" +
                "id=" + id +
                ", _id=" + _id +
                ", date=" + date +
                '}';
    }

    /***
     _id            10 3 6
     thread_id      9 5 8
     address        13758507060 07103357578 10657555511312
     person         12
     date           1286584994000 1285553993000 1285907547000
     date_sent      0 0 0
     protocol
     read           1 1 1
     status         -1 -1 -1
     type           1 1 2
     reply_path_present
     subject
     body           浙江新昌力渊铸造有限公司 wlw86054008
     service_center
     locked         0 0 0
     error_code     0 0 0
     seen           1 1 1
     */

}
