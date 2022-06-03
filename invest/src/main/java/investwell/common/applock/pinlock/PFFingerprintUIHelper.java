package investwell.common.applock.pinlock;

import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;
import android.widget.ImageView;

import com.iw.acceleratordemo.R;


/**
 * Created by aleksandr on 2018/02/10.
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public class PFFingerprintUIHelper extends FingerprintManagerCompat.AuthenticationCallback {

    private static final long ERROR_TIMEOUT_MILLIS = 1600;
    private static final long SUCCESS_DELAY_MILLIS = 200;

    private final FingerprintManagerCompat mFingerprintManager;
    private final ImageView mIcon;
    private final PFFingerprintAuthListener mCallback;
    private CancellationSignal mCancellationSignal;

    private boolean mSelfCancelled;

    public PFFingerprintUIHelper(FingerprintManagerCompat fingerprintManager,
                                 ImageView icon,
                                 PFFingerprintAuthListener callback) {
        super();
        mFingerprintManager = fingerprintManager;
        mIcon = icon;
        mCallback = callback;
    }

    public boolean isFingerprintAuthAvailable() {
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        return mFingerprintManager.isHardwareDetected()
                && mFingerprintManager.hasEnrolledFingerprints();
    }

    public void startListening(FingerprintManagerCompat.CryptoObject cryptoObject) {
        if (!isFingerprintAuthAvailable()) {
            return;
        }
        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        mFingerprintManager.authenticate(
                cryptoObject, 0, mCancellationSignal, this, null);
        mIcon.setImageResource(R.mipmap.thumb_inactive);
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!mSelfCancelled) {
            showError(errString);
            mIcon.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCallback.onError();
                }
            }, 10);
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        mCallback.onError();
        showError(helpString);
    }

    @Override
    public void onAuthenticationFailed() {
        mCallback.onError();
        showError(mIcon.getResources().getString(
                R.string.fingerprint_not_recognized_pf));
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        mIcon.setImageResource(R.mipmap.thumb_active);
        mIcon.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCallback.onAuthenticated();
            }
        }, SUCCESS_DELAY_MILLIS);
    }

    private void showError(CharSequence error) {
        mIcon.setImageResource(R.mipmap.thumb_error);

        mIcon.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIcon.setImageResource(R.mipmap.thumb_inactive);
            }
        }, ERROR_TIMEOUT_MILLIS);

    }


}
