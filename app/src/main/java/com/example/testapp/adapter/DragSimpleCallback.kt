package com.example.testapp.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragSimpleCallBack(private val listener: SwipeListener) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)
{

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean
    {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
    {
        val ps = viewHolder.adapterPosition
        listener.onSwipe(ps)
    }
}

interface SwipeListener {
    fun onSwipe(pos: Int)
}