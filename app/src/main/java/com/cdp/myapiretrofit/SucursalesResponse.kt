package com.cdp.myapiretrofit

import com.cdp.myapiretrofit.clases.Sucursales
import com.google.gson.annotations.SerializedName

data class SucursalesResponse (
    @SerializedName("sucursales") var listaSucursales: ArrayList<Sucursales>
)
