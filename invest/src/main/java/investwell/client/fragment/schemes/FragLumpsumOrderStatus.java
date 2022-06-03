package investwell.client.fragment.schemes;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.FragLumsumOrderStatusAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.customView.CustomButton;

public class FragLumpsumOrderStatus extends Fragment implements View.OnClickListener , ToolbarFragment.ToolbarCallback{

    RecyclerView lumpsum_recycle;
    boolean mIsAnySuccess = false;
    private FragLumsumOrderStatusAdapter mAdapter;
    private AppSession mSession;
    private ArrayList<JSONObject> mOrderList;
    private CustomButton mTvEdit, mTvPay;
    private String mIsFirstTimePayment = "false";
    private MainActivity mActivity;
    private AppApplication mApplication;
    private String mUCC_Code = "";
    private ToolbarFragment toolbarFragment;
    private View view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_lumsum_order_status, container, false);

        setInitializer();

        setUpToolBar();
        setRecyclerAdapter();
        getDataFromBundle();
        setListener();

        return view;
    }

    private void setInitializer() {
        mSession = AppSession.getInstance(getActivity());
        mActivity.setMainVisibility(this, null);
        mApplication = (AppApplication) mActivity.getApplication();
        lumpsum_recycle = view.findViewById(R.id.lumpsum_recycle);
        mTvPay = view.findViewById(R.id.btn_pay_now);
        mTvEdit = view.findViewById(R.id.btn_edit);
    }
    @Override
    public void onToolbarItemClick(View view) {
        if(view.getId()==R.id.btn_add_new){
            mActivity.displayViewOther(0,null);
        }
    }

    private void setUpToolBar() {
        toolbarFragment
                = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment
                != null) {
            toolbarFragment
                    .setUpToolBar(getResources().getString(R.string.toolbar_title_rder_status_txt), true, false, false, true, false, false, false,  "home");
            toolbarFragment.setCallback(this);
        }
    }


    private void getDataFromBundle() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("ucc_code")) {
            mUCC_Code = bundle.getString("ucc_code");
        }


        if (bundle != null && bundle.containsKey("data")) {
            try {
                JSONArray jsonArray = new JSONArray(bundle.getString("data"));
                List<JSONObject> list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (!jsonObject.optString("Remark").contains("FAILED")) {
                        mIsAnySuccess = true;
                    } else {
                        mIsAnySuccess = false;
                    }

                    list.add(jsonObject);
                    if (!list.get(i).optString("Remark").contains("FAILED")) {
                        mIsAnySuccess = true;
                    } else {
                        mIsAnySuccess = false;
                    }
                }
                mAdapter.updateList(list);
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (bundle.containsKey("firstTimePayment")) {
                    mIsFirstTimePayment = bundle.getString("firstTimePayment");
                    if (mIsFirstTimePayment != null) {
                        if (mIsFirstTimePayment.equalsIgnoreCase("true") && mIsAnySuccess) {
                            mTvPay.setText(getResources().getString(R.string.lumpsum_order_status_pay_now));
                            mTvPay.setVisibility(View.VISIBLE);
                            mTvEdit.setVisibility(View.GONE);
                        } else if (mIsFirstTimePayment.equalsIgnoreCase("true") && !mIsAnySuccess) {
                            mTvPay.setText(getResources().getString(R.string.lumpsum_order_status_pay_now));
                            mTvPay.setVisibility(View.VISIBLE);
                            mTvEdit.setVisibility(View.VISIBLE);
                        } else if (!mIsFirstTimePayment.equalsIgnoreCase("true") && mIsAnySuccess) {
                            mTvPay.setText(getResources().getString(R.string.lumpsum_order_status_go_back));
                            mTvPay.setVisibility(View.GONE);
                            mTvEdit.setVisibility(View.GONE);
                        } else if (!mIsFirstTimePayment.equalsIgnoreCase("true") && !mIsAnySuccess) {
                            mTvPay.setText(getResources().getString(R.string.lumpsum_order_status_go_back));
                            mTvPay.setVisibility(View.GONE);
                            mTvEdit.setVisibility(View.VISIBLE);
                        }
                    }
                }else{
                    if ( mIsAnySuccess) {
                        mTvPay.setText(getResources().getString(R.string.lumpsum_order_status_pay_now));
                        mTvPay.setVisibility(View.VISIBLE);
                        mTvEdit.setVisibility(View.GONE);
                    }else{
                        mTvPay.setText(getResources().getString(R.string.lumpsum_order_status_pay_now));
                        mTvPay.setVisibility(View.VISIBLE);
                        mTvEdit.setVisibility(View.VISIBLE);
                    }
                }
                //                if (mIsAnySuccess) {
                //                    mTvPay.setVisibility(View.VISIBLE);
                //
                //                } else {
                //                    mTvPay.setVisibility(View.GONE);
                //                    mTvEdit.setVisibility(View.VISIBLE);
                //                }
                if (bundle.containsKey("order_type")) {
                    if (bundle.getString("order_type").equals("lumsum")) {
                        if (mIsAnySuccess) {
                            mTvPay.setText(getResources().getString(R.string.lumpsum_order_status_pay_now));
                        } else {
                            mTvPay.setText(getResources().getString(R.string.lumpsum_order_status_go_back));
                        }
                    } else {
                        if (mIsAnySuccess) {
                            mTvPay.setText(getResources().getString(R.string.lumpsum_order_status_pay_now));
                        } else {
                            mTvPay.setText(getResources().getString(R.string.lumpsum_order_status_go_back));
                        }
                    }
                } else {
                    if (mIsAnySuccess) {
                        mTvPay.setText(getResources().getString(R.string.lumpsum_order_status_pay_now));
                    } else {
                        mTvPay.setText(getResources().getString(R.string.lumpsum_order_status_go_back));
                    }
                }
            }
        }
    }

    private void setListener() {

        mTvPay.setOnClickListener(this);
        mTvEdit.setOnClickListener(this);

    }

    private void setRecyclerAdapter() {
        lumpsum_recycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        mAdapter = new FragLumsumOrderStatusAdapter(getActivity(), new ArrayList<JSONObject>(), new FragLumsumOrderStatusAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        lumpsum_recycle.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_edit) {
            mActivity.getSupportFragmentManager().popBackStack();
        } else if (id == R.id.btn_pay_now) {
            Bundle bundle1 = new Bundle();
            bundle1.putString("ucc_code", mUCC_Code);
            bundle1.putString("type", "call_from_cleint");
            mActivity.displayViewOther(83, bundle1);
        }
    }


}
