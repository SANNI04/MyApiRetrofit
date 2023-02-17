package com.cdp.myapiretrofit

import com.cdp.myapiretrofit.clases.Usuarios
import com.google.gson.annotations.SerializedName

class UsuariosResponse(
    @SerializedName("usuarios") var listaUsuarios: ArrayList<Usuarios>
    )
