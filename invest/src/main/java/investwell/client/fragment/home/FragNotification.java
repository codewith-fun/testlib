package investwell.client.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.NotificationAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.Utils;
import investwell.utils.customView.CustomDialog;

public class FragNotification extends Fragment implements ToolbarFragment.ToolbarCallback, CustomDialog.DialogBtnCallBack {
    private RecyclerView mNotifyRecycle;
    private AppSession mSession;
    private NotificationAdapter notificationAdapter;
    private MainActivity mActivity;
    private BrokerActivity mBrokerActivity;
    private View view;
    private String txtNotification="";
    private ToolbarFragment fragToolBar;
    private CustomDialog customDialog;
    private String notificationListStatus="";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mActivity.setMainVisibility(this, null);
        }else if(context instanceof BrokerActivity){
            this.mBrokerActivity = (BrokerActivity) context;
            mSession = AppSession.getInstance(mBrokerActivity);
            mBrokerActivity.setMainVisibility(this, null);
        }
    }
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;
    @Override
    public void onStop(){
        super.onStop();

        AppApplication.notification = "";
        Intent myIntent = new Intent("FBR-IMAGE");
        myIntent.putExtra("action", AppApplication.notification);
        getActivity().sendBroadcast(myIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_frag_notification, container, false);


        mNotifyRecycle = view.findViewById(R.id.notify_recycle);


        errorContentInitializer(view);
        mNotifyRecycle.setHasFixedSize(true);
        mNotifyRecycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        customDialog = new CustomDialog(this);
        notificationAdapter = new NotificationAdapter(getActivity(), new ArrayList<JSONObject>());
        mNotifyRecycle.setAdapter(notificationAdapter);

        if (mSession.getNotification().isEmpty()) {
            mNotifyRecycle.setVisibility(View.GONE);
            viewNoData.setVisibility(View.VISIBLE);
        } else {
            mNotifyRecycle.setVisibility(View.VISIBLE);
            viewNoData.setVisibility(View.GONE);
            setRecycleData();
        }
        setUpToolBar();

        return view;
    }
    //Error Content Notification
    private void errorContentInitializer(View view) {
        viewNoData = view.findViewById(R.id.content_no_data);
        tvErrorMessage = viewNoData.findViewById(R.id.tv_error_message);
        ivErrorImage = viewNoData.findViewById(R.id.iv_error_image);
        ivErrorImage.setImageResource(R.drawable.notification_error);
        tvErrorMessage.setText(R.string.notifications_paceholder_txt);
    }


    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(getActivity(), ContextCompat.getColor(getActivity(),R.color.colorPrimary));

            }
            if(notificationListStatus.equalsIgnoreCase("show notification")) {
                fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_notifications),
                        true, false, false, true, false, false, false, getResources().getString(R.string.clear));
                fragToolBar.setCallback(this);
            }else{
                fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_notifications),
                        true, false, false, false, false, false, false, "");
                fragToolBar.setCallback(this);
            }
        }

    }

    private void showDialog(String msg, boolean showCancelBtn, boolean showDoneBtn) {
        if (showCancelBtn && showDoneBtn) {
            customDialog.showDialog(getActivity(), getResources().getString(R.string.alert_dialog_header_txt),
                    msg,
                    getResources().getString(R.string.dialog_no_mandate_btn_proceed_txt),
                    getResources().getString(R.string.dialog_no_mandate_btn_cancel_txt), true, true);
        } else {
            customDialog.showDialog(getActivity(), getResources().getString(R.string.alert_dialog_header_txt),
                    msg,
                    getResources().getString(R.string.dialog_no_mandate_btn_proceed_txt),
                    getResources().getString(R.string.dialog_no_mandate_btn_cancel_txt), false, true);
        }

    }

    private void setRecycleData() {
        try {
            JSONArray jsonArray = new JSONArray(mSession.getNotification());
            ArrayList<JSONObject> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);

                list.add(jsonObject);
            }
            Collections.reverse(list);
            if (list.size() > 0) {
                notificationListStatus = "show notification";
                mSession.setNotifyIcon("yes");
            }else{
                notificationListStatus = "hide notification";
                mSession.setNotifyIcon("no");
            }

            notificationAdapter.updateList(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


  /*  public void showDialog(String message, final Boolean value) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout_gridview_type_two_a.dialog_okk);
        TextView msg_txt = dialog.findViewById(R.id.msg_txt);
        TextView tvOk = dialog.findViewById(R.id.tvOk);

        msg_txt.setText(message);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (value == true) {
                    mSession.setNotification("");
                    llPlaceholderView.setVisibility(View.VISIBLE);
                    mNotifyRecycle.setVisibility(View.GONE);
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        });


        dialog.setCancelable(true);
        dialog.show();

    }*/

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.btn_add_new) {
            if (mSession.getNotification().isEmpty()) {
                /*    showDialog("No Notification Found", false);*/
                showDialog(getResources().getString(R.string.alert_empty_notification_header_txt), true, false);
                txtNotification = "No Operations";

            } else {
                showDialog(getResources().getString(R.string.alert_dialog_notification_clear_txt), true, true);
            }
        }
    }

    @Override
    public void onDialogBtnClick(View view) {
        int id = view.getId();
        if (id == R.id.btDone) {
            if (txtNotification.equalsIgnoreCase("No Operations")) {
                viewNoData.setVisibility(View.VISIBLE);
                mNotifyRecycle.setVisibility(View.GONE);

            } else {
                mSession.setNotification("");
                mNotifyRecycle.setVisibility(View.GONE);
                //TODO nothing
            }
        } else if (id == R.id.btCalcel) {
        }
    }
}
