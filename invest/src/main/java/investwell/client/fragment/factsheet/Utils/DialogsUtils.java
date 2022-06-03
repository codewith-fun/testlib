package investwell.client.fragment.factsheet.Utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;

import com.iw.acceleratordemo.R;


public class DialogsUtils extends Dialog {
    private static DialogsUtils mDialogsUtils;
    private DialogsUtils mProgressbar;
    private OnDismissListener mOnDissmissListener;

    private DialogsUtils(Context context) {
        super(context, R.style.full_screen_dialog);
        setContentView(R.layout.progress_piggy);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    public DialogsUtils(Context context, Boolean instance) {
        super(context);
        mProgressbar = new DialogsUtils(context);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mOnDissmissListener != null) {
            mOnDissmissListener.onDismiss(this);
        }
    }

    public static void showProgressBar(Context context, boolean cancelable) {
        showProgressBar(context, cancelable, null);
    }

    public static void showProgressBar(Context context, boolean cancelable, String message) {
        if (mDialogsUtils != null && mDialogsUtils.isShowing()) {
            mDialogsUtils.cancel();
        }
        mDialogsUtils = new DialogsUtils(context);
        mDialogsUtils.setCancelable(cancelable);
        mDialogsUtils.show();

    }

    public static void showProgressBar(Context context, OnDismissListener listener) {

        if (mDialogsUtils != null && mDialogsUtils.isShowing()) {
            mDialogsUtils.cancel();
        }
        mDialogsUtils = new DialogsUtils(context);
        mDialogsUtils.setListener(listener);
        mDialogsUtils.setCancelable(Boolean.TRUE);
        mDialogsUtils.show();
    }

    public static void hideProgressBar() {
        if (mDialogsUtils != null) {
            mDialogsUtils.dismiss();
        }
    }

    private void setListener(OnDismissListener listener) {
        mOnDissmissListener = listener;

    }

    public static void showListViewBottomProgressBar(View view) {
        if (mDialogsUtils != null) {
            mDialogsUtils.dismiss();
        }

        view.setVisibility(View.VISIBLE);
    }

    public static void hideListViewBottomProgressBar(View view) {
        if (mDialogsUtils != null) {
            mDialogsUtils.dismiss();
        }

        view.setVisibility(View.GONE);
    }

    public void showProgress(Context context, boolean cancelable, String message) {

        if (mProgressbar != null && mProgressbar.isShowing()) {
            mProgressbar.cancel();
        }
        mProgressbar.setCancelable(cancelable);
        mProgressbar.show();
    }

}