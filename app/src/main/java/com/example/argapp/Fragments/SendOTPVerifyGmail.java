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
import androidx.navigation.Navigation;

import com.example.argapp.R;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

public class SendOTPVerifyGmail extends Fragment {

    private EditText m_EmailInput;
    private Button m_SendOtpButton;
    private FirebaseAuth m_Auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gmail_signup, container, false);

        m_EmailInput = view.findViewById(R.id.emailInput);
        m_SendOtpButton = view.findViewById(R.id.sendOtpButton);
        m_Auth = FirebaseAuth.getInstance();

        m_SendOtpButton.setOnClickListener(v -> {
            String email = m_EmailInput.getText().toString();
            if (!email.isEmpty()) {
                m_Auth.sendSignInLinkToEmail(email, getActionCodeSettings())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "OTP sent to email!", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(view).navigate(R.id.action_emailVerification_to_otpVerification);
                            } else {
                                Toast.makeText(getContext(), "Failed to send OTP!", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getContext(), "Email cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private ActionCodeSettings getActionCodeSettings() {
        return ActionCodeSettings.newBuilder()
                .setUrl("https://example.com/verify")
                .setHandleCodeInApp(true)
                .setAndroidPackageName("com.example.argapp", true, null)
                .build();
    }
}
