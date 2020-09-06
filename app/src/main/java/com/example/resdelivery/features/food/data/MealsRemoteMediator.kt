package com.example.resdelivery.features.food.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.resdelivery.data.local.MealsDatabase
import com.example.resdelivery.data.network.ApiService
import com.example.resdelivery.models.Meal
import com.example.resdelivery.models.RemoteKeys
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException

private const val API_STARTING_INDEX = 1

@ExperimentalPagingApi
class MealsRemoteMediator(
    private val query: String,
    private val service: ApiService,
    private val database: MealsDatabase
) : RemoteMediator<Int, Meal>() {


    override suspend fun load(loadType: LoadType, state: PagingState<Int, Meal>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                //If RemoteKey is null (because the anchorPosition was null),
                // then the page we need to load is the initial one
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: API_STARTING_INDEX
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                remoteKeys?.let {
                    remoteKeys.nextKey
                } ?: throw InvalidObjectException("Remote key should not be null for $loadType")
            }
        }
        try {
            val response = service.getFood(query, page, state.config.pageSize)
            val meals = response.meals
            val hasReachedMax = meals.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.getMealsDao().clearAllMeals()
                    database.getRemoteKeysDao().clearRemoteKeys()
                }
                val prevKey = null // since we only paginate forward
                val nextKey = if (hasReachedMax) null else page + 1
                val keys = meals.map {
                    RemoteKeys(it.id, prevKey, nextKey)
                }
                database.getMealsDao().insertMeals(meals)
                database.getRemoteKeysDao().insertAll(keys)
            }
            return MediatorResult.Success(endOfPaginationReached = hasReachedMax)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Meal>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                // Get the remote keys of the last item retrieved
                database.getRemoteKeysDao().remoteKeysRepoId(repo.id)
            }
    }

    /*
        LoadType.REFRESH gets called when it's the first time we're loading data,
         or when PagingDataAdapter.refresh() is called; so now the point of reference for loading our data is the state.anchorPosition.
         If this is the first load, then the anchorPosition is null. When PagingDataAdapter.refresh() is called,
          the anchorPosition is the first visible position in the displayed list,
        so we will need to load the page that contains that specific item.
     */
    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Meal>
    ): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                database.getRemoteKeysDao().remoteKeysRepoId(repoId)
            }
        }
    }

}