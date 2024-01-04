package lk.jiat.app.bangerbageshop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import lk.jiat.app.bangerbageshop.R;
import lk.jiat.app.bangerbageshop.dialog.OTPVerificationDialog;

public class RegistrationActivity extends AppCompatActivity {


    EditText name, email, password, mobile;
    private FirebaseAuth auth;
    FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;

    private final String TAG = "OTP Verification";

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();

        // getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            finish();
        }

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mobile = findViewById(R.id.phoneNumber);
        // getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("firstTime", true);

        if (isFirstTime) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();

            Intent intent = new Intent(RegistrationActivity.this, OnBoardingActivity.class);
            startActivity(intent);
            finish();
        }

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(RegistrationActivity.this, "SMS quota for the Login has been exceeded", Toast.LENGTH_SHORT).show();
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(RegistrationActivity.this, "Code has been sent", Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }

    public void signUp(View view) {

        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        String mobileNumber = mobile.getText().toString();


        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Enter Name!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Enter Email Address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Enter Password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userPassword.length() < 6) {
            Toast.makeText(this, "Password too short, Enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        signInWithPhone(mobileNumber);
        OTPVerificationDialog otpVerificationDialog = new OTPVerificationDialog(RegistrationActivity.this,mobileNumber);
        otpVerificationDialog.setCancelable(false);
        otpVerificationDialog.show();

        EditText otpET1 = otpVerificationDialog.findViewById(R.id.otpET1);
        EditText otpET2 = otpVerificationDialog.findViewById(R.id.otpET2);
        EditText otpET3 = otpVerificationDialog.findViewById(R.id.otpET3);
        EditText otpET4 = otpVerificationDialog.findViewById(R.id.otpET4);
        EditText otpET5 = otpVerificationDialog.findViewById(R.id.otpET5);
        EditText otpET6 = otpVerificationDialog.findViewById(R.id.otpET6);

        TextView resendButton = otpVerificationDialog.findViewById(R.id.resendOTP);
        TextView mobileN = otpVerificationDialog.findViewById(R.id.mobileNumber);

        mobileN.setText(mobileNumber);

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resendButton.getText().toString().equals("Resend Code")){
                    signInWithPhone(mobileNumber);
                }
            }
        });

        otpVerificationDialog.findViewById(R.id.changeMobileNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otpVerificationDialog.dismiss();
            }
        });
        otpVerificationDialog.findViewById(R.id.verifyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyOTP(otpET1.getText().toString().trim()+otpET2.getText().toString().trim()+
                        otpET3.getText().toString().trim()+otpET4.getText().toString()+otpET5.getText().toString().trim()+
                        otpET6.getText().toString().trim());
            }
        });


        // startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
    }

    public void signIn(View view) {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
    }

    private void signInWithPhone(String phoneNumber) {

        if (phoneNumber.startsWith("0")) {
            phoneNumber = phoneNumber.substring(1, phoneNumber.length());
        }

        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber("+94"+phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                    .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegistrationActivity.this, "Successfully Register", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                            } else {
                                                Toast.makeText(RegistrationActivity.this, "Registration Failed" + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
//                            updateUi(user);
//                            userId= user.getUid();
//                            final String s = UUID.randomUUID().toString();
//                            new Registration().saveData();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
    public  void verifyOTP(String OTP) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,OTP);
        signInWithPhoneAuthCredential(credential);
    }
}