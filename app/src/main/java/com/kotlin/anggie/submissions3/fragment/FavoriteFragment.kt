package com.kotlin.anggie.submissions3.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kotlin.anggie.submissions3.R
import com.kotlin.anggie.submissions3.activity.MatchDetailActivity
import com.kotlin.anggie.submissions3.adapter.MatchAdapter
import com.kotlin.anggie.submissions3.helper.Constant
import com.kotlin.anggie.submissions3.helper.DBHelper
import com.kotlin.anggie.submissions3.helper.HomeScreenState
import com.kotlin.anggie.submissions3.model.Event
import com.kotlin.anggie.submissions3.presenter.FavoriteMatchPresenter
import com.kotlin.anggie.submissions3.view.FavoriteMatchView
import kotlinx.android.synthetic.main.fragment_favorite.*

class FavoriteFragment: Fragment(), FavoriteMatchView {

    lateinit var favMatchPresenter: FavoriteMatchPresenter
    private var matches = mutableListOf<Event?>()
    private lateinit var adapter: MatchAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dbHelper = DBHelper.getInstance(view.context)
        favMatchPresenter = FavoriteMatchPresenter(this, dbHelper)

        adapter = MatchAdapter(matches) {pos ->
            val event = matches[pos]
            event?.let {
                val intent = Intent(context, MatchDetailActivity::class.java)
                intent.putExtra(Constant.EVENT, it)
                intent.putExtra(Constant.DONE_MATCH, false)
                startActivity(intent)

            }
        }

        val layoutManager = LinearLayoutManager(context)
        rv_fav_match.layoutManager = layoutManager
        rv_fav_match.adapter = adapter
        swipe_fav_layout.setOnRefreshListener {
            favMatchPresenter.getFavMatch()
        }
    }

    override fun onResume() {
        super.onResume()
        favMatchPresenter.getFavMatch()
    }

    override fun setScreenState(homeScreenState: HomeScreenState) {
        when (homeScreenState) {
            is HomeScreenState.Error -> {
                swipe_fav_layout.isRefreshing = false
                Toast.makeText(context, homeScreenState.message, Toast.LENGTH_SHORT).show()
            }
            is HomeScreenState.Loading -> {
                swipe_fav_layout.isRefreshing = true
            }
            is HomeScreenState.Data -> {
                matches.clear()
                matches.addAll(homeScreenState.eventResponse)
                adapter.notifyDataSetChanged()
                swipe_fav_layout.isRefreshing = false
            }

        }
    }

}