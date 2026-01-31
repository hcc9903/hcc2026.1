package com.kailang.wastebook.data;

import android.content.Context;
import androidx.lifecycle.LiveData;

import com.kailang.wastebook.data.Entity.User;
import com.kailang.wastebook.utils.AppExecutors;

import java.util.List;

public class UserRepository {
    private UserDao userDao;
    private LiveData<List<User>> allUsersLive;

    public UserRepository(Context context) {
        UserDatabase userDatabase = UserDatabase.getDatabase(context.getApplicationContext());
        userDao = userDatabase.getUserDao();
        allUsersLive = userDao.getUserLive();
    }

    public LiveData<List<User>> getAllUsersLive() {
        return allUsersLive;
    }

    public void insertUser(final User... users) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                userDao.insertUser(users);
            }
        });
    }

    
}
