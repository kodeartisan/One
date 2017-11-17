package com.komoriwu.one.one.detail;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.komoriwu.one.R;
import com.komoriwu.one.base.MvpBaseActivity;
import com.komoriwu.one.model.bean.AuthorBean;
import com.komoriwu.one.model.bean.CommentBean;
import com.komoriwu.one.model.bean.ContentListBean;
import com.komoriwu.one.model.bean.MovieDetailBean;
import com.komoriwu.one.model.bean.MusicDetailBean;
import com.komoriwu.one.model.bean.ReadDetailBean;
import com.komoriwu.one.one.detail.mvp.ReadDetailContract;
import com.komoriwu.one.one.detail.mvp.ReadDetailPresenter;
import com.komoriwu.one.utils.Constants;
import com.komoriwu.one.utils.HtmlUtil;
import com.komoriwu.one.utils.Utils;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ReadDetailActivity extends MvpBaseActivity<ReadDetailPresenter> implements
        ReadDetailContract.View {
    private static final String TAG=ReadDetailActivity.class.getSimpleName();
    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.tv_detail_title)
    TextView tvDetailTitle;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_introduce)
    TextView tvIntroduce;
    @BindView(R.id.tv_author)
    TextView tvAuthor;
    @BindView(R.id.iv_author)
    ImageView ivAuthor;
    @BindView(R.id.iv_loading)
    ImageView ivLoading;
    @BindView(R.id.tv_hp_author)
    TextView tvHpAuthor;
    @BindView(R.id.tv_auth_it)
    TextView tvAuthIt;
    @BindView(R.id.tv_recommend)
    TextView tvRecommend;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.layout_bottom)
    RelativeLayout layoutBottom;
    @BindView(R.id.layout_bottom2)
    RelativeLayout layoutBottom2;
    @BindView(R.id.tv_like_num)
    TextView tvLikeNum;
    @BindView(R.id.tv_comment_num)
    TextView tvCommentNum;
    @BindView(R.id.nsv_scroller)
    NestedScrollView nsvScroller;
    private ContentListBean mContentListBean;
    private CommentAdapter mCommentAdapter;
    private AnimationDrawable mAnimationDrawable;
    private boolean mIsBottomShow = true;

    @Override
    public void setInject() {
        getActivityComponent().inject(this);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_read_detail;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void init() {
        initAnim();
        initToolbar();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mContentListBean = (ContentListBean) getIntent().getSerializableExtra(Constants.
                ONE_LIST_BEAN);
        tvTitle.setText(mContentListBean.getShareList().getWx().getTitle().split("\\|")[0].
                trim());
        tvDetailTitle.setText(mContentListBean.getTitle());
        tvUserName.setText(mContentListBean.getShareList().getWx().getDesc().split(" ")
                [0].trim());
        tvLikeNum.setText(mContentListBean.getLikeCount() + "");

        initWebView();
        presenter.loadDetail(mContentListBean);

        initListener();
    }

    private void initListener() {
        nsvScroller.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX,
                                       int oldScrollY) {
                if (scrollY - oldScrollY > 0 && mIsBottomShow) {  //下移隐藏
                    mIsBottomShow = false;
                    layoutBottom2.animate().translationY(layoutBottom2.getHeight());
                    tvTitle.setText(tvDetailTitle.getText().toString());
                } else if (scrollY - oldScrollY < 0 && !mIsBottomShow) {    //上移出现
                    mIsBottomShow = true;
                    layoutBottom2.animate().translationY(0);
                }

                if (scrollY==0&&mIsBottomShow){
                    tvTitle.setText(mContentListBean.getShareList().getWx().getTitle().split(
                            "\\|")[0].trim());
                }
            }
        });
    }

    private void initAnim() {
        ivLoading.setImageResource(R.drawable.web_view_loading);
        mAnimationDrawable = (AnimationDrawable) ivLoading.getDrawable();
        mAnimationDrawable.start();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings settings = webView.getSettings();
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setSupportZoom(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                stopAnim();
            }
        });
    }

    @Override
    public void showErrorMsg(String msg) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void showReadData(ReadDetailBean readDetailBean) {
       showContent(readDetailBean.getHpContent(),readDetailBean.getHpAuthorIntroduce() +
               " " + readDetailBean.getEditorEmail(),readDetailBean.getAuthor().get(0));
    }


    @Override
    public void showMovieData(MovieDetailBean movieDetailBean) {
        MovieDetailBean.DataBean dataBean=movieDetailBean.getData().get(0);
        showContent(dataBean.getContent(), dataBean.getChargeEdt() +
                " " + dataBean.getEditorEmail(),dataBean.getUser());
    }

    @Override
    public void showMusicData(MusicDetailBean musicDetailBean) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void showReadCommend(CommentBean commentBean) {
        mCommentAdapter = new CommentAdapter(this, commentBean);
        recyclerView.setAdapter(mCommentAdapter);
        tvCommentNum.setText(commentBean.getCount() + "");
    }
    @SuppressLint("SetTextI18n")
    private void showContent(String hpContent, String sIntroduce, AuthorBean authorBean) {
        List<String> list = new ArrayList<>();
        list.add(Constants.ONE_DETAIL_CSS);
        List<String> list1 = new ArrayList<>();
        list1.add(Constants.ONE_DETAIL_JS1);
        list1.add(Constants.ONE_DETAIL_JS2);
        list1.add(Constants.ONE_DETAIL_JS3);
        String htmlData = HtmlUtil.createHtmlData(hpContent,
                list, list1);
        webView.loadData(htmlData, HtmlUtil.MIME_TYPE, HtmlUtil.ENCODING);

        tvIntroduce.setText(sIntroduce);
        Utils.displayImage(this, authorBean.getWebUrl(), ivAuthor, Utils.getImageOptions(
                R.mipmap.ic_launcher_round, 360));
        tvHpAuthor.setText(authorBean.getUserName() + " " + authorBean.getWbName());
        tvAuthIt.setText(authorBean.getDesc());
    }

    private void stopAnim() {
        mAnimationDrawable.stop();
        layoutBottom.setVisibility(View.VISIBLE);
        ivLoading.setVisibility(View.GONE);
    }
}
