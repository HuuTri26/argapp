package com.example.argapp.Fragments;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.argapp.Activities.MainActivity;
import com.example.argapp.Classes.CountryCodes;
import com.example.argapp.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUp#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUp extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignUp() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUp newInstance(String param1, String param2) {
        SignUp fragment = new SignUp();
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

    private EditText m_FirstName;
    private EditText m_LastName;
    private EditText m_Email;
    private EditText m_Password;
    private EditText m_ConfirmPassword;
    private EditText m_PhoneNumber;
    private TextView m_LoginTextView;
    private Button m_SignUpButton;
    private View m_View;
    private AutoCompleteTextView m_Spinner;
    private MainActivity m_HostedActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_View = inflater.inflate(R.layout.signup_page, container, false);
        m_FirstName = m_View.findViewById(R.id.firstNameEditText);
        m_LastName = m_View.findViewById(R.id.lastNameEditText);
        m_Email = m_View.findViewById(R.id.emailEditText);
        m_Password = m_View.findViewById(R.id.passwordEditText);
        m_ConfirmPassword = m_View.findViewById(R.id.confirmPasswordEditText);
        m_PhoneNumber = m_View.findViewById(R.id.phoneNumber);
        m_Spinner = m_View.findViewById(R.id.spinner);
        m_LoginTextView = m_View.findViewById(R.id.loginTextView);
        m_SignUpButton = m_View.findViewById(R.id.signUpButton);
        m_HostedActivity = (MainActivity) requireActivity();

        AnimatorSet buttonAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(
                getContext(), R.animator.buttons_click_animation);

        creatLoginTextStyle();
        createSpinner();

        m_SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAnimator.setTarget(m_SignUpButton);
                buttonAnimator.start();

                String email = m_Email.getText().toString();
                String password = m_Password.getText().toString();
                String firstName = m_FirstName.getText().toString();
                String lastName = m_LastName.getText().toString();
                String phoneNumber = m_Spinner.getText().toString() + m_PhoneNumber.getText().toString();

                if(checkValidation())
                {
                    m_HostedActivity.Register(firstName, lastName, email, password, phoneNumber);
                }
            }
        });

        m_LoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(m_View).navigate(R.id.action_signup_page_to_login_page);
            }
        });

        m_FirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    checkNameValidation(m_FirstName);
                }
            }
        });

        m_LastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    checkNameValidation(m_LastName);
                }
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

        m_ConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    checkConfirmPasswordValidation();
                }
            }
        });

        m_PhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    checkPhoneNumberValidation();
                }
            }
        });

        return m_View;
    }

    private void creatLoginTextStyle()
    {
        String fullText = "Already have an account? Log in";
        // Create a SpannableString
        SpannableString spannableString = new SpannableString(fullText);

        // Apply color to "Log in"
        int startIndex = fullText.indexOf("Log in");
        int endIndex = startIndex + "Log in".length();
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#05D3BD")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the styled text in the TextView
        m_LoginTextView.setText(spannableString);
    }
    private void createSpinner()
    {
        List<String> countryCodes = CountryCodes.GetCountryCodes();
        // Add generic type parameters to ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, countryCodes);
        m_Spinner.setAdapter(adapter);
        m_Spinner.setDropDownHeight(400);
        m_Spinner.setDropDownBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.edit_text_shape));

    }

    private boolean checkValidation()
    {
        return checkNameValidation(m_FirstName) && checkNameValidation(m_LastName)
                && checkEmailValidation() && checkPasswordValidation()
                && checkConfirmPasswordValidation() && checkPhoneNumberValidation();
    }

    private boolean checkNameValidation(EditText name)
    {
        TextInputLayout textParent = (TextInputLayout) name.getParent().getParent();
        String nameText = name.getText().toString();
        boolean isValid = true;

        if(!nameText.isEmpty())
        {
            textParent.setError(null);
            if(!nameText.matches("[a-zA-Z]+"))
            {
                textParent.setError("This field should contain only letters");
                isValid = false;
            }
        }
        else
        {
            textParent.setError("This field cannot be empty");
            isValid = false;
        }

        return isValid;
    }

    private boolean checkEmailValidation()
    {
        TextInputLayout textParent = (TextInputLayout) m_Email.getParent().getParent();
        String emailText = m_Email.getText().toString();
        boolean isValid = true;

        if(!emailText.isEmpty())
        {
            textParent.setError(null);
            if(!emailText.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))
            {
                textParent.setError("The email address is invalid");
                isValid = false;
            }
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
            if(!passwordText.matches("^(?=.*[A-Z]).{8,}$"))
            {
                textParent.setError("Password should contain at least 1 uppercase and at least 8 characters");
                isValid = false;
            }
        }
        else
        {
            textParent.setError("This field cannot be empty");
            isValid = false;
        }

        return isValid;
    }

    private boolean checkConfirmPasswordValidation()
    {
        TextInputLayout textParent = (TextInputLayout) m_ConfirmPassword.getParent().getParent();
        String confrimPasswordText = m_ConfirmPassword.getText().toString();
        String passwordText = m_Password.getText().toString();
        boolean isValid = true;

        if(!confrimPasswordText.isEmpty())
        {
            textParent.setError(null);
            if(!confrimPasswordText.equals(passwordText))
            {
                textParent.setError("Passwords do not match");
                isValid = false;
            }
        }
        else{
            textParent.setError("This field cannot be empty");
            isValid = false;
        }

        return true;
    }

    private boolean checkPhoneNumberValidation()
    {
        TextInputLayout textParent = (TextInputLayout) m_PhoneNumber.getParent().getParent();
        String phoneNumber = m_PhoneNumber.getText().toString();
        boolean isValid = true;

        if(!phoneNumber.isEmpty())
        {
            textParent.setError(null);
            if(phoneNumber.length() < 9 || phoneNumber.length() > 11)
            {
                textParent.setError("The phone number is invalid");
                isValid = false;
            }
        }
        else
        {
            textParent.setError("This field cannot be empty");
            isValid = false;
        }

        return isValid;
    }
}