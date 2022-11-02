package com.dicoding.storyappsub1.model

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyappsub1.api.ApiService
import java.lang.Exception

class StoriesPagingSource(
    private val token: String,
    private val apiService: ApiService
) : PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories("Bearer $token", position, params.loadSize, 0)

            val listStoryData = responseData.listStory
            LoadResult.Page(
                data = listStoryData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (listStoryData.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}