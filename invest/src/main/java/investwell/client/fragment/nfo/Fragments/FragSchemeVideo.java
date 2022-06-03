package investwell.client.fragment.nfo.Fragments;

import static investwell.utils.signature.SignatureView.TAG;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class FragSchemeVideo extends Fragment {

    private MainActivity mActivity;
    private AppApplication mApplication;
    private View view;
    VideoView videoView;
    TextView nfoTitle,nfoDesc;
    AppSession mSession;
    private RequestQueue requestQueue;
    private boolean passKey;
    private String exclCode, SchemeName;
    public ToolbarFragment fragToolBar;
    private Bundle bundle;
    private LinearLayout frag_video;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_frag_scheme_video, container, false);
        mSession = AppSession.getInstance(getActivity());
        mActivity = (MainActivity) getActivity();
        mApplication = (AppApplication) getActivity().getApplication();
        bundle = getArguments();
        if (bundle != null && bundle.containsKey("Exlcode")) {
            exclCode = bundle.getString("Exlcode");
        }
        if (bundle != null && bundle.containsKey("SchemeName")) {
            SchemeName = bundle.getString("SchemeName");
        }
        setUpToolBar();
        setInitializer(view);
        videoListApi();
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(SchemeName, true, false, false, false, false, false, false, "");

        }
    }

    private void setInitializer(View view) {

        nfoTitle = view.findViewById(R.id.nfo_video_title);
        nfoDesc = view.findViewById(R.id.nfo_video_desc);
        frag_video = view.findViewById(R.id.frag_video);
        frag_video.setVisibility(View.GONE);

       // getLifecycle().addObserver(youTubePlayerView);
    }
    private void setVideo(String videoUrl) {


       /* youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                Log.e("12345",videoUrl);


                Log.e("12345",videoID);
                youTubePlayer.loadVideo(videoID,0);
            }
        });*/

    }

    private   void watchYoutubeVideo( String videoUrl){
        String videoID = videoUrl.replace("https://www.youtube.com/embed/","");
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoID));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + videoID));
        try {
            startActivity(appIntent);
            getActivity().getSupportFragmentManager().popBackStack();
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }

    }

    private void videoListApi() {
        String url = Config.Get_Scheme_Video;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY,mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID,AppConstants.APP_BID);
            jsonObject.put("Exlcode", exclCode);
            jsonObject.put("FormatReq", "Y");
            jsonObject.put("Category", "N");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        String Status = response.optString("Status");
                        if (Status.equalsIgnoreCase("True")) {
                            JSONArray list = response.getJSONArray("ResponseData");
                            JSONObject jsonObject1 = list.optJSONObject(0);
                           // [{"Title":"Video for NFO","Category":"NFO","URLLink":"https:\/\/www.youtube.com\/embed\/SABZzcT2YeE","Description":"Top ICIC Mutual Fund","Exlcode":"65529"}]
                          if (jsonObject1.has("URLLink") && !jsonObject1.optString("URLLink").equals("")) {
                                frag_video.setVisibility(View.VISIBLE);
                                nfoTitle.setText(jsonObject1.optString("Title"));
                                nfoDesc.setText(jsonObject1.optString("Description"));
                                //setVideo();
                              watchYoutubeVideo(jsonObject1.optString("URLLink"));
                            }
                        }else {

                            String ServiceMSG = response.optString("ServiceMSG");
                            Toast.makeText(mActivity, ServiceMSG, Toast.LENGTH_SHORT).show();

                           // watchYoutubeVideo("https://www.youtube.com/embed/SABZzcT2YeE");
                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                    } finally {

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mActivity,"Try Again"+error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
