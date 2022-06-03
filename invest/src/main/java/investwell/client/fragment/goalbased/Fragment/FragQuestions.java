package investwell.client.fragment.goalbased.Fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.goalbased.Adapter.QuestionAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class FragQuestions extends Fragment implements View.OnClickListener {

    private StringRequest stringRequest;
    private RequestQueue requestQueue;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private AppSession mSession;
    private RecyclerView question_recycle;
    private QuestionAdapter questionAdapter;
    private ToolbarFragment fragToolBar;
    private ArrayList<JSONObject> mQuestionList;
    private String mType = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);
            mApplication = (AppApplication) mActivity.getApplication();
            mSession = AppSession.getInstance(mActivity);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        setUpToolBar();
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("type"))
            mType = bundle.getString("type");
        question_recycle = view.findViewById(R.id.question_recycle);
        question_recycle.setHasFixedSize(true);
        question_recycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        questionAdapter = new QuestionAdapter(getActivity(), new ArrayList<JSONObject>(), this);
        question_recycle.setAdapter(questionAdapter);
        getData();
        view.findViewById(R.id.assess_btn).setOnClickListener(this);
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_start_assesment), true, false, false, false, false, false, false, "");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.assess_btn) {
            checkValidation();
        }
    }

    private void checkValidation() {
        if (questionAdapter.mHashMapForMarks.size() == mQuestionList.size()) {
            int marks = 0;
            for (int i = 0; i < questionAdapter.mHashMapForMarks.size(); i++) {
                marks = marks + questionAdapter.mHashMapForMarks.get(i);
            }
            submitData(marks);
        } else {
            mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Error), getString(R.string.risk_assesment_question_uncomplete), "message", false, true);
        }

    }

    private void getData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
        params.put(AppConstants.PASSKEY, mSession.getPassKey());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.Question, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mQuestionList = new ArrayList<>();
                if (response.optBoolean("Status")) {
                    JSONArray jsonArray = response.optJSONArray("RiskQuestionList");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                        mQuestionList.add(jsonObject1);
                    }
                    questionAdapter.updateList(mQuestionList);
                } else {
                    mApplication.showSnackBar(question_recycle, response.optString("ServiceMSG"));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                mApplication.showSnackBar(question_recycle, getResources().getString(R.string.error_try_again));
            }
        });


        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }

    private void submitData(int marks) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
        params.put(AppConstants.PASSKEY, mSession.getPassKey());
        params.put("Score", String.valueOf(marks));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.Risk_Result, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response.optBoolean("Status")) {
                    try {
                        JSONObject object = response.optJSONArray("RiskResultDetail").getJSONObject(0);
                        Bundle bundle = new Bundle();
                        bundle.putString("riskname", object.optString("RiskName"));
                        bundle.putString("riskdescription", object.optString("RiskDescription"));
                        bundle.putString("riskimage", object.optString("RiskImage"));
                        bundle.putString("riskcode", object.optString("RiskCode"));
                        bundle.putString("profile", object.toString());
                        bundle.putString("type", mType.isEmpty() ? "newRiskProfile" : mType);
                        mActivity.displayViewOther(60, bundle);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {

                    mApplication.showSnackBar(question_recycle, response.optString("ServiceMSG"));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                mApplication.showSnackBar(question_recycle, getResources().getString(R.string.error_try_again));
            }
        });


        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }

}
