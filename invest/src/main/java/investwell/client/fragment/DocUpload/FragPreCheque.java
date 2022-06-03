package investwell.client.fragment.DocUpload;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.iw.acceleratordemo.R;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;



public class FragPreCheque extends Fragment implements View.OnClickListener {

    private MainActivity mActivity;
    public ToolbarFragment fragToolBar;
    private Bundle bundle;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_pre_cheque, container, false);
        mActivity = (MainActivity)getActivity();
        bundle = getArguments();
        view.findViewById(R.id.tvUpload).setOnClickListener(this);
        setUpToolBar();
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.investment_profile_upload_cheque_txt), true, false, false, false, false, false, false,"");

        }
    }

@Override
    public void onClick(View view){

    int id = view.getId();
    if (id == R.id.tvUpload) {
        mActivity.displayViewOther(54, bundle);
    } else if (id == R.id.back_arrow) {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}



}
