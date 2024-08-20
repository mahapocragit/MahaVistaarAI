package com.ai.ai_disc;


import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.math.MathUtils;

public final class zoom
{
    private static final float DEFAULT_ZOOM_FACTOR = 1.0f;

    @NonNull
    private final Rect mCropRegion = new Rect();

    public final float maxZoom;

    @Nullable
    private final Rect mSensorSize;

    public final boolean hasSupport;

    public zoom(@NonNull final CameraCharacteristics characteristics)
    {
        this.mSensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);

        if (this.mSensorSize == null)
        {
            this.maxZoom = zoom.DEFAULT_ZOOM_FACTOR;
            this.hasSupport = false;
            return;
        }

        final Float value = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);

        this.maxZoom = ((value == null) || (value < zoom.DEFAULT_ZOOM_FACTOR))
                ? zoom.DEFAULT_ZOOM_FACTOR
                : value;

        this.hasSupport = (Float.compare(this.maxZoom, zoom.DEFAULT_ZOOM_FACTOR) > 0);
    }

    public void setZoom(@NonNull final CaptureRequest.Builder builder, final float zoom)
    {
        if (this.hasSupport == false)
        {
            return;
        }

        final float newZoom = MathUtils.clamp(zoom, com.ai.ai_disc.zoom.DEFAULT_ZOOM_FACTOR, this.maxZoom);

        final int centerX = this.mSensorSize.width() / 2;
        final int centerY = this.mSensorSize.height() / 2;
        final int deltaX  = (int)((0.5f * this.mSensorSize.width()) / newZoom);
        final int deltaY  = (int)((0.5f * this.mSensorSize.height()) / newZoom);

        this.mCropRegion.set(centerX - deltaX,
                centerY - deltaY,
                centerX + deltaX,
                centerY + deltaY);

        builder.set(CaptureRequest.SCALER_CROP_REGION, this.mCropRegion);
    }
}