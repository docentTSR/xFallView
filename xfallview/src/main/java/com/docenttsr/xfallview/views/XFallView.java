package com.docentTSR.xFallView.views;

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

import com.docentTSR.xFallView.R;
import com.docentTSR.xFallView.models.XViewModel;
import com.docentTSR.xFallView.utils.DisplayUtils;
import com.docentTSR.xFallView.utils.RandomUtil;

import java.util.ArrayList;
import java.util.List;


public class XFallView extends View {

    private static final String CALCULATE_POSITIONS_THREAD_NAME = "Calculate-View-Positions-Thread";

    private static final int MSG_CALCULATE = 0;

    private static final int INVALID_RESOURCE_ID = -1;

    private static final long INVALID_TIME = Long.MIN_VALUE;

    private static final int DEFAULT_MAX_VIEWS_COUNT = 50;

    private static final int DEFAULT_MIN_VELOCITY_Y = 50;
    private static final int DEFAULT_MAX_VELOCITY_Y = 3 * DEFAULT_MIN_VELOCITY_Y;

    private static final int DEFAULT_MIN_ALPHA = 10;
    private static final int DEFAULT_MAX_ALPHA = 255;

    private static final int DEFAULT_WIND = 0;

    private static final int DEFAULT_ROTATE_OFF = 1;

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

    private List<XViewModel> xViewModelList;

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
    private boolean isRotateOff;
    private List<Bitmap> xViewBitmapList;


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

        for (XViewModel xViewModel : xViewModelList) {
            xViewMatrix.setTranslate(0, 0);

            if (!isRotateOff) {
                xViewMatrix.postRotate(
                        xViewModel.getRotateAngle(),
                        xViewModel.getPivotX(),
                        xViewModel.getPivotY()
                );
            }

            xViewMatrix.postScale(
                    xViewModel.getScale(), xViewModel.getScale(),
                    xViewModel.getPivotX(), xViewModel.getPivotY()
            );

            xViewMatrix.postTranslate(
                    xViewModel.getPosX(), xViewModel.getPosY()
            );

            xViewPaint.setColor(xViewModel.getTransparency());

            canvas.drawBitmap(xViewModel.getBitmap(), xViewMatrix, xViewPaint);
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
        if (isInEditMode()) {
            return;
        }

        parseAttributes(attrs);

        lastTimeMillis = INVALID_TIME;

        initCalculateThread();

        initCalculateHandler();

        xViewModelList = new ArrayList<>(maxViewsCount);

        xViewMatrix = new Matrix();

        xViewPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void parseAttributes(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.XFallView);

        maxViewsCount = array.getInt(R.styleable.XFallView_maxViewsCount, DEFAULT_MAX_VIEWS_COUNT);

        minVelocity = array.getInt(R.styleable.XFallView_minVelocity, DEFAULT_MIN_VELOCITY_Y);
        maxVelocity = array.getInt(R.styleable.XFallView_maxVelocity, DEFAULT_MAX_VELOCITY_Y);

        minAlpha = array.getInt(R.styleable.XFallView_minAlpha, DEFAULT_MIN_ALPHA);
        maxAlpha = array.getInt(R.styleable.XFallView_maxAlpha, DEFAULT_MAX_ALPHA);

        wind = array.getInt(R.styleable.XFallView_wind, DEFAULT_WIND);

        isRotateOff = array.getInt(R.styleable.XFallView_rotate, DEFAULT_ROTATE_OFF) == 1;

        parseScaleFromAttributes(array);

        parseDrawableFromAttributes(array);

        array.recycle();
    }

    private void parseScaleFromAttributes(TypedArray array) {
        TypedValue typedValue = new TypedValue();

        final int minScaleResId = array.getResourceId(R.styleable.XFallView_minScale, INVALID_RESOURCE_ID);
        if (minScaleResId != INVALID_RESOURCE_ID) {

            getResources().getValue(minScaleResId, typedValue, true);

            minScale = typedValue.getFloat();

        } else {
            minScale = DEFAULT_MIN_SCALE;
        }

        final int maxScaleResId = array.getResourceId(R.styleable.XFallView_maxScale, INVALID_RESOURCE_ID);
        if (maxScaleResId != INVALID_RESOURCE_ID) {

            getResources().getValue(maxScaleResId, typedValue, true);

            maxScale = typedValue.getFloat();

        } else {
            maxScale = DEFAULT_MAX_SCALE;
        }
    }

