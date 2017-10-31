package com.mobile.timeselectlibrary;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.timeselectlibrary.util.DateUtils;
import com.mobile.timeselectlibrary.util.ScreenUtil;
import com.mobile.timeselectlibrary.view.PickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author yuanxueyuan
 * @Title: TimeSelector
 * @Description: 日期选择控件
 * @date 2017/9/12  10:59
 */
public class TimeSelector implements View.OnClickListener {

    private final String TAG = "TimeSelector";

    public enum SCROLLTYPE {
        YEAR(1), MONTH(2), DAY(4), HOUR(8), MINUTE(16), SECOND(32);

        SCROLLTYPE(int value) {
            this.value = value;
        }

        public int value;

    }

    private int scrollUnits = SCROLLTYPE.YEAR.value + SCROLLTYPE.MONTH.value
            + SCROLLTYPE.DAY.value + SCROLLTYPE.HOUR.value
            + SCROLLTYPE.MINUTE.value + SCROLLTYPE.SECOND.value;
    private Context context;
    private String dateFormat;

    private Dialog seletorDialog;
    //选择器
    private PickerView yearPicker, monthPicker, dayPicker, hourPicker, minutePicker, secondPicker;

    //文字
    private TextView yearText, monthText, dayText, hourText, minuteText, secondText, cancelText, sureText, titleText;
    private ImageView cancelImg, sureImg;

    private final int MAX_MINUTE = 59;
    private final int MINMINUTE = 0;
    private final int MAXSECOND = 59;
    private int MAXHOUR = 23;
    private int MINHOUR = 0;
    private final int MAXMONTH = 12;//最大的月份

    private int curYear, curMonth, curDay, curHour, curMinute, curSecond;
    private TextView tvDate;

    private TimeSelectDelegate delegate;

    private ArrayList<String> year, month, day, hour, minute, second;

    private int startYear, startMonth, startDay, startHour, startMinute, startSecond,
            endYear, endMonth, endDay, endHour, endMinute, endSecond,
            minute_workStart, minute_workEnd, second_wordStart,
            second_workEnd, hour_workStart, hour_workEnd;
    private boolean spanYear, spanMon, spanDay, spanHour, spanMin, spanSecond;
    private Calendar selectedCalender = Calendar.getInstance();
    private final long ANIMATORDELAY = 200L;
    private final long CHANGEDELAY = 90L;
    private Calendar startCalendar;//最小的时间
    private Calendar curCalendar;//当选时间
    private Calendar endCalendar;//最大的时间

    /**
     * @param context
     * @param startDate format："yyyy-MM-dd HH:mm:ss"
     * @param endDate
     */
    public TimeSelector(Context context, String startDate, String endDate, String format) {
        this.context = context;
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        dateFormat = format;
        Date startTime = DateUtils.parse(startDate, format);
        Date endTime = DateUtils.parse(endDate, format);
        if (startTime == null || endTime == null) {
            Log.i(TAG,"startTime == null || endTime == null");
            return;
        }
        startCalendar.setTime(startTime);
        endCalendar.setTime(endTime);
        initDialog();
        initView();
    }

    /**
     * @author yuanxueyuan
     * @Title: setDelegate
     * @Description: 设置代理
     * @date 2017/4/24 9:04
     */
    public void setTimeSelectDelegate(TimeSelectDelegate delegate) {
        this.delegate = delegate;
    }


    /**
     * @author yuanxueyuan
     * @Title: show
     * @Description: 显示时间控件
     * @date 2017/2/22 10:07
     */
    public void show(final TextView tvDate) {
        this.tvDate = tvDate;
        curCalendar = Calendar.getInstance();
        Date time = null;
        // 如果没有选择时间，时间控件默认选中当前时间
        if (tvDate.getText().toString().isEmpty()) {
            Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            String str = formatter.format(curDate);
            time = DateUtils.parse(str, dateFormat);
            if (time == null) {
                Log.i(TAG,"time == null");
                return;
            }
            curCalendar.setTime(time);
        } else {
            time = DateUtils.parse(tvDate.getText().toString(), dateFormat);
            if (time == null) {
                Log.i(TAG,"time == null");
                return;
            }
            curCalendar.setTime(time);
        }
        initParameter();
        initTime();
        addListener();
        sureText.setOnClickListener(this);
        seletorDialog.show();
    }

