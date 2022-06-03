package investwell.client.fragment.nav.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import investwell.client.activity.MainActivity;
import investwell.client.fragment.nav.adapter.NavSchemeAdapter;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class FragNavItemSchemes extends Fragment {
    private AppSession mSession;
    private String mObjCode, mSchemeCode;
    private List<JSONObject> list;
    private NavSchemeAdapter mAdapter;
    private ProgressDialog mBar;
    private TextView mTvCart;
    private Boolean first = true;
    private MainActivity mActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
        }
    }

    @Nullable

    @Override
    public void onResume() {
        super.onResume();
        updateCart();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_navschemes, container, false);
        mSession = AppSession.getInstance(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null) {
            mSchemeCode = bundle.getString("schemecode");
            mObjCode = bundle.getString("objcode");
        }


        RecyclerView recyclerView = view.findViewById(R.id.scheme_recyclier);
        mTvCart = view.findViewById(R.id.tv_cart_badge);
        mAdapter = new NavSchemeAdapter(getActivity(), new ArrayList<JSONObject>(),FragNavItemSchemes.this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
recyclerView.addItemDecoration(new DividerItemDecoration(mActivity,DividerItemDecoration.VERTICAL));
        getData();

        return view;
    }


    private void getData() {
        if (first) {
            mBar = ProgressDialog.show(getActivity(), null, null, true, false);
            mBar.setContentView(R.layout.progress_piggy);
            mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
        params.put(AppConstants.PASSKEY, mSession.getPassKey());
        params.put("ObjCode", mObjCode);
        params.put("Fcode", mSchemeCode);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.GET_NAV, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mBar.dismiss();
                first = false;
                list = new ArrayList<>();
                if (response.optBoolean("Status")) {
                    JSONArray araArray = null;
                    try {
                        araArray = response.getJSONArray("LatestNAVDetail");
                        for (int i = 0; i < araArray.length(); i++) {
                            JSONObject object = araArray.getJSONObject(i);
                            list.add(object);
                        }
                        mAdapter.updatelist(list);
//                            setSpinnerAMC();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getActivity(), response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                mBar.dismiss();
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });


        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }

    public void updateCart() {
        try {
            mTvCart.setText("0");
            if (mSession.getAddToCartList().isEmpty() || mSession.getAddToCartList().length() == 2) {
                mTvCart.setVisibility(View.GONE);
            } else {
                mTvCart.setVisibility(View.VISIBLE);
                JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
                mTvCart.setText("" + jsonArray.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
