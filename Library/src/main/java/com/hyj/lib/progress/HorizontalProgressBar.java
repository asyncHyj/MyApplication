package com.hyj.lib.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.hyj.lib.R;

/**
 * <pre>
 *     自定义控件几个步骤，也是要弄明白的几点
 *      1、自定义属性的申明与获取
 *      2、测量onMeasure
 *      3、布局onLayout(ViewGroup)
 *      4、绘制onDraw
 *      5、onTouchEvent
 *      6、onInterceptTouchEvent(ViewGroup)
 *      7、状态恢复与保存
 * </pre>
 *
 * @Author hyj
 * @Date 2016/5/22 9:46
 */
public class HorizontalProgressBar extends ProgressBar {
    //自定义属性
    protected int textSize = sp2px(10);//显示文字大小
    protected int textColor = 0XFFFC00D1;//显示文字颜色
    protected int textOffset = dp2px(5);//文字跟进度条之间的距离
    protected int unReachColor = 0XFFD3D6DA;//未下载进度颜色
    protected int unReachHeight = dp2px(2);//未下载进度高度
    protected int reachColor = 0XFFFC00D1;//已下载进度条颜色
    protected int reachHeight = dp2px(2);//已下载进度条高度

    //绘制工具
    protected Paint paint = new Paint();
    protected int realWidth;//当前控件的宽度-padding值,真正占用的宽度

    /**
     * 将dp转换成px
     *
     * @param dpValue
     * @return
     */
    protected int dp2px(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    /**
     * 将sp转换成px
     *
     * @param spValue
     * @return
     */
    protected int sp2px(int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources().getDisplayMetrics());
    }

    public HorizontalProgressBar(Context context) {
        this(context, null);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        myInit(attrs);
    }

    protected void myInit(AttributeSet attrs) {
        initAttrs(attrs);
        initData();
    }

    /**
     * 初始化自定义属性
     *
     * @param attrs
     */
    protected void initAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProBar);
        textSize = (int) ta.getDimension(R.styleable.HorizontalProBar_pbTextSize, textSize);
        textColor = ta.getColor(R.styleable.HorizontalProBar_pbTextColor, textColor);
        textOffset = (int) ta.getDimension(R.styleable.HorizontalProBar_pbTextOffset, textOffset);
        unReachColor = ta.getColor(R.styleable.HorizontalProBar_pbUnreachColor, unReachColor);
        unReachHeight = (int) ta.getDimension(R.styleable.HorizontalProBar_PbUnreachHeight, unReachHeight);
        reachColor = ta.getColor(R.styleable.HorizontalProBar_pbReachColor, reachColor);
        reachHeight = (int) ta.getDimension(R.styleable.HorizontalProBar_pbReachHeight, reachHeight);
        ta.recycle();
    }

    protected void initData() {
        paint.setTextSize(textSize);//设置画笔绘制字体的大小
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //因为是水平进度条，所以宽度肯定有一个具体的值(match_parent、其他)，因此不必分情况
        //int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthValue = MeasureSpec.getSize(widthMeasureSpec);

        int height = measureHeight(heightMeasureSpec);

        //确定view的宽高
        setMeasuredDimension(widthValue, height);

        //设置控件实际绘制的大小
        realWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    /**
     * 拿到高度
     *
     * @param heightMeasureSpec
     * @return
     */
    private int measureHeight(int heightMeasureSpec) {
        int height = 0;

        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (MeasureSpec.EXACTLY == mode) {//给了一个精确值
            height = size;
        } else {
            //高度取已显示、未显示、文字三者中最大的值
            //paint.descent()：baseline之下至字符最低处的距离
            //paint.ascent()：baseline之上至字符最高处的距离(负数)
            int textHieght = (int) (paint.descent() - paint.ascent());
            int max = Math.max(reachHeight, unReachHeight);
            max = Math.max(max, Math.abs(textHieght));
            height = getPaddingTop() + getPaddingBottom() + max;

            //此种模式下，测量值不能超过给定的size值
            if (MeasureSpec.AT_MOST == mode) {
                height = Math.min(height, size);
            }
        }

        return height;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();

        //移动绘制坐标位置
        canvas.translate(getPaddingLeft(), getHeight() / 2);

        boolean needUnreach = true;//是否需要绘制未完成部分

        //draw reachBar
        String text = getProgress() + "%";
        int textWidth = (int) paint.measureText(text);//测量文本宽度
        textWidth += textOffset;//整个文本域的长度

        float radio = getProgress() * 1.0f / getMax();//reachbar长度所占比例
        float progressX = radio * (realWidth - textWidth);
        if (progressX + textWidth > realWidth) {//判断是否已经绘制完成
            progressX = realWidth - textWidth;
            needUnreach = false;
        }

        float endX = progressX - textOffset;//reachbar实际长度得减去textoffset
        if (endX > 0) {
            paint.setColor(reachColor);
            paint.setStrokeWidth(reachHeight);
            canvas.drawLine(0, 0, endX, 0, paint);
        }

        //draw text
        paint.setColor(textColor);
        int y = (int) (-(paint.descent() + paint.ascent()) / 2);//绘制文字的基线往下移动
        canvas.drawText(text, progressX, y, paint);

        //draw unreachBar
        if (needUnreach) {
            float start = progressX + textWidth;
            paint.setColor(unReachColor);
            paint.setStrokeWidth(unReachHeight);
            canvas.drawLine(start, 0, realWidth, 0, paint);
        }

        canvas.restore();
    }
}