package investwell.common.applock.pinlock;

/**
 * Created by aleksandr on 2018/02/14.
 */

public interface PFFingerprintAuthListener {

    void onAuthenticated();

    void onError();
}
