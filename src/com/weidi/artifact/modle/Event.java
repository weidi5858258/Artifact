package com.weidi.artifact.modle;

import com.weidi.dbutil.ClassVersion;
import com.weidi.dbutil.Primary;

/**
 * Event{_id=1, time='2017/03/01 08:48:20',
 * event1='android.intent.action.SCREEN_OFF',
 * event2='null', event3='null', event4='null', // 对象null
 * event5='null', event6='null', event7='null', // 对象null
 * event8='null', event9='null', event10='null'}// 对象null
 * <p>
 * String currentTime = mPhoneSimpleDateFormat.format(new Date());
 * Event event = new Event();
 * event.time = currentTime;
 * event.event1 = "android.intent.action.SCREEN_ON";
 * SimpleDao.getInstance().add(Event.class, event);
 */
@ClassVersion(version = 1)
public class Event {

    @Primary
    public int _id;
    public String time;
    public String event1;
    public String event2;
    public String event3;
    public String event4;
    public String event5;
    public String event6;
    public String event7;
    public String event8;
    public String event9;
    public String event10;

    @Override
    public String toString() {
        return "Event{" +
                "_id=" + _id +
                ", time='" + time + '\'' +
                ", event1='" + event1 + '\'' +
                ", event2='" + event2 + '\'' +
                ", event3='" + event3 + '\'' +
                ", event4='" + event4 + '\'' +
                ", event5='" + event5 + '\'' +
                ", event6='" + event6 + '\'' +
                ", event7='" + event7 + '\'' +
                ", event8='" + event8 + '\'' +
                ", event9='" + event9 + '\'' +
                ", event10='" + event10 + '\'' +
                '}';
    }

}
