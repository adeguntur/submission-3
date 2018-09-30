package com.ade.kotlin.football

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.ade.kotlin.football.Api.API
import com.ade.kotlin.football.DB.database
import com.ade.kotlin.football.Key.KEY
import com.ade.kotlin.football.Model.Favorite
import com.ade.kotlin.football.Model.Match
import com.ade.kotlin.football.Model.Team
import com.ade.kotlin.football.Presenter.Detail
import com.ade.kotlin.football.Presenter.MatchDetail
import com.ade.kotlin.football.R.drawable.ic_add_to_favorites
import com.ade.kotlin.football.R.drawable.ic_added_to_favorites
import com.ade.kotlin.football.R.id.add_to_favorite
import com.ade.kotlin.football.R.id.home
import com.ade.kotlin.football.R.menu.detail_menu
import com.ade.kotlin.football.R.string.*
import com.ade.kotlin.football.View.DetailInterface
import com.ade.kotlin.football.View.MainInterface
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.jetbrains.anko.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

class DetailActivity : AppCompatActivity(), DetailInterface, MainInterface {

    private lateinit var homeId: String
    private lateinit var awayId: String
    private lateinit var eventId: String

    private lateinit var txtHomeName: TextView
    private lateinit var txtHomeScore: TextView
    private lateinit var txtHomeFormation: TextView
    private lateinit var txtHomeGoals: TextView
    private lateinit var txtHomeShots: TextView
    private lateinit var txtHomeGoalkeeper: TextView
    private lateinit var txtHomeDefense: TextView
    private lateinit var txtHomeForward: TextView
    private lateinit var txtHomeSubtitutes: TextView
    private lateinit var txtHomeMidfield: TextView
    private lateinit var imgHome: ImageView

    private lateinit var txtAwayName: TextView
    private lateinit var txtAwayScore: TextView
    private lateinit var txtAwayFormation: TextView
    private lateinit var txtAwayGoals: TextView
    private lateinit var txtAwayShots: TextView
    private lateinit var txtAwayGoalkeeper: TextView
    private lateinit var txtAwayDefense: TextView
    private lateinit var txtAwayForward: TextView
    private lateinit var txtAwaySubtitutes: TextView
    private lateinit var txtAwayMidfield: TextView
    private lateinit var imgAway: ImageView

    private lateinit var txtMatchDate: TextView

    private lateinit var progressBar: ProgressBar

    private lateinit var detailPresenter: Detail
    private lateinit var detailMatchPresenter: MatchDetail

    private lateinit var team: Team
    private lateinit var team2: Team
    private lateinit var match: Match

    private var menuItem: Menu? = null
    private var isFavorite: Boolean = false

