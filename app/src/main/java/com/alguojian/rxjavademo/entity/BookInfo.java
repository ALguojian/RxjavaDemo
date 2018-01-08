package com.alguojian.rxjavademo.entity;

/**
 * ${DESCRIPTION}
 *
 * @author ALguojian
 * @date 2018/1/8
 */


public class BookInfo {
    BookInfoResponse bookInfoResponse;
    BookCommentResponse bookCommentResponse;

    public BookInfo(BookInfoResponse bookInfoResponse, BookCommentResponse bookCommentResponse) {
        this.bookInfoResponse = bookInfoResponse;
        this.bookCommentResponse = bookCommentResponse;
    }
}
