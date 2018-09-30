package com.kotlin.anggie.submissions3.view

interface MatchDetailView {
    fun showSnackbar(message: String)

    fun setAsFavorite(favorite: Boolean)
}