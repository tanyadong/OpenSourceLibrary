package com.mobile.customdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * auther : zhouweitong
 * date : 2017/9/12 11:50
 * description :
 */

public class CustomDialogs extends Dialog implements View.OnClickListener, TextWatcher {


    private OnCloseListener listener;
    private OnClickEditDialogListener editListener;

    private boolean testbtomdialog;
    private LinearLayout templinearLayout;
    private MyScrollView btomdialogmain;

    private boolean dialogOnclickClose = false;   //点击其他区域关闭弹框
    private Context mContext;

    private TextView titleTxt;          //标题
    private TextView titleTxtSec;          //二级标题
    private TextView titleTxtThr;          //三级标题
    private boolean titleTxtSecBool;          //二级标题
    private boolean titleTxtThrBool;          //三级标题
    private String tempTitleTxtSec;
    private String tempTitleTxtThr;
    private LinearLayout contentLL;     //提示内容布局
    private TextView contentTxt;        //提示内容
    private EditText editText;          //编辑框
    private ImageView clearContent;     //清除内容
    private LinearLayout clearLL;       //清除内容布局

    private View submitView;            //确定点击view
    private View cancelView;            //取消点击view

    private TextView submitTxt;         //positive按钮名字
    private TextView cancelTxt;         //negative按钮名字

    private RelativeLayout cancelRl;    //negative按钮RL
    private View midLineView;

    private String content;             //自定义内容
    private String positiveName;        //自定义positive按钮名字
    private String negativeName;        //自定义negative按钮名字
    private String title;               //自定义标题

    private boolean dialogType;         //是否显示取消按钮标志
    private boolean dialogTypeEdit;     //是否显示编辑框标志
    private int customLayoutId;         //自定义布局id

