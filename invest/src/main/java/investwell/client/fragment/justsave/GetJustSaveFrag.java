package investwell.client.fragment.justsave;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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

import investwell.client.activity.MainActivity;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class GetJustSaveFrag extends Fragment implements View.OnClickListener {
    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue requestQueue;
    private AppSession mSession;
    private MainActivity mActivity;
    private Bundle bundle;
    private LinearLayout mMemberLy, mFolioLy;
    private Spinner mMemberSpinner, mFolioSpinner;
    private TextView mSchemeName;
    private String spinner_type, mUcc, mCid;
    private Button minvestBtn;
    private ToolbarFragment toolbarFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);
            mSession = AppSession.getInstance(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_get_just_save, container, false);
        setUpToolBar();
        initializer(view);

        getDataFromBundle();
        setListeners(view);

        getJustSave();
        return view;
    }

    private void initializer(View view) {
        mSession = AppSession.getInstance(getActivity());
        mActivity = (MainActivity) getActivity();
        bundle = new Bundle();
        mFolioLy = view.findViewById(R.id.folioLy);
        mFolioSpinner = view.findViewById(R.id.folio_spinner);
        mMemberSpinner = view.findViewById(R.id.member_spinner);
        mSchemeName = view.findViewById(R.id.colorBlue);
        mMemberLy = view.findViewById(R.id.memberLy);
        minvestBtn = view.findViewById(R.id.investBtn);

    }

    private void getDataFromBundle() {
        if (bundle != null && bundle.containsKey("cid")) {
            mCid = bundle.getString("cid");
        } else {
            mCid = mSession.getCID();
        }
    }

    private void setListeners(View view) {
        view.findViewById(R.id.investBtn).setOnClickListener(this);
        view.findViewById(R.id.rlChangeScheme).setOnClickListener(this);
    }

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(mSession.getJustSave(), true, false, false, false, false, false, false, "");
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.investBtn) {
            if (minvestBtn.getText().toString().contains("Create")) {
                String AlreadyUser = "true";
                bundle.putString("AlreadyUser", AlreadyUser);
                mActivity.displayViewOther(5, bundle);
            } else {
                mActivity.displayViewOther(28, bundle);
            }
        } else if (v.getId() == R.id.rlChangeScheme) {
            mActivity.displayViewOther(96, null);
        }

    }

    private void getJustSave() {
        DialogsUtils.showProgressBar(getActivity(), false);
        String url = Config.Get_Just_Save;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Cid", mCid);
            jsonObject.put("OnlineOption", mSession.getAppType());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArray = response.optJSONArray("JustSaveDetail");
                        JSONObject jsonObject1 = jsonArray.optJSONObject(0);
                        String SchemeName = jsonObject1.optString("SchemeName");
                        bundle.putString("Fcode", jsonObject1.optString("Fcode"));
                        bundle.putString("Scode", jsonObject1.optString("Scode"));
                        bundle.putString("colorBlue", SchemeName);
                        bundle.putString("Passkey", mSession.getPassKey());
                        bundle.putString("Bid", AppConstants.APP_BID);
                        mSchemeName.setText(SchemeName);
                        getMemberList(jsonObject1.optString("Fcode"));
                    } else {
                        DialogsUtils.hideProgressBar();
                        Toast.makeText(getActivity(), response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
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
            DialogsUtils.hideProgressBar();
        }


    }

    private void getMemberList(final String Fcode) {
        String url = Config.PROFILE_LIST;
        spinner_type = "member";
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
                        setSpinnerValue(list, spinner_type, Fcode);


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
            DialogsUtils.hideProgressBar();
        }


    }

    private void getFolioList(final String Fcode) {
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
                        setSpinnerValue(list, spinner_type, Fcode);
                    } else {

//                        Toast.makeText(getActivity(), response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                        mFolioLy.setVisibility(View.GONE);
                        bundle.putString("FolioNo", "");
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

    private void setSpinnerValue(final ArrayList<JSONObject> list, String SpinnerType, final String Fcode) {

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
                    getFolioList(Fcode);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        } else {
            mFolioLy.setVisibility(View.VISIBLE);
            String[] folio_number = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                folio_number[i] = list.get(i).optString("FolioNo");
            }
            ArrayAdapter spinner_value = new ArrayAdapter(getActivity(), R.layout.spinner_item, folio_number);
            spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mFolioSpinner.setAdapter(spinner_value);

            mFolioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    bundle.putString("FolioNo", mFolioSpinner.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }


    }


}
