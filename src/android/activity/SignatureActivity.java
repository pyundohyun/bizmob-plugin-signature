package com.mcnc.bizmoblite.plugin.signature;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mcnc.bizmoblite.util.BMPGenerator;
import com.mcnc.bizmoblite.util.RUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;


/**
 * @author uyphanha
 * @version 04/28/2017
 */
public class SignatureActivity extends Activity {

    private Paint mPaint;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private String callback = "";
    private String target_path;
    private String orientation = "land";
    private int width = 128;
    private int height = 64;
    private int view_width = 128;
    private int view_height = 64;
    private SignatureActivity.DrawView drawView = null;

    ImageView btnOK, btnCancel;

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnOK) {
                String filePath;
                String rootFile = "";
                SignatureActivity.this.drawView.setDrawingCacheEnabled(true);
                SignatureActivity.this.drawView.buildDrawingCache();
                Bitmap e = SignatureActivity.this.drawView.getDrawingCache();
                Bitmap resize = Bitmap.createScaledBitmap(e, SignatureActivity.this.width, SignatureActivity.this.height, true);

                try {
                    byte[] bmp1 = BMPGenerator.encodeBMP(resize, 1);
                    if (SignatureActivity.this.target_path.length() > 0) {
                        int e1 = SignatureActivity.this.target_path.lastIndexOf('/');
                        if (e1 > 0) {
                            rootFile = SignatureActivity.this.target_path.substring(e1 + 1);
                        }

                        File file;
                        if (e1 > 0) {
                            filePath = SignatureActivity.this.target_path.substring(0, e1);
                            file = new File(filePath);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                        }

                        if (rootFile.length() > 0) {
                            file = new File(SignatureActivity.this.target_path);
                            FileOutputStream fo = new FileOutputStream(file);
                            fo.write(bmp1);
                            fo.close();
                        }
                    }

                    String base64String = Base64.encodeToString(bmp1, Base64.DEFAULT);
                    Intent intentResult = new Intent();
                    intentResult.putExtra("action", "GOTO_SIGNATURE");
                    intentResult.putExtra("callback", SignatureActivity.this.callback);
                    JSONObject root = new JSONObject();
                    root.put("result", true);
                    root.put("sign_data", base64String);
                    root.put("file_path", SignatureActivity.this.target_path);
                    intentResult.putExtra("message", root.toString());
                    SignatureActivity.this.setResult(Activity.RESULT_OK, intentResult);
                    SignatureActivity.this.finish();
                    System.gc();
                } catch (Exception var12) {
                    var12.printStackTrace();
                }
            } else {
                Intent intentResult = new Intent();
                intentResult.putExtra("action", "GOTO_SIGNATURE");
                intentResult.putExtra("callback", SignatureActivity.this.callback);
                JSONObject root = new JSONObject();

                try {
                    root.put("result", false);
                    root.put("sign_data", "");
                    root.put("file_path", "");
                } catch (JSONException var11) {
                    var11.printStackTrace();
                }

                intentResult.putExtra("message", root.toString());
                SignatureActivity.this.setResult(0, intentResult);
                SignatureActivity.this.finish();
                System.gc();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Intent intent = this.getIntent();
        if (intent.hasExtra("target_path")) {
            this.target_path = intent.getStringExtra("target_path");
        } else {
            target_path = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "sign.png";
        }

        if (intent.hasExtra("width")) {
            this.width = intent.getIntExtra("width", width);
        }

        if (intent.hasExtra("height")) {
            this.height = intent.getIntExtra("height", height);
        }

        if (intent.hasExtra("orientation")) {
            this.orientation = intent.getStringExtra("orientation");
        }

        if (this.orientation.startsWith("land")) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (this.orientation.startsWith("portrait")) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (this.orientation.startsWith("none")) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }

        this.fitView(this);
        LinearLayout.LayoutParams emptyParam = new LinearLayout.LayoutParams(10, 10);
        LinearLayout emptyLayout = new LinearLayout(this);
        emptyLayout.setLayoutParams(emptyParam);

        LinearLayout.LayoutParams wrapParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setLayoutParams(wrapParam);

        LinearLayout frameLayout = new LinearLayout(this);
        LinearLayout.LayoutParams frameParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameParam.setMargins(10, 10, 10, 10);
        frameLayout.setOrientation(LinearLayout.VERTICAL);

        rootLayout.addView(frameLayout, frameParam);

        Resources rs = this.getResources();
        Drawable drawable = rs.getDrawable(RUtil.getDrawableR(this, "bizmob_sign_bg"));
        rootLayout.setBackgroundDrawable(drawable);

        LinearLayout.LayoutParams titleParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        titleParam.setMargins(0, 0, 8, 6);
        LinearLayout titleLayout = new LinearLayout(this);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setLayoutParams(titleParam);
        titleLayout.setGravity(5);

        btnOK = new ImageView(this);
        btnCancel = new ImageView(this);
        btnOK.setBackgroundResource(RUtil.getDrawableR(this, "bizmob_sign_ok_btn"));
        btnCancel.setBackgroundResource(RUtil.getDrawableR(this, "bizmob_sign_cancel_btn"));
        btnOK.setOnClickListener(this.onClick);
        btnCancel.setOnClickListener(this.onClick);
        titleLayout.addView(btnOK);
        titleLayout.addView(emptyLayout);
        titleLayout.addView(btnCancel);
        frameLayout.addView(titleLayout, titleParam);
        LinearLayout.LayoutParams bmpParam = new LinearLayout.LayoutParams(this.view_width, this.view_height);
        this.drawView = new SignatureActivity.DrawView(this);
        frameLayout.addView(this.drawView, bmpParam);
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setColor(Color.BLACK);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStrokeWidth(12.0F);
        this.mEmboss = new EmbossMaskFilter(new float[]{1.0F, 1.0F, 1.0F}, 0.4F, 6.0F, 3.5F);
        this.mBlur = new BlurMaskFilter(8.0F, BlurMaskFilter.Blur.NORMAL);
        this.setContentView(rootLayout);
    }

    @SuppressWarnings("deprecation")
    private void fitView(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        int min = width > height ? height : width;
        int max = width > height ? width : height;
        if (this.orientation.equals("land")) {
            if (getScreenDensity() == 160) {
                width = max * 50 / 100;
                height = width * this.height / this.width;
            } else {
                width = max * 75 / 100;
                height = width * this.height / this.width;
            }
        } else if (this.orientation.equals("portrait")) {
            if (this.width > this.height) {
                width = min - 60;
                height = width * this.height / this.width;
            } else {
                height = min - 60;
                width = height * this.width / this.height;
            }
        } else if (this.width > this.height) {
            width = min - 60;
            height = width * this.height / this.width;
        } else {
            height = min - 60;
            width = height * this.width / this.height;
        }

        this.view_width = width;
        this.view_height = height;
    }

    private int getScreenDensity() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) (metrics.density * 160f);
    }

    public class DrawView extends View {
        private static final float MINP = 0.25F;
        private static final float MAXP = 0.75F;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        private float mX;
        private float mY;
        private static final float TOUCH_TOLERANCE = 4.0F;
        private SignatureActivity.DrawView drawView = null;

        public DrawView(Context c) {
            super(c);
            this.mBitmap = Bitmap.createBitmap(SignatureActivity.this.view_width, SignatureActivity.this.view_height, Bitmap.Config.ARGB_8888);
            this.mCanvas = new Canvas(this.mBitmap);
            this.mPath = new Path();
            this.mBitmapPaint = new Paint(4);
        }

        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }

        protected void onDraw(Canvas canvas) {
            canvas.drawColor(-1);
            canvas.drawBitmap(this.mBitmap, 0.0F, 0.0F, this.mBitmapPaint);
            canvas.drawPath(this.mPath, SignatureActivity.this.mPaint);
        }

        private void touch_start(float x, float y) {
            this.mPath.reset();
            this.mPath.moveTo(x, y);
            this.mX = x;
            this.mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - this.mX);
            float dy = Math.abs(y - this.mY);
            if (dx >= 4.0F || dy >= 4.0F) {
                this.mPath.quadTo(this.mX, this.mY, (x + this.mX) / 2.0F, (y + this.mY) / 2.0F);
                this.mX = x;
                this.mY = y;
            }

        }

        private void touch_up() {
            this.mPath.lineTo(this.mX, this.mY);
            this.mCanvas.drawPath(this.mPath, SignatureActivity.this.mPaint);
            this.mPath.reset();
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case 0:
                    this.touch_start(x, y);
                    this.invalidate();
                    break;
                case 1:
                    this.touch_up();
                    this.invalidate();
                    break;
                case 2:
                    this.touch_move(x, y);
                    this.invalidate();
            }

            return true;
        }
    }
}
