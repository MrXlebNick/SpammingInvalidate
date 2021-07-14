package com.xlebnick.spammingpaging.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.InvalidatingPagingSourceFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.xlebnick.spammingpaging.model.Kitty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class MainViewModel : ViewModel() {

    private val kittiesPagingFactory = InvalidatingPagingSourceFactory {
        KittiesPagingSource()
    }
    private val kittiesPager =
        Pager(PagingConfig(pageSize = 6), pagingSourceFactory = kittiesPagingFactory)
    val kitties: Flow<PagingData<Kitty>> = kittiesPager
        .flow
        .cachedIn(viewModelScope)

    fun invalidate() {
        kittiesPagingFactory.invalidate()
    }
}


class KittiesPagingSource() : PagingSource<Int, Kitty>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Kitty> {
        return try {
            // Start refresh at page 1 if undefined.
            val nextPage = params.key ?: 1
            val newKitties = createKitties()

            LoadResult.Page(
                data = newKitties,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (newKitties.isEmpty()) null else nextPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun createKitties(): List<Kitty> {

        return withContext(Dispatchers.IO) {
            Log.d("***", "loading")
            return@withContext (0 until 10).map { Kitty(it.toString()) }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Kitty>): Int? {
        return state.anchorPosition?.let {
            if (it < state.config.initialLoadSize) {
                // if anchor position is less than initial loading count then download from the beginning
                0
            } else {
                // otherwise load a page around anchorPosition using initialLoadSize
                (it - state.config.initialLoadSize / 2).coerceAtLeast(0)
            }
        }
    }
}