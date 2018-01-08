package com.alguojian.rxjavademo.allinterface;

import com.alguojian.rxjavademo.entity.BookCommentRequest;
import com.alguojian.rxjavademo.entity.BookCommentResponse;
import com.alguojian.rxjavademo.entity.BookInfoRequest;
import com.alguojian.rxjavademo.entity.BookInfoResponse;
import com.alguojian.rxjavademo.entity.LoginRequest;
import com.alguojian.rxjavademo.entity.LoginResponse;
import com.alguojian.rxjavademo.entity.RegisterRequest;
import com.alguojian.rxjavademo.entity.RegisterResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * RetrefitApi
 *
 * @author ALguojian
 * @date 2018/1/8
 */
public interface RetrefitApi {

    @POST
    Observable<LoginResponse> login(@Body LoginRequest request);

    @POST
    Observable<RegisterResponse> register(@Body RegisterRequest request);

    @POST
    Observable<BookInfoResponse> getBookInfo(@Body BookInfoRequest request);

    @POST
    Observable<BookCommentResponse> getBookComment(@Body BookCommentRequest request);

}
