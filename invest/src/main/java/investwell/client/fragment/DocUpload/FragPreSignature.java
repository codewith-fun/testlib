package investwell.client.fragment.DocUpload;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.iw.acceleratordemo.R;

import investwell.client.activity.SignatureActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.Utils;


public class FragPreSignature extends Fragment implements View.OnClickListener {

    private Bundle mBundle;
    public ToolbarFragment fragToolBar;
    private AppSession mSession;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_pre_signature, container, false);
        mBundle = getArguments();
        mSession = AppSession.getInstance(getActivity());
        view.findViewById(R.id.tvOnScreen).setOnClickListener(this);
        view.findViewById(R.id.tvUpload).setOnClickListener(this);
        setUpToolBar();

        if (Utils.getConfigData(mSession).optString("SignatureOnScreen").equalsIgnoreCase("Y")){
            view.findViewById(R.id.tvOnScreen).setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.tvOnScreen).setVisibility(View.GONE);
        }
        return view;
    }


    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.incomplete_profile_signature_txt), true, false, false, false, false, false, false, "");


        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), SignatureActivity.class);
        try {
            intent.putExtra("ucc_code", mBundle.getString("ucc_code"));
            if (mBundle.containsKey("coming_from")){
                intent.putExtra("coming_from", mBundle.getString("coming_from"));
            }
            intent.putExtra("chequeRequired", Utils.getConfigData(mSession).optString("ChequeRequired"));
        }catch (Exception e){
            e.printStackTrace();
        }

        int id = view.getId();
        if (id == R.id.tvOnScreen) {
            intent.putExtra("onscreen", "true");
            startActivityForResult(intent, 100);
        } else if (id == R.id.tvUpload) {
            intent.putExtra("onscreen", "false");
            startActivityForResult(intent, 100);
        }
    }


}
