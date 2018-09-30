package com.kotlin.anggie.submissions3.presenter

import com.kotlin.anggie.submissions3.api.ApiService
import com.kotlin.anggie.submissions3.helper.HomeScreenState
import com.kotlin.anggie.submissions3.view.PrevMatchView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PrevMatchPresenter(private val prevView: PrevMatchView,
                         private val apiService: ApiService) {

    fun getPrevMatch() {
        prevView.setScreenState(HomeScreenState.Loading)
        apiService.getLastEvent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({eventResponse ->
                    eventResponse.events?.let {
                        prevView.setScreenState(HomeScreenState.Data(it))
                    }
                }, {
                    prevView.setScreenState(HomeScreenState.Error(it.message))
                })
    }
}