    /**
     * @author yuanxueyuan
     * @Title: initDialog
     * @Description: 初始化dialog
     * @date 2017/9/12 13:30
     */
    private void initDialog() {
        if (seletorDialog == null) {
            seletorDialog = new Dialog(context, R.style.time_select_dialog);
            seletorDialog.setCancelable(false);
            seletorDialog.setCanceledOnTouchOutside(true);// 设置点击其他区域消失
            seletorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            seletorDialog.setContentView(R.layout.time_selector_dialog);
            Window window = seletorDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            int width = ScreenUtil.getInstance(context).getScreenWidth();
            lp.width = width;
            window.setAttributes(lp);
        }
    }

    /**
     * @author yuanxueyuan
     * @Title: initView
     * @Description: 初始化界面
     * @date 2017/9/12 14:04
     */
    private void initView() {
        //选择器
        yearPicker = (PickerView) seletorDialog.findViewById(R.id.picker_year);
        monthPicker = (PickerView) seletorDialog.findViewById(R.id.picker_month);
        dayPicker = (PickerView) seletorDialog.findViewById(R.id.picker_day);
        hourPicker = (PickerView) seletorDialog.findViewById(R.id.picker_hour);
        minutePicker = (PickerView) seletorDialog.findViewById(R.id.picker_minute);
        secondPicker = (PickerView) seletorDialog.findViewById(R.id.picker_second);

        //文字
        yearText = (TextView) seletorDialog.findViewById(R.id.text_year);
        monthText = (TextView) seletorDialog.findViewById(R.id.text_month);
        dayText = (TextView) seletorDialog.findViewById(R.id.text_day);
        hourText = (TextView) seletorDialog.findViewById(R.id.text_hour);
        minuteText = (TextView) seletorDialog.findViewById(R.id.text_minute);
        secondText = (TextView) seletorDialog.findViewById(R.id.text_second);

        //title
        cancelText = (TextView) seletorDialog.findViewById(R.id.text_cancel);
        cancelImg = (ImageView) seletorDialog.findViewById(R.id.img_cancel);
        titleText = (TextView) seletorDialog.findViewById(R.id.text_title);
        sureText = (TextView) seletorDialog.findViewById(R.id.text_sure);
        sureImg = (ImageView) seletorDialog.findViewById(R.id.img_sure);

        setDateVisiable(dateFormat);
    }

    /**
     * @param format 时间格式
     * @author yuanxueyuan
     * @Title: setDateVisiable
     * @Description: 设置年月日时分秒的显示
     * @date 2017/9/12 15:12
     */
    private void setDateVisiable(String format) {
        if (format == null) {
            return;
        }
        boolean haveYear = format.contains("yyyy");
        boolean haveMonth = format.contains("MM");
        boolean haveDay = format.contains("dd");
        boolean haveHour = format.contains("HH");
        boolean haveMinute = format.contains("mm");
        boolean haveSecond = format.contains("ss");
        if (haveYear) {//年显示
            yearPicker.setVisibility(View.VISIBLE);
            yearText.setVisibility(View.VISIBLE);
        }
        if (haveMonth) {//月显示
            monthPicker.setVisibility(View.VISIBLE);
            monthText.setVisibility(View.VISIBLE);
        }
        if (haveDay) {
            dayPicker.setVisibility(View.VISIBLE);
            dayText.setVisibility(View.VISIBLE);
        }
        if (haveHour) {
            hourPicker.setVisibility(View.VISIBLE);
            hourText.setVisibility(View.VISIBLE);
        }
        if (haveMinute) {
            minutePicker.setVisibility(View.VISIBLE);
            minuteText.setVisibility(View.VISIBLE);
        }
        if (haveSecond) {
            secondPicker.setVisibility(View.VISIBLE);
            secondText.setVisibility(View.VISIBLE);
        }
    }


