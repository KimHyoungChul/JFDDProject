package org.linphone;

import org.linphone.LinphonePreferences.AccountBuilder;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneAddress.TransportType;
import org.linphone.setup.EchoCancellerCalibrationFragment;
import org.linphone.setup.SetupActivity;
import org.linphone.setup.SetupFragmentsEnum;
import org.linphone.setup.WizardConfirmFragment;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
public class UserFirstLogin extends FragmentActivity implements OnClickListener {

	private Button apply;
	private EditText login;
	private EditText password;
	private EditText domain;
	
	private LinphonePreferences mPrefs;
	private boolean accountCreated = false;
	private LinphoneCoreListenerBase mListener;
	private LinphoneAddress address;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_first_login);
		
		mPrefs = LinphonePreferences.instance();
		login = (EditText)findViewById(R.id.setup_username);
		password = (EditText) findViewById(R.id.setup_password);
		domain = (EditText) findViewById(R.id.setup_domain);
		apply = (Button) findViewById(R.id.setup_apply);
		apply.setOnClickListener(this);
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.user_first_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}

	
	
	private void logIn(String username, String password, String domain, boolean sendEcCalibrationResult) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null && getCurrentFocus() != null) {
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}

        saveCreatedAccount(username, password, domain);

//		if (LinphoneManager.getLc().getDefaultProxyConfig() != null) {
//			launchEchoCancellerCalibration(sendEcCalibrationResult);
//		}
	}

	public void saveCreatedAccount(String username, String password, String domain) {
		if (accountCreated)
			return;

		String identity = "sip:" + username + "@" + domain;
		try {
			address = LinphoneCoreFactory.instance().createLinphoneAddress(identity);
		} catch (LinphoneCoreException e) {
			e.printStackTrace();
		}
		boolean isMainAccountLinphoneDotOrg = domain.equals(getString(R.string.default_domain));
		boolean useLinphoneDotOrgCustomPorts = getResources().getBoolean(R.bool.use_linphone_server_ports);
		AccountBuilder builder = new AccountBuilder(LinphoneManager.getLc())
		.setUsername(username)
		.setDomain(domain)
		.setPassword(password);
		
		if (isMainAccountLinphoneDotOrg && useLinphoneDotOrgCustomPorts) {
			if (getResources().getBoolean(R.bool.disable_all_security_features_for_markets)) {
				builder.setProxy(domain + ":5228")
				.setTransport(TransportType.LinphoneTransportTcp);
			}
			else {
				builder.setProxy(domain + ":5223")
				.setTransport(TransportType.LinphoneTransportTls);
			}
			
			builder.setExpires("604800")
			.setOutboundProxyEnabled(true)
			.setAvpfEnabled(true)
			.setAvpfRRInterval(3)
			.setQualityReportingCollector("sip:voip-metrics@sip.linphone.org")
			.setQualityReportingEnabled(true)
			.setQualityReportingInterval(180)
			.setRealm("sip.linphone.org");
			
			
			mPrefs.setStunServer(getString(R.string.default_stun));
			mPrefs.setIceEnabled(true);
		} else {
			String forcedProxy = getResources().getString(R.string.setup_forced_proxy);
			if (!TextUtils.isEmpty(forcedProxy)) {
				builder.setProxy(forcedProxy)
				.setOutboundProxyEnabled(true)
				.setAvpfRRInterval(5);
			}
		}
		
		if (getResources().getBoolean(R.bool.enable_push_id)) {
			String regId = mPrefs.getPushNotificationRegistrationID();
			String appId = getString(R.string.push_sender_id);
			if (regId != null && mPrefs.isPushNotificationEnabled()) {
				String contactInfos = "app-id=" + appId + ";pn-type=google;pn-tok=" + regId;
				builder.setContactParameters(contactInfos);
			}
		}
		
		try {
			builder.saveNewAccount();
			accountCreated = true;
		} catch (LinphoneCoreException e) {
			e.printStackTrace();
		}
	}


	public void isAccountVerified() {
		Toast.makeText(this, getString(R.string.setup_account_validated), Toast.LENGTH_LONG).show();
	}

	public void isEchoCalibrationFinished() {
		mPrefs.setAccountEnabled(mPrefs.getAccountCount() - 1, true);
		success();
	}
	
	public void success() {
		mPrefs.firstLaunchSuccessful();
		setResult(Activity.RESULT_OK);
		finish();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (login.getText() == null || login.length() == 0 || password.getText() == null || password.length() == 0 || domain.getText() == null || domain.length() == 0) {
			Toast.makeText(this, getString(R.string.first_launch_no_login_password), Toast.LENGTH_LONG).show();
			return ;
		}
	logIn(login.getText().toString(), password.getText().toString(), domain.getText().toString(), false);
	startActivity(getIntent().setClass(this, AllControlsOperationActivity.class));
	}
	
	
}
