package investwell.utils.customView;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import java.util.Objects;

public class CustomDialog {
    public DialogBtnCallBack dialogBtnCallBack;

    public CustomDialog(DialogBtnCallBack dialogBtnCallBack) {
        this.dialogBtnCallBack = dialogBtnCallBack;
    }

    public void showDialog(Context context, String title, String message, String btnPrimary, String btnSecondary, boolean isShowingPrimaryBtn, boolean isShowingSecondaryBtn) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dailog_common_application, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        LinearLayout linerMain = dialogView.findViewById(R.id.linerMain);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        final CustomButton btDone = dialogView.findViewById(R.id.btDone);
        final CustomButton btCancel = dialogView.findViewById(R.id.btCalcel);

        RelativeLayout relSubMenu = dialogView.findViewById(R.id.relativeLayout);

        tvTitle.setText(!TextUtils.isEmpty(title) ? title : "");
        tvMessage.setText(!TextUtils.isEmpty(message) ? message : "");
        btDone.setText(!TextUtils.isEmpty(btnPrimary) ? btnPrimary : "");
        btCancel.setText(!TextUtils.isEmpty(btnSecondary) ? btnSecondary : "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }



        if (isShowingPrimaryBtn) {
            btDone.setVisibility(View.VISIBLE);
        } else {
            btDone.setVisibility(View.GONE);
        }

        if (isShowingSecondaryBtn) {
            btCancel.setVisibility(View.VISIBLE);
        } else {
            btCancel.setVisibility(View.GONE);
        }
        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBtnCallBack.onDialogBtnClick(btDone);
                alertDialog.dismiss();
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBtnCallBack.onDialogBtnClick(btCancel);
                alertDialog.dismiss();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    public interface DialogBtnCallBack {
        void onDialogBtnClick(View view);
    }
}
