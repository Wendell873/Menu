// app/src/main/java/com/wendell/menu/TaskAdapter.kt
package com.wendell.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wendell.menu.model.Task

class TaskAdapter(
    private val items: List<Task>,
    private val onEdit: (Task) -> Unit,
    private val onDelete: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTaskTitle)
        val tvDesc: TextView = view.findViewById(R.id.tvTaskDesc)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(v)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val t = items[position]
        holder.tvTitle.text = t.title
        holder.tvDesc.text = t.description
        holder.btnEdit.setOnClickListener { onEdit(t) }
        holder.btnDelete.setOnClickListener { onDelete(t) }
    }

    override fun getItemCount(): Int = items.size
}
