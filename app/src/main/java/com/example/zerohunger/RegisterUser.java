package com.example.zerohunger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword, editTextConfirmPassword, editTextIndexNumber, editTextReferenceNumber, editTextPhoneNumber, editTextHostel;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // create firebase instance
        mAuth = FirebaseAuth.getInstance();

        // set on click listeners to buttons
        TextView backButton = (TextView) findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        TextView registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        // get elements
        editTextFirstName = (EditText) findViewById(R.id.firstName);
        editTextLastName = (EditText) findViewById(R.id.lastName);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        editTextConfirmPassword = (EditText) findViewById(R.id.confirmPassword);
        editTextIndexNumber = (EditText) findViewById(R.id.indexNumber);
        editTextReferenceNumber = (EditText) findViewById(R.id.referenceNumber);
        editTextPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        editTextHostel = (EditText) findViewById(R.id.hostel);

        // get progress bar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View view) {
        // define action when clicked
        switch (view.getId()) {
            case R.id.backButton:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String indexNumber = editTextIndexNumber.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String referenceNumber = editTextReferenceNumber.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String hostel = editTextHostel.getText().toString().trim();

        if (firstName.isEmpty()){
            editTextFirstName.setError("Required");
            editTextFirstName.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            editTextLastName.setError("Required");
            editTextLastName.requestFocus();
        }

        if (indexNumber.isEmpty()) {
            editTextIndexNumber.setError("Required");
            editTextIndexNumber.requestFocus();
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Required");
            editTextEmail.requestFocus();
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please Provide Valid Email");
            editTextEmail.requestFocus();
        }

        if(password.isEmpty()) {
            editTextPassword.setError("Required");
            editTextPassword.requestFocus();
        }

        if(confirmPassword.isEmpty()){
            editTextConfirmPassword.setError("Required");
            editTextConfirmPassword.requestFocus();
        }

        if (!confirmPassword.equals(password)){
            editTextConfirmPassword.setError("Passwords do not match");
            editTextConfirmPassword.requestFocus();
        }

        if (password.length()<6){
            editTextPassword.setError("Password should be longer than 6 characters");
            editTextPassword.requestFocus();
        }

        progressBar.setVisibility(View.VISIBLE);

        //create user with firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            User user = new User(firstName, lastName, email, indexNumber, referenceNumber, phoneNumber, hostel);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        startActivity(new Intent(RegisterUser.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                        Toast.makeText(RegisterUser.this, "User has been registered", Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(RegisterUser.this, "Failed to register. Please try again.", Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }else{
                            Toast.makeText(RegisterUser.this, "Failed to register. Please try again.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });


    }
}