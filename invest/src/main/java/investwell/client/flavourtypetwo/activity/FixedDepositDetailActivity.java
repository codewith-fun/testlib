package investwell.client.flavourtypetwo.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.flavourtypetwo.adapter.FixedDepositDetailAdapter;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppSession;

public class FixedDepositDetailActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView rvFixedDepositDetail;
    private Bundle bundle;
    private FixedDepositDetailAdapter fixedDepositDetailAdapter;

    private ImageView ivToolBarBackIcon;

    private CardView cv_dashboard_noData;
    private AppSession mSession;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_deposit_detail);
        initializer();
        setFixedDepositDetailAdapter();
        getDataFormBundle();
        setListeners();

    }
    //Error Content Initializer
    private void errorContentInitializer(View view) {
        viewNoData = view.findViewById(R.id.content_no_data);
        tvErrorMessage = viewNoData.findViewById(R.id.tv_error_message);
        ivErrorImage = viewNoData.findViewById(R.id.iv_error_image);
    }

    //Display Connection TimeOut Content
    private void displayConnectionTimeOut() {
        viewNoData.setVisibility(View.VISIBLE);
        ivErrorImage.setImageResource(R.drawable.bg_no_interent);
        tvErrorMessage.setText(R.string.error_connection_timeout);
    }

    //Display Server Error Content
    private void displayServerErrorMessage(VolleyError error) {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_data_found);
        tvErrorMessage.setText(error.getLocalizedMessage());
    }
    //Display Server Error Content
    private void displayServerMessage(String error) {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_data_found);
        tvErrorMessage.setText(error);
    }
    //Display Network Error Content
    private void displayNoInternetMessage() {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_interent);
        tvErrorMessage.setText(R.string.no_internet);
    }
    private void initializer() {
        mSession = AppSession.getInstance(FixedDepositDetailActivity.this);
        bundle = getIntent().getExtras();
        ivToolBarBackIcon = findViewById(R.id.iv_back_my_assets);
        cv_dashboard_noData = findViewById(R.id.cv_dashboard_noData);
        rvFixedDepositDetail = findViewById(R.id.rv_fixed_deposit_detail);
    }

    private void getDataFormBundle() {
        if (bundle != null && bundle.containsKey("JsonData")) {
            cv_dashboard_noData.setVisibility(View.GONE);
            try {
                JSONArray jsonArray = new JSONArray(bundle.getString("JsonData"));
                setFixedDepositDetailRecyclerData(jsonArray);
            } catch (Exception e) {
                e.printStackTrace();
                cv_dashboard_noData.setVisibility(View.VISIBLE);
            }
        }else {
            cv_dashboard_noData.setVisibility(View.VISIBLE);
        }
    }

    private void setFixedDepositDetailAdapter() {
        fixedDepositDetailAdapter = new FixedDepositDetailAdapter(FixedDepositDetailActivity.this, new ArrayList<JSONObject>());
        rvFixedDepositDetail.setHasFixedSize(true);
        rvFixedDepositDetail.setLayoutManager(new LinearLayoutManager(FixedDepositDetailActivity.this,LinearLayoutManager.VERTICAL,false));
        rvFixedDepositDetail.setAdapter(fixedDepositDetailAdapter);

    }

    private void setFixedDepositDetailRecyclerData(JSONArray response) {
        ArrayList<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            JSONObject jsonObject = response.optJSONObject(i);
            list.add(jsonObject);
        }
        fixedDepositDetailAdapter.updateList(list);
    }

    private void setListeners() {
        ivToolBarBackIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back_my_assets) {
            onBackPressed();
        }
    }
}
