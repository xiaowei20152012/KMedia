package com.k.todo.base


import android.view.View

interface OnItemClickListener {

    fun onItemClick(itemView: View, position: Int, `object`: Any)
}
