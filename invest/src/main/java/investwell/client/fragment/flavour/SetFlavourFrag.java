package investwell.client.fragment.flavour;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.Objects;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class SetFlavourFrag extends Fragment implements View.OnClickListener {

    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue requestQueue;
    private AppSession mSession;
    private String Action, mFcode = "", mScode = "";
    private String[] mScheme;
    private TextView mSchemeName;
    private EditText mEtSeacrh;
    private RecyclerView mFlavourRecycle;
    private SetFlavourAdapter setFlavourAdapter;
    private LinearLayout mSchemeLy;
    private AutoCompleteTextView mAutoComplete;
    private AppApplication mApplication;
    private MainActivity mainActivity;
    private BrokerActivity mBrokerActivity;
    private ToolbarFragment toolbarFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BrokerActivity) {
            this.mBrokerActivity = (BrokerActivity) context;
            mSession = AppSession.getInstance(mBrokerActivity);
            mApplication = (AppApplication) mBrokerActivity.getApplication();
            mSession = AppSession.getInstance(getActivity());
            mBrokerActivity.setMainVisibility(this, null);

        } else if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mainActivity);
            mApplication = (AppApplication) mainActivity.getApplication();
            mSession = AppSession.getInstance(getActivity());
            mainActivity.setMainVisibility(this, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_set_flavour, container, false);
        setUpToolBar();
        initializer(view);

        getFlavour();
        setListener();

        return view;
    }

    private void initializer(View view) {
        mSession = AppSession.getInstance(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mApplication = (AppApplication) Objects.requireNonNull(getActivity()).getApplication();
        }
        mSchemeLy = view.findViewById(R.id.schemeLy);
        mSchemeName = view.findViewById(R.id.colorBlue);
        mAutoComplete = view.findViewById(R.id.autoComplete);
        view.findViewById(R.id.submit_btn).setOnClickListener(this);
        // view.findViewById(R.id.bac_arrow).setOnClickListener(this);
    }

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(getResources().getString(R.string.toolbar_title_flavour_of_the_month), true, false, false, false, false, false, false, "");
        }
    }

    private void setListener() {
        mAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() >= 3) {

                    ChoseScheme();

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mSchemeName.setText(mSession.getFlavour());
        if (mSchemeName.getText().toString().isEmpty()) {
            mSchemeLy.setVisibility(View.GONE);
        } else {
            mSchemeLy.setVisibility(View.VISIBLE);
        }
    }

    public void onClick(View v) {

        if (v.getId() == R.id.submit_btn) {
            if (mSchemeName.getText().toString().isEmpty()) {
                mApplication.showSnackBar(mSchemeName, getResources().getString(R.string.set_falvour_error_select_scheme));
            } else if (mFcode.isEmpty()) {
                mApplication.showSnackBar(mSchemeName, getResources().getString(R.string.set_falvour_error_select_scheme_saved));

            } else {
                mSession.setFlavour(mSchemeName.getText().toString());
                setFlavour();
            }
        }

    }


    private void ChoseScheme() {

        //  DialogsUtils.showProgressBar(getActivity(), false);
        String url = Config.Search_list;
        final JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("SearchValue", mAutoComplete.getText().toString());
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //    DialogsUtils.hideProgressBar();
                    ArrayList<JSONObject> list = new ArrayList<>();
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArray = response.optJSONArray("SchemeSearchDetail");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            list.add(jsonObject1);
                        }

                        setRecycleValue(list);


                    } else {
                        mApplication.showSnackBar(mSchemeName, response.optString("ServiceMSG"));
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

    private void setRecycleValue(final ArrayList<JSONObject> list) {

      /*  mFlavourRecycle.setHasFixedSize(true);
        mFlavourRecycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        setFlavourAdapter = new SetFlavourAdapter(getActivity(), new ArrayList<JSONObject>(), new SetFlavourAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                JSONObject mDataList = setFlavourAdapter.mDataList.get(position);
                mSchemeName.setText(mDataList.optString("SchemeName"));
                mFcode = mDataList.optString("FCode");
                mScode = mDataList.optString("SchemeCode");
                mFlavourRecycle.setVisibility(View.GONE);
                mSchemeLy.setVisibility(View.VISIBLE);
                mEtSeacrh.setText("");
            }
        });
        setFlavourAdapter.updateList(list);
        mFlavourRecycle.setAdapter(setFlavourAdapter);*/

        mScheme = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            mScheme[i] = list.get(i).optString("SchemeName");
        }
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.auto_complete_textview, mScheme);
        mAutoComplete.setThreshold(1); //will start working from first character
        mAutoComplete.setAdapter(adapter);

        mAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mFcode = list.get(i).optString("FCode");
                mScode = list.get(i).optString("SchemeCode");
                mSession.setFlavour(mAutoComplete.getText().toString());
                mSchemeName.setText(mSession.getFlavour());
            }
        });

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
                            Action = "Modify";
                    } else {
                        Action = "Create";
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


    private void setFlavour() {
        DialogsUtils.showProgressBar(getActivity(), false);
        String url = Config.Set_Flavour;
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Fcode", mFcode);
            jsonObject.put("Scode", mScode);
            jsonObject.put("OnlineOption", mSession.getAppType());
            jsonObject.put("Action", Action);


            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    mApplication.showSnackBar(mSchemeName, response.optString("ServiceMSG"));
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        getActivity().getSupportFragmentManager().popBackStack();
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
}
