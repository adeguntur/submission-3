package com.kotlin.anggie.submissions3.presenter

import com.kotlin.anggie.submissions3.helper.DBHelper
import com.kotlin.anggie.submissions3.helper.HomeScreenState
import com.kotlin.anggie.submissions3.view.FavoriteMatchView


class FavoriteMatchPresenter(private val favMatchView: FavoriteMatchView,
                             private val dbHelper: DBHelper) {
    fun getFavMatch() {
        favMatchView.setScreenState(HomeScreenState.Loading)
        val listEvent = dbHelper.getAllFavMatches()
        favMatchView.setScreenState(HomeScreenState.Data(listEvent))
    }
}

