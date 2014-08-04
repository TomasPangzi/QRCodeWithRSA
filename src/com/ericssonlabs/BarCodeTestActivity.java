package com.ericssonlabs;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.utils.RSAUtils;
import com.zxing.activity.CaptureActivity;
import com.zxing.encoding.EncodingHandler;

public class BarCodeTestActivity extends Activity {
	/** Called when the activity is first created. */
	private TextView resultTextView;
	private EditText qrStrEditText;
	private ImageView qrImgImageView;
	RSAPublicKey pubKey;
	RSAPrivateKey priKey;
	// 模
	private static String modulus = "116624669553338190040387088065711810439407415147864122411635065489179257733774946389966570650910135580845114492524048766984462162246046080803007808162024695716617717171779253693557925037446796751229107712789227550488013775218704124732992557151377614464446282985708455217297489157790463394223927525770392348483";
	// 公钥指数
	private static String public_exponent = "65537";
	// 私钥指数
	private static String private_exponent = "67164577306737313375563877883701738886652640034268208373626230629386342442374045220203980592295973987638084689432460634638365433108816656663379826715585047748794678114594262411575700886303358513883816944511384855574330063612541730690764723835935432845510576638126081899221224410345816330395060631121239597353";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		resultTextView = (TextView) this.findViewById(R.id.tv_scan_result);
		qrStrEditText = (EditText) this.findViewById(R.id.et_qr_string);
		qrImgImageView = (ImageView) this.findViewById(R.id.iv_qr_image);

		// 使用模和指数生成公钥和私钥
		pubKey = RSAUtils.getPublicKey(modulus, public_exponent);
		priKey = RSAUtils.getPrivateKey(modulus, private_exponent);

		Button scanBarCodeButton = (Button) this
				.findViewById(R.id.btn_scan_barcode);
		scanBarCodeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 打开扫描界面扫描条形码或二维码
				Intent openCameraIntent = new Intent(BarCodeTestActivity.this,
						CaptureActivity.class);
				startActivityForResult(openCameraIntent, 0);
			}
		});

		Button generateQRCodeButton = (Button) this
				.findViewById(R.id.btn_add_qrcode);
		generateQRCodeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					String contentString = qrStrEditText.getText().toString();
					if (!contentString.equals("")) {
						// 根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
						String encodedContent = RSAUtils.encryptByPublicKey(
								contentString, pubKey);

						Bitmap qrCodeBitmap = EncodingHandler.createQRCode(
								encodedContent, 350);
						qrImgImageView.setImageBitmap(qrCodeBitmap);
					} else {
						Toast.makeText(BarCodeTestActivity.this,
								"Text can not be empty", Toast.LENGTH_SHORT)
								.show();
					}

				} catch (WriterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 处理扫描结果（在界面上显示）
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			try {
				String decodedContent = RSAUtils.decryptByPrivateKey(
						scanResult, priKey);
				resultTextView.setText(decodedContent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}