// app/src/main/java/com/wendell/menu/model/Task.kt
package com.wendell.menu.model

data class Task(
    var id: String? = null,
    var title: String = "",
    var description: String = "",
    var status: String = "todo",
    var ownerId: String? = null
)
