package com.xht.indexbar;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.xht.indexbar.adapter.CityListAdapter;
import com.xht.indexbar.bean.CityBean;
import com.xht.indexbar.decoration.DividerItemDecoration;
import com.xht.indexbar.decoration.SectionItemDecoration;
import com.xht.indexbar.pinyinhelper.PinyinHelper;
import com.xht.indexbar.widget.SideIndexBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SideIndexBar.OnLetterTouchListener {

    private TextView mTvOverLay;
    private SideIndexBar mSideIndexBar;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private List<CityBean> allCities = new ArrayList<>();
    private List<CityBean> mResults = new ArrayList<>();
    private PinyinHelper mPinyinHelper;

    private Context mContext;
    private CityListAdapter mAdapter;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mEditText = findViewById(R.id.editText);
        mSideIndexBar = findViewById(R.id.sideIndexBar);
        mTvOverLay = findViewById(R.id.tv_overlay);
        mRecyclerView = findViewById(R.id.recyclerView);

        mPinyinHelper = new PinyinHelper();

        mSideIndexBar.setOverlayTextView(mTvOverLay).setOnLetterTouchListener(this);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                if (TextUtils.isEmpty(keyword)){
                    ((SectionItemDecoration)(mRecyclerView.getItemDecorationAt(0))).setData(allCities);
                    mAdapter.updateData(allCities);
                } else {
                    mResults.clear();
                    // 判断数据中是否含有所输入的字符，将含有该字符的集合转换、排序、更新列表
                    Log.i("xht","afterTextChanged()--keyword==" + keyword);
                    for(int i = 0; i < allCities.size(); i++) {
                        if(allCities.get(i).getName().contains(keyword)) {
                            mResults.add(allCities.get(i));
                            Log.i("xht","result1==" + allCities.get(i));
                        } else if(allCities.get(i).getIndexTag().equals(keyword.toUpperCase())) {
                            mResults.add(allCities.get(i));
                            Log.i("xht","result2==" + allCities.get(i));
                        }
                    }
                    // 使用tinypinyin转换、排序
                    mPinyinHelper.sortSourceDatas(mResults);
                    ((SectionItemDecoration)(mRecyclerView.getItemDecorationAt(0))).setData(mResults);
                    mAdapter.updateData(mResults);
                }
            }
        });

        initData();
        initRecyclerView();
    }

    private void initData() {
        String[] cityArray = getResources().getStringArray(R.array.provinces);
        for (int i = 0; i < cityArray.length; i++) {
            CityBean cityBean = new CityBean();
            cityBean.setName(cityArray[i]);
            allCities.add(cityBean);
        }
        // 使用tinypinyin转换、排序
        mPinyinHelper.sortSourceDatas(allCities);
    }

    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SectionItemDecoration(mContext, allCities), 0);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, allCities), 1);
        mAdapter = new CityListAdapter(mContext, allCities);
        mRecyclerView.setAdapter(mAdapter);

//        for(int i = 0; i < allCities.size(); i++) {
//            Log.i("xht","allCities==" + allCities.get(i).getIndexPinyin());
//        }
    }

    @Override
    public void onIndexChanged(String letter, int position) {
        mTvOverLay.setText(letter);
        //滚动RecyclerView到索引位置
        if (allCities == null || allCities.isEmpty())
            return;
        if (TextUtils.isEmpty(letter))
            return;
        int size = allCities.size();
        for (int i = 0; i < size; i++) {
            if (TextUtils.equals(letter.substring(0, 1), allCities.get(i).getIndexTag().substring(0, 1))) {
                if (mLayoutManager != null) {
                    mLayoutManager.scrollToPositionWithOffset(i, 0);
                    return;
                }
            }
        }
    }
}
