package com.example.androidtest

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import java.util.concurrent.Executors

class ViewModel : androidx.lifecycle.ViewModel() {
    lateinit var hits: LiveData<PagedList<Hit>>
    private val dataSourceFactory = ApiDataSource.Factory()

    init {

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(20)
            .build()
        hits = LivePagedListBuilder(dataSourceFactory, config)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()

    }

    fun refresh() {
       dataSourceFactory.dataSource?.invalidate()
    }

    fun unsubscribe() {
        dataSourceFactory.unsubscribe()
    }
}