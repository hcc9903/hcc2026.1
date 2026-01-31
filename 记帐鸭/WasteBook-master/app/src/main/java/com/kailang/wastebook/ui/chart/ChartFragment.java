package com.kailang.wastebook.ui.chart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.kailang.wastebook.R;
import com.kailang.wastebook.adapters.WasteBookAdapter;
import com.kailang.wastebook.data.Entity.WasteBook;
import com.kailang.wastebook.utils.DateToLongUtils;
import com.kailang.wastebook.utils.PieChartUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChartFragment extends Fragment {

    private boolean isExpenseMode = true;  // true=支出模式, false=收入模式
    private Double totalIncome = 0.0, totalExpense = 0.0, total = 0.0;
    private long start, end;
    private String selectedStr = "近1个月";
    private List<WasteBook> allWasteBooks;
    private MutableLiveData<List<WasteBook>> selectedWasteBooks = new MutableLiveData<>();

    private RecyclerView recyclerView;
    private WasteBookAdapter wasteBookAdapter;
    private LiveData<List<WasteBook>> wasteBooksLive;
    private ChartViewModel chartViewModel;
    private LineChart lineChart;
    private HashMap<String, Float> categoryDataMap;  // 分类数据映射表
    private PieChart mPieChart;
    private Button btnExpense, btnIncome;  // 支出/收入切换按钮
    private TextView select;

    private OptionsPickerView pvNoLinkOptions;
    private ArrayList<String> options1Items_type = new ArrayList<>(), options1Items_INOUT = new ArrayList<>();
    private ArrayList<ArrayList<String>> options1Items_date = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chart, container, false);
        chartViewModel = ViewModelProviders.of(this).get(ChartViewModel.class);
        btnIncome = root.findViewById(R.id.textView_in_chart);
        btnExpense = root.findViewById(R.id.textView_out_chart);
        btnIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpenseMode = false;
                selector(selectedStr);
            }
        });
        btnExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpenseMode = true;
                selector(selectedStr);
            }
        });
        chartViewModel.getAllWasteBookLive().observe(getViewLifecycleOwner(), new Observer<List<WasteBook>>() {
            @Override
            public void onChanged(List<WasteBook> wasteBooks) {
                allWasteBooks = wasteBooks;
                selector(selectedStr);
            }
        })
