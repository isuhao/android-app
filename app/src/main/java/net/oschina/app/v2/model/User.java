package net.oschina.app.v2.model;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.oschina.app.v2.AppException;
import net.oschina.app.v2.utils.StringUtils;
import net.oschina.app.v2.utils.TLog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

/**
 * 登录用户实体类
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class User extends Base {

    public final static int RELATION_ACTION_DELETE = 0x00;//取消关注
    public final static int RELATION_ACTION_ADD = 0x01;//加关注

    public final static int RELATION_TYPE_BOTH = 0x01;//双方互为粉丝
    public final static int RELATION_TYPE_FANS_HIM = 0x02;//你单方面关注他
    public final static int RELATION_TYPE_NULL = 0x03;//互不关注
    public final static int RELATION_TYPE_FANS_ME = 0x04;//只有他关注我
    private static final String NODE_RESULT = "result";
    private static final String NODE_ERROR_CODE = "errorCode";
    private static final String NODE_ERROR_MESSAGE = "errorMessage";
    private static final String NODE_UID = "uid";
    private static final String NODE_LOCATION = "location";
    private static final String NODE_NAME = "name";
    private static final String NODE_FOLLOWERS = "followers";
    private static final String NODE_FANS = "fans";
    private static final String NODE_SOCRE = "score";
    private static final String NODE_PORTRAIT = "portrait";

    private int uid;
    private String location;
    private String name;
    private int followers;
    private int fans;
    private int score;
    private String face;
    private String account;
    private String pwd;
    private Result validate;
    private boolean isRememberMe;

    private String jointime;
    private String gender;
    private String devplatform;
    private String expertise;
    private int relation;
    private String latestonline;


    public boolean isRememberMe() {
        return isRememberMe;
    }

    public void setRememberMe(boolean isRememberMe) {
        this.isRememberMe = isRememberMe;
    }

    public String getJointime() {
        return jointime;
    }

    public void setJointime(String jointime) {
        this.jointime = jointime;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDevplatform() {
        return devplatform;
    }

    public void setDevplatform(String devplatform) {
        this.devplatform = devplatform;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public String getLatestonline() {
        return latestonline;
    }

    public void setLatestonline(String latestonline) {
        this.latestonline = latestonline;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public Result getValidate() {
        return validate;
    }

    public void setValidate(Result validate) {
        this.validate = validate;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public   static   String   inputStream2String(InputStream   is)   throws   IOException{
        ByteArrayOutputStream   baos   =   new ByteArrayOutputStream();
        int   i=-1;
        while((i=is.read())!=-1){
            baos.write(i);
        }
        return   baos.toString();
    }

    public static User parse(InputStream stream) throws IOException, AppException {
        //TLog.error("用户:" + inputStream2String(stream));
        User user = new User();
        Result res = null;
        // 获得XmlPullParser解析器
        XmlPullParser xmlParser = Xml.newPullParser();
        try {
            xmlParser.setInput(stream, Base.UTF8);
            // 获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
            int evtType = xmlParser.getEventType();
            // 一直循环，直到文档结束
            while (evtType != XmlPullParser.END_DOCUMENT) {
                String tag = xmlParser.getName();
                switch (evtType) {

                    case XmlPullParser.START_TAG:
                        // 如果是标签开始，则说明需要实例化对象了
                        if (tag.equalsIgnoreCase(NODE_RESULT)) {
                            res = new Result();
                        } else if (tag.equalsIgnoreCase(NODE_ERROR_CODE)) {
                            res.setErrorCode(StringUtils.toInt(xmlParser.nextText(), -1));
                        } else if (tag.equalsIgnoreCase(NODE_ERROR_MESSAGE)) {
                            res.setErrorMessage(xmlParser.nextText().trim());
                        } else if (res != null && res.OK()) {
                            if (tag.equalsIgnoreCase(NODE_UID)) {
                                user.uid = StringUtils.toInt(xmlParser.nextText(), 0);
                            } else if (tag.equalsIgnoreCase(NODE_LOCATION)) {
                                user.setLocation(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase(NODE_NAME)) {
                                user.setName(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase(NODE_FOLLOWERS)) {
                                user.setFollowers(StringUtils.toInt(xmlParser.nextText(), 0));
                            } else if (tag.equalsIgnoreCase(NODE_FANS)) {
                                user.setFans(StringUtils.toInt(xmlParser.nextText(), 0));
                            } else if (tag.equalsIgnoreCase(NODE_SOCRE)) {
                                user.setScore(StringUtils.toInt(xmlParser.nextText(), 0));
                            } else if (tag.equalsIgnoreCase(NODE_PORTRAIT)) {
                                user.setFace(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase(Notice.NODE_NOTICE)) {
                                user.setNotice(new Notice());
                            }  else if (user.getNotice() != null) {
                                if (tag.equalsIgnoreCase(Notice.NODE_ATME_COUNT)) {
                                    user.getNotice().setAtmeCount(
                                            StringUtils.toInt(xmlParser.nextText(), 0));
                                } else if (tag.equalsIgnoreCase(Notice.NODE_MESSAGE_COUNT)) {
                                    user.getNotice().setMsgCount(
                                            StringUtils.toInt(xmlParser.nextText(), 0));
                                } else if (tag.equalsIgnoreCase(Notice.NODE_REVIEW_COUNT)) {
                                    user.getNotice().setReviewCount(
                                            StringUtils.toInt(xmlParser.nextText(), 0));
                                } else if (tag.equalsIgnoreCase(Notice.NODE_NEWFANS_COUNT)) {
                                    user.getNotice().setNewFansCount(
                                            StringUtils.toInt(xmlParser.nextText(), 0));
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        //如果遇到标签结束，则把对象添加进集合中
                        if (tag.equalsIgnoreCase(NODE_RESULT) && res != null) {
                            user.setValidate(res);
                        }
                        break;
                }
                // 如果xml没有结束，则导航到下一个节点
                evtType = xmlParser.next();
            }

        } catch (XmlPullParserException e) {
            throw AppException.xml(e);
        } finally {
            stream.close();
        }
        return user;
    }
}
