package com.qihoo.seat.fa.util;


import ohos.agp.render.render3d.BuildConfig;
import ohos.agp.utils.TextTool;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.Locale;

/**
 * LogUtils
 *
 * @since 2021-08-12
 */
public class LogUtils {
    private static final String TAG = "net_work";
    private static final String TAG_LOG = "";

    private static final int DOMAIN_ID = 0xD000F00;

    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, DOMAIN_ID, LogUtils.TAG_LOG);

    private static final String LOG_FORMAT = "%{public}s: %{public}s";

    private LogUtils() {
    }

    /**
     * Print debug log
     *
     * @param tag log tag
     * @param msg log message
     */
    public static void debug(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            HiLog.debug(LABEL_LOG, LOG_FORMAT, tag, msg);
        }
    }

    /**
     * Print info log
     *
     * @param tag log tag
     * @param msg log message
     */
    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            HiLog.info(LABEL_LOG, LOG_FORMAT, tag, msg);
            HiLog.info(LABEL_LOG, msg);
        }
    }

    /**
     * Print info log
     *
     * @param msg log message
     */
    public static void i(String msg) {
        if (BuildConfig.DEBUG) {
            if (!TextTool.isNullOrEmpty(msg)) {
                HiLog.info(LABEL_LOG, LOG_FORMAT, TAG, msg);
            }
        }
    }

    /**
     * Print warn log
     *
     * @param tag log tag
     * @param msg log message
     */
    public static void warn(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            HiLog.warn(LABEL_LOG, LOG_FORMAT, tag, msg);
        }
    }

    /**
     * Print error log
     *
     * @param msg log message
     */
    public static void e(String msg) {
        if (BuildConfig.DEBUG) {
            if (!TextTool.isNullOrEmpty(msg)) {
                HiLog.error(LABEL_LOG, LOG_FORMAT, TAG, msg);
            }
        }
    }

    /**
     * Print error log
     *
     * @param tag log tag
     * @param msg log message
     */
    public static void error(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            HiLog.error(LABEL_LOG, LOG_FORMAT, tag, msg);
        }
    }

    /**
     * Print error log
     *
     * @param msg log message
     */
    public static void error(String msg) {
        if (BuildConfig.DEBUG) {
            HiLog.error(LABEL_LOG, LOG_FORMAT, TAG, msg);
        }
    }
}
