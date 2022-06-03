@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package investwell.client.fragment.DocUpload

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.iw.acceleratordemo.R
import investwell.client.activity.AccountConfActivity
import investwell.client.activity.AppApplication
import investwell.client.activity.MainActivity
import investwell.client.fragment.others.ToolbarFragment
import investwell.utils.AppConstants
import investwell.utils.AppSession
import investwell.utils.Config
import kotlinx.android.synthetic.main.activity_doc_submit.*
import org.json.JSONException
import org.json.JSONObject

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FragDocumentDetailsList : Fragment(), View.OnClickListener {
    private var mSignFileName = ""
    private var mChequeFileName = ""
    private var mSession: AppSession? = null
    private lateinit var mBundle: Bundle
    private var mApplication: AppApplication? = null
    private var mUccIin = ""
    private var mActivity: MainActivity? = null
    private var fragToolBar: ToolbarFragment? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            this.mActivity = context
            mSession = AppSession.getInstance(mActivity)
            mApplication = mActivity!!.application as AppApplication
        }
    }

    override fun onStart() {
        super.onStart()
        getDocumentsDetail()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_doc_submit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()

        getDataFromBundle()

        iv_upload_sign.setOnClickListener(this)
        iv_cheque_upload.setOnClickListener(this)
        btn_submit.setOnClickListener(this)

    }


    private fun setUpToolBar() {
        fragToolBar = childFragmentManager.findFragmentById(R.id.frag_toolBar) as ToolbarFragment?
        if (fragToolBar != null) {
            fragToolBar!!.setUpToolBar(resources.getString(R.string.documents), true, false, false, false, false, false, false, "")
        }
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.iv_upload_sign -> {
                mActivity!!.displayViewOther(93, mBundle)
            }

            R.id.iv_cheque_upload -> {
                mActivity!!.displayViewOther(94, mBundle)
            }

            R.id.btn_submit -> {

                if (mSignFileName.equals("NA", true)) {
                    Toast.makeText(mActivity, "Please Upload Signature", Toast.LENGTH_LONG).show()
                } else if (mChequeFileName.equals("NA", true)) {
                    Toast.makeText(mActivity, "Please Upload cheques", Toast.LENGTH_LONG).show()
                } else {
                    saveFileNameBoth()
                }

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getDataFromBundle() {
        mBundle = requireArguments()
        mUccIin = mBundle.getString("ucc_code")!!

        if (mBundle.containsKey("signUploaded")) {
            mSignFileName = mBundle.getString("File1")!!
            if (!TextUtils.isEmpty(mSignFileName)) {
                tv_signature.text = "Signature Uploaded Successfully"
                imageView10.setImageResource(R.drawable.check_green)
                iv_upload_sign.setImageResource(R.drawable.reload)
            } else {
                tv_signature.text = "Add Your Signature"
                imageView10.setImageResource(R.drawable.check_gray)
                iv_upload_sign.setImageResource(R.drawable.upload)
            }
        }

    }

    private fun getDocumentsDetail() {
        val mBar = ProgressDialog.show(activity, null, null, true, false)
        mBar.setContentView(R.layout.progress_piggy)
        mBar.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val url = Config.Document_Upload_Detials
        val jsonObject = JSONObject()
        try {
            jsonObject.put(AppConstants.PASSKEY, mSession!!.passKey)
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID)
            jsonObject.put("IINUCC", mUccIin)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObject, { `object` ->
            mBar.dismiss()
            try {
                if (`object`.optString("Status") == "True") {
                    var jsonData : JSONObject = `object`.optJSONArray("ResponseData").get(0) as JSONObject
                    mSignFileName = `jsonData`.optString("SignatureFileName")
                    mChequeFileName = `jsonData`.optString("ChequeFileName")

                    if (!mSignFileName.equals("NA", true) && mSignFileName.length>0 ) {
                        tv_signature.text = "Signature Uploaded Successfully"
                        imageView10.setImageResource(R.drawable.check_green)
                        iv_upload_sign.setImageResource(R.drawable.reload)
                    } else {
                        tv_signature.text = "Add Your Signature"
                        imageView10.setImageResource(R.drawable.check_gray)
                        iv_upload_sign.setImageResource(R.drawable.upload)
                    }

                    if (!mChequeFileName.equals("NA", true) && mChequeFileName.length>0 ) {
                        tv_cheque.text = "Cheque Uploaded Successfully"
                        imageView11.setImageResource(R.drawable.check_green)
                        iv_cheque_upload.setImageResource(R.drawable.reload)
                    } else {
                        tv_cheque.text = "Add Bank Cheque"
                        imageView11.setImageResource(R.drawable.check_gray)
                        iv_cheque_upload.setImageResource(R.drawable.upload)
                    }



                } else {
                    Toast.makeText(mActivity, `object`.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                    // mApplication.showSnackBar(mImageView, `object`.optString("ServiceMSG"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }) { volleyError ->
            mBar.dismiss()
            if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                val error = VolleyError(String(volleyError.networkResponse.data))
                try {
                    val jsonObject = JSONObject(error.message)
                    Toast.makeText(mActivity, jsonObject.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();

                    //mApplication.showSnackBar(mImageView, jsonObject.optString("error"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else if (volleyError is NoConnectionError) {
                Toast.makeText(mActivity, resources.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

                //mApplication.showSnackBar(mImageView, resources.getString(R.string.no_internet))

            }
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
        val requestQueue = Volley.newRequestQueue(mActivity)
        requestQueue.add(jsonObjectRequest)
    }

    private fun saveFileNameBoth() {
        val mBar = ProgressDialog.show(activity, null, null, true, false)
        mBar.setContentView(R.layout.progress_piggy)
        mBar.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val url = Config.SIGNATURE_UPLOAD_2
        val jsonObject = JSONObject()
        try {
            jsonObject.put("Bid", AppConstants.APP_BID)
            jsonObject.put("UCC", mUccIin)
            jsonObject.put("FileName", mSignFileName)
            jsonObject.put("Passkey", mSession!!.passKey)
            jsonObject.put("OnlineOption", mSession!!.appType)
            jsonObject.put("ChequeFileName", mChequeFileName)
            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObject, { response ->
                mBar.dismiss()
                if (response.optString("Status").equals("True", ignoreCase = true)) {
                    if (mBundle != null) {
                        val intent = Intent(activity, AccountConfActivity::class.java)
                        intent.putExtra("coming_from", "mComingFrom")
                        startActivity(Intent(activity, AccountConfActivity::class.java))
                    } else {
                        startActivity(Intent(activity, AccountConfActivity::class.java))
                    }
                } else {
                    mApplication?.showCommonDailog(mActivity, activity, false, "Server Response", response.optString("ServiceMSG"), "message", false, true)
                }
            }) { error: VolleyError? -> mBar.dismiss() }
            jsonObjectRequest.retryPolicy = object : RetryPolicy {
                override fun getCurrentTimeout(): Int {
                    return 50000
                }

                override fun getCurrentRetryCount(): Int {
                    return 1
                }

                override fun retry(error: VolleyError) {}
            }
            val requestQueue = Volley.newRequestQueue(activity)
            requestQueue.add(jsonObjectRequest)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


}