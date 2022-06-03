package investwell.client.activity;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.broker.activity.BrokerActivity;
import investwell.client.adapter.LanguageSupportAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.common.applock.AppLockOptionActivity;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppSession;
import investwell.utils.Utils;
import investwell.utils.model.LanguageBean;

public class LanguageSupportActivity extends BaseActivity implements LanguageSupportAdapter.LanguageClickListener {
    public ToolbarFragment fragToolBar;
    private LanguageSupportAdapter languageSupportAdapter;
    private List<LanguageBean> langList;
    private Button btnContinue;
    private RecyclerView rvLanguageChosen;
    private AppSession appSession;
    private JSONArray langJSONArray;
    private ArrayList<JSONObject> langJSONObject;
    public int langClicked = 0;
    private AppApplication appApplication;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appSession = AppSession.getInstance(LanguageSupportActivity.this);
        setContentView(R.layout.activity_language_support);
        appApplication = (AppApplication) this.getApplication();
        setUpToolBar();
        initializer();


        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appSession.getSavedLangPos() == 0) {
                    launchNextActivity();
                } else {
                    appApplication.showSnackBar(btnContinue, "Your have changed the Language to  " + appSession.getSelectedAppLang());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(LanguageSupportActivity.this, SplashActivity.class);
                            i.putExtra("comingFrom", "LanguageChanged");
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        }
                    }, 2000);

                }
            }
        });
    }
    //Error Content Initializer
    private void errorContentInitializer(View view) {
        viewNoData = view.findViewById(R.id.content_no_data);
        tvErrorMessage = viewNoData.findViewById(R.id.tv_error_message);
        ivErrorImage = viewNoData.findViewById(R.id.iv_error_image);
    }

    //Display Connection TimeOut Content
    private void displayConnectionTimeOut() {
        viewNoData.setVisibility(View.VISIBLE);
        ivErrorImage.setImageResource(R.drawable.bg_no_interent);
        tvErrorMessage.setText(R.string.error_connection_timeout);
    }

    //Display Server Error Content
    private void displayServerErrorMessage(VolleyError error) {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_data_found);
        tvErrorMessage.setText(error.getLocalizedMessage());
    }
    //Display Server Error Content
    private void displayServerMessage(String error) {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_data_found);
        tvErrorMessage.setText(error);
    }
    //Display Network Error Content
    private void displayNoInternetMessage() {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_interent);
        tvErrorMessage.setText(R.string.no_internet);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initializer() {
        langList = new ArrayList<>();
        langJSONArray = new JSONArray();
        langJSONObject = new ArrayList<>();
        btnContinue = findViewById(R.id.btn_lang_continue);
        rvLanguageChosen = findViewById(R.id.rv_language_support);
        appSession.setDefaultAppLang(getResources().getString(R.string.app_language_english));
        setLanguageAdapter();
    }

    private void setUpUiVisibility() {
        if (Utils.getConfigData(appSession).optJSONArray("LanguageList").length() > 0) {
            langJSONArray = Utils.getConfigData(appSession).optJSONArray("LanguageList");
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
            langJSONObject.clear();
            languageSupportAdapter.upDateLangList(new ArrayList<JSONObject>());
        }
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolbar_title_language_support),
                    false, false, false, false, false, false, false, "");
        }
    }

    /*******************************************
     * Method used to set dynamic language items
     *******************************************/

    private void setLanguageAdapter() {
        languageSupportAdapter = new LanguageSupportAdapter(this, new ArrayList<JSONObject>());
        rvLanguageChosen.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvLanguageChosen.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rvLanguageChosen.setItemAnimator(new DefaultItemAnimator());
        rvLanguageChosen.setNestedScrollingEnabled(false);
        rvLanguageChosen.setAdapter(languageSupportAdapter);
        setUpUiVisibility();
    }

    private void launchNextActivity() {
        if (appSession.getUserType().equals("") || !appSession.getHasFirstTimeCompleted()) {

            if (Utils.getConfigData(appSession).optString("AppIntroScreen").equalsIgnoreCase("Y")) {
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(getApplicationContext(), UserTypesActivity.class);
                startActivity(intent);
                finish();
            }

        } else if (appSession.getHasAppLockEnable()) {
            Intent intent = new Intent(getApplicationContext(), AppLockOptionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("type", "verify_lock");
            intent.putExtra("callFrom", "splash");
            startActivity(intent);
            finish();
        } else if (appSession.getHasLoging()) {
            if (appSession.getAppLockType().equalsIgnoreCase("nothing")) {
                if ((appSession.getHasLoging() && appSession.getLoginType().equals("Client"))
                        || (appSession.getHasLoging() && appSession.getLoginType().equals("ClientG"))
                        || (appSession.getHasLoging() && appSession.getLoginType().equals("Prospects"))) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), BrokerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), AppLockOptionActivity.class);
                intent.putExtra("type", "set_screen_lock");
                intent.putExtra("callFrom", "login");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void onLanguageSelected(View view) {
       /* switch (view.getId()) {

            case R.id.ll_language_support:

                Lingver.getInstance().setLocale(this, "en");
                appSession.setDefaultAppLang(getResources().getString(R.string.app_language_english) + "(" + getResources().getString(R.string.app_language_english_inverse) + ")");
                appSession.setSelectedAppLang("English");
                appSession.setSavedLangPos(0);
                langClicked = 0;
                break;
            case 1:
                Lingver.getInstance().setLocale(this, "hi");
                appSession.setDefaultAppLang(getResources().getString(R.string.app_language_hindi) + "(" + getResources().getString(R.string.app_language_hindi_inverse) + ")");
                appSession.setSelectedAppLang("Hindi");
                appSession.setSavedLangPos(1);
                langClicked = 1;
                break;
            case 2:
                Lingver.getInstance().setLocale(this, "gu");
                appSession.setDefaultAppLang(getResources().getString(R.string.app_language_gujrati) + "(" + getResources().getString(R.string.app_language_gujrati_inverse) + ")");
                appSession.setSelectedAppLang("Gujarati");
                appSession.setSavedLangPos(2);
                langClicked = 2;

                break;
            case 3:
                Lingver.getInstance().setLocale(this, "mr");
                appSession.setDefaultAppLang(getResources().getString(R.string.app_language_marathi) + "(" + getResources().getString(R.string.app_language_marathi_inverse) + ")");
                appSession.setSelectedAppLang("Marathi");
                appSession.setSavedLangPos(3);
                langClicked = 3;

                break;
            case 4:
                Lingver.getInstance().setLocale(this, "bn");
                appSession.setDefaultAppLang(getResources().getString(R.string.app_language_bengali) + "(" + getResources().getString(R.string.app_language_bengali_inverse) + ")");
                appSession.setSelectedAppLang("Bangla");
                appSession.setSavedLangPos(4);
                langClicked = 4;

                break;
            case 5:
                Lingver.getInstance().setLocale(this, "ta");
                appSession.setDefaultAppLang(getResources().getString(R.string.app_language_tamil) + "(" + getResources().getString(R.string.app_language_tamil_inverse) + ")");
                appSession.setSelectedAppLang("Tamil");
                appSession.setSavedLangPos(5);
                langClicked = 5;

                break;
            case 6:
                Lingver.getInstance().setLocale(this, "te");
                appSession.setDefaultAppLang(getResources().getString(R.string.app_language_telgu) + "(" + getResources().getString(R.string.app_language_telgu_inverse) + ")");
                appSession.setSelectedAppLang("Telegu");
                appSession.setSavedLangPos(6);
                langClicked = 6;

                break;
            case 7:
                Lingver.getInstance().setLocale(this, "kn");
                appSession.setDefaultAppLang(getResources().getString(R.string.app_language_kannada) + "(" + getResources().getString(R.string.app_language_kannada_inverse) + ")");
                appSession.setSelectedAppLang("Kannad");
                appSession.setSavedLangPos(7);
                langClicked = 7;

                break;
            case 8:
                Lingver.getInstance().setLocale(this, "pa");
                appSession.setDefaultAppLang(getResources().getString(R.string.app_language_punjabi) + "(" + getResources().getString(R.string.app_language_punjabi_inverse) + ")");
                appSession.setSelectedAppLang("Punjabi");
                appSession.setSavedLangPos(8);
                langClicked = 8;

                break;
            default:
                Lingver.getInstance().setLocale(this, "en");
                appSession.setDefaultAppLang(getResources().getString(R.string.app_language_english) + "(" + getResources().getString(R.string.app_language_english_inverse) + ")");
                appSession.setSelectedAppLang("English");
                langClicked = 0;

                break;

        }*/
    }
}
