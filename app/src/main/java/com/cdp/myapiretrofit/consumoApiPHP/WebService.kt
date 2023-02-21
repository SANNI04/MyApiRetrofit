package com.cdp.myapiretrofit

import com.cdp.myapiretrofit.clases.Detalles
import com.cdp.myapiretrofit.clases.Ordenes
import com.cdp.myapiretrofit.clases.Repuestos
import com.cdp.myapiretrofit.clases.Usuarios
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

object AppConstantes{
    const val BASE_URL = "https://www.isa-america.com/"
}

interface WebService {

    @GET("ApiRestImaq/ordenes/read")
    suspend fun  obtenerOrdenes(): Response<OrdenesResponse>

    @GET("ApiRestImaq/ordenes/read/{index_id}")
    suspend fun getOrden(
        @Path("index_id") index_id: Int
    ): Response <Ordenes>

    @POST("ApiRestImaq/ordenes/create")
    suspend fun agregarOrden(
        @Body ordenes: Ordenes
    ): Response<JsonObject>

    @PUT ("ApiRestImaq/ordenes/update")
    suspend fun actualizarOrden(
        @Body ordenes: Ordenes
    ): Response<JsonObject>

    @DELETE("ApiRestImaq/ordenes/delete")
    suspend fun borrarOrden(
        @Path("index_id") index_id: Int,
    ): Response<JsonObject>

    //REPUESTOS SUGERIDOS

    @GET("ApiRestImaq/repuestos/read")
    suspend fun  obtenerRepuestos(): Response<RepuestosResponse>

    @GET("ApiRestImaq/repuestos/read/{index_id}")
    suspend fun getRepuesto(
        @Path("index_id") index_id: Int
    ): Response <Repuestos>

    @POST("ApiRestImaq/repuestos/create")
    suspend fun agregarRepuestos(
        @Body repuestos: Repuestos
    ): Response<JsonObject>

    @PUT ("ApiRestImaq/repuestos/update")
    suspend fun actualizarOrden(
        @Body repuestos: Repuestos
    ): Response<JsonObject>

    @GET("ApiRestImaq/usuarios/login")
    suspend fun  obtenerUsuario(): Response<UsuariosResponse>

    @POST("ApiRestImaq/detalles/create")
    suspend fun agregarDetalle(
        @Body detalles: Detalles
    ): Response<JsonObject>

    @GET("ApiRestImaq/clientes/read")
    suspend fun  obtenerClientes(): Response<ClientesResponse>

    @GET("ApiRestImaq/clientes/read")
    suspend fun  obtenerSucursales(): Response<SucursalesResponse>

    @GET("ApiRestImaq/clientes/read")
    suspend fun  obtenerTecnicos(): Response<TecnicosResponse>

}

object RetrofitClient{
    val webService: WebService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstantes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(WebService::class.java)
    }
}

