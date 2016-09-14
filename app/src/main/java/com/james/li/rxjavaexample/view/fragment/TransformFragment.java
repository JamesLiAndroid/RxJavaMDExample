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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 *
 */
public class TransformFragment extends Fragment {

    @BindView(R.id.fragment_filter_example_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_filter_example_swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.btn_scan)
    Button btnScan;
    @BindView(R.id.btn_buffer)
    Button btnBuffer;
    @BindView(R.id.btn_group_by)
    Button btnGroupBy;
    @BindView(R.id.btn_window)
    Button btnWindow;
    @BindView(R.id.btn_cast)
    Button btnCast;
    @BindView(R.id.btn_map)
    Button btnMap;
    @BindView(R.id.btn_flat_map)
    Button btnFlatMap;
    @BindView(R.id.btn_concat_map)
    Button btnConcatMap;
    @BindView(R.id.btn_flat_map_iterable)
    Button btnFlatMapIterable;
    @BindView(R.id.btn_switch_map)
    Button btnSwitchMap;
    private ApplicationAdapter mAdapter;

    private List<AppInfo> appInfoList = new ArrayList<>();
    private List<AppInfo> appInfosResult = new ArrayList<>();

    public TransformFragment() {
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
        View view = inflater.inflate(R.layout.fragment_transform, container, false);
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

        Observable.concat(UtilsTestOperator.groupByTest(appInfos))
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

    /*    Observable.from(appInfos).subscribe(new Observer<AppInfo>() {
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
        });*/
    }

    @OnClick({R.id.btn_scan, R.id.btn_buffer, R.id.btn_group_by, R.id.btn_window, R.id.btn_cast, R.id.btn_map, R.id.btn_flat_map, R.id.btn_concat_map, R.id.btn_flat_map_iterable, R.id.btn_switch_map})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_scan:
                // Scan操作符:scan()函数对原始Observable发射的每一项数据都应用一个函数，计算出函数的结果值
                // ，并将该值填充回可观测序列，等待和下一次发射的数据一起使用。有累计的概念在里面
                mSwipeRefreshLayout.setRefreshing(true);
                appInfosResult.clear();
                mAdapter.clearApplications();
                Observable.from(appInfoList)
                       .scan(new Func2<AppInfo, AppInfo, AppInfo>() {
                           @Override
                           public AppInfo call(AppInfo appInfo, AppInfo appInfo2) {
                               if (appInfo.getName().length() > appInfo2.getName().length()) {
                                   return appInfo;
                               } else {
                                   return appInfo2;
                               }
                           }
                       })
                        .distinct()
                        .subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Log.d("TAG","获取第n个app名称："+ApplicationList.getInstance().getList().get(10).getName());
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
                        appInfosResult.add(appInfo);
                        mAdapter.addApplication(appInfosResult.size() - 1, appInfo);
                    }
                });
                break;
            case R.id.btn_buffer:
                // buffer操作符
                UtilsTestOperator.intervalTest(6).buffer(200, TimeUnit.MILLISECONDS, 2)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<Integer>>() {
                    @Override
                    public void call(List<Integer> integers) {
                        Toast.makeText(getActivity(), "每个数组："+integers, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.btn_group_by:
                // groupBy操作符
                mSwipeRefreshLayout.setRefreshing(true);
                appInfosResult.clear();
                mAdapter.clearApplications();
                Observable.concat(UtilsTestOperator.groupByTest(ApplicationList.getInstance().getList()))
                        .subscribe(new Observer<AppInfo>() {
                            @Override
                            public void onCompleted() {
                                mSwipeRefreshLayout.setRefreshing(false);
                                Log.d("TAG","获取第n个app名称："+ApplicationList.getInstance().getList().get(10).getName());
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
                                appInfosResult.add(appInfo);
                                mAdapter.addApplication(appInfosResult.size() - 1, appInfo);
                            }
                        });
                break;
            case R.id.btn_window:
                // window操作符
                mSwipeRefreshLayout.setRefreshing(true);
                appInfosResult.clear();
                mAdapter.clearApplications();
                Observable<Observable<AppInfo>> observable = UtilsTestOperator.distinctTest(ApplicationList.getInstance().getList())
                        .window(3, 2).take(12).distinct();
                observable.subscribe(new Observer<Observable<AppInfo>>() {
                    @Override
                    public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(Observable<AppInfo> appInfoObservable) {
                        System.out.println("subdivide begin......");
                        appInfoObservable.subscribe(new Action1<AppInfo>() {
                            @Override
                            public void call(AppInfo appInfo) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                appInfosResult.add(appInfo);
                                mAdapter.addApplication(appInfosResult.size() - 1, appInfo);
                                Log.d("TAG",""+appInfo.getName());
                            }
                        });
                    }
                });
                break;
            case R.id.btn_cast:
                break;
            case R.id.btn_map:
                mSwipeRefreshLayout.setRefreshing(true);
                appInfosResult.clear();
                mAdapter.clearApplications();
                // map操作符
                Observable.from(appInfoList)
                        .map(new Func1<AppInfo, AppInfo>() {
                            @Override
                            public AppInfo call(AppInfo appInfo) {
                                String currentName = appInfo.getName();
                                String lowerCaseName = currentName.toLowerCase();
                                appInfo.setName(lowerCaseName);
                                return appInfo;
                            }
                        }).subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Log.d("TAG","获取第n个app名称："+ApplicationList.getInstance().getList().get(10).getName());
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
                        appInfosResult.add(appInfo);
                        mAdapter.addApplication(appInfosResult.size() - 1, appInfo);
                    }
                });
                break;
            case R.id.btn_flat_map:
                // flatmap操作符，发射的结果由于交叉而导致顺序错乱
                Observable.from(appInfoList)
                        .flatMap(new Func1<AppInfo, Observable<String>>() {
                            @Override
                            public Observable<String> call(AppInfo appInfo) {
                                String name = appInfo.getName().toUpperCase();
                                return Observable.just(name.substring(1));
                            }
                        })
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                Log.d("TAG", "打印的字符："+ s);
                                Toast.makeText(getActivity(), "打印的字符："+ s, Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.btn_concat_map:
                // concatmap操作符, 发射的结果保持原来的顺序
                Observable.from(appInfoList)
                        .concatMap(new Func1<AppInfo, Observable<String>>() {
                            @Override
                            public Observable<String> call(AppInfo appInfo) {
                                String name = appInfo.getName().toUpperCase();
                                return Observable.just(name.substring(1));
                            }
                        })
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                Log.d("TAG", "打印的字符："+ s);
                                Toast.makeText(getActivity(), "打印的字符："+ s, Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.btn_flat_map_iterable:
                // FlatMapIterable:它将源数据两两结成对并生成Iterable，而不是原始数据项和生成的Observables。
                break;
            case R.id.btn_switch_map:
                // switchMap()和flatMap()很像,取消订阅并停止监视之前那个数据项产生的Observable，并开始监视当前发射的这一个。
                break;
        }
    }
}
