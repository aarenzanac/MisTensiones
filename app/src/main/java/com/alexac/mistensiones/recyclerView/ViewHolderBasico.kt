package com.alexac.mistensiones.recyclerView

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolderBasico<T>(itemView: View) : RecyclerView.ViewHolder(itemView){
    abstract fun bind(item: T, position: Int)
}