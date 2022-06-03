package investwell.common.applock.pinlock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.iw.acceleratordemo.R;

import investwell.utils.AppSession;


/**
 * Created by Aleksandr Nikiforov on 2018/02/07.
 * <p>
 * Lock Screen Fragment. Support pin code authorization and
 * fingerprint authorization for API 23 +.
 */
public class PFLockScreenFragment extends Fragment {

    private static final String TAG = PFLockScreenFragment.class.getName();
    private ImageView mFingerprintButton;
    private View mDeleteButton;
    private TextView mLeftButton;
    /* private TextView mNextButton;*/
    public PFCodeView mCodeView;
    private boolean mUseFingerPrint = true;
    private boolean mFingerprintHardwareDetected = false;
    private boolean mIsCreateMode = false;
    private OnPFLockScreenCodeCreateListener mCodeCreateListener;
    private OnPFLockScreenLoginListener mLoginListener;
    private String mEncodedPinCode = "";
    private PFFLockScreenConfiguration mConfiguration;
    private View mRootView;
    private PFFingerprintAuthListener mAuthListener;
    private String mFirstTimeEnterCode = "";
    private TextView mTitle;
    private String mType = "";
    private AppSession mSession;

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {

        super.onStart();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lock_screen_pf, container, false);
/*        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{ContextCompat.getColor(getActivity(), R.color.colorAccent),
                        ContextCompat.getColor(getActivity(), R.color.coffe),});

        view.findViewById(R.id.fragment_pf).setBackground(gradientDrawable);*/
        Bundle bundle = getArguments();
        mSession = AppSession.getInstance(getActivity());
        if (bundle != null && bundle.containsKey("type"))
            mType = bundle.getString("type");

        mFingerprintButton = view.findViewById(R.id.button_finger_print);
        mDeleteButton = view.findViewById(R.id.button_delete);
        mLeftButton = view.findViewById(R.id.button_left);
        /*  mNextButton = view.findViewById(R.id.button_next);*/
        mDeleteButton.setOnClickListener(mOnDeleteButtonClickListener);
        mDeleteButton.setOnLongClickListener(mOnDeleteButtonOnLongClickListener);
        mCodeView = view.findViewById(R.id.code_view);
        initKeyViews(view);
        mCodeView.setListener(mCodeListener);


        if (!mUseFingerPrint) {
            mFingerprintButton.setVisibility(View.GONE);
        }

        mRootView = view;
        applyConfiguration(mConfiguration);
        if (mType.equalsIgnoreCase("change_pin")) {

        } else if (AppSession.getInstance(getActivity()).getPIN().length() > 0) {
            mFingerprintButton.setVisibility(View.VISIBLE);
            startFingerPrint();
        } else {
            mFingerprintButton.setVisibility(View.GONE);
        }

