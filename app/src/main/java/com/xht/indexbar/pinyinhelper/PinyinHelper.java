package com.xht.indexbar.pinyinhelper;

import android.util.Log;

import com.github.promeg.pinyinhelper.Pinyin;
import com.xht.indexbar.bean.BaseIndexPinyinBean;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by xht on 2018/3/7.
 */

public class PinyinHelper {

    //汉语-->拼音
    public void convert(List<? extends BaseIndexPinyinBean> datas) {
        if (null == datas || datas.isEmpty()) {
            return;
        }
        int size = datas.size();
        for (int i = 0; i < size; i++) {
            BaseIndexPinyinBean indexPinyinBean = datas.get(i);
            StringBuilder pySb = new StringBuilder();
            String target = indexPinyinBean.getTarget();//取出需要被拼音化的字段
            //遍历target的每个char得到它的全拼音
            for (int i1 = 0; i1 < target.length(); i1++) {
                //利用TinyPinyin将char转成拼音
                //查看源码，方法内 如果char为汉字，则返回大写拼音
                //如果c不是汉字，则返回String.valueOf(c)
                pySb.append(Pinyin.toPinyin(target.charAt(i1)).toUpperCase());
            }
            indexPinyinBean.setIndexPinyin(pySb.toString());//设置城市名全拼音
        }
    }

    //拼音-->indexTag
    public void fillIndexTag(List<? extends BaseIndexPinyinBean> datas) {
        if (null == datas || datas.isEmpty()) {
            return;
        }
        int size = datas.size();
        for (int i = 0; i < size; i++) {
            BaseIndexPinyinBean indexPinyinBean = datas.get(i);
            String tagString = indexPinyinBean.getIndexPinyin().toString().substring(0, 1);
            // 匹配首字母
            if (tagString.matches("[A-Z]")) {//如果是A-Z字母开头
                indexPinyinBean.setIndexTag(tagString);
            } else {//特殊字母这里统一用#处理
                indexPinyinBean.setIndexTag("#");
            }
        }
    }

    //对源数据进行排序
    public void sortSourceDatas(List<? extends BaseIndexPinyinBean> datas) {
        if (null == datas || datas.isEmpty()) {
            return;
        }
        convert(datas);
        fillIndexTag(datas);
        //对数据源进行排序
        Collections.sort(datas, new Comparator<BaseIndexPinyinBean>() {
            @Override
            public int compare(BaseIndexPinyinBean lhs, BaseIndexPinyinBean rhs) {
                if (lhs.getIndexTag().equals("#")) {
                    return 1;
                } else if (rhs.getIndexTag().equals("#")) {
                    return -1;
                } else {
                    return lhs.getIndexPinyin().compareTo(rhs.getIndexPinyin());
                }
            }
        });
    }

    //对IndexBar的数据源进行排序(右侧栏),在 sortSourceDatas方法后调用
    public void getSortedIndexDatas(List<? extends BaseIndexPinyinBean> sourceDatas, List<String> indexDatas) {
        if (null == sourceDatas || sourceDatas.isEmpty()) {
            return;
        }
        //按数据源来 此时sourceDatas 已经有序
        int size = sourceDatas.size();
        String baseIndexTag;
        for (int i = 0; i < size; i++) {
            baseIndexTag = sourceDatas.get(i).getIndexTag();
            if (!indexDatas.contains(baseIndexTag)) {//则判断是否已经将这个索引添加进去，若没有则添加
                indexDatas.add(baseIndexTag);
            }
        }
    }

}
