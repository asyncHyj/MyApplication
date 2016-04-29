package com.hyj.lib.lock;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyj.lib.BaseActivity;
import com.hyj.lib.Constants;
import com.hyj.lib.R;
import com.hyj.lib.lock.lockpattern3.LockPointView;
import com.hyj.lib.tools.DialogUtils;
import com.hyj.lib.tools.MD5Utils;
import com.hyj.lib.tools.SPUtils;

public class LockActivity extends BaseActivity {
    /**
     * 设置密码
     */
    private final int STATE_SET = 0X001;
    /**
     * 修改密码
     */
    private final int STATE_UPDATE = 0X002;
    /**
     * 解锁
     */
    private final int STATE_UNLOCKING = 0X003;

    private TextView tvTitle;
    private LockPointView lock;
    private LinearLayout llOperation;
    private TextView tvForget;
    private TextView tvUpdate;

    private String pwd;//设置的密码
    private int state;//当前状态
    private int pwdSetNumber = 0;//密码设置次数

    private long exitTime;//APP退出标记

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_main);

        myInit();
    }

    private void myInit() {
        initView();
        initData();
        iniListener();
        updateView();
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.lockTvTitle);
        lock = (LockPointView) findViewById(R.id.lock);
        llOperation = (LinearLayout) findViewById(R.id.lockLlOperation);
        tvForget = (TextView) findViewById(R.id.lockTvForget);
        tvUpdate = (TextView) findViewById(R.id.lockTvUpdate);
    }

    private void initData() {
//        lock.setPointCount(4);

        pwd = (String) SPUtils.getParam(this, Constants.FILE_NAME_SHARED, Constants.FIELD_PWD, "");
        if (TextUtils.isEmpty(pwd)) {
            state = STATE_SET;
        } else {
            state = STATE_UNLOCKING;
        }
    }

    private void iniListener() {
        lock.setOnCompleteListener(new LockPointView.OnCompleteListener() {
            @Override
            public void onComplete(String password) {
                chargePwd(password);
            }
        });

        tvForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LockActivity.this, LockQuestionActivity.class);
                LockActivity.this.startActivity(intent);
                LockActivity.this.finish();
            }
        });

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = STATE_UPDATE;
                updateView();
            }
        });
    }

    /**
     * 设置界面上的信息值
     */
    private void updateView() {
        switch (state) {
            case STATE_SET:
                llOperation.setVisibility(View.INVISIBLE);

                if (0 == pwdSetNumber) {
                    tvTitle.setText("绘制手势密码");
                } else if (1 == pwdSetNumber) {
                    tvTitle.setText("确认手势密码");
                }
                break;

            case STATE_UPDATE:
                llOperation.setVisibility(View.INVISIBLE);
                tvTitle.setText("验证手势密码");
                break;

            default:
                pwdSetNumber = 0;
                tvTitle.setText("输入手势密码");
                llOperation.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 判断密码设置
     *
     * @param password
     */
    private void chargePwd(String password) {
        password = MD5Utils.toMD5(password);

        if (STATE_SET == state) {
            if (0 == pwdSetNumber) {
                pwd = password;
                pwdSetNumber++;
                lock.clearPassword();
            } else if (1 == pwdSetNumber) {
                pwdSetNumber = 0;
                if (!pwd.equals(password)) {
                    DialogUtils.showToastShort(this, "两次绘制的手势密码不一致，请重新绘制");
                    lock.errorPoint();
                } else {
                    lock.resetPoint();
                    SPUtils.putParam(this, Constants.FILE_NAME_SHARED, Constants.FIELD_PWD, pwd);
                    state = STATE_UNLOCKING;
                }
            }
            updateView();
        } else if (STATE_UPDATE == state) {
            if (!pwd.equals(password)) {
                lock.errorPoint();
                DialogUtils.showToastShort(this, "手势密码错误，请重新输入");
                return;
            }
            lock.resetPoint();
            state = STATE_SET;
            updateView();
        } else if (STATE_UNLOCKING == state) {
            if (!pwd.equals(password)) {
                lock.errorPoint();
                DialogUtils.showToastShort(this, "手势密码错误，请重新输入");
                return;
            }

            DialogUtils.showToastShort(this, "手势密码输入正确");
            lock.resetPoint();
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                DialogUtils.showToastShort(this, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
//                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}