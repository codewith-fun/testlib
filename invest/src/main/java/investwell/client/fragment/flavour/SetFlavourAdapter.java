package investwell.client.fragment.flavour;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import investwell.client.activity.MainActivity;
import investwell.utils.AppSession;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SetFlavourAdapter extends RecyclerView.Adapter<SetFlavourAdapter.MyViewHolder> {
    public ArrayList<JSONObject> jsonObject;
    private Context mcontext;
    private AppSession mSession;
    private MainActivity mActivity;
    private OnItemClickListener listener;

    public SetFlavourAdapter(Context context, ArrayList<JSONObject> jsonObject, OnItemClickListener listener) {

        this.mcontext = context;
        this.jsonObject = jsonObject;
        this.listener = listener;

    }

    @Override
    public SetFlavourAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        SetFlavourAdapter.MyViewHolder vh = new SetFlavourAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.setItem(position, listener);
        final JSONObject jsonObject1 = jsonObject.get(position);
        final JSONObject jsonObject = new JSONObject();
        mActivity = (MainActivity)mcontext;
        mSession = AppSession.getInstance(mcontext);
        holder.name.setText(jsonObject1.optString("SchemeName"));
    }

    @Override
    public int getItemCount() {
        return jsonObject.size();
    }

    public void updateList(List<JSONObject> list) {
        jsonObject.clear();
        jsonObject.addAll(list);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, cat;
        LinearLayout ly_item;




        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            cat = view.findViewById(R.id.name_cat);
            ly_item = view.findViewById(R.id.search_item);

        }


        public void setItem(final int position, final OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(position);
                }
            });


        }
    }
}

