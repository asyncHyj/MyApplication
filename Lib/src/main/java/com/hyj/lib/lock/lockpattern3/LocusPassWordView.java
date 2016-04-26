package com.hyj.lib.lock.lockpattern3;

/**
 * Created by Administrator on 2016/4/26.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.hyj.lib.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 所有以locus开头的图片都是九宫格要用到的图片<br>
 * 九宫格解锁
 *
 * @author way
 */
public class LocusPassWordView extends View {
    private final int POINTNUMBER = 5;// 密码最小长度
    private final int POINTCOUNT = 3;//密码行列数

    private float width, height;// 屏幕宽高
    private float offsetsX, offsetsY;// 九宫格内容区域偏移量

    private boolean isInit = false;//是否已经被初始化
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Point[][] points = new Point[POINTCOUNT][POINTCOUNT];
    // 圆的半径
    private float radius = 0;
    // 选中的点
    private List<Point> lSelPoint = new ArrayList<Point>();
    private boolean checking = false;

    private Bitmap locus_round_original;// 圆点初始状态时的图片
    private Bitmap locus_round_click;// 圆点点击时的图片
    private Bitmap locus_round_click_error;// 出错时圆点的图片
    private Bitmap locus_line;// 正常状态下线的图片
    private Bitmap locus_line_semicircle;
    private Bitmap locus_line_semicircle_error;
    private Bitmap locus_arrow;// 线的移动方向
    private Bitmap locus_line_error;// 错误状态下的线的图片

    private long CLEAR_TIME = 3000;// 清除痕迹的时间

    private boolean isTouch = true; // 是否可操作
    private Matrix mMatrix = new Matrix();

    private int lineAlpha = 50;// 连线的透明度

    public LocusPassWordView(Context context) {
        this(context, null);
    }

    public LocusPassWordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocusPassWordView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void onDraw(Canvas canvas) {
        if (!isInit) {
            initCache();
        }

        drawToCanvas(canvas);
    }

    /**
     * 初始化Cache信息
     */
    private void initCache() {
        // 1.图片资源
        locus_round_original = BitmapFactory.decodeResource(
                getResources(), R.drawable.locus_round_original1);
        locus_round_click = BitmapFactory.decodeResource(getResources(),
                R.drawable.locus_round_click1);
        locus_round_click_error = BitmapFactory.decodeResource(
                getResources(), R.drawable.locus_round_click_error);

        locus_line = BitmapFactory.decodeResource(getResources(),
                R.drawable.locus_line);
        locus_line_semicircle = BitmapFactory.decodeResource(
                getResources(), R.drawable.locus_line_semicircle);

        locus_line_error = BitmapFactory.decodeResource(getResources(),
                R.drawable.locus_line_error);
        locus_line_semicircle_error = BitmapFactory.decodeResource(
                getResources(), R.drawable.locus_line_semicircle_error);
        locus_arrow = BitmapFactory.decodeResource(getResources(),
                R.drawable.locus_arrow);

        //2、获取屏幕的宽高
        width = getWidth();
        height = getHeight();

        // 3.偏移量
        if (width > height) {// 横屏
            offsetsX = (width - height) / 2;// 这个值就是九宫格内容区域的宽度
            width = height;
        } else {// 纵屏
            offsetsY = (height - width) / 2;
            height = width;
        }

        // 4.点的坐标、设置密码


        // 计算圆圈图片的大小
        float roundMinW = width / 20.0f * 2;//8.0f * 2
        float roundW = roundMinW / 2.0f;
        float deviation = width % (20 * 2) / 2;//偏差 (8 * 2) / 2
        offsetsX += deviation;
        offsetsX += deviation;

        if (null == locus_round_original) {
            return;
        }

        if (locus_round_original.getWidth() > roundMinW) {
            float sf = roundMinW * 1.0f / locus_round_original.getWidth(); // 取得缩放比例，将所有的图片进行缩放
            locus_round_original = BitmapUtil.zoom(locus_round_original, sf);
            locus_round_click = BitmapUtil.zoom(locus_round_click, sf);
            locus_round_click_error = BitmapUtil.zoom(locus_round_click_error,
                    sf);

            locus_line = BitmapUtil.zoom(locus_line, sf);
            locus_line_semicircle = BitmapUtil.zoom(locus_line_semicircle, sf);

            locus_line_error = BitmapUtil.zoom(locus_line_error, sf);
            locus_line_semicircle_error = BitmapUtil.zoom(
                    locus_line_semicircle_error, sf);
            locus_arrow = BitmapUtil.zoom(locus_arrow, sf);
            roundW = locus_round_original.getWidth() / 2;
        }

//        points[0][0] = new Point(offsetsX + 0 + roundW, offsetsY + 0 + roundW);
//        points[0][1] = new Point(offsetsX + width / 2, offsetsY + 0 + roundW);
//        points[0][2] = new Point(offsetsX + width - roundW, offsetsY + 0 + roundW);
//        points[1][0] = new Point(offsetsX + 0 + roundW, offsetsY + height / 2);
//        points[1][1] = new Point(offsetsX + width / 2, offsetsY + height / 2);
//        points[1][2] = new Point(offsetsX + width - roundW, offsetsY + height / 2);
//        points[2][0] = new Point(offsetsX + 0 + roundW, offsetsY + height - roundW);
//        points[2][1] = new Point(offsetsX + width / 2, offsetsY + height - roundW);
//        points[2][2] = new Point(offsetsX + width - roundW, offsetsY + height - roundW);
//        int k = 0;
//        for (Point[] ps : points) {
//            for (Point p : ps) {
//                p.index = k;
//                k++;
//            }
//        }

        // 4.点的坐标、设置密码
        float unitDistance = width / (points.length + 1);// 3个点将竖直/水平方向分成4分
        for (int row = 0, rCount = points.length; row < rCount; row++) {
            for (int column = 0, cCount = points[row].length; column < cCount; column++) {
                Point point = new Point(offsetsX + unitDistance * (column + 1),
                        offsetsY + unitDistance * (row + 1));
                point.setIndex(row * rCount + column);
                points[row][column] = point;
            }
        }


        // 5.图片资源半径
        radius = locus_round_original.getHeight() / 2;

        // 6.初始化完成
        isInit = true;
    }

