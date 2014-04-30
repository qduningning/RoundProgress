package com.znn.roundprogress;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * 带图片的圆环样式的进度环
 * 
 * @author Randy.Zhang
 *
 */
public class ProgressImageView extends View {

	private static final float IMAGE_PADDING_SCALE = 0.1f;

	public static int LEVEL_COLOR_INDEX_RED = 0;
	public static int LEVEL_COLOR_INDEX_BLUE = 1;
	public static int LEVEL_COLOR_INDEX_BROWN = 2;
	public static int LEVEL_COLOR_INDEX_GREEN = 3;
	public static int LEVEL_COLOR_INDEX_NONE = -1;
	
	private static final int[] LEVEL_COLOR_ARRAY = { 0X84F84646, 0X843C86E4,
			0X84EC9041, 0X8448AF21};//红色 蓝色 棕色 绿色

	private int progress = 0, progressColor = 0XFF00FF00; //进度的颜色

	private int level = -1;
	private int[] levelColorArray = LEVEL_COLOR_ARRAY;

	private boolean levelCoverImage = true;

	private String text;

	private float descent;
	private float textHeight;
	private int textColor = 0XFFFFFFFF;
	private float textSize = 22;

	private Context context;

	private Bitmap imageRes;

	private RectF drawProgressRect;
	private RectF drawImageRect;
	private Rect imageRect;

	private Xfermode clipMode;

	private PaintFlagsDrawFilter paintFilter;

	private Paint paint;

	public ProgressImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public ProgressImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public ProgressImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private void init(Context context) {
		this.context = context;

		drawProgressRect = new RectF();//进度的圆环的外切正方形
		drawImageRect = new RectF();
		imageRect = new Rect();

		clipMode = new PorterDuffXfermode(Mode.SRC_IN);//交集并第一个图模式

		paintFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG//抗锯齿
				| Paint.FILTER_BITMAP_FLAG);//线性取样

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(0XFFFFFFFF);
		paint.setFakeBoldText(true);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();

		int smallWidth = 0, drawImageRectWidth = 0;
		if (width > height) {//View中最大的一个正方形的区域
			smallWidth = height;
			drawProgressRect.left = (width - height) / 2;
			drawProgressRect.top = 0;
			drawProgressRect.right = drawProgressRect.left + height;
			drawProgressRect.bottom = drawProgressRect.top + height;
		} else {
			smallWidth = width;
			drawProgressRect.left = 0;
			drawProgressRect.top = (height - width) / 2;
			drawProgressRect.right = drawProgressRect.left + width;
			drawProgressRect.bottom = drawProgressRect.top + width;
		}
		//将内部的图片和外部的圆环之间留一点空隙
		drawImageRectWidth = (int) ((float) smallWidth * (1 - IMAGE_PADDING_SCALE * 2));
		//也是一个正方形的区域
		drawImageRect.left = (width - drawImageRectWidth) / 2;
		drawImageRect.top = (height - drawImageRectWidth) / 2;
		drawImageRect.right = drawImageRect.left + drawImageRectWidth;
		drawImageRect.bottom = drawImageRect.top + drawImageRectWidth;

		//文字配置初始化
		textSize = width / 5;
		paint.setTextSize(textSize);
		FontMetrics fm = paint.getFontMetrics();
		descent = fm.descent;
		textHeight = (int) Math.floor(fm.descent - fm.ascent);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		canvas.setDrawFilter(paintFilter);

		//画圆饼
		if (progress > 0 && progress < 101) {
			paint.setColor(progressColor);
			int sweepAngle = (int) ((float) progress / 100 * 360);
			canvas.drawArc(drawProgressRect, 270, sweepAngle, true, paint);
		}

		if (!levelCoverImage) {//先画遮盖层，也就是在图片下面
			if (level > -1 && level <= levelColorArray.length) {
				paint.setColor(levelColorArray[level]);
				canvas.drawCircle(drawImageRect.left + drawImageRect.width()
						/ 2, drawImageRect.top + drawImageRect.height() / 2,
						drawImageRect.width() / 2, paint);
			}
		}

