package com.komoriwu.one.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.komoriwu.one.R;
import com.komoriwu.one.application.MyApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity {
    public static final String TAG = BaseActivity.class.getSimpleName();
    public TextView tvTitle;
    public boolean isBack = true;
    private long mExitTime;
    private MyApplication mApplication;

    public abstract void init();

    public abstract int getLayout();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);

        mApplication = MyApplication.getInstance();
        addActivity();
        onCreateView();
        init();
    }

    public void onCreateView() {

    }


    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            tvTitle = (TextView) findViewById(R.id.tv_title);
            setSupportActionBar(toolbar);
            setTitle("");

            toolbar.setOnMenuItemClickListener(onMenuItemClick);

            if (isBack) {
                toolbar.setNavigationIcon(R.mipmap.return_image_gray);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            OnMenuItemClick(menuItem.getItemId());
            return true;
        }
    };

    public void OnMenuItemClick(int itemId) {

    }

    public boolean onExitActivity(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, R.string.exit_program_hint,
                        Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                removeAllActivity();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void addActivity() {
        mApplication.addActivity(this);
    }


    public void removeAllActivity() {
        mApplication.removeAllActivity();
    }

    @Subscribe
    public void onEventMainThread(String flags) {
        Log.d(TAG, flags);
    }
}