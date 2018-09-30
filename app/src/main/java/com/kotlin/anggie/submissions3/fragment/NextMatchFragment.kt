package com.kotlin.anggie.submissions3.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TabHost
import android.widget.Toast
import com.kotlin.anggie.submissions3.R
import com.kotlin.anggie.submissions3.activity.MatchDetailActivity
import com.kotlin.anggie.submissions3.adapter.MatchAdapter
import com.kotlin.anggie.submissions3.api.ApiService
import com.kotlin.anggie.submissions3.helper.Constant
import com.kotlin.anggie.submissions3.helper.HomeScreenState
import com.kotlin.anggie.submissions3.model.Event
import com.kotlin.anggie.submissions3.presenter.NextMatchPresenter
import com.kotlin.anggie.submissions3.view.NextMatchView
import kotlinx.android.synthetic.main.frgment_next_match.*

class NextMatchFragment: Fragment(), NextMatchView {

    lateinit var nextPresenter: NextMatchPresenter
    private var matches = mutableListOf<Event?>()
    private lateinit var adapter: MatchAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frgment_next_match, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nextPresenter = NextMatchPresenter(this, ApiService.instance)

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
        rv_next_match.layoutManager = layoutManager
        rv_next_match.adapter = adapter
        swipe_next_layout.setOnRefreshListener {
            nextPresenter.getNextMatch()
        }
    }

    override fun onResume() {
        super.onResume()
        nextPresenter.getNextMatch()
    }


    override fun setScreenState(homeScreenState: HomeScreenState) {
        when (homeScreenState) {
            is HomeScreenState.Error -> {
                swipe_next_layout.isRefreshing = false
                Toast.makeText(context, homeScreenState.message, Toast.LENGTH_SHORT).show()
            }
            is HomeScreenState.Loading -> {
                swipe_next_layout.isRefreshing = true
            }
            is HomeScreenState.Data -> {
                matches.clear()
                matches.addAll(homeScreenState.eventResponse)
                adapter.notifyDataSetChanged()
                swipe_next_layout.isRefreshing = false
            }

        }
    }

}