package net.oschina.app.v2.activity.news.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.api.remote.NewsApi;
import net.oschina.app.v2.emoji.EmojiFragment;
import net.oschina.app.v2.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.v2.model.Comment;
import net.oschina.app.v2.model.CommentList;
import net.oschina.app.v2.model.Entity;
import net.oschina.app.v2.model.FavoriteList;
import net.oschina.app.v2.model.News;
import net.oschina.app.v2.model.News.Relative;
import net.oschina.app.v2.service.PublicCommentTask;
import net.oschina.app.v2.service.ServerTaskUtils;
import net.oschina.app.v2.ui.empty.EmptyLayout;
import net.oschina.app.v2.utils.StringUtils;
import net.oschina.app.v2.utils.TDevice;
import net.oschina.app.v2.utils.UIHelper;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;

public class NewsDetailFragment extends BaseDetailFragment implements
		EmojiTextListener, EmojiFragmentControl {

	protected static final String TAG = NewsDetailFragment.class
			.getSimpleName();
	private static final String NEWS_CACHE_KEY = "news_";
	private static final String NEWS_DETAIL_SCREEN = "news_detail_screen";
	private TextView mTvTitle, mTvSource, mTvTime;
	private int mNewsId;
	private News mNews;
	private TextView mTvCommentCount;
	private EmojiFragment mEmojiFragment;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		ActionBarActivity act = (ActionBarActivity) activity;
		mTvCommentCount = (TextView) act.getSupportActionBar().getCustomView()
				.findViewById(R.id.tv_comment_count);
		mTvCommentCount.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				UIHelper.showComment(getActivity(), mNews.getId(),
						CommentList.CATALOG_NEWS);
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_fragment_news_detail,
				container, false);

		mNewsId = getActivity().getIntent().getIntExtra("news_id", 0);

		initViews(view);
		return view;
	}

	private void initViews(View view) {
		mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
		mTvTitle = (TextView) view.findViewById(R.id.tv_title);
		mTvSource = (TextView) view.findViewById(R.id.tv_source);
		mTvTime = (TextView) view.findViewById(R.id.tv_time);

		mWebView = (WebView) view.findViewById(R.id.webview);
		initWebView(mWebView);
	}

	@Override
	protected String getCacheKey() {
		return new StringBuilder(NEWS_CACHE_KEY).append(mNewsId).toString();
	}

	@Override
	protected void sendRequestData() {
		mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
		NewsApi.getNewsDetail(mNewsId, mHandler);
	}

	@Override
	protected Entity parseData(InputStream is) throws Exception {
		return News.parse(is);
	}

	@Override
	protected Entity readData(Serializable seri) {
		return (News) seri;
	}

	@Override
	protected void onCommentChanged(int opt, int id, int catalog,
			boolean isBlog, Comment comment) {
		if (id == mNewsId && catalog == CommentList.CATALOG_NEWS && !isBlog) {
			if (Comment.OPT_ADD == opt && mNews != null) {
				mNews.setCommentCount(mNews.getCommentCount() + 1);
				if (mTvCommentCount != null) {
					mTvCommentCount.setVisibility(View.VISIBLE);
					mTvCommentCount.setText(getString(R.string.comment_count,
							mNews.getCommentCount()));
				}
			}
		}
	}

	@Override
	protected void executeOnLoadDataSuccess(Entity entity) {
		mNews = (News) entity;
		fillUI();
		fillWebViewBody();
	}

	private void fillUI() {
		mTvTitle.setText(mNews.getTitle());
		mTvSource.setText(mNews.getAuthor());
		mTvTime.setText(StringUtils.friendly_time(mNews.getPubDate()));
		if (mTvCommentCount != null) {
			mTvCommentCount.setVisibility(View.VISIBLE);
			mTvCommentCount.setText(getString(R.string.comment_count,
					mNews.getCommentCount()));
		}
		notifyFavorite(mNews.getFavorite() == 1);
	}

	private void fillWebViewBody() {
		String body = UIHelper.WEB_STYLE + mNews.getBody();
		// 读取用户设置：是否加载文章图片--默认有wifi下始终加载图片
		if (AppContext.shouldLoadImage() || TDevice.isWifiOpen()) {
			// 过滤掉 img标签的width,height属性
			body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
			body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
			// 添加点击图片放大支持
			body = body
					.replaceAll("(<img[^>]+src=\")(\\S+)\"",
							"$1$2\" onClick=\"javascript:mWebViewImageListener.onImageClick('$2')\"");
		} else {
			// 过滤掉 img标签
			body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
		}

		// 更多关于***软件的信息
		String softwareName = mNews.getSoftwareName();
		String softwareLink = mNews.getSoftwareLink();
		if (!StringUtils.isEmpty(softwareName)
				&& !StringUtils.isEmpty(softwareLink))
			body += String
					.format("<div id='oschina_software' style='margin-top:8px;color:#FF0000;font-weight:bold'>更多关于:&nbsp;<a href='%s'>%s</a>&nbsp;的详细信息</div>",
							softwareLink, softwareName);

		// 相关新闻
		if (mNews.getRelatives().size() > 0) {
			String strRelative = "";
			for (Relative relative : mNews.getRelatives()) {
				strRelative += String.format(
						"<a href='%s' style='text-decoration:none'>%s</a><p/>",
						relative.url, relative.title);
			}
			body += String.format("<p/><hr/><b>相关资讯</b><div><p/>%s</div>",
					strRelative);
		}

		body += "<div style='margin-bottom: 80px'/>";

		mWebView.setWebViewClient(mWebClient);
		UIHelper.addWebImageShow(getActivity(), mWebView);
		mWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
	}

	@Override
	protected void onFavoriteChanged(boolean flag) {
		mNews.setFavorite(flag ? 1 : 0);
		saveCache(mNews);
	}

	@Override
	public void setEmojiFragment(EmojiFragment fragment) {
		mEmojiFragment = fragment;
		mEmojiFragment.setEmojiTextListener(this);
	}

	@Override
	public void onSendClick(String text) {
		if (!TDevice.hasInternet()) {
			AppContext.showToastShort(R.string.tip_network_error);
			return;
		}
		if (!AppContext.instance().isLogin()) {
			UIHelper.showLogin(getActivity());
			return;
		}
		if (TextUtils.isEmpty(text)) {
			AppContext.showToastShort(R.string.tip_comment_content_empty);
			mEmojiFragment.requestFocusInput();
			return;
		}
		PublicCommentTask task = new PublicCommentTask();
		task.setId(mNewsId);
		task.setCatalog(CommentList.CATALOG_NEWS);
		task.setIsPostToMyZone(0);
		task.setContent(text);
		task.setUid(AppContext.instance().getLoginUid());
		ServerTaskUtils.publicComment(getActivity(), task);
		mEmojiFragment.reset();
	}

	@Override
	protected int getFavoriteTargetId() {
		return mNews != null ? mNews.getId() : -1;
	}

	@Override
	protected int getFavoriteTargetType() {
		return mNews != null ? FavoriteList.TYPE_NEWS : -1;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(NEWS_DETAIL_SCREEN);
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(NEWS_DETAIL_SCREEN);
		MobclickAgent.onPause(getActivity());
	}
}
