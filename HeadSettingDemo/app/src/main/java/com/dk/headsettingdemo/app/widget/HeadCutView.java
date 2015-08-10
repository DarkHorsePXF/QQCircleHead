package com.dk.headsettingdemo.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import com.dk.headsettingdemo.app.R;


public class HeadCutView extends ImageView {

    private PointF mDownPoint;
    private PointF mMiddlePoint;
    private Matrix mMatrix;
    private Matrix mTempMatrix;
    private Bitmap mBitmap;

    private final int MODE_NONE = 0;
    private final int MODE_DRAG = 1;
    private final int MODE_ZOOM = 2;
    private final int MODE_POINTER_UP = 3;
    private int CURR_MODE = MODE_NONE;

    private float mLastDistance;

    private Paint mFrontGroundPaint = new Paint();
    private final int mRadius = 200;
    private Xfermode mXfermode;
    private Rect r;
    private RectF rf;

    private float mCircleCenterX, mCircleCenterY;
    private float mCircleX, mCircleY;
    private boolean isCutImage;

    public HeadCutView(Context context) {
        super(context);
    }

    public HeadCutView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }
    /*
    通过资源id设置图片
     */
    public void setImageResource(int resId) {
        mBitmap = BitmapFactory.decodeResource(getResources(), resId);
        setImageBitmap(mBitmap);
        init();
    }
    /*
    通过Bitmap设置图片
     */
    public void setImageFromBitmap(Bitmap bitmap){
        mBitmap=bitmap;
        setImageBitmap(mBitmap);
        init();
    }



    private void init() {
        mDownPoint = new PointF();
        mMiddlePoint = new PointF();
        mMatrix = new Matrix();
        mTempMatrix = new Matrix();
        mFrontGroundPaint.setColor(getResources().getColor(R.color.front_ground));
        mFrontGroundPaint.setAntiAlias(true);
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

        setScaleType(ScaleType.MATRIX);
        ViewTreeObserver ob = getViewTreeObserver();
        ob.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mCircleCenterX = getWidth() / 2;
                mCircleCenterY = getHeight() / 2;
                mCircleX = mCircleCenterX - mRadius;
                mCircleY = mCircleCenterY - mRadius;
                center();
                getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if(isCutImage) {
            return;
        }
        if (rf == null || rf.isEmpty()) {
            r = new Rect(0, 0, getWidth(), getHeight());
            rf = new RectF(r);
        }
        int sc = canvas.saveLayer(rf, null, Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG | Canvas.ALL_SAVE_FLAG);
        canvas.drawRect(r, mFrontGroundPaint);
        mFrontGroundPaint.setXfermode(mXfermode);
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mRadius, mFrontGroundPaint);
        canvas.restoreToCount(sc);
        mFrontGroundPaint.setXfermode(null);
    }

    public Bitmap clipImage() {
        isCutImage = true;
        Paint paint = new Paint();
        invalidate();
        setDrawingCacheEnabled(true);
        Bitmap bitmap = getDrawingCache().copy(getDrawingCache().getConfig(),
                false);
        setDrawingCacheEnabled(false);
        Bitmap targetBitmap = Bitmap.createBitmap(2 * mRadius, 2 * mRadius,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        RectF dst = new RectF(-bitmap.getWidth() / 2 + mRadius, -getHeight()
                / 2 + mRadius, bitmap.getWidth() - bitmap.getWidth() / 2
                + mRadius, getHeight() - getHeight() / 2 + mRadius);
        canvas.drawBitmap(bitmap, null, dst, paint);
        isCutImage = false;
        return targetBitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float[] values = new float[9];
        mMatrix.getValues(values);
        float left = values[Matrix.MTRANS_X];
        float top = values[Matrix.MTRANS_Y];
        float right = (left + mBitmap.getWidth() * values[Matrix.MSCALE_X]);
        float bottom = (top + mBitmap.getHeight() * values[Matrix.MSCALE_Y]);
        float x = 0f;
        float y = 0f;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                CURR_MODE = MODE_DRAG;
                mDownPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (getDistance(event) > 10f) {
                    CURR_MODE = MODE_ZOOM;
                    midPoint(mMiddlePoint, event);
                    mLastDistance = getDistance(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (CURR_MODE == MODE_DRAG || CURR_MODE == MODE_POINTER_UP) {
                    if (CURR_MODE == MODE_DRAG) {

                        x = event.getX() - mDownPoint.x;
                        y = event.getY() - mDownPoint.y;
                        if (x + left > mCircleX) {
                            x = 0;
                        }
                        if (x + right < mCircleX + 2 * mRadius) {
                            x = 0;
                        }
                        if (y + top > mCircleY) {
                            y = 0;
                        }
                        if (y + bottom < mCircleY + 2 * mRadius) {
                            y = 0;
                        }
                        mMatrix.postTranslate(x, y);
                        mDownPoint.set(event.getX(), event.getY());

                    } else {
                        CURR_MODE = MODE_DRAG;
                        mDownPoint.set(event.getX(), event.getY());
                    }
                } else {
                    float distance = getDistance(event);
                    if (distance > 10f) {
                        float scale = distance / mLastDistance;

                        if (left >= mCircleX) {
                            mMiddlePoint.x = 0;
                        }
                        if (right <= mCircleX + 2 * mRadius) {
                            mMiddlePoint.x = right;
                        }
                        if (top >= mCircleY) {
                            mMiddlePoint.y = 0;
                        }
                        if (bottom <= mCircleY + 2 * mRadius) {
                            mMiddlePoint.y = bottom;
                        }
                        mTempMatrix.set(mMatrix);
                        mTempMatrix.postScale(scale, scale, mMiddlePoint.x, mMiddlePoint.y);

                        float[] temp_values = new float[9];
                        mTempMatrix.getValues(temp_values);
                        float temp_left = temp_values[Matrix.MTRANS_X];
                        float temp_top = temp_values[Matrix.MTRANS_Y];
                        float temp_right = (temp_left + mBitmap.getWidth() * temp_values[Matrix.MSCALE_X]);
                        float temp_bottom = (temp_top + mBitmap.getHeight() * temp_values[Matrix.MSCALE_Y]);
                        if (temp_left > mCircleX || temp_right < mCircleX + 2 * mRadius ||
                                temp_top > mCircleY || temp_bottom < mCircleY + 2 * mRadius) {
                            return true;
                        }
                        mMatrix.postScale(scale, scale, mMiddlePoint.x, mMiddlePoint.y);
                        mLastDistance = getDistance(event);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                CURR_MODE = MODE_NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                CURR_MODE = MODE_POINTER_UP;
                break;
        }
        setImageMatrix(mMatrix);
        return true;
    }

    private float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    protected void center() {

        float height = mBitmap.getHeight();
        float width = mBitmap.getWidth();
        float screenWidth = getWidth();
        float screenHeight = getHeight();
        float scale = 1f;
        if (width >= height) {
            scale = screenWidth / width;
        } else {
            if (height <= screenHeight) {
                scale = screenWidth / width;
            } else {
                scale = screenHeight / height;
            }
        }
        float deltaX = (screenWidth - width * scale) / 2f;
        float deltaY = (screenHeight - height * scale) / 2f;
        mMatrix.postScale(scale, scale);
        mMatrix.postTranslate(deltaX, deltaY);
        setImageMatrix(mMatrix);
    }
}
