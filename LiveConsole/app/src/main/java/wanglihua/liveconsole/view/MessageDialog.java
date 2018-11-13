package wanglihua.liveconsole.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

public class MessageDialog {
	private static ProgressDialog prDialog;
	
	public final static void show(Context mContext, String message){
		prDialog = new ProgressDialog(mContext, AlertDialog.THEME_HOLO_LIGHT);
		prDialog.setMessage(message);
		prDialog.setIndeterminate(true);
		prDialog.setCancelable(true);
		prDialog.show();
	}

	
	public  final static void dismiss()
	{
		if (prDialog!=null&&prDialog.isShowing()) {
			prDialog.dismiss();
		}
	}
}
