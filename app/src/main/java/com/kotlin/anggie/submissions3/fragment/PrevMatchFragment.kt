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
import com.kotlin.anggie.submissions3.api.ApiService
import com.kotlin.anggie.submissions3.helper.Constant
import com.kotlin.anggie.submissions3.helper.HomeScreenState
import com.kotlin.anggie.submissions3.model.Event
import com.kotlin.anggie.submissions3.presenter.PrevMatchPresenter
import com.kotlin.anggie.submissions3.view.PrevMatchView
import kotlinx.android.synthetic.main.fragment_prev_match.rv_prev_match
import kotlinx.android.synthetic.main.fragment_prev_match.swipe_prev_layout

class PrevMatchFragment : Fragment(), PrevMatchView {

    lateinit var prevMatchPresenter: PrevMatchPresenter
    private var matches = mutableListOf<Event?>()
    private lateinit var adapter: MatchAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prev_match, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prevMatchPresenter = PrevMatchPresenter(this, ApiService.instance)

        adapter = MatchAdapter(matches) { pos ->
            val event = matches[pos]
            event?.let {
                val intent = Intent(context, MatchDetailActivity::class.java)
                intent.putExtra(Constant.EVENT, it)
                startActivity(intent)
            }
        }
        val layoutManager = LinearLayoutManager(context)
        rv_prev_match.layoutManager = layoutManager
        rv_prev_match.adapter = adapter
        swipe_prev_layout.setOnRefreshListener {
            prevMatchPresenter.getPrevMatch()
        }
    }

    override fun onResume() {
        super.onResume()
        prevMatchPresenter.getPrevMatch()
    }

    override fun setScreenState(homeScreenState: HomeScreenState) {
        when (homeScreenState) {
            is HomeScreenState.Error -> {
                swipe_prev_layout.isRefreshing = false
                Toast.makeText(context, homeScreenState.message, Toast.LENGTH_SHORT)
                        .show()
            }
            is HomeScreenState.Loading -> {
                swipe_prev_layout.isRefreshing = true
            }
            is HomeScreenState.Data -> {
                matches.clear()
                matches.addAll(homeScreenState.eventResponse)
                adapter.notifyDataSetChanged()
                swipe_prev_layout.isRefreshing = false
            }
        }
    }
}
