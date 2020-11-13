
package net.wavy.qr_reader_gmv_03._02_postlogin.graphic;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import com.google.android.gms.vision.CameraSource;

import net.wavy.qr_reader_gmv_03.R;


public class GraphicOverlay extends ViewGroup
{
	private final Object mLock = new Object();
	private int mPreviewWidth;
	private float mWidthScaleFactor = 1.0f;
	private int mPreviewHeight;
	private float mHeightScaleFactor = 1.0f;
	private int mFacing = CameraSource.CAMERA_FACING_BACK;

	private float left, top, endY;
	private int rectWidth, rectHeight;
	private int frames;
	private boolean invertAnimation;
	private int lineColor, lineWidth;

	public GraphicOverlay(Context context)
	{
		super(context);
	}

	public GraphicOverlay(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public GraphicOverlay(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.graphic_overlay,
				0, 0);
		rectWidth = a.getInteger(R.styleable.graphic_overlay_square_width, getResources().getInteger(R.integer.scanner_rect_width));
		rectHeight = a.getInteger(R.styleable.graphic_overlay_square_height, getResources().getInteger(R.integer.scanner_rect_height));
		lineColor = a.getColor(R.styleable.graphic_overlay_line_color, ContextCompat.getColor(context, R.color.scanner_line));
		lineWidth = a.getInteger(R.styleable.graphic_overlay_line_width, getResources().getInteger(R.integer.line_width));
		frames = a.getInteger(R.styleable.graphic_overlay_line_speed, getResources().getInteger(R.integer.line_speed));
	}

	public float getWidthScaleFactor()
	{
		return mWidthScaleFactor;
	}

	public float getHeightScaleFactor()
	{
		return mHeightScaleFactor;
	}

	public void setCameraInfo(int previewWidth, int previewHeight, int facing)
	{
		synchronized (mLock)
		{
			mPreviewWidth = previewWidth;
			mPreviewHeight = previewHeight;
			mFacing = facing;
		}
		postInvalidate();
	}

	public int dpToPx(int dp)
	{
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight)
	{
		left = (w - dpToPx(rectWidth)) / 2;
		top = (h - dpToPx(rectHeight)) / 2;
		endY = top;
		super.onSizeChanged(w, h, oldWidth, oldHeight);
	}

	@Override
	public boolean shouldDelayChildPressedState()
	{
		return false;
	}

	@Override
	protected void dispatchDraw(Canvas canvas)
	{
		super.dispatchDraw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		Paint paintBox = new Paint();
		paintBox.setAntiAlias(true);

		paintBox.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		RectF rect = new RectF(left, top, dpToPx(rectWidth) + left, dpToPx(rectHeight) + top);
		canvas.drawRect(rect, paintBox);

		Paint borderBox = new Paint();
		borderBox.setStyle(Paint.Style.STROKE);
		borderBox.setStrokeWidth(1); // set stroke width
		borderBox.setColor(Color.parseColor("#f98f5f5f"));
		canvas.drawRect(rect, borderBox);

		Paint line = new Paint();
		line.setColor(lineColor);
		line.setStrokeWidth(Float.valueOf(lineWidth));

		float limitUp = top + (float) frames; // 386.0
		float limitBottom = top + (float) dpToPx(rectHeight) - (float) frames; // 1061.0

		if (endY >= limitBottom)
		{
			invertAnimation = true;
		}
		else if (endY == limitUp)
		{
			invertAnimation = false;
		}

		if (invertAnimation)
		{
			endY -= frames;
		}
		else
		{
			endY += frames;
		}
		canvas.drawLine(left, endY, left + dpToPx(rectWidth), endY, line);
		invalidate();
	}
}
