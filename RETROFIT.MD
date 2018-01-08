## retrofit使用总结

```

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
 *
 *
 */
public interface RetrofitApi {

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
     * 添加固定参数的请求头
     *
     * @param request
     * @param name
     * @return
     */
    @POST("/post")
    @Headers("plat: 1")
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

```

## retrofit源码分析：
```
private final Map<Method, ServiceMethod> serviceMethodCache = new LinkedHashMap<>();
  // 网络请求配置对象（对网络请求接口中方法注解进行解析后得到的对象）
  // 作用：存储网络请求相关的配置，如网络请求的方法、数据转换器、网络请求适配器、网络请求工厂、基地址等

  private final HttpUrl baseUrl;
  // 网络请求的url地址

  private final okhttp3.Call.Factory callFactory;
  // 网络请求器的工厂
  // 作用：生产网络请求器（Call）
  // Retrofit是默认使用okhttp

   private final List<CallAdapter.Factory> adapterFactories;
  // 网络请求适配器工厂的集合
  // 作用：放置网络请求适配器工厂
  // 网络请求适配器工厂作用：生产网络请求适配器（CallAdapter）
  // 下面会详细说明


  private final List<Converter.Factory> converterFactories;
  // 数据转换器工厂的集合
  // 作用：放置数据转换器工厂
  // 数据转换器工厂作用：生产数据转换器（converter）

  private final Executor callbackExecutor;
  // 回调方法执行器

private final boolean validateEagerly;
// 标志位
// 作用：是否提前对业务接口中的注解进行验证转换的标志位


<-- Retrofit类的构造函数 -->
Retrofit(okhttp3.Call.Factory callFactory, HttpUrl baseUrl,
      List<Converter.Factory> converterFactories, List<CallAdapter.Factory> adapterFactories,
      Executor callbackExecutor, boolean validateEagerly) {
    this.callFactory = callFactory;
    this.baseUrl = baseUrl;
    this.converterFactories = unmodifiableList(converterFactories);
    this.adapterFactories = unmodifiableList(adapterFactories);
    // unmodifiableList(list)近似于UnmodifiableList<E>(list)
    // 作用：创建的新对象能够对list数据进行访问，但不可通过该对象对list集合中的元素进行修改
    this.callbackExecutor = callbackExecutor;
    this.validateEagerly = validateEagerly;
  ...
  // 仅贴出关键代码

```