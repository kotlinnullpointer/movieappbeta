package com.nagarro.poptvworkshop.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CarousalMediaItem(
    val title: String,
    val mediaItem: List<CarousalItem>?,
    val showTitle: Boolean
) : Parcelable