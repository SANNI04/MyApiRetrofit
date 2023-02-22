package com.cdp.myapiretrofit

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cdp.myapiretrofit.clases.Usuarios
import com.cdp.myapiretrofit.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Login : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fun validarUsuario(): Boolean {
            val nombre = binding.Nombre.text.toString()
            return if (nombre.isEmpty()) {
                binding.Nombre.error = "El campo es obligatorio"
                false
            } else {
                binding.Nombre.error = null
                true
            }
        }

        fun validarClave(): Boolean {
            val clave = binding.Password.text.toString()
            return if (clave.isEmpty()) {
                binding.Password.error = "El campo es obligatorio"
                false
            } else {
                binding.Password.error = null
                true
            }
        }

        binding.Login.setOnClickListener {

            validarUsuario()
            validarClave()

            consultarUsuario()

        }

            /*binding.btnRe.setOnClickListener {
                val intento1 = Intent(this, Registro::class.java)
                startActivity(intento1)
            }*/

    }



    private fun consultarUsuario() {
        val nombre = binding.Nombre.text.toString()
        val clave = binding.Password.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.login(Usuarios(nombre, clave))

            runOnUiThread {
                if (call.isSuccessful) {
                    val usuariosResponse  = call.body()
                    if (usuariosResponse != null) {
                        Log.d("LOGIN", "Login exitoso")
                        Toast.makeText(this@Login, "Bienvenido", Toast.LENGTH_SHORT).show()
                        ingresar()
                    } else {
                        Log.e("LOGIN", "Respuesta vacía")
                        Toast.makeText(this@Login, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("LOGIN", "Error al iniciar sesión: ${call.code()} - ${call.errorBody()?.string()}")
                    Toast.makeText(this@Login, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun ingresar(){
        val intento = Intent(this, MainActivity::class.java)
        startActivity(intento)

    }
}