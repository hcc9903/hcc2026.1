package com.kailang.wastebook.data;

import android.content.Context;
import androidx.lifecycle.LiveData;

import com.kailang.wastebook.data.Entity.Category;
import com.kailang.wastebook.utils.AppExecutors;

import java.util.List;

public class CategoryRepository {
    private LiveData<List<Category>> allCategoriesLive;
    private CategoryDao categoryDao;

    public CategoryRepository(Context context) {
        CategoryDatabase categoryDatabase = CategoryDatabase.getDatabase(context);
        categoryDao = categoryDatabase.getCategoryDao();
        allCategoriesLive = categoryDao.getAllCategoriesLive();
    }

    public void insertCategory(final Category... categories) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                categoryDao.insertCategory(categories);
            }
        });
    }

    public void updateCategory(final Category... categories) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                categoryDao.updateCategory(categories);
            }
        });
    }

    public void deleteCategory(final Category... categories) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                categoryDao.deleteCategory(categories);
            }
        });
    }

    public LiveData<List<Category>> getAllCategoriesLive() {
        return allCategoriesLive;
    }

    
}
