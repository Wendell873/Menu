// app/src/main/java/com/wendell/menu/RegisterActivity.kt
package com.wendell.menu

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtName: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        edtName = findViewById(R.id.edtName)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = edtName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val pass = edtPassword.text.toString().trim()

            if (name.isEmpty()) {
                edtName.error = "Nome necessário"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.error = "E-mail inválido"
                return@setOnClickListener
            }
            if (pass.length < 6) {
                edtPassword.error = "Senha deve ter ao menos 6 caracteres"
                return@setOnClickListener
            }
            registerUser(name, email, pass)
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
                    val userMap = mapOf(
                        "uid" to uid,
                        "name" to name,
                        "email" to email
                    )
                    dbRef.setValue(userMap).addOnCompleteListener { writeTask ->
                        if (writeTask.isSuccessful) {
                            Toast.makeText(this, "Usuário cadastrado", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Erro ao salvar usuário: ${writeTask.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Falha no cadastro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
