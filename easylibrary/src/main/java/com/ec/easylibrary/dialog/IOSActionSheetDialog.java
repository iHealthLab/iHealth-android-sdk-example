package com.ec.easylibrary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ec.easylibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 仿IOS样式的弹出框
 * <p/>
 * please use {@link IOSActionSheetDialog.Builder} to create a instance ,do not use "new".
 * such as:
 * <pre>
 * new IOSActionSheetDialog.Builder(MainActivity.this)
 *                .setTitle("title")
 *                .addSheetItem("item_name", IOSActionSheetDialog.COLOR_IOS_RED)
 *                .setOnItemClickListener(new IOSActionSheetDialog.OnItemClickListener() {
 *                          @Override
 *                          public void onItemClick(LinearLayout parent, View view, int position) {
 *                                          //do some thing
 *                           }
 *                 })
 *                 .build()
 *                 .show();
 * </pre>
 *
 * @author huburt
 * @date 2017-01-11 14:16
 */
public class IOSActionSheetDialog extends Dialog {
    //IOS版蓝色
    public static final String COLOR_IOS_BLUE = "#037BFF";
    //IOS版红色
    public static final String COLOR_IOS_RED = "#FD4A2E";

    public static final String COLOR_IOS_GREY = "#8F8F8F";

    //title参数
    private int mTitleSize = 13;
    private int mTitleHeight = 45;
    private String mTitleColor = COLOR_IOS_GREY;

    //条目参数
    private int mItemTextSize = 18;
    private int mItemHeight = 45;
    private boolean isItemTextBold = false;

    //底部参数
    private int mCancelHeight = 45;
    private int mCancelTextSize = 18;
    private String mCancelTextColor = COLOR_IOS_BLUE;
    private boolean isCancelTextBold = false;

    //屏幕宽度
    private int mWidth;
    //屏幕高度
    private int mHeight;
    //屏幕密度
    private float mDensity;

    private ScrollView mSvContent;
    private LinearLayout mLvContent;
    private TextView mTvTitle;
    private TextView mTvCancel;

    private OnItemClickListener mOnItemClickListener;

    private List<SheetItem> mSheetItemList;
    private String mTitle;
    private boolean isShowTitle;


    public IOSActionSheetDialog(Context context, List<SheetItem> sheetItemList,
                                String title, OnItemClickListener onItemClickListener,
                                boolean cancelable, boolean canceledOnTouchOutside) {
        //传入style设置window参数
        super(context, R.style.ActionSheetDialogStyle);

        mSheetItemList = sheetItemList;
        mTitle = title;
        mOnItemClickListener = onItemClickListener;
        setCancelable(cancelable);
        setCanceledOnTouchOutside(canceledOnTouchOutside);

        //获取屏幕参数
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        mWidth = outMetrics.widthPixels;
        mHeight = outMetrics.heightPixels;
        mDensity = outMetrics.density;
        init();
    }

    private void init() {
        // 获取Dialog布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_actionsheet, null);
        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(mWidth);

        mSvContent = (ScrollView) view.findViewById(R.id.sLayout_content);
        mLvContent = (LinearLayout) view.findViewById(R.id.lLayout_content);
        mTvTitle = (TextView) view.findViewById(R.id.txt_title);
        mTvCancel = (TextView) view.findViewById(R.id.txt_cancel);
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (!TextUtils.isEmpty(mTitle)) {
            setTitle(mTitle);
        }

        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        setContentView(view);
    }

    @Override
    public void show() {
        adjustUI();
        showSheetItems();
        super.show();
    }

    private void adjustUI() {
        //调整title高度
        LinearLayout.LayoutParams titleParams = (LinearLayout.LayoutParams) mTvTitle.getLayoutParams();
        int height = (int) (mTitleHeight * mDensity + 0.5f);
        titleParams.height = height;
        mTvTitle.setLayoutParams(titleParams);
        //调整title字体大小
        mTvTitle.setTextSize(mTitleSize);
        try {
            int color = Color.parseColor(mTitleColor);
            mTvTitle.setTextColor(color);
        } catch (Exception e) {
            e.printStackTrace();
            mTvTitle.setTextColor(getContext().getResources().getColor(R.color.color_black_60));
        }

        //调整cancel高度
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mTvCancel.getLayoutParams();
        height = (int) (mCancelHeight * mDensity + 0.5f);
        layoutParams.height = height;
        mTvCancel.setLayoutParams(layoutParams);
        //调整cancel字体大小
        mTvCancel.setTextSize(mCancelTextSize);
        try {
            int color = Color.parseColor(mCancelTextColor);
            mTvCancel.setTextColor(color);
        } catch (Exception e) {
            e.printStackTrace();
            mTvCancel.setTextColor(getContext().getResources().getColor(R.color.color_dialog_ios_text_blue));
        }
        //cancel字体是否加粗
        mTvCancel.getPaint().setFakeBoldText(isCancelTextBold);
    }

    private void showSheetItems() {
        if (mSheetItemList == null || mSheetItemList.size() <= 0) {
            return;
        }
        int size = mSheetItemList.size() - 1;

        // 添加条目过多的时候控制高度
        if (size >= 7) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSvContent.getLayoutParams();
            params.height = mHeight / 2;
            mSvContent.setLayoutParams(params);
        }

