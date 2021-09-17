package com.qihoo.seat.fa.util;

import com.qihoo.seat.fa.ResourceTable;
import ohos.agp.components.AttrHelper;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.LayoutScatter;
import ohos.agp.components.Text;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;

/**
 * ToastUtil
 *
 * @since 2021-08-12
 */
public class ToastUtil {
    private static final int OFFSET_TOAST = 64;
    private static final int TOAST_SHOW_DURATION = 3500;

    /**
     * showToast
     *
     * @param context context
     * @param message message
     */
    public static void showToast(Context context, String message) {
        DirectionalLayout toast =
                (DirectionalLayout) LayoutScatter.getInstance(context).parse(ResourceTable.Layout_toast, null, false);
        Text toastText = (Text) toast.getComponentAt(0);
        toastText.setText(message);
        new ToastDialog(context)
                .setComponent(toast)
                .setOffset(0, AttrHelper.vp2px(OFFSET_TOAST, context))
                .setTransparent(true)
                .setSize(DirectionalLayout.LayoutConfig.MATCH_CONTENT, DirectionalLayout.LayoutConfig.MATCH_CONTENT)
                .setDuration(TOAST_SHOW_DURATION)
                .show();
    }
}
