
package investwell.client.fragment.help;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.activity.MainActivity;
import investwell.utils.AppSession;
import investwell.utils.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;


public class FragHelpFAQs extends Fragment {
    public MainActivity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;


        }
    }
private SwipeRefreshLayout mSwipe;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_help_faq, container, false);
        AppSession mSession = AppSession.getInstance(getActivity());
        mSwipe=view.findViewById(R.id.refresh);
        RecyclerView mQues_Recycle = view.findViewById(R.id.ques_recycle);
        WebView mWebview=view.findViewById(R.id.faq_online);
        mWebview.loadUrl("https://m.investwell.in/knowledgeBase/kb.html");
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        mQues_Recycle.setHasFixedSize(true);
        mQues_Recycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        String[] ques = new String[]{getString(R.string.faq_Q1), getString(R.string.faq_Q2), getString(R.string.faq_Q3), getString(R.string.faq_Q4), getString(R.string.faq_Q5) + " " + Utils.getConfigData(mSession).optString("BrokerName") + "?", getString(R.string.faq_Q6), getString(R.string.faq_Q7),
                getString(R.string.faq_Q8), getString(R.string.faq_Q9), getString(R.string.faq_Q10)};
        String[] ans = new String[]{getString(R.string.faq_A1), getString(R.string.faq_A2), getString(R.string.faq_A3), getString(R.string.faq_A4), getString(R.string.faq_A5) + " " + Utils.getConfigData(mSession).optString("Email"), getString(R.string.faq_A6) + " " + Utils.getConfigData(mSession).optString("BrokerName"), getString(R.string.faq_A7), getString(R.string.faq_A8), getString(R.string.faq_A9), getString(R.string.faq_A10)};


        FaQAdapter faQAdapter = new FaQAdapter(getActivity(), ques, ans);
        mQues_Recycle.setAdapter(faQAdapter);


        return view;
    }
    public class CustomWebClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mSwipe.setRefreshing(true);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
mSwipe.setRefreshing(true);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mSwipe.setRefreshing(false);

            return false;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            //Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
            mSwipe.setRefreshing(false);

        }
    }

    public static class FaQAdapter extends RecyclerView.Adapter<FaQAdapter.MyViewHolder> {


        public ArrayList<JSONObject> jsonObject;
        Context context;
        String[] ques, ans;


        public FaQAdapter(Context context, String[] Ques, String[] Ans) {

            this.context = context;
            this.ques = Ques;
            this.ans = Ans;

        }

        @NonNull
        @Override
        public FaQAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_help_faq_item, parent, false);
            return new MyViewHolder(v);
        }


        @Override
        public void onBindViewHolder(final FaQAdapter.MyViewHolder holder, final int position) {
            holder.mTvQues.setText(ques[position]);
            holder.mTvAns.setText(ans[position]);


            holder.cardView.setOnClickListener(view -> {
                if (holder.mTvAns.getVisibility() == View.VISIBLE) {
                    holder.mIvOpen.setBackgroundResource(R.drawable.down_arrow);
                    holder.mTvAns.setVisibility(View.GONE);
                } else {
                    holder.mTvAns.setVisibility(View.VISIBLE);
                    holder.mIvOpen.setBackgroundResource(R.drawable.up_arrow);
                }

            });

        }

        @Override
        public int getItemCount() {

            return ques.length;

        }


        public static class MyViewHolder extends RecyclerView.ViewHolder {


            private TextView mTvQues, mTvAns;
            private ImageView mIvOpen;
            private CardView cardView;

            public MyViewHolder(View view) {
                super(view);
                mTvQues = view.findViewById(R.id.TvQues);
                mIvOpen = view.findViewById(R.id.IvOpen);
                mTvAns = view.findViewById(R.id.TvAns);
                cardView = view.findViewById(R.id.cardView);


            }
        }


    }
}
