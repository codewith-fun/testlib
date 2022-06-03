package investwell.client.fragment.schemes;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iw.acceleratordemo.R;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;


public class FragEmptyCart extends Fragment {
    private MainActivity mActivity;
    private AppApplication mApplication;
    private AppSession mSession;
    private ToolbarFragment toolbarFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_empty_cart, container, false);

        mActivity.setMainVisibility(this, null);
        mApplication = (AppApplication) mActivity.getApplication();
        setUpToolBar();
        view.findViewById(R.id.scheme_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(mActivity, TopSchemeFragment.class));

                Bundle bundle1 = new Bundle();

                mActivity.displayViewOther(41, bundle1);
            }
        });



        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(getResources().getString(R.string.toolBar_title_my_cart), true, false, false, false,false,false,false,"");
        }
    }

}
