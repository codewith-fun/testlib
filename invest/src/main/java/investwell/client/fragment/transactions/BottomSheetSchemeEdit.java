package investwell.client.fragment.transactions;

import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iw.acceleratordemo.R;

public class BottomSheetSchemeEdit extends BottomSheetDialogFragment {
    private View view;
    public BottomSheetSchemeEdit() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view=inflater.inflate(R.layout.layout_bottomsheet_scheme_edit, container, false);
        return view;
    }
}