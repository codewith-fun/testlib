package investwell.client.flavourtypetwo.activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.iw.acceleratordemo.R;
import investwell.client.activity.MainActivity;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppSession;


public class EnquiryItemActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvEnquiryTitle, tvEnquiryDesc, tvToolBarTitle, tvNothing;
    private ImageView iv_back_my_assets, ivEnquiry;
    private Button btnContinueEnquiry;
    private Bundle bundle;
    private AppSession mSession;
    private String imagePath = "", title = "", description = "";
    private Intent intent;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enqiry_item);
        initializer();
        getDataFromBundle();
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
    private void getDataFromBundle() {
        if (bundle != null) {
            if (bundle.containsKey("title")) {
                title = bundle.getString("title");
            }
            if (bundle.containsKey("description")) {
                description = bundle.getString("description");
            }
            if (bundle.containsKey("image")) {
                imagePath = bundle.getString("image");
            }
        } else {
            imagePath = "";
            title = "";
            description = "";
            tvToolBarTitle.setVisibility(View.GONE);
            tvEnquiryTitle.setVisibility(View.GONE);
            tvEnquiryDesc.setVisibility(View.GONE);
            ivEnquiry.setVisibility(View.GONE);
            btnContinueEnquiry.setVisibility(View.GONE);
            tvNothing.setVisibility(View.VISIBLE);
        }
        setUpUi();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void initializer() {
        mSession = AppSession.getInstance(EnquiryItemActivity.this);
        bundle = getIntent().getExtras();
        intent = new Intent();
        tvEnquiryTitle = findViewById(R.id.tv_enquiry_title);
        tvToolBarTitle = findViewById(R.id.tv_toolbar_title);
        tvEnquiryDesc = findViewById(R.id.tv_enquiry_desc);
        btnContinueEnquiry = findViewById(R.id.btn_eqnuiry);
        iv_back_my_assets = findViewById(R.id.iv_back_my_assets);
        tvNothing = findViewById(R.id.tvNothing);
        ivEnquiry = findViewById(R.id.iv_enquiry);
    }

    private void setListeners() {
        iv_back_my_assets.setOnClickListener(this);
        btnContinueEnquiry.setOnClickListener(this);
    }

    private void onEnquiryBtnClick() {
      Intent  intent = new Intent(EnquiryItemActivity.this, MainActivity.class);
        intent.putExtra("Flavour", "TYPE 2");
        intent.putExtra("position", "99");
        intent.putExtra("titleEnquiry",title);
        startActivity(intent);
    }

    private void setUpUi() {
        tvEnquiryTitle.setText(title);
        tvEnquiryDesc.setText(description);
        tvToolBarTitle.setText(title);
        Picasso.get().load(imagePath).error(R.mipmap.profileplaceholder).into(ivEnquiry);
        tvToolBarTitle.setVisibility(View.VISIBLE);
        tvEnquiryTitle.setVisibility(View.VISIBLE);
        tvEnquiryDesc.setVisibility(View.VISIBLE);
        ivEnquiry.setVisibility(View.VISIBLE);
        btnContinueEnquiry.setVisibility(View.VISIBLE);
        tvNothing.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back_my_assets) {
            super.onBackPressed();
        } else if (id == R.id.btn_eqnuiry) {
            onEnquiryBtnClick();
        }
    }
}
