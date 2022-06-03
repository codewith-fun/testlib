package investwell.client.fragment.goalmodulev1;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.GoalAdapterOldVersion;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoalVersonOneFragment extends Fragment implements View.OnClickListener, ToolbarFragment.ToolbarCallback {

    private ProgressDialog mBar;
    private AppApplication mAppApl;
    private GoalAdapterOldVersion mAdapter;
    private AppSession mSession;
    private MainActivity mActivity;
    private String mType = "";
    private TextView mTvNothing;
    private String mUCC_Code = "";
    private ToolbarFragment toolbarFragment;
    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mAppApl = (AppApplication) mActivity.getApplication();
            mSession = AppSession.getInstance(mActivity);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal_verson_one, container, false);


        mActivity.setMainVisibility(this, null);

        setUpToolBar();
        RecyclerView recycleView = view.findViewById(R.id.recycleView);
        mTvNothing = view.findViewById(R.id.tvNothing);
        recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        mAdapter = new GoalAdapterOldVersion(getActivity(), new ArrayList<JSONObject>(), new GoalAdapterOldVersion.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                JSONObject jsonObject = mAdapter.mDataList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("data", jsonObject.toString());
                mActivity.displayViewOther(108, bundle);
            }
        });
        recycleView.setAdapter(mAdapter);


        getGoalSummary();
        return view;
    }

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(mSession.getGoal(), true, false, false, false, false, false, false, getResources().getString(R.string.goal_summary_btn_txt));
            toolbarFragment.setCallback(this);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivLeft) {
        }
    }

    private void getGoalSummary() {


        try {

//        JSONObject object = mAppApl.getGoalSummery();
            JSONObject object = new JSONObject(mSession.getGoalData());
            List<JSONObject> goalSummaryList = new ArrayList<>();
            if (object.optBoolean("Status")) {
                JSONArray araArray = object.getJSONArray("GoalReportDetail");
                for (int i = 0; i < araArray.length(); i++) {
                    JSONObject jobject = araArray.getJSONObject(i);
                    goalSummaryList.add(jobject);
                }
                mAdapter.updateList(goalSummaryList);
                // showMedateListDailog();
            } else {
                Toast.makeText(getActivity(), object.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onToolbarItemClick(View view) {

    }
}
