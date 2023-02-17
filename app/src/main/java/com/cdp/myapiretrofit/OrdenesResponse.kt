package com.cdp.myapiretrofit

import com.cdp.myapiretrofit.clases.Ordenes
import com.google.gson.annotations.SerializedName

data class OrdenesResponse (
    @SerializedName("orden_trabajo") var listaOrdenes: ArrayList<Ordenes>
)