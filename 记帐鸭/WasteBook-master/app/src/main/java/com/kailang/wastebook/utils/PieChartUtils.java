package com.kailang.wastebook.utils;

import android.graphics.Color;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PieChartUtils {
    // ========== 现代化渐变配色 ==========
    // 支出颜色:粉红渐变系
    public final int[] PIE_COLORS_EXPENSE = {
            Color.rgb(244, 63, 94),   // #f43f5e
            Color.rgb(236, 72, 153),  // #ec4899
            Color.rgb(244, 114, 182), // #f472b6
            Color.rgb(251, 146, 195), // #fb92c3
            Color.rgb(253, 164, 175), // #fda4af
            Color.rgb(254, 205, 211), // #fecdd3
            Color.rgb(252, 231, 243), // #fce7f3
            Color.rgb(253, 242, 248)  // #fdf2f8
    };
    
    // 收入颜色:青蓝渐变系
    public final int[] PIE_COLORS_INCOME = {
            Color.rgb(6, 182, 212),   // #06b6d4
            Color.rgb(59, 130, 246),  // #3b82f6
            Color.rgb(96, 165, 250),  // #60a5fa
            Color.rgb(147, 197, 253), // #93c5fd
            Color.rgb(165, 180, 252), // #a5b4fc
            Color.rgb(196, 181, 253), // #c4b5fd
            Color.rgb(221, 214, 254), // #ddd6fe
            Color.rgb(237, 233, 254)  // #ede9fe
    };
    
    // 默认颜色(兼容旧代码)
    public final int[] PIE_COLORS = PIE_COLORS_EXPENSE;
    private static PieChartUtils pieChartUtil;
    private List<PieEntry> entries;

    public static  PieChartUtils getPitChart(){
        if( pieChartUtil==null){
            pieChartUtil=new PieChartUtils();
        }
        return  pieChartUtil;
    }


    public void setPieChart(PieChart pieChart, Map<String, Float> pieValues, String title, boolean showLegend) {
        pieChart.setUsePercentValues(true);//设置使用百分比（后续有详细介绍）
        pieChart.getDescription().setEnabled(false);//设置描述
        pieChart.setRotationEnabled(true);//是否可以旋转
        pieChart.setHighlightPerTapEnabled(true);//点击是否放大
        pieChart.setDrawCenterText(true);//设置绘制环中文字
        pieChart.setDrawEntryLabels(true);
        //这个方法为true就是环形图，为false就是饼图
        pieChart.setDrawHoleEnabled(true);//环形

        pieChart.setExtraOffsets(0, 0, 0, 0); //设置边距
        // 0表示摩擦最大，基本上一滑就停
        // 1表示没有摩擦，会自动转化为0.9999,及其顺滑
        pieChart.setDragDecelerationFrictionCoef(0.35f);//设置滑动时的摩擦系数（值越小摩擦系数越大）
        pieChart.setCenterText(title);//设置环中的文字
        pieChart.setEntryLabelColor(Color.rgb(30, 41, 59)); // 深色标签
        pieChart.setCenterTextSize(18f);//设置环中文字的大小(加大)
        pieChart.setCenterTextColor(Color.rgb(100, 116, 139)); // 现代化灰色
        pieChart.setRotationAngle(120f);//设置旋转角度

        pieChart.setTransparentCircleRadius(61f);//设置半透明圆环的半径,看着就有一种立体的感觉
        //设置环形中间空白颜色是白色
        pieChart.setHoleColor(Color.TRANSPARENT);
        //设置半透明圆环的颜色
        pieChart.setTransparentCircleColor(Color.WHITE);
        //设置半透明圆环的透明度
        pieChart.setTransparentCircleAlpha(110);

        //图例设置
        Legend legend = pieChart.getLegend();
        if (showLegend) {
            legend.setEnabled(true);//是否显示图例
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);//图例相对于图表横向的位置
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);//图例相对于图表纵向的位置
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);//图例显示的方向
            legend.setDrawInside(false);
            legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);//方向
        } else {
            legend.setEnabled(false);
        }

        //设置饼图数据
        setPieChartData(pieChart, pieValues);

        pieChart.animateY(1200, Easing.EasingOption.EaseInOutQuad);//数据显示动画(加长动画时间)

    }
    //设置饼图数据
    private void setPieChartData(PieChart pieChart, Map<String, Float> pieValues) {
        //遍历HashMap
        Set set = pieValues.entrySet();
        Iterator it = set.iterator();//得到适配器
        entries=new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            entries.add(new PieEntry(Float.valueOf(entry.getValue().toString()), entry.getKey().toString()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);//设置饼块之间的间隔
        dataSet.setSelectionShift(6f);//设置饼块选中时偏离饼图中心的距离
        dataSet.setColors(PIE_COLORS);//设置饼块的颜色
        dataSet.setValueTextSize(5f);
        //设置数据显示方式有见图
        dataSet.setValueLinePart1OffsetPercentage(80f);//数据连接线距图形片内部边界的距离，为百分数
        dataSet.setValueLinePart1Length(0.3f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setValueLineColor( PIE_COLORS[3]);//设置连接线的颜色
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);//y轴数据显示在饼图内/外
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);//x轴数据显示在饼图内/外

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(13f); // 加大文字
        pieData.setValueTextColor(Color.rgb(30, 41, 59)); // 深色文字

        pieChart.setData(pieData);
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }
    
    // ========== 新增:支持支出/收入颜色选择 ==========
    /**
     * 设置饼状图(支持支出/收入颜色)
     * @param pieChart 饼状图对象
     * @param pieValues 数据
     * @param title 标题
     * @param showLegend 是否显示图例
     * @param isExpense true=支出(粉红渐变), false=收入(青蓝渐变)
     */
    public void setPieChartWithType(PieChart pieChart, Map<String, Float> pieValues, 
                                     String title, boolean showLegend, boolean isExpense) {
        // 先调用原方法设置基础样式
        setPieChart(pieChart, pieValues, title, showLegend);
        
        // 重新设置颜色
        PieData data = pieChart.getData();
        if (data != null) {
            PieDataSet dataSet = (PieDataSet) data.getDataSet(0);
            // 根据类型选择颜色
            int[] colors = isExpense ? PIE_COLORS_EXPENSE : PIE_COLORS_INCOME;
            dataSet.setColors(colors);
            pieChart.invalidate();
        }
    }

}
