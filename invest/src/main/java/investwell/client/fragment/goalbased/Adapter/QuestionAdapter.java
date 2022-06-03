package investwell.client.fragment.goalbased.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.goalbased.Fragment.FragQuestions;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyViewHolder> {

    public HashMap<Integer, Integer> mHashMapForMarks;
    public ArrayList<JSONObject> jsonObject;
    Context context;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private FragQuestions mFrag;

    public QuestionAdapter(Context context, ArrayList<JSONObject> jsonObject, FragQuestions frag) {
        mFrag = frag;
        this.context = context;
        this.jsonObject = jsonObject;
        mHashMapForMarks = new HashMap<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_list_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        mActivity = (MainActivity) context;
        final JSONObject jsonObject1 = jsonObject.get(position);
        holder.question.setText(jsonObject1.optString("Question"));
        final JSONArray jsonArray = jsonObject1.optJSONArray("QuestionOptionList");

        final RadioGroup ll = new RadioGroup(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        holder.mRadioGroup.removeAllViews();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject2 = jsonArray.optJSONObject(i);
            RadioButton rdbtn = new RadioButton(context);
            rdbtn.setId(jsonObject2.optInt("OptionID"));
            rdbtn.setText(jsonObject2.optString("Option"));
            ll.addView(rdbtn);

        }
        ll.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                int id = jsonObject.optInt("OptionID");
                if (id == checkedId){
                    mHashMapForMarks.put(position, jsonObject.optInt("Marks"));
                }

            }

        });
        holder.mRadioGroup.addView(ll);
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView question;
        private RecyclerView checkbox_recycle;
        private RadioGroup mRadioGroup;


        public MyViewHolder(View view) {
            super(view);

            question = view.findViewById(R.id.question);
            mRadioGroup = view.findViewById(R.id.radiogroup);

        }
    }


}

