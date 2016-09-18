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

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.james.li.rxjavaexample.R;
import com.james.li.rxjavaexample.adapter.ApplicationAdapter;
import com.james.li.rxjavaexample.bean.AppInfo;
import com.james.li.rxjavaexample.bean.ApplicationList;
import com.james.li.rxjavaexample.cache.ACache;
import com.james.li.rxjavaexample.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.joins.Pattern2;
import rx.joins.Plan0;
import rx.observables.JoinObservable;
import rx.schedulers.Schedulers;

/**
 * create an instance of this fragment.
 */
public class CombinationFragment extends Fragment {


    @BindView(R.id.btn_switch)
    Button btnSwitch;
    @BindView(R.id.btn_start_with)
    Button btnStartWith;
    @BindView(R.id.btn_merge)
    Button btnMerge;
    @BindView(R.id.btn_zip)
    Button btnZip;
    @BindView(R.id.btn_join)
    Button btnJoin;
    @BindView(R.id.btn_conbine_latest)
    Button btnConbineLatest;
    @BindView(R.id.btn_and_then_when)
    Button btnAndThenWhen;
    @BindView(R.id.btn_merge_delay_error)
    Button btnMergeDelayError;
    @BindView(R.id.ll_btns_filter)
    LinearLayout llBtnsFilter;

    public CombinationFragment() {
        // Required empty public constructor
    }


    @BindView(R.id.fragment_filter_example_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.fragment_filter_example_swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ApplicationAdapter mAdapter;

    private List<AppInfo> appInfoList = new ArrayList<>();
    private List<AppInfo> appInfosResult = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_combination, container, false);
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
        // 测试merge操作符

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

