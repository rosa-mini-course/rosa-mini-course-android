package com.winnerwinter.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
//import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.exception.ApolloHttpException;
import com.apollographql.apollo.exception.ApolloNetworkException;
import com.winnerwinter.SignInMutation;
import com.winnerwinter.SignUpMutation;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText uidEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private Button loginButton;
//    private TextView registerTextView;
    private ApolloClient apolloClient;
    private UserManager userManager;

    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initHelper();
        initActionBar();
        initLayout();
        initEditText();
        initButton();
//        initTextView();
    }

    private void initHelper() {
        apolloClient = ApolloManager.getInstance(this);
        userManager = UserManager.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        User user = userManager.loadUser();
        if (user != null) {
            startActivity(new Intent(this, MainActivity.class));
        }
        if (!MyApplication.shouldLogin) {
            finish();
        }
    }

    private void initActionBar() {
//        getWindow().setStatusBarColor(Color.argb(255, 250, 250, 250));
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    private void initLayout() {
        progressBar = findViewById(R.id.loading);
    }

    private void initEditText() {
        uidEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
    }

    private void initButton() {
        loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(v -> {
            if (uidEditText.getText().toString().equals("")) {
                Toast.makeText(LoginActivity.this, "帐号不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (passwordEditText.getText().toString().equals("")) {
                Toast.makeText(LoginActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                // 此处进行登录验证
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.VISIBLE);
                    loginButton.setEnabled(false);
                });
                String username = uidEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                SignInMutation mutation = SignInMutation.builder().passwd(password).mail(username).build();

                try {
                    apolloClient
                            .mutate(mutation)
                            .enqueue(new ApolloCall.Callback<SignInMutation.Data>() {
                                         @Override
                                         public void onResponse(@NotNull Response<SignInMutation.Data> response) {
                                             if (response.hasErrors()) {
                                                 final BackendError error = ApolloErrorHelper.getBackendError(Objects.requireNonNull(response.getErrors()));
                                                 runOnUiThread(() -> {
                                                     progressBar.setVisibility(View.GONE);
                                                     loginButton.setClickable(true);
                                                     Toast.makeText(LoginActivity.this, error.message, Toast.LENGTH_SHORT).show();
                                                 });
                                                 return;
                                             }

                                             SignInMutation.SignIn data = Objects.requireNonNull(Objects.requireNonNull(response.getData()).signIn());
                                             User user = new User(data.userId(), data.useremail());
                                             userManager.saveUser(user);
                                             Log.e("user2", user.toString());

                                             runOnUiThread(() -> {
                                                 progressBar.setVisibility(View.GONE);
                                                 loginButton.setClickable(true);
                                                 Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                                 startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                 finish();
                                             });
                                         }
                                         //
                                         @Override
                                         public void onFailure(@NotNull final ApolloException e) {
                                             // Lower-level error
                                             e.printStackTrace();
                                             runOnUiThread(() -> {
                                                 Toast.makeText(LoginActivity.this, "登录失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                 progressBar.setVisibility(View.GONE);
                                                 loginButton.setEnabled(true);
                                             });
                                         }
                                     }
                            );
                } catch (@NotNull final ApolloNetworkException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "登录失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        loginButton.setEnabled(true);
                    });
                }

            }).start();
        });
        signupButton = findViewById(R.id.signup);
        signupButton.setOnClickListener(v -> {
            if (uidEditText.getText().toString().equals("")) {
                Toast.makeText(LoginActivity.this, "帐号不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (passwordEditText.getText().toString().equals("")) {
                Toast.makeText(LoginActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(() -> {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.VISIBLE);
                    signupButton.setEnabled(false);
                });
            });
            String username = uidEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            SignUpMutation mutation = SignUpMutation.builder().invCode("5201314").mail(username).passwd(password).rol("student").build();
            try {
                apolloClient.mutate(mutation)
                        .enqueue(new ApolloCall.Callback<SignUpMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<SignUpMutation.Data> response) {
                                if (response.hasErrors()) {
                                    final BackendError error = ApolloErrorHelper.getBackendError(Objects.requireNonNull(response.getErrors()));
                                    runOnUiThread(() -> {
                                        progressBar.setVisibility(View.GONE);
                                        signupButton.setEnabled(false);
                                        Toast.makeText(LoginActivity.this, error.message, Toast.LENGTH_SHORT).show();
                                    });
                                    return;
                                }
                                String useremail = Objects.requireNonNull(response.getData()).signUp().useremail();
                                String userId = response.getData().signUp().userId();
                                User user = new User(userId, useremail);
                                userManager.saveUser(user);
                                runOnUiThread(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    signupButton.setEnabled(true);
                                    Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                });

                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                e.printStackTrace();
                                runOnUiThread(() -> {
                                    Toast.makeText(LoginActivity.this, "注册失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    signupButton.setEnabled(true);
                                });
                            }
                        });
            } catch (@NotNull ApolloNetworkException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "注册失败" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    signupButton.setEnabled(true);
                });
            }

        });
    }

//    private void initTextView() {
//        registerTextView = findViewById(R.id.tv_register);
//        registerTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
//            }
//        });
//    }
}
