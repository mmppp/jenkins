package com.hanke.navi.skyair.scale;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.hanke.navi.R;
import com.hanke.navi.skyair.MyApplication;


public abstract class BaseScaleView extends View{

    public static final int[] ATTR={
//            R.styleable.scales_scale_view_min,
//            R.styleable.scales_scale_view_max,
//            R.styleable.scales_scale_view_margin,
//            R.styleable.scales_scale_view_height
            R.attr.scale_view_min,
            R.attr.scale_view_max,
            R.attr.scale_view_margin,
            R.attr.scale_view_height,
    };
    public static final int SCALE_MIN = 0;
    public static final int SCALE_MAX = 1;
    public static final int SCALE_MARGIN = 2;
    public static final int SCALE_HEIGHT = 3;
    protected int mMin ;//最小刻度
    protected int mMax ;//最大刻度
    protected int mScaleMargin ;//刻度线的间距
    protected int mScaleHeight ;//刻度线的高度
    public Scroller mScroller;
    public int mScrollLastX;

    public OnScrollListener mScrollListener;

    public int mCountScale;//滑动的总刻度
    public int mMidCountScale;//中间刻度
    public int mScaleMaxHeight;//整刻度线的高度
    public int mRectWidth;//总宽度
    public int mRectHeight;//总高度

    public int mTempScale;//用于判断滑动方向
    public int mScaleScrollViewRange;//实际上就是自定义View的高度

    public int width,height;
    public static int width_view,height_view;
    public float Yzhou;

    public interface OnScrollListener{
        void onScaleScroll(int scale);
    }

    public int getmMin() {
        return mMin;
    }

    public void setmMin(int mMin) {
        this.mMin = mMin;
    }

    public int getmMax() {
        return mMax;
    }

    public void setmMax(int mMax) {
        this.mMax = mMax;
    }

    public BaseScaleView(Context context) {
        this(context,null);
    }

    public BaseScaleView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BaseScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        //方法一：获取屏幕宽高
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;
        Log.e("BaseScaleView","屏幕宽width = "+width+", 屏幕高height = "+height);
        //方法二：获取屏幕宽高
//        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//        int width123 = wm.getDefaultDisplay().getWidth();
//        int height123 = wm.getDefaultDisplay().getHeight();
//        Log.e("msg屏幕宽高","width123 = "+width123+", height123 = "+height123);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置View的宽高
        setMeasuredDimension(MyApplication.getMyApplication().getWidth()/8,2*MyApplication.getMyApplication().getHeight()/5);
        if(MyApplication.getAppContext().getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){//设置是竖屏
            //获取自定义View的宽高
            width_view = this.getMeasuredWidth();//自定义View的宽度
            height_view = this.getMeasuredHeight();//自定义View的高度
            Log.e("自定义View的宽高","width_view = "+width_view+", height_view = "+height_view);
        }
        mScaleScrollViewRange = getMeasuredHeight();
        Log.e("msg","mScaleScrollViewRange = "+mScaleScrollViewRange);
        mTempScale = mScaleScrollViewRange / mScaleMargin / 2 + mMin;//用于判断滑动方向
        mMidCountScale = mScaleScrollViewRange / mScaleMargin / 2 + mMin;//中间刻度
    }

    public void init(AttributeSet attrs){
        mScroller = new Scroller(getContext());
        initVar(attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画笔
        Paint paint = new Paint();
//        paint.setColor(Color.GRAY);
        paint.setAntiAlias(true);// 抗锯齿
        paint.setDither(true);// 设置防抖动,设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        paint.setStyle(Paint.Style.STROKE);//设置空心
        paint.setTextAlign(Paint.Align.CENTER);//文字居中

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(width_view / 35);//控制线条的粗细
        float Yzhou=paint.getStrokeWidth();
        this.Yzhou = Yzhou;

        onDrawLine(canvas,paint);//画Y轴线
        //画刻度线
        Paint linepaint = new Paint();
        linepaint.setAntiAlias(true);
        linepaint.setDither(true);
        linepaint.setColor(Color.WHITE);
        linepaint.setStrokeWidth(width_view / 35);//控制线条的粗细
        //画字的笔
        Paint textpaint = new Paint();
        textpaint.setTextSize(13*width_view / 64);
        textpaint.setColor(Color.WHITE);
        textpaint.setAntiAlias(true);//抗锯齿
        textpaint.setDither(true);

        onDrawScale(canvas,linepaint,textpaint);//画刻度

        // 绘制三角形外部
        Paint paint_san_wai = new Paint();
        paint_san_wai.setColor(Color.WHITE);
        paint_san_wai.setStyle(Paint.Style.STROKE);
        paint_san_wai.setStrokeWidth(width_view / 40);
        paint_san_wai.setAntiAlias(true);
        paint_san_wai.setDither(true);
        // 绘制三角形内部
        Paint paint_san_nei = new Paint();
        paint_san_nei.setColor(Color.parseColor("#22d640"));
        paint_san_nei.setAntiAlias(true);
        paint_san_nei.setDither(true);
        paint_san_nei.setStrokeWidth(width_view / 60);
        //画圆角矩形外边框
        Paint paint_kuang = new Paint();
        paint_kuang.setStyle(Paint.Style.STROKE);//充满
        paint_kuang.setStrokeWidth(width_view / 40);
        paint_kuang.setColor(Color.WHITE);
        paint_kuang.setAntiAlias(true);// 设置画笔的锯齿效果
        paint_kuang.setDither(true);
        //画圆角矩形内部填充色
        Paint paint_nei = new Paint();
        paint_nei.setStyle(Paint.Style.FILL);//充满
        paint_nei.setColor(Color.parseColor("#22d640"));
        paint_nei.setAntiAlias(true);// 设置画笔的锯齿效果
        paint_nei.setDither(true);
        paint_nei.setStrokeWidth(width_view / 100);
        //方框里的字
        Paint middle_textpaint = new Paint();
        middle_textpaint.setAntiAlias(true);
        middle_textpaint.setDither(true);
        middle_textpaint.setColor(Color.WHITE);
        middle_textpaint.setTextSize(7*width_view/32);

        onDrawPointer(canvas,paint_san_wai,paint_san_nei,paint_kuang,paint_nei,middle_textpaint);//画指针
        super.onDraw(canvas);
    }
    public abstract void initVar(AttributeSet attrs);//初始化参数
    public abstract void onDrawLine(Canvas canvas,Paint paint);//画线
    public abstract void onDrawScale(Canvas canvas,Paint linepaint,Paint textpaint);//画刻度
    public abstract void onDrawPointer(Canvas canvas,Paint paint1,Paint paint2,Paint paint3,Paint paint4,Paint paint5);//画指针

    /**
     * 使用Scroller时需重写
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        // 判断Scroller是否执行完毕
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            // 通过重绘来不断调用computeScroll
            invalidate();
        }
    }
    public void smoothScrollBy(int dx,int dy){
        mScroller.startScroll(mScroller.getFinalX(),mScroller.getFinalY(),dx,dy);
    }
    public void smoothScrollTo(int fx,int fy){
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx,dy);
    }

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnScrollListener(OnScrollListener listener) {
        this.mScrollListener =listener;
    }

    public void setmMidCountScale(int mMidCountScale) {
        this.mMidCountScale = mMidCountScale;
    }

    public int getmMidCountScale() {
        return mMidCountScale;
    }

    public int getmScaleScrollViewRange() {
        return mScaleScrollViewRange;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
