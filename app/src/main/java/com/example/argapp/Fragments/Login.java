package com.example.argapp.Fragments;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.argapp.Activities.MainActivity;
import com.example.argapp.R;
import com.google.android.material.textfield.TextInputLayout;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static Login newInstance(String param1, String param2) {
        Login fragment = new Login();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private View m_View;
    private EditText m_Email;
    private EditText m_Password;
    private Button m_LoginButton;
    private TextView m_SignUpButton;
    private MainActivity m_HostedActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_View =  inflater.inflate(R.layout.login_page, container, false);
        m_LoginButton = m_View.findViewById(R.id.loginButton);
        m_SignUpButton = m_View.findViewById(R.id.signUpButton);
        m_Email = m_View.findViewById(R.id.emailEditText);
        m_Password = m_View.findViewById(R.id.passwordEditText);
        m_HostedActivity = (MainActivity) requireActivity();

        AnimatorSet buttonAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(
                getContext(), R.animator.buttons_click_animation);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finishAffinity();
            }
        });

        m_LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAnimator.setTarget(m_LoginButton);
                buttonAnimator.start();

                String email = m_Email.getText().toString();
                String password = m_Password.getText().toString();

                if(checkValidation()) {
                    m_HostedActivity.Login(email, password);
                }
            }
        });

        m_SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAnimator.setTarget(m_SignUpButton);
                buttonAnimator.start();

                Navigation.findNavController(m_View).navigate(R.id.action_login_page_to_signup_page);
            }
        });

        m_Email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    checkEmailValidation();
                }
            }
        });

        m_Password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    checkPasswordValidation();
                }
            }
        });

        return m_View;
    }

    private boolean checkValidation()
    {
        return checkEmailValidation() && checkPasswordValidation();
    }

    private boolean checkEmailValidation()
    {
        TextInputLayout textParent = (TextInputLayout) m_Email.getParent().getParent();
        String emailText = m_Email.getText().toString();
        boolean isValid = true;

        if(!emailText.isEmpty())
        {
            textParent.setError(null);
        }
        else
        {
            textParent.setError("This field cannot be empty");
            isValid = false;
        }

        return isValid;
    }
    private boolean checkPasswordValidation()
    {
        TextInputLayout textParent = (TextInputLayout) m_Password.getParent().getParent();
        String passwordText = m_Password.getText().toString();
        boolean isValid = true;

        if(!passwordText.isEmpty())
        {
            textParent.setError(null);
        }
        else
        {
            textParent.setError("This field cannot be empty");
            isValid = false;
        }

        return isValid;
    }
}