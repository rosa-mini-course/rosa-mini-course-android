package com.winnerwinter.myapplication.data

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.winnerwinter.SignInMutation
import com.winnerwinter.myapplication.data.model.LoggedInUser
import com.winnerwinter.myapplication.ui.login.LoginViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    var user: LoggedInUser = LoggedInUser("", "");
    fun login(username: String, password: String): Result<LoggedInUser> {
        val apolloClient = ApolloClient.builder().serverUrl("http://192.168.1.38:4000/graphql").build()
        val signInMutation = SignInMutation(password, username)
        try {
            // TODO: handle loggedInUser authentication
//            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
//            return Result.Success(fakeUser)
//            var user: LoggedInUser = LoggedInUser("", "");
            apolloClient
                .mutate(signInMutation)
                .enqueue(object: ApolloCall.Callback<SignInMutation.Data>() {
                    override fun onResponse(response: Response<SignInMutation.Data>) {
//                        TODO("Not yet implemented")
                        user = LoggedInUser(response.data?.signIn?.userId.toString(), response.data?.signIn?.useremail.toString())
                        Log.d(TAG, "登录成功")
                    }
                    override fun onFailure(e: ApolloException) {
//                        TODO("Not yet implemented")
                        Log.d(TAG, "登录失败")
                    }
                })
            if (user.userId === "" && user.displayName === "") {
                return Result.Error(IOException("Error logging in"))
            }
            return Result.Success(user)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}
