package com.Dinggrn.weiliao.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.Dinggrn.weiliao.R;


public class MyLetterView extends View {

	Paint paint;
	OnTouchLetterListener listener;

	String[] letters = {"#","A","B","C","D","E","F","G","H",
			"I","J","K","L","M","N","O","P","Q",
			"R","S","T","U","V","W","X","Y","Z"};

	int textColor;//�Զ������ԣ�������ɫ
	int textSize;//�Զ������ԣ������С

	/**
	 * ˫��������Ӧ�õĳ��ϣ�
	 * �����ڲ����ļ��У������Զ���Viewʱ
	 * 
	 * @param context
	 * @param attrs
	 */
	public MyLetterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttr(context,attrs);

		// ��ʼ������
		initPaint();
	}

	/**
	 * �Ӳ����ļ��ж�ȡ�Զ�������
	 * @param context
	 * @param attrs
	 */
	private void initAttr(Context context, AttributeSet attrs) {
		TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.MyLetterView);
		textColor = t.getColor(R.styleable.MyLetterView_text_color, Color.BLACK);
		textSize = t.getDimensionPixelSize(R.styleable.MyLetterView_text_size, 
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
		t.recycle();
	}

	public void setListener(OnTouchLetterListener listener) {
		this.listener = listener;
	}


	/**
	 * ��ʼ������
	 */
	private void initPaint() {
		paint = new Paint();
		paint.setColor(textColor);
		paint.setTextSize(textSize);
		paint.setStyle(Style.FILL);
		paint.setTypeface(Typeface.MONOSPACE);
	}

	/**
	 * ��������������
	 * ��ʹ�ô��봴����ͼ�����ʱ����Ҫʹ�õ�����������
	 * ��Ҫʹ�ø���setter��set������Ϊ��ͼ�ṩ����
	 * @param context
	 */
	public MyLetterView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * ����ϣ����view�����ӡ���������Ļ��
	 * ���Ҫ����������Ҫ����(canvas),����(paint)
	 * onDraw����Ϊ�����ṩ��һ������
	 * �κ�����������ϻ������ݣ�������ʾ����Ļ��
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//���ı�������Ļ��
		for(int i=0;i<letters.length;i++){
			//x = ��Ŀ��/2 - �ֵĿ��/2
			//y = ��ĸ߶�/2 + �ֵĸ߶�/2 + ���ɿ�ĸ߶�

			//��Ŀ�� = ������Զ�����ͼ�Ŀ��
			//�����������ͼ�Ŀ����getWidth������
			int width = getWidth();

			//��Ŀ�� = ������Զ�����ͼ�ĸ߶�/27
			//�����������ͼ�ĸ߶���getHeight������
			int height = getHeight() / letters.length;

			//�ֵĿ�Ⱥ͸߶�
			Rect bounds = new Rect();
			paint.getTextBounds(letters[i], 0, letters[i].length(), bounds );
			//���ֵĿ��
			int letterwidth = bounds.width();
			//���ֵĸ߶�
			int letterheight = bounds.height();

			float x = width/2 - letterwidth/2;
			float y = height/2 + letterheight/2 + height*i;
			canvas.drawText(letters[i], x, y, paint);
		}
	}

	/**
	 * ��ָ���Զ���View�ϰ��»��߻�������̧��
	 * �÷������ᱻ����
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			setBackgroundColor(Color.DKGRAY);
			//��ָ��λ��
			float y = event.getY();
			//ͨ����ָ��λ�ú��Զ�����ͼ����߶ȵı�ֵ
			//�����Ҫȥ�ַ����±�ֵ
			int idx = (int) ((y*letters.length)/getHeight());
			//����±�ֵȡֵ����(0~letters.lenght)
			if(idx>=0&&idx<letters.length){
				if(listener!=null){//��������Ϊ��
					String str = letters[idx];
					listener.onTouchLetter(str);
				}
			}
			break;
		
		default:
			setBackgroundColor(Color.TRANSPARENT);
			if(listener!=null){
				listener.onFinishTouch();
			}
			break;
		}

		return true;
	}

	public interface OnTouchLetterListener{
		/**
		 * ���û���ָ���Զ���View�ϻ�����ʱ��
		 * ���ø÷�����������ǰ��ָλ�õ���ĸ
		 * ��Ϊ���������÷�����
		 * @param str
		 */
		void onTouchLetter(String str);
		/**
		 * ���û���ָ�뿪���Զ���Viewʱ
		 * ���ø÷���
		 */
		void onFinishTouch();
	}
}
