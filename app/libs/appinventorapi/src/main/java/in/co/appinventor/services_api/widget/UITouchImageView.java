package in.co.appinventor.services_api.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.OverScroller;
import android.widget.Scroller;

import androidx.appcompat.widget.AppCompatImageView;

import in.co.appinventor.services_api.util.OrientationHelper;

/* renamed from: in.co.appinventor.services_api.widget.UITouchImageView */
public class UITouchImageView extends AppCompatImageView {
    private static final String DEBUG = "DEBUG";
    private static final float SUPER_MAX_MULTIPLIER = 1.25f;
    private static final float SUPER_MIN_MULTIPLIER = 0.75f;
    /* access modifiers changed from: private */
    public Context context;
    private ZoomVariables delayedZoomVariables;
    /* access modifiers changed from: private */
    public GestureDetector.OnDoubleTapListener doubleTapListener = null;
    /* access modifiers changed from: private */
    public Fling fling;
    private boolean imageRenderedAtLeastOnce;
    /* access modifiers changed from: private */

    /* renamed from: m */
    public float[] f0m;
    /* access modifiers changed from: private */
    public GestureDetector mGestureDetector;
    /* access modifiers changed from: private */
    public ScaleGestureDetector mScaleDetector;
    private ScaleType mScaleType;
    private float matchViewHeight;
    private float matchViewWidth;
    /* access modifiers changed from: private */
    public Matrix matrix;
    /* access modifiers changed from: private */
    public float maxScale;
    /* access modifiers changed from: private */
    public float minScale;
    /* access modifiers changed from: private */
    public float normalizedScale;
    private boolean onDrawReady;
    private float prevMatchViewHeight;
    private float prevMatchViewWidth;
    private Matrix prevMatrix;
    private int prevViewHeight;
    private int prevViewWidth;
    /* access modifiers changed from: private */
    public State state;
    private float superMaxScale;
    private float superMinScale;
    /* access modifiers changed from: private */
    public OnTouchImageViewListener touchImageViewListener = null;
    /* access modifiers changed from: private */
    public OnTouchListener userTouchListener = null;
    /* access modifiers changed from: private */
    public int viewHeight;
    /* access modifiers changed from: private */
    public int viewWidth;

    /* renamed from: in.co.appinventor.services_api.widget.UITouchImageView$OnTouchImageViewListener */
    public interface OnTouchImageViewListener {
        void onMove();
    }

    /* renamed from: in.co.appinventor.services_api.widget.UITouchImageView$State */
    private enum State {
        NONE,
        DRAG,
        ZOOM,
        FLING,
        ANIMATE_ZOOM
    }

    public UITouchImageView(Context context2) {
        super(context2);
        sharedConstructing(context2);
    }

    public UITouchImageView(Context context2, AttributeSet attrs) {
        super(context2, attrs);
        sharedConstructing(context2);
    }

    public UITouchImageView(Context context2, AttributeSet attrs, int defStyle) {
        super(context2, attrs, defStyle);
        sharedConstructing(context2);
    }

    private void sharedConstructing(Context context2) {
        UITouchImageView.super.setClickable(true);
        this.context = context2;
        this.mScaleDetector = new ScaleGestureDetector(context2, new ScaleListener(this, (C00871) null));
        this.mGestureDetector = new GestureDetector(context2, new GestureListener(this, (C00871) null));
        this.matrix = new Matrix();
        this.prevMatrix = new Matrix();
        this.f0m = new float[9];
        this.normalizedScale = 1.0f;
        if (this.mScaleType == null) {
            this.mScaleType = ScaleType.FIT_CENTER;
        }
        this.minScale = 1.0f;
        this.maxScale = 3.0f;
        this.superMinScale = SUPER_MIN_MULTIPLIER * this.minScale;
        this.superMaxScale = SUPER_MAX_MULTIPLIER * this.maxScale;
        setImageMatrix(this.matrix);
        setScaleType(ScaleType.MATRIX);
        setState(State.NONE);
        this.onDrawReady = false;
        UITouchImageView.super.setOnTouchListener(new PrivateOnTouchListener(this, (C00871) null));
    }

