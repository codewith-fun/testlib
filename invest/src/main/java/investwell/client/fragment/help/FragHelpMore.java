package investwell.client.fragment.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.iw.acceleratordemo.R;

import investwell.client.activity.MainActivity;
import investwell.client.activity.WebViewActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.Utils;


public class FragHelpMore extends Fragment implements View.OnClickListener {
    private AppSession mSession;
    private String toc = "", privacyPolicy = "", aboutUs = "", testimonial = "";
    private MainActivity mainActivity;
    public ToolbarFragment fragToolBar;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mainActivity);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_help_more, container, false);
        mSession = AppSession.getInstance(getActivity());
        view.findViewById(R.id.cvPrivacy).setOnClickListener(this);
        view.findViewById(R.id.cvTerm).setOnClickListener(this);
        view.findViewById(R.id.cvDesclaimers).setOnClickListener(this);
        view.findViewById(R.id.cv_about_us).setOnClickListener(this);
        view.findViewById(R.id.cvEnquiry).setOnClickListener(this);
        setUpUiVisibility(view);
        return view;
    }



    private void setUpUiVisibility(View view) {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("TermsCondition"))) {
            toc = Utils.getConfigData(mSession).optString("TermsCondition");
            view.findViewById(R.id.cvTerm).setVisibility(View.VISIBLE);
        } else {
            toc = "";
            view.findViewById(R.id.cvTerm).setVisibility(View.GONE);

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("PrivacyPolicy"))) {
            privacyPolicy = Utils.getConfigData(mSession).optString("PrivacyPolicy");
            view.findViewById(R.id.cvPrivacy).setVisibility(View.VISIBLE);
        } else {
            privacyPolicy = "";
            view.findViewById(R.id.cvPrivacy).setVisibility(View.GONE);

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("AboutUs"))) {
            aboutUs = Utils.getConfigData(mSession).optString("AboutUs");
            view.findViewById(R.id.cv_about_us).setVisibility(View.VISIBLE);
        } else {
            aboutUs = "";
            view.findViewById(R.id.cv_about_us).setVisibility(View.GONE);

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("Testimonials"))) {
            testimonial = Utils.getConfigData(mSession).optString("Testimonials");
            view.findViewById(R.id.cvDesclaimers).setVisibility(View.VISIBLE);
        } else {
            testimonial = "";
            view.findViewById(R.id.cvDesclaimers).setVisibility(View.GONE);

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("EnquiryFormRequired"))&&
                Utils.getConfigData(mSession).optString("EnquiryFormRequired").equalsIgnoreCase("Y")) {
            view.findViewById(R.id.cvEnquiry).setVisibility(View.VISIBLE);

        } else {

            view.findViewById(R.id.cvEnquiry).setVisibility(View.GONE);

        }
    }

    @Override
    public void onClick(View v) {
        Intent mIntent;
        int id = v.getId();
        if (id == R.id.cvPrivacy) {
            mIntent = new Intent(getActivity(), WebViewActivity.class);
            mIntent.putExtra("title", "Privacy Policy");
            mIntent.putExtra("url", privacyPolicy);
            startActivity(mIntent);
        } else if (id == R.id.cvTerm) {
            mIntent = new Intent(getActivity(), WebViewActivity.class);
            mIntent.putExtra("title", "Terms of Use");
            mIntent.putExtra("url", toc);
            startActivity(mIntent);
        } else if (id == R.id.cvDesclaimers) {
            mIntent = new Intent(getActivity(), WebViewActivity.class);
            mIntent.putExtra("title", "Testimonial");
            mIntent.putExtra("url", testimonial);
            startActivity(mIntent);
        } else if (id == R.id.cv_about_us) {
            Intent intent = new Intent(getActivity(), WebViewActivity.class);
            intent.putExtra("title", "About Us");
            intent.putExtra("url", aboutUs);
            startActivity(intent);
        } else if (id == R.id.cvEnquiry) {
            mainActivity.displayViewOther(99, null);
        }
    }
}
