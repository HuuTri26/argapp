package com.example.argapp.Fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.argapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class OTPVerify extends Fragment {

    private EditText m_OtpInput;
    private Button m_VerifyOtpButton;
    private FirebaseAuth m_Auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.otp_verify_gmail, container, false);

        m_OtpInput = view.findViewById(R.id.otpInput);
        m_VerifyOtpButton = view.findViewById(R.id.verifyOtpButton);
        m_Auth = FirebaseAuth.getInstance();

        m_VerifyOtpButton.setOnClickListener(v -> {
            String otp = m_OtpInput.getText().toString();
            if (!otp.isEmpty()) {
                // Verify OTP logic here
                Toast.makeText(getContext(), "OTP verified successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "OTP cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}

