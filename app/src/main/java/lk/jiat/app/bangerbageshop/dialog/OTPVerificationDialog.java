package lk.jiat.app.bangerbageshop.dialog;

import static android.graphics.Color.TRANSPARENT;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

import lk.jiat.app.bangerbageshop.R;


public class OTPVerificationDialog extends Dialog {

    private EditText otpET1,otpET2,otpET3,otpET4,otpET5,otpET6;
    private TextView resendButton;
    private Button verifyBtn;
    private int resendTime = 60;
    private boolean resendEnabled = false;
    private int startPoition = 0;
    private final String MOBILE_NUMBER;

    public OTPVerificationDialog(@NonNull Context context,String mobile ) {
        super(context);
        this.MOBILE_NUMBER = mobile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getResources().getColor(android.R.color.transparent)));
        setContentView(R.layout.otp_popup);

        otpET1 = findViewById(R.id.otpET1);
        otpET2 = findViewById(R.id.otpET2);
        otpET3 = findViewById(R.id.otpET3);
        otpET4 = findViewById(R.id.otpET4);
        otpET5 = findViewById(R.id.otpET5);
        otpET6 = findViewById(R.id.otpET6);
        resendButton = findViewById(R.id.resendOTP);

        verifyBtn = findViewById(R.id.verifyButton);

        otpET1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()==1){

                    startPoition = 0;
                    showKeyboard(otpET2);
                }
            }
        });
        otpET2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()==1){

                    startPoition = 1;
                    showKeyboard(otpET3);
                }
            }
        });
        otpET3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()==1){

                    startPoition = 2;
                    showKeyboard(otpET4);
                }
            }
        });
        otpET4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()==1){
                    startPoition = 3;
                    showKeyboard(otpET5);

                }
            }
        });
        otpET5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()==1){
                    showKeyboard(otpET6);
                    startPoition = 4;
                }
            }
        });
        otpET6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()==1){
                    startPoition = 5;
                    verifyBtn.setBackgroundResource(R.drawable.roundback_red);
                }
            }
        });


        final TextView mobile = findViewById(R.id.mobileNumber);
        mobile.setText(MOBILE_NUMBER);

        showKeyboard(otpET1);

        startCountDown();

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resendEnabled){
                    //resendCode
                    startCountDown();
                }
            }
        });
    }

    private void showKeyboard(EditText editText){
        editText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
    }
    private void startCountDown(){
        resendEnabled = false;
        resendButton.setTextColor(Color.parseColor("#99000000"));
        new CountDownTimer(resendTime*1000,1000){

            @Override
            public void onTick(long l) {
                resendButton.setText("Resend Code ("+(l/1000)+")");
            }

            @Override
            public void onFinish() {
                resendEnabled = true;
                resendButton.setText("Resend Code");
                resendButton.setTextColor(getContext().getResources().getColor(android.R.color.holo_blue_dark));
            }
        }.start();
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_DEL){
            if(startPoition==3){
                showKeyboard(otpET3);
                startPoition=2;

            }else if(startPoition==2){
                showKeyboard(otpET2);
                startPoition=1;

            }else if(startPoition==1){
                showKeyboard(otpET1);
                startPoition=0;

            }
            verifyBtn.setBackgroundResource(R.drawable.roundback_brown);
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

}
