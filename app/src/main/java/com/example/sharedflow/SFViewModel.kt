package com.example.sharedflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.util.ArrayList

/**
 * @author wanglun
 * @date 2021/07/23
 * @description
 */
class SFViewModel(initPage: Int = 0) : ViewModel() {

    companion object {
        const val pageSize = 20
    }

    val list = Pager(
        config = PagingConfig(pageSize = pageSize, prefetchDistance = 5),
        initialKey = initPage,
        pagingSourceFactory = {
            SFPagingSource(initPage)
        }
    ).flow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PagingData.empty()
    )

    class SFPagingSource(initPage: Int) : PagingSource<Int, String>() {
        override fun getRefreshKey(state: PagingState<Int, String>): Int? {
            return 1
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
            if (params.key == null) {
                return LoadResult.Page(
                    loadData(params.key!!),
                    nextKey = if (params.key != null && params.key!! <= 5) params.key!! + 1 else null,
                    prevKey = if (params.key != null && params.key!! > 0) params.key!! - 1 else null
                )
            } else {
                return LoadResult.Error(throwable = Throwable())
            }
        }

        private fun loadData(index: Int): ArrayList<String> {
            return ArrayList<String>().apply {
                for (i in 1..20) {
                    add("${i + index * pageSize}")
                }
            }
        }

    }


}


