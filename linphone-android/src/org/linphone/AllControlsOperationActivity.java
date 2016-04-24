package org.linphone;

import static android.content.Intent.ACTION_MAIN;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.mediastream.Log;
import org.linphone.ui.LinphoneSliders;
import org.linphone.ui.LinphoneSliders.LinphoneSliderTriggered;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AllControlsOperationActivity extends FragmentActivity  {

    private RadioGroup rgs;
	private LinphoneCoreListenerBase mListener;
    public List<Fragment> fragments = new ArrayList<Fragment>();
	private static final int CALL_ACTIVITY = 19;
    private static final int CHAT_ACTIVITY = 21;
	private OrientationEventListener mOrientationHelper;
	private static AllControlsOperationActivity instance;
	
	static final boolean isInstanciated() {
		return instance != null;
	}

	public static final AllControlsOperationActivity instance() {
		if (instance != null)
			return instance;
		throw new RuntimeException("ÊµÀý»¯³ö´í");
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		ContactsManager.getInstance().initializeSyncAccount(getApplicationContext(), getContentResolver());

	 	if(!LinphonePreferences.instance().isContactsMigrationDone()){
			ContactsManager.getInstance().migrateContacts();
			LinphonePreferences.instance().contactsMigrationDone();
		}
		setContentView(R.layout.activity_all_controls_operation);
		instance = this;
		fragments.add(new DataListOperationFragment());
		fragments.add(new MapOperationFragment());
		fragments.add(new SettingsFragment());
		rgs=(RadioGroup) findViewById(R.id.radiogroup);
        FragmentAdapter tabAdapter = new FragmentAdapter(this, fragments, R.id.tab_content, rgs);
        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentAdapter.OnRgsExtraCheckedChangedListener(){
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                System.out.println("Extra---- " + index + " checked!!! ");
            }
        });
        
        
		if (!LinphoneManager.isInstanciated()) {
			Log.e("No service running: avoid crash by starting the launcher", this.getClass().getName());
			// super.onCreate called earlier
			finish();
			startActivity(getIntent().setClass(this, LinphoneLauncherActivity.class));
			return;
		}
        
        mListener = new LinphoneCoreListenerBase(){
			@Override
			public void messageReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneChatMessage message) {

			}

			@Override
			public void registrationState(LinphoneCore lc, LinphoneProxyConfig proxy, LinphoneCore.RegistrationState state, String smessage) {
				if (state.equals(RegistrationState.RegistrationCleared)) {
					if (lc != null) {
						LinphoneAuthInfo authInfo = lc.findAuthInfo(proxy.getIdentity(), proxy.getRealm(), proxy.getDomain());
						if (authInfo != null)
							lc.removeAuthInfo(authInfo);
					}
				}
			}

			@Override
			public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State state, String message) {
				if (state == State.IncomingReceived) {					
					startActivity(new Intent(AllControlsOperationActivity.instance, IncomingCallActivity.class));
				} else if (state == State.OutgoingInit) {
					if (call.getCurrentParamsCopy().getVideoEnabled()) {
						startVideoActivity(call);
					} else {
						startIncallActivity(call);
					}
				} else if (state == State.CallEnd || state == State.Error || state == State.CallReleased) {
					// Convert LinphoneCore message for internalization
					if (message != null && message.equals("Call declined.")) {
						displayCustomToast(getString(R.string.error_call_declined), Toast.LENGTH_LONG);
					} else if (message != null && message.equals("Not Found")) {
						displayCustomToast(getString(R.string.error_user_not_found), Toast.LENGTH_LONG);
					} else if (message != null && message.equals("Unsupported media type")) {
						displayCustomToast(getString(R.string.error_incompatible_media), Toast.LENGTH_LONG);
					} else if (message != null && state == State.Error) {
						displayCustomToast(getString(R.string.error_unknown) + " - " + message, Toast.LENGTH_LONG);
					}
					resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
				}

//				int missedCalls = LinphoneManager.getLc().getMissedCallsCount();
//				displayMissedCalls(missedCalls);
			}
		};

		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.addListener(mListener);
		}



	}
	private void decline(LinphoneCall mCall) {
		LinphoneManager.getLc().terminateCall(mCall);
	}
	
	private void answer(LinphoneCall mCall) {
		LinphoneCallParams params = LinphoneManager.getLc().createDefaultCallParameters();
		
		boolean isLowBandwidthConnection = !LinphoneUtils.isHighBandwidthConnection(this);
		if (isLowBandwidthConnection) {
			params.enableLowBandwidth(true);
			Log.d("Low bandwidth enabled in call params");
		}
		
		if (!LinphoneManager.getInstance().acceptCallWithParams(mCall, params)) {
			// the above method takes care of Samsung Galaxy S
			Toast.makeText(this, R.string.couldnt_accept_call, Toast.LENGTH_LONG).show();
		} else {
			if (!LinphoneActivity.isInstanciated()) {
				return;
			}
			final LinphoneCallParams remoteParams = mCall.getRemoteParams();
			if (remoteParams != null && remoteParams.getVideoEnabled() && LinphonePreferences.instance().shouldAutomaticallyAcceptVideoRequests()) {
				LinphoneActivity.instance().startVideoActivity(mCall);
			} else {
				LinphoneActivity.instance().startIncallActivity(mCall);
			}
		}
	}
	
	
	
	
	public void resetClassicMenuLayoutAndGoBackToCallIfStillRunning() {
//		if (dialerFragment != null) {
//			((DialerFragment) dialerFragment).resetLayout(false);
//		}

		if (LinphoneManager.isInstanciated() && LinphoneManager.getLc().getCallsNb() > 0) {
			LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
			if (call.getState() == LinphoneCall.State.IncomingReceived) {
				startActivity(new Intent(AllControlsOperationActivity.this, IncomingCallActivity.class));
			} else if (call.getCurrentParamsCopy().getVideoEnabled()) {
				startVideoActivity(call);
			} else {
				startIncallActivity(call);
			}
		}
	}
	
	
	
	
	public void displayCustomToast(final String message, final int duration) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toastRoot));

		TextView toastText = (TextView) layout.findViewById(R.id.toastMessage);
		toastText.setText(message);

		final Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(duration);
		toast.setView(layout);
		toast.show();
	}
	public void startVideoActivity(LinphoneCall currentCall) {
		Intent intent = new Intent(this, InCallActivity.class);
		intent.putExtra("VideoEnabled", true);
		startOrientationSensor();
		startActivityForResult(intent, CALL_ACTIVITY);
	}

	public void startIncallActivity(LinphoneCall currentCall) {
		Intent intent = new Intent(this, InCallActivity.class);
		intent.putExtra("VideoEnabled", false);
		startOrientationSensor();
		startActivityForResult(intent, CALL_ACTIVITY);
	}
	
	private synchronized void startOrientationSensor() {
		if (mOrientationHelper == null) {
			mOrientationHelper = new LocalOrientationEventListener(this);
		}
		mOrientationHelper.enable();
	}
	private int mAlwaysChangingPhoneAngle = -1;

	private class LocalOrientationEventListener extends OrientationEventListener {
		public LocalOrientationEventListener(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(final int o) {
			if (o == OrientationEventListener.ORIENTATION_UNKNOWN) {
				return;
			}

			int degrees = 270;
			if (o < 45 || o > 315)
				degrees = 0;
			else if (o < 135)
				degrees = 90;
			else if (o < 225)
				degrees = 180;

			if (mAlwaysChangingPhoneAngle == degrees) {
				return;
			}
			mAlwaysChangingPhoneAngle = degrees;

			Log.d("Phone orientation changed to ", degrees);
			int rotation = (360 - degrees) % 360;
			LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
			if (lc != null) {
				lc.setDeviceRotation(rotation);
				LinphoneCall currentCall = lc.getCurrentCall();
				if (currentCall != null && currentCall.cameraEnabled() && currentCall.getCurrentParamsCopy().getVideoEnabled()) {
					lc.updateCall(currentCall, null);
				}
			}
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

//		Bundle extras = intent.getExtras();
//		if (extras != null && extras.getBoolean("GoToChat", false)) {
//			LinphoneService.instance().removeMessageNotification();
//			String sipUri = extras.getString("ChatContactSipUri");
//			displayChat(sipUri);
//		} else if (extras != null && extras.getBoolean("Notification", false)) {
//			if (LinphoneManager.getLc().getCallsNb() > 0) {
//				LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
//				if (call.getCurrentParamsCopy().getVideoEnabled()) {
//					startVideoActivity(call);
//				} else {
//					startIncallActivity(call);
//				}
//			}
//		} else {
//			if (dialerFragment != null) {
//				if (extras != null && extras.containsKey("SipUriOrNumber")) {
//					if (getResources().getBoolean(R.bool.automatically_start_intercepted_outgoing_gsm_call)) {
//						((DialerFragment) dialerFragment).newOutgoingCall(extras.getString("SipUriOrNumber"));
//					} else {
//						((DialerFragment) dialerFragment).displayTextInAddressBar(extras.getString("SipUriOrNumber"));
//					}
//				} else {
//					((DialerFragment) dialerFragment).newOutgoingCall(intent);
//				}
//			}
			if (LinphoneManager.getLc().getCalls().length > 0) {
				LinphoneCall calls[] = LinphoneManager.getLc().getCalls();
				if (calls.length > 0) {
					LinphoneCall call = calls[0];

					if (call != null && call.getState() != LinphoneCall.State.IncomingReceived) {
						if (call.getCurrentParamsCopy().getVideoEnabled()) {
							startVideoActivity(call);
						} else {
							startIncallActivity(call);
						}
					}
				}

				// If a call is ringing, start incomingcallactivity
				Collection<LinphoneCall.State> incoming = new ArrayList<LinphoneCall.State>();
				incoming.add(LinphoneCall.State.IncomingReceived);
				if (LinphoneUtils.getCallsInState(LinphoneManager.getLc(), incoming).size() > 0) {
					if (InCallActivity.isInstanciated()) {
						InCallActivity.instance().startIncomingCallActivity();
					} else {
						startActivity(new Intent(this, IncomingCallActivity.class));
					}
				}
			}
	}
	@Override
	protected void onResume() {
		super.onResume();

		if (!LinphoneService.isReady())  {
			startService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
		}

		ContactsManager.getInstance().prepareContactsInBackground();

//		updateMissedChatCount();
//
//		displayMissedCalls(LinphoneManager.getLc().getMissedCallsCount());

		LinphoneManager.getInstance().changeStatusToOnline();

		if(getIntent().getIntExtra("PreviousActivity", 0) != CALL_ACTIVITY){
			if (LinphoneManager.getLc().getCalls().length > 0) {
				LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
				LinphoneCall.State callState = call.getState();
				if (callState == State.IncomingReceived) {
					startActivity(new Intent(this, IncomingCallActivity.class));
				} else {

						if (call.getCurrentParamsCopy().getVideoEnabled()) {
							startVideoActivity(call);
						} else {
							startIncallActivity(call);
						}
					}
				}
		}
	}	
	@Override
	protected void onDestroy() {
		if (mOrientationHelper != null) {
			mOrientationHelper.disable();
			mOrientationHelper = null;
		}

		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.removeListener(mListener);
		}

		instance = null;
		super.onDestroy();
		System.gc();

	}
	

}
