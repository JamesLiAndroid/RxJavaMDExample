package com.james.li.rxjavaexample.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.james.li.rxjavaexample.R;
import com.james.li.rxjavaexample.adapter.ApplicationAdapter;
import com.james.li.rxjavaexample.bean.AppInfo;
import com.james.li.rxjavaexample.bean.AppInfoRich;
import com.james.li.rxjavaexample.bean.ApplicationList;
import com.james.li.rxjavaexample.cache.ACache;
import com.james.li.rxjavaexample.utils.Utils;
import com.james.li.rxjavaexample.utils.UtilsTestOperator;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 简单的HelloWorld内容
 * 通过
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link  interface
 * to handle interaction events.
 * Use the {@link } factory method to
 * create an instance of this fragment.
 */
public class HelloWorldFragment extends Fragment {


    @BindView(R.id.fragment_first_example_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.fragment_first_example_swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;

    // 测试操作符
    @BindView(R.id.btn_from)
    Button btnFrom;
    @BindView(R.id.btn_just)
    Button btnJust;
    @BindView(R.id.btn_repeat)
    Button btnRepeat;
    @BindView(R.id.btn_defer)
    Button btnDefer;
    @BindView(R.id.btn_range)
    Button btnRange;
    @BindView(R.id.btn_interval)
    Button btnInterval;
    @BindView(R.id.btn_timer)
    Button btnTimer;
    @BindView(R.id.ll_btns)
    LinearLayout llBtns;

    private ApplicationAdapter mAdapter;

    private File mFilesDir;

    private Gson gson = new Gson();

    public HelloWorldFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hello_world, container, false);
        ButterKnife.bind(this, view);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        manager.setSmoothScrollbarEnabled(true);
        mRecyclerView.setLayoutManager(manager);

        mAdapter = new ApplicationAdapter(new ArrayList<AppInfo>());
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTheList();
            }
        });

        // 开始加载数据
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setRefreshing(true);
        mRecyclerView.setVisibility(View.GONE);

        getFileDir().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        mFilesDir = file;
                        refreshTheList();
                    }
                });

    }

    /**
     * 刷新App数据
     */
    private void refreshTheList() {
        getApps().toSortedList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<AppInfo>>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(getActivity(), "刷新完毕！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "数据加载出错！", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(List<AppInfo> appInfos) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mAdapter.addApplications(appInfos);
                        mSwipeRefreshLayout.setRefreshing(false);
                        storeList(appInfos);
                    }
                });
    }

    /**
     * 存储获取的应用信息
     *
     * @param appInfos
     */
    private void storeList(final List<AppInfo> appInfos) {
        ApplicationList.getInstance().setList(appInfos);

        Schedulers.io().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                Type appInfoType = new TypeToken<List<AppInfo>>() {
                }.getType();
                String json = gson.toJson(appInfos, appInfoType);
                ACache.get(getActivity()).put("APPS", json, 60 * 2); // 设置缓存时间
            }
        });
    }

    /**
     * 获取文件所在路径
     *
     * @return
     */
    private Observable<File> getFileDir() {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                subscriber.onNext(getActivity().getApplication().getFilesDir());
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private Observable<AppInfo> getApps() {
        return Observable.create(new Observable.OnSubscribe<AppInfo>() {
            @Override
            public void call(Subscriber<? super AppInfo> subscriber) {
                List<AppInfo> appInfos = null;
                if (ACache.get(getActivity()).getAsString("APPS") != null) {
                    Log.d("TAG", "json数据存在");
                    if (ApplicationList.getInstance().getList() != null) {
                        appInfos = ApplicationList.getInstance().getList();
                        Log.d("TAG", "从列表获取");
                    } else {
                        Log.d("TAG", "从json数据获取");
                        String json = ACache.get(getActivity()).getAsString("APPS");
                        appInfos = gson.fromJson(json,
                                new TypeToken<List<AppInfo>>() {
                                }.getType());
                    }
                    // 直接获取内容
                    for (AppInfo appInfo : appInfos) {
                        if (subscriber.isUnsubscribed()) {
                            return;
                        }
                        subscriber.onNext(appInfo);
                    }

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                    return;
                }
                Log.d("TAG", "json数据不存在！");
                List<AppInfoRich> apps = new ArrayList<AppInfoRich>();

                final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);

                List<ResolveInfo> infos = getActivity().getPackageManager().queryIntentActivities(intent, 0);
                for (ResolveInfo info : infos) {
                    apps.add(new AppInfoRich(getActivity(), info));
                }

                for (AppInfoRich appInfoRich : apps) {
                    Bitmap icon = Utils.drawableToBitmap(appInfoRich.getIcon());
                    String name = appInfoRich.getName();
                    String iconPath = mFilesDir + "/" + name;
                    Utils.storeBitmap(getActivity(), icon, iconPath);

                    if (subscriber.isUnsubscribed()) {
                        return;
                    }

                    subscriber.onNext(new AppInfo(appInfoRich.getLastUpdateTime(), name, iconPath));
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        });
    }

    private void loadAppsFromCurrentList(List<AppInfo> appInfos) {


    }

    @OnClick({R.id.btn_from, R.id.btn_just, R.id.btn_repeat, R.id.btn_defer, R.id.btn_range, R.id.btn_interval, R.id.btn_timer})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_from:
                // from操作符
                UtilsTestOperator.fromTest().subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(getActivity(), "from操作符测试完毕", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "读取数字错误！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d("TAG","From操作获取的数字为："+integer);
                        Toast.makeText(getActivity(), "获取的数字为："+integer, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.btn_just:
                // just操作符
                UtilsTestOperator.justTest(1,3,5).subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(getActivity(), "just操作符测试完毕", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "读取数字错误！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d("TAG","Just操作获取的数字为："+integer);
                        Toast.makeText(getActivity(), "获取的数字为："+integer, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.btn_repeat:
                break;
            case R.id.btn_defer:
                break;
            case R.id.btn_range:
                break;
            case R.id.btn_interval:
                break;
            case R.id.btn_timer:
                break;
        }
    }
}