		if (imageRes != null) {//中间的图片
			int layerNumber = canvas.saveLayer(drawImageRect.left,
					drawImageRect.top,
					drawImageRect.left + drawImageRect.width(),
					drawImageRect.top + drawImageRect.height(), null,
					Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
							| Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
							| Canvas.FULL_COLOR_LAYER_SAVE_FLAG
							| Canvas.CLIP_TO_LAYER_SAVE_FLAG);
			paint.setColor(0XFF00FF00);
			canvas.drawCircle(drawImageRect.left + drawImageRect.width() / 2,
					drawImageRect.top + drawImageRect.height() / 2,
					drawImageRect.width() / 2, paint);
			paint.setXfermode(clipMode);//设置叠加方式 这里是取交集 留下src
			canvas.drawBitmap(imageRes, imageRect, drawImageRect, paint);
			paint.setXfermode(null);
			canvas.restoreToCount(layerNumber);
		}

		if (levelCoverImage) {//后画遮盖层，也就是在图片上面
			if (level > -1 && level <= levelColorArray.length) {
				paint.setColor(levelColorArray[level]);
				canvas.drawCircle(drawImageRect.left + drawImageRect.width()
						/ 2, drawImageRect.top + drawImageRect.height() / 2,
						drawImageRect.width() / 2, paint);
			}
		}

		if (text != null) {//添加文字
			paint.setColor(textColor);
			float textTop = 0;
			float textLeft = 0;
			textTop = drawImageRect.bottom
					- (drawImageRect.height() - textHeight) / 2 - descent;
			textLeft = drawImageRect.left
					+ (drawImageRect.width() - paint.measureText(text)) / 2;
			canvas.drawText(text, textLeft, textTop, paint);
		}
	}

	/**
	 * 设置进度
	 * @param progress
	 */
	public void setProgress(int progress) {
		this.progress = progress;
		this.invalidate();
	}

	/**
	 * level遮盖层的颜色 -1为无
	 * @param level
	 */
	public void setLevel(int level) {
		this.level = level;
		this.invalidate();
	}

	/**
	 * 设置文字
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
		this.invalidate();
	}

	/**
	 * 设置后面的图片
	 * @param imageResId 图片的资源ID
	 */
	public void setImageRes(int imageResId) {
		try {
			imageRes = BitmapFactory.decodeResource(context.getResources(),
					imageResId);
			imageRect = new Rect(0, 0, imageRes.getWidth(),
					imageRes.getHeight());
			this.invalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getProgressColor() {
		return progressColor;
	}

	/**
	 * 设置进度环的颜色
	 * @param progressColor
	 */
	public void setProgressColor(int progressColor) {
		this.progressColor = progressColor;
		this.invalidate();
	}

	public int[] getLevelColorArray() {
		return levelColorArray;
	}

	public void setLevelColorArray(int[] levelColorArray) {
		this.levelColorArray = levelColorArray;
		this.invalidate();
	}

	public int getTextColor() {
		return textColor;
	}

	/**
	 * 设置文字颜色
	 * @param textColor
	 */
	public void setTextColor(int textColor) {
		this.textColor = textColor;
		this.invalidate();
	}

	public float getTextSize() {
		return textSize;
	}

	/**
	 * 设置文字大小
	 * @param textSize
	 */
	public void setTextSize(float textSize) {
		this.textSize = textSize;
		paint.setTextSize(textSize);
		FontMetrics fm = paint.getFontMetrics();
		descent = fm.descent;
		textHeight = (int) Math.floor(fm.descent - fm.ascent);
		this.invalidate();
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		if (paint != null) {
			this.paint = paint;
			this.invalidate();
		}
	}

	public int getProgress() {
		return progress;
	}

	public int getLevel() {
		return level;
	}

	public String getText() {
		return text;
	}

	/**
	 * 设置后面的图片
	 * @param imageRes
	 */
	public void setimageRes(Bitmap imageRes) {
		try {
			this.imageRes = imageRes;
			imageRect = new Rect(0, 0, imageRes.getWidth(),
					imageRes.getHeight());
			this.invalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isLevelCoverImage() {
		return levelCoverImage;
	}

	/**
	 * 设置遮罩层是否遮住图片也就是是否在图片上面
	 * @param levelCoverImage
	 */
	public void setLevelCoverImage(boolean levelCoverImage) {
		this.levelCoverImage = levelCoverImage;
	}

}
