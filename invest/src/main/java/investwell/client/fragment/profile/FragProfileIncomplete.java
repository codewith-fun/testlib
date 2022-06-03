package investwell.client.fragment.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.customView.CustomTextViewRegular;

public class FragProfileIncomplete extends Fragment implements View.OnClickListener {

    private ProgressDialog mBar;
    private AppSession mSession;
    private MainActivity mActivity;
    private BrokerActivity mBrokerActivity;
    private AppApplication mApplication;
    private ImageView mIvImage1, mIvImage2, mIvImage3, mIvImage4, mIvImage5, mIvImage6, mIvImage7;
    private ImageView mIvArrow1;
    private String ComingFrom;
    private CardView mFatcaCard;
    private ToolbarFragment fragToolBar;
    private View view;
    private Bundle bundle;
    private String mCid, mUcc;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BrokerActivity) {
            this.mBrokerActivity = (BrokerActivity) context;
            mBrokerActivity.setMainVisibility(this, null);
            mApplication = (AppApplication) mBrokerActivity.getApplication();
            mSession = AppSession.getInstance(mBrokerActivity);

        } else if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);
            mSession = AppSession.getInstance(mActivity);
            mApplication = (AppApplication) mActivity.getApplication();
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_profile_incomplete, container, false);
        bundle = getArguments();
        setUpToolBar();
        setInitializer();
        UpdateView();
        return view;
    }


    /***************************************************
     Method contains all initializations of elements
     *****************************************************/

    private void setInitializer() {
        mIvImage1 = view.findViewById(R.id.profile_small);
        mIvImage2 = view.findViewById(R.id.mandate_small);
        mIvImage3 = view.findViewById(R.id.signature_small);
        mIvImage4 = view.findViewById(R.id.fatca_small);
        mIvImage5 = view.findViewById(R.id.cheque_small);
        mIvImage6 = view.findViewById(R.id.pan_small);
        mIvImage7 = view.findViewById(R.id.ivBank);

        mIvArrow1 = view.findViewById(R.id.ivArrow1);

        ImageView mProfileImage = view.findViewById(R.id.profile_icon);
        CustomTextViewRegular mTvUsername = view.findViewById(R.id.card_user_name);
        CustomTextViewRegular tvName = view.findViewById(R.id.et_name);

        CardView cvProfile = view.findViewById(R.id.cvProfile);
        CardView cvMandate = view.findViewById(R.id.cvMandate);
        CardView cvSignature = view.findViewById(R.id.cvSignature);
        CardView cvFatca = view.findViewById(R.id.cvFatca);

        cvProfile.setOnClickListener(this);
        cvMandate.setOnClickListener(this);
        cvSignature.setOnClickListener(this);
        cvFatca.setOnClickListener(this);
        view.findViewById(R.id.cvCheque).setOnClickListener(this);
        view.findViewById(R.id.cvPANKyc).setOnClickListener(this);
        view.findViewById(R.id.cvBank).setOnClickListener(this);
        view.findViewById(R.id.profile_detail).setOnClickListener(this);

        if (bundle != null && bundle.containsKey("cid")) {
            mCid = bundle.getString("cid");
            mUcc = bundle.getString("ucc_code");
        } else {
            mCid = mSession.getCID();
            mUcc = mSession.getUCC_CODE();
        }

        if (bundle != null && bundle.containsKey("InvestorName")) {
            mTvUsername.setText(bundle.getString("InvestorName"));

        } else if ((mSession.getLoginType().equals("Broker") || mSession.getLoginType().equals("SubBroker") || mSession.getLoginType().equals("RM") || mSession.getLoginType().equalsIgnoreCase("Zone")
                || mSession.getLoginType().equalsIgnoreCase("Region")
                || mSession.getLoginType().equalsIgnoreCase("Branch"))) {
            mTvUsername.setText(mSession.getBrokerFullName());
        } else if (!mSession.getFullName().isEmpty()) {
            mTvUsername.setText(mSession.getFullName());
        } else {
            mTvUsername.setText("Dear Investor");
        }

        String name = mTvUsername.getText().toString().trim();
        String symbolText = "";
        if (!name.equals("")) {
            String[] m = name.split(" ", 2);
            for (int i = 0; i < m.length; i++) {
                symbolText = symbolText + m[i].charAt(0);
            }

        }
        tvName.setText(symbolText);

/*
        if (bundle!=null && bundle.containsKey("ProfilePic")){
            tvName.setText(usertext);
        }else if (mSession.getHasLoging() && mSession.getImageRawData().length() > 0) {
            tvName.setText(usertext);
            *//*String path = mSession.getImageRawData();
            Picasso.get().load(path).error(R.mipmap.profileplaceholder).transform(new CircleTransform()).into(mProfileImage);
      *//*  } else {
            tvName.setText(usertext);
        }*/
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.main_nav_title_profile_detail),
                    true, false, false, false, false, false, false, "");

        }
    }

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        int id = view.getId();
        if (id == R.id.cvPANKyc) {
            bundle.putString("cid", mCid);
            bundle.putString("coming_from", "IncompleteProfile");
            mActivity.displayViewOther(5, bundle);
        } else if (id == R.id.cvProfile) {
            if (mSession.getLoginType().equalsIgnoreCase("Client") || mSession.getLoginType().equalsIgnoreCase("ClientG")
                    || mSession.getLoginType().equalsIgnoreCase("Prospects")) {
                mActivity.displayViewOther(5, bundle);
            }
        } else if (id == R.id.profile_detail) {
            bundle.putString("coming_from", "Investment Profile");
            bundle.putString("ucc_code", mUcc);
            bundle.putString("cid", mCid);
            mActivity.displayViewOther(82, bundle);
        } else if (id == R.id.cvMandate) {
            bundle.putString("ucc_code", mUcc);
            if (!mUcc.equals("NA"))
                //mActivity.displayViewOther(12, bundle);
                mActivity.displayViewOther(37, bundle);
            else {
                Toast.makeText(mActivity, "Please complete profile before mandate", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.cvSignature) {
            if (!mUcc.equals("NA")) {
                bundle.putString("ucc_code", mUcc);
                mActivity.displayViewOther(124, bundle);

                   /* Intent intent = new Intent(getActivity(), SignatureActivity.class);
                    intent.putExtra("ucc_code", mCid);
                    startActivityForResult(intent, 100);*/
            } else {
                Toast.makeText(mActivity, "Please complete profile before Signature upload", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.cvCheque) {
            System.out.println();
            if (!mUcc.equals("NA")) {
                bundle.putString("ucc_code", mUcc);
                mActivity.displayViewOther(124, bundle);

                /* mActivity.displayViewOther(94, bundle);*/
            } else {
                Toast.makeText(mActivity, "Please complete profile before Signature upload", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.cvFatca) {
            ProfileDetail(mUcc);
        } else if (id == R.id.cvBank) {
            Log.i("allbanks", "onClick: " + mCid + " and " + mUcc);
            bundle.putString("coming_from", "Investment Profile");
            bundle.putString("ucc_code", mUcc);
            bundle.putString("cid", mCid);
            mActivity.displayViewOther(117, bundle);
        }
    }

    private void UpdateView() {

        if (bundle != null && bundle.containsKey("allData")) {
            try {
                JSONObject jsonObject = new JSONObject(bundle.getString("allData"));
                if (jsonObject.optString("ActiveStatus").equalsIgnoreCase("N")) {
                    view.findViewById(R.id.profile_detail).setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.profile_detail).setVisibility(View.VISIBLE);
                }

                if (jsonObject.optString("ActiveStatus").equalsIgnoreCase("Y") && jsonObject.optString("DocUploadStatus").equalsIgnoreCase("Y")) {
                    view.findViewById(R.id.cvSignature).setEnabled(false);
                    view.findViewById(R.id.signature_arrow).setVisibility(View.GONE);

                    if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
                        view.findViewById(R.id.cvCheque).setEnabled(true);
                        view.findViewById(R.id.cheque_arrow).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.cvCheque).setEnabled(false);
                        view.findViewById(R.id.cheque_arrow).setVisibility(View.GONE);
                        view.findViewById(R.id.cvCheque).setVisibility(View.GONE);
                        mIvImage5.setVisibility(View.GONE);
                        view.findViewById(R.id.hlcheque).setVisibility(View.GONE);
                    }


                } else if (jsonObject.optString("ActiveStatus").equalsIgnoreCase("N") &&
                        (jsonObject.optString("DocUploadStatus").equalsIgnoreCase("Y") || jsonObject.optString("DocUploadStatus").equalsIgnoreCase("N"))) {

                    view.findViewById(R.id.cvSignature).setEnabled(true);
                    view.findViewById(R.id.signature_arrow).setVisibility(View.VISIBLE);


                }


                if (jsonObject.optString("CAFStatus").equalsIgnoreCase("Y")) {
                    mIvImage1.setImageResource(R.mipmap.green_checked);
                    mIvArrow1.setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.cvProfile).setEnabled(false);
                    view.findViewById(R.id.profile_detail).setVisibility(View.VISIBLE);

                } else {
                    mIvImage1.setImageResource(R.mipmap.red_cross);
                    mIvArrow1.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.cvProfile).setEnabled(true);
                    view.findViewById(R.id.profile_detail).setVisibility(View.GONE);
                }

                if (jsonObject.optString("MandateStatus").equalsIgnoreCase("Y")) {
                    mIvImage4.setImageResource(R.mipmap.green_checked);
                } else {
                    mIvImage4.setImageResource(R.mipmap.red_cross);
                }

                if (jsonObject.optString("DocUploadStatus").equalsIgnoreCase("Y")) {
                    mIvImage3.setImageResource(R.mipmap.green_checked);
                    mIvImage5.setImageResource(R.mipmap.green_checked);
                    view.findViewById(R.id.signature_arrow).setVisibility(View.GONE);
                    view.findViewById(R.id.cvSignature).setEnabled(false);

                    if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
                        view.findViewById(R.id.cvCheque).setEnabled(true);
                        view.findViewById(R.id.cheque_arrow).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.cvCheque).setEnabled(false);
                        view.findViewById(R.id.cheque_arrow).setVisibility(View.GONE);
                        view.findViewById(R.id.cvCheque).setVisibility(View.GONE);
                        mIvImage5.setVisibility(View.GONE);
                        view.findViewById(R.id.hlcheque).setVisibility(View.GONE);
                    }

                } else {
                    mIvImage3.setImageResource(R.mipmap.red_cross);
                    mIvImage5.setImageResource(R.mipmap.red_cross);
                }

                if (jsonObject.optString("FatcaStatus").equalsIgnoreCase("Y")) {
                    mIvImage2.setImageResource(R.mipmap.green_checked);
                    view.findViewById(R.id.ivFatcaArrow).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.cvFatca).setEnabled(true);

                } else {
                    mIvImage2.setImageResource(R.mipmap.red_cross);
                    view.findViewById(R.id.ivFatcaArrow).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.cvFatca).setEnabled(true);
                }

                if (jsonObject.optString("PANKYCStatus").equalsIgnoreCase("Y")) {

                    mIvImage6.setImageResource(R.mipmap.green_checked);
                    view.findViewById(R.id.panKYCArrow).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.cvPANKyc).setEnabled(false);
                } else {
                    mIvImage6.setImageResource(R.mipmap.red_cross);
                    view.findViewById(R.id.panKYCArrow).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.cvPANKyc).setEnabled(true);
                }

                mIvImage7.setImageResource(R.mipmap.green_checked);

                if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
                    view.findViewById(R.id.cvBank).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.cvBank).setVisibility(View.GONE);
                    mIvImage7.setVisibility(View.GONE);
                    view.findViewById(R.id.hlBank).setVisibility(View.GONE);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {

            if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
                view.findViewById(R.id.cvBank).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.cvBank).setVisibility(View.GONE);
            }

            if (mSession.getHasCAFStatus()) {
                mIvImage1.setImageResource(R.mipmap.green_checked);
                mIvArrow1.setVisibility(View.INVISIBLE);
                view.findViewById(R.id.cvProfile).setEnabled(false);
                view.findViewById(R.id.profile_detail).setVisibility(View.VISIBLE);
            } else {
                mIvImage1.setImageResource(R.mipmap.red_cross);
                mIvArrow1.setVisibility(View.VISIBLE);
                view.findViewById(R.id.cvProfile).setEnabled(true);
                view.findViewById(R.id.profile_detail).setVisibility(View.GONE);
            }

            if (mSession.getHasMendate()) {
                mIvImage4.setImageResource(R.mipmap.green_checked);
            } else {
                mIvImage4.setImageResource(R.mipmap.red_cross);
            }

            if (mSession.getHasSignature()) {
                mIvImage3.setImageResource(R.mipmap.green_checked);
                mIvImage5.setImageResource(R.mipmap.green_checked);
            } else {
                mIvImage3.setImageResource(R.mipmap.red_cross);
                mIvImage5.setImageResource(R.mipmap.red_cross);
            }

            if (mSession.getHasFatca()) {
                mIvImage2.setImageResource(R.mipmap.green_checked);
                view.findViewById(R.id.ivFatcaArrow).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.cvFatca).setEnabled(false);

            } else {
                mIvImage2.setImageResource(R.mipmap.red_cross);
                view.findViewById(R.id.ivFatcaArrow).setVisibility(View.VISIBLE);
                view.findViewById(R.id.cvFatca).setEnabled(true);
            }
            if (mSession.getPANKYC()) {
                mIvImage6.setImageResource(R.mipmap.green_checked);
                view.findViewById(R.id.cvPANKyc).setEnabled(false);
                view.findViewById(R.id.panKYCArrow).setVisibility(View.INVISIBLE);

            } else {
                mIvImage6.setImageResource(R.mipmap.red_cross);
                view.findViewById(R.id.cvPANKyc).setEnabled(true);
                view.findViewById(R.id.panKYCArrow).setVisibility(View.VISIBLE);
            }
        }

    }


    public void ProfileDetail(final String Ucc) {


        try {
            String url = Config.Profile_Detail;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("UCC", Ucc);
            jsonObject.put("OnlineOption", mSession.getAppType());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    if (response.optString("Status").equalsIgnoreCase("True")) {

                        JSONObject jsonObject1 = response.optJSONObject("ProfileDetail");
                        Bundle bundle = new Bundle();
                        bundle.putString("ucc_code", Ucc);
                        bundle.putString("UserAllData", jsonObject1.toString());
                        mActivity.displayViewOther(86, bundle);
                       /* if(mSession.getAppType().equalsIgnoreCase("B")|| mSession.getAppType().equalsIgnoreCase("DB")) {
                            mainActivity.displayViewOther(58, bundle);
                        }
                        else if(mSession.getAppType().equals(getResources().getString(R.string.apptype_n))|| mSession.getAppType().equalsIgnoreCase("DN")){
                            mainActivity.displayViewOther(86, bundle);
                        }*/
                    } else {
                        Toast.makeText(getActivity(), response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            String isSingUploaded = data.getStringExtra("signUploaded");
            if (isSingUploaded.equals("yes")) {
                mSession.setHasSignature(true);
                getActivity().getSupportFragmentManager().popBackStack();
                mActivity.displayViewOther(15, null);
            }
        }

    }


}
