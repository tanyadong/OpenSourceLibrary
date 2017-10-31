package com.zihao;

import com.zihao.toast.MyToast;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;

/**
 * 主界面
 * 
 * @author zihao
 * 
 */
public class MainActivity extends Activity implements OnClickListener {

	private Button imgAndTextBtn, imgBtn, textBtn;// 分别表示三种Toast
	private static final int SHOW_TIME = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		imgAndTextBtn = (Button) findViewById(R.id.type_one_btn);
		imgBtn = (Button) findViewById(R.id.type_two_btn);
		textBtn = (Button) findViewById(R.id.type_three_btn);

		imgAndTextBtn.setOnClickListener(this);
		imgBtn.setOnClickListener(this);
		textBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.type_one_btn:
			MyToast.makeImgAndTextToast(this,
					getResources().getDrawable(R.drawable.tips_smile),
					"这是一个Toast", SHOW_TIME).show();
			break;
		case R.id.type_two_btn:
			MyToast.makeImgToast(this,
					getResources().getDrawable(R.drawable.tips_smile),
					SHOW_TIME).show();
			break;
		case R.id.type_three_btn:
			MyToast.makeTextToast(this, "这是一个Toast", SHOW_TIME).show();
			break;
		}
	}

}