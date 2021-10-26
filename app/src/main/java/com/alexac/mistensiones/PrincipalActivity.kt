package com.alexac.mistensiones

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.prinpipal_activity.*

class PrincipalActivity : AppCompatActivity() {

    private val database = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.prinpipal_activity)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        if (email != null) {
            database.collection("usuariosRegistrados").document(email).get().addOnSuccessListener{
                textViewNombreLogueado.setText(it.get("nombre") as String?)
            }
            setup(email)
        }

    }

    private fun setup(email: String){

        imageButtonModificarDatosInicio.setOnClickListener {
            val pantallaModificarDatosIntent = Intent(this, ModificarDatosInicioActivity::class.java).apply {
                putExtra("email", email) }
            startActivity(pantallaModificarDatosIntent)
        }

        imageButtonSalir.setOnClickListener {
            val pantallaPrincipalIntent = Intent(this, LoginActivity::class.java).apply {}
            FirebaseAuth.getInstance().signOut()
            startActivity(pantallaPrincipalIntent)
        }
    }
}
