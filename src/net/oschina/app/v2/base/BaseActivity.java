package net.oschina.app.v2.base;

import net.oschina.app.R;
import net.oschina.app.v2.ui.dialog.BaseToast;
import net.oschina.app.v2.ui.dialog.DialogControl;
import net.oschina.app.v2.ui.dialog.DialogHelper;
import net.oschina.app.v2.ui.dialog.WaitDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public abstract class BaseActivity extends ActionBarActivity implements
		DialogControl, VisibilityControl, View.OnClickListener {
	private boolean _isVisible;
	private WaitDialog _waitDialog;

	protected LayoutInflater mInflater;
	private ActionBar mActionBar;
	private TextView mTvActionTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!hasActionBar()) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		onBeforeSetContentLayout();
		if (getLayoutId() != 0) {
			setContentView(getLayoutId());
		}
		mActionBar = getSupportActionBar();
		mInflater = getLayoutInflater();
		if (hasActionBar()) {
			initActionBar(mActionBar);
		}
		init(savedInstanceState);
	}

	protected void onBeforeSetContentLayout() {
	}

	protected boolean hasActionBar() {
		return true;
	}

	protected int getLayoutId() {
		return 0;
	}

	protected View inflateView(int resId) {
		return mInflater.inflate(resId, null);
	}

	protected int getActionBarTitle() {
		return R.string.app_name;
	}

	protected boolean hasBackButton() {
		return false;
	}

	protected void init(Bundle savedInstanceState) {
	}

	protected void initActionBar(ActionBar actionBar) {
		if (actionBar == null)
			return;
		if (hasBackButton()) {
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			View view = inflateView(R.layout.actionbar_custom_backtitle);
			view.findViewById(R.id.btn_back).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							onBackPressed();
						}
					});
			mTvActionTitle = (TextView) view
					.findViewById(R.id.tv_actionbar_title);
			int titleRes = getActionBarTitle();
			if (titleRes != 0) {
				mTvActionTitle.setText(titleRes);
			}
			actionBar.setCustomView(view);
		} else {
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
			actionBar.setDisplayUseLogoEnabled(false);
			int titleRes = getActionBarTitle();
			if (titleRes != 0) {
				actionBar.setTitle(titleRes);
			}
		}
	}

	protected void setActionBarTitle(int resId) {
		if (hasActionBar() && resId != 0) {
			if (mTvActionTitle != null) {
				mTvActionTitle.setText(resId);
			}
			mActionBar.setTitle(resId);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		_isVisible = false;
		hideWaitDialog();
		super.onPause();
	}

	@Override
	protected void onResume() {
		_isVisible = true;
		super.onResume();
	}

	public void showToast(int msgResid, int icon, int gravity) {
		showToast(getString(msgResid), icon, gravity);
	}

	public void showToast(String message, int icon, int gravity) {
		BaseToast toast = new BaseToast(this);
		toast.setMessage(message);
		toast.setMessageIc(icon);
		toast.setLayoutGravity(gravity);
		toast.show();
	}

	@Override
	public WaitDialog showWaitDialog() {
		return showWaitDialog(R.string.loading);
	}

	@Override
	public WaitDialog showWaitDialog(int resid) {
		return showWaitDialog(getString(resid));
	}

	@Override
	public WaitDialog showWaitDialog(String message) {
		if (_isVisible) {
			if (_waitDialog == null) {
				_waitDialog = DialogHelper.getWaitDialog(this, message);
			}
			if (_waitDialog != null) {
				_waitDialog.setMessage(message);
				_waitDialog.show();
			}
			return _waitDialog;
		}
		return null;
	}

	@Override
	public void hideWaitDialog() {
		if (_isVisible && _waitDialog != null) {
			try {
				_waitDialog.dismiss();
				_waitDialog = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public boolean isVisible() {
		return _isVisible;
	}
}