    /**
     * merge操作符，加载当前存在的app数据
     *
     * @param appInfos
     */
    private void mergeReversedCurrentApps(List<AppInfo> appInfos) {
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
        // 测试merge操作符
        List<AppInfo> mergeReversedApps = Lists.reverse(appInfos);
        Observable<AppInfo> observableApps = Observable.from(appInfos);
        Observable<AppInfo> observableReversedApps = Observable.from(mergeReversedApps);
        Observable<AppInfo> finalApps = Observable.merge(observableReversedApps, observableApps);

        finalApps.subscribe(new Observer<AppInfo>() {
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

    /**
     * mergeDelayError操作符，加载当前存在的app数据
     *
     * @param appInfos
     */
    private void mergeDelayErrorCurrentApps(List<AppInfo> appInfos) {
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
        // 测试merge操作符
        List<AppInfo> mergeReversedApps = Lists.reverse(appInfos);
        Observable<AppInfo> observableApps = Observable.from(appInfos);
        Observable<AppInfo> observableReversedApps = Observable.from(mergeReversedApps);
        Observable<AppInfo> finalApps = Observable.mergeDelayError(observableReversedApps, observableApps);

        finalApps.subscribe(new Observer<AppInfo>() {
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

    /**
     * mergeDelayError操作符，加载当前存在的app数据
     *
     * @param appInfos
     */
    private void zipTitleCurrentApps(List<AppInfo> appInfos) {
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
        // 测试zip操作符
        //List<AppInfo> mergeReversedApps = Lists.reverse(appInfos);
        Observable<AppInfo> observableApps = Observable.from(appInfos);
        Observable<Long> longTime = Observable.interval(1, TimeUnit.SECONDS);

        Observable<AppInfo> finalApps = Observable.zip(longTime, observableApps, new Func2<Long, AppInfo, AppInfo>() {
            @Override
            public AppInfo call(Long aLong, AppInfo appInfo) {
                return Utils.updateTitle(appInfo, aLong);
            }
        }).observeOn(AndroidSchedulers.mainThread());

        finalApps.subscribe(new Observer<AppInfo>() {
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
                /*if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }*/
                /*int position = appInfoList.size() - 1;
                mAdapter.addApplication(position, appInfo);
                mRecyclerView.smoothScrollToPosition(position);*/
                appInfoList.add(appInfo);
                mAdapter.addApplication(appInfoList.size() - 1, appInfo);
            }
        });
    }

    /**
     * join操作符，加载当前存在的app数据
     *
     * @param appInfos
     */
    private void joinCurrentApps(List<AppInfo> appInfos) {
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
        // 测试zip操作符
        final List<AppInfo> finalAppInfos = appInfos;
        Observable<AppInfo> observableApps = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, AppInfo>() {
                    @Override
                    public AppInfo call(Long aLong) {
                        return finalAppInfos.get(aLong.intValue());
                    }
                });
        Observable<Long> longTime = Observable.interval(1000, TimeUnit.MILLISECONDS);

        observableApps.join(
                        longTime,

                        new Func1<AppInfo, Observable<Long>>() {
                            @Override
                            public Observable<Long> call(AppInfo appInfo) {
                                return Observable.timer(2, TimeUnit.SECONDS);
                            }
                        },

                        new Func1<Long, Observable<Long>>() {
                            @Override
                            public Observable<Long> call(Long time) {
                                return Observable.timer(0, TimeUnit.SECONDS);
                            }
                        },

                        new Func2<AppInfo, Long, AppInfo>() {
                            @Override
                            public AppInfo call(AppInfo appInfo, Long aLong) {
                                return Utils.updateTitle(appInfo,aLong);
                            }
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .take(10)
                .subscribe(new Observer<AppInfo>() {
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

    /**
     * combineLatest操作符，加载当前存在的app数据
     *
     * @param appInfos
     */
    private void combineLatestCurrentApps(List<AppInfo> appInfos) {
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
        // 测试zip操作符
        //List<AppInfo> mergeReversedApps = Lists.reverse(appInfos);
        final List<AppInfo> finalAppInfos = appInfos;
        Observable<AppInfo> observableApps = Observable.interval(1000, TimeUnit.MILLISECONDS).map(new Func1<Long, AppInfo>() {
            @Override
            public AppInfo call(Long aLong) {
                return finalAppInfos.get(aLong.intValue());
            }
        });
        Observable<Long> longTime = Observable.interval(1500, TimeUnit.MILLISECONDS);

        Observable<AppInfo> finalApps = Observable
                .combineLatest(observableApps, longTime, new Func2<AppInfo, Long, AppInfo>() {
                    @Override
                    public AppInfo call(AppInfo appInfo, Long aLong) {
                        return Utils.updateTitle(appInfo, aLong);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .take(20);

        finalApps.subscribe(new Observer<AppInfo>() {
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

    /**
     * and-then-when操作符，加载当前存在的app数据
     *
     * @param appInfos
     */
    private void andThenWhenCurrentApps(List<AppInfo> appInfos) {
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
        // 测试and-then-when操作符
        //List<AppInfo> mergeReversedApps = Lists.reverse(appInfos);
        final List<AppInfo> finalAppInfos = appInfos;
        Observable<AppInfo> observableApps = Observable.from(finalAppInfos);
        Observable<Long> longTime = Observable.interval(1, TimeUnit.SECONDS);

        Pattern2<AppInfo, Long> pattern = JoinObservable.from(observableApps).and(longTime);

        Plan0<AppInfo> plan = pattern.then(new Func2<AppInfo, Long, AppInfo>() {
            @Override
            public AppInfo call(AppInfo appInfo, Long aLong) {
                return Utils.updateTitle(appInfo, aLong);
            }
        });

        JoinObservable.when(plan).toObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppInfo>() {
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

    @OnClick({R.id.btn_switch, R.id.btn_start_with, R.id.btn_merge, R.id.btn_zip, R.id.btn_join,
            R.id.btn_conbine_latest, R.id.btn_and_then_when, R.id.btn_merge_delay_error})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_switch:
                break;
            case R.id.btn_start_with:
                break;
            case R.id.btn_merge:
                // merge操作符
                mergeReversedCurrentApps(ApplicationList.getInstance().getList());
                break;
            case R.id.btn_zip:
                // zip操作符
                zipTitleCurrentApps(ApplicationList.getInstance().getList());
                break;
            case R.id.btn_join:
                // join操作符
                joinCurrentApps(ApplicationList.getInstance().getList());
                break;
            case R.id.btn_conbine_latest:
                // combineLatest操作符
                combineLatestCurrentApps(ApplicationList.getInstance().getList());
                break;
            case R.id.btn_and_then_when:
                // and then when
                andThenWhenCurrentApps(ApplicationList.getInstance().getList());
                break;
            case R.id.btn_merge_delay_error:
                // mergeDelayError操作符
                mergeDelayErrorCurrentApps(ApplicationList.getInstance().getList());
                break;
        }
    }
}