    private void parseDrawableFromAttributes(TypedArray array) {
        xViewBitmapList = new ArrayList<>();

        final int bitmapArrayResId = array.getResourceId(R.styleable.XFallView_srcArray, INVALID_RESOURCE_ID);
        if (bitmapArrayResId != INVALID_RESOURCE_ID) {

            TypedArray bitmapResIdArray = getResources().obtainTypedArray(bitmapArrayResId);

            int bitmapResId;

            for (int i = 0; i < bitmapResIdArray.length(); i++) {
                bitmapResId = bitmapResIdArray.getResourceId(i, INVALID_RESOURCE_ID);

                if (bitmapResId == INVALID_RESOURCE_ID) {
                    continue;
                }

                xViewBitmapList.add(
                        BitmapFactory.decodeResource(
                                getResources(), bitmapResId
                        )
                );
            }

            bitmapResIdArray.recycle();

            if (xViewBitmapList.isEmpty()) {
                throw new IllegalStateException("You must set valid 'app:srcArray' attribute for XFallView");
            }

        } else {
            throw new IllegalStateException("You must set 'app:srcArray' attribute for XFallView");
        }
    }

    private void initCalculateThread() {
        calculatePositionThread = new HandlerThread(
                CALCULATE_POSITIONS_THREAD_NAME + "-" + String.valueOf(System.currentTimeMillis())
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

                    for (XViewModel xViewModel : xViewModelList) {
                        xViewNextPosX = xViewModel.getPosX() + (wind > 0 ? (xViewModel.getVelocityY() / wind) * deltaTime : 0);
                        xViewNextPosY = xViewModel.getPosY() + xViewModel.getVelocityY() * deltaTime;

                        xViewModel.setPosX(xViewNextPosX);
                        xViewModel.setPosY(xViewNextPosY);

                        if (isPositionsOutOfRange(xViewModel.getBitmap(), xViewNextPosX, xViewNextPosY)) {
                            xViewModel.setPosX(
                                    randomPositionX(xViewModel.getBitmap())
                            );

                            xViewModel.setPosY(
                                    resetPositionY(xViewModel.getBitmap())
                            );
                        }

                        if (!isRotateOff) {
                            if (!isRotateAngleOutOfRange(xViewModel.getRotateAngle())) {
                                resetRotateAngleToValidRange(xViewModel);
                            }

                            xViewModel.setRotateAngle(
                                    xViewModel.getRotateAngle() + 1
                            );
                        }
                    }
                }

                lastTimeMillis = currentTimeMillis;
                postInvalidate();
            }
        };
    }

    private boolean isPositionsOutOfRange(Bitmap bitmap, float x, float y) {
        return (x < -bitmap.getWidth() || x > viewportWidth + bitmap.getWidth())
                || y > viewportHeight + bitmap.getHeight();
    }

    private boolean isRotateAngleOutOfRange(int angle) {
        return angle < 0 || angle > 360;
    }

    private void resetRotateAngleToValidRange(XViewModel xViewModel) {
        xViewModel.setRotateAngle(
                xViewModel.getRotateAngle() % 360
        );
    }

    public void startFall() {
        if (!isXFallInited()) {
            generateXViews();
        }

        setVisibility(VISIBLE);
    }

    private boolean isXFallInited() {
        return !xViewModelList.isEmpty();
    }

    private void generateXViews() {
        Bitmap bitmap;
        float pivotX, pivotY;

        for (int index = 0; index < maxViewsCount; index++) {
            bitmap = randomBitmapFromList();

            pivotX = bitmap.getWidth() / 2.f;
            pivotY = bitmap.getHeight() / 2.f;

            XViewModel xViewModel = new XViewModel(
                    bitmap,
                    randomPositionX(bitmap),
                    randomPositionY(bitmap),
                    pivotX,
                    pivotY,
                    randomVelocityY()
            );

            xViewModel.setTransparency(randomTransparency());
            xViewModel.setScale(randomScale());

            if (!isRotateOff) {
                xViewModel.setRotateAngle(randomRotateAngle());
            }

            xViewModelList.add(xViewModel);
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

    private Bitmap randomBitmapFromList() {
        return xViewBitmapList.get(
                RandomUtil.nextInt(0, xViewBitmapList.size())
        );
    }

    private float randomPositionX(Bitmap bitmap) {
        return RandomUtil.nextFloat(viewportWidth + 2 * bitmap.getWidth()) - bitmap.getWidth();
    }

    private float randomPositionY(Bitmap bitmap) {
        return RandomUtil.nextFloat(viewportHeight + 2 * bitmap.getHeight()) - bitmap.getHeight();
    }

    private float resetPositionY(Bitmap bitmap) {
        return -bitmap.getHeight();
    }

    private float randomVelocityY() {
        return RandomUtil.nextFloat(
                DisplayUtils.dpToPx(getContext(), minVelocity),
                DisplayUtils.dpToPx(getContext(), maxVelocity)
        );
    }

    private float randomScale() {
        return RandomUtil.nextFloat(minScale, maxScale);
    }

    private int randomTransparency() {
        return RandomUtil.nextInt(minAlpha, maxAlpha) << 24;
    }

    private int randomRotateAngle() {
        return RandomUtil.nextInt(0, 360);
    }

}
