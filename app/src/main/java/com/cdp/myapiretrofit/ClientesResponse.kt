package com.cdp.myapiretrofit

import com.cdp.myapiretrofit.clases.Clientes
import com.cdp.myapiretrofit.clases.Ordenes
import com.google.gson.annotations.SerializedName

data class ClientesResponse (
    @SerializedName("clientes") var listaClientes: ArrayList<Clientes>
)
