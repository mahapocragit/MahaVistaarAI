package in.co.appinventor.services_api.widget.holder;

import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;

/* renamed from: in.co.appinventor.services_api.widget.holder.ShapeHolder */
public class ShapeHolder {
    private float alpha = 1.0f;
    private int color;
    private Paint paint;
    private ShapeDrawable shape;

    /* renamed from: x */
    private float f1x = 0.0f;

    /* renamed from: y */
    private float f2y = 0.0f;

    public void setPaint(Paint value) {
        this.paint = value;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setX(float value) {
        this.f1x = value;
    }

    public float getX() {
        return this.f1x;
    }

    public void setY(float value) {
        this.f2y = value;
    }

    public float getY() {
        return this.f2y;
    }

    public void setShape(ShapeDrawable value) {
        this.shape = value;
    }

    public ShapeDrawable getShape() {
        return this.shape;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int value) {
        this.shape.getPaint().setColor(value);
        this.color = value;
    }

    public void setAlpha(float alpha2) {
        this.alpha = alpha2;
        this.shape.setAlpha((int) ((255.0f * alpha2) + 0.5f));
    }

    public float getWidth() {
        return this.shape.getShape().getWidth();
    }

    public void setWidth(float width) {
        Shape s = this.shape.getShape();
        s.resize(width, s.getHeight());
    }

    public float getHeight() {
        return this.shape.getShape().getHeight();
    }

    public void setHeight(float height) {
        Shape s = this.shape.getShape();
        s.resize(s.getWidth(), height);
    }

    public void resizeShape(float width, float height) {
        this.shape.getShape().resize(width, height);
    }

    public ShapeHolder(ShapeDrawable s) {
        this.shape = s;
    }
}
