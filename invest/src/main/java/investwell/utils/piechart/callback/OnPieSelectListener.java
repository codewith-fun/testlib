package investwell.utils.piechart.callback;

import androidx.annotation.NonNull;

import investwell.utils.piechart.data.IPieInfo;


/**
 * Created by 大灯泡 on 2017/11/10.
 */

public interface OnPieSelectListener<T extends IPieInfo> {
    /**
     * 选中的回调
     *
     * @param pieInfo   数据实体
     * @param isFloatUp 是否浮起
     */
    void onSelectPie(@NonNull T pieInfo, boolean isFloatUp);
}
