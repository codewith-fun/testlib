package investwell.client.activity;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import investwell.client.adapter.ChatBotAdapter;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class BotRiskEvalActivity extends BaseActivity {
    private RecyclerView rvRiskAssesBot;
    private ChatBotAdapter chatBotAdapter;
    private ArrayList<JSONObject> mChatBotList;
    private AppApplication mApplication;
    private AppSession mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot_risk_assesment);
        initializer();
        setRiskAdapter();

    }

    private void initializer() {
        mSession = AppSession.getInstance(this);
        mApplication = (AppApplication) getApplication();
        rvRiskAssesBot = findViewById(R.id.rv_risk_asses_bot);
    }

    private void setRiskAdapter() {

        rvRiskAssesBot.setHasFixedSize(true);
        rvRiskAssesBot.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        chatBotAdapter = new ChatBotAdapter(this, new ArrayList<JSONObject>());
        rvRiskAssesBot.setAdapter(chatBotAdapter);
        callRiskAssesQuestionApi();
    }

    private void callRiskAssesQuestionApi() {
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
        params.put(AppConstants.PASSKEY, mSession.getPassKey());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.Question, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mChatBotList = new ArrayList<>();
                if (response.optBoolean("Status")) {
                    JSONArray jsonArray = response.optJSONArray("RiskQuestionList");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                        mChatBotList.add(jsonObject1);
                    }
                    chatBotAdapter.updateList(mChatBotList);
                } else {
                    mApplication.showSnackBar(rvRiskAssesBot, response.optString("ServiceMSG"));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                mApplication.showSnackBar(rvRiskAssesBot, getResources().getString(R.string.error_try_again));
            }
        });


        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}
