package ru.netology.mapshomework.ui

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.netology.mapshomework.R
import ru.netology.mapshomework.base.ItemViewHolder
import ru.netology.mapshomework.core.db.marker.MarkerEntity
import ru.netology.mapshomework.databinding.MarkItemBinding
import ru.netology.mapshomework.utils.setDebouncedListener


interface MarksListener {
    fun onRemove(id: Long)

    fun onEdit(id: Long, text: String)

    fun onTap(id: Long?)
}

class MarksAdapter(
    private val listener: MarksListener
) : PagingDataAdapter<MarkerEntity, MarksAdapter.MarkViewHolder>(DiffUtilCallback),
    View.OnClickListener {

    override fun onClick(view: View) {
        val mark = view.tag as MarkerEntity?
        when (view.id) {
            R.id.ivDots -> showPopupMenu(view)
            R.id.markItem -> listener.onTap(mark?.id)
        }
    }

    inner class MarkViewHolder(
        private val binding: MarkItemBinding
    ) : ItemViewHolder<MarkerEntity?>(binding.root) {


        override fun bind(item: MarkerEntity?) = with(binding) {
            bindText(item)
            ivDots.setDebouncedListener(300L, this@MarksAdapter)
            markItem.setDebouncedListener(400L, this@MarksAdapter)
        }

        fun bindText(item: MarkerEntity?) = with(binding) {
            markItem.tag = item
            ivDots.tag = item
            if (item != null) {
                tvDecription.text = item.description
            } else {
                tvDecription.text = "???????????????"
            }
        }

    }

    override fun onBindViewHolder(holder: MarkViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: MarkViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            return super.onBindViewHolder(holder, position, payloads)
        }

        payloads.forEach { payload ->
            if (payload is Set<*>) {
                if (payload.contains(TEXT_PAYLOAD)) {
                    holder.bindText(getItem(position))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkViewHolder {
        return MarkViewHolder(
            MarkItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private fun showPopupMenu(view: View) {
        val context = view.context
        val menu = PopupMenu(context, view)
        val mark = view.tag as MarkerEntity?
        mark?.let { marker ->
            menu.menu.add(0, EDIT_ID, Menu.NONE, context.getString(R.string.edit))
            menu.menu.add(0, REMOVE_ID, Menu.NONE, context.getString(R.string.delete))

            menu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    EDIT_ID -> listener.onEdit(marker.id, marker.description)
                    REMOVE_ID -> listener.onRemove(marker.id)
                }
                return@setOnMenuItemClickListener true
            }

            menu.show()
        }
    }

    companion object {
        private const val EDIT_ID: Int = 1
        private const val REMOVE_ID: Int = 2

        private const val TEXT_PAYLOAD: Int = 10
    }

    private object DiffUtilCallback : DiffUtil.ItemCallback<MarkerEntity>() {

        override fun areItemsTheSame(oldItem: MarkerEntity, newItem: MarkerEntity): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MarkerEntity, newItem: MarkerEntity): Boolean =
            oldItem == newItem

        override fun getChangePayload(oldItem: MarkerEntity, newItem: MarkerEntity): Set<Int> {
            val payloads: MutableSet<Int> = mutableSetOf()

            if (oldItem.description != newItem.description) {
                payloads.add(TEXT_PAYLOAD)
            }
            return payloads
        }
    }
}