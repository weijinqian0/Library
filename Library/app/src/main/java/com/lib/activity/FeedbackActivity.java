package com.lib.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.utils.CommonUtils;


public class FeedbackActivity extends Activity implements OnClickListener {

	private TextView mTitleTv;
	private ImageView mBackImg;
	private EditText mContentEdit;
	private EditText mPhoneNumberEdit;
	private Button submit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_feedback);

		init();
		initEvents();
	}

	private void init() {
		mTitleTv = (TextView) findViewById(R.id.title_tv);
		mTitleTv.setText(R.string.feedback);
		mBackImg = (ImageView) findViewById(R.id.back_img);
		mBackImg.setVisibility(View.VISIBLE);

		mContentEdit = (EditText) findViewById(R.id.content_edit);
		mPhoneNumberEdit=(EditText) findViewById(R.id.mobile_number_edit);
		submit = (Button)findViewById(R.id.submit_btn);

	}

	private void initEvents() {
		mBackImg.setOnClickListener(this);
		submit.setOnClickListener(this);
	}

	/* 
	 * ���Ͷ��� 
	 */

	public void sendMessage(String phone, String message){

		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, FeedbackActivity.class), 0);

		SmsManager sms = SmsManager.getDefault();

		sms.sendTextMessage(phone, null, message, pi, null);

	}


	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_img:
				// ���ذ�ť
				this.finish();
				break;
			case R.id.submit_btn:
				// ��������ȷ����ť

				String number = mContentEdit.getText().toString();
				String content = mPhoneNumberEdit.getText().toString();

				if(content.length()==0){
					CommonUtils.startShakeAnim(this, mContentEdit);
					Toast toast=Toast.makeText(this, "��������ȷ����", Toast.LENGTH_SHORT);
					//��ʾtoast��Ϣ
					toast.show();

				}else  {
					Toast toast=Toast.makeText(this, "���ͳɹ�", Toast.LENGTH_SHORT);
					//��ʾtoast��Ϣ
					toast.show();
					String senMessage="����"+number+":"+content;
					sendMessage("15952086148",senMessage);

				}
				break;

			default:
				break;
		}
	}

}
