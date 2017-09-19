package com.weidi.artifact.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.weidi.artifact.R;


/*
    引用：
  	<StateSelectionView
		android:id="@+id/myStatus" 
		android:layout_gravity="center"
		android:layout_width="wrap_content"
		android:layout_height="wrap_content"/>
 */
public class StateSelectionView extends View{
	private Bitmap background;
	private Bitmap slideBitmap;
	private Paint paint;
	public boolean currentStatus;
	
	//在代码里面创建对象的时候，使用此构造方法
	public StateSelectionView(Context context) {
		super(context);
		init();
	}
	/**
	 * 在布局文件中声名的view，创建时由系统自动调用。
	 * @param context	上下文对象
	 * @param attrs		属性集
	 */
	public StateSelectionView(Context context, AttributeSet attribute) {
		super(context, attribute);
		init();
	}
	public StateSelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init(){
		/*
		 * background 要绘制的图片
		 * left	               图片的左边界
		 * top	               图片的上边界
		 * paint      绘制图片要使用的画笔
		 */
		background = BitmapFactory.decodeResource(getResources(), R.drawable.switch_background);
		slideBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.slide_button);
		paint = new Paint();
		paint.setAntiAlias(true);//打开抗矩齿
	}

	/*
	 * view 对象显示的屏幕上，有几个重要步骤：
	 * 1、构造方法 创建 对象。
	 * 2、测量view的大小。	onMeasure(int,int);
	 * 3、确定view的位置 ，view自身有一些建议权，决定权在 父view手中。  onLayout();
	 * 4、绘制 view 的内容 。 onDraw(Canvas);
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		/**
		 * 设置当前view的大小
		 * width  :view的宽度
		 * height :view的高度   （单位：像素）
		 */
		setMeasuredDimension(background.getWidth(), background.getHeight());
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
		canvas.drawBitmap(background, 0, 0, paint);
		canvas.drawBitmap(slideBitmap, maxLength, 0, paint);
	}
	
	private int maxLength;
	private int startIndex;
	private int newIndex;
	private boolean isDrag;
	@Override//以后点击事件也不要弄了，就弄这个触摸方法吧。
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()){
			case MotionEvent.ACTION_UP:{
				if(isDrag){
					if(maxLength <= (background.getWidth() - slideBitmap.getWidth()) / 2){
						maxLength = 0;
						currentStatus = false;
					}else{
						maxLength = background.getWidth() - slideBitmap.getWidth();
						currentStatus = true;
					}
				}else{//单击事件，那onClick()方法一直不执行，只能用这样了。即使返回为false也没有点击事件。
					if(!currentStatus){
						maxLength = background.getWidth() - slideBitmap.getWidth();
						currentStatus = true;//“开”的时候状态值只要是true就行了
					}else{
						maxLength = 0;
						currentStatus = false;
					}
				}
				break;
			}
			case MotionEvent.ACTION_DOWN:{
				isDrag = false;
				startIndex = (int)event.getRawX();
				break;
			}
			case MotionEvent.ACTION_MOVE:{
				newIndex = (int)event.getRawX();
				maxLength = newIndex - startIndex;
				if(Math.abs(maxLength) > 5){//移动距离大于5就不是单击事件了
					isDrag = true;
				}
				//不让控件滑出边界
				if(isDrag){
					maxLength = (maxLength <=0) ? 0 : maxLength;
					maxLength = (maxLength >= background.getWidth() - slideBitmap.getWidth()) 
							   ? background.getWidth() - slideBitmap.getWidth()
							   : maxLength;
				}
				break;
			}
		}
		invalidate();//一执行系统就会回调onDraw()方法
		return true;
	}
	
}
