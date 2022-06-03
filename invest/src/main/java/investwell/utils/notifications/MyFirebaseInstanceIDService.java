package investwell.utils.notifications;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.iw.acceleratordemo.R;

import investwell.utils.AppConstants;
import investwell.utils.AppSession;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        FirebaseMessaging.getInstance().subscribeToTopic(getApplicationContext().getString(R.string.TokenKey)+ AppConstants.APP_BID);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        AppSession appSession = AppSession.getInstance(getApplicationContext());
        appSession.setFcmToken(refreshedToken);

    }

}
