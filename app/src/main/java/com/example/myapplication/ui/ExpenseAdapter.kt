package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.Expense
import com.example.myapplication.R

class ExpenseAdapter(
    private val onItemClick: (Expense) -> Unit
) : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = getItem(position)
        holder.bind(expense)
        holder.itemView.setOnClickListener { onItemClick(expense) }
    }

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
        private val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        private val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)

        fun bind(expense: Expense) {
            tvAmount.text = expense.amount.toString()
            tvCategory.text = expense.category
            tvDate.text = expense.date.toString()
            tvDescription.text = expense.description
            // TODO: ivIcon.setImageResource(...) по категории
        }
    }

    class ExpenseDiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean = oldItem == newItem
    }
}
