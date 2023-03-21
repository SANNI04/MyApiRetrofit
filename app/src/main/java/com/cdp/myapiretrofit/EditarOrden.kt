package com.cdp.myapiretrofit

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cdp.myapiretrofit.capturaFirma.CaptureBitmapView
import com.cdp.myapiretrofit.clases.Ordenes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EditarOrden  : AppCompatActivity() {

    lateinit var context: Context
    lateinit var listaOrdenes: ArrayList<Ordenes>
    var ordenes = Ordenes(-1,"", "","","","","","","","","","","","","","","","","","","","")
    private lateinit var mSig: CaptureBitmapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_rv_orden)

        val btnEditar = findViewById<Button>(R.id.btnEditar)

        btnEditar.setOnClickListener{
            editTaskDialog()
        }
    }

    private fun editTaskDialog() {

        val inflater = LayoutInflater.from(this)
        val subView = inflater.inflate(R.layout.item_formulario_orden, null)

        mSig = CaptureBitmapView(this, null)
        val firma: LinearLayout = subView.findViewById(R.id.etFirma)
        firma.addView(
            mSig,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        val limpiar: Button = subView.findViewById(R.id.btnLimpiar)
        limpiar.setOnClickListener{
            mSig.ClearCanvas()
        }

        val codigo: EditText = subView.findViewById(R.id.etCodigo)
        val tipo: EditText = subView.findViewById(R.id.etTipo)
        val cliente: EditText = subView.findViewById(R.id.etOrden)
        val sucursal: EditText = subView.findViewById(R.id.etSucursal)
        val persona: EditText = subView.findViewById(R.id.etPersona)
        val tecnico: EditText = subView.findViewById(R.id.etTecnico)
        val observaciones: EditText = subView.findViewById(R.id.etObservaciones)
        val fecha_ot: EditText = subView.findViewById(R.id.etFecha_ot)
        val equipo: EditText = subView.findViewById(R.id.etEquipo)
        val marca: EditText = subView.findViewById(R.id.etMarca)
        val estado: EditText = subView.findViewById(R.id.etEstado)
        val horaI: EditText = subView.findViewById(R.id.etHoraI)
        val horaF: EditText = subView.findViewById(R.id.etHoraF)
        val voltaje: EditText = subView.findViewById(R.id.etVoltaje)
        val amperaje: EditText = subView.findViewById(R.id.etAmperaje)
        val clavija: EditText = subView.findViewById(R.id.etClavija)
        val modelo: EditText = subView.findViewById(R.id.etModelo)
        val serie: EditText = subView.findViewById(R.id.etSerie)
        val firma1: LinearLayout = subView.findViewById(R.id.etFirma)

        //ponemos en cada atributo de ordenes los valores recolectados

        codigo.setText(ordenes.codigo_orden_trabajo)
        tipo.setText(ordenes.tipo_orden_trabajo)
        cliente.setText(ordenes.cliente)
        sucursal.setText(ordenes.sucursal)
        persona.setText(ordenes.persona_encargada)
        tecnico.setText(ordenes.tecnico)
        observaciones.setText(ordenes.observaciones)
        fecha_ot.setText(ordenes.fecha_orden_trabajo)
        equipo.setText(ordenes.equipo)
        marca.setText(ordenes.marca)
        estado.setText(ordenes.estado_equipo)
        horaI.setText(ordenes.hora_inicio)
        horaF.setText(ordenes.hora_finalizacion)
        voltaje.setText(ordenes.voltaje)
        amperaje.setText(ordenes.amperaje)
        clavija.setText(ordenes.clavija)
        modelo.setText(ordenes.modelo)
        serie.setText(ordenes.serie)
        //firma1.addView(ordenes.firma_cliente)
        this.ordenes= ordenes

        //Realizamos la misma operacion que al crear una orden

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar usuario")
        builder.setView(subView)
        builder.create()
        builder.setPositiveButton(
            "EDITAR"
        ) { _, _ ->

            this.ordenes.codigo_orden_trabajo = codigo.text.toString()
            this.ordenes.tipo_orden_trabajo = tipo.text.toString()
            this.ordenes.cliente = cliente.text.toString()
            this.ordenes.sucursal = sucursal.text.toString()
            this.ordenes.persona_encargada = persona.text.toString()
            this.ordenes.tecnico = tecnico.text.toString()
            this.ordenes.observaciones = observaciones.text.toString()
            this.ordenes.fecha_orden_trabajo = fecha_ot.text.toString()
            this.ordenes.equipo = equipo.text.toString()
            this.ordenes.marca = marca.text.toString()
            this.ordenes.estado_equipo = estado.text.toString()
            this.ordenes.hora_inicio = horaI.text.toString()
            this.ordenes.hora_finalizacion = horaF.text.toString()
            this.ordenes.voltaje = voltaje.text.toString()
            this.ordenes.amperaje = amperaje.text.toString()
            this.ordenes.clavija = clavija.text.toString()
            this.ordenes.modelo = modelo.text.toString()
            this.ordenes.serie = serie.text.toString()
            this.ordenes.firma_cliente = mSig.getBitmap().toString()

            if (TextUtils.isEmpty(codigo.toString()) && TextUtils.isEmpty(tipo.toString()) && TextUtils.isEmpty(
                    cliente.toString()
                ) && TextUtils.isEmpty(sucursal.toString()) && TextUtils.isEmpty(persona.toString()) && TextUtils.isEmpty(
                    tecnico.toString()
                ) && TextUtils.isEmpty(
                    observaciones.toString()
                ) &&
                TextUtils.isEmpty(fecha_ot.toString()) && TextUtils.isEmpty(equipo.toString()) && TextUtils.isEmpty(
                    marca.toString()
                ) && TextUtils.isEmpty(estado.toString()) && TextUtils.isEmpty(horaI.toString()) && TextUtils.isEmpty(
                    horaF.toString()
                ) && TextUtils.isEmpty(voltaje.toString()) &&
                TextUtils.isEmpty(amperaje.toString()) && TextUtils.isEmpty(clavija.toString()) && TextUtils.isEmpty(
                    modelo.toString()
                ) && TextUtils.isEmpty(serie.toString())) {
                Toast.makeText(
                    this@EditarOrden,
                    "Se deben llenar los campos",
                    Toast.LENGTH_LONG
                ).show()
            }

            //llamamos la funcion updateOrdenes de la clase Sqlite y le pasamos los parametros necesarios

            else {
                CoroutineScope(Dispatchers.IO).launch {
                    val call = RetrofitClient.webService.agregarOrden(ordenes)
                    runOnUiThread{
                        if(call.isSuccessful){
                            Toast.makeText(this@EditarOrden, call.body().toString(), Toast.LENGTH_SHORT)
                                .show()
                            obtenerOrdenes()
                            limpiarObjeto()
                        }else{
                            Toast.makeText(this@EditarOrden, call.body().toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
           }
        }

        //si se cancela ...

        builder.setNegativeButton(
            "CANCELAR"
        ) { _, _ -> Toast.makeText(this@EditarOrden, "Tarea Cancelada", Toast.LENGTH_LONG).show() }
        builder.show()
    }

    fun obtenerOrdenes(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.obtenerOrdenes()
            runOnUiThread{
                if(call.isSuccessful){
                    listaOrdenes = call.body()!!.listaOrdenes
                }else{
                    Toast.makeText(this@EditarOrden, "Error consultar todos", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun limpiarObjeto(){
        this.ordenes.index_id=-1
        this.ordenes.codigo_orden_trabajo= ""
        this.ordenes.tipo_orden_trabajo= ""
        this.ordenes.cliente= ""
        this.ordenes.sucursal= ""
        this.ordenes.persona_encargada= ""
        this.ordenes.tecnico= ""
        this.ordenes.observaciones= ""
        this.ordenes.fecha_orden_trabajo= ""
        this.ordenes.equipo= ""
        this.ordenes.marca= ""
        this.ordenes.estado_equipo= ""
        this.ordenes.hora_inicio= ""
        this.ordenes.hora_finalizacion= ""
        this.ordenes.voltaje= ""
        this.ordenes.amperaje= ""
        this.ordenes.clavija= ""
        this.ordenes.modelo= ""
        this.ordenes.serie= ""
        this.ordenes.firma_cliente= ""
    }


}