/**
 * Lenovo.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.wind.algorithm.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author tangxx3
 * @version $Id: Profiler.java, v 0.1 2016年3月9日 下午2:24:44 tangxx3 Exp $
 */
public final class Profiler {
    /** 线程时间栈. */
    private static final ThreadLocal<TimeEntry>           entryStack        = new ThreadLocal<TimeEntry>();

    private static final ThreadLocal<Map<String, Object>> threadLocalHolder = new ThreadLocal<Map<String, Object>>();

    private static final String                           START_FLAG        = "START_FLAG";

    /** 空字符串. */
    private static final String                           EMPTY_STRING      = "";

    private Profiler() {

    }

    public static void put(String key, Object value) {
        Map<String, Object> m = threadLocalHolder.get();
        if (m == null) {
            m = new HashMap<String, Object>();
            threadLocalHolder.set(m);
        }
        m.put(key, value);
    }

    public static Object get(String key) {
        Map<String, Object> m = threadLocalHolder.get();
        if (m != null) {
            return m.get(key);
        }
        return null;
    }

    public static boolean isStart() {
        return "1".equals(get(START_FLAG));
    }

    /**
     * 开始一个线程计时统计.
     */
    public static void start() {
        start((String) null);
    }

    /**
     * 开始计时.
     *
     * @param message 第一个计时单元的信息
     */
    public static void start(String message) {
        entryStack.set(new TimeEntry(message, null, null));
        put(START_FLAG, "1");
    }

    /**
     * 清除计时器。
     * <p>
     * 清除以后必须再次调用<code>start</code>方可重新计时。
     * </p>
     * 
     */
    public static void reset() {
        entryStack.set(null);
        threadLocalHolder.set(null);
    }

    /**
     * 开始一个新的子计时单元.
     *
     * @param message 新计时单元的信息
     */
    public static void enter(String message) {
        TimeEntry currentTimeEntry = getCurrentTimeEntry();

        if (currentTimeEntry != null) {
            currentTimeEntry.enterSubTimeEntry(message);
        }
    }

    /**
     * 结束最近的一个entry，记录结束时间.
     */
    public static void release() {
        TimeEntry currentTimeEntry = getCurrentTimeEntry();

        if (currentTimeEntry != null) {
            currentTimeEntry.release();
        }
    }

    /**
     * 取得耗费的总时间.
     *
     * @return 耗费的总时间，如果未开始计时，则返回<code>-1</code>
     */
    public static long getDuration() {
        TimeEntry entry = (TimeEntry) entryStack.get();

        if (entry != null) {
            return entry.getDuration();
        } else {
            return -1;
        }
    }

    /**
     * 列出所有的计时单元信息.
     *
     * @return 列出所有entry，并统计各自所占用的时间
     */
    public static String dump() {
        return dump("", "");
    }

    /**
     * 列出所有的计时单元信息.
     *
     * @param prefix 前缀
     *
     * @return 列出所有entry，并统计各自所占用的时间
     */
    public static String dump(String prefix) {
        return dump(prefix, prefix);
    }

    /**
     * 列出所有的计时单元信息.
     *
     * @param prefix1 首行前缀
     * @param prefix2 后续行前缀
     *
     * @return 列出所有entry，并统计各自所占用的时间
     */
    public static String dump(String prefix1, String prefix2) {
        TimeEntry entry = (TimeEntry) entryStack.get();

        if (entry != null) {
            return entry.toString(prefix1, prefix2);
        } else {
            return EMPTY_STRING;
        }
    }

    /**
     * 取得第一个entry.
     *
     * @return 第一个entry，如果不存在，则返回<code>null</code>
     */
    public static TimeEntry getTimeEntry() {
        return (TimeEntry) entryStack.get();
    }

    /**
     * 取得最近的一个entry.
     *
     * @return 最近的一个entry，如果不存在，则返回<code>null</code>
     */
    private static TimeEntry getCurrentTimeEntry() {
        TimeEntry subTimeEntry = (TimeEntry) entryStack.get();
        TimeEntry entry = null;

        if (subTimeEntry != null) {
            do {
                entry = subTimeEntry;
                subTimeEntry = entry.getLastTimeEntry();
            } while (subTimeEntry != null);
        }

        return entry;
    }