    /**
     * @author yuanxueyuan
     * @Title: initParameter
     * @Description: 初始化参数
     * @date 2017/9/12 13:31
     */
    private void initParameter() {
        //当前时间
        curYear = curCalendar.get(Calendar.YEAR);
        curMonth = curCalendar.get(Calendar.MONTH) + 1;
        curDay = curCalendar.get(Calendar.DAY_OF_MONTH);
        curHour = curCalendar.get(Calendar.HOUR_OF_DAY);
        curMinute = curCalendar.get(Calendar.MINUTE);
        curSecond = curCalendar.get(Calendar.SECOND);

        //开始时间
        startYear = startCalendar.get(Calendar.YEAR);
        startMonth = startCalendar.get(Calendar.MONTH) + 1;
        startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        startMinute = startCalendar.get(Calendar.MINUTE);
        startSecond = startCalendar.get(Calendar.SECOND);

        //结束时间
        endYear = endCalendar.get(Calendar.YEAR);
        endMonth = endCalendar.get(Calendar.MONTH) + 1;
        endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        endMinute = endCalendar.get(Calendar.MINUTE);
        endSecond = endCalendar.get(Calendar.SECOND);

        spanYear = startYear != endYear;
        spanMon = (!spanYear) && (startMonth != endMonth);
        spanDay = (!spanMon) && (startDay != endDay);
        spanHour = (!spanDay) && (startHour != endHour);
        spanMin = (!spanHour) && (startMinute != endMinute);
        spanSecond = (!spanMin) && (startSecond != endSecond);
        selectedCalender.setTime(curCalendar.getTime());
    }

    /**
     * @author yuanxueyuan
     * @Title: initTime
     * @Description: 初始化时间的集合
     * @date 2017/9/12 12:18
     */
    private void initTime() {
        initArrayList();
        if (spanYear) {
            for (int i = startYear; i <= endYear; i++) {
                year.add(String.valueOf(i));
            }
            for (int i = startMonth; i <= MAXMONTH; i++) {
                month.add(formatTimeUnit(i));
            }
            for (int i = startDay; i <= curCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(formatTimeUnit(i));
            }
            for (int i = startHour; i <= MAXHOUR; i++) {
                hour.add(formatTimeUnit(i));
            }
            for (int i = startMinute; i <= MAX_MINUTE; i++) {
                minute.add(formatTimeUnit(i));
            }
            for (int i = startSecond; i <= MAXSECOND; i++) {
                second.add(formatTimeUnit(i));
            }
        } else if (spanMon) {
            year.add(String.valueOf(startYear));
            for (int i = startMonth; i <= endMonth; i++) {
                month.add(formatTimeUnit(i));
            }
            for (int i = startDay; i <= startCalendar
                    .getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(formatTimeUnit(i));
            }
            for (int i = startHour; i <= MAXHOUR; i++) {
                hour.add(formatTimeUnit(i));
            }
            for (int i = startMinute; i <= MAX_MINUTE; i++) {
                minute.add(formatTimeUnit(i));
            }
            for (int i = startSecond; i <= MAXSECOND; i++) {
                second.add(formatTimeUnit(i));
            }
        } else if (spanDay) {
            year.add(String.valueOf(startYear));
            month.add(formatTimeUnit(startMonth));
            for (int i = startDay; i <= endDay; i++) {
                day.add(formatTimeUnit(i));
            }
            for (int i = startHour; i <= MAXHOUR; i++) {
                hour.add(formatTimeUnit(i));
            }
            for (int i = startMinute; i <= MAX_MINUTE; i++) {
                minute.add(formatTimeUnit(i));
            }
            for (int i = startSecond; i <= MAXSECOND; i++) {
                second.add(formatTimeUnit(i));
            }
        } else if (spanHour) {
            year.add(String.valueOf(startYear));
            month.add(formatTimeUnit(startMonth));
            day.add(formatTimeUnit(startDay));
            for (int i = startHour; i <= endHour; i++) {
                hour.add(formatTimeUnit(i));
            }
            for (int i = startMinute; i <= MAX_MINUTE; i++) {
                minute.add(formatTimeUnit(i));
            }
            for (int i = startSecond; i <= MAXSECOND; i++) {
                second.add(formatTimeUnit(i));
            }
        } else if (spanMin) {
            year.add(String.valueOf(startYear));
            month.add(formatTimeUnit(startMonth));
            day.add(formatTimeUnit(startDay));
            hour.add(formatTimeUnit(startHour));
            for (int i = startMinute; i <= endMinute; i++) {
                minute.add(formatTimeUnit(i));
            }
            for (int i = startSecond; i <= MAXSECOND; i++) {
                second.add(formatTimeUnit(i));
            }
        } else if (spanSecond) {
            year.add(String.valueOf(startYear));
            month.add(formatTimeUnit(startMonth));
            day.add(formatTimeUnit(startDay));
            hour.add(formatTimeUnit(startHour));
            minute.add(formatTimeUnit(startSecond));
            for (int i = startSecond; i <= MAXSECOND; i++) {
                second.add(formatTimeUnit(i));
            }
        }
        loadComponent();
    }

