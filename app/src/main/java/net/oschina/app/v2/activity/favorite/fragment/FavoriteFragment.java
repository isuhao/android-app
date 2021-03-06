package net.oschina.app.v2.activity.favorite.fragment;

import android.view.View;

import com.umeng.analytics.MobclickAgent;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.activity.favorite.adapter.FavoriteAdapter;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.base.BaseRecycleViewFragment;
import net.oschina.app.v2.base.RecycleBaseAdapter;
import net.oschina.app.v2.model.FavoriteList;
import net.oschina.app.v2.model.FavoriteList.Favorite;
import net.oschina.app.v2.model.ListEntity;
import net.oschina.app.v2.utils.UIHelper;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 我的收藏
 * 
 * @author william_sim
 */
public class FavoriteFragment extends BaseRecycleViewFragment {

	protected static final String TAG = FavoriteFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "favorite_list";
	private static final String FAVORITE_SCREEN = "favorite_screen";

	@Override
	protected RecycleBaseAdapter getListAdapter() {
		return new FavoriteAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		FavoriteList list = FavoriteList.parse(is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return ((FavoriteList) seri);
	}

	@Override
	protected void sendRequestData() {
		NewsApi.getFavoriteList(AppContext.instance().getLoginUid(), mCatalog,
				mCurrentPage, getResponseHandler());
	}

	@Override
	public void onItemClick(View view, int position) {
		Favorite item = (Favorite) mAdapter.getItem(position);
		if (item != null)
			UIHelper.showUrlRedirect(view.getContext(), item.url);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(FAVORITE_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(FAVORITE_SCREEN);
		MobclickAgent.onPause(getActivity());
	}
}