    /**
     * 计时单元.
     * 
     * @author pengzj2
     * @version $Id: HttpProfiler.java, v 0.1 2012-10-31 下午9:08:19 pengzj2 Exp $
     */
    public static final class TimeEntry {

        /** 子计时单元. */
        private final List<TimeEntry> subEntries = new ArrayList<TimeEntry>(4);

        /** 描述信息. */
        private final String          message;

        /** 父计时单元. */
        private final TimeEntry       parentEntry;

        /** 第一个计时单元. */
        private final TimeEntry       firstEntry;

        /** 时间基线，第一个计时单元开始时间. */
        private final long            baseTime;

        /** 开始计时时间. */
        private final long            startTime;

        /** 结束时间. */
        private long                  endTime;

        /**
         * 构造方法.
         * 
         * @param message     消息
         * @param parentEntry 父计时单元
         * @param firstEntry  第一个计时单元
         */
        private TimeEntry(String message, TimeEntry parentEntry, TimeEntry firstEntry) {
            this.message = message;
            this.startTime = System.currentTimeMillis();
            this.parentEntry = parentEntry;
            this.firstEntry = null == firstEntry ? this : firstEntry;
            this.baseTime = (firstEntry == null) ? 0 : firstEntry.startTime;
        }

        /**
         * 取计时单元描述信息.
         * 
         * @return
         */
        public String getMessage() {
            return message;
        }

        /**
         * 取得计时单元相对于第一个计时单元的开始时间.
         * 
         * @return
         */
        public long getStartTime() {
            return (baseTime > 0) ? (startTime - baseTime) : 0;
        }

        /**
         * 取得计时单元相对于第一个计时单元的结束时间
         *
         * @return 相对结束时间，如果entry还未结束，则返回<code>-1</code>
         */
        public long getEndTime() {
            if (endTime < baseTime) {
                return -1;
            } else {
                return endTime - baseTime;
            }
        }

        /**
         * 取得计时单元的持续时间.
         * <p>
         * 包含子计时单元消耗的时间。
         * </p>
         *
         * @return 计时单元的时间，如果计时单元还未结束，则返回<code>-1</code>
         */
        public long getDuration() {
            if (endTime < startTime) {
                return -1;
            } else {
                return endTime - startTime;
            }
        }

        /**
         * 取得计时单元消耗时间.
         * <p>
         * 不包括子计时单元消耗的时间。
         * </p>
         *
         * @return entry自身所用的时间，如果entry还未结束，则返回<code>-1</code>
         */
        public long getDurationOfSelf() {
            long duration = getDuration();

            if (duration < 0) {
                return -1;
            }

            if (subEntries.isEmpty()) {
                return duration;
            }

            for (int i = 0, len = subEntries.size(); i < len; i++) {
                TimeEntry subTimeEntry = (TimeEntry) subEntries.get(i);
                duration -= subTimeEntry.getDuration();
            }

            if (duration < 0) {
                return -1;
            } else {
                return duration;
            }
        }

        /**
         * 取得当前计时单元在父计时单元中所占的时间百分比.
         *
         * @return 百分比
         */
        public double getPecentage() {
            double parentDuration = 0;
            double duration = getDuration();

            if ((parentEntry != null) && parentEntry.isReleased()) {
                parentDuration = parentEntry.getDuration();
            }

            if ((duration > 0) && (parentDuration > 0)) {
                return duration / parentDuration;
            } else {
                return 0;
            }
        }

        /**
         * 取得当前计时单元在整个计时周期中所占的时间百分比。
         *
         * @return 百分比
         */
        public double getPecentageOfAll() {
            double firstDuration = 0;
            double duration = getDuration();

            if ((firstEntry != null) && firstEntry.isReleased()) {
                firstDuration = firstEntry.getDuration();
            }

            if ((duration > 0) && (firstDuration > 0)) {
                return duration / firstDuration;
            } else {
                return 0;
            }
        }

