package investwell.client.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.iw.acceleratordemo.R
import investwell.common.basic.BaseActivity
import investwell.utils.*
import kotlinx.android.synthetic.main.activity_initiaite_email_verify.*
import kotlinx.android.synthetic.main.activity_otp_verification.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class OTPVerificationEmailActivity : BaseActivity(), View.OnClickListener {
    private var mSession: AppSession? = null
    private val verificationId: String? = null
    private var mTvEmail: TextView? = null
    private var mPhoneNumber: String? = ""
    private var mFullName: String? = ""
    private var mEmailAddress: String? = ""
    private var mBundle: Bundle? = null
    private val mCode = ""
    private var VerificationCode: String? = null
    private var mApplication: AppApplication? = null
    private val ivBack: ImageView? = null
    private val viewNoData: View? = null
    private val ivErrorImage: ImageView? = null
    private val tvErrorMessage: TextView? = null
    public override fun onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy()
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(tokenReceiver);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_otp_verification)
        if (!Utils.isTablet(this)) requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mSession = AppSession.getInstance(this)
        mApplication = application as AppApplication
        val intent = intent
        if (intent != null) {
            mBundle = intent.extras
            if (mBundle != null) {
                mPhoneNumber = mBundle!!.getString("phone")
                mFullName = mBundle!!.getString("fullname")
                mEmailAddress = mBundle!!.getString("email")
                VerificationCode = mBundle!!.getString("code")
                mSession?.setEmail(mEmailAddress)
            }
        }
        findViewById<View>(R.id.btn_verify_sms).setOnClickListener(this)
        findViewById<View>(R.id.imageView3).setOnClickListener(this)
        findViewById<View>(R.id.tv_sms_resend).setOnClickListener(this)
        mTvEmail = findViewById(R.id.tv_header_sms)
        editTextCodeEmail.requestFocus()
        val primaryMsg = resources.getString(R.string.otp_verification_email_desc_txt) + "  "
        val html = "$primaryMsg<b>$mEmailAddress</b>"
        tv_email_desc.text = Html.fromHtml(html)
        FCMToken()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_verify_sms -> {
                val code = editTextCodeEmail!!.text.toString().trim { it <= ' ' }
                if (code.isEmpty() || code.length < 6) {
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                    mApplication!!.showSnackBar(editTextCodeEmail, resources.getString(R.string.otp_verification_email_empty_error))
                } else if (code == VerificationCode) {
                    BasicRegister(mBundle!!.getString("email"))
                } else {
                    mApplication!!.showSnackBar(editTextCodeEmail, resources.getString(R.string.otp_verification_email_invalid_error))
                }
            }
            R.id.tv_sms_resend -> {
                editTextCodeEmail!!.clearText()
                sendEmailOTP()
            }
            R.id.imageView3 -> finish()
        }
    }
    private fun sendEmailOTP() {
        avi_email_otp_resend.smoothToShow()
        tv_sms_resend.visibility=View.GONE
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
                    avi_email_otp_resend.hide()
                    tv_sms_resend.visibility=View.VISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    VerificationCode = objDa.optString("VerificationCode")

                } else {
                    avi_email_otp_resend.hide()
                    tv_sms_resend.visibility=View.VISIBLE
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
    private fun BasicRegister(email: String?) {
        avi_email_verify.smoothToShow()
        btn_verify_sms.text=""
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val url = Config.SAVE_BASIC_DETAILS
        val params: MutableMap<String?, String?> = HashMap()
        try {
            params["EmailID"] = email
            params["Bid"] = AppConstants.APP_BID
            params["Mobile"] = mBundle!!.getString("phone")
            params["Name"] = mBundle!!.getString("fullname")
            params["PAN"] = ""
            params["Passkey"] = mSession!!.passKey
            params["KYCStatus"] = ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, JSONObject(params as Map<*, *>), { `object` ->
            try {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                if (`object`.optString("Status") == "True") {
                    mSession!!.mobileNumber = mBundle!!.getString("phone")
                    mSession!!.email = email
                    doREgistered()
                } else {
                    avi_email_verify.hide()
                    btn_verify_sms.text = getString(R.string.verify)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    mApplication!!.showSnackBar(editTextCodeEmail, `object`.optString("ServiceMSG"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }) { volleyError: VolleyError ->
            avi_email_verify.hide()
            btn_verify_sms.text = getString(R.string.verify)
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                val error = VolleyError(String(volleyError.networkResponse.data))
                try {
                    val jsonObject = JSONObject(error.message.toString())
                    mApplication!!.showSnackBar(editTextCodeEmail, jsonObject.optString("error"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else if (volleyError is NoConnectionError) mApplication!!.showSnackBar(editTextCodeEmail, resources.getString(R.string.no_internet))
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
        val requestQueue = Volley.newRequestQueue(this@OTPVerificationEmailActivity)
        requestQueue.add(jsonObjectRequest)
    }

    private fun doREgistered() {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, "http://notificationv2.investwell.in/public/user/register",
                Response.Listener { response: String? ->
                    avi_email_verify.hide()
                    btn_verify_sms.text = getString(R.string.verify)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    try {
                        val jsonObject = JSONObject(response)
                        if (jsonObject.optString("status") == "success") {
                            val intent = Intent(this@OTPVerificationEmailActivity, OtpSuccessActivity::class.java)
                            intent.putExtras(mBundle!!)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            avi_email_verify.hide()
                            btn_verify_sms.text = getString(R.string.verify)
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            mApplication!!.showSnackBar(editTextCodeEmail, resources.getString(R.string.error_try_again))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { volleyError: VolleyError ->
                    avi_email_verify.hide()
                    btn_verify_sms.text = getString(R.string.verify)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    if (volleyError.message != null) {
                        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                            val error = VolleyError(String(volleyError.networkResponse.data))
                            try {
                                val jsonObject = JSONObject(error.message)
                                mApplication!!.showSnackBar(editTextCodeEmail, jsonObject.optString("error"))
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } else if (volleyError is NoConnectionError) {
                        }
                    }
                }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                val tz = TimeZone.getDefault()
                val Timezon_id = tz.id
                params["gcm_id"] = mSession!!.fcmToken
                params["device_name"] = DeviceUtils.getDeviceName()
                params["device_model"] = DeviceUtils.getDeviceModel()
                params["device_os"] = DeviceUtils.getDeviceOS()
                params["device_api"] = DeviceUtils.getDeviceAPILevel()
                params["last_lat"] = ""
                params["last_long"] = ""
                params["device_memory"] = DeviceUtils.getDeviceMemory(this@OTPVerificationEmailActivity) + ""
                params["device_id"] = DeviceUtils.getDeviceId(this@OTPVerificationEmailActivity) + ""
                params["pin_code"] = ""
                params["timezone"] = Timezon_id
                params["email"] = mEmailAddress!!
                params["app_type"] = "Android"
                params["app_name"] = getString(R.string.app_name)
                params["user_name"] = mBundle!!.getString("fullname")!!
                params["user_mobile_no"] = mBundle!!.getString("phone")!!
                params["user_type"] = mSession!!.userType
                params["iw_client_id"] = mSession!!.cid
                params["iw_bid"] = AppConstants.APP_BID

                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("fcmServerToken"))) {
                    params["iw_bid_api_key"] = Utils.getConfigData(mSession).optString("fcmServerToken")
                } else {
                    params["iw_bid_api_key"] = ""
                }
                return params
            }
        }
        stringRequest.retryPolicy = object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        }
        val requestQueue = Volley.newRequestQueue(this@OTPVerificationEmailActivity)
        requestQueue.add(stringRequest)
    }

    private fun FCMToken() {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        // Toast.makeText(getBaseContext(), "ERROR", Toast.LENGTH_SHORT).show();
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result!!.token
                    mSession!!.fcmToken = token
                    //  Toast.makeText(getBaseContext(), ""+mSession.getFcmToken(), Toast.LENGTH_SHORT).show();
                })
    }
}