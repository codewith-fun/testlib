package investwell.client.fragment.schemes;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
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
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class FragAddSchemesSingle extends Fragment implements View.OnClickListener {
    String category = "E", scheme_type = "G", Reinvest = "Z";
    Spinner mSpinerAMC, mSpinnerScheme, mSpinnerFolio;
    private AppSession mSession;
    private ArrayList<JSONObject> mListAmc, mListSchemes;
    private String mSelectedAmcCode = "", mType = "", mSelectedFolio = "0";
    private JSONObject mSelectedSchemeObject;
    private JsonObjectRequest jsonObjectRequest;
    private String mUCC_Code="";
    private MainActivity mActivity;
    private AppApplication mApplication;
    private ToolbarFragment toolbarFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mApplication = (AppApplication) mActivity.getApplication();
            mActivity.setMainVisibility(this, null);
        }
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_add_shceme_single, container, false);

        setUpToolBar();

        view.findViewById(R.id.cancel_btn).setOnClickListener(this);
        view.findViewById(R.id.add_cart_btn).setOnClickListener(this);
        mSpinerAMC = view.findViewById(R.id.amc_spinner);
        mSpinnerScheme = view.findViewById(R.id.scheme_spinner);
        mSpinnerFolio = view.findViewById(R.id.folio_spinner);
        RadioGroup scheme_category_grp = view.findViewById(R.id.scheme_category);
        RadioGroup scheme_type_grp = view.findViewById(R.id.scheme_type);

        Bundle bundle = getArguments();
        if (bundle.containsKey("type")) {
            mType = bundle.getString("type");
        }

        if (bundle != null && bundle.containsKey("ucc_code")) {
            mUCC_Code = bundle.getString("ucc_code");
        }


        scheme_category_grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.equity) {
                    category = "E";
                } else if (i == R.id.debt) {
                    category = "D";
                } else if (i == R.id.hybrid) {
                    category = "H";
                }

                getAllSchemesWithDetails();
            }
        });

        scheme_type_grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.growth) {
                    scheme_type = "G";
                    Reinvest = "Z";
                } else if (i == R.id.devident) {
                    scheme_type = "D";
                    Reinvest = "D";
                }
                getAllSchemesWithDetails();

            }
        });


        getAllAMC();
        return view;

    }
    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(getResources().getString(R.string.lumpsum_add_scheme_txt), true, false, false, false, false, false,false, "");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back_arrow) {
            getActivity().getSupportFragmentManager().popBackStack();
        } else if (id == R.id.cancel_btn) {
            getActivity().getSupportFragmentManager().popBackStack();
        } else if (id == R.id.add_cart_btn) {
            AddSchemes();
        }

    }


    private void setSpinnerAMC() {
        ArrayList<String> stringArray = new ArrayList<String>();
        for (int i = 0, count = mListAmc.size(); i < count; i++) {
            JSONObject jsonObject = mListAmc.get(i);
            stringArray.add(jsonObject.optString("AMCName"));
        }
        ArrayAdapter spinner_value = new ArrayAdapter(getActivity(), R.layout.spinner_item, stringArray);
        spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinerAMC.setAdapter(spinner_value);
        mSpinerAMC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject jsonObject = mListAmc.get(mSpinerAMC.getSelectedItemPosition());
                mSelectedAmcCode = jsonObject.optString("AMCCode");
                getAllSchemesWithDetails();
               // getFolioList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setSpinnerScheme() {
        ArrayList<String> stringArray = new ArrayList<String>();
        for (int i = 0, count = mListSchemes.size(); i < count; i++) {
            JSONObject jsonObject = mListSchemes.get(i);
            stringArray.add(jsonObject.optString("SchemeName"));
        }
        ArrayAdapter spinner_value = new ArrayAdapter(getActivity(), R.layout.spinner_item, stringArray);
        spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerScheme.setAdapter(spinner_value);
        mSpinnerScheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedSchemeObject = mListSchemes.get(mSpinnerScheme.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void AddSchemes() {
        final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.ADD_SCHEMES_SIP;
        JSONObject jsonParam = new JSONObject();

        try {
            jsonParam.put("UCC", mUCC_Code);
            jsonParam.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonParam.put(AppConstants.CUSTOMER_ID, mSession.getCID());
            jsonParam.put("Fcode", mSelectedAmcCode);
            jsonParam.put("Scode", mSelectedSchemeObject.optString("SchemeCode"));
            jsonParam.put("FolioNo", "0");
            jsonParam.put("Reinvest", Reinvest);
            jsonParam.put("Amount", "0");
            jsonParam.put("Installment", "0");
            jsonParam.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonParam.put("TranType", mType);
            jsonParam.put("OnlineOption",mSession.getAppType());

        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mBar.dismiss();
                try {
                    if (object.optString("Status").equals("True")) {
                        //Toast.makeText(getActivity(), "Successfully Scheme added to your cart", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getActivity(), object.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), jsonObject.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }


    private void getAllAMC() {
        final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.GET_ALL_AMC;

        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.KEY_BROKER_ID,AppConstants.APP_BID);
            jsonObject.put(AppConstants.PASSKEY,mSession.getPassKey());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mBar.dismiss();
                    try {
                        mListAmc = new ArrayList<>();
                        if (jsonObject.optBoolean("Status")) {
                            JSONArray araArray = jsonObject.getJSONArray("AMCList");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                mListAmc.add(object);
                            }
                            setSpinnerAMC();

                        } else {
                            if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "invalidPasskey",false,true);
                            }else{
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "message",false,true);
                            }                        }


                    } catch (JSONException e) {
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
                            Toast.makeText(getActivity(), jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();

                }
            });
            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 0;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 0;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        }catch (Exception e){
            e.printStackTrace();
        }



    }


    private void getAllSchemesWithDetails() {
        final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//http://nativeapi.my-portfolio.in/BSEStarMF.svc/SchemeListV2/{PASSKEY}/{BID}/{UCC}/{FCODE}
// /{FOLIONO}/{SCHEMETYPE}/{OPTION}/{MYSCHEME}
       /* String url = Config.GET_SCHMES_LIST + mSession.getPassKey() + "/" + AppConstants.VALUE_BROKER_ID
                + "/" + mSession.getUCC_CODE() + "/" + mSelectedAmcCode + "/" + mSelectedFolio + "/" + category + "/" + scheme_type + "/N";
*/

       String url = Config.GET_SCHMES_LIST;
        try{

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("UCC", mUCC_Code);
            jsonParam.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonParam.put("Fcode", mSelectedAmcCode);
            jsonParam.put("FolioNo", mSelectedFolio);
            jsonParam.put("SchemeType", category);
            jsonParam.put("Option", scheme_type);
            jsonParam.put("MyScheme", "N");
            jsonParam.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonParam.put("OnlineOption",mSession.getAppType());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    mBar.dismiss();
                    try {
                        mListSchemes = new ArrayList<>();

                        if (response.optBoolean("Status")) {
                            JSONArray araArray = response.getJSONArray("SchemeListDetail");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                mListSchemes.add(object);
                            }
                            setSpinnerScheme();
                        } else {
                            mApplication.showSnackBar(mSpinerAMC,getResources().getString(R.string.error_try_again));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    mBar.dismiss();
                    if (error.networkResponse != null && error.networkResponse.data != null) {

                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            Toast.makeText(getActivity(), jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (error instanceof NoConnectionError)
                        Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();


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

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);





        }catch (Exception e){
            e.printStackTrace();
        }


      /*  StringRequest createOrderRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        mBar.dismiss();
                        try {
                            mListSchemes = new ArrayList<>();
                            JSONObject mDataList = new JSONObject(result);
                            if (mDataList.optBoolean("Status")) {
                                JSONArray araArray = mDataList.getJSONArray("SchemeListDetail");
                                for (int i = 0; i < araArray.length(); i++) {
                                    JSONObject object = araArray.getJSONObject(i);
                                    mListSchemes.add(object);
                                }
                                setSpinnerScheme();
                            } else {
                                Toast.makeText(getActivity(), "Error, Please try again", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mBar.dismiss();
                        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                            try {
                                JSONObject mDataList = new JSONObject(error.getMessage());
                                Toast.makeText(getActivity(), mDataList.toString(), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (volleyError instanceof NoConnectionError)
                            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                    }
                });

        createOrderRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(createOrderRequest);*/
    }


}
