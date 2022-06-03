package investwell.client.fragment.schemes;


import static com.facebook.FacebookSdk.getApplicationContext;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.iw.acceleratordemo.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.FragSearchSchemesAdapter;
import investwell.client.adapter.SectionTypeOneAdapter;
import investwell.client.flavourtypetwo.adapter.SectionsTypeTwoAdapter;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.DrawableClickListener;
import investwell.utils.SearchAnimationToolbar;
import investwell.utils.Utils;
import investwell.utils.customView.CustomEditText;


public class FragSearchSchemes extends Fragment implements View.OnClickListener {
    private AppSession mSession;
    public static String values;
    private MainActivity mActivity;
    private AppApplication mAppplication;
    private FragSearchSchemesAdapter mAdapter;
    private TextView mTvLoading;
    private ShimmerFrameLayout mShimmerViewContainer;
    private RecyclerView recyclerView;
    private CustomEditText mEtSearch;
    private ImageView mIvSearchTextClear;

    private LinearLayoutManager mLayoutManager;
    private Timer timer = new Timer();
    private final long DELAY = 1000; // milliseconds
    private SearchAnimationToolbar toolbar;
    private LinearLayout fullScreenShedow;
    private RelativeLayout singleShedow;
    private String mType = "";
    private SectionTypeOneAdapter sectionTypeOneAdapter;
    private JSONArray sectionJSONArray;
    private ArrayList<JSONObject> sectionJSONObjectList;
    private RecyclerView rvInvestRoute, rvInvestRoute1A, rvDashboardData;
    private String secTitle = "";
    private AppBarLayout appBarSecondary, appbarPrimary;
    private SectionsTypeTwoAdapter sectionsTypeTwoAdapter;
    private String screenType = "";
    private TextView mTvToolBarTitle;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mAppplication = (AppApplication) mActivity.getApplication();
            mActivity.setMainVisibility(this, null);

        }
    }


    @Override
    public void onStop() {
        super.onStop();

        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            IBinder binder = getActivity().getCurrentFocus().getWindowToken();
            if (imm.isAcceptingText() && binder != null) {
                imm.hideSoftInputFromWindow(binder, 0);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.frag_search_scheme, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("type")) {
            mType = bundle.getString("type");

        }
        if (bundle != null) {
            if (!TextUtils.isEmpty(bundle.getString("openDirectSearch"))) {
                screenType = bundle.getString("openDirectSearch");

            }
        }
        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mActivity);

        sectionJSONArray = new JSONArray();
        sectionJSONObjectList = new ArrayList<>();
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        fullScreenShedow = view.findViewById(R.id.fullScreenShedow);
        singleShedow = view.findViewById(R.id.singleShedow);
        mEtSearch = view.findViewById(R.id.etSearch);
        mIvSearchTextClear = view.findViewById(R.id.iv_clear_search);
        rvDashboardData = view.findViewById(R.id.rv_invest_data);
        appbarPrimary = view.findViewById(R.id.primary_appbar);
        appBarSecondary = view.findViewById(R.id.appbar);
        mTvLoading = view.findViewById(R.id.tvLoading);
        mTvLoading.setVisibility(View.GONE);
        mTvToolBarTitle = view.findViewById(R.id.tv_search_tool_title);
        mTvToolBarTitle.setText(mSession.getInvestNow());
        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setNestedScrollingEnabled(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FragSearchSchemesAdapter(mActivity, new ArrayList<JSONObject>(), new FragSearchSchemesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    JSONObject jsonObject = mAdapter.mDataList.get(position);

                    if (mType.equalsIgnoreCase("scheme_for_just_save")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("data", jsonObject.toString());
                        mActivity.removesFragmentsFromBackStack(2);
                        mActivity.displayViewOther(96, bundle);
                    } else {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("SchName", jsonObject.optString("SchemeName"));
                        jsonObject1.put("Scode", jsonObject.optString("SchemeCode"));
                        jsonObject1.put("Fcode", jsonObject.optString("FCode"));
                        jsonObject1.put("Exlcode", jsonObject.optString("ExlCode"));

                        Bundle bundle = new Bundle();
                        bundle.putString("passkey", mSession.getPassKey());
                        bundle.putString("excl_code", jsonObject.optString("ExlCode"));
                        bundle.putString("bid", AppConstants.APP_BID);
                        bundle.putString("scheme", jsonObject.optString("SchemeName"));
                        bundle.putString("type", "scheme");
                        bundle.putString("object", jsonObject1.toString());
                        mActivity.displayViewOther(42, bundle);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        recyclerView.setAdapter(mAdapter);


        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                mIvSearchTextClear.setVisibility(mEtSearch.getText().length() > 0 ? View.VISIBLE : View.GONE);
                if (cs.length() > 0) {
                    rvDashboardData.setVisibility(View.GONE);
                } else {
                    if (screenType.equalsIgnoreCase("DirectSearch")) {
                        rvDashboardData.setVisibility(View.GONE);
                    } else {
                        rvDashboardData.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                String text = editable.toString();
                if (!text.isEmpty()) {
                    int strLength = text.length();
                    if (strLength >= 3) {
                        startSearch(text);
                    } else {
                        mAdapter.updateList(new ArrayList<JSONObject>());
                    }

                } else {
                    mAdapter.updateList(new ArrayList<JSONObject>());
                }


            }
        });
        if (mSession.getAudioPermission().equalsIgnoreCase("G")) {
            //TOODO Nothing
        } else {
            // requestAudioPermission();
        }


        mEtSearch.setOnEditorActionListener(editorActionListener);
     /*   final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                mEtSearch.setHint("Listening....");
            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {
                mEtSearch.setHint("Searching Out...");
            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mEtSearch.setHint("Ooops! unable to process");
                    }
                }, 3000);

            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null)
                    mEtSearch.setText(matches.get(0));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.requireNonNull(mEtSearch.getText()).length() > 3) {
                        startSearch(mEtSearch.getText().toString());

                    }
                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        mEtSearch.setDrawableClickListener(new DrawableClickListener() {


            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:

                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        break;

                    default:
                        break;
                }
            }

        });

        */

        setUpSectionsAdapter(view);
        if (screenType.equalsIgnoreCase("DirectSearch")) {
            rvDashboardData.setVisibility(View.GONE);
            appbarPrimary.setVisibility(View.GONE);
            appBarSecondary.setVisibility(View.VISIBLE);
        } else {
            rvDashboardData.setVisibility(View.VISIBLE);
            appbarPrimary.setVisibility(View.VISIBLE);
            appBarSecondary.setVisibility(View.GONE);
        }
        view.findViewById(R.id.ivBack).setOnClickListener(this);
        view.findViewById(R.id.ivBackPrimary).setOnClickListener(this);
        view.findViewById(R.id.iv_search).setOnClickListener(this);
        view.findViewById(R.id.iv_clear_search).setOnClickListener(this);
    }

    /**
     * Requesting multiple permissions (storage and location) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestAudioPermission() {
        final boolean abc = true;
        Dexter.withActivity(mActivity)
                .withPermissions(
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            mSession.setAudioPermission("G");
                            mAppplication.showSnackBar(mTvLoading, "Audio permissions  granted!");


                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void setUpSectionsAdapter(View view) {
        sectionsTypeTwoAdapter = new SectionsTypeTwoAdapter(mActivity, new ArrayList<JSONObject>());
        sectionTypeOneAdapter = new SectionTypeOneAdapter(mActivity, new ArrayList<JSONObject>());
        String appType = "";
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2")) {
            appType = Utils.getConfigData(mSession).optString("APPType");

            rvDashboardData.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
            rvDashboardData.setItemAnimator(new DefaultItemAnimator());
            rvDashboardData.setNestedScrollingEnabled(false);
            rvDashboardData.setAdapter(sectionsTypeTwoAdapter);
        } else {
            appType = Utils.getConfigData(mSession).optString("APPType");
            rvDashboardData.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
            rvDashboardData.setItemAnimator(new DefaultItemAnimator());
            rvDashboardData.setNestedScrollingEnabled(false);
            rvDashboardData.setAdapter(sectionTypeOneAdapter);
        }

        setUpUiVisibility(view, appType);
    }

    private void setUpUiVisibility(View view, String appType) {
        view.findViewById(R.id.primary_appbar).setVisibility(View.VISIBLE);
        view.findViewById(R.id.appbar).setVisibility(View.GONE);
        if (Utils.getConfigData(mSession).optJSONArray("SectionList").length() > 0) {
            sectionJSONArray = Utils.getConfigData(mSession).optJSONArray("SectionList");
            for (int i = 0; i < sectionJSONArray.length(); i++) {
                JSONObject object = null;
                try {
                    object = sectionJSONArray.getJSONObject(i);
                    if (object.optString("Priority").equals("99")) {
                        sectionJSONObjectList.add(object);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            for (int i = 0; i < sectionJSONObjectList.size(); i++) {

                secTitle = sectionJSONObjectList.get(i).optString("Title");
            }
            sectionTypeOneAdapter.updateSectionsList(sectionJSONObjectList, secTitle);
            sectionsTypeTwoAdapter.updateSectionsList(sectionJSONObjectList, appType);
        } else {
            sectionJSONObjectList.clear();
            sectionTypeOneAdapter.updateSectionsList(new ArrayList<JSONObject>(), secTitle);
            sectionsTypeTwoAdapter.updateSectionsList(new ArrayList<JSONObject>(), appType);
        }
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                if (!TextUtils.isEmpty(mEtSearch.getText()) && mEtSearch.getText().length() >= 3)
                    startSearch(mEtSearch.getText().toString());
                else
                    mAdapter.updateList(new ArrayList<JSONObject>());
                return true;
            }
            return false;
        }

    };

    private void startSearch(final String searchText) {
        timer.cancel();
        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getSearchScheme(searchText);
                                }
                            });

                        }
                    }
                },
                DELAY
        );
    }

    public void crossFade(View viewToBeVisible, final View viewToBeInvisible) {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        viewToBeVisible.setAlpha(0f);
        viewToBeVisible.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        viewToBeVisible.animate()
                .alpha(1f)
                .setDuration(1000)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout_gridview_type_two_a passes, etc.)
        viewToBeInvisible.animate()
                .alpha(0f)
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        viewToBeInvisible.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivBack) {
            if (screenType.equalsIgnoreCase("DirectSearch")) {
                mActivity.getSupportFragmentManager().popBackStack();

            } else {
                appBarSecondary.setVisibility(View.GONE);
                appbarPrimary.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                rvDashboardData.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.ivBackPrimary) {
            mActivity.getSupportFragmentManager().popBackStack();
        } else if (id == R.id.iv_clear_search) {
            mEtSearch.setText("");
            mIvSearchTextClear.setVisibility(View.GONE);
            appbarPrimary.setVisibility(View.GONE);
            rvDashboardData.setVisibility(View.GONE);
            mAdapter.updateList(new ArrayList<JSONObject>());
        } else if (id == R.id.iv_search) {
            appBarSecondary.setVisibility(View.VISIBLE);
            appbarPrimary.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            mEtSearch.setText("");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.action_search) {
            toolbar.onSearchIconClick();
            return true;
        } else if (itemId == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




 /*   @Override
    public void onSearchQueryChanged(final String query) {
        timer.cancel();
        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSearchKey = query;
                                    getSearchScheme("a");
                                }
                            });

                        }
                    }
                },
                DELAY
        );
    }*/


    private void getSearchScheme(String text) {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        if (mAdapter.mDataList.size() > 1) {
            singleShedow.setVisibility(View.VISIBLE);
            fullScreenShedow.setVisibility(View.GONE);
        } else {
            singleShedow.setVisibility(View.GONE);
            fullScreenShedow.setVisibility(View.VISIBLE);
        }

        String url = Config.Search_list;
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put("SearchValue", text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<JSONObject> list = new ArrayList<>();
                try {

                    if (response.optBoolean("Status")) {
                        JSONArray jsonArray = response.optJSONArray("SchemeSearchDetail");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            list.add(jsonObject1);
                        }
                        mAdapter.updateList(list);

                    } else {

                        mAppplication.showSnackBar(mEtSearch, response.optString("ServiceMSG"));
                        // mEtSearch.setText("");
                        timer.cancel();
                    }
                } catch (Exception e) {

                } finally {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    if (list.size() > 0) {
                        mAdapter.updateList(list);
                        mTvLoading.setVisibility(View.GONE);
                    } else {
                        mAdapter.updateList(new ArrayList<JSONObject>());
                        mTvLoading.setVisibility(View.VISIBLE);
                        // mEtSearch.setText("");
                        timer.cancel();
                        mEtSearch.setSelection(mEtSearch.getText().length());
                        // mEtSearch.setHint("No Matching Schemes found");

                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    try {
                        JSONObject jsonObject = new JSONObject(error.getMessage());

                        mAppplication.showSnackBar(mEtSearch, jsonObject.optString("error"));
                        // mEtSearch.setText("");
                        timer.cancel();
                        mEtSearch.setSelection(mEtSearch.getText().length());
                        //mEtSearch.setHint("No Matching Schemes found");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError) {
                    if (getActivity() != null)
                        mAppplication.showSnackBar(mEtSearch, getResources().getString(R.string.no_internet));
                }
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
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);


    }


}
