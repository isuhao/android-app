package net.oschina.app.v2.model.chat;

import cn.bmob.v3.BmobObject;

/**
 * Created by Tonlin on 2015/6/1.
 */
public class UserRelation extends BmobObject {

    private IMUser owner;
    private IMUser friend;
    private boolean selected;
    private String sortKey;

    public UserRelation() {

    }

    public IMUser getOwner() {
        return owner;
    }

    public void setOwner(IMUser owner) {
        this.owner = owner;
    }

    public IMUser getFriend() {
        return friend;
    }

    public void setFriend(IMUser friend) {
        this.friend = friend;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }
}
