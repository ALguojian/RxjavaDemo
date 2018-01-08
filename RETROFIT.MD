## retrofit分析

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