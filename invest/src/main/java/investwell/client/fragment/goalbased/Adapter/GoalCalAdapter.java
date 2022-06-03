package investwell.client.fragment.goalbased.Adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.fragment.goalbased.Fragment.FragGoalCategory;
import investwell.client.activity.MainActivity;
import investwell.utils.AppSession;

public class GoalCalAdapter extends BaseAdapter {
    private Context mContext;
    private MainActivity mActivity;
    private AppSession mSession;
    public ArrayList<JSONObject> mDataList;

    public GoalCalAdapter(Context context,  ArrayList<JSONObject> mDataList, FragGoalCategory tab) {
        this.mContext = context;
        this.mDataList = mDataList;
        mActivity = (MainActivity) context;
        mSession = AppSession.getInstance(context);
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_goalbase, null);

        }

        ImageView ivIcon = convertView.findViewById(R.id.ivIcon);
        TextView tvText = convertView.findViewById(R.id.tvText);

        final JSONObject object = mDataList.get(position);
        tvText.setText(object.optString("CategoryName"));
        String path = object.optString("CategoryIcon");
        Picasso.get().load(path).placeholder(R.mipmap.tranparent).into(ivIcon);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemclickc(object.toString());
            }
        });
        return convertView;

    }

    private void itemclickc(String cat) {
        Bundle bundle = new Bundle();
        bundle.putString("category_data", cat);
        mActivity.displayViewOther(76, bundle);
    }


    public void updateList(List<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }


}
