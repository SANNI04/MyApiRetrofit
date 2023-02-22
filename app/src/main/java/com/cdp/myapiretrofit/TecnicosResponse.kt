package com.cdp.myapiretrofit

import com.cdp.myapiretrofit.clases.Tecnicos
import com.google.gson.annotations.SerializedName

data class TecnicosResponse (
    @SerializedName("tecnicos") var listaTecnicos: ArrayList<Tecnicos>
)
