package investwell.client.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.WebViewActivity;

/**
 * Created by gigabyte on 21-09-2016.
 */
public class DocumentAllAdapter extends RecyclerView.Adapter<DocumentAllAdapter.MyViewHolder> {
    public List<JSONObject> mDataListRycleView;
    private Context mContaxt;

    public DocumentAllAdapter(Context context, ArrayList<JSONObject> horizontalList) {
        this.mDataListRycleView = horizontalList;
        mContaxt = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.document_swipe_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final JSONObject object = mDataListRycleView.get(position);

        holder.relMain.setVisibility(View.GONE);
        holder.mRelLayout.setVisibility(View.VISIBLE);
        holder.tvName.setText(object.optString("DocName"));
        holder.tvDescription.setText(object.optString("DocDescription"));
        try {
            String path = object.optString("DocPath");
          //  path = path.replace("co.in", "in");
            if (path != null && !path.equals("")) {
                Picasso.get().load(path).placeholder(R.mipmap.knowledge_area)
                        .error(R.mipmap.card_blank).into(holder.ivImage);
            }
        } catch (Exception e) {

        }
        holder.tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContaxt, WebViewActivity.class);
                intent.putExtra("title", "Quick Read");
                intent.putExtra("url", object.optString("URLLink"));
                mContaxt.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataListRycleView.size();

    }

    public void updateList(List<JSONObject> list) {
        mDataListRycleView.clear();
        mDataListRycleView.addAll(list);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvDescription, tvMore;
        public ImageView ivImage;
        private LinearLayout mRelLayout;
        private RelativeLayout relMain;


        public MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.textView2);
            tvDescription = view.findViewById(R.id.textView3);
            ivImage = view.findViewById(R.id.ivImage2);
            mRelLayout = view.findViewById(R.id.linerAllDocuments);
            relMain = view.findViewById(R.id.relMain);
            tvMore = view.findViewById(R.id.textView5);
        }
    }
}