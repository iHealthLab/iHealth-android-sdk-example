package com.ec.easylibrary.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;

import com.ec.easylibrary.R;

public class EditTextWithDel extends AppCompatEditText {
    public static int RATIO_IMG_WIDTH = 2;
    private static final int  MSG_DELAY_TIME = 100;
    private Drawable imgDelete;
    private Context mContext;
    public EditTextWithDel(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public EditTextWithDel(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public EditTextWithDel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MSG_DELAY_TIME:
                    setDrawable();
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    private void init(){
        imgDelete = mContext.getResources().getDrawable(R.drawable.icon_delete_edittext);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
            }
        });
        mHandler.sendEmptyMessageDelayed(MSG_DELAY_TIME,100);
    }
    public void setDrawable(){
        if (length() == 0 || !isEnabled()){
            setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        }else{
            int height = getHeight();
            imgDelete.setBounds(0, 0, 60, 60);
            setCompoundDrawables(null,null,imgDelete,null);
        }
    }

    /**
     *
     * @param event
     * event.getX() 获取相对应自身左上角的X坐标
     * event.getY() 获取相对应自身左上角的Y坐标
     * getWidth() 获取控件的宽度
     * getTotalPaddingRight() 获取删除图标左边缘到控件右边缘的距离
     * getPaddingRight() 获取删除图标右边缘到控件右边缘的距离
     * getWidth() - getTotalPaddingRight() 计算删除图标左边缘到控件左边缘的距离
     * getWidth() - getPaddingRight() 计算删除图标右边缘到控件左边缘的距离
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (imgDelete != null && event.getAction() == MotionEvent.ACTION_UP){
            int x = (int)event.getX();
            int y = (int)event.getY();
            //判断触摸点是否在水平范围内
            boolean isInnerWidth = (x > (getWidth() - getTotalPaddingRight())) &&
                    (x < (getWidth() - getPaddingRight()));
            //获取删除图标的边界，返回一个Rect对象
            Rect rect = imgDelete.getBounds();
            //获取删除图标的高度
            int height = rect.height();
            //计算图标底部到控件底部的距离
            int distance = (getHeight() - height) /2;
            //判断触摸点是否在竖直范围内(可能会有点误差)
            //触摸点的纵坐标在distance到（distance+图标自身的高度）之内，则视为点中删除图标
            boolean isInnerHeight = (y > distance) && (y < (distance + height));

            if(isInnerWidth && isInnerHeight) {
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(enabled) {
            int height = getHeight();
            imgDelete.setBounds(0, 0, 60, 60);
            setCompoundDrawables(null,null,imgDelete,null);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        }
    }
}
