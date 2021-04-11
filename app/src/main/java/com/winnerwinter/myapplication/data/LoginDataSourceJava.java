package com.winnerwinter.myapplication.data;


import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.winnerwinter.SignInMutation;
import com.winnerwinter.myapplication.data.model.LoggedInUser;

import org.jetbrains.annotations.NotNull;


import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSourceJava {
    public Result<LoggedInUser> login(String username, String password){
        final LoggedInUser[] user = new LoggedInUser[1];
        ApolloClient apolloClient = ApolloClient.builder().serverUrl("http://192.168.1.38:4000/graphql").build();
        SignInMutation signInMutation = new SignInMutation(password, username);
        try {
            // TODO: handle loggedInUser authentication
//            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
//            return Result.Success(fakeUser)
//            var user: LoggedInUser = LoggedInUser("", "");
            apolloClient
                    .mutate(signInMutation)
                    .enqueue(new ApolloCall.Callback<SignInMutation.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<SignInMutation.Data> response) {
                            user[0] = new LoggedInUser(response.getData().getSignIn().getUserId(), response.getData().getSignIn().getUseremail());
                            Log.d(TAG, "登录成功");
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            Log.d(TAG, "登录失败");
                        }


                });
            if (user[0].getUserId().equals("") && user[0].getDisplayName().equals("")) {
                return new Result.Error(new IOException("Error logging in"));
            }
            return new Result.Success(user[0]);
        } catch (Throwable e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
