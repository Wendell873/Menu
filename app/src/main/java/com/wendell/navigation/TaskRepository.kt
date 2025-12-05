// app/src/main/java/com/wendell/menu/repo/TaskRepository.kt
package com.wendell.menu.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.wendell.menu.model.Task

class TaskRepository {
    private val db = FirebaseDatabase.getInstance().getReference("tasks")
    private val auth = FirebaseAuth.getInstance()

    fun createTask(task: Task, onComplete: (Boolean, String?) -> Unit) {
        val key = db.push().key ?: run {
            onComplete(false, "Erro ao gerar chave")
            return
        }
        task.id = key
        task.ownerId = auth.currentUser?.uid
        db.child(key).setValue(task)
            .addOnCompleteListener { t ->
                onComplete(t.isSuccessful, t.exception?.message)
            }
    }

    fun updateTask(task: Task, onComplete: (Boolean, String?) -> Unit) {
        val id = task.id ?: run {
            onComplete(false, "Task sem id")
            return
        }
        db.child(id).setValue(task).addOnCompleteListener { t ->
            onComplete(t.isSuccessful, t.exception?.message)
        }
    }

    fun deleteTask(taskId: String, onComplete: (Boolean, String?) -> Unit) {
        db.child(taskId).removeValue().addOnCompleteListener { t ->
            onComplete(t.isSuccessful, t.exception?.message)
        }
    }

    fun getTasksRef() = db
}
