package com.cdp.myapiretrofit

import com.cdp.myapiretrofit.clases.Usuarios
import com.google.gson.annotations.SerializedName

data class UsuariosResponse(
    val nombre: String,
    val clave: String
)