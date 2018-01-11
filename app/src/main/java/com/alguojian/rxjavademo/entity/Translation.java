package com.alguojian.rxjavademo.entity;

import com.socks.library.KLog;

import static com.alguojian.rxjavademo.base.MyApplication.TTAG;

/**
 * ${DESCRIPTION}
 *
 * @author ALguojian
 * @date 2018/1/9
 */

public class Translation {

    private int status;

    private content content;
    private static class content {
        private String from;
        private String to;
        private String vendor;
        private String out;
        private int errNo;
    }

    public void show() {
        KLog.d(TTAG, content.out);
    }

    @Override
    public String toString() {
        return "Translation{" +
                "status=" + status +
                ", content=" + content +
                '}';
    }
}