    private lateinit var homeName: String
    private lateinit var awayName: String
    private lateinit var homeScore: String
    private lateinit var awayScore: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scrollView{
            linearLayout {
                orientation = LinearLayout.VERTICAL
                padding = dip(16)

                progressBar = progressBar {

                }.lparams{
                    gravity = Gravity.CENTER
                }

                linearLayout {
                    orientation = LinearLayout.VERTICAL

                    txtMatchDate = textView {
                        id = R.id.match_date
                    }.lparams {
                        width = wrapContent
                        height = wrapContent
                        gravity = Gravity.CENTER
                    }

                    linearLayout {
                        padding = dip(16)
                        orientation = LinearLayout.HORIZONTAL

                        txtHomeScore = textView {
                            id = R.id.home_score_match
                            textSize = 22f
                            gravity = Gravity.CENTER
                        }.lparams {
                            weight = 1f
                            rightMargin = 16
                        }

                        textView {
                            text = getString(vs)
                            gravity = Gravity.CENTER
                        }.lparams {
                            weight = 1f
                        }

                        txtAwayScore = textView {
                            id = R.id.away_score_match
                            textSize = 22f
                            gravity = Gravity.CENTER
                        }.lparams {
                            weight = 1f
                            leftMargin = 16
                        }
                    }.lparams {
                        gravity = Gravity.CENTER
                    }

                    // TOP - First
                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL

                        // Left
                        linearLayout {
                            orientation = LinearLayout.VERTICAL

                            imgHome = imageView {
                                id = R.id.img_home
                            }.lparams {
                                width = dip(150)
                                height = dip(150)
                            }

                            txtHomeName = textView {
                                id = R.id.home_name
                                textSize = 18f
                            }.lparams {
                                gravity = Gravity.CENTER
                            }

                            txtHomeFormation = textView {
                                id = R.id.home_formation
                            }.lparams {
                                gravity = Gravity.CENTER
                            }
                        }.lparams {
                            weight = 1f
                        }

                        // Right
                        linearLayout {
                            orientation = LinearLayout.VERTICAL

                            imgAway = imageView {
                                id = R.id.img_away
                            }.lparams {
                                width = dip(150)
                                height = dip(150)
                            }

                            txtAwayName = textView {
                                id = R.id.away_name
                                textSize = 18f
                            }.lparams {
                                gravity = Gravity.CENTER
                            }

                            txtAwayFormation = textView {
                                id = R.id.away_formation
                            }.lparams {
                                gravity = Gravity.CENTER
                            }
                        }.lparams {
                            weight = 1f
                            topMargin = dip(16)
                        }
                    }

                    // TOP - Second
                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.CENTER

                        txtHomeGoals = textView {
                            id = R.id.home_goals
                            textSize = 18f
                        }.lparams {
                            weight = 1f
                            gravity = Gravity.CENTER
                            rightMargin = 16
                        }

                        textView {
                            text = getString(goals)
                        }.lparams {
                            weight = 1f
                            gravity = Gravity.CENTER
                        }

                        txtAwayGoals = textView {
                            id = R.id.away_goals
                            textSize = 18f
                        }.lparams {
                            weight = 1f
                            gravity = Gravity.CENTER
                            leftMargin = 16
                        }

                    }.lparams {
                        topMargin = dip(16)
                        gravity = Gravity.CENTER
                    }

                    // TOP - Third
                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL

                        txtHomeShots = textView {
                            id = R.id.home_shots
                            gravity = Gravity.CENTER
                            textSize = 18f
                        }.lparams {
                            weight = 1f
                            rightMargin = 16
                        }

                        textView {
                            text = getString(shots)
                            gravity = Gravity.CENTER
                        }.lparams {
                            weight = 1f
                        }

                        txtAwayShots = textView {
                            id = R.id.away_shots
                            gravity = Gravity.CENTER
                            textSize = 18f
                        }.lparams {
                            weight = 1f
                            leftMargin = 16
                        }

                    }.lparams {
                        topMargin = dip(16)
                        gravity = Gravity.CENTER
                    }

                    textView {
                        text = getString(lineup)
                    }.lparams {
                        topMargin = dip(16)
                        gravity = Gravity.CENTER
                    }

                    // TOP - Fourth
                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL

                        txtHomeGoalkeeper = textView {
                            id = R.id.home_goalkeeper
                            gravity = Gravity.CENTER
                            textSize = 18f
                        }.lparams {
                            weight = 1f
                            rightMargin = dip(16)
                        }

                        textView {
                            text = getString(goalkeeper)
                            gravity = Gravity.CENTER
                        }.lparams {
                            weight = 1f
                        }

