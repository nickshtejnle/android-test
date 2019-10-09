package com.example.androidtest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.paging.PagedListAdapter
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_list_item.view.*

class Adapter : PagedListAdapter<Hit, Adapter.ViewHolder>(DiffCalback()),
    CompoundButton.OnCheckedChangeListener {
    override fun onCheckedChanged(button: CompoundButton?, checked: Boolean) {
    }

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_list_item, parent, false),
            itemSelectedListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(getItem(position), tracker?.isSelected(getItemId(position)) ?: false)
    }

    override fun getItemId(position: Int): Long = position.toLong()


    class ViewHolder(view: View, private val selectedListener: ItemSelectedListener) :
        RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                selectedListener.onItemSelected(
                    this,
                    itemView.isActivated
                )
            }
            itemView.switch_compat.setOnClickListener {
                selectedListener.onItemSelected(
                    this,
                    itemView.isActivated
                )
            }
        }

        fun setData(hit: Hit?, isSelected: Boolean) {
            itemView.title.text = hit?.title
            itemView.created.text = hit?.created_at
            itemView.isActivated = isSelected
            itemView.switch_compat.isChecked = isSelected
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getSelectionKey(): Long? = itemId
                override fun getPosition(): Int = adapterPosition
            }
    }

    private val itemSelectedListener: ItemSelectedListener = object : ItemSelectedListener {
        override fun onItemSelected(viewHolder: ViewHolder, selected: Boolean) {
            val itemId = getItemId(viewHolder.adapterPosition)
            if (selected) {
                tracker?.deselect(itemId)
            } else {
                tracker?.select(itemId)
            }
        }
    }


    class DiffCalback : DiffUtil.ItemCallback<Hit>() {
        override fun areItemsTheSame(oldItem: Hit, newItem: Hit): Boolean {
            return oldItem.objectID == newItem.objectID
        }

        override fun areContentsTheSame(oldItem: Hit, newItem: Hit): Boolean {
            return oldItem == newItem
        }

    }


    interface ItemSelectedListener {
        fun onItemSelected(viewHolder: ViewHolder, selected: Boolean)
    }

}