package com.ec.easylibrary.dialog.confirm;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ec.easylibrary.R;

/**
 * Created by Administrator on 2018/4/24.
 */

public class ConfirmDialog extends Dialog {

    public ConfirmDialog(Context context, int width, int height, String title, String message, OnClickLisenter onClickLisenter) {
        this(context, width, height, title, message, R.layout.dialog_layout_confirm, R.style.defaultDialog, onClickLisenter);
    }

    public ConfirmDialog(Context context, int width, int height, String title, String message, int layoutRes, int style, final OnClickLisenter onClickLisenter) {
        super(context, style);
        View view = LayoutInflater.from(context).inflate(layoutRes, null);
        setContentView(view);
        LinearLayout positive = view.findViewById(R.id.llPositive);
        LinearLayout nagetive = view.findViewById(R.id.llNagetive);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        if (!TextUtils.isEmpty(message)) {
            String str = message.replace("\\n", "\n");
//            Log.i("download", "替换之后的字符串：" + str);
            tvMessage.setText(str);

        }
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        if (onClickLisenter != null) {
            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    onClickLisenter.positiveOnClick();
                }
            });
            nagetive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    onClickLisenter.nagetiveOnClick();
                }
            });
        }

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = width;
        params.height = height;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }


    public interface OnClickLisenter {
        void positiveOnClick();

        void nagetiveOnClick();
    }

}
