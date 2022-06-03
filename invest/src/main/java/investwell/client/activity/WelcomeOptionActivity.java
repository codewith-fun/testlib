package investwell.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.iw.acceleratordemo.R;

import investwell.common.basic.BaseActivity;
import investwell.utils.AppSession;

public class WelcomeOptionActivity extends BaseActivity implements View.OnClickListener {
    private AppSession mSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_option);
        mSession = AppSession.getInstance(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        int id = view.getId();
        if (id == R.id.button2) {
            intent = new Intent(WelcomeOptionActivity.this, SignUpActivity.class);
            mSession.setHasFirstTimeAppIntroLaunched(true);
            startActivity(intent);
        } else if (id == R.id.button3) {
            intent = new Intent(WelcomeOptionActivity.this, LoginActivity.class);
            mSession.setHasFirstTimeAppIntroLaunched(true);
            startActivity(intent);
        }

    }
}