    public void setOnTouchListener(OnTouchListener l) {
        this.userTouchListener = l;
    }

    public void setOnTouchImageViewListener(OnTouchImageViewListener l) {
        this.touchImageViewListener = l;
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener l) {
        this.doubleTapListener = l;
    }

    public void setImageResource(int resId) {
        UITouchImageView.super.setImageResource(resId);
        savePreviousImageValues();
        fitImageToView();
    }

    public void setImageBitmap(Bitmap bm) {
        UITouchImageView.super.setImageBitmap(bm);
        savePreviousImageValues();
        fitImageToView();
    }

    public void setImageDrawable(Drawable drawable) {
        UITouchImageView.super.setImageDrawable(drawable);
        savePreviousImageValues();
        fitImageToView();
    }

    public void setImageURI(Uri uri) {
        UITouchImageView.super.setImageURI(uri);
        savePreviousImageValues();
        fitImageToView();
    }

    public ScaleType getScaleType() {
        return this.mScaleType;
    }

    public void setScaleType(ScaleType type) {
        if (type == ScaleType.FIT_START || type == ScaleType.FIT_END) {
            throw new UnsupportedOperationException("TouchImageView does not support FIT_START or FIT_END");
        } else if (type == ScaleType.MATRIX) {
            UITouchImageView.super.setScaleType(ScaleType.MATRIX);
        } else {
            this.mScaleType = type;
            if (this.onDrawReady) {
                setZoom(this);
            }
        }
    }

    public boolean isZoomed() {
        return this.normalizedScale != 1.0f;
    }

    public RectF getZoomedRect() {
        if (this.mScaleType == ScaleType.FIT_XY) {
            throw new UnsupportedOperationException("getZoomedRect() not supported with FIT_XY");
        }
        PointF topLeft = transformCoordTouchToBitmap(0.0f, 0.0f, true);
        PointF bottomRight = transformCoordTouchToBitmap((float) this.viewWidth, (float) this.viewHeight, true);
        float w = (float) getDrawable().getIntrinsicWidth();
        float h = (float) getDrawable().getIntrinsicHeight();
        return new RectF(topLeft.x / w, topLeft.y / h, bottomRight.x / w, bottomRight.y / h);
    }

    private void savePreviousImageValues() {
        if (this.matrix != null && this.viewHeight != 0 && this.viewWidth != 0) {
            this.matrix.getValues(this.f0m);
            this.prevMatrix.setValues(this.f0m);
            this.prevMatchViewHeight = this.matchViewHeight;
            this.prevMatchViewWidth = this.matchViewWidth;
            this.prevViewHeight = this.viewHeight;
            this.prevViewWidth = this.viewWidth;
        }
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", UITouchImageView.super.onSaveInstanceState());
        bundle.putFloat("saveScale", this.normalizedScale);
        bundle.putFloat("matchViewHeight", this.matchViewHeight);
        bundle.putFloat("matchViewWidth", this.matchViewWidth);
        bundle.putInt("viewWidth", this.viewWidth);
        bundle.putInt("viewHeight", this.viewHeight);
        this.matrix.getValues(this.f0m);
        bundle.putFloatArray("matrix", this.f0m);
        bundle.putBoolean("imageRendered", this.imageRenderedAtLeastOnce);
        return bundle;
    }

    public void onRestoreInstanceState(Parcelable state2) {
        if (state2 instanceof Bundle) {
            Bundle bundle = (Bundle) state2;
            this.normalizedScale = bundle.getFloat("saveScale");
            this.f0m = bundle.getFloatArray("matrix");
            this.prevMatrix.setValues(this.f0m);
            this.prevMatchViewHeight = bundle.getFloat("matchViewHeight");
            this.prevMatchViewWidth = bundle.getFloat("matchViewWidth");
            this.prevViewHeight = bundle.getInt("viewHeight");
            this.prevViewWidth = bundle.getInt("viewWidth");
            this.imageRenderedAtLeastOnce = bundle.getBoolean("imageRendered");
            UITouchImageView.super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
            return;
        }
        UITouchImageView.super.onRestoreInstanceState(state2);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.onDrawReady = true;
        this.imageRenderedAtLeastOnce = true;
        if (this.delayedZoomVariables != null) {
            setZoom(this.delayedZoomVariables.scale, this.delayedZoomVariables.focusX, this.delayedZoomVariables.focusY, this.delayedZoomVariables.scaleType);
            this.delayedZoomVariables = null;
        }
        UITouchImageView.super.onDraw(canvas);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        UITouchImageView.super.onConfigurationChanged(newConfig);
        savePreviousImageValues();
    }

