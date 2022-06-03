package investwell.client.fragment.flavour;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class GetFlavourFrag extends Fragment {
    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue requestQueue;
    private AppSession mSession;
    private MainActivity mActivity;
    private TextView mSchemeName;
    private Bundle bundle;
    private Spinner mMemberSpinner, mFolioSpinner;
    private LinearLayout mMemberLy, mfolioLy;
    private LinearLayout mMianLy;
    private String spinner_type, mUcc, mCid,mNewCid="";
    private Button minvestBtn;
    private ToolbarFragment toolbarFragment;
    private TextView tvNotLoggedIn;
    private AppApplication appApplication;
    private String SelectedInvestment = "Purchase";
    String Fcode="",Scode="";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);
            mSession = AppSession.getInstance(getActivity());
            appApplication = (AppApplication) mActivity.getApplication();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        final View view = inflater.inflate(R.layout.fragment_get_flavour, container, false);
        setUpToolBar();
        initializer(view);
        getDataFromBundle();
        view.findViewById(R.id.investBtn).setOnClickListener(view1 -> {

            if (minvestBtn.getText().toString().contains("Create")) {
                String AlreadyUser = "true";
                bundle.putString("AlreadyUser", AlreadyUser);
                mActivity.displayViewOther(5, bundle);
            } else {
                bundle.putString("Cid",mNewCid);
                if (SelectedInvestment.equals("Purchase")) {
                    mActivity.displayViewOther(28, bundle);
                }else {
                    mActivity.displayViewOther(33, bundle);
                }
            }

        });
        getFlavour();
        checkLogin();

        final RadioGroup rg = view.findViewById(R.id.rgInvestment);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int pos = rg.indexOfChild(view.findViewById(i));
                switch (pos){
                    case 0:
                        getMemberList(Fcode, Scode);
                        SelectedInvestment = "Purchase";
                        break;

                    case 1:
                        getMemberList(Fcode,Scode);
                        SelectedInvestment = "Redemption";
                        break;
                }

            }
        });

        return view;
    }

    private void initializer(View view) {
        mSchemeName = view.findViewById(R.id.colorBlue);
        mMemberLy = view.findViewById(R.id.memberLy);
        mfolioLy = view.findViewById(R.id.folioLy);
        mMemberSpinner = view.findViewById(R.id.member_spinner);
        mFolioSpinner = view.findViewById(R.id.folio_spinner);
        mMianLy = view.findViewById(R.id.mainLy);
        minvestBtn = view.findViewById(R.id.investBtn);
        bundle = new Bundle();
        tvNotLoggedIn = view.findViewById(R.id.tv_not_logged_in);
    }

    private void getDataFromBundle() {
        if (bundle != null && bundle.containsKey("cid")) {
            mCid = bundle.getString("cid");
        } else {
            mCid = mSession.getCID();
        }

    }

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(mSession.getFom(), true, false, false, false, false, false, false, "");
        }
    }

    private void checkLogin() {
        if (!mSession.getHasLoging()) {
            minvestBtn.setEnabled(false);
            minvestBtn.setBackground(getResources().getDrawable(R.drawable.btn_bg_tertiary));
            minvestBtn.setTextColor(getResources().getColor(R.color.colorYellow));
            tvNotLoggedIn.setVisibility(View.VISIBLE);
        } else {
            minvestBtn.setEnabled(true);
            minvestBtn.setBackground(getResources().getDrawable(R.drawable.btn_bg_primary));
            minvestBtn.setTextColor(getResources().getColor(R.color.btnPrimaryTextColor));
            tvNotLoggedIn.setVisibility(View.GONE);
        }
    }

    private void getFlavour() {
        DialogsUtils.showProgressBar(getActivity(), false);
        String url = Config.Get_Flavour;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("OnlineOption", mSession.getAppType());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();

                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArray = response.optJSONArray("FlavourofMonthDetail");
                        JSONObject jsonObject1 = jsonArray.optJSONObject(0);
                        String SchemeName = jsonObject1.optString("SchemeName");
                        bundle.putString("Fcode", jsonObject1.optString("Fcode"));
                        bundle.putString("Scode", jsonObject1.optString("Scode"));
                        bundle.putString("colorBlue", SchemeName);
                        bundle.putString("Passkey", mSession.getPassKey());
                        bundle.putString("Bid", AppConstants.APP_BID);
                        mSchemeName.setText(jsonObject1.optString("SchemeName"));
                        mMianLy.setVisibility(View.VISIBLE);
                        Fcode = jsonObject1.optString("Fcode");
                        Scode = jsonObject1.optString("Scode");
                        getMemberList(Fcode,Scode);


                    } else {
                        appApplication.showSnackBar(mSchemeName, response.optString("ServiceMSG"));
                        mMianLy.setVisibility(View.GONE);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogsUtils.hideProgressBar();
                }
            });

             jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });
            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMemberList(final String Fcode, final String Scode) {
        String url = Config.PROFILE_LIST;
        spinner_type = "member";
        DialogsUtils.showProgressBar(getActivity(), false);
        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("Cid", mCid);
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    ArrayList<JSONObject> list = new ArrayList<>();
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArray = response.optJSONArray("ProfileListDetail");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            list.add(jsonObject1);

                        }
                        setSpinnerValue(list, spinner_type);

                    } else {
                        mMemberLy.setVisibility(View.GONE);
                        minvestBtn.setText(R.string.txt_create_investment);


                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogsUtils.hideProgressBar();
                }
            });
             jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });
            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void getFolioList(final String Fcode, final String Scode) {
        String url = Config.Folio_LIST;
        spinner_type = "folio";
        DialogsUtils.showProgressBar(getActivity(), false);
        JSONObject jsonObject = new JSONObject();
        final ArrayList<JSONObject> list = new ArrayList<>();
        try {
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("UCC", mUcc);
            jsonObject.put("Fcode", Fcode);
            jsonObject.put("OnlineOption", mSession.getAppType());
            if (SelectedInvestment.equals("Redemption")){
                jsonObject.put("Scode",Scode);
            }

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArray = response.optJSONArray("ExisitingFolioDetail");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            list.add(jsonObject1);


                        }
                        setSpinnerValue(list, spinner_type);


                    } else {

                        //  Toast.makeText(getActivity(), response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();

                        mfolioLy.setVisibility(View.GONE);
                        if (SelectedInvestment.equals("Purchase")) {
                            bundle.putString("FolioNo", "");
                            minvestBtn.setVisibility(View.VISIBLE);
                        }else{
                            minvestBtn.setVisibility(View.INVISIBLE);
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogsUtils.hideProgressBar();

                }
            });

             jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });

            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setSpinnerValue(final ArrayList<JSONObject> list, String SpinnerType) {

        if (SpinnerType.equalsIgnoreCase("member")) {
            mMemberLy.setVisibility(View.VISIBLE);
            String[] member_name = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                member_name[i] = list.get(i).optString("InvestorName");
            }
            ArrayAdapter spinner_value = new ArrayAdapter(getActivity(), R.layout.spinner_item, member_name);
            spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mMemberSpinner.setAdapter(spinner_value);


            mMemberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mUcc = list.get(i).optString("UCC");
                    bundle.putString("UCC", mUcc);
                    bundle.putString("applicant_name", list.get(i).optString("InvestorName"));
                    mNewCid = list.get(i).optString("Cid");
                    getFolioList(Fcode,Scode);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        } else {
            mfolioLy.setVisibility(View.VISIBLE);

            ArrayList<String> folio_number = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {

                folio_number.add(list.get(i).optString("FolioNo"));

            }
            if (SelectedInvestment.equals("Purchase")){
                folio_number.add("New Folio");
            }

            ArrayAdapter spinner_value = new ArrayAdapter(getActivity(), R.layout.spinner_item, folio_number);
            spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mFolioSpinner.setAdapter(spinner_value);

            mFolioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if ( mFolioSpinner.getSelectedItem().equals("New Folio") && SelectedInvestment.equals("Purchase")){
                        bundle.putString("FolioNo", "");
                    }else{
                        bundle.putString("FolioNo", mFolioSpinner.getSelectedItem().toString());
                    }


                    if (SelectedInvestment.equals("Redemption")){
                        getFoiloDetail(mFolioSpinner.getSelectedItem().toString(),Scode);
                    }else{
                        minvestBtn.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }


    }

    private void getFoiloDetail(String folio,String Scode){
        DialogsUtils.showProgressBar(getActivity(),false);
        String url = Config.Folio_Query;
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Cid", mNewCid);
            jsonObject.put("FolioNo", folio);
            jsonObject.put("SchemeCode", Scode);

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        minvestBtn.setVisibility(View.VISIBLE);
                        bundle.putString("purchase_cost",response.optJSONArray("FolioQueryDetail").optJSONObject(0).optString("CurrentUnits"));
                        bundle.putString("market_position",response.optJSONArray("FolioQueryDetail").optJSONObject(0).optString("CurrentValue"));

                    } else {
                        minvestBtn.setVisibility(View.GONE);

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogsUtils.hideProgressBar();
                    // appApplication.showSnackBar(mSchemeName, error.getLocalizedMessage());
                }
            });
             jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });
            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
