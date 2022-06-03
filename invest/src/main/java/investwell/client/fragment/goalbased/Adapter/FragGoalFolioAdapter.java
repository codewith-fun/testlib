package investwell.client.fragment.goalbased.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.fragment.goalbased.Fragment.FragGoalSummeryDetails;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.CounterHandler;

public class FragGoalFolioAdapter extends RecyclerView.Adapter<FragGoalFolioAdapter.ViewHolder> implements CounterHandler.CounterListener {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private FragGoalFolioAdapter.OnItemClickListener listener;
    private AppSession mSession;
    private String mGoalId;
    private FragGoalSummeryDetails mPFrag;
    private FragGoalFolioMenuAdapter fragGoalFolioMenuAdapter;
    private int percent;
    private EditText mEtPercent;
    private long number;
    private Button mNegativeBtn, mPositiveBtn, mAllotBtn;
    private String whomVisible = "";

    public FragGoalFolioAdapter(Context context, ArrayList<JSONObject> list, String goalid, FragGoalSummeryDetails details, FragGoalFolioAdapter.OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
        this.mGoalId = goalid;
        this.mPFrag = details;

        mSession = AppSession.getInstance(mContext);

    }

    @Override
    public FragGoalFolioAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_goal_folio, viewGroup, false);
        return new FragGoalFolioAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FragGoalFolioAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.setItem(position, listener);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void updateList(List<JSONObject> list) {
        mDataList.clear();

        mDataList.addAll(list);
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvMarketValue, mTvFolioNo, mTvSchemeName;
        CheckBox mCbFolioName;
        ImageView mIvMenu;
        View mBottomView;
        RelativeLayout rlGoalParent;

        public ViewHolder(View view) {
            super(view);
            mCbFolioName = view.findViewById(R.id.cb_folioTitle);
            mTvMarketValue = view.findViewById(R.id.tvMarketVal);
            mTvFolioNo = view.findViewById(R.id.tvfolioNum);
            mTvSchemeName = view.findViewById(R.id.tv_SchemeName);
            mIvMenu = view.findViewById(R.id.ivMenu);
            mBottomView = view.findViewById(R.id.bottomView);
            rlGoalParent = view.findViewById(R.id.rl_goal_parent);
        }


        public void setItem(final int position, final FragGoalFolioAdapter.OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);

            mTvSchemeName.setText(jsonObject.optString("SchemeName"));
            mTvMarketValue.setText(mContext.getString(R.string.rs) + jsonObject.optString("InvestedAmount"));
//            mTvFolioName.setText(jsonObject.optString("FolioName"));
            if (jsonObject.optString("UnAllocated").equalsIgnoreCase("0%")) {
                mTvFolioNo.setText(jsonObject.optString("FirstName") + " (" + jsonObject.optString("FolioNo") + ")");
            } else {
                mTvFolioNo.setText(jsonObject.optString("FirstName") + " (" + jsonObject.optString("FolioNo") + ") " + jsonObject.optString("UnAllocated"));
            }
            final String fcode = jsonObject.optString("Fcode");
            final String scode = jsonObject.optString("Scode");
            final String folioNo = jsonObject.optString("FolioNo");
            //String goalid = jsonObject.optString("GoalID");
            final JSONArray jsonArray = jsonObject.optJSONArray("ChildResponseData");


            ArrayList<String> goalid = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                goalid.add(jsonObject1.optString("GoalID"));
            }

            if (goalid.contains(mGoalId)) {
                mCbFolioName.setChecked(true);
                mCbFolioName.setEnabled(true);
                mTvSchemeName.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                mTvMarketValue.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                mBottomView.setBackgroundColor(mContext.getResources().getColor(R.color.colorGreen));
                mIvMenu.setVisibility(View.VISIBLE);
                mIvMenu.setEnabled(true);
                int[][] states = new int[][]{
                        new int[]{-android.R.attr.state_enabled}, // disabled
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{}  // default
                };

                int[] colors = new int[]{
                        mContext.getResources().getColor(R.color.colorGreen),
                        mContext.getResources().getColor(R.color.colorGreen),
                        mContext.getResources().getColor(R.color.colorGreen)
                };

                ColorStateList colorStateList = new ColorStateList(states, colors);
                CompoundButtonCompat.setButtonTintList(mCbFolioName, colorStateList);

            } else if (jsonArray.length() == 0) {
                mCbFolioName.setChecked(false);
                mCbFolioName.setEnabled(true);
                mTvSchemeName.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                mTvMarketValue.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                mBottomView.setBackgroundColor(mContext.getResources().getColor(R.color.colorOrange));
                mIvMenu.setVisibility(View.INVISIBLE);
                mIvMenu.setEnabled(false);

                int[][] states = new int[][]{
                        new int[]{-android.R.attr.state_enabled}, // disabled
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{}  // default
                };

                int[] colors = new int[]{
                        mContext.getResources().getColor(R.color.colorOrange),
                        mContext.getResources().getColor(R.color.colorOrange),
                        mContext.getResources().getColor(R.color.colorOrange)
                };

                ColorStateList colorStateList = new ColorStateList(states, colors);


                CompoundButtonCompat.setButtonTintList(mCbFolioName, colorStateList);
            } else if (jsonObject.optString("UnAllocated").equalsIgnoreCase("0%")) {
                mCbFolioName.setChecked(true);
                mCbFolioName.setEnabled(false);
                mTvSchemeName.setTextColor(mContext.getResources().getColor(R.color.colorGrey_400));
                mTvMarketValue.setTextColor(mContext.getResources().getColor(R.color.colorGrey_400));
                mBottomView.setBackgroundColor(mContext.getResources().getColor(R.color.colorGrey_400));
                mIvMenu.setVisibility(View.VISIBLE);
                mIvMenu.setEnabled(true);
                int[][] states = new int[][]{
                        new int[]{-android.R.attr.state_enabled}, // disabled
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{}  // default
                };

                int[] colors = new int[]{
                        mContext.getResources().getColor(R.color.colorGrey_400),
                        mContext.getResources().getColor(R.color.colorGrey_400),
                        mContext.getResources().getColor(R.color.colorGrey_400)
                };

                ColorStateList colorStateList = new ColorStateList(states, colors);

                CompoundButtonCompat.setButtonTintList(mCbFolioName, colorStateList);
            } else {
                mCbFolioName.setChecked(false);
                mCbFolioName.setEnabled(true);
                int[][] states = new int[][]{
                        new int[]{-android.R.attr.state_enabled}, // disabled
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{}  // default
                };

                int[] colors = new int[]{
                        mContext.getResources().getColor(R.color.colorOrange),
                        mContext.getResources().getColor(R.color.colorOrange),
                        mContext.getResources().getColor(R.color.colorOrange)
                };

                ColorStateList colorStateList = new ColorStateList(states, colors);

                CompoundButtonCompat.setButtonTintList(mCbFolioName, colorStateList);
                mTvSchemeName.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                mTvMarketValue.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                mBottomView.setBackgroundColor(mContext.getResources().getColor(R.color.colorOrange));
                mIvMenu.setVisibility(View.VISIBLE);
                mIvMenu.setEnabled(true);

            }

            mCbFolioName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String check = "Unmap";
                    if (mCbFolioName.isChecked()) {
                        check = "Map";
                        showSelectionDialog(mCbFolioName, check, position, mTvSchemeName.getText().toString(), jsonObject.optString("FirstName"), jsonObject.optString("FolioNo"), jsonObject.optString("UnAllocated").replace("%", ""), jsonObject.optString("Fcode"), jsonObject.optString("Scode"));

                    } else {
                        check = "Unmap";
                        folioMapUnmap(check, position, jsonObject.optString("UnAllocated"), mGoalId, fcode, scode, folioNo);
                    }


                }
            });

            mIvMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showFolioDetail(mTvSchemeName.getText().toString(), jsonObject.optString("FirstName"), jsonObject.optString("FolioNo"), jsonObject.optString("UnAllocated"), jsonObject, jsonArray);
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    private void showSelectionDialog(final CheckBox checkBox, final String status, final int position, String SchemeName, String name, final String folioNo, final String UnAllocated, final String Fcode, final String Scode) {

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.folio_dialog);
        ImageView mIvClose = dialog.findViewById(R.id.ivClose);
        TextView mTvSchemeName = dialog.findViewById(R.id.tvSchemeName);
        TextView mTvDetail = dialog.findViewById(R.id.tvDetail);
        mEtPercent = dialog.findViewById(R.id.etPercent);
        mNegativeBtn = dialog.findViewById(R.id.negative_btn);
        mPositiveBtn = dialog.findViewById(R.id.positive_btn);
        mAllotBtn = dialog.findViewById(R.id.allot_btn);

        mPositiveBtn.setVisibility(View.INVISIBLE);
        mEtPercent.setText(UnAllocated);
        mTvDetail.setText(name + " (" + folioNo + ")");
        percent = Integer.parseInt(mEtPercent.getText().toString());
        number = Long.parseLong(mEtPercent.getText().toString());


        new CounterHandler.Builder()
                .incrementalView(mPositiveBtn)
                .decrementalView(mNegativeBtn)
                .minRange(0) // cant go any less than -50
                .maxRange(percent)
                .startNumber(number)// cant go any further than 50
                .isCycle(true) // 49,50,-50,-49 and so on
                .counterDelay(200) // speed of counter
                .counterStep(1)  // steps e.g. 0,2,4,6...
                .listener(this) // to listen counter results and show them in app
                .build();
        mTvSchemeName.setText(SchemeName);

        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBox.setChecked(false);
                dialog.dismiss();
            }
        });

        mAllotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                folioMapUnmap(status, position, mEtPercent.getText().toString(), mGoalId, Fcode, Scode, folioNo);
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }


    @Override
    public void onIncrement(View view, long num) {
        mEtPercent.setText(String.valueOf(num));
        if (num == percent) {
            mPositiveBtn.setVisibility(View.INVISIBLE);
        } else {
            mPositiveBtn.setVisibility(View.VISIBLE);
        }
        mNegativeBtn.setVisibility(View.VISIBLE);
        mAllotBtn.setEnabled(true);
    }

    @Override
    public void onDecrement(View view, long num) {
        mEtPercent.setText(String.valueOf(num));
        if (num < percent) {
            mPositiveBtn.setVisibility(View.VISIBLE);
        } else {
            mPositiveBtn.setVisibility(View.INVISIBLE);
        }
        if (mEtPercent.getText().toString().equalsIgnoreCase("0")) {
            mNegativeBtn.setVisibility(View.INVISIBLE);
            mAllotBtn.setEnabled(false);
        } else {
            mNegativeBtn.setVisibility(View.VISIBLE);
            mAllotBtn.setEnabled(true);
        }
    }


    public void showFolioDetail(String SchemeName, String ApplicantName, final String Folio, String Unallocated, final JSONObject Obj, JSONArray jsonArray) {

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.folio_dialog_detail);
        ImageView mIvClose = dialog.findViewById(R.id.ivClose);
        RecyclerView mSchemeRecycle = dialog.findViewById(R.id.schemeRecycle);
        LinearLayout mllUnallocated = dialog.findViewById(R.id.llUnallocated);
        TextView mTvScemeName = dialog.findViewById(R.id.tvSchemeName);
        TextView mTvDetail = dialog.findViewById(R.id.tvDetail);
        TextView mTvPercent = dialog.findViewById(R.id.tvPercent);
        mTvPercent.setText(Unallocated);
        if (Unallocated.equalsIgnoreCase("0%")) {
            mllUnallocated.setVisibility(View.GONE);
        } else {
            mllUnallocated.setVisibility(View.VISIBLE);
        }
        mSchemeRecycle.setHasFixedSize(true);
        mSchemeRecycle.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        fragGoalFolioMenuAdapter = new FragGoalFolioMenuAdapter(mContext, new ArrayList<JSONObject>(), new FragGoalFolioMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                JSONObject jsonObject = fragGoalFolioMenuAdapter.mDataList.get(position);
                folioMapUnmap("Unmap", position, jsonObject.optString("Allocation"), jsonObject.optString("GoalID"), Obj.optString("Fcode"), Obj.optString("Scode"), Folio);
                dialog.dismiss();
            }
        });

        mSchemeRecycle.setAdapter(fragGoalFolioMenuAdapter);
        mTvScemeName.setText(SchemeName);
        mTvDetail.setText(ApplicantName + " (" + Folio + ")");

        ArrayList<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            list.add(jsonObject);
        }

        fragGoalFolioMenuAdapter.updateList(list);
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private void folioMapUnmap(String action, int pos, String percent, String GoalId, String Fcode, String Scode, String FolioNo) {
        final ProgressDialog mBar = ProgressDialog.show(mContext, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.GOAL_FOLIO_Maping;

        JSONObject jsonParam = new JSONObject();
        JSONObject object = mDataList.get(pos);

        try {
            jsonParam.put("Passkey", mSession.getPassKey());
            jsonParam.put("Bid", AppConstants.APP_BID);
            jsonParam.put("Cid", mSession.getCID());
            jsonParam.put("GoalID", GoalId);
            jsonParam.put("FolioNo", FolioNo);
            jsonParam.put("Fcode", Fcode);
            jsonParam.put("Action", action);
            jsonParam.put("Scode", Scode);
            jsonParam.put("Allocation", percent);
        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                mBar.dismiss();
                try {
                    if (jsonObject.optBoolean("Status")) {
                        mPFrag.getGoalDetail();
                        mPFrag.getfoliolist();
                        Toast.makeText(mContext, jsonObject.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(mContext, jsonObject.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mBar.dismiss();

                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    try {
                        JSONObject jsonObject = new JSONObject(error.getMessage());
                        Toast.makeText(mContext, jsonObject.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }


}