    private String tempRegEx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(dialogOnclickClose);
        if (customLayoutId != 0) {
            setContentView(customLayoutId);
        } else {
            if (testbtomdialog) {
                Window w = this.getWindow();
                w.getDecorView().setPadding(0, 0, 0, 0);
                w.setGravity(Gravity.BOTTOM);
                w.setWindowAnimations(R.style.dialogstyle);
                WindowManager.LayoutParams lp = w.getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                w.setAttributes(lp);

                setContentView(R.layout.bottombodynew);
                btomdialogmain = (MyScrollView) findViewById(R.id.ll_dialogbtomnew);

                btomdialogmain.addView(templinearLayout);

            } else {
                setContentView(R.layout.mainbody);
                initView();
            }
        }

    }

    private void initView() {
        titleTxt = (TextView) findViewById(R.id.dialog_title);
        titleTxtSec = (TextView) findViewById(R.id.dialog_title_second);
        titleTxtThr = (TextView) findViewById(R.id.dialog_title_third);
        contentTxt = (TextView) findViewById(R.id.dialog_info);
        cancelRl = (RelativeLayout) findViewById(R.id.rl_cancle);
        contentLL = (LinearLayout) findViewById(R.id.ll_dialog_info);
        clearContent = (ImageView) findViewById(R.id.img_clearcontent);
        clearContent.setOnClickListener(this);
        clearLL = (LinearLayout) findViewById(R.id.ll_clearcontent);
        midLineView = findViewById(R.id.view_midline);
        submitView = findViewById(R.id.sure);
        cancelView = findViewById(R.id.no);
        submitTxt = (TextView) findViewById(R.id.text_sure);
        cancelTxt = (TextView) findViewById(R.id.text_no);


        submitView.setOnClickListener(this);
        cancelView.setOnClickListener(this);
        if (!TextUtils.isEmpty(positiveName)) {
            submitTxt.setText(positiveName);
        }

        if (!TextUtils.isEmpty(negativeName)) {
            cancelTxt.setText(negativeName);
        }

        if (!TextUtils.isEmpty(title)) {
            titleTxt.setText(title);
        }

        if (!TextUtils.isEmpty(content)) {
            contentTxt.setText(content);
        }

        if (dialogType) {
            cancelRl.setVisibility(View.VISIBLE);
            midLineView.setVisibility(View.VISIBLE);
        }

        if (dialogTypeEdit) {
            editText.addTextChangedListener(this);
            editText.setMaxLines(1);
            contentLL.addView(editText);
            clearLL.setVisibility(View.VISIBLE);
            if (editText.getText().toString().trim()!=null||!"".equals(editText.getText().toString().trim()))
            {
                clearContent.setVisibility(View.VISIBLE);
            }
            contentTxt.setVisibility(View.GONE);
        }

        if (titleTxtSecBool) {
            titleTxtSec.setVisibility(View.VISIBLE);
            titleTxtSec.setText(tempTitleTxtSec);
        }

        if (titleTxtThrBool) {
            titleTxtThr.setVisibility(View.VISIBLE);
            titleTxtThr.setText(tempTitleTxtThr);
        }

    }

    /**
     * Description: 原始框
     */
    public CustomDialogs(Context context) {
        super(context, R.style.dialog);
        this.mContext = context;
        templinearLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams llmain = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        templinearLayout.setOrientation(LinearLayout.VERTICAL);
        templinearLayout.setLayoutParams(llmain);
    }

    /**
     * @param content String 自定义内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @param type true 是否显示取消按钮
     */
    public void setCancleButton(boolean type) {
        this.dialogType = type;
    }

    /**
     * @param type true 显示编辑框
     */
    public void setEdit(boolean type) {
        EditText customedit = new EditText(mContext);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        customedit.setLayoutParams(ll);
        customedit.setSingleLine();
        customedit.setTextColor(getContext().getResources().getColor(R.color.text));
        WindowManager vm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        customedit.setMaxWidth(vm.getDefaultDisplay().getWidth() * 3 / 5);
        customedit.setBackground(null);
        this.editText = customedit;
        this.dialogTypeEdit = type;
    }

    /**
     * @param title String 二级标题内容
     */
    public void setEditTitleSecond(String title) {
        this.titleTxtSecBool = true;
        tempTitleTxtSec = title;
    }

    /**
     * @param title String 三级标题内容
     */
    public void setEditTitleThird(String title) {
        this.titleTxtThrBool = true;
        tempTitleTxtThr = title;
    }

    public CustomDialogs setEditTitleThirdColor(int color) {
        titleTxtThr.setTextColor(color);
        return this;
    }

    public CustomDialogs setEditTitleThirdSize(int size) {
        titleTxtThr.setTextSize(size);
        return this;
    }

    /**
     * @param type Int 1.普通 2.密码 3.只有字母 4.只有数字 5.只有字母/密码 6.只有数字/密码
     */
    public void setEditType(int type) {
        String digits = "abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (type == 2) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else if (type == 3) {
            editText.setKeyListener(DigitsKeyListener.getInstance(digits));
        } else if (type == 4) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (type == 5) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setKeyListener(DigitsKeyListener.getInstance(digits));
        } else if (type == 6) {
            editText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        }

    }

    /**
     * Description: get编辑框
     */
    public EditText getEdit() {
        return editText;
    }

    /**
     * @param regex String 自定义不能输入哪些内容
     */
    public void setCustomEditType(String regex) {
        this.tempRegEx = regex;
    }

    /**
     * @param textsize int  自定义编辑框字符长度
     */
    public void setEditMaxSize(int textsize) {
        this.editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(textsize)});
    }

    /**
     * @param hint String  自定义编辑框hint
     */
    public void setEditHint(String hint) {
        this.editText.setHint(hint);
    }

    /**
     * @param layoutId Int 自定义布局id
     */
    public void setCustomLayout(int layoutId) {
        this.customLayoutId = layoutId;
    }

    /**
     * Description: 设置标题
     */
    public CustomDialogs setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Description: 原始框listener
     */
    public CustomDialogs setListener(OnCloseListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * Description: 编辑框listener
     */
    public CustomDialogs setEditListener(OnClickEditDialogListener editListener) {
        this.editListener = editListener;
        return this;
    }

    /**
     * Description: 设置 PositiveButton 名字
     */
    public CustomDialogs setPositiveButton(String name) {
        this.positiveName = name;
        return this;
    }

    /**
     * Description: 设置 NegativeButton 名字
     */
    public CustomDialogs setNegativeButton(String name) {
        this.negativeName = name;
        return this;
    }

    /**
     * Description: dialag消失方法
     */
    public void diadismiss() {
        this.dismiss();
    }

    public void setBottomDialogList(String bName, View.OnClickListener btlistener) {
        this.testbtomdialog = true;

        LinearLayout linearLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (50 * mContext.getResources().getDisplayMetrics().density + 0.5f));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(ll);

        LinearLayout.LayoutParams llline = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        View view = new View(mContext);
        view.setLayoutParams(llline);
        view.setBackgroundColor(getContext().getResources().getColor(R.color.gray));
        linearLayout.addView(view);

        LinearLayout.LayoutParams lltext = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView textView = new TextView(mContext);
        textView.setLayoutParams(lltext);
        textView.setText(bName);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getContext().getResources().getColor(R.color.text));
        textView.setOnClickListener(btlistener);
        linearLayout.addView(textView);

        templinearLayout.addView(linearLayout);
    }

    /**
     * @param closeDialog boolean  true 可以点击空白区域关闭弹框
     */
    public void setOnCloseListener(boolean closeDialog) {
        this.dialogOnclickClose = closeDialog;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.no) {
            if (listener != null) {
                listener.onClickNo(this);
            } else if (editListener != null) {
                if (dialogTypeEdit) {
                    editListener.onClickNo(this, editText);
                }
            }

        } else if (i == R.id.sure) {
            if (listener != null) {
                listener.onClickOK(this);
            } else if (editListener != null) {
                if (dialogTypeEdit) {
                    editListener.onClick(this, editText);
                }
            }
        } else if (i == R.id.img_clearcontent ) {
            editText.setText("");
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s == null || "".equals(s.toString())) {
            clearContent.setVisibility(View.GONE);
        } else {
            clearContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (tempRegEx == null || "".equals(tempRegEx)) {
            return;
        }
        String temp = s.toString();
        Pattern p = Pattern.compile(tempRegEx);
        Matcher m = p.matcher(temp);
        if (temp == null || "".equals(temp)) {
            return;
        }
        if (!m.matches()) {
            s.delete(s.length() - 1, s.length());
        }
    }

    public interface OnCloseListener {

        void onClickOK(Dialog dialog);

        void onClickNo(Dialog dialog);

    }

    public interface OnClickEditDialogListener {

        void onClick(Dialog dialog, EditText editText);

        void onClickNo(Dialog dialog, EditText editText);
    }

}
