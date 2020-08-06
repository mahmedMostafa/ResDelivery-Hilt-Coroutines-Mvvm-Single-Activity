//package com.example.resdelivery.data.resource
//
//import androidx.annotation.MainThread
//import androidx.annotation.WorkerThread
//import com.example.resdelivery.util.Result
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.*
//import retrofit2.Response
//
///**
// * A repository which provides resource from local database as well as remote end point.
// *
// * [RESULT] represents the type for database.
// * [REQUEST] represents the type for network.
// */
//@ExperimentalCoroutinesApi
//abstract class NetworkBoundResource<RESULT,REQUEST> {
//
//    fun asFlow() = flow<Result<RESULT>> {
//
//        // Emit Loading State
//        emit(Result.Loading)
//
//        try {
//            // Emit Database content first
//            emit(Result.Success(loadFromDb().first()))
//
//            // Fetch latest posts from remote
//            val apiResponse = fetchFromRemote()
//
//            // Parse body
//            val remotePosts = apiResponse.body()
//
//            // Check for response validation
//            if (apiResponse.isSuccessful && remotePosts != null) {
//                // Save posts into the persistence storage
//                saveRemoteData(remotePosts)
//            } else {
//                // Something went wrong! Emit Error state.
//                emit(Result.Error(apiResponse.message()))
//            }
//        } catch (e: Exception) {
//            // Exception occurred! Emit error
//            emit(Result.Error("Network error! Can't get latest Meals."))
//            e.printStackTrace()
//        }
//
//        // Retrieve posts from persistence storage and emit
//        emitAll(loadFromDb().map { Result.Success(it) })
//
//    }
//
//    /**
//     * Saves retrieved from remote into the persistence storage.
//     */
//    @WorkerThread
//    protected abstract suspend fun saveRemoteData(response: REQUEST)
//
//    /**
//     * Retrieves all data from persistence storage.
//     */
//    @MainThread
//    protected abstract fun loadFromDb(): Flow<RESULT>
//
//    /**
//     * Fetches [Response] from the remote end point.
//     */
//    @MainThread
//    protected abstract suspend fun fetchFromRemote(): Response<REQUEST>
//}