        /**
         * 取得所有子计时单元.
         *
         * @return 所有子entries的列表（不可更改）
         */
        public List<TimeEntry> getSubEntries() {
            return Collections.unmodifiableList(subEntries);
        }

        /**
         * 结束当前计时单元，并记录结束时间。
         */
        private void release() {
            endTime = System.currentTimeMillis();
        }

        /**
         * 判断当前计时单元是否结束。
         *
         * @return 如果entry已经结束，则返回<code>true</code>
         */
        private boolean isReleased() {
            return endTime > 0;
        }

        /**
         * 创建一个新的子计时单元。
         *
         * @param message 子计时单元描述的信息
         */
        private void enterSubTimeEntry(String message) {
            TimeEntry subTimeEntry = new TimeEntry(message, this, firstEntry);

            subEntries.add(subTimeEntry);
        }

        /**
         * 取得最后一个子计时单元.
         *
         * @return 未结束的子entry，如果没有子entry，或所有entry均已结束，则返回<code>null</code>
         */
        private TimeEntry getLastTimeEntry() {
            TimeEntry subTimeEntry = null;

            if (!subEntries.isEmpty()) {
                subTimeEntry = (TimeEntry) subEntries.get(subEntries.size() - 1);

                if (subTimeEntry.isReleased()) {
                    subTimeEntry = null;
                }
            }

            return subTimeEntry;
        }

        /**
         * 将entry转换成字符串的表示。
         *
         * @return 字符串表示的entry
         */
        public String toString() {
            return toString("", "");
        }

        /**
         * 将计时单元转换成字符串的表示.
         *
         * @param prefix1 首行前缀
         * @param prefix2 后续行前缀
         *
         * @return 字符串表示的计时单元
         */
        private String toString(String prefix1, String prefix2) {
            StringBuffer buffer = new StringBuffer();

            toString(buffer, prefix1, prefix2);

            return buffer.toString();
        }

        /**
         * 将计时单元转换成字符串的表示.
         *
         * @param buffer    字符串buffer
         * @param prefix1   首行前缀
         * @param prefix2   后续行前缀
         */
        private void toString(StringBuffer buffer, String prefix1, String prefix2) {
            buffer.append(prefix1);

            String localMessage = getMessage();
            long startTime1 = getStartTime();
            long duration = getDuration();
            long durationOfSelf = getDurationOfSelf();
            double percent = getPecentage();
            double percentOfAll = getPecentageOfAll();

            Object[] params = new Object[] { localMessage, // {0} - entry信息 
                    Long.valueOf(startTime1), // {1} - 起始时间
                    Long.valueOf(duration), // {2} - 持续总时间
                    Long.valueOf(durationOfSelf), // {3} - 自身消耗的时间
                    Double.valueOf(percent), // {4} - 在父entry中所占的时间比例
                    Double.valueOf(percentOfAll) // {5} - 在总时间中所旧的时间比例
            };

            StringBuffer pattern = new StringBuffer("{1,number} ");

            if (isReleased()) {
                pattern.append("[{2,number}ms");

                if ((durationOfSelf > 0) && (durationOfSelf != duration)) {
                    pattern.append(" ({3,number}ms)");
                }

                if (percent > 0) {
                    pattern.append(", {4,number,##%}");
                }

                if (percentOfAll > 0) {
                    pattern.append(", {5,number,##%}");
                }

                pattern.append("]");
            } else {
                pattern.append("[UNRELEASED]");
            }

            if (localMessage != null) {
                pattern.append(" - {0}");
            }

            buffer.append(MessageFormat.format(pattern.toString(), params));

            for (int i = 0; i < subEntries.size(); i++) {
                TimeEntry subTimeEntry = (TimeEntry) subEntries.get(i);

                buffer.append('\n');

                if (i == (subEntries.size() - 1)) {
                    subTimeEntry.toString(buffer, prefix2 + "*---", prefix2 + "    "); // 最后一项
                } else if (i == 0) {
                    subTimeEntry.toString(buffer, prefix2 + "+---", prefix2 + "|   "); // 第一项
                } else {
                    subTimeEntry.toString(buffer, prefix2 + "----", prefix2 + "|   "); // 中间项
                }
            }
        }
    }
}
