package investwell.common.welcome;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import investwell.utils.AppSession;
import investwell.utils.Utils;


public class ViewPagerFragment extends Fragment {
    private int mPostion = 0;
    private TextView mTvTitle, mTvDescriptions;
    private AppSession mSession;
    private ImageView mImageView1;

    public static Fragment getInstance(int positon) {
        ViewPagerFragment f = new ViewPagerFragment();
        Bundle args = new Bundle();
        args.putInt("position", positon);
        f.setArguments(args);
        return f;
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_viewpager, container, false);
        mTvTitle = view.findViewById(R.id.tv_header_title);
        mTvDescriptions = view.findViewById(R.id.tv_header_desc);
        mImageView1 = view.findViewById(R.id.iv_big_body_image);


        mSession = AppSession.getInstance(getActivity());
        mPostion = getArguments().getInt("position");

        setSCreens(mPostion);
        return view;
    }

    private void setSCreens(int position) {
        JSONArray count = Utils.getConfigData(mSession).optJSONArray("SliderList");
        for (int i = 0; i < count.length(); i++) {

            if (i == position) {

                JSONObject jsonObject = count.optJSONObject(mPostion);
                setData(jsonObject);
            }


        }


    }

    private void setData(JSONObject jsonObject) {
        if (!TextUtils.isEmpty(jsonObject.optString("Title")) && jsonObject.optString("Title") != null) {
            mTvTitle.setText(jsonObject.optString("Title"));
        }
        if (!TextUtils.isEmpty(jsonObject.optString("Description")) && jsonObject.optString("Description") != null) {
            mTvDescriptions.setText(jsonObject.optString("Description"));
        }
        if (!TextUtils.isEmpty(jsonObject.optString("ImagePath")) && jsonObject.optString("ImagePath") != null) {
            Picasso.get().load(jsonObject.optString("ImagePath")).error(R.drawable.bg_white).into(mImageView1);
        }


    }


}