    /**
     * @author yuanxueyuan
     * @Title: formatTimeUnit
     * @Description: 格式化显示时间
     * @date 2017/3/1 19:23
     */
    private String formatTimeUnit(int unit) {
        return unit < 10 ? "0" + String.valueOf(unit) : String.valueOf(unit);
    }

    /**
     * @author yuanxueyuan
     * @Title: initArrayList
     * @Description: 初始化集合
     * @date 2017/9/12 12:14
     */
    private void initArrayList() {
        if (year == null)
            year = new ArrayList<String>();
        if (month == null)
            month = new ArrayList<String>();
        if (day == null)
            day = new ArrayList<String>();
        if (hour == null)
            hour = new ArrayList<String>();
        if (minute == null)
            minute = new ArrayList<String>();
        if (second == null)
            second = new ArrayList<String>();
        year.clear();
        month.clear();
        day.clear();
        hour.clear();
        minute.clear();
        second.clear();
    }

    /**
     * @author yuanxueyuan
     * @Title: addListener
     * @Description: 添加监听
     * @date 2017/9/12 13:25
     */
    private void addListener() {
        sureText.setOnClickListener(this);
        sureImg.setOnClickListener(this);
        cancelText.setOnClickListener(this);
        cancelImg.setOnClickListener(this);
        yearPicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.YEAR, Integer.parseInt(text));
                // 设置年
                yearPicker.setmCurrentYearSelected(Integer.parseInt(text));
                // 滑动“年”view时动态刷新其他日期控件
                monthChange();
            }
        });
        monthPicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                // 设置当前选择月
                monthPicker.setmCurrentMonthSelected(Integer.parseInt(text));
                // 设置月份之前将日期置成1号，避免选择日期过大，切换到2月没有当前日期月份自动加1
                selectedCalender.set(Calendar.DATE, 1);
                // 月份值
                selectedCalender.set(Calendar.MONTH, Integer.parseInt(text) - 1);
                dayChange();
            }
        });
        dayPicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.DAY_OF_MONTH,
                        Integer.parseInt(text));
                dayPicker.setmCurrentDaySelected(Integer.parseInt(text));
                curDay = selectedCalender.get(Calendar.DAY_OF_MONTH);
                hourChange();
            }
        });
        hourPicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.HOUR_OF_DAY,
                        Integer.parseInt(text));
                hourPicker.setmCurrentHourSelected(Integer.parseInt(text));
                minuteChange();
            }
        });
        minutePicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.MINUTE, Integer.parseInt(text));
                minutePicker.setmCurrentMinSelected(Integer.parseInt(text));
            }
        });
        secondPicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.SECOND, Integer.parseInt(text));
                secondPicker.setmCurrentMinSelected(Integer.parseInt(text));
            }
        });
    }

    /**
     * @author yuanxueyuan
     * @Title: setCurDate
     * @Description: 设置当前数据
     * @date 2017/9/12 13:25
     */
    private void setCurDate() {
        yearPicker.setSelected(curYear - startYear);
        yearPicker.setmCurrentYearSelected(curYear - startYear);
        monthPicker.setSelected(curMonth - 1);
        monthPicker.setmCurrentMonthSelected(curMonth);
        dayPicker.setSelected(curDay - 1);
        dayPicker.setmCurrentDaySelected(curDay);
        hourPicker.setSelected(curHour);
        hourPicker.setmCurrentHourSelected(curHour);
        minutePicker.setSelected(curMinute);
        minutePicker.setmCurrentMinSelected(curMinute);
        secondPicker.setSelected(curSecond);
        secondPicker.setmCurrentMinSelected(curSecond);
    }

    /**
     * @param color 颜色值
     * @author yuanxueyuan
     * @Title: setColor
     * @Description: 设置颜色值
     * @date 2017/9/12 13:43
     */
    public void setThemeColor(@ColorRes int color) {
        setPickerColor(color);//设置时间选择器的颜色值
        setCancelColor(color);//设置取消的颜色值
        setSureColor(color);//设置确定的颜色值
        setTitleColor(color);//设置title的颜色值
    }

    /**
     * @param color 颜色值
     * @author yuanxueyuan
     * @Title: setPickerColor
     * @Description: 设置时间选择器的颜色值
     * @date 2017/9/12 14:12
     */
    public void setPickerColor(@ColorRes int color) {
        if (yearPicker == null || monthPicker == null || dayPicker == null || hourPicker == null || minutePicker == null || secondPicker == null) {
            return;
        }
        yearPicker.setColor(color);
        monthPicker.setColor(color);
        dayPicker.setColor(color);
        hourPicker.setColor(color);
        minutePicker.setColor(color);
        secondPicker.setColor(color);
    }

    /**
     * @param color 颜色值
     * @author yuanxueyuan
     * @Title: setCancelColor
     * @Description: 设置取消的颜色
     * @date 2017/9/12 14:08
     */
    public void setCancelColor(@ColorRes int color) {
        if (cancelText == null) {
            return;
        }
        setCancelTextVisiable(true);
        cancelText.setTextColor(seletorDialog.getContext().getResources().getColor(color));
    }

    /**
     * @param color 颜色值
     * @author yuanxueyuan
     * @Title: setCancelColor
     * @Description: 设置取消的颜色
     * @date 2017/9/12 14:08
     */
    public void setSureColor(@ColorRes int color) {
        if (sureText == null) {
            return;
        }
        setSureTextVisiable(true);
        sureText.setTextColor(seletorDialog.getContext().getResources().getColor(color));
    }

    /**
     * @param color 颜色值
     * @author yuanxueyuan
     * @Title: setCancelColor
     * @Description: 设置取消的颜色
     * @date 2017/9/12 14:08
     */
    public void setTitleColor(@ColorRes int color) {
        if (titleText == null) {
            return;
        }
        titleText.setTextColor(seletorDialog.getContext().getResources().getColor(color));
    }


    /**
     * @param title 标题文字
     * @author yuanxueyuan
     * @Title: setTitleText
     * @Description: 设置标题文字
     * @date 2017/9/12 14:21
     */
    public void setTitleText(String title) {
        if (titleText == null || title == null || "".equals(title)) {
            return;
        }
        titleText.setText(title);
    }


    /**
     * @param cancel 取消的文字
     * @author yuanxueyuan
     * @Title: setCancelText
     * @Description: 设置取消的文字
     * @date 2017/9/12 14:23
     */
    public void setCancelText(String cancel) {
        if (cancelText == null || cancel == null || "".equals(cancel)) {
            return;
        }
        setCancelTextVisiable(true);
        cancelText.setText(cancel);
    }

    /**
     * @param sure
     * @author yuanxueyuan
     * @Title: setSureText
     * @Description: 设置确定的文字
     * @date 2017/9/12 14:26
     */
    public void setSureText(String sure) {
        if (sureText == null || sure == null || "".equals(sure)) {
            return;
        }
        setSureTextVisiable(true);
        sureText.setText(sure);
    }

    /**
     * @param drawable 图片资源
     * @author yuanxueyuan
     * @Title: setCancelPictur
     * @Description: 设置取消按钮的图片
     * @date 2017/9/12 14:27
     */
    public void setCancelPicture(@DrawableRes int drawable) {
        if (cancelText == null || cancelImg == null) {
            return;
        }
        setCancelTextVisiable(false);
        cancelImg.setImageResource(drawable);
    }

    /**
     * @param drawable 图片资源
     * @author yuanxueyuan
     * @Title: setCancelPictur
     * @Description: 设置取消按钮的图片
     * @date 2017/9/12 14:27
     */
    public void setSurePicture(@DrawableRes int drawable) {
        if (sureText == null || sureImg == null) {
            return;
        }
        setSureTextVisiable(false);
        sureImg.setImageResource(drawable);
    }

    /**
     * @param tag ture 显示取消的文字，隐藏取消的图片
     * @author yuanxueyuan
     * @Title: setCancelTextVisiable
     * @Description: 设置取消文字是否可见
     * @date 2017/9/12 14:35
     */
    private void setCancelTextVisiable(boolean tag) {
        if (tag) {
            cancelText.setVisibility(View.VISIBLE);
            cancelImg.setVisibility(View.GONE);
        } else {
            cancelText.setVisibility(View.GONE);
            cancelImg.setVisibility(View.VISIBLE);
        }
    }


    /**
     * @param tag ture 显示取消的文字，隐藏取消的图片
     * @author yuanxueyuan
     * @Title: setSureTextVisiable
     * @Description: 设置确定文字是否可见
     * @date 2017/9/12 14:35
     */
    private void setSureTextVisiable(boolean tag) {
        if (tag) {
            sureText.setVisibility(View.VISIBLE);
            sureImg.setVisibility(View.GONE);
        } else {
            sureText.setVisibility(View.GONE);
            sureImg.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @author yuanxueyuan
     * @Title: loadComponent
     * @Description: 加载数据
     * @date 2017/9/12 13:24
     */
    private void loadComponent() {
        yearPicker.setData(year);
        monthPicker.setData(month);
        dayPicker.setData(day);
        hourPicker.setData(hour);
        minutePicker.setData(minute);
        secondPicker.setData(second);
        setCurDate();
        excuteScroll();
    }

    /**
     * @author yuanxueyuan
     * @Title: excuteScroll
     * @Description: 设置是否能滚动
     * @date 2017/9/12 13:23
     */
    private void excuteScroll() {
        yearPicker.setCanScroll(year.size() > 1
                && (scrollUnits & SCROLLTYPE.YEAR.value) == SCROLLTYPE.YEAR.value);
        monthPicker.setCanScroll(month.size() > 1
                && (scrollUnits & SCROLLTYPE.MONTH.value) == SCROLLTYPE.MONTH.value);
        dayPicker.setCanScroll(day.size() > 1
                && (scrollUnits & SCROLLTYPE.DAY.value) == SCROLLTYPE.DAY.value);
        hourPicker.setCanScroll(hour.size() > 1
                && (scrollUnits & SCROLLTYPE.HOUR.value) == SCROLLTYPE.HOUR.value);
        minutePicker.setCanScroll(minute.size() > 1
                && (scrollUnits & SCROLLTYPE.MINUTE.value) == SCROLLTYPE.MINUTE.value);
        secondPicker.setCanScroll(second.size() > 1
                && (scrollUnits & SCROLLTYPE.SECOND.value) == SCROLLTYPE.SECOND.value);
    }

    /**
     * @author yuanxueyuan
     * @Title: monthChange
     * @Description: 月的改变
     * @date 2017/9/12 10:57
     */
    private void monthChange() {
        month.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        if (selectedYear == startYear) {
            for (int i = startMonth; i <= MAXMONTH; i++) {
                month.add(formatTimeUnit(i));
            }
        } else if (selectedYear == endYear) {
            for (int i = 1; i <= endMonth; i++) {
                month.add(formatTimeUnit(i));
            }
        } else {
            for (int i = 1; i <= MAXMONTH; i++) {
                month.add(formatTimeUnit(i));
            }
        }
        monthPicker.setData(month);
        selectedCalender.set(Calendar.MONTH,
                monthPicker.getmCurrentMonthSelected() - 1);
        // curCalendar.set(Calendar.MONTH,Integer.parseInt(month.get(0))+1);
        monthPicker.setSelected(monthPicker.getmCurrentMonthSelected() - 1);
        monthPicker.postDelayed(new Runnable() {
            @Override
            public void run() {
                dayChange();
            }
        }, CHANGEDELAY);

    }


    /**
     * @author yuanxueyuan
     * @Title: dayChange
     * @Description: 日的改变
     * @date 2017/9/12 10:57
     */
    private void dayChange() {
        day.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        int selectedMonth = selectedCalender.get(Calendar.MONTH);
        if (selectedYear == startYear && selectedMonth == startMonth) {
            for (int i = startDay; i <= selectedCalender
                    .getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(formatTimeUnit(i));
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth) {
            for (int i = 1; i <= endDay; i++) {
                day.add(formatTimeUnit(i));
            }
        } else {
            for (int i = 1; i <= selectedCalender
                    .getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(formatTimeUnit(i));
            }
        }

        dayPicker.setData(day);
        // 如果上一次选择的日 大于当前日的集合，则默认显示第一天
        if (dayPicker.getmCurrentDaySelected() > day.size()) {
            selectedCalender.set(Calendar.DATE, day.size() - 1);
            dayPicker.setSelected(day.size() - 1);
        } else {
            // 否则显示上一次选择的天
            selectedCalender
                    .set(Calendar.DATE, dayPicker.getmCurrentDaySelected());
            dayPicker.setSelected(dayPicker.getmCurrentDaySelected() - 1);
        }
        dayPicker.postDelayed(new Runnable() {
            @Override
            public void run() {
                hourChange();
            }
        }, CHANGEDELAY);
    }


    /**
     * @author yuanxueyuan
     * @Title: hourChange
     * @Description: 小时的改变
     * @date 2017/9/12 10:57
     */
    private void hourChange() {
        hour.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
        int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);

        if (selectedYear == startYear && selectedMonth == startMonth
                && selectedDay == startDay) {
            for (int i = startHour; i <= MAXHOUR; i++) {
                hour.add(formatTimeUnit(i));
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth
                && selectedDay == endDay) {
            for (int i = MINHOUR; i <= endHour; i++) {
                hour.add(formatTimeUnit(i));
            }
        } else {
            for (int i = MINHOUR; i <= MAXHOUR; i++) {
                hour.add(formatTimeUnit(i));
            }

        }
        hourPicker.setData(hour);
        hourPicker.setSelected(hourPicker.getmCurrentHourSelected());
        hourPicker.postDelayed(new Runnable() {
            @Override
            public void run() {
                minuteChange();
            }
        }, CHANGEDELAY);

    }

    /**
     * @author yuanxueyuan
     * @Title: minuteChange
     * @Description: 分钟的改变
     * @date 2017/9/12 10:57
     */
    private void minuteChange() {
        minute.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
        int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);
        int selectedHour = selectedCalender.get(Calendar.HOUR_OF_DAY);

        if (selectedYear == startYear && selectedMonth == startMonth
                && selectedDay == startDay && selectedHour == startHour) {
            for (int i = startMinute; i <= MAX_MINUTE; i++) {
                minute.add(formatTimeUnit(i));
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth
                && selectedDay == endDay && selectedHour == endHour) {
            for (int i = MINMINUTE; i <= endMinute; i++) {
                minute.add(formatTimeUnit(i));
            }
        } else if (selectedHour == hour_workStart) {
            for (int i = minute_workStart; i <= MAX_MINUTE; i++) {
                minute.add(formatTimeUnit(i));
            }
        } else if (selectedHour == hour_workEnd) {
            for (int i = MINMINUTE; i <= minute_workEnd; i++) {
                minute.add(formatTimeUnit(i));
            }
        } else {
            for (int i = MINMINUTE; i <= MAX_MINUTE; i++) {
                minute.add(formatTimeUnit(i));
            }
        }
        minutePicker.setData(minute);
        minutePicker.setSelected(minutePicker.getmCurrentMinSelected());
        excuteScroll();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.text_sure || id == R.id.img_sure) {//确定按钮
            tvDate.setText(DateUtils.format(selectedCalender.getTime(), dateFormat));
            if (this.delegate instanceof TimeSelectDelegate) {
                delegate.onClickSelectSure();
            }
            seletorDialog.dismiss();
        } else if (id == R.id.text_cancel || id == R.id.img_cancel) {//取消按钮
            seletorDialog.dismiss();
        }
    }

    /**
     * @author yuanxueyuan
     * @Title: TimeSelectDelegate
     * @Description: 时间选择器的代理
     * @date 2017/9/12  14:14
     */
    public interface TimeSelectDelegate {
        void onClickSelectSure();// 点击确定选择按钮
    }

}