                        txtAwayGoalkeeper = textView {
                            id = R.id.away_goalkeeper
                            gravity = Gravity.CENTER
                            textSize = 18f
                        }.lparams {
                            leftMargin = dip(16)
                            weight = 1f
                        }

                    }.lparams {
                        topMargin = dip(16)
                    }

                    // TOP - Fifth
                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL

                        txtHomeDefense = textView {
                            id = R.id.home_defense
                            gravity = Gravity.CENTER
                            textSize = 18f
                        }.lparams {
                            weight = 1f
                            rightMargin = dip(16)
                        }

                        textView {
                            text = getString(defense)
                            gravity = Gravity.CENTER
                        }.lparams {
                            weight = 1f
                        }

                        txtAwayDefense = textView {
                            id = R.id.away_defense
                            gravity = Gravity.CENTER
                            textSize = 18f
                        }.lparams {
                            weight = 1f
                            leftMargin = dip(16)
                        }

                    }.lparams {
                        topMargin = dip(16)
                    }

                    // TOP - Sixth
                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL

                        txtHomeMidfield = textView {
                            id = R.id.home_midfield
                            gravity = Gravity.CENTER
                            textSize = 18f
                        }.lparams {
                            weight = 1f
                            rightMargin = dip(16)
                        }

                        textView {
                            text = getString(midfield)
                            gravity = Gravity.CENTER
                        }.lparams {
                            weight = 1f
                        }

                        txtAwayMidfield = textView {
                            id = R.id.away_midfield
                            gravity = Gravity.CENTER
                            textSize = 18f
                        }.lparams {
                            weight = 1f
                            leftMargin = dip(16)
                        }

                    }.lparams {
                        topMargin = dip(16)
                    }

                    // TOP - Sixth
                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL

                        txtHomeForward = textView {
                            id = R.id.home_forward
                            gravity = Gravity.CENTER
                            textSize = 18f
                        }.lparams {
                            weight = 1f
                            rightMargin = dip(16)
                        }

                        textView {
                            text = getString(forward)
                            gravity = Gravity.CENTER
                        }.lparams {
                            weight = 1f
                        }

                        txtAwayForward = textView {
                            id = R.id.away_forward
                            gravity = Gravity.CENTER
                            textSize = 18f
                        }.lparams {
                            weight = 1f
                            leftMargin = dip(16)
                        }

                    }.lparams {
                        topMargin = dip(16)
                    }

                    // TOP - Sixth
                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL

                        txtHomeSubtitutes = textView {
                            id = R.id.home_substitutes
                            gravity = Gravity.CENTER
                            textSize = 18f
                        }.lparams {
                            weight = 1f
                            rightMargin = dip(16)
                        }

                        textView {
                            text = getString(subtitute)
                            gravity = Gravity.CENTER
                        }.lparams {
                            weight = 1f
                        }

                        txtAwaySubtitutes = textView {
                            id = R.id.away_substitutes
                            gravity = Gravity.CENTER
                            textSize = 18f
                        }.lparams {
                            weight = 1f
                            leftMargin = dip(16)
                        }

                    }.lparams {
                        topMargin = dip(16)
                        bottomMargin = dip(16)
                    }
                }
            }
        }

        val i = intent

        homeId = i.getStringExtra(KEY.HOME_ID_KEY)
        awayId = i.getStringExtra(KEY.AWAY_ID_KEY)
        eventId = i.getStringExtra(KEY.EVENT_ID_KEY)

        favoriteState()
        val request = API()
        val gson = Gson()

        detailPresenter = Detail(this, request, gson)
        detailMatchPresenter = MatchDetail(this, request, gson)

        detailPresenter.getBadgeList(homeId, awayId)
        detailMatchPresenter.getDetailMatch(eventId)
    }

    private fun View.visible(){
        visibility = View.VISIBLE
    }

    private fun View.invisible(){
        visibility = View.GONE
    }

    override fun showLoading() {
        progressBar.visible()
    }

    override fun hideLoading() {
        progressBar.invisible()
    }

    private fun favoriteState(){
        database.use {
            val result = select(Favorite.TABLE_FAVORITE)
                    .whereArgs("(TEAM_HOME_ID = {id}) and (TEAM_AWAY_ID = {id2}) and (EVENT_DATE = {id3})",
                            "id" to homeId,
                            "id2" to awayId,
                            "id3" to eventId)
            val favorite = result.parseList(classParser<Favorite>())
            if (!favorite.isEmpty()) isFavorite = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(detail_menu, menu)
        menuItem = menu
        setFavorite()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            home -> {
                finish()
                true
            }
            add_to_favorite -> {
                if (isFavorite) removeFromFavorite()
                else addToFavorite()
                isFavorite = !isFavorite
                setFavorite()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addToFavorite(){
        try {
            database.use {
                insert(Favorite.TABLE_FAVORITE,
                        Favorite.TEAM_HOME_ID to homeId,
                        Favorite.TEAM_AWAY_ID to awayId,
                        Favorite.EVENT_DATE to eventId,
                        Favorite.TEAM_HOME_NAME to homeName,
                        Favorite.TEAM_AWAY_NAME to awayName,
                        Favorite.TEAM_HOME_SCORE to homeScore,
                        Favorite.TEAM_AWAY_SCORE to awayScore)
            }

            toast("Added to favorite")
        } catch (e: SQLiteConstraintException){
            toast(e.localizedMessage)
        }
    }

    private fun removeFromFavorite(){
        try {
            database.use {
                delete(Favorite.TABLE_FAVORITE,
                        "(TEAM_HOME_ID = {id}) and (TEAM_AWAY_ID = {id2}) and (EVENT_DATE = {id3})",
                        "id" to homeId,
                        "id2" to awayId,
                        "id3" to eventId)
            }
            toast("Removed to favorite")
        } catch (e: SQLiteConstraintException){
            toast(e.localizedMessage)
        }
    }

    private fun setFavorite() {
        if (isFavorite)
            menuItem?.getItem(0)?.icon = ContextCompat.getDrawable(this, ic_added_to_favorites)
        else
            menuItem?.getItem(0)?.icon = ContextCompat.getDrawable(this, ic_add_to_favorites)
    }

    override fun showTeamsList(data: List<Team>?, data2: List<Team>?) {
        team = Team(data?.get(0)?.teamBadge)
        team2 = Team(data2?.get(0)?.teamBadge)
        Picasso.with(this).load(data?.get(0)?.teamBadge).into(imgHome)
        Picasso.with(this).load(data2?.get(0)?.teamBadge).into(imgAway)
    }

    override fun showMatchList(data: List<Match>?) {
        match = Match(data?.get(0)?.eventId,
                data?.get(0)?.homeName,
                data?.get(0)?.awayName,
                data?.get(0)?.homeScore,
                data?.get(0)?.awayScore,
                data?.get(0)?.dateEvent,
                data?.get(0)?.homeGoalKeeper,
                data?.get(0)?.awayGoalKeeper,
                data?.get(0)?.homeGoalDetails,
                data?.get(0)?.awayGoalDetails,
                data?.get(0)?.homeShots,
                data?.get(0)?.awayShots,
                data?.get(0)?.homeDefense,
                data?.get(0)?.awayDefense,
                data?.get(0)?.homeMidfield,
                data?.get(0)?.awayMidfield,
                data?.get(0)?.homeForward,
                data?.get(0)?.awayForward,
                data?.get(0)?.homeSubstitutes,
                data?.get(0)?.awaySubstitutes,
                data?.get(0)?.homeFormation,
                data?.get(0)?.awayFormation,
                data?.get(0)?.teamBadge,
                data?.get(0)?.homeId,
                data?.get(0)?.awayId)

        homeName = data?.get(0)?.homeName.toString()
        awayName = data?.get(0)?.awayName.toString()
        homeScore = data?.get(0)?.homeScore.toString()
        awayScore = data?.get(0)?.awayScore.toString()

        txtHomeName.text = data?.get(0)?.homeName
        txtHomeScore.text = data?.get(0)?.homeScore
        txtHomeFormation.text = data?.get(0)?.homeFormation
        txtHomeGoals.text = data?.get(0)?.homeGoalDetails
        txtHomeGoalkeeper.text = data?.get(0)?.homeGoalKeeper
        txtHomeShots.text = data?.get(0)?.homeShots
        txtHomeDefense.text = data?.get(0)?.homeDefense
        txtHomeForward.text = data?.get(0)?.homeForward
        txtHomeSubtitutes.text = data?.get(0)?.homeSubstitutes
        txtHomeMidfield.text = data?.get(0)?.homeMidfield

        txtAwayName.text = data?.get(0)?.awayName
        txtAwayScore.text = data?.get(0)?.awayScore
        txtAwayFormation.text = data?.get(0)?.awayFormation
        txtAwayGoals.text = data?.get(0)?.awayGoalDetails
        txtAwayGoalkeeper.text = data?.get(0)?.awayGoalKeeper
        txtAwayShots.text = data?.get(0)?.awayShots
        txtAwayDefense.text = data?.get(0)?.awayDefense
        txtAwayForward.text = data?.get(0)?.awayForward
        txtAwaySubtitutes.text = data?.get(0)?.awaySubstitutes
        txtAwayMidfield.text = data?.get(0)?.awayMidfield

        txtMatchDate.text = data?.get(0)?.dateEvent
    }
}