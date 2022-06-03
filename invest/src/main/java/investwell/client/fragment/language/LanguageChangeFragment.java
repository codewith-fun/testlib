package investwell.client.fragment.language;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.activity.SplashActivity;
import investwell.client.adapter.LanguageSettingAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.Utils;
import investwell.utils.model.LanguageBean;

/**
 * A simple {@link Fragment} subclass.
 */
public class LanguageChangeFragment extends Fragment implements ToolbarFragment.ToolbarCallback/*, LanguageSupportAdapter.LanguageClickListener */{

    public ToolbarFragment fragToolBar;
    private LanguageSettingAdapter languageSupportAdapter;
    private List<LanguageBean> langList;
    private Button btnContinue;
    private RecyclerView rvLanguageChosen;
    private AppSession appSession;
    private MainActivity mActivity;
    private BrokerActivity mBrokerActivity;
    private JSONArray langJSONArray;
    private String langClicked = "default";
private AppApplication appApplication;
    //private ArrayList<JSONObject> langJSONObject;
    public LanguageChangeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            appSession = AppSession.getInstance(mActivity);
            mActivity.setMainVisibility(this, null);
            appApplication=(AppApplication)mActivity.getApplication();
        } else if (context instanceof BrokerActivity) {
            this.mBrokerActivity = (BrokerActivity) context;
            appSession = AppSession.getInstance(mBrokerActivity);
            mBrokerActivity.setMainVisibility(this, null);
            appApplication=(AppApplication)mBrokerActivity.getApplication();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_language_change, container, false);
        initializer(view);

        setUpToolBar();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
      /*          if (appSession.getSavedLangPos()==0) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("type", "home");
                    mActivity.displayViewOther(22, bundle1);
                } else {*/
                    if (getActivity() != null) {
                        appApplication.showSnackBar(btnContinue,"Your have changed the Language to  "+appSession.getSelectedAppLang());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(getActivity(), SplashActivity.class);
                                i.putExtra("comingFrom", "LanguageChangedFromSettings");
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                getActivity().finish();
                            }
                        },3000);

                    }
              /*  }*/
                appSession.setCLicked(true);
            }
        });

        return view;
    }

    private void initializer(View view) {
        langList = new ArrayList<>();
        langJSONArray = new JSONArray();
        btnContinue = view.findViewById(R.id.btn_lang_continue);
        rvLanguageChosen = view.findViewById(R.id.rv_language_support);
        setLanguageAdapter();

    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolbar_title_language_support),
                    true, false, false, false, false, false, false, "");
            fragToolBar.setCallback(this);
        }
    }

    /*******************************************
     * Method used to set dynamic language items
     *******************************************/

    private void setLanguageAdapter() {
        if (getActivity() == mActivity) {
            languageSupportAdapter = new LanguageSettingAdapter(mActivity, new ArrayList<JSONObject>());
            rvLanguageChosen.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
            rvLanguageChosen.addItemDecoration(new DividerItemDecoration(mActivity, LinearLayoutManager.VERTICAL));
            rvLanguageChosen.setItemAnimator(new DefaultItemAnimator());
            rvLanguageChosen.setNestedScrollingEnabled(false);
            rvLanguageChosen.setAdapter(languageSupportAdapter);
            setUpUiVisibility();
        } else if (getActivity() == mBrokerActivity) {
            languageSupportAdapter = new LanguageSettingAdapter(mBrokerActivity, new ArrayList<JSONObject>());
            rvLanguageChosen.setLayoutManager(new LinearLayoutManager(mBrokerActivity, LinearLayoutManager.VERTICAL, false));
            rvLanguageChosen.addItemDecoration(new DividerItemDecoration(mBrokerActivity, LinearLayoutManager.VERTICAL));
            rvLanguageChosen.setItemAnimator(new DefaultItemAnimator());
            rvLanguageChosen.setNestedScrollingEnabled(false);
            rvLanguageChosen.setAdapter(languageSupportAdapter);
            setUpUiVisibility();
        }
    }

    private void setUpUiVisibility() {
        if (Utils.getConfigData(appSession).optJSONArray("LanguageList").length() > 0) {
            langJSONArray = Utils.getConfigData(appSession).optJSONArray("LanguageList");
            ArrayList<JSONObject> langJSONObject = new ArrayList<>();

            for (int i = 0; i < langJSONArray.length(); i++) {
                JSONObject object = null;
                try {
                    object = langJSONArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                langJSONObject.add(object);
            }
            languageSupportAdapter.upDateLangList(langJSONObject);
        } else {
            languageSupportAdapter.upDateLangList(new ArrayList<JSONObject>());
        }
    }



    @Override
    public void onToolbarItemClick(View view) {

    }
}
