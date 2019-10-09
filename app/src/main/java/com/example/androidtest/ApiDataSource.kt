package com.example.androidtest

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

typealias ResultCallback = ((data: List<Hit>, page: Long, hasNextPage: Boolean, hasPrevPage: Boolean) -> Unit)

class ApiDataSource : PageKeyedDataSource<Long, Hit>() {

    private val compositeDisposable = CompositeDisposable()

    private val api: Api = Retrofit.Builder()
        .baseUrl("https://hn.algolia.com/api/v1/")
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(
            OkHttpClient.Builder().addNetworkInterceptor(
                HttpLoggingInterceptor().setLevel(
                    HttpLoggingInterceptor.Level.BODY
                )
            ).build()
        )
        .build()
        .create(Api::class.java)

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<Long, Hit>
    ) {
        load(1) { data, page, hasNextPage, hasPrevPage ->
            val nextPage: Long? = if (hasNextPage) page.plus(1) else null
            val prevPage: Long? = if (hasPrevPage) page.minus(1) else null
            callback.onResult(data, prevPage, nextPage)
        }
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Hit>) {
        load(params.key) { data, page, hasNextPage, _ ->
            val nextPage: Long? = if (hasNextPage) page.plus(1) else null
            callback.onResult(data, nextPage)
        }
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Hit>) {
        load(params.key) { data, page, _, hasPrevPage ->
            val prevPage: Long? = if (hasPrevPage) page.minus(1) else null
            callback.onResult(data, prevPage)
        }
    }

    fun unsubscribe() {
        compositeDisposable.clear()
    }

    private fun load(loadPage: Long, onResult: ResultCallback) {
        val disposable = api.getHits(page = loadPage)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ resp ->
                val hasPrevPage = resp.page > 1
                val hasNextPage = resp.page < resp.nbPages
                val page = if (hasNextPage) loadPage else null
                onResult(resp.hits, page ?: 1, hasNextPage, hasPrevPage)

            }, { it.printStackTrace() })
        compositeDisposable.add(disposable)
    }

    class Factory : DataSource.Factory<Long, Hit>() {
        var dataSource: ApiDataSource? = null

        override fun create(): DataSource<Long, Hit> {
            dataSource = ApiDataSource()
            return dataSource as ApiDataSource
        }

        fun unsubscribe() {
            dataSource?.unsubscribe()
        }
    }
}