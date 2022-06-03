package investwell.client.fragment.justsave;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONObject;

import investwell.client.activity.MainActivity;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class JustSaveFrag extends Fragment {

    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue requestQueue;
    private AppSession mSession;
    private String mFcode = "", mScode = "", Action;
    private TextView mTvScheme;
    private MainActivity mainActivity;
    private ToolbarFragment toolbarFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
            mainActivity.setMainVisibility(this, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_just_save, container, false);
        setUpToolBar();
        initializer(view);
        setListeners(view);
        return view;
    }

    private void initializer(View view) {
        mSession = AppSession.getInstance(getActivity());
        mTvScheme = view.findViewById(R.id.autoComplete);
        Bundle bundle = getArguments();
        if (bundle!=null && bundle.containsKey("data")){
            try {
                JSONObject jsonObject = new JSONObject(bundle.getString("data"));
                mFcode = jsonObject.optString("FCode");
                mScode = jsonObject.optString("SchemeCode");
                mTvScheme.setText(jsonObject.optString("SchemeName"));
            }catch ( Exception e){

            }

        }
    }

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(mSession.getJustSave(), true, false, false, false, false, false, false, "");
        }
    }

    private void setListeners(View view) {
        view.findViewById(R.id.savebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTvScheme.getText().toString().isEmpty() || mFcode.isEmpty()) {
                    mTvScheme.setError(getResources().getString(R.string.set_falvour_error_select_scheme));
                } else {
                    setJustSave();
                }
            }
        });

        mTvScheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle1 = new Bundle();
                bundle1.putString("type", "scheme_for_just_save");
                mainActivity.displayViewOther(75, bundle1);
            }
        });




    }


    private void setJustSave() {
        if (mSession.getSave().isEmpty()) {
            Action = "Create";
        } else {
            Action = "Modify";
        }
        String url = Config.Set_Just_Save;
        JSONObject jsonObject = new JSONObject();
        DialogsUtils.showProgressBar(getActivity(), false);
        try {

            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Cid", mSession.getCID());
            jsonObject.put("UCC", mSession.getUCC_CODE());
            jsonObject.put("Fcode", mFcode);
            jsonObject.put("Scode", mScode);
            jsonObject.put("OnlineOption", mSession.getAppType());
            jsonObject.put("Action", Action);

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    Toast.makeText(getActivity(), response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        mSession.setSave(mTvScheme.getText().toString());
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