;
        //饼状图数据,更新UI
        selectedWasteBooks.observe(getViewLifecycleOwner(), new Observer<List<WasteBook>>() {
            @Override
            public void onChanged(List<WasteBook> wasteBooks) {
                onWasteBooksChanged(wasteBooks);
            }
        });
        //选择器
        select = root.findViewById(R.id.tv_select_chart);
        //饼状图
        mPieChart = root.findViewById(R.id.mPieChart);
        //点击事件
        mPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry pieEntry = (PieEntry) e;
                mPieChart.setCenterText(pieEntry.getLabel());
            }

            @Override
            public void onNothingSelected() {

            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化适配器

        recyclerView = requireActivity().findViewById(R.id.recyclerView_wasteBook_chart);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        wasteBookAdapter = new WasteBookAdapter(requireContext(), false);
        recyclerView.setAdapter(wasteBookAdapter);


        initCustomOptionPicker();
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvNoLinkOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        String temp_date = options1Items_date.get(options1).get(options2);
                        select.setText(temp_date + "▼");
                        selectedStr = temp_date;
                        selector(temp_date);
                    }
                }).setSubmitText("确定")
                        .setCancelText("取消")
                        .setTitleText("查询")
                        .setOutSideCancelable(false)
                        .build();
                pvNoLinkOptions.setPicker(options1Items_type, options1Items_date);
                pvNoLinkOptions.show();
            }
        });
    }

    //二级联动
    private void initCustomOptionPicker() {
        //选项0
        options1Items_INOUT.add("支出");
        options1Items_INOUT.add("收入");
        //选项1
        options1Items_type.add("按周");
        options1Items_type.add("按月");
        options1Items_type.add("按年");
        //选项2
        ArrayList<String> item_0 = new ArrayList<>();
        item_0.add("当前周");
        item_0.add("近两周");
        item_0.add("近三周");
        ArrayList<String> item_1 = new ArrayList<>();
        item_1.add("近1个月");
        item_1.add("近3个月");
        item_1.add("近6个月");
        ArrayList<String> item_2 = new ArrayList<>();
        for (int i = 2019; i >= 2010; i--) {
            item_2.add(i + "年");
        }
        options1Items_date.add(item_0);
        options1Items_date.add(item_1);
        options1Items_date.add(item_2);
    }


    private void selector(String timeStr) {
        List<WasteBook> showWasteBooks = new ArrayList<>();
        IN = 0.0;
        OUT = 0.0;
        //支出
        if (isOUT) {
            setStartEnd(timeStr);
            if (allWasteBooks != null)
                for (WasteBook w : allWasteBooks) {
                    if (w.isType() && w.getTime() >= end && w.getTime() <= start) {
                        showWasteBooks.add(w);
                        OUT += w.getAmount();
                    }
                }
        } else if (!isOUT) {
            setStartEnd(timeStr);
            for (WasteBook w : allWasteBooks) {
                if (!w.isType() && w.getTime() >= end && w.getTime() <= start) {
                    showWasteBooks.add(w);
                    IN += w.getAmount();
                }
            }
        }
        selectedWasteBooks.setValue(showWasteBooks);
    }

    private void setStartEnd(String timeStr) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        start = cal.getTimeInMillis();
        if (timeStr.contains("周")) {
            if (timeStr.charAt(0) == '当') {
                //一周
                cal.add(Calendar.DAY_OF_MONTH, -7);
                end = cal.getTimeInMillis();
            } else if (timeStr.charAt(1) == '两') {
                //两周
                cal.add(Calendar.DAY_OF_MONTH, -14);
                end = cal.getTimeInMillis();

            } else if (timeStr.charAt(1) == '三') {
                //三周
                cal.add(Calendar.DAY_OF_MONTH, -21);
                end = cal.getTimeInMillis();
            }

        } else if (timeStr.contains("月")) {
            if (timeStr.charAt(1) == '1') {
                //一个月
                cal.add(Calendar.MONTH, -1);
                end = cal.getTimeInMillis();
            } else if (timeStr.charAt(1) == '2') {
                //2个月
                cal.add(Calendar.MONTH, -2);
                end = cal.getTimeInMillis();

            } else if (timeStr.charAt(1) == '3') {
                //3个月
                cal.add(Calendar.MONTH, -3);
                end = cal.getTimeInMillis();
            }
        } else if (timeStr.contains("年")) {
            //按年
            int yearTemp = Integer.parseInt(timeStr.substring(0, timeStr.length() - 1));
            end = DateToLongUtils.dateToLong(yearTemp + "-1-1 00:00:00");
            start = DateToLongUtils.dateToLong(yearTemp + "-12-31 23:59:59");
        }
    }
    
    // ========== 提取的辅助方法(优化后) ==========
    
    /**
     * 处理账单数据变化
     * <p>当账单数据更新时调用,负责聚合数据并更新图表和列表</p>
     * 
     * @param wasteBooks 账单列表
     */
    private void onWasteBooksChanged(List<WasteBook> wasteBooks) {
        if (wasteBooks == null || wasteBooks.isEmpty()) {
            showEmptyChart();
            return;
        }
        
        // 聚合分类数据 - O(n)算法
        java.util.Map<String, CategoryData> categoryMap = aggregateByCategory(wasteBooks);
        
        // 更新图表
        updateChart(categoryMap);
        
        // 更新列表(只显示每个分类的第一条记录)
        updateRecyclerView(categoryMap);
    }
    
    /**
     * 显示空图表
     */
    private void showEmptyChart() {
        categoryDataMap = new HashMap<>();
        String title = isExpenseMode ? "支出" : "收入";
        PieChartUtils.getPitChart().setPieChart(mPieChart, categoryDataMap, title, true);
    }
    
    /**
     * 按分类聚合账单数据
     * <p>使用HashMap优化,时间复杂度O(n)</p>
     * 
     * @param wasteBooks 账单列表
     * @return 分类数据映射表
     */
    private java.util.Map<String, CategoryData> aggregateByCategory(List<WasteBook> wasteBooks) {
        java.util.Map<String, CategoryData> categoryMap = new java.util.LinkedHashMap<>();  // 保持插入顺序
        
        for (WasteBook book : wasteBooks) {
            String category = book.getCategory();
            
            if (!categoryMap.containsKey(category)) {
                // 首次遇到该分类,创建新记录
                CategoryData data = new CategoryData();
                data.firstBook = book;
                data.totalAmount = book.getAmount();
                categoryMap.put(category, data);
            } else {
                // 累加金额
                CategoryData data = categoryMap.get(category);
                data.totalAmount += book.getAmount();
            }
        }
        
        return categoryMap;
    }
    
    /**
     * 更新饼状图
     * 
     * @param categoryMap 分类数据映射表
     */
    private void updateChart(java.util.Map<String, CategoryData> categoryMap) {
        categoryDataMap = new HashMap<>();
        double totalSum = isExpenseMode ? totalExpense : totalIncome;
        
        // 计算百分比
        for (java.util.Map.Entry<String, CategoryData> entry : categoryMap.entrySet()) {
            String category = entry.getKey();
            double amount = entry.getValue().totalAmount;
            float percentage = (float) (amount / totalSum * 100);
            categoryDataMap.put(category, percentage);
        }
        
        // 更新图表
        String title = isExpenseMode ? "支出" : "收入";
        PieChartUtils.getPitChart().setPieChartWithType(
            mPieChart, categoryDataMap, title, true, isExpenseMode
        );
    }
    
    /**
     * 更新RecyclerView列表
     * 
     * @param categoryMap 分类数据映射表
     */
    private void updateRecyclerView(java.util.Map<String, CategoryData> categoryMap) {
        List<WasteBook> displayBooks = new ArrayList<>();
        
        // 只显示每个分类的第一条记录
        for (CategoryData data : categoryMap.values()) {
            displayBooks.add(data.firstBook);
        }
        
        wasteBookAdapter.setAllWasteBook(displayBooks);
        wasteBookAdapter.notifyDataSetChanged();
    }
    
    /**
     * 分类数据内部类
     * <p>用于存储每个分类的首条记录和总金额</p>
     */
    private static class CategoryData {
        WasteBook firstBook;  // 该分类的第一条记录
        double totalAmount;   // 该分类的总金额
    }
}