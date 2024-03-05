package com.example.taskmanager.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.di.Task
import com.example.taskmanager.databinding.ItemTaskBinding


class TaskAdapter(private  val listener: onItemClickListener) : ListAdapter<Task, TaskAdapter.TasksViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class TasksViewHolder(private val binding: ItemTaskBinding) : // without inner is like static, can access taskadapter from outside
        RecyclerView.ViewHolder(binding.root) {

            init { //can avoid calling the onclicklistener multiple times
                binding.apply {
                    root.setOnClickListener{
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION){ //-1 position is no position
                            val task = getItem(position)
                            listener.onItemClick(task)
                        }
                    }

                    checkBoxCompleted.setOnClickListener {
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION){ //-1 position is no position
                            val task = getItem(position)
                            listener.onCheckBoxClick(task, checkBoxCompleted.isChecked)
                        }
                    }
                }
            }
        fun bind(task: Task) {
            binding.apply {
                checkBoxCompleted.isChecked = task.checked
                name.text = task.name
                desc.text = task.desc
            }
        }
    }

    interface onItemClickListener {
        fun onItemClick(task: Task)
        fun onCheckBoxClick(task: Task, isChecked: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem


    }

}