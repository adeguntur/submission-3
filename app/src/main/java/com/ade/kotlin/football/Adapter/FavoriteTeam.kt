package com.ade.kotlin.football.Adapter

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.ade.kotlin.football.R
import com.ade.kotlin.football.Model.Favorite
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.sdk25.listeners.onClick

class FavoriteTeam (private val events: List<Favorite>, private val listener: (Favorite) -> Unit)
    : RecyclerView.Adapter<FavoriteTeam.MatchViewHolder>(){

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bindItems(events[position], listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        return MatchViewHolder(MatchUI().createView(AnkoContext.create(parent.context, parent)))
    }

    override fun getItemCount(): Int = events.size

    class MatchUI : AnkoComponent<ViewGroup> {
        override fun createView(ui: AnkoContext<ViewGroup>): View {
            return with(ui){
                cardView{
                    id = R.id.cv_item
                    lparams(width = matchParent, height = wrapContent){
                        topMargin = dip(16)
                        rightMargin = dip(16)
                        leftMargin = dip(16)
                    }

                    linearLayout {
                        orientation = LinearLayout.VERTICAL
                        padding = dip(16)

                        textView {
                            id = R.id.match_date
                            gravity = Gravity.CENTER
                        }.lparams {
                            width = matchParent
                        }

                        linearLayout {
                            lparams(width = matchParent, height = wrapContent)
                            orientation = LinearLayout.HORIZONTAL

                            textView {
                                id = R.id.home_name
                                gravity = Gravity.CENTER
                            }.lparams {
                                width = matchParent
                                weight = 1f
                            }

                            textView {
                                id = R.id.score_match
                                gravity = Gravity.CENTER
                            }.lparams {
                                width = matchParent
                                weight = 1f
                            }

                            textView {
                                id = R.id.away_name
                                gravity = Gravity.CENTER
                            }.lparams {
                                width = matchParent
                                weight = 1f
                            }
                        }
                    }
                }
            }
        }
    }

    class MatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val homeName: TextView = view.find(R.id.home_name)
        private val awayName: TextView = view.find(R.id.away_name)
        private val score: TextView = view.find(R.id.score_match)
        private val matchDate: TextView = view.find(R.id.match_date)
        val cv: CardView = view.find(R.id.cv_item)

        fun bindItems(events: Favorite, listener: (Favorite) -> Unit){
            homeName.text = events.teamHomeName
            awayName.text = events.teamAwayName
            if(events.teamHomeScore != null){
                score.text = events.teamHomeScore + " VS " + events.teamAwayScore
            }else{
                score.text = "? VS ?"
            }
            matchDate.text = events.eventDate
            cv.onClick{
                listener(events)
            }
        }
    }
}