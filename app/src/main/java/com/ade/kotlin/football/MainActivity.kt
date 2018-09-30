package com.ade.kotlin.football

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.ade.kotlin.football.Adapter.FavoriteTeam
import com.ade.kotlin.football.Adapter.MainAdapter
import com.ade.kotlin.football.Api.API
import com.ade.kotlin.football.DB.database
import com.ade.kotlin.football.Key.KEY
import com.ade.kotlin.football.Model.Favorite
import com.ade.kotlin.football.Model.Match
import com.ade.kotlin.football.Presenter.Main
import com.ade.kotlin.football.Presenter.Next
import com.ade.kotlin.football.R.array.main
import com.ade.kotlin.football.R.color.colorAccent
import com.ade.kotlin.football.R.string.*
import com.ade.kotlin.football.View.MainInterface
import com.google.gson.Gson
import org.jetbrains.anko.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class MainActivity : AppCompatActivity(), MainInterface {
    override fun showLoading() {
        progressBar.visible()
    }

    override fun hideLoading() {
        progressBar.invisible()
    }

    override fun showMatchList(data: List<Match>?) {
        favorites.clear()
        swipeRefresh.isRefreshing = false
        events.clear()
        data?.let {
            events.addAll(data)
            adapter.notifyDataSetChanged()
        } ?: toast(getString(no_data))
    }

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var listMatch: RecyclerView

    private lateinit var spinner: Spinner

    private lateinit var adapter: MainAdapter
    private lateinit var favoriteAdapter : FavoriteTeam

    private lateinit var pastPresenter: Main
    private lateinit var nextPresenter: Next
    private var events: MutableList<Match> = mutableListOf()
    private var favorites: MutableList<Favorite> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        linearLayout{
            lparams(width = matchParent, height = wrapContent)
            orientation = LinearLayout.VERTICAL

            spinner = spinner(){
                id = R.id.spinner
            }

            swipeRefresh = swipeRefreshLayout{
                setColorSchemeResources(colorAccent,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light)

                relativeLayout{
                    lparams(width = matchParent, height = wrapContent)

                    listMatch = recyclerView{
                        id = R.id.list_match
                        lparams(width = matchParent, height = wrapContent)
                        layoutManager = LinearLayoutManager(ctx)
                    }

                    progressBar = progressBar {

                    }.lparams{
                        centerHorizontally()
                    }
                }
            }
        }

        val request = API()
        val gson = Gson()
        pastPresenter = Main(this, request, gson)
        nextPresenter = Next(this, request, gson)

        val spinnerItems = resources.getStringArray(main)
        val spinnerAdapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, spinnerItems)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if(spinner.selectedItem == getString(past_match)){
                    adapter = MainAdapter(events) {
                        startActivity<DetailActivity>(
                                KEY.HOME_ID_KEY to it.homeId,
                                KEY.AWAY_ID_KEY to it.awayId,
                                KEY.EVENT_ID_KEY to it.eventId)
                    }
                    listMatch.adapter = adapter

                    pastPresenter.getMatchList(getString(league_id))

                    swipeRefresh.onRefresh {
                        pastPresenter.getMatchList(getString(league_id))
                    }

                }else if (spinner.selectedItem == getString(favorite_match)){
                    favoriteAdapter = FavoriteTeam(favorites) {
                        startActivity<DetailActivity>(
                                KEY.HOME_ID_KEY to it.teamHomeId,
                                KEY.AWAY_ID_KEY to it.teamAwayId,
                                KEY.EVENT_ID_KEY to it.eventDate)
                    }
                    listMatch.adapter = favoriteAdapter

                    showFavorite()

                    swipeRefresh.onRefresh {
                        favorites.clear()
                        showFavorite()
                    }
                }

                else{
                    adapter = MainAdapter(events) {
                        startActivity<DetailActivity>(
                                KEY.HOME_ID_KEY to it.homeId,
                                KEY.AWAY_ID_KEY to it.awayId,
                                KEY.EVENT_ID_KEY to it.eventId)
                    }
                    listMatch.adapter = adapter

                    nextPresenter.getMatchList(getString(league_id))

                    swipeRefresh.onRefresh {
                        nextPresenter.getMatchList(getString(league_id))
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


    }

    private fun showFavorite(){
        events.clear()
        database.use {
            swipeRefresh.isRefreshing = false
            val result = select(Favorite.TABLE_FAVORITE)
            val favorite = result.parseList(classParser<Favorite>())
            favorites.addAll(favorite)
            adapter.notifyDataSetChanged()
        }
    }

    private fun View.visible(){
        visibility = View.VISIBLE
    }

    private fun View.invisible(){
        visibility = View.INVISIBLE
    }
}
