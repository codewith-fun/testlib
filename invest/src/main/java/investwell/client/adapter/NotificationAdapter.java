package investwell.client.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import investwell.client.activity.SignUpActivity;
import investwell.client.activity.WebViewActivity;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private NotificationAdapter.OnItemClickListener listener;
    private String mType = "";
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

    public NotificationAdapter(Context context, ArrayList<JSONObject> list) {
        mContext = context;
        mDataList = list;

    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_notification, viewGroup, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotificationAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.setItem(position);

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void updateList(List<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTvTitle, mTvMessage, mTime, mTvViewMore;
        ImageView mIvBannerNotification;


        public ViewHolder(View view) {
            super(view);
            mTvViewMore = view.findViewById(R.id.textView30);
            mTvTitle = view.findViewById(R.id.tvTitle);
            mTvMessage = view.findViewById(R.id.tvMessage);
            mTime = view.findViewById(R.id.time);
            mIvBannerNotification = view.findViewById(R.id.iv_notification_banner);
        }


        public void setItem(final int position) {
            final JSONObject jsonObject = mDataList.get(position);
            mTvTitle.setText(jsonObject.optString("title"));
       /*     Pattern p = Pattern.compile(URL_REGEX);
            Matcher m = p.matcher("example.com");//replace with string to compare
            if (m.find()) {
                StringBuilder sb = new StringBuilder();
                for (String s : pullLinks(jsonObject.optString("text"))) {
                    sb.append(s);
                }
                String url = sb.toString();
                SpannableString SpanString = new SpannableString(
                        jsonObject.optString("text"));

                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View textView) {
                        Intent mIntent = new Intent(mContext, WebViewActivity.class);
                        mIntent.putExtra("title", "Notification");
                        mIntent.putExtra("url", url);
                        mContext.startActivity(mIntent);

                    }
                };

// Finding string length
                int n = url.length();
                SpanString.setSpan(clickableSpan, jsonObject.optString("text").indexOf(url), jsonObject.optString("text").indexOf(url) + n, 0);
                SpanString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorAccent)), jsonObject.optString("text").indexOf(url), jsonObject.optString("text").indexOf(url) + n, 0);
                SpanString.setSpan(new UnderlineSpan(), jsonObject.optString("text").indexOf(url), jsonObject.optString("text").indexOf(url) + n, 0);
                mTvMessage.setMovementMethod(LinkMovementMethod.getInstance());
                mTvMessage.setText(SpanString, TextView.BufferType.SPANNABLE);
                mTvMessage.setSelected(true);
            } else {*/
            mTvMessage.setText(jsonObject.optString("text"));

            /* }*/
            mTime.setText(jsonObject.optString("time"));
            if (!TextUtils.isEmpty(jsonObject.optString("imgpath")) && !jsonObject.optString("imgpath").equalsIgnoreCase("null")) {
                Picasso.get().load(jsonObject.optString("imgpath")).into(mIvBannerNotification);
                mIvBannerNotification.setVisibility(View.VISIBLE);
            }else {
                mIvBannerNotification.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(jsonObject.optString("link")) && !jsonObject.optString("link").equalsIgnoreCase("null")) {
                mTvViewMore.setVisibility(View.VISIBLE);
                itemView.setOnClickListener(view -> {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(jsonObject.optString("link")));
                    mContext.startActivity(i);
                });
                mTime.setOnClickListener(view -> {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(jsonObject.optString("link")));
                    mContext.startActivity(i);
                });
                mTvMessage.setOnClickListener(view -> {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(jsonObject.optString("link")));
                    mContext.startActivity(i);
                });
                mTvViewMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try{
                            String url = jsonObject.optString("link");
                            if (!url.startsWith("https://") && !url.startsWith("http://")){
                                url = "http://" + url;
                            }
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            mContext.startActivity(i);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(mContext, "Can't open", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                mTvViewMore.setVisibility(View.GONE);
            }

        }
    }

    //Pull all links from the body for easy retrieval
    public ArrayList<String> pullLinks(String text) {
        ArrayList<String> links = new ArrayList<String>();

        //String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        String regex = "\\(?\\b(https?://|www[.]|ftp://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        int i = 0;
        String urlStr = "";
        while (m.find()) {
            i++;
            urlStr = m.group();

            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }


        }
        if (i < 2) {
            links.add(urlStr);
        }

        return links;
    }
}



