package com.cdp.myapiretrofit

import com.cdp.myapiretrofit.clases.Modelos
import com.google.gson.annotations.SerializedName

data class ModelosResponse (
    @SerializedName("modelos") var listaModelos: ArrayList<Modelos>
        )
