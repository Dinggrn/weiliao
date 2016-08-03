package com.Dinggrn.weiliao.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.Dinggrn.weiliao.R;


public class MyTabIcon extends View{
    
	int textColor;//�ı���ͼ�����ɫ
	int textSize;//�ײ����ֵĴ�С
	Drawable drawable;//ʹ�õ�ͼ��
	String textContent;//��������
	//��Ϊֻ�ܻ���Bitmap���͵�ͼƬ����Ļ��
	//������Ҫ��Drawable--->Bitmap
	Bitmap icon;
	
	Paint textPaint;//ר�Ż����ֵĻ���
	Paint drawPaint;//ר�Ż�ͼ��Ļ���
	//alpha��ȡֵ��Χ��0~255
	//Խ�ӽ���0����ɫԽǳ��͸����
	//Խ�ӽ���255����ɫԽ���͸����
	int alpha;//ͨ��alphaֵ�����趨textColor����ǳ(͸����)
	
	
	public MyTabIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context,attrs);
		initPaint();
	}

	/**
	 * ��ʼ������
	 */
	private void initPaint() {
		textPaint = new Paint();
		textPaint.setTextSize(textSize);
		textPaint.setStyle(Style.FILL);
		//���ʵ���ɫ��Ϊ�ڻ���Ҫ�����޸ģ�����û��Ҫ������ָ��
		//textPaint.setColor(color);
		
		drawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		//��������ɫ������ͬ��
	}


	/**
	 * ��ȡ�����ļ������õ�����
	 * Ϊ�ĸ����Ը�ֵ
	 */
	private void initView(Context context, AttributeSet attrs) {
		TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.MyTabIcon);
		textColor = t.getColor(R.styleable.MyTabIcon_icon_color, Color.GREEN);
		int defValue = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 
				12, getResources().getDisplayMetrics());
		textSize = t.getDimensionPixelSize(
				R.styleable.MyTabIcon_icon_textsize, 
				defValue );
		//����û��ڲ����ļ���ָ����drawable���͵��ļ�
		drawable = t.getDrawable(R.styleable.MyTabIcon_icon_icon);
		//��һ��Drawable���͵�ͼƬתΪ��Ӧ��Bitmap����
		icon = ((BitmapDrawable)drawable).getBitmap();
		//icon��ʾʱ����̫С���Ŵ�
		icon = Bitmap.createScaledBitmap(icon, icon.getWidth()*2, icon.getHeight()*2, true);
		
		//����û��ڲ����ļ���ָ�����ı�����
		textContent = t.getString(R.styleable.MyTabIcon_icon_textcontent);
		t.recycle();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//��ͼ��
		//(left,top)����ͼ������Ͻ�����
		//ͼ��ĺ�����(�ؼ����/2-ͼ��Ŀ��/2)
		float left = getWidth()/2 - icon.getWidth()/2;
		//ͼ���������(�ؼ��߶�/2-ͼ��ĸ߶�/2)
		float top = getHeight()/2 - icon.getHeight()/2 - 10;
		canvas.drawBitmap(icon, left, top, drawPaint);
		//������
		//���ֵķ�Χ
		Rect bounds = new Rect();
		textPaint.getTextBounds(
				textContent, 
				0, textContent.length(), bounds);
		//(x,y)�������ֵ����½�����
		//���ֵĺ�����(�ؼ����/2-���ֿ��/2)
		float x = getWidth()/2-bounds.width()/2;
		//���ֵ�������(�ؼ��߶�/2+ͼƬ�߶�/2+���ֿ��/2)
		float y = getHeight()/2+icon.getHeight()/2+bounds.height() - 20;
		textPaint.setColor(Color.GRAY);
		textPaint.setAlpha(255);//TODO ע�͵�������ɶ��ͬ
		canvas.drawText(textContent, x, y, textPaint);
		drawColorText(canvas,x,y);
		drawColorIcon(canvas,left,top);
	}
	/**
	 * ���ƴ���ɫ��ͼ��
	 * @param canvas ���Ի�����Ļ�ϵ�canvas
	 * @param left
	 * @param top
	 */
	private void drawColorIcon(Canvas canvas, float left, float top) {
		
		Bitmap bitmap = Bitmap.createBitmap(icon.getWidth(), icon.getHeight(), Config.ARGB_8888);
		//myCanvas��������bitmap��
		Canvas myCanvas = new Canvas(bitmap);
		myCanvas.drawBitmap(icon, 0, 0, drawPaint);
		
		Rect r = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		drawPaint.setColor(textColor);
		drawPaint.setAlpha(alpha);
		drawPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		myCanvas.drawRect(r, drawPaint);
		//�ѸղŻ�����ɫͼƬʱΪ�������õ�
		//��ɫ��͸���Ⱥͻ��ģʽͳͳȥ��
		//�ָ������ʼʱ�Ļ���
		drawPaint.reset();//TODO ��������ע�͵�������ʲô��ͬ
		//�ѻ��õ�bitmap������Ļ��
		canvas.drawBitmap(bitmap, left, top, null);
		
	}

	/**
	 * ���ƴ���ɫ���ı�
	 * @param canvas ���Ի�����Ļ�ϵ�canvas
	 * @param x
	 * @param y
	 */
	private void drawColorText(Canvas canvas, float x, float y) {
		textPaint.setColor(textColor);
		//Ϊ��������͸����
		textPaint.setAlpha(alpha);
		canvas.drawText(textContent, x, y, textPaint);
		
	}
	
	public void setMyTabIconAlpha(int alpha){
		this.alpha = alpha;
		//ͨ��invalidate��������ϵͳ���ϵ�ȥ
		//����onDraw����
		invalidate();
	}
}
