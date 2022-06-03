package investwell.common.basic

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError
import com.iw.acceleratordemo.R
import investwell.utils.AppSession
import investwell.utils.Utils

open class BaseActivity : AppCompatActivity() {
    private  var appType = ""
    private var mSession: AppSession? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSession = AppSession.getInstance(this@BaseActivity)
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType"))) {
            appType = Utils.getConfigData(mSession).optString("APPType")
        }
        setAppTheme(appType)
    }

    private fun setAppTheme(theme: String) {
        if (theme.equals("TYPE 1D", ignoreCase = true)) {
            setTheme(R.style.Theme_App_DarkAppTheme)
        } else {
            setTheme(R.style.Theme_App_LightAppTheme)
        }
    }
}