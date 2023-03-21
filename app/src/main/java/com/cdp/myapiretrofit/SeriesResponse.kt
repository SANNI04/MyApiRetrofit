package com.cdp.myapiretrofit

import com.cdp.myapiretrofit.clases.Series
import com.google.gson.annotations.SerializedName

data class SeriesResponse (
    @SerializedName("series") var listaSeries: ArrayList<Series>
        )