    public float getMaxZoom() {
        return this.maxScale;
    }

    public void setMaxZoom(float max) {
        this.maxScale = max;
        this.superMaxScale = SUPER_MAX_MULTIPLIER * this.maxScale;
    }

    public float getMinZoom() {
        return this.minScale;
    }

    public void setMinZoom(float min) {
        this.minScale = min;
        this.superMinScale = SUPER_MIN_MULTIPLIER * this.minScale;
    }

    public float getCurrentZoom() {
        return this.normalizedScale;
    }

    public void resetZoom() {
        this.normalizedScale = 1.0f;
        fitImageToView();
    }

    public void setZoom(float scale) {
        setZoom(scale, 0.5f, 0.5f);
    }

    public void setZoom(float scale, float focusX, float focusY) {
        setZoom(scale, focusX, focusY, this.mScaleType);
    }

    public void setZoom(float scale, float focusX, float focusY, ScaleType scaleType) {
        if (!this.onDrawReady) {
            this.delayedZoomVariables = new ZoomVariables(scale, focusX, focusY, scaleType);
            return;
        }
        if (scaleType != this.mScaleType) {
            setScaleType(scaleType);
        }
        resetZoom();
        scaleImage((double) scale, (float) (this.viewWidth / 2), (float) (this.viewHeight / 2), true);
        this.matrix.getValues(this.f0m);
        this.f0m[2] = -((getImageWidth() * focusX) - (((float) this.viewWidth) * 0.5f));
        this.f0m[5] = -((getImageHeight() * focusY) - (((float) this.viewHeight) * 0.5f));
        this.matrix.setValues(this.f0m);
        fixTrans();
        setImageMatrix(this.matrix);
    }

    public void setZoom(UITouchImageView img) {
        PointF center = img.getScrollPosition();
        setZoom(img.getCurrentZoom(), center.x, center.y, img.getScaleType());
    }