        for (int i = 0; i <= size; i++) {
            final int index = i;
            SheetItem sheetItem = mSheetItemList.get(i);

            TextView textView = new TextView(getContext());
            textView.setText(sheetItem.name);
            textView.setTextSize(mItemTextSize);
            textView.setGravity(Gravity.CENTER);

            int height = (int) (mItemHeight * mDensity + 0.5f);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));

            textView.getPaint().setFakeBoldText(isItemTextBold);

            // 背景图片
            if (size == 0) {
                if (isShowTitle) {
                    textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
                } else {
                    textView.setBackgroundResource(R.drawable.actionsheet_single_selector);
                }
            } else {
                if (isShowTitle) {
                    if (i >= 0 && i < size) {
                        textView.setBackgroundResource(R.drawable.actionsheet_middle_selector);
                    } else {
                        textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
                    }
                } else {
                    if (i == 0) {
                        textView.setBackgroundResource(R.drawable.actionsheet_top_selector);
                    } else if (i < size) {
                        textView.setBackgroundResource(R.drawable.actionsheet_middle_selector);
                    } else {
                        textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
                    }
                }
            }

            // 字体颜色
            try {
                int color = Color.parseColor(sheetItem.color);
                textView.setTextColor(color);
            } catch (Exception e) {
                e.printStackTrace();
                textView.setTextColor(Color.parseColor(COLOR_IOS_BLUE));
            }

            // 点击事件
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mLvContent, v, index);
                    }
                    dismiss();
                }
            });
            mLvContent.addView(textView);
        }

    }

    public void setTitle(CharSequence title) {
        isShowTitle = true;
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(title);
    }

    public void setTitleColor(String color) {
        mTitleColor = color;
    }

    public void setTitleSize(int size) {
        mTitleSize = size;
    }

    public void setTitleHeight(int height) {
        mTitleHeight = height;
    }

    public void setItemTextSize(int size) {
        mItemTextSize = size;
    }

    public void setItemHeight(int height) {
        mItemHeight = height;
    }

    public void setCancelHeight(int cancelHeight) {
        mCancelHeight = cancelHeight;
    }

    public void setCancelTextSize(int cancelTextSize) {
        mCancelTextSize = cancelTextSize;
    }

    public void setCancelTextColor(String cancelTextColor) {
        mCancelTextColor = cancelTextColor;
    }

    public void setItemTextBold(boolean itemTextBold) {
        isItemTextBold = itemTextBold;
    }

    public void setCancelTextBold(boolean cancelTextBold) {
        isCancelTextBold = cancelTextBold;
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    public static class Builder {
        private android.content.Context context;
        private List<SheetItem> mSheetItemList;
        private OnItemClickListener onItemClickListener;
        private boolean cancelable = true;
        private boolean canceledOnTouchOutside = true;
        private String title;
        private int mTitleSize = 13;
        private int mTitleHeight = 45;
        private String mTitleColor = COLOR_IOS_GREY;
        private int mItemTextSize = 18;
        private int mItemHeight = 45;
        private int mCancelHeight = 45;
        private int mCancelTextSize = 18;
        private String mCancelTextColor = COLOR_IOS_BLUE;
        private boolean isCancelTextBold = false;
        private boolean isItemTextBold = false;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
            return this;
        }

        public Builder addSheetItem(String name, String color) {
            if (mSheetItemList == null) {
                mSheetItemList = new ArrayList<SheetItem>();
            }
            mSheetItemList.add(new SheetItem(name, color));
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public Builder setSheetItemList(List<SheetItem> sheetItemList) {
            mSheetItemList = sheetItemList;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitleSize(int size) {
            mTitleSize = size;
            return this;
        }

        public Builder setTitleHeight(int height) {
            mTitleHeight = height;
            return this;
        }

        public Builder setTitleColor(String color) {
            mTitleColor = color;
            return this;
        }

        public Builder setItemHeight(int height) {
            mItemHeight = height;
            return this;
        }

        public Builder setItemTextSize(int size) {
            mItemTextSize = size;
            return this;
        }

        public Builder setCancelHeight(int cancelHeight) {
            mCancelHeight = cancelHeight;
            return this;
        }

        public Builder setCancelTextSize(int cancelTextSize) {
            mCancelTextSize = cancelTextSize;
            return this;
        }

        public Builder setCancelTextColor(String cancelTextColor) {
            mCancelTextColor = cancelTextColor;
            return this;
        }

        public Builder setCancelTextBold(boolean cancelTextBold) {
            isCancelTextBold = cancelTextBold;
            return this;
        }

        public Builder setItemTextBold(boolean itemTextBold) {
            isItemTextBold = itemTextBold;
            return this;
        }

        public IOSActionSheetDialog build() {
            IOSActionSheetDialog sheetDialog = new IOSActionSheetDialog(context, mSheetItemList, title, onItemClickListener, cancelable, canceledOnTouchOutside);
            sheetDialog.setTitleHeight(mTitleHeight);
            sheetDialog.setTitleSize(mTitleSize);
            sheetDialog.setTitleColor(mTitleColor);
            sheetDialog.setItemHeight(mItemHeight);
            sheetDialog.setItemTextSize(mItemTextSize);
            sheetDialog.setCancelHeight(mCancelHeight);
            sheetDialog.setCancelTextSize(mCancelTextSize);
            sheetDialog.setCancelTextColor(mCancelTextColor);
            sheetDialog.setItemTextBold(isItemTextBold);
            sheetDialog.setCancelTextBold(isCancelTextBold);
            return sheetDialog;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(LinearLayout parent, View view, int position);
    }

    public static class SheetItem {
        String name;
        String color;

        public SheetItem(String name, String color) {
            this.name = name;
            this.color = color;
        }
    }
}
