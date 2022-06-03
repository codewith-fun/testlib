package investwell.client.fragment.documentViewer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;

import androidx.fragment.app.Fragment;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;

public class FragDocViewer extends Fragment {
    private MainActivity mActivity;
    private AppSession mSession;
    private View view;
    private ToolbarFragment fragToolBar;
    private ImageView ivDocImage;
    private String filePath = "";

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
        view = inflater.inflate(R.layout.frag_client_doc_view, container, false);
        setInitializer();
        setUpToolBar();
        Bundle bundle = getArguments();
        if (bundle != null ) {
            filePath = bundle.getString("filePath");
        }
        ivDocImage = view.findViewById(R.id.ivDocImage);
        Picasso.get().load(filePath).placeholder(R.mipmap.tranparent).into(ivDocImage);
        return view;
    }
    private void setInitializer() {
        setHasOptionsMenu(true);
        mActivity.setMainVisibility(this, null);
        mSession = AppSession.getInstance(mActivity);
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar("Document View", true, false, false, false, false, false, false, "");
        }
    }

}


