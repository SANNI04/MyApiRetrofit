package com.cdp.myapiretrofit.clases

import java.util.Date

data class Ordenes(
   var index_id: Int,
   var codigo_orden_trabajo: String,
   var tipo_orden_trabajo: String,
   var cliente: String,
   var sucursal: String,
   var persona_encargada: String,
   var tecnico: String,
   var observaciones: String,
   var fecha_orden_trabajo: String,
   var equipo: String,
   var marca: String,
   var estado_equipo: String,
   var horometro: String,
   var hora_inicio: String,
   var hora_finalizacion: String,
   var voltaje: String,
   var amperaje: String,
   var clavija: String,
   var modelo: String,
   var serie: String,
   var firma_cliente: String,
   var nombre_cliente: String
 )
