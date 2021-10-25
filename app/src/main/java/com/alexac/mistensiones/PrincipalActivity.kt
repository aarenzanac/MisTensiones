package com.alexac.mistensiones

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.datos_inicio_activity.*
import kotlinx.android.synthetic.main.prinpipal_activity.*

class PrincipalActivity : AppCompatActivity() {

    private val database = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.prinpipal_activity)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        if (email != null) {
            setup(email)
        }
    }

    private fun setup(email: String){
        database.collection("usuariosRegistrados").document(email).get().addOnSuccessListener{
            textViewNombreLogueado.setText(it.get("nombre") as String?)
        }
        buttonSalir.setOnClickListener {
            val pantallaPrincipalIntent = Intent(this, LoginActivity::class.java).apply {}
            FirebaseAuth.getInstance().signOut()
            startActivity(pantallaPrincipalIntent)
        }
    }
}