    public PointF getScrollPosition() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return null;
        }
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        PointF point = transformCoordTouchToBitmap((float) (this.viewWidth / 2), (float) (this.viewHeight / 2), true);
        point.x /= (float) drawableWidth;
        point.y /= (float) drawableHeight;
        return point;
    }

    public void setScrollPosition(float focusX, float focusY) {
        setZoom(this.normalizedScale, focusX, focusY);
    }

    /* access modifiers changed from: private */
    public void fixTrans() {
        this.matrix.getValues(this.f0m);
        float transX = this.f0m[2];
        float transY = this.f0m[5];
        float fixTransX = getFixTrans(transX, (float) this.viewWidth, getImageWidth());
        float fixTransY = getFixTrans(transY, (float) this.viewHeight, getImageHeight());
        if (fixTransX != 0.0f || fixTransY != 0.0f) {
            this.matrix.postTranslate(fixTransX, fixTransY);
        }
    }

    /* access modifiers changed from: private */
    public void fixScaleTrans() {
        fixTrans();
        this.matrix.getValues(this.f0m);
        if (getImageWidth() < ((float) this.viewWidth)) {
            this.f0m[2] = (((float) this.viewWidth) - getImageWidth()) / 2.0f;
        }
        if (getImageHeight() < ((float) this.viewHeight)) {
            this.f0m[5] = (((float) this.viewHeight) - getImageHeight()) / 2.0f;
        }
        this.matrix.setValues(this.f0m);
    }

    private float getFixTrans(float trans, float viewSize, float contentSize) {
        float minTrans;
        float maxTrans;
        if (contentSize <= viewSize) {
            minTrans = 0.0f;
            maxTrans = viewSize - contentSize;
        } else {
            minTrans = viewSize - contentSize;
            maxTrans = 0.0f;
        }
        if (trans < minTrans) {
            return (-trans) + minTrans;
        }
        if (trans > maxTrans) {
            return (-trans) + maxTrans;
        }
        return 0.0f;
    }

    /* access modifiers changed from: private */
    public float getFixDragTrans(float delta, float viewSize, float contentSize) {
        if (contentSize <= viewSize) {
            return 0.0f;
        }
        return delta;
    }

    /* access modifiers changed from: private */
    public float getImageWidth() {
        return this.matchViewWidth * this.normalizedScale;
    }

    /* access modifiers changed from: private */
    public float getImageHeight() {
        return this.matchViewHeight * this.normalizedScale;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            setMeasuredDimension(0, 0);
            return;
        }
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        this.viewWidth = setViewSize(widthMode, widthSize, drawableWidth);
        this.viewHeight = setViewSize(heightMode, heightSize, drawableHeight);
        setMeasuredDimension(this.viewWidth, this.viewHeight);
        fitImageToView();
    }

    private void fitImageToView() {
        Drawable drawable = getDrawable();
        if (drawable != null && drawable.getIntrinsicWidth() != 0 && drawable.getIntrinsicHeight() != 0 && this.matrix != null && this.prevMatrix != null) {
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            float scaleX = ((float) this.viewWidth) / ((float) drawableWidth);
            float scaleY = ((float) this.viewHeight) / ((float) drawableHeight);
            switch (C00871.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()]) {
                case OrientationHelper.PORTRAIT:
                    scaleY = 1.0f;
                    scaleX = 1.0f;
                    break;
                case OrientationHelper.LANDSCAPE:
                    scaleY = Math.max(scaleX, scaleY);
                    scaleX = scaleY;
                    break;
                case 3:
                    scaleY = Math.min(1.0f, Math.min(scaleX, scaleY));
                    scaleX = scaleY;
                    break;
                case 4:
                    break;
                case 5:
                    break;
                default:
                    throw new UnsupportedOperationException("TouchImageView does not support FIT_START or FIT_END");
            }
            scaleY = Math.min(scaleX, scaleY);
            scaleX = scaleY;
            float redundantXSpace = ((float) this.viewWidth) - (((float) drawableWidth) * scaleX);
            float redundantYSpace = ((float) this.viewHeight) - (((float) drawableHeight) * scaleY);
            this.matchViewWidth = ((float) this.viewWidth) - redundantXSpace;
            this.matchViewHeight = ((float) this.viewHeight) - redundantYSpace;
            if (isZoomed() || this.imageRenderedAtLeastOnce) {
                if (this.prevMatchViewWidth == 0.0f || this.prevMatchViewHeight == 0.0f) {
                    savePreviousImageValues();
                }
                this.prevMatrix.getValues(this.f0m);
                this.f0m[0] = (this.matchViewWidth / ((float) drawableWidth)) * this.normalizedScale;
                this.f0m[4] = (this.matchViewHeight / ((float) drawableHeight)) * this.normalizedScale;
                float transX = this.f0m[2];
                float transY = this.f0m[5];
                translateMatrixAfterRotate(2, transX, this.prevMatchViewWidth * this.normalizedScale, getImageWidth(), this.prevViewWidth, this.viewWidth, drawableWidth);
                translateMatrixAfterRotate(5, transY, this.prevMatchViewHeight * this.normalizedScale, getImageHeight(), this.prevViewHeight, this.viewHeight, drawableHeight);
                this.matrix.setValues(this.f0m);
            } else {
                this.matrix.setScale(scaleX, scaleY);
                this.matrix.postTranslate(redundantXSpace / 2.0f, redundantYSpace / 2.0f);
                this.normalizedScale = 1.0f;
            }
            fixTrans();
            setImageMatrix(this.matrix);
        }
    }

    /* renamed from: in.co.appinventor.services_api.widget.UITouchImageView$1 */
    static /* synthetic */ class C00871 {
        static final /* synthetic */ int[] $SwitchMap$android$widget$ImageView$ScaleType = new int[ScaleType.values().length];

        static {
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER_CROP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER_INSIDE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_CENTER.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_XY.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    private int setViewSize(int mode, int size, int drawableWidth) {
        switch (mode) {
            case Integer.MIN_VALUE:
                return Math.min(drawableWidth, size);
            case 0:
                return drawableWidth;
            case 1073741824:
                return size;
            default:
                return size;
        }
    }

    private void translateMatrixAfterRotate(int axis, float trans, float prevImageSize, float imageSize, int prevViewSize, int viewSize, int drawableSize) {
        if (imageSize < ((float) viewSize)) {
            this.f0m[axis] = (((float) viewSize) - (((float) drawableSize) * this.f0m[0])) * 0.5f;
        } else if (trans > 0.0f) {
            this.f0m[axis] = -((imageSize - ((float) viewSize)) * 0.5f);
        } else {
            this.f0m[axis] = -((((Math.abs(trans) + (((float) prevViewSize) * 0.5f)) / prevImageSize) * imageSize) - (((float) viewSize) * 0.5f));
        }
    }

    /* access modifiers changed from: private */
    public void setState(State state2) {
        this.state = state2;
    }

    public boolean canScrollHorizontallyFroyo(int direction) {
        return canScrollHorizontally(direction);
    }

    public boolean canScrollHorizontally(int direction) {
        this.matrix.getValues(this.f0m);
        float x = this.f0m[2];
        if (getImageWidth() < ((float) this.viewWidth)) {
            return false;
        }
        if (x >= -1.0f && direction < 0) {
            return false;
        }
        if (Math.abs(x) + ((float) this.viewWidth) + 1.0f < getImageWidth() || direction <= 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void scaleImage(double deltaScale, float focusX, float focusY, boolean stretchImageToSuper) {
        float lowerScale;
        float upperScale;
        if (stretchImageToSuper) {
            lowerScale = this.superMinScale;
            upperScale = this.superMaxScale;
        } else {
            lowerScale = this.minScale;
            upperScale = this.maxScale;
        }
        float origScale = this.normalizedScale;
        this.normalizedScale = (float) (((double) this.normalizedScale) * deltaScale);
        if (this.normalizedScale > upperScale) {
            this.normalizedScale = upperScale;
            deltaScale = (double) (upperScale / origScale);
        } else if (this.normalizedScale < lowerScale) {
            this.normalizedScale = lowerScale;
            deltaScale = (double) (lowerScale / origScale);
        }
        this.matrix.postScale((float) deltaScale, (float) deltaScale, focusX, focusY);
        fixScaleTrans();
    }

    /* access modifiers changed from: private */
    public PointF transformCoordTouchToBitmap(float x, float y, boolean clipToBitmap) {
        this.matrix.getValues(this.f0m);
        float origW = (float) getDrawable().getIntrinsicWidth();
        float origH = (float) getDrawable().getIntrinsicHeight();
        float transX = this.f0m[2];
        float transY = this.f0m[5];
        float finalX = ((x - transX) * origW) / getImageWidth();
        float finalY = ((y - transY) * origH) / getImageHeight();
        if (clipToBitmap) {
            finalX = Math.min(Math.max(finalX, 0.0f), origW);
            finalY = Math.min(Math.max(finalY, 0.0f), origH);
        }
        return new PointF(finalX, finalY);
    }

    /* access modifiers changed from: private */
    public PointF transformCoordBitmapToTouch(float bx, float by) {
        this.matrix.getValues(this.f0m);
        float origW = (float) getDrawable().getIntrinsicWidth();
        return new PointF(this.f0m[2] + (getImageWidth() * (bx / origW)), this.f0m[5] + (getImageHeight() * (by / ((float) getDrawable().getIntrinsicHeight()))));
    }

    /* access modifiers changed from: private */
    @TargetApi(16)
    public void compatPostOnAnimation(Runnable runnable) {
        if (Build.VERSION.SDK_INT >= 16) {
            postOnAnimation(runnable);
        } else {
            postDelayed(runnable, 16);
        }
    }

    private void printMatrixInfo() {
        float[] n = new float[9];
        this.matrix.getValues(n);
        Log.d(DEBUG, "Scale: " + n[0] + " TransX: " + n[2] + " TransY: " + n[5]);
    }

    /* renamed from: in.co.appinventor.services_api.widget.UITouchImageView$GestureListener */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private GestureListener() {
        }

        /* synthetic */ GestureListener(UITouchImageView x0, C00871 x1) {
            this();
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (UITouchImageView.this.doubleTapListener != null) {
                return UITouchImageView.this.doubleTapListener.onSingleTapConfirmed(e);
            }
            return UITouchImageView.this.performClick();
        }

        public void onLongPress(MotionEvent e) {
            UITouchImageView.this.performLongClick();
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (UITouchImageView.this.fling != null) {
                UITouchImageView.this.fling.cancelFling();
            }
            Fling unused = UITouchImageView.this.fling = new Fling((int) velocityX, (int) velocityY);
            UITouchImageView.this.compatPostOnAnimation(UITouchImageView.this.fling);
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        public boolean onDoubleTap(MotionEvent e) {
            boolean consumed = false;
            if (UITouchImageView.this.doubleTapListener != null) {
                consumed = UITouchImageView.this.doubleTapListener.onDoubleTap(e);
            }
            if (UITouchImageView.this.state != State.NONE) {
                return consumed;
            }
            UITouchImageView.this.compatPostOnAnimation(new DoubleTapZoom(UITouchImageView.this.normalizedScale == UITouchImageView.this.minScale ? UITouchImageView.this.maxScale : UITouchImageView.this.minScale, e.getX(), e.getY(), false));
            return true;
        }

        public boolean onDoubleTapEvent(MotionEvent e) {
            if (UITouchImageView.this.doubleTapListener != null) {
                return UITouchImageView.this.doubleTapListener.onDoubleTapEvent(e);
            }
            return false;
        }
    }

    /* renamed from: in.co.appinventor.services_api.widget.UITouchImageView$PrivateOnTouchListener */
    private class PrivateOnTouchListener implements OnTouchListener {
        private PointF last;

        private PrivateOnTouchListener() {
            this.last = new PointF();
        }

        /* synthetic */ PrivateOnTouchListener(UITouchImageView x0, C00871 x1) {
            this();
        }

        public boolean onTouch(View v, MotionEvent event) {
            UITouchImageView.this.mScaleDetector.onTouchEvent(event);
            UITouchImageView.this.mGestureDetector.onTouchEvent(event);
            PointF curr = new PointF(event.getX(), event.getY());
            if (UITouchImageView.this.state == State.NONE || UITouchImageView.this.state == State.DRAG || UITouchImageView.this.state == State.FLING) {
                switch (event.getAction()) {
                    case 0:
                        this.last.set(curr);
                        if (UITouchImageView.this.fling != null) {
                            UITouchImageView.this.fling.cancelFling();
                        }
                        UITouchImageView.this.setState(State.DRAG);
                        break;
                    case OrientationHelper.PORTRAIT:
                    case 6:
                        UITouchImageView.this.setState(State.NONE);
                        break;
                    case OrientationHelper.LANDSCAPE:
                        if (UITouchImageView.this.state == State.DRAG) {
                            float deltaX = curr.x - this.last.x;
                            float deltaY = curr.y - this.last.y;
                            UITouchImageView.this.matrix.postTranslate(UITouchImageView.this.getFixDragTrans(deltaX, (float) UITouchImageView.this.viewWidth, UITouchImageView.this.getImageWidth()), UITouchImageView.this.getFixDragTrans(deltaY, (float) UITouchImageView.this.viewHeight, UITouchImageView.this.getImageHeight()));
                            UITouchImageView.this.fixTrans();
                            this.last.set(curr.x, curr.y);
                            break;
                        }
                        break;
                }
            }
            UITouchImageView.this.setImageMatrix(UITouchImageView.this.matrix);
            if (UITouchImageView.this.userTouchListener != null) {
                UITouchImageView.this.userTouchListener.onTouch(v, event);
            }
            if (UITouchImageView.this.touchImageViewListener == null) {
                return true;
            }
            UITouchImageView.this.touchImageViewListener.onMove();
            return true;
        }
    }

    /* renamed from: in.co.appinventor.services_api.widget.UITouchImageView$ScaleListener */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        /* synthetic */ ScaleListener(UITouchImageView x0, C00871 x1) {
            this();
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            UITouchImageView.this.setState(State.ZOOM);
            return true;
        }

        public boolean onScale(ScaleGestureDetector detector) {
            UITouchImageView.this.scaleImage((double) detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY(), true);
            if (UITouchImageView.this.touchImageViewListener != null) {
                UITouchImageView.this.touchImageViewListener.onMove();
            }
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            UITouchImageView.this.setState(State.NONE);
            boolean animateToZoomBoundary = false;
            float targetZoom = UITouchImageView.this.normalizedScale;
            if (UITouchImageView.this.normalizedScale > UITouchImageView.this.maxScale) {
                targetZoom = UITouchImageView.this.maxScale;
                animateToZoomBoundary = true;
            } else if (UITouchImageView.this.normalizedScale < UITouchImageView.this.minScale) {
                targetZoom = UITouchImageView.this.minScale;
                animateToZoomBoundary = true;
            }
            if (animateToZoomBoundary) {
                UITouchImageView.this.compatPostOnAnimation(new DoubleTapZoom(targetZoom, (float) (UITouchImageView.this.viewWidth / 2), (float) (UITouchImageView.this.viewHeight / 2), true));
            }
        }
    }

    /* renamed from: in.co.appinventor.services_api.widget.UITouchImageView$DoubleTapZoom */
    private class DoubleTapZoom implements Runnable {
        private static final float ZOOM_TIME = 500.0f;
        private float bitmapX;
        private float bitmapY;
        private PointF endTouch;
        private AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        private long startTime;
        private PointF startTouch;
        private float startZoom;
        private boolean stretchImageToSuper;
        private float targetZoom;

        DoubleTapZoom(float targetZoom2, float focusX, float focusY, boolean stretchImageToSuper2) {
            UITouchImageView.this.setState(State.ANIMATE_ZOOM);
            this.startTime = System.currentTimeMillis();
            this.startZoom = UITouchImageView.this.normalizedScale;
            this.targetZoom = targetZoom2;
            this.stretchImageToSuper = stretchImageToSuper2;
            PointF bitmapPoint = UITouchImageView.this.transformCoordTouchToBitmap(focusX, focusY, false);
            this.bitmapX = bitmapPoint.x;
            this.bitmapY = bitmapPoint.y;
            this.startTouch = UITouchImageView.this.transformCoordBitmapToTouch(this.bitmapX, this.bitmapY);
            this.endTouch = new PointF((float) (UITouchImageView.this.viewWidth / 2), (float) (UITouchImageView.this.viewHeight / 2));
        }

        public void run() {
            float t = interpolate();
            UITouchImageView.this.scaleImage(calculateDeltaScale(t), this.bitmapX, this.bitmapY, this.stretchImageToSuper);
            translateImageToCenterTouchPosition(t);
            UITouchImageView.this.fixScaleTrans();
            UITouchImageView.this.setImageMatrix(UITouchImageView.this.matrix);
            if (UITouchImageView.this.touchImageViewListener != null) {
                UITouchImageView.this.touchImageViewListener.onMove();
            }
            if (t < 1.0f) {
                UITouchImageView.this.compatPostOnAnimation(this);
            } else {
                UITouchImageView.this.setState(State.NONE);
            }
        }

        private void translateImageToCenterTouchPosition(float t) {
            float targetX = this.startTouch.x + ((this.endTouch.x - this.startTouch.x) * t);
            float targetY = this.startTouch.y + ((this.endTouch.y - this.startTouch.y) * t);
            PointF curr = UITouchImageView.this.transformCoordBitmapToTouch(this.bitmapX, this.bitmapY);
            UITouchImageView.this.matrix.postTranslate(targetX - curr.x, targetY - curr.y);
        }

        private float interpolate() {
            return this.interpolator.getInterpolation(Math.min(1.0f, ((float) (System.currentTimeMillis() - this.startTime)) / ZOOM_TIME));
        }

        private double calculateDeltaScale(float t) {
            return ((double) (this.startZoom + ((this.targetZoom - this.startZoom) * t))) / ((double) UITouchImageView.this.normalizedScale);
        }
    }

    /* renamed from: in.co.appinventor.services_api.widget.UITouchImageView$Fling */
    private class Fling implements Runnable {
        int currX;
        int currY;
        CompatScroller scroller;

        Fling(int velocityX, int velocityY) {
            int maxX;
            int minX;
            int maxY;
            int minY;
            UITouchImageView.this.setState(State.FLING);
            this.scroller = new CompatScroller(UITouchImageView.this.context);
            UITouchImageView.this.matrix.getValues(UITouchImageView.this.f0m);
            int startX = (int) UITouchImageView.this.f0m[2];
            int startY = (int) UITouchImageView.this.f0m[5];
            if (UITouchImageView.this.getImageWidth() > ((float) UITouchImageView.this.viewWidth)) {
                minX = UITouchImageView.this.viewWidth - ((int) UITouchImageView.this.getImageWidth());
                maxX = 0;
            } else {
                maxX = startX;
                minX = startX;
            }
            if (UITouchImageView.this.getImageHeight() > ((float) UITouchImageView.this.viewHeight)) {
                minY = UITouchImageView.this.viewHeight - ((int) UITouchImageView.this.getImageHeight());
                maxY = 0;
            } else {
                maxY = startY;
                minY = startY;
            }
            this.scroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            this.currX = startX;
            this.currY = startY;
        }

        public void cancelFling() {
            if (this.scroller != null) {
                UITouchImageView.this.setState(State.NONE);
                this.scroller.forceFinished(true);
            }
        }

        public void run() {
            if (UITouchImageView.this.touchImageViewListener != null) {
                UITouchImageView.this.touchImageViewListener.onMove();
            }
            if (this.scroller.isFinished()) {
                this.scroller = null;
            } else if (this.scroller.computeScrollOffset()) {
                int newX = this.scroller.getCurrX();
                int newY = this.scroller.getCurrY();
                int transX = newX - this.currX;
                int transY = newY - this.currY;
                this.currX = newX;
                this.currY = newY;
                UITouchImageView.this.matrix.postTranslate((float) transX, (float) transY);
                UITouchImageView.this.fixTrans();
                UITouchImageView.this.setImageMatrix(UITouchImageView.this.matrix);
                UITouchImageView.this.compatPostOnAnimation(this);
            }
        }
    }

    @TargetApi(9)
    /* renamed from: in.co.appinventor.services_api.widget.UITouchImageView$CompatScroller */
    private class CompatScroller {
        boolean isPreGingerbread;
        OverScroller overScroller;
        Scroller scroller;

        public CompatScroller(Context context) {
            if (Build.VERSION.SDK_INT < 9) {
                this.isPreGingerbread = true;
                this.scroller = new Scroller(context);
                return;
            }
            this.isPreGingerbread = false;
            this.overScroller = new OverScroller(context);
        }

        public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
            if (this.isPreGingerbread) {
                this.scroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            } else {
                this.overScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            }
        }

        public void forceFinished(boolean finished) {
            if (this.isPreGingerbread) {
                this.scroller.forceFinished(finished);
            } else {
                this.overScroller.forceFinished(finished);
            }
        }

        public boolean isFinished() {
            if (this.isPreGingerbread) {
                return this.scroller.isFinished();
            }
            return this.overScroller.isFinished();
        }

        public boolean computeScrollOffset() {
            if (this.isPreGingerbread) {
                return this.scroller.computeScrollOffset();
            }
            this.overScroller.computeScrollOffset();
            return this.overScroller.computeScrollOffset();
        }

        public int getCurrX() {
            if (this.isPreGingerbread) {
                return this.scroller.getCurrX();
            }
            return this.overScroller.getCurrX();
        }

        public int getCurrY() {
            if (this.isPreGingerbread) {
                return this.scroller.getCurrY();
            }
            return this.overScroller.getCurrY();
        }
    }

    /* renamed from: in.co.appinventor.services_api.widget.UITouchImageView$ZoomVariables */
    private class ZoomVariables {
        public float focusX;
        public float focusY;
        public float scale;
        public ScaleType scaleType;

        public ZoomVariables(float scale2, float focusX2, float focusY2, ScaleType scaleType2) {
            this.scale = scale2;
            this.focusX = focusX2;
            this.focusY = focusY2;
            this.scaleType = scaleType2;
        }
    }
}
