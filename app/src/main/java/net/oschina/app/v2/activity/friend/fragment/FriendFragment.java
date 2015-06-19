package net.oschina.app.v2.activity.friend.fragment;

import android.view.View;

import com.umeng.analytics.MobclickAgent;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.activity.friend.adapter.FriendAdapter;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.BaseRecycleViewFragment;
import net.oschina.app.v2.base.RecycleBaseAdapter;
import net.oschina.app.v2.model.FriendList;
import net.oschina.app.v2.model.FriendList.Friend;
import net.oschina.app.v2.model.ListEntity;
import net.oschina.app.v2.model.Notice;
import net.oschina.app.v2.model.User;
import net.oschina.app.v2.service.NoticeUtils;
import net.oschina.app.v2.utils.UIHelper;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 我的好友
 * 
 * @author william_sim
 */
public class FriendFragment extends BaseRecycleViewFragment {

	protected static final String TAG = FriendFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "friend_list";
	private static final String FRIEND_SCREEN = "friend_screen";

	@Override
	protected void initViews(View view) {
		super.initViews(view);
		UIHelper.sendBroadcastForNotice(getActivity());
	}
	
	@Override
	protected RecycleBaseAdapter getListAdapter() {
		return new FriendAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		FriendList list = FriendList.parse(is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return ((FriendList) seri);
	}

	@Override
	protected void sendRequestData() {
		User user = AppContext.instance().getLoginInfo();
		NewsApi.getFriendList(user.getUid(), mCatalog, mCurrentPage, getResponseHandler());
	}

	@Override
	protected void onRefreshNetworkSuccess() {
		if (mCatalog == FriendList.TYPE_FANS) {
			NoticeUtils.clearNotice(Notice.TYPE_NEWFAN);
		}
	}

	@Override
	public void onItemClick(View view, int position) {
		Friend item = (Friend) mAdapter.getItem(position);
		if (item != null)
			UIHelper.showUserCenter(getActivity(), item.getUserid(),
					item.getName());
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(FRIEND_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(FRIEND_SCREEN);
		MobclickAgent.onPause(getActivity());
	}
}