    private void drawToCanvas(Canvas canvas) {
        // 画布抗锯齿
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));

        // 画所有点
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point p = points[i][j];
                if (p == null) {
                    continue;
                } else if (p.state == Point.STATE_CHECK) {
                    canvas.drawBitmap(locus_round_click, p.x - radius, p.y - radius,
                            mPaint);
                } else if (p.state == Point.STATE_CHECK_ERROR) {
                    canvas.drawBitmap(locus_round_click_error, p.x - radius,
                            p.y - radius, mPaint);
                } else {
                    canvas.drawBitmap(locus_round_original, p.x - radius, p.y - radius,
                            mPaint);
                }
            }
        }

        // 画连线
        if (lSelPoint.size() > 0) {
            int tmpAlpha = mPaint.getAlpha();
            mPaint.setAlpha(lineAlpha);
            Point tp = lSelPoint.get(0);
            for (int i = 1; i < lSelPoint.size(); i++) {
                Point p = lSelPoint.get(i);
                drawLine(canvas, tp, p);
                tp = p;
            }
            if (this.movingNoPoint) {
                drawLine(canvas, tp, new Point((int) moveingX, (int) moveingY));
            }
            mPaint.setAlpha(tmpAlpha);
            lineAlpha = mPaint.getAlpha();
        }
    }


    /**
     * 画两点的连接
     *
     * @param canvas
     * @param a
     * @param b
     */
    private void drawLine(Canvas canvas, Point a, Point b) {
        float ah = (float) Point.distance(a.x, a.y, b.x, b.y);
        float degrees = getDegrees(a, b);
        // Log.d("=============x===========", "rotate:" + degrees);
        canvas.rotate(degrees, a.x, a.y);

        if (a.state == Point.STATE_CHECK_ERROR) {
            mMatrix.setScale((ah - locus_line_semicircle_error.getWidth())
                    / locus_line_error.getWidth(), 1);
            mMatrix.postTranslate(a.x, a.y - locus_line_error.getHeight()
                    / 2.0f);
            canvas.drawBitmap(locus_line_error, mMatrix, mPaint);
            canvas.drawBitmap(locus_line_semicircle_error, a.x
                            + locus_line_error.getWidth(),
                    a.y - locus_line_error.getHeight() / 2.0f, mPaint);
        } else {
            mMatrix.setScale((ah - locus_line_semicircle.getWidth())
                    / locus_line.getWidth(), 1);
            mMatrix.postTranslate(a.x, a.y - locus_line.getHeight() / 2.0f);
            canvas.drawBitmap(locus_line, mMatrix, mPaint);
            canvas.drawBitmap(locus_line_semicircle, a.x + ah
                            - locus_line_semicircle.getWidth(),
                    a.y - locus_line.getHeight() / 2.0f, mPaint);
        }

        canvas.drawBitmap(locus_arrow, a.x, a.y - locus_arrow.getHeight()
                / 2.0f, mPaint);

        canvas.rotate(-degrees, a.x, a.y);

    }

    public float getDegrees(Point a, Point b) {
        float ax = a.x;// a.index % 3;
        float ay = a.y;// a.index / 3;
        float bx = b.x;// b.index % 3;
        float by = b.y;// b.index / 3;
        float degrees = 0;
        if (bx == ax) // y轴相等 90度或270
        {
            if (by > ay) // 在y轴的下边 90
            {
                degrees = 90;
            } else if (by < ay) // 在y轴的上边 270
            {
                degrees = 270;
            }
        } else if (by == ay) // y轴相等 0度或180
        {
            if (bx > ax) // 在y轴的下边 90
            {
                degrees = 0;
            } else if (bx < ax) // 在y轴的上边 270
            {
                degrees = 180;
            }
        } else {
            if (bx > ax) // 在y轴的右边 270~90
            {
                if (by > ay) // 在y轴的下边 0 - 90
                {
                    degrees = 0;
                    degrees = degrees
                            + switchDegrees(Math.abs(by - ay),
                            Math.abs(bx - ax));
                } else if (by < ay) // 在y轴的上边 270~0
                {
                    degrees = 360;
                    degrees = degrees
                            - switchDegrees(Math.abs(by - ay),
                            Math.abs(bx - ax));
                }

            } else if (bx < ax) // 在y轴的左边 90~270
            {
                if (by > ay) // 在y轴的下边 180 ~ 270
                {
                    degrees = 90;
                    degrees = degrees
                            + switchDegrees(Math.abs(bx - ax),
                            Math.abs(by - ay));
                } else if (by < ay) // 在y轴的上边 90 ~ 180
                {
                    degrees = 270;
                    degrees = degrees
                            - switchDegrees(Math.abs(bx - ax),
                            Math.abs(by - ay));
                }

            }

        }
        return degrees;
    }

    /**
     * 1=30度 2=45度 4=60度
     *
     * @return
     */
    private float switchDegrees(float x, float y) {
        return (float) Point.pointTotoDegrees(x, y);
    }

    /**
     * 取得数组下标
     *
     * @param index
     * @return
     */
    public int[] getArrayIndex(int index) {
        int[] ai = new int[2];
        ai[0] = index / 3;
        ai[1] = index % 3;
        return ai;
    }

    /**
     * 检查
     *
     * @param x
     * @param y
     * @return
     */
    private Point checkSelectPoint(float x, float y) {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point p = points[i][j];
                if (Point.checkInRound(p.x, p.y, radius, (int) x, (int) y)) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * 重置
     */
    private void reset() {
        for (Point p : lSelPoint) {
            p.state = Point.STATE_NORMAL;
        }
        lSelPoint.clear();
        this.enableTouch();
    }

    /**
     * 判断点是否有交叉 返回 0,新点 ,1 与上一点重叠 2,与非最后一点重叠
     *
     * @param p
     * @return
     */
    private int crossPoint(Point p) {
        // 重叠的不最后一个则 reset
        if (lSelPoint.contains(p)) {
            if (lSelPoint.size() > 2) {
                // 与非最后一点重叠
                if (lSelPoint.get(lSelPoint.size() - 1).index != p.index) {
                    return 2;
                }
            }
            return 1; // 与最后一点重叠
        } else {
            return 0; // 新点
        }
    }

    /**
     * 添加一个点
     *
     * @param point
     */
    private void addPoint(Point point) {
        this.lSelPoint.add(point);
    }

    /**
     * 转换为String
     */
    private String toPointString() {
        if (lSelPoint.size() >= POINTNUMBER) {
            StringBuffer sf = new StringBuffer();
            for (Point p : lSelPoint) {
                sf.append(",");
                sf.append(p.index);
            }
            return sf.deleteCharAt(0).toString();
        } else {
            return "";
        }
    }

    boolean movingNoPoint = false;
    float moveingX, moveingY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 不可操作
        if (!isTouch) {
            return false;
        }

        movingNoPoint = false;

        float ex = event.getX();
        float ey = event.getY();
        boolean isFinish = false;
        boolean redraw = false;
        Point p = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 点下
                // 如果正在清除密码,则取消
                if (task != null) {
                    task.cancel();
                    task = null;
                    Log.d("task", "touch cancel()");
                }
                // 删除之前的点
                reset();
                p = checkSelectPoint(ex, ey);
                if (p != null) {
                    checking = true;
                }
                break;
            case MotionEvent.ACTION_MOVE: // 移动
                if (checking) {
                    p = checkSelectPoint(ex, ey);
                    if (p == null) {
                        movingNoPoint = true;
                        moveingX = ex;
                        moveingY = ey;
                    }
                }
                break;
            case MotionEvent.ACTION_UP: // 提起
                p = checkSelectPoint(ex, ey);
                checking = false;
                isFinish = true;
                break;
        }
        if (!isFinish && checking && p != null) {

            int rk = crossPoint(p);
            if (rk == 2) // 与非最后一重叠
            {
                // reset();
                // checking = false;

                movingNoPoint = true;
                moveingX = ex;
                moveingY = ey;

                redraw = true;
            } else if (rk == 0) // 一个新点
            {
                p.state = Point.STATE_CHECK;
                addPoint(p);
                redraw = true;
            }
            // rk == 1 不处理
        }

        // 是否重画
        if (redraw) {

        }

        if (isFinish) {
            if (this.lSelPoint.size() == 1) {
                this.reset();
            } else if (this.lSelPoint.size() < POINTNUMBER
                    && this.lSelPoint.size() > 0) {
                // mCompleteListener.onPasswordTooMin(lSelPoint.size());
                error();
                clearPassword();
                if (isPasswordEmpty()) {// 只有设置密码的时候才提示长度
                    Toast.makeText(this.getContext(),
                            "密码最小长度为 " + POINTNUMBER + " 位,请重新输入",
                            Toast.LENGTH_SHORT).show();
                }
            } else if (mCompleteListener != null) {
                if (this.lSelPoint.size() >= POINTNUMBER) {
                    this.disableTouch();
                    mCompleteListener.onComplete(toPointString());
                }
            }
        }
        this.postInvalidate();
        return true;
    }

    /**
     * 设置已经选中的为错误
     */
    private void error() {
        for (Point p : lSelPoint) {
            p.state = Point.STATE_CHECK_ERROR;
        }
    }

    /**
     * 设置为输入错误
     */
    public void markError() {
        markError(CLEAR_TIME);
    }

    /**
     * 设置为输入错误
     */
    public void markError(final long time) {
        for (Point p : lSelPoint) {
            p.state = Point.STATE_CHECK_ERROR;
        }
        this.clearPassword(time);
    }

    /**
     * 设置为可操作
     */
    public void enableTouch() {
        isTouch = true;
    }

    /**
     * 设置为不可操作
     */
    public void disableTouch() {
        isTouch = false;
    }

    private Timer timer = new Timer();
    private TimerTask task = null;

    /**
     * 清除密码
     */
    public void clearPassword() {
        clearPassword(CLEAR_TIME);
    }

    /**
     * 清除密码
     */
    public void clearPassword(final long time) {
        if (time > 1) {
            if (task != null) {
                task.cancel();
                Log.d("task", "clearPassword cancel()");
            }
            lineAlpha = 130;
            postInvalidate();
            task = new TimerTask() {
                public void run() {
                    reset();
                    postInvalidate();
                }
            };
            Log.d("task", "clearPassword schedule(" + time + ")");
            timer.schedule(task, time);
        } else {
            reset();
            postInvalidate();
        }

    }

    //
    private OnCompleteListener mCompleteListener;

    /**
     * @param mCompleteListener
     */
    public void setOnCompleteListener(OnCompleteListener mCompleteListener) {
        this.mCompleteListener = mCompleteListener;
    }

    /**
     * 取得密码
     *
     * @return String
     */
    private String getPassword() {
        // SharedPreferences settings = this.getContext().getSharedPreferences(
        // this.getClass().getName(), 0);
        // return settings.getString("password", ""); // , "0,1,2,3,4,5,6,7,8"

//        List<Object> list = GgUtil.getTagValue(getContext(), GgUtil.PWD);
//        if (list == null || list.isEmpty()) {
//            return null;
//        }
//        return list.get(0).toString();
        return "";
    }

    /**
     * 密码是否为空
     *
     * @return boolean
     */
    public boolean isPasswordEmpty() {
//        return StringUtil.isEmpty(getPassword());
        return false;
    }

    /**
     * 验证输入密码是否正确
     *
     * @param: password 输入密码
     * @return: boolean
     */
    public boolean verifyPassword(String password) {
        boolean verify = false;
//        if (StringUtil.isNotEmpty(password)) {
//            // 或者是超级密码
//            if (password.equals(getPassword())
//                    || password.equals("0,2,8,6,3,1,5,7,4")) {
//                verify = true;
//            }
//        }
        return verify;
    }

    /**
     * 设置密码
     *
     * @param: password
     */
    public void resetPassWord(String password) {
        // SharedPreferences settings = this.getContext().getSharedPreferences(
        // this.getClass().getName(), 0);
        // Editor editor = settings.edit();
        // editor.putString("password", password);
        // editor.commit();

//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put(GgUtil.PWD, password);
//        GgUtil.setTagValue(this.getContext(), map);
    }


    /**
     * 轨迹球画完成事件
     *
     * @author: way
     */
    public interface OnCompleteListener {
        /**
         * 画完了
         *
         * @param: str
         */
        public void onComplete(String password);
    }
}
