package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private val onItemClick: (User) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(DiffCallback) {


    private var selectedUserId: Int? = null

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private val textViewEmail: TextView = itemView.findViewById(R.id.textViewEmail)

        fun bind(user: User, isSelected: Boolean) {
            textViewName.text = user.name
            textViewEmail.text = user.email


            itemView.setBackgroundColor(
                if (isSelected) 0xFFE0E0E0.toInt() else 0xFFFFFFFF.toInt()
            )

            itemView.setOnClickListener {
                // Önce eski seçimi sıfırla
                val oldSelected = selectedUserId
                selectedUserId = user.id

                notifyItemChanged(bindingAdapterPosition)
                oldSelected?.let { oldId ->
                    val oldIndex = currentList.indexOfFirst { it.id == oldId }
                    if (oldIndex != -1) notifyItemChanged(oldIndex)
                }

                onItemClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user, user.id == selectedUserId)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem == newItem
    }
}
