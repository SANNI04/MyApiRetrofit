package com.cdp.myapiretrofit

import com.cdp.myapiretrofit.clases.Marcas
import com.google.gson.annotations.SerializedName

data class MarcasResponse (
    @SerializedName("marcas") var listaMarcas: ArrayList<Marcas>
        )

