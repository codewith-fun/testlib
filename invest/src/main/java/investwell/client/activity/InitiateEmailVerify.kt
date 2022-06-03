package investwell.client.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.iw.acceleratordemo.R
import investwell.utils.AppConstants
import investwell.utils.AppSession
import investwell.utils.Config
import investwell.utils.Utils
import kotlinx.android.synthetic.main.activity_initiaite_email_verify.*
import org.json.JSONException
import org.json.JSONObject

class InitiateEmailVerify : AppCompatActivity() {
    private var mBundle: Bundle? = null
    private var mSession: AppSession? = null
    private var mPhoneNumber: String? = ""
    private var mFullName: String? = ""
    private var mEmailAddress: String? = ""
    private var VerificationCode: String? = null
    private var mApplication: AppApplication? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initiaite_email_verify)
        initializer()
        dataFromBundle
        setListener()
    }

    private fun initializer() {
        mApplication = application as AppApplication
        mSession = AppSession.getInstance(this)
        mBundle = Bundle()
    }

    private val dataFromBundle: Unit
        get() {
            val intent = intent
            if (intent != null) {
                mBundle = intent.extras
                if (mBundle != null) {
                    mPhoneNumber = mBundle!!.getString("phone")
                    mFullName = mBundle!!.getString("fullname")
                    mEmailAddress = mBundle!!.getString("email")
                }
                mSession!!.email = mEmailAddress
            }
        }

    private fun sendEmailOTP() {
        avi.smoothToShow()
        button5.text = ""
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        try {
            val jsonObject = JSONObject()
            jsonObject.put("Bid", AppConstants.APP_BID)
            jsonObject.put("Passkey", mSession!!.passKey)
            jsonObject.put("EmailID", mEmailAddress)
            val url = Config.EMAIL_OTP
            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObject, { objDa ->
                if (objDa.optString("Status").equals("True", ignoreCase = true)) {
                    avi.hide()
                    button5.text = getString(R.string.verify_email)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    VerificationCode = objDa.optString("VerificationCode")
                    val intent = Intent(this@InitiateEmailVerify, OTPVerificationEmailActivity::class.java)
                    mBundle?.putString("code", VerificationCode)
                    intent.putExtras(mBundle!!)
                    startActivity(intent)
                } else {
                    avi.hide()
                    button5.text = getString(R.string.verify_email)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                }
            }
            ) { volleyError: VolleyError ->
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    val error = VolleyError(String(volleyError.networkResponse.data))
                    try {
                        val jsonObject1 = JSONObject(error.message.toString())
                        mApplication?.showSnackBar(button5, jsonObject1.optString("error"))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else if (volleyError is NoConnectionError) mApplication?.showSnackBar(button5, resources.getString(R.string.no_internet))
            }
            jsonObjectRequest.retryPolicy = object : RetryPolicy {
                override fun getCurrentTimeout(): Int {
                    return 50000
                }

                override fun getCurrentRetryCount(): Int {
                    return 1
                }

                override fun retry(error: VolleyError) {}
            }
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(jsonObjectRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListener() {
        findViewById<View>(R.id.button5).setOnClickListener {
            if (mBundle != null) {
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("EmailVerification")) &&
                        Utils.getConfigData(mSession).optString("EmailVerification").equals("Y", ignoreCase = true)) {
                    sendEmailOTP()
                } else {
                    val intent = Intent(this@InitiateEmailVerify, OtpSuccessActivity::class.java)
                    intent.putExtras(mBundle!!)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

            } else {
                Toast.makeText(applicationContext, R.string.error_connection_timeout, Toast.LENGTH_SHORT).show()
            }
        }
        imageView7.setOnClickListener { finish() }
    }
}