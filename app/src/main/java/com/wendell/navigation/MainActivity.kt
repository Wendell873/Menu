// app/src/main/java/com/wendell/menu/MainActivity.kt
package com.wendell.menu

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wendell.menu.model.Task
import com.wendell.menu.repo.TaskRepository

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private val list = mutableListOf<Task>()

    private lateinit var fabAdd: FloatingActionButton

    private val auth = FirebaseAuth.getInstance()
    private val tasksRef = FirebaseDatabase.getInstance().getReference("tasks")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerTasks)
        fabAdd = findViewById(R.id.fabAdd)

        adapter = TaskAdapter(list,
            onEdit = { task -> showEditDialog(task) },
            onDelete = { task -> deleteTask(task) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fabAdd.setOnClickListener {
            showCreateDialog()
        }

        observeTasks()
    }

    private fun observeTasks() {
        val uid = auth.currentUser?.uid ?: return
        val q = tasksRef.orderByChild("ownerId").equalTo(uid)

        q.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (child in snapshot.children) {
                    val t = child.getValue(Task::class.java)
                    t?.let { list.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Erro ao ler tarefas: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showCreateDialog() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_task_form, null)
        val edtTitle = view.findViewById<EditText>(R.id.edtTaskTitle)
        val edtDesc = view.findViewById<EditText>(R.id.edtTaskDesc)

        AlertDialog.Builder(this)
            .setTitle("Nova Tarefa")
            .setView(view)
            .setPositiveButton("Salvar") { _: DialogInterface, _: Int ->
                val title = edtTitle.text.toString().trim()
                val desc = edtDesc.text.toString().trim()
                if (title.isEmpty()) {
                    Toast.makeText(this, "Título vazio", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val repo = TaskRepository()
                val task = Task(title = title, description = desc)
                repo.createTask(task) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Tarefa criada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erro: $error", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditDialog(task: Task) {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_task_form, null)
        val edtTitle = view.findViewById<EditText>(R.id.edtTaskTitle)
        val edtDesc = view.findViewById<EditText>(R.id.edtTaskDesc)
        edtTitle.setText(task.title)
        edtDesc.setText(task.description)

        AlertDialog.Builder(this)
            .setTitle("Editar Tarefa")
            .setView(view)
            .setPositiveButton("Salvar") { _, _ ->
                val title = edtTitle.text.toString().trim()
                val desc = edtDesc.text.toString().trim()
                if (title.isEmpty()) {
                    Toast.makeText(this, "Título vazio", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                task.title = title
                task.description = desc
                TaskRepository().updateTask(task) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Tarefa atualizada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erro: $error", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteTask(task: Task) {
        TaskRepository().deleteTask(task.id ?: return) { success, error ->
            if (success) {
                Toast.makeText(this, "Tarefa removida", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erro ao remover: $error", Toast.LENGTH_LONG).show()
            }
        }
    }
}
