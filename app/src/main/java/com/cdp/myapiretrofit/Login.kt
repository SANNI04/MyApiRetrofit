package com.cdp.myapiretrofit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cdp.myapiretrofit.clases.Usuarios
import com.cdp.myapiretrofit.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    var listaUsuarios = arrayListOf<Usuarios>()
    var usuarios=Usuarios(-1,"","")

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
            val clave = binding.Clave.text.toString()
            return if (clave.isEmpty()) {
                binding.Clave.error = "El campo es obligatorio"
                false
            } else {
                binding.Clave.error = null
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
        val nombre = binding.Nombre
        val clave = binding.Clave

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.obtenerUsuario()
            runOnUiThread{
                if(call.isSuccessful){
                    Toast.makeText(this@Login, call.body().toString(), Toast.LENGTH_SHORT)
                        .show()
                    listaUsuarios = call.body()!!.listaUsuarios
                    ingresar()
                }else{
                    Toast.makeText(this@Login, call.body().toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }

    fun ingresar(){
        val intento = Intent(this, MainActivity::class.java)
        startActivity(intento)
    }
}