package com.example.caltracker.data;

import com.example.caltracker.RestModel.User;
import com.example.caltracker.data.model.LoggedInUser;

import java.io.IOException;

import static com.example.caltracker.API.RestClient.UserAuth;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            User user = UserAuth(username,password);
            if (user != null){
                LoggedInUser fakeUser =
                        new LoggedInUser(user);
                return new Result.Success<>(fakeUser);
            }else{
                return new Result.Error(new IllegalArgumentException("Username or password is invalid"));
            }

        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
