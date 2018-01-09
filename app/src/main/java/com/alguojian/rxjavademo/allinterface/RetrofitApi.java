package com.alguojian.rxjavademo.allinterface;

import com.alguojian.rxjavademo.entity.BookCommentRequest;
import com.alguojian.rxjavademo.entity.BookCommentResponse;
import com.alguojian.rxjavademo.entity.BookInfoRequest;
import com.alguojian.rxjavademo.entity.BookInfoResponse;
import com.alguojian.rxjavademo.entity.LoginRequest;
import com.alguojian.rxjavademo.entity.LoginResponse;
import com.alguojian.rxjavademo.entity.RegisterRequest;
import com.alguojian.rxjavademo.entity.RegisterResponse;
import com.alguojian.rxjavademo.entity.Translation;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * RetrofitApi
 *
 * @author ALguojian
 * @date 2018/1/8
 * <p>
 * 请求方式有：
 * @Get
 * @post
 * @PUT
 * @DELETE
 * @HEAD
 * @OPTIONS
 * @HTTP (可用于替换上面所有的请求方式)
 * <p>
 * <p>
 * 标记请求方法有：
 * @FormUrlEncoded 表示请求是一个Form表单
 * 每个键值对需要用@Filed来注解键名，随后的对象需要提供值。
 * <p>
 * <p>
 * @Multipart 表示请求体是一个支持文件上传的Form表单
 * 每个键值对需要用@Part来注解键名，随后的对象需要提供值。
 * <p>
 * <p>
 * @Streaming 表示返回的数据以流的形式返回，适用于返回数据较大的场景
 * （如果没有该注解，默认把数据全部载入内存，之后获取数据也是从内存中读取）
 * <p>
 * <p>
 * 网络请求参数如下：
 * @Headers 添加请求头，固定参数
 * @Body 以 Post方式 传递 自定义数据类型 给服务器，也可以传递Map，相当于@Field，需要做处理
 * FormBody.Builder builder = new FormBody.Builder();
 * builder.add("key","value");
 * <p>
 * <p>
 * <p>
 * @Field以及@FieldMap 传递单个参数或者一个Map集合
 * <p>
 * <p>
 * <p>
 * @Part & @PartMap，提交文件，注意Map添加参数是，值为responseBody
 * <p>
 * <p>
 * <p>
 * @Query以及@QueryMap 用法与@Field相同
 * <p>
 * <p>
 * <p>
 * @Path 访问的API是：https://api.github.com/users/{user}/repos
 * 在发起请求时， {user} 会被替换为方法的第一个参数 user（被@Path注解作用）
 * <p>
 * <p>
 * <p>
 * @Url 当有URL注解时，@GET传入的URL就可以省略,当GET、POST...HTTP等方法中没有设置Url时，则必须使用 {@link Url}提供
 */
public interface RetrofitApi {


    //RX模式的方法定义之一
    @PUT("/task/{Id}")
    Observable<LoginResponse> update(@Path("Id") String Id, @Body HashMap<String, Object> body);

    //RX模式的方法定义之二
    @PUT("/task/{Id}")
    Call<LoginResponse> _update(@Path("Id") String Id, @Body HashMap<String, Object> body);

    //常规异步式的方法定义
    @PUT("/task/{Id}")
    void update_(@Path("Id") String Id, @Body HashMap<String, Object> body, Callback<LoginRequest> callback);

    /**
     * method：网络请求的方法（区分大小写）
     * path：网络请求地址路径
     * hasBody：是否有请求体
     */
    @HTTP(method = "GET", path = "blog/id=8", hasBody = true)
    Observable<LoginResponse> testHttp(@Body LoginRequest request);

    @POST("/post")
    Observable<LoginResponse> login(@Body LoginRequest request);

    /**
     * 添加请求头，不固定形式，参数需要传值
     *
     * @param plat 参数值
     * @return 访问的API是：https://api.github.com/users/{user}/repos
     * 在发起请求时， {user} 会被替换为方法的第一个参数 user（被@Path注解作用）
     */
    @POST("users/{user}/rew")
    @FormUrlEncoded
    Observable<LoginResponse> getUser(@Path("user") String user, @Header("plat") String plat, @Field("userName") String name, @Field("age") int age);


    /**
     * 添加Map集合请求参数
     *
     * @param map 当有URL注解时，@GET传入的URL就可以省略,当GET、POST...HTTP等方法中没有设置Url时，则必须使用 {@link Url}提供
     * @return
     */
    @POST
    @FormUrlEncoded
    Observable<LoginResponse> testFileMap(@Url String url, @FieldMap Map<String, String> map);


    /**
     * @param name {@link RequestBody}、{@link okhttp3.MultipartBody.Part} 、任意类型
     * @param age  除 {@link okhttp3.MultipartBody.Part} 以外，
     *             其它类型都必须带上表单字段({@link okhttp3.MultipartBody.Part} 中已经包含了表单字段的信息)，
     * @param file
     * @return
     */
    @POST("/asd")
    @Multipart
    Observable<LoginResponse> testPart(@Part("name") RequestBody name, @Part("age") RequestBody age, @Part MultipartBody.Part file);


    /**
     * 用于 @GET 方法的查询参数（Query = Url 中 ‘?’ 后面的 key-value）
     * url = http://www.println.net/?cate=android，其中，Query = cate
     *
     * @param cate
     * @return
     */
    @GET
    Observable<LoginResponse> useQuery(@Query("cate") String cate);

    /**
     * 表单形式上传文件，添加参数到RequestBody
     *
     * @param map
     * @param file
     * @return
     */
    @POST("asds")
    @Multipart
    Observable<LoginResponse> testPartMap(@PartMap Map<String, RequestBody> map, @Part MultipartBody.Part file);

    /**
     * 添加固定参数的请求头，多个请求头用花括号包起来，逗号隔开，键值对已分号隔开
     *
     * @param request
     * @param name
     * @return
     */
    @POST("/post")
    @Headers({"plat: 1", "version: 1", "User-Agent: tbl"})
    @FormUrlEncoded
    Observable<LoginResponse> testForm(@Body LoginRequest request, @Field("userName") String name);

    @POST
    Observable<RegisterResponse> register(@Body RegisterRequest request);

    @POST
    @Multipart
    Observable<RegisterResponse> testMuilt(@Body RegisterRequest request, @Part("name") RequestBody name, @Part MultipartBody.Part file);

    @POST
    Observable<BookInfoResponse> getBookInfo(@Body BookInfoRequest request);

    @POST
    Observable<BookCommentResponse> getBookComment(@Body BookCommentRequest request);

    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20world")
    Observable<Translation> getCall();

}