        return view;
    }

    private void startFingerPrint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setAuthListener(new PFFingerprintAuthListener() {
                @Override
                public void onAuthenticated() {
                    if (mLoginListener != null) {
                        mLoginListener.onFingerprintSuccessful();
                    }

                }

                @Override
                public void onError() {

                    try {
                        if (mLoginListener != null && getActivity() != null) {
                            mLoginListener.onFingerprintLoginFailed();
                            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            if (v != null) {
                                v.vibrate(400);
                            }
                            //Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
                        }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }


    public void setConfiguration(PFFLockScreenConfiguration configuration) {
        this.mConfiguration = configuration;
        applyConfiguration(configuration);
    }

    private void applyConfiguration(PFFLockScreenConfiguration configuration) {
        if (mRootView == null || configuration == null) {
            return;
        }
        mTitle = mRootView.findViewById(R.id.title_text_view);
        mTitle.setText(configuration.getTitle());
        if (TextUtils.isEmpty(configuration.getLeftButton())) {
            mLeftButton.setVisibility(View.GONE);
        } else {
            mLeftButton.setText(configuration.getLeftButton());
            mLeftButton.setOnClickListener(configuration.getOnLeftButtonClickListener());
        }

      /*  if (!TextUtils.isEmpty(configuration.getNextButton())) {
            mNextButton.setText(configuration.getNextButton());
        }*/

        mUseFingerPrint = configuration.isUseFingerprint();
        if (!mUseFingerPrint) {
            mFingerprintButton.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.VISIBLE);
        }
        mIsCreateMode = mConfiguration.getMode() == PFFLockScreenConfiguration.MODE_CREATE;

        if (mIsCreateMode) {
            mLeftButton.setVisibility(View.GONE);
            mFingerprintButton.setVisibility(View.GONE);
        }

    /*    if (mIsCreateMode) {
            mNextButton.setOnClickListener(mOnNextButtonClickListener);
        } else {
            mNextButton.setOnClickListener(null);
        }*/
        mCodeView.setCodeLength(mConfiguration.getCodeLength());
    }

    private void initKeyViews(View parent) {
        parent.findViewById(R.id.button_0).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_1).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_2).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_3).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_4).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_5).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_6).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_7).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_8).setOnClickListener(mOnKeyClickListener);
        parent.findViewById(R.id.button_9).setOnClickListener(mOnKeyClickListener);
    }

    private View.OnClickListener mOnKeyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof TextView) {
                String string = ((TextView) v).getText().toString();
                if (string.length() != 1) {
                    return;
                }
                int codeLength = mCodeView.input(string);
                configureRightButton(codeLength);
            }
        }
    };

    private View.OnClickListener mOnDeleteButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int codeLength = mCodeView.delete();
            configureRightButton(codeLength);
        }
    };

    private View.OnLongClickListener mOnDeleteButtonOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            mCodeView.clearCode();
            configureRightButton(0);
            return true;
        }
    };


    public void setAuthListener(PFFingerprintAuthListener authListener) {
        mAuthListener = authListener;
    }



    private void showNoFingerprintDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.no_fingerprints_title_pf)
                .setMessage(R.string.no_fingerprints_message_pf)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel_pf, null)
                .setPositiveButton(R.string.settings_pf, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS));
                    }
                }).create().show();
    }

    private void configureRightButton(int codeLength) {
        if (mIsCreateMode) {
            if (codeLength > 0) {
                mDeleteButton.setVisibility(View.VISIBLE);
            } else {
                mDeleteButton.setVisibility(View.GONE);
            }
            return;
        }

        if (codeLength > 0) {
            mFingerprintButton.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.VISIBLE);
            mDeleteButton.setEnabled(true);
            return;
        }

        if (mUseFingerPrint && mFingerprintHardwareDetected) {
            mFingerprintButton.setVisibility(View.VISIBLE);
            mDeleteButton.setVisibility(View.GONE);
        } else {
            mFingerprintButton.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.VISIBLE);
        }

        mDeleteButton.setEnabled(false);

    }


    private PFCodeView.OnPFCodeListener mCodeListener = new PFCodeView.OnPFCodeListener() {
        @Override
        public void onCodeCompleted(String code) {
            try {

                if (mIsCreateMode) {
                    if (mFirstTimeEnterCode.isEmpty()) {
                        mFirstTimeEnterCode = code;

                        mCodeView.clearCode();
                    } else {
                        if (mFirstTimeEnterCode.equals(code)) {
                            String encodedCode = PFFingerprintPinCodeHelper.getInstance().encodePin(getContext(), code);
                            if (mCodeCreateListener != null) {
                                mCodeCreateListener.onCodeCreated(encodedCode);
                            }
                        } else {

                            mFirstTimeEnterCode = "";
                            mType = "";
                            applyConfiguration(mConfiguration);
                            errorAction();
                        }
                    }

                } else {
                    boolean isCorrect = PFFingerprintPinCodeHelper.getInstance().checkPin(getContext(), mEncodedPinCode, code);
                    if (mLoginListener != null) {
                        if (isCorrect) {
                            mLoginListener.onCodeInputSuccessful();
                        } else {
                            mLoginListener.onPinLoginFailed();
                            errorAction();
                        }
                    }
                    if (!isCorrect && mConfiguration.isClearCodeOnError()) {
                        mCodeView.clearCode();
                    }
                }
            } catch (Exception e) {
                deleteEncodeKey();
            }


        }

        @Override
        public void onCodeNotCompleted(String code) {
            if (mIsCreateMode) {
                // mNextButton.setVisibility(View.GONE);
                return;
            }
        }
    };


  /*  private View.OnClickListener mOnNextButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if (mFirstTimeEnterCode.equals("") && mNextButton.getText().toString().equalsIgnoreCase("next")) {

                    mNextButton.setText("Confirm");
                    mFirstTimeEnterCode = mCode;
                    mTitle.setVisibility(View.VISIBLE);
                    mTitle.setText("Re-enter your PIN");
                    mCodeView.clearCode();
                } else if (!mFirstTimeEnterCode.equals("") &&
                        mNextButton.getText().toString().equalsIgnoreCase("Confirm")) {
                    if (mFirstTimeEnterCode.equals(mCode)) {
                        String encodedCode = PFFingerprintPinCodeHelper.getInstance().encodePin(getContext(), mCode);
                        if (mCodeCreateListener != null) {
                            mCodeCreateListener.onCodeCreated(encodedCode);
                        }
                    } else {
                        mTitle.setText("Create Pin");
                        mNextButton.setText("Next");
                        mFirstTimeEnterCode = "";
                        mType = "";
                        applyConfiguration(mConfiguration);
                        errorAction();
                    }
                }


                //showFingerprintAlertDialog(getActivity());
            } catch (PFSecurityException e) {
                e.printStackTrace();
                deleteEncodeKey();
            }
        }
    };
*/

    private void deleteEncodeKey() {
        try {
            PFFingerprintPinCodeHelper.getInstance().delete();
        } catch (PFSecurityException e) {
            e.printStackTrace();
            Log.d(TAG, "Can not delete the alias");
        }
    }

    private void errorAction() {
        Vibrator v = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(400);
        }
        final Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_pf);
        mCodeView.startAnimation(animShake);
        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          mCodeView.clearCode();
                                          mDeleteButton.setVisibility(View.GONE);
                                          if (mType.equalsIgnoreCase("change_pin")) {

                                          } else if (AppSession.getInstance(getActivity()).getPIN().length() > 0) {
                                              mFingerprintButton.setVisibility(View.VISIBLE);
                                              startFingerPrint();
                                          } else {
                                              mFingerprintButton.setVisibility(View.GONE);
                                          }
                                      }
                                  },

                400);
    }


    /**
     * Set OnPFLockScreenCodeCreateListener.
     *
     * @param listener OnPFLockScreenCodeCreateListener object.
     */
    public void setCodeCreateListener(OnPFLockScreenCodeCreateListener listener) {
        mCodeCreateListener = listener;
    }

    /**
     * Set OnPFLockScreenLoginListener.
     *
     * @param listener OnPFLockScreenLoginListener object.
     */
    public void setLoginListener(OnPFLockScreenLoginListener listener) {
        mLoginListener = listener;
    }

    /**
     * Set Encoded pin code.
     *
     * @param encodedPinCode encoded pin code string, that was created before.
     */
    public void setEncodedPinCode(String encodedPinCode) {
        mEncodedPinCode = encodedPinCode;
    }


    /**
     * Pin Code create callback interface.
     */
    public interface OnPFLockScreenCodeCreateListener {

        /**
         * Callback method for pin code creation.
         *
         * @param encodedCode encoded pin code string.
         */
        void onCodeCreated(String encodedCode);

    }


    /**
     * Login callback interface.
     */
    public interface OnPFLockScreenLoginListener {

        /**
         * Callback method for successful login attempt with pin code.
         */
        void onCodeInputSuccessful();

        /**
         * Callback method for successful login attempt with fingerprint.
         */
        void onFingerprintSuccessful();

        /**
         * Callback method for unsuccessful login attempt with pin code.
         */
        void onPinLoginFailed();

        /**
         * Callback method for unsuccessful login attempt with fingerprint.
         */
        void onFingerprintLoginFailed();

    }


}

