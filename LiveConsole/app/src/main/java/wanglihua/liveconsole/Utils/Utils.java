package wanglihua.liveconsole.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class Utils {

	public final static String HONG_HEI = "honghei";
	public final static String FEN_HEI = "fenhei";
	public final static String HUANG = "huang";
	public final static String LAN_BAI = "lanbai";
	public final static String LV_BAI = "lvbai";
	public final static String LV_QIN = "lvqin";
	public final static String OTHER = "other";
	/** 获取屏幕的宽度 */
	public final static int getWindowsWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	public final static void setSize(View child,int flag,int mScreenWidth,int width,int height){

		switch (flag){
			case 1:
				LinearLayout.LayoutParams linelay = (LinearLayout.LayoutParams) child.getLayoutParams();
				if(width!=-1){
					linelay.width = width*mScreenWidth/750;
				}
				if(height!=-1){
					linelay.height = height*mScreenWidth/750;
				}
				child.setLayoutParams(linelay);
				break;
			case 2:
				RelativeLayout.LayoutParams rellay = (RelativeLayout.LayoutParams) child.getLayoutParams();
				if(width!=-1){
					rellay.width = width*mScreenWidth/750;
				}
				if(height!=-1){
					rellay.height = height*mScreenWidth/750;
				}
				child.setLayoutParams(rellay);
				break;
			case 3:
				FrameLayout.LayoutParams fralay = (FrameLayout.LayoutParams) child.getLayoutParams();
				if(width!=-1){
					fralay.width = width*mScreenWidth/750;
				}
				if(height!=-1){
					fralay.height = height*mScreenWidth/750;
				}
				child.setLayoutParams(fralay);

				break;
		}

	}

	public final static void setMargins(View child,int flag,int mScreenWidth,int left,int top,int right,int bottom){

		switch (flag){
			case 1:
				LinearLayout.LayoutParams linelay = (LinearLayout.LayoutParams) child.getLayoutParams();
				linelay.setMargins(left*mScreenWidth/750, top*mScreenWidth/750, right*mScreenWidth/750, bottom*mScreenWidth/750);
				child.setLayoutParams(linelay);
				break;
			case 2:
				RelativeLayout.LayoutParams rellay = (RelativeLayout.LayoutParams) child.getLayoutParams();
				rellay.setMargins(left*mScreenWidth/750, top*mScreenWidth/750, right*mScreenWidth/750, bottom*mScreenWidth/750);
				child.setLayoutParams(rellay);
				break;
			case 3:
				FrameLayout.LayoutParams fralay = (FrameLayout.LayoutParams) child.getLayoutParams();
				fralay.setMargins(left*mScreenWidth/750, top*mScreenWidth/750, right*mScreenWidth/750, bottom*mScreenWidth/750);
				child.setLayoutParams(fralay);
				break;
		}

	}

	public final static void saveUseingTitle(Context mContext,String mTitle, String mReporter){
		SharedPreferences sp = mContext.getSharedPreferences("title_info", 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("mTitle",mTitle);
		editor.putString("mReporter",mReporter);
		editor.commit();
	}


	public final static boolean isConnect(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {

					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.v("error", e.toString());
		}
		return false;
	}

	public  final static void AlertDialog(String _title, String _dec,Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle(_title);
		builder.setPositiveButton("确认", null);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(_dec);
		builder.show();
	}

	public final static String key_Base64 = "iLiuzhou";
	@SuppressLint("NewApi")
	public final static String DecryptDoNet(String message) throws Exception {
		byte[] bytesrc = Base64.decode(message.getBytes(), Base64.DEFAULT);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key_Base64.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key_Base64.getBytes("UTF-8"));
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
		byte[] retByte = cipher.doFinal(bytesrc);
		return new String(retByte);
	}

	public static String Md5(String plainText) {
		if (plainText == null)
			return null;
		StringBuffer buf = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			// Log.e("555","result: " + buf.toString());//32λ�ļ���
			// Log.e("555","result: " + buf.toString().substring(8,24));//16λ�ļ���

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buf.toString();
	}

	public static int px2sp(Context context, float pxValue,int mScreenWidth) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue *mScreenWidth/750/ fontScale + 0.5f);
	}
}
