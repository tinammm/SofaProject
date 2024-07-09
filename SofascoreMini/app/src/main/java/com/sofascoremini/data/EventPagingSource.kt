package com.sofascoremini.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sofascoremini.data.models.Span
import com.sofascoremini.data.remote.Result
import com.sofascoremini.ui.main.adapters.MatchEventItem
import javax.inject.Inject
import javax.inject.Singleton

const val ITEMS_PER_PAGE = 10

@Singleton
class EventPagingSource @Inject constructor(
    private val tournamentRepository: TournamentRepository,
    private val tournamentId: Int
) : PagingSource<Pair<Span, Int>,
        MatchEventItem>() {

    override fun getRefreshKey(state: PagingState<Pair<Span, Int>,
            MatchEventItem>): Pair<Span, Int>? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.nextKey
                ?: state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }


    override suspend fun load(params: LoadParams<Pair<Span, Int>>): LoadResult<Pair<Span, Int>,
            MatchEventItem> {
        val (span, page) = params.key ?: Pair(Span.LAST, 1)


        val nextSpan = if (span == Span.LAST && page == 0) Span.NEXT else span
        val prevSpan = if (span == Span.NEXT && page == 0) Span.LAST else span


        val nextPage = if (span == Span.LAST && page == 0) 0
        else if (span == Span.NEXT && page >= 0) page + 1 else page - 1

        val prevPage = if (span == Span.NEXT && page == 0) 0
        else if (span == Span.NEXT && page > 0) page - 1 else page + 1



        return when (val response =
            tournamentRepository.getTournamentMatches(id = tournamentId, span, page)) {
            is Result.Success -> {
                val data = response.data.map { MatchEventItem.EventItem(it) }.toList()
                val nextKey = if (response.data.isNotEmpty()) Pair(nextSpan, nextPage) else null
                val prevKey = if (response.data.isNotEmpty()) Pair(prevSpan, prevPage) else null

                LoadResult.Page(
                    data = data,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }

            is Result.Error -> {
                LoadResult.Error(Exception("Unexpected response type"))
            }
        }
    }
}