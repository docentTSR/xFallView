package com.docenttsr.views.xfallview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.docenttsr.views.xfallview.Utils.DisplayUtils;
import com.docenttsr.views.xfallview.Utils.RandomUtil;

import java.util.ArrayList;
import java.util.List;


public class XFallView extends View {

    private static final String CALCULATE_POSITIONS_THREAD_NAME = "Calculate-View-Positions-Thread";

    private static final int MSG_CALCULATE = 0;

    private static final long INVALID_TIME = Long.MIN_VALUE;

    private static final int DEFAULT_MAX_VIEWS_COUNT = 50;

    private static final int DEFAULT_MIN_VELOCITY_Y = 50;
    private static final int DEFAULT_MAX_VELOCITY_Y = 3 * DEFAULT_MIN_VELOCITY_Y;

    private static final int DEFAULT_MIN_ALPHA = 10;
    private static final int DEFAULT_MAX_ALPHA = 255;

    private static final int DEFAULT_WIND = 0;

    private static final float DEFAULT_MIN_SCALE = .5f;
    private static final float DEFAULT_MAX_SCALE = 1.2f;

    private int viewportWidth;
    private int viewportHeight;

    private float xViewNextPosX;
    private float xViewNextPosY;

    private long currentTimeMillis;
    private long lastTimeMillis;

    private Paint xViewPaint;
    private Matrix xViewMatrix;

    private List<XView> xViewsList;

    private Handler calculatePositionsHandler;
    private HandlerThread calculatePositionThread;

    // ===========================================================
    // Attrs
    // ===========================================================

    private int maxViewsCount;
    private int minVelocity;
    private int maxVelocity;
    private int minAlpha;
    private int maxAlpha;
    private float wind;
    private float minScale;
    private float maxScale;
    private Bitmap xViewBitmap;


    public XFallView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public XFallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public XFallView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        viewportWidth = getMeasuredWidth();
        viewportHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (XView xView : xViewsList) {
            xViewMatrix.setTranslate(0, 0);

            xViewMatrix.postScale(
                    xView.getScale(), xView.getScale(),
                    xView.getPivotX(), xView.getPivotY()
            );

            xViewMatrix.postTranslate(
                    xView.getPosX(), xView.getPosY()
            );

            xViewPaint.setColor(xView.getTransparency());

            canvas.drawBitmap(xViewBitmap, xViewMatrix, xViewPaint);
        }

        calculatePositionsHandler.sendEmptyMessage(MSG_CALCULATE);
    }

    @Override
    protected void onDetachedFromWindow() {
        notifyCalculateThreadStop();

        calculatePositionThread.quit();

        super.onDetachedFromWindow();
    }

    private void init(AttributeSet attrs) {
        initByAttr(attrs);

        lastTimeMillis = INVALID_TIME;

        initCalculateThread();

        initCalculateHandler();

        xViewsList = new ArrayList<>(maxViewsCount);

        xViewMatrix = new Matrix();

        xViewPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void initByAttr(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.XFallView);

        maxViewsCount = array.getInt(R.styleable.XFallView_maxViewsCount, DEFAULT_MAX_VIEWS_COUNT);

        minVelocity = array.getInt(R.styleable.XFallView_minVelocity, DEFAULT_MIN_VELOCITY_Y);
        maxVelocity = array.getInt(R.styleable.XFallView_maxVelocity, DEFAULT_MAX_VELOCITY_Y);

        minAlpha = array.getInt(R.styleable.XFallView_minAlpha, DEFAULT_MIN_ALPHA);
        maxAlpha = array.getInt(R.styleable.XFallView_maxAlpha, DEFAULT_MAX_ALPHA);

        wind = array.getInt(R.styleable.XFallView_wind, DEFAULT_WIND);

        final int INVALID_RESOURCE_ID = -1;

        final int minScaleResId = array.getResourceId(R.styleable.XFallView_minScale, INVALID_RESOURCE_ID);

        if (minScaleResId != INVALID_RESOURCE_ID) {
            TypedValue minScaleValue = new TypedValue();
            getResources().getValue(minScaleResId, minScaleValue, true);
            minScale = minScaleValue.getFloat();

        } else {
            minScale = DEFAULT_MIN_SCALE;
        }

        final int maxScaleResId = array.getResourceId(R.styleable.XFallView_maxScale, INVALID_RESOURCE_ID);

        if (maxScaleResId != INVALID_RESOURCE_ID) {
            TypedValue maxScaleValue = new TypedValue();
            getResources().getValue(maxScaleResId, maxScaleValue, true);
            maxScale = maxScaleValue.getFloat();

        } else {
            maxScale = DEFAULT_MAX_SCALE;
        }

        final int bitmapResId = array.getResourceId(R.styleable.XFallView_src, INVALID_RESOURCE_ID);

        if (bitmapResId != INVALID_RESOURCE_ID) {
            xViewBitmap = BitmapFactory.decodeResource(
                    getResources(), bitmapResId
            );

        } else {
            throw new IllegalStateException("You must set 'app:src' attribute for XFallView...");
        }

        array.recycle();
    }

    private void initCalculateThread() {
        calculatePositionThread = new HandlerThread(
                CALCULATE_POSITIONS_THREAD_NAME + "-"+ String.valueOf(System.currentTimeMillis())
        );
        calculatePositionThread.start();
    }

    private void initCalculateHandler() {
        calculatePositionsHandler = new Handler(calculatePositionThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                currentTimeMillis = System.currentTimeMillis();

                if (lastTimeMillis != INVALID_TIME) {
                    float deltaTime = (currentTimeMillis - lastTimeMillis) / 1_000.f;

                    for (XView xView : xViewsList) {
                        xViewNextPosX = xView.getPosX() + (wind > 0 ? (xView.getVelocityY() / wind) * deltaTime : 0);
                        xViewNextPosY = xView.getPosY() + xView.getVelocityY() * deltaTime;

                        xView.setPosX(xViewNextPosX);
                        xView.setPosY(xViewNextPosY);

                        if (isOutOfRange(xViewNextPosX, xViewNextPosY)) {
                            xView.setPosX(randomPositionX());
                            xView.setPosY(resetPositionY());
                        }
                    }
                }

                lastTimeMillis = currentTimeMillis;
                postInvalidate();
            }
        };
    }

    private boolean isOutOfRange(float x, float y) {
        return (x < -xViewBitmap.getWidth() || x > viewportWidth + xViewBitmap.getWidth())
                || y > viewportHeight + xViewBitmap.getHeight();
    }

    public void startFall() {
        if (!isXFallInited()) {
            generateXViews();
        }

        setVisibility(VISIBLE);
    }

    private boolean isXFallInited() {
        return !xViewsList.isEmpty();
    }

    private void generateXViews() {
        float pivotX = xViewBitmap.getWidth() / 2.f;
        float pivotY = xViewBitmap.getHeight() / 2.f;

        for (int index = 0; index < maxViewsCount; index++) {
            XView xView = new XView.Builder()
                    .setPosX(randomPositionX())
                    .setPosY(randomPositionY())
                    .setPivotX(pivotX)
                    .setPivotY(pivotY)
                    .setVelocityY(randomVelocityY())
                    .setTransparency(randomTransparency())
                    .setScale(randomScale())
                    .create();

            xViewsList.add(xView);
        }
    }

    public void stopFall() {
        setVisibility(INVISIBLE);

        lastTimeMillis = INVALID_TIME;

        notifyCalculateThreadStop();
    }

    private void notifyCalculateThreadStop() {
        calculatePositionsHandler.removeMessages(MSG_CALCULATE);
    }

    // ===========================================================
    // Common
    // ===========================================================

    private float randomPositionX() {
        return RandomUtil.nextFloat(viewportWidth + 2 * xViewBitmap.getWidth()) - xViewBitmap.getWidth();
    }

    private float randomPositionY() {
        return RandomUtil.nextFloat(viewportHeight + 2 * xViewBitmap.getHeight()) - xViewBitmap.getHeight();
    }

    private float resetPositionY() {
        return -xViewBitmap.getHeight();
    }

    private float randomVelocityY() {
        return RandomUtil.nextFloat(
                DisplayUtils.dpToPx(getContext(), minVelocity),
                DisplayUtils.dpToPx(getContext(), maxVelocity)
        );
    }

    private int randomTransparency() {
        return RandomUtil.nextInt(minAlpha, maxAlpha) << 24;
    }

    private float randomScale() {
        return RandomUtil.nextFloat(minScale, maxScale);
    }

}
