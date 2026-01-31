package com.kailang.wastebook.data;

import android.content.Context;
import androidx.lifecycle.LiveData;

import com.kailang.wastebook.data.Entity.WasteBook;
import com.kailang.wastebook.utils.AppExecutors;

import java.util.List;

/**
 * 账单数据仓库类
 * <p>
 * 负责封装账单数据的CRUD操作,使用AppExecutors在后台线程执行数据库操作,
 * 避免阻塞UI线程。提供LiveData接口供ViewModel层观察数据变化。
 * </p>
 * 
 * @author WasteBook Team
 * @version 1.0
 */
public class WasteBookRepository {
    private LiveData<List<WasteBook>> allWasteBooksLive;
    private WasteBookDao wasteBookDao;

   public  WasteBookRepository(Context context){
        WasteBookDatabase wasteBookDatabase = WasteBookDatabase.getDatabase(context.getApplicationContext());
        wasteBookDao = wasteBookDatabase.getWasteBookDao();
        allWasteBooksLive = wasteBookDao.getAllWasteBookLive();
    }

    /**
     * 插入账单记录
     * <p>在后台线程异步执行插入操作</p>
     * 
     * @param wasteBooks 要插入的账单记录(可变参数,支持批量插入)
     */
    public void insertWasteBook(final WasteBook...wasteBooks){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                wasteBookDao.insertWasteBook(wasteBooks);
            }
        });
    }

    /**
     * 更新账单记录
     * <p>在后台线程异步执行更新操作</p>
     * 
     * @param wasteBooks 要更新的账单记录(可变参数,支持批量更新)
     */
    public void updateWasteBook(final WasteBook...wasteBooks){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                wasteBookDao.updateWasteBook(wasteBooks);
            }
        });
    }
    /**
     * 删除账单记录
     * <p>在后台线程异步执行删除操作</p>
     * 
     * @param wasteBooks 要删除的账单记录(可变参数,支持批量删除)
     */
    public void deleteWasteBook(final WasteBook...wasteBooks){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                wasteBookDao.deleteWasteBook(wasteBooks);
            }
        });
    }
    /**
     * 删除所有账单记录
     * <p>清空账单数据库,谨慎使用</p>
     */
    public void deleteAllWasteBooks(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                wasteBookDao.deleteAllWasteBooks();
            }
        });
    }

//    public LiveData<List<WasteBook>> selectWasteBookByLongTime(long a,long b){
//       return wasteBookDao.selectWasteBookByLongTime(a,b);
//    }

    /**
     * 搜索账单记录
     * <p>根据关键词搜索账单的备注或分类字段</p>
     * 
     * @param pattern 搜索关键词
     * @return 包含搜索结果的LiveData对象
     */
    public LiveData<List<WasteBook>>findWasteBookWithPattern(String pattern){
        return wasteBookDao.findWordsWithPattern("%" + pattern + "%");
    }
    /**
     * 获取所有账单记录(按时间倒序)
     * 
     * @return 包含所有账单的LiveData对象,数据变化时自动通知观察者
     */
    public LiveData<List<WasteBook>> getAllWasteBooksLive(){
        return allWasteBooksLive;
    }
    /**
     * 获取所有账单记录(按金额倒序)
     * 
     * @return 包含所有账单的LiveData对象,按金额从大到小排序
     */
    public LiveData<List<WasteBook>> getAllWasteBooksLiveByAmount(){
        return wasteBookDao.getAllWasteBookLiveByAmount();
    }

    

}
