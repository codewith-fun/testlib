package investwell.client.fragment.mandate;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.FragMendateListAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class FragMendateList extends Fragment implements View.OnClickListener {
    private FragMendateListAdapter mAdapter;
    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private TextView mTvNothing;
    private investwell.utils.customView.CustomButton mCreateButton;
    private ArrayList<JSONObject> mMendateList;
    private String mUCC_Code = "",mCid;
    private ToolbarFragment fragToolBar;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getSIPMendateList();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_mendate_list, container, false);
        mSession = AppSession.getInstance(getActivity());
        mActivity.setMainVisibility(this, null);
        mApplication = (AppApplication) mActivity.getApplication();

        mCreateButton = view.findViewById(R.id.create_acnt_btn);
        mCreateButton.setOnClickListener(this);
        setUpToolBar();

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("ucc_code")) {
            mUCC_Code = bundle.getString("ucc_code");
            mCid = bundle.getString("cid");
        }else {
            mCid = mSession.getCID();
            mUCC_Code = mSession.getUCC_CODE();
        }

        RecyclerView recycleView = view.findViewById(R.id.recycleView);
        mTvNothing = view.findViewById(R.id.tvNothing);
        recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new FragMendateListAdapter(getActivity(), new ArrayList<JSONObject>(), new FragMendateListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                    Bundle bundle = new Bundle();
//                    bundle.putString("ucc_code", mUCC_Code);
                    bundle.putString("cid", mCid);
                    mActivity.displayViewOther(12, bundle);
            }
        });
        recycleView.setAdapter(mAdapter);


        getSIPMendateList();
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.main_nav_title_mandate_list), true, false, false, false,false,false,false,"");
        }
    }

    @Override
    public void onClick(View v) {
        /*  case R.id.ivLeft:
               mActivity.getSupportFragmentManager().popBackStack();
                break;
*/
        if (v.getId() == R.id.create_acnt_btn) {
            Bundle bundle = new Bundle();// throw the data for add account
            bundle.putString("ucc_code", mUCC_Code);
            bundle.putBoolean("isMandate", true);
//                mActivity.displayViewOther(12, bundle);
            bundle.putString("cid", mCid);
            mActivity.displayViewOther(117, bundle);
        }
    }

    private void getSIPMendateList() {
        final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));//{PASSKEY}/{BID}/{UCC}
        String url = Config.GET_MENDATE_LIST;


        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put("UCC", mUCC_Code);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mBar.dismiss();
                    try {
                        mMendateList = new ArrayList<>();
                        if (jsonObject.optBoolean("Status")) {
                            JSONArray araArray = jsonObject.getJSONArray("SIPMandateDetail");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                object.put("UCC", mUCC_Code);
                                mMendateList.add(object);
                            }


                        } else {
                            mTvNothing.setVisibility(View.GONE);
                            if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                                mApplication.showCommonDailog(mActivity, mActivity, false, getString(R.string.fatca_instruction), jsonObject.optString("ServiceMSG"), "invalidPasskey",false,true);
                            } else {
                                mApplication.showCommonDailog(mActivity, mActivity, false, getString(R.string.fatca_instruction), jsonObject.optString("ServiceMSG"), "message",false,true);
                            }
                        }

                        if (mMendateList.size() > 0) {
                            mAdapter.updateList(mMendateList);
                            mTvNothing.setVisibility(View.GONE);
                        } else {
                            mTvNothing.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, volleyError -> {
                mBar.dismiss();
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    try {
                        JSONObject jsonObject1 = new JSONObject(error.getMessage());
                        Toast.makeText(getActivity(), jsonObject1.toString(), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();


            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
