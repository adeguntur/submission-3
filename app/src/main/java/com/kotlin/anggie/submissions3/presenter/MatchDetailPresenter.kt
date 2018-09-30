package com.kotlin.anggie.submissions3.presenter

import com.kotlin.anggie.submissions3.api.ApiService
import com.kotlin.anggie.submissions3.helper.DBHelper
import com.kotlin.anggie.submissions3.helper.DatabaseListener
import com.kotlin.anggie.submissions3.model.Event
import com.kotlin.anggie.submissions3.model.Team
import com.kotlin.anggie.submissions3.view.MatchDetailView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MatchDetailPresenter(private val databaseHelper: DBHelper,
                           private val matchDetailView: MatchDetailView,
                           private val footballMatchService: ApiService) {

    var favState = false
    private val compositeDisposable = CompositeDisposable()

    fun getTeam(teamId: String,
                listener: GetTeamListener) {
        listener.onLoading()
        compositeDisposable.add(
                footballMatchService.getTeamDetail(teamId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            it.teams?.let { list ->
                                list[0]?.let { team ->
                                    listener.onSuccess(team)
                                }
                            }
                        }, {
                            //                         Log.e("Get Team", "Failed", it)
                            listener.onFailed(it.message)
                        })
        )
    }

    fun composeSubscriber() {
        compositeDisposable.dispose()
    }

    fun getFavorite(eventId: String) {
        val favState = databaseHelper.isMatchFavorite(eventId)
        matchDetailView.setAsFavorite(favState)
    }

    fun setFavorite(event: Event) {
        val favState = databaseHelper.isMatchFavorite(event.idEvent.toString())
        if (!favState) {
            addToFavorite(event)
        } else {
            removeFromFavorite(event.idEvent ?: "")
        }
    }

    private fun removeFromFavorite(eventId: String) {
        databaseHelper.removeFavMatch(eventId, object : DatabaseListener {
            override fun onSuccess() {
                matchDetailView.setAsFavorite(false)
                matchDetailView.showSnackbar("Removed from favorite")
                favState = !favState
            }

            override fun onFailed(message: String) {
                matchDetailView.showSnackbar(message)
            }
        })
    }

    private fun addToFavorite(event: Event) {
        databaseHelper.insertFavMatch(event, object : DatabaseListener {
            override fun onSuccess() {
                matchDetailView.setAsFavorite(true)
                matchDetailView.showSnackbar("Added to favorite")
                favState = !favState
            }

            override fun onFailed(message: String) {
                matchDetailView.showSnackbar(message)
            }
        })
    }
}

interface GetTeamListener {
    fun onLoading()
    fun onSuccess(team: Team)
    fun onFailed(message: String?)
}