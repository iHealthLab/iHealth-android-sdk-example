介绍Dialog的调用
具体运行效果 在drawable当中有一个 gif文件 可自行查看
声明方法
  private LoadingDialog hud;

创建方法
//最基础的loading样式
hud = LoadingDialog.create(this)
      .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);

hud = LoadingDialog.create(this)
      .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
      .setLabel("Please wait")
      .setCancellable(true);

hud = LoadingDialog.create(this)
      .setStyle(LoadingDialog.Style.SPIN_INDETERMINATE)
      .setLabel("Please wait")
      .setDetailsLabel("Downloading data");
//圆形 扇形慢慢加载进度
hud = LoadingDialog.create(MainActivity.this)
      .setStyle(LoadingDialog.Style.PIE_DETERMINATE)
      .setLabel("Please wait");

hud = LoadingDialog.create(MainActivity.this)
      .setStyle(LoadingDialog.Style.ANNULAR_DETERMINATE)
      .setLabel("Please wait");

hud = LoadingDialog.create(MainActivity.this)
      .setStyle(LoadingDialog.Style.BAR_DETERMINATE)
      .setLabel("Please wait");

ImageView imageView = new ImageView(this);
       imageView.setImageResource(R.mipmap.ic_launcher);
       hud = LoadingDialog.create(this)
       .setCustomView(imageView)
       .setLabel("This is a custom view");

hud = KProgressHUD.create(this)
       .setStyle(LoadingDialog.Style.SPIN_INDETERMINATE)
       .setDimAmount(0.5f);

hud = KProgressHUD.create(this)
       .setStyle(LoadingDialog.Style.SPIN_INDETERMINATE)
       .setWindowColor(getResources().getColor(R.color.colorPrimary))
       .setAnimationSpeed(2);

调用显示         hud.show();
调用隐藏         hud.dismiss();

提供2个测试代码
   private void simulateProgressUpdate() {
        hud.setMaxProgress(100);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            int currentProgress;
            @Override
            public void run() {
                currentProgress += 1;
                hud.setProgress(currentProgress);
                if (currentProgress < 100) {
                    handler.postDelayed(this, 50);
                }
            }
        }, 100);
    }
    private void scheduleDismiss() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hud.dismiss();
            }
        }, 2000);
    }