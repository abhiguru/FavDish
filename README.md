# FavDish
# Retrofit logging
object ApiCallService {
    private val BASE_URL = "https://www.gurucold.in:2828/"
    val okHttpClient = OkHttpClient.Builder()
    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        if(BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(logging)
        }
    }
    private val api = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient.build())
                    .build()
                    .create(ApiCall::class.java)
    fun items(id:UUID) = api.getItems(resourceId = id)
    fun itemsList() = api.getItemsList()
}
