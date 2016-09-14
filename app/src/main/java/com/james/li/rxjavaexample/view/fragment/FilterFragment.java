package com.james.li.rxjavaexample.view.fragment;

import android.content.Context;
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
import com.james.li.rxjavaexample.bean.ApplicationList;
import com.james.li.rxjavaexample.cache.ACache;
import com.james.li.rxjavaexample.utils.UtilsTestOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 过滤操作功能的Fragment，需要等首页加载完成之后下拉刷新才能加载正常数据
 * A simple {@link Fragment} subclass.
 */
public class FilterFragment extends Fragment {

    @BindView(R.id.fragment_filter_example_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.fragment_filter_example_swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.btn_filter)
    Button btnFilter;
    @BindView(R.id.btn_take)
    Button btnTake;
    @BindView(R.id.btn_distinct)
    Button btnDistinct;
    @BindView(R.id.btn_first)
    Button btnFirst;
    @BindView(R.id.btn_skip)
    Button btnSkip;
    @BindView(R.id.btn_element_at)
    Button btnElementAt;
    @BindView(R.id.btn_sampling)
    Button btnSampling;
    @BindView(R.id.btn_timeout)
    Button btnTimeout;
    @BindView(R.id.btn_debounce)
    Button btnDebounce;
    @BindView(R.id.ll_btns_filter)
    LinearLayout llBtnsFilter;

    private ApplicationAdapter mAdapter;

    private List<AppInfo> appInfoList = new ArrayList<>();
    private List<AppInfo> appInfosResult = new ArrayList<>();


    public FilterFragment() {
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
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
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
                loadCurrentApps(ApplicationList.getInstance().getList());
            }
        });

        // 开始加载数据
        mSwipeRefreshLayout.setEnabled(true);
        //mRecyclerView.setVisibility(View.GONE);

        loadCurrentApps(ApplicationList.getInstance().getList());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * 无过滤操作符，加载当前存在的app数据
     *
     * @param appInfos
     */
    private void loadCurrentApps(List<AppInfo> appInfos) {
        mSwipeRefreshLayout.setRefreshing(true);
        appInfoList.clear();
        mAdapter.clearApplications();

        if (appInfos == null || appInfos.size() == 0) {
            // 从缓存获取
            if (ACache.get(getActivity()).getAsString("APPS") != null) {
                Log.d("TAG", "filter:从json数据获取");
                String json = ACache.get(getActivity()).getAsString("APPS");
                Gson gson = new Gson();
                appInfos = gson.fromJson(json,
                        new TypeToken<List<AppInfo>>() {
                        }.getType());
            }
        }

        if (appInfos == null || appInfos.size() == 0) {
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        Observable.from(appInfos).subscribe(new Observer<AppInfo>() {
            @Override
            public void onCompleted() {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "加载当前数据完成！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "加载出错！", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onNext(AppInfo appInfo) {
                appInfoList.add(appInfo);
                mAdapter.addApplication(appInfoList.size() - 1, appInfo);
            }
        });
    }

    @OnClick({R.id.btn_filter, R.id.btn_take, R.id.btn_distinct, R.id.btn_first, R.id.btn_skip,
            R.id.btn_element_at, R.id.btn_sampling, R.id.btn_timeout, R.id.btn_debounce})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_filter:
                mSwipeRefreshLayout.setRefreshing(true);
                appInfosResult.clear();
                mAdapter.clearApplications();
                // filter操作符
                UtilsTestOperator.filterTest(appInfoList).subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "filter操作符加载数据完成！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "加载出错！", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        appInfosResult.add(appInfo);
                        mAdapter.addApplication(appInfosResult.size() - 1, appInfo);
                    }
                });
                break;
            case R.id.btn_take:
                mSwipeRefreshLayout.setRefreshing(true);
                appInfosResult.clear();
                mAdapter.clearApplications();
                // take操作符
                UtilsTestOperator.takeTest(appInfoList).subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "take操作符加载数据完成！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "加载出错！", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        appInfosResult.add(appInfo);
                        mAdapter.addApplication(appInfosResult.size() - 1, appInfo);
                    }
                });
                break;
            case R.id.btn_distinct:
                mSwipeRefreshLayout.setRefreshing(true);
                appInfosResult.clear();
                mAdapter.clearApplications();
                // distinct操作符
                UtilsTestOperator.distinctTest(appInfoList).subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "distinct操作符加载数据完成！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "加载出错！", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        appInfosResult.add(appInfo);
                        mAdapter.addApplication(appInfosResult.size() - 1, appInfo);
                    }
                });
                break;
            case R.id.btn_first:
                mSwipeRefreshLayout.setRefreshing(true);
                appInfosResult.clear();
                mAdapter.clearApplications();
                // first操作符
                UtilsTestOperator.firstTest(appInfoList).subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "first操作符加载数据完成！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "加载出错！", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        appInfosResult.add(appInfo);
                        mAdapter.addApplication(appInfosResult.size() - 1, appInfo);
                    }
                });
                break;
            case R.id.btn_skip:
                // skip操作符
                mSwipeRefreshLayout.setRefreshing(true);
                appInfosResult.clear();
                mAdapter.clearApplications();
                // first操作符
                UtilsTestOperator.skipTest(appInfoList).subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "skip操作符加载数据完成！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "加载出错！", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        appInfosResult.add(appInfo);
                        mAdapter.addApplication(appInfosResult.size() - 1, appInfo);
                    }
                });
                break;
            case R.id.btn_element_at:
                // ElementAt操作符
                /**
                 * 需要强调的是，一定将
                 *  .subscribeOn(Schedulers.io())
                 *  .observeOn(AndroidSchedulers.mainThread())
                 * 放在变换，过滤，组合等操作符之后，放在subscribe()方法之前
                 */
                mSwipeRefreshLayout.setRefreshing(true);
                appInfosResult.clear();
                mAdapter.clearApplications();
                UtilsTestOperator.elementAtTest(appInfoList).subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "ElementAt操作符加载数据完成！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "加载出错！", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        appInfosResult.add(appInfo);
                        mAdapter.addApplication(appInfosResult.size() - 1, appInfo);
                    }
                });
                break;
            case R.id.btn_sampling:
                // sampling操作符，写在本地
                Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        for (int i = 0; i < 10; i++) {
                            try {
                                Thread.sleep(1000);
                                subscriber.onNext(String.valueOf(i));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                subscriber.onError(e);
                            }
                        }
                        subscriber.onCompleted();
                    }
                }).sample(3, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "sample操作符加载数据完成！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        try {
                            Toast.makeText(getActivity(), "加载出错！", Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d("TAG","onNext:"+s);
                        Toast.makeText(getActivity(), "sample操作符加载数据+" + s, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.btn_timeout:
                break;
            case R.id.btn_debounce:
                break;
            default:
                break;
        }
    }
}
