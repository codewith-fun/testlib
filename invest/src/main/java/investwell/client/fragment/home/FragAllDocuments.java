package investwell.client.fragment.home;

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

import investwell.client.activity.MainActivity;
import investwell.client.adapter.DocumentAllAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;


public class FragAllDocuments extends Fragment implements ToolbarFragment.ToolbarCallback, View.OnClickListener {
    private AppSession mSession;
    private DocumentAllAdapter mAdapter;
    private MainActivity mActivity;
    private String mType = "";
    private Bundle mBundle;
    private ToolbarFragment fragToolBar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_all_documents, container, false);


        mBundle = getArguments();
        if (mBundle != null && mBundle.containsKey("type")) {
            mType = mBundle.getString("type");
        }

        mSession = AppSession.getInstance(getActivity());

        setUpToolBar();
        RecyclerView recyclerView = view.findViewById(R.id.rv_cart_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager_singles = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager_singles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new DocumentAllAdapter(getActivity(), new ArrayList<JSONObject>());
        recyclerView.setAdapter(mAdapter);

        updateDocumentdata();
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolbar_title_quick_read), true, false, false, false, false, false, false, "");
            fragToolBar.setCallback(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    public void updateDocumentdata() {
        List<JSONObject> documentList = new ArrayList<>();
        try {
            if (mType.equals("singleView")) {
                JSONObject jsonObject = new JSONObject(mBundle.getString("data"));
                documentList.add(jsonObject);
            } else {
                JSONObject jsonObject = new JSONObject(mSession.getDocumentData());
                JSONArray jsonArray = jsonObject.optJSONArray("DocumentList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    if (!jsonObject1.toString().contains(".pdf"))
                        documentList.add(jsonObject1);
                }
            }
            mAdapter.updateList(documentList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onToolbarItemClick(View view) {

    }
}


