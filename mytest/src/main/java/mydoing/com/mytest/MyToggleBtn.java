package mydoing.com.mytest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by Silencer on 2019/3/3.
 */


public class MyToggleBtn extends View implements View.OnClickListener {
    private Bitmap  slipper_btn,bgBitmap;
    /**
     * 选择开关左侧的文字信息
     */
    private static final String showTextLeft = "完成任务";
    /**
     * 选择开关右侧的文字信息
     */
    private static final String showTextRight = "产生任务";
    Handler handler = new Handler();
    /**
     * 最小高度
     */
    private float miniHeight;
    /**
     * 最小宽度
     */
    private float miniWidth;
    private Paint paint;
    //默认显示右侧
    private String showText;
    private Context ctx;
    /**
     * 当前的开关状态
     * true 为开
     * false 为关
     */
    private boolean currState = false;
    private int slideLeft = 0;
    /**
     * 响应view的点击事件
     */
    private boolean isScroll = false;
    /**
     * 上一个事件中的X坐标
     */
    private int lastX;
    /**
     * down事件中的X坐标
     */
    private int downX;
    private int slideLeftMax;
    /**
     * 判断触摸时，是否发生滑动事件
     */
    private boolean isSliding = false;
    private IOnSwitchListener onSwitchListener;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            if (currState) {
                if (slideLeft < slideLeftMax) {
                    isScroll = true;
                    slideLeft += 20;
                    handler.postDelayed(this, 100);
                } else {
                    isScroll = false;
                }
            } else {
                if (slideLeft > 0) {
                    isScroll = true;
                    slideLeft -= 15;
                    handler.postDelayed(this, 100);
                } else {
                    isScroll = false;
                }
            }
            ((Activity) (ctx)).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (currState) {// 开状态
                        showText = showTextLeft;
                        if (onSwitchListener != null) {
                            onSwitchListener.OnRightClick();
                        }
                    } else {
                        showText = showTextRight;
                        if (onSwitchListener != null) {
                            onSwitchListener.OnLeftClick();
                        }
                    }
                    flushView();
                }
            });
        }
    };

    /**
     * 在布局文件中声明该控件时，调用此方法
     *
     * @param context
     * @param attrs
     */
    public MyToggleBtn(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    /**
     *   初始化
     */
    private void init(Context context) {
        ctx = context;
        showText = showTextRight;

        paint = new Paint();
        paint.setAntiAlias(true);// 抗矩齿
        paint.setTextSize(sp2px(context, 14));
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        miniHeight = fontMetrics.bottom - fontMetrics.top + getPaddingTop() + getBottom() + sp2px(context, 10);
        float textWidth = 0;
        if (showTextLeft.length() > showTextRight.length()) {
            textWidth = paint.measureText(showTextLeft);
        } else {
            textWidth = paint.measureText(showTextRight);
        }
        paint.measureText(showTextLeft);
        paint.measureText(showTextRight);
        miniWidth = (float) (1.5 * miniHeight + textWidth + getPaddingLeft() + getPaddingRight() + sp2px(context, 10));
        //滑动图片，左边界的最大值
        slideLeftMax = (int) (miniWidth - miniHeight);
        paint.setColor(Color.WHITE);
        // 添加点击事件

        setOnClickListener(this);

        slipper_btn = BitmapFactory.decodeResource(getResources(), R.drawable.icon_btn);
        bgBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.icon_btn_bg);

    }

    @Override
    public void onClick(View v) {
        // 如果发生了滑动的动作，就不执行以下代码
        if (isSliding) {
            return;
        }
        if (isScroll) {
            return;
        }
        currState = !currState;
        handler.postDelayed(runnable, 100);
        // 切换按钮的开关状态
        //flushState();
    }

    /**
     * 刷新状态
     * 根据当前的状态，刷新页面
     */
    private void flushState() {
        if (currState) {// 开状态
            showText = showTextLeft;
            slideLeft = slideLeftMax;

            if (onSwitchListener != null) {
                onSwitchListener.OnRightClick();
            }
        } else {
            showText = showTextRight;
            // 关状态
            slideLeft = 0;
            if (onSwitchListener != null) {
                onSwitchListener.OnLeftClick();
            }
        }
        flushView();
    }

    @Override
    /**
     * 重写该方法，处理触摸事件
     * 如果该view消费了事件，那么，返回true
     */
    public boolean onTouchEvent(MotionEvent event) {
        // super 注释掉以后，onclick事件，就失效了，因为，点击这个动作，也是从onTouchEvent 方法中解析出来，符合一定的要求，就是一个点击事件
        // 系统中，如果发现，view产生了up事件，就认为，发生了onclick动作,就行执行listener.onClick方法
        super.onTouchEvent(event);
            /*
             * 点击切换开关，与触摸滑动切换开关，就会产生冲突
             * 我们自己规定，如果手指在屏幕上移动，超过15个象素，就按滑动来切换开关，同时禁用点击切换开关的动作
             */
        if (isScroll) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("MotionEvent.ACTION_DOWN");

                downX = lastX = (int) event.getX(); // 获得相对于当前view的坐标
//                  event.getRawX(); // 是相对于屏幕的坐标
                // down 事件发生时，肯定不是滑动的动作
                isSliding = false;
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("MotionEvent.ACTION_MOVE");

                // 获得距离
                int disX = (int) (event.getX() - lastX);
                // 改变滑动图片的左边界
                slideLeft += disX;
                flushView();

                // 为lastX重新赋值
                lastX = (int) event.getX();

                // 判断是否发生滑动事件
                if (Math.abs(event.getX() - downX) > 15) { // 手指在屏幕上滑动的距离大于15象素
                    isSliding = true;
                }

                break;
            case MotionEvent.ACTION_UP:
                System.out.println("MotionEvent.ACTION_UP");
                // 只有发生了滑动，才执行以下代码
                if (isSliding) {

                    // 如果slideLeft > 最大值的一半 当前是开状态
                    // 否则就是关的状态
                    if (slideLeft > slideLeftMax / 2) { // 开状态
                        currState = true;
                    } else {
                        currState = false;
                    }
                    flushState();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制背景图
        canvas.drawBitmap(bgBitmap, 0, 0, paint);


//        paint.setColor(Color.RED);
//        paint.setStrokeWidth(1);
//        paint.setStyle(paint.getStyle().FILL);
//        canvas.drawCircle((float) (miniHeight * 0.5), (float) (miniHeight * 0.5), (float) (miniHeight * 0.5), paint);
//        canvas.drawCircle((float) (miniWidth - miniHeight * 0.5), (float) (miniHeight * 0.5), (float) (miniHeight * 0.5), paint);
//
//        canvas.drawRect((float) (miniHeight * 0.5), 0, (float) (miniWidth - miniHeight * 0.5), miniHeight, paint);

        float textWidth = paint.measureText(showText);

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;

        float newY = getMeasuredHeight() / 2 - fontMetrics.descent + (fontMetrics.descent - fontMetrics.ascent) / 2;
        float baseX = 0;
        paint.setColor(Color.WHITE);
        if (!currState) {
            baseX = (float) ((getMeasuredWidth() - textWidth) - getPaddingRight() - miniHeight * 0.5);
        } else {
            baseX = (float) (0 + getPaddingLeft() + miniHeight * 0.5);
        }
        canvas.drawText(showText, baseX, newY, paint);

        // 绘制滑动图片
        canvas.drawBitmap(slipper_btn, slideLeft, 0, paint);
//        paint.setColor(Color.WHITE);
//        canvas.drawCircle((float) (slideLeft + miniHeight * 0.5), (float) (miniHeight * 0.5), (float) (miniHeight * 0.5 + 0.5), paint);
    }

    @Override
    /**
     * 指定view的大小
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 当前view的宽度，就和背景图的大小一致
        int measuredWidth = (int) miniWidth;
        int measuredHeight = (int) miniHeight;

        //指定测量的宽高
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    /**
     * 刷新页面
     */
    private void flushView() {
        // 保证 slideLeft >=0  同时   <= slideLeftMax
        if (slideLeft < 0) {
            slideLeft = 0;
        }
        if (slideLeft > slideLeftMax) {
            slideLeft = slideLeftMax;
        }
        invalidate();// 刷新页面
    }

    public float sp2px(Context ctx, float sp) {
        return (float) (ctx.getResources().getDisplayMetrics().density * sp + 0.5);
    }

    public void setOnSwitchListener(IOnSwitchListener onSwitchListener) {
        this.onSwitchListener = onSwitchListener;
    }

    public interface IOnSwitchListener {
        public void OnLeftClick();

        public void OnRightClick();
    }
}

