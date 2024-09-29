package ru.shum.data

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.shum.data.api.SuggestsApiService
import ru.shum.data.api.YandexApiService
import ru.shum.data.repository.ScheduleRepositoryImpl
import ru.shum.data.repository.SuggestsRepositoryImpl
import ru.shum.domain.repository.ScheduleRepository
import ru.shum.domain.repository.SuggestsRepository
import java.io.File
import java.util.concurrent.TimeUnit

private const val BASE_YANDEX_URL = "https://api.rasp.yandex.net/v3.0/"
private const val BASE_SUGGESTS_URL = "https://suggests.rasp.yandex.net/"

val dataModule = module {

    single<ScheduleRepository> {
        ScheduleRepositoryImpl(api = get())
    }
    single<SuggestsRepository> {
        SuggestsRepositoryImpl(service = get())
    }

    single(named("suggests")) {
        Retrofit.Builder()
            .baseUrl(BASE_SUGGESTS_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>(named("suggests")).create(SuggestsApiService::class.java) }

    single(named("yandex")) {
        Retrofit.Builder()
            .baseUrl(BASE_YANDEX_URL)
            .client(provideOkHttpClient(get()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>(named("yandex")).create(YandexApiService::class.java) }
}

private fun provideOkHttpClient(context: Context): OkHttpClient {
    val cacheSize = 10 * 1024 * 1024L

    val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize)

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }

    val cacheInterceptor = Interceptor { chain ->
        var request = chain.request()
        request = if (isNetworkAvailable(context)) {
            request.newBuilder()
                .header("Cache-Control", "public, max-age=60")
                .build()
        } else {
            request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=604800")
                .build()
        }
        chain.proceed(request)
    }

    return OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(cacheInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
}

@SuppressLint("MissingPermission")
fun isNetworkAvailable(context: Context) =
    (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
        getNetworkCapabilities(activeNetwork)?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    }