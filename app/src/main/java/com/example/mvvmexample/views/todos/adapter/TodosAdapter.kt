package com.example.mvvmexample.views.todos.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmexample.R
import com.example.mvvmexample.databinding.TodosListBinding
import com.example.mvvmexample.model.todos.TodosData
import com.example.mvvmexample.views.todos.interfaces.TodosClickListener
import kotlinx.android.synthetic.main.todos_list.view.*

class TodosAdapter(
    val context: Context?,
    val clickListener: TodosClickListener
) : RecyclerView.Adapter<TodosAdapter.ViewHolder>() {

    var todosList: List<TodosData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding: TodosListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.todos_list, parent, false
        )
        return ViewHolder(viewBinding)
    }


    override fun getItemCount(): Int {
        return todosList.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
if (todosList.get(position).completed!!.equals(true)){
    holder.itemView.tv_status.text = "Completed"
    holder.itemView.card_todo.setCardBackgroundColor(ColorStateList.valueOf(context!!.getColor(R.color.green)))
}else{
    holder.itemView.tv_status.text="Pending"
    holder.itemView.card_todo.setCardBackgroundColor(ColorStateList.valueOf(context!!.getColor(R.color.mango_tango)))
}

    }

    fun setTodos(todos: List<TodosData>) {
        this.todosList = todos
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val viewBinding: TodosListBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun onBind(position: Int) {

            val row = todosList[position]
            viewBinding.todos = row
            viewBinding.todoClickInterface = clickListener
        }
    }
}