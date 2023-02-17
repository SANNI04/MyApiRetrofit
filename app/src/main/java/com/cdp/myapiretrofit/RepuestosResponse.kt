package com.cdp.myapiretrofit

import com.cdp.myapiretrofit.clases.Repuestos
import com.google.gson.annotations.SerializedName

data class RepuestosResponse (
    @SerializedName("repuestos") var listaRepuestos: ArrayList<Repuestos>
)