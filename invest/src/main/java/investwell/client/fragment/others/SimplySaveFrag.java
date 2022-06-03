package investwell.client.fragment.others;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import investwell.client.activity.MainActivity;
import investwell.client.activity.WebViewActivity;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class SimplySaveFrag extends Fragment {
    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue requestQueue;
    private Bundle bundle;
    private Spinner mSaveSimplySpinner;
    private AppSession mSession;
    private Button mButton;
    private String mCid, url;
    private MainActivity mActivity;
    private ToolbarFragment toolbarFragment;
    private CardView cvSpinnerSimplySave;
    private TextView tvNoData;

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
        View view = inflater.inflate(R.layout.fragment_simply_save, container, false);
        setUpToolBar();
        initializer(view);
        getDataFromBundle();
        setListeners(view);
        getFolioLostData();

        return view;
    }

    private void initializer(View view) {
        mSaveSimplySpinner = view.findViewById(R.id.simplyspinner);
        mButton = view.findViewById(R.id.submit_btn);

        bundle = getArguments();
        cvSpinnerSimplySave = view.findViewById(R.id.cv_spinner_simply_save);
        tvNoData = view.findViewById(R.id.tvNothing);
    }

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(mSession.getSimplySave(), true, false, false, false, false, false, false, "");
        }
    }

    private void getDataFromBundle() {
        if (bundle != null && bundle.containsKey("cid")) {
            mCid = bundle.getString("cid");
        } else {
            mCid = mSession.getCID();
        }
    }

    private void setListeners(View view) {
        view.findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("title", "Simply Save");
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
    }

    private void getFolioLostData() {
        DialogsUtils.showProgressBar(getActivity(), false);
        String url = Config.Simply_Save;
        JSONObject jsonObject = new JSONObject();
        final ArrayList<JSONObject> list = new ArrayList<>();
        try {
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Cid", mCid);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArray = response.optJSONArray("FolioforSimplySaveDetail");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            list.add(jsonObject1);
                        }
                        setSpinnerData(list);
                        cvSpinnerSimplySave.setVisibility(View.VISIBLE);
                        mButton.setVisibility(View.VISIBLE);
                        tvNoData.setVisibility(View.GONE);
                    } else {
                        mButton.setVisibility(View.GONE);
                        cvSpinnerSimplySave.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
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

    private void setSpinnerData(final ArrayList<JSONObject> list) {
        String[] folio_number = new String[list.size()];
        String[] investor_name = new String[list.size()];
        String[] spinner_value = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            folio_number[i] = list.get(i).optString("Folio");
            investor_name[i] = list.get(i).optString("InvestorName");
            spinner_value[i] = folio_number[i] + " - " + investor_name[i];
        }

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinner_value);
        mSaveSimplySpinner.setAdapter(adapter);
        mSaveSimplySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                url = "https://investeasy.nipponindiaim.com/Simplysave/AuthenticationCheckingPage/Auth?SCHEME=" + list.get(i).optString("Scheme") + "&PLAN=" + list.get(i).optString("Plan") + "&OPTION=" + list.get(i).optString("Option") + "&EUIN=" + list.get(i).optString("EUIN") +
                        "&TOKEN=" + list.get(i).optString("Token") + "&REFERRER_URL" + list.get(i).optString("RefererURL") + "&FOLIO=" + list.get(i).optString("Folio") +
                        "&ARNCODE=" + list.get(i).optString("ARN") + "&RESPONSE_URL=" + list.get(i).optString("ResponseURL") +
                        "&SubBrokerCode=" + list.get(i).optString("SubBrokerCode") + "&SubBrokerARN=" + list.get(i).optString("SubBrokerARN") + "&Platform=InvestWell";

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

}
