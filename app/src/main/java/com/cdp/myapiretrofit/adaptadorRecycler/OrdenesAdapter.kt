package com.cdp.myapiretrofit.adaptadorRecycler

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cdp.myapiretrofit.R
import com.cdp.myapiretrofit.RetrofitClient
import com.cdp.myapiretrofit.capturaFirma.CaptureBitmapView
import com.cdp.myapiretrofit.clases.Detalles
import com.cdp.myapiretrofit.clases.Ordenes
import com.cdp.myapiretrofit.clases.Repuestos
import com.cdp.myapiretrofit.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xdroid.toaster.Toaster
import xdroid.toaster.Toaster.toast
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

//El adaptador es el medio por el que yo le paso toda mi información al recycler view
// Es el que llena las vistas
// cramos nuestra clase y dentro un contructor donde le pasamos la lista de ordenes


class OrdenesAdapter(

    var context: Context,
    var listaOrdenes: ArrayList<Ordenes>

    ): RecyclerView.Adapter<OrdenesAdapter.OrdenesViewHolder>()  {

    //PARA REALIZAR LA FUNCION DE BUSQUEDA
    var listaOriginal: ArrayList<Ordenes>

    init {
        this.listaOrdenes = listaOrdenes
        listaOriginal = ArrayList()
        listaOriginal.addAll(listaOrdenes)
    }

    var ordenes = Ordenes(-1,"","","","","","","","","","","","","","","","","","","")

    lateinit var binding: ActivityMainBinding
    private lateinit var mSig: CaptureBitmapView
    lateinit var adaptador: OrdenesAdapter
    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1

    var repuestos= Repuestos(-1,"","","")

    var detalles = Detalles(-1,"","","","","",
        "","","","","","","",
        "","","","","","",
        "","", "","","","","","","",
        "","","","","","","","","",
        "","","","","","","",
        "","","","","","","","",
        "","","","","","","","","",
        "","","","","","","","","")

    //creamos ahora un viewholder ques una vista determinada para cada orden de la lista(lo que lleva dentro esa vista)
    //La llamamos y la mostramos

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdenesViewHolder {
        val vista =
            LayoutInflater.from(parent.context).inflate(R.layout.item_rv_orden, parent, false)
        return OrdenesViewHolder(vista)

    }

    //Ahora este metodo agarra la vista que esta creada anteriormente en el viewholder y se lo pasa a cada una de las ordenes para que se pinten igual todos
    //le pasamos un entero porque la posicion se maneja por enteros


    override fun onBindViewHolder(holder: OrdenesViewHolder, position: Int) {
        //var mSig = CaptureBitmapView(context, null)

        val ordenes = listaOrdenes[position]

        holder.codigo_orden_trabajo.text = ordenes.codigo_orden_trabajo
        holder.tipo_orden_trabajo.text = ordenes.tipo_orden_trabajo
        holder.cliente.text = ordenes.cliente
        holder.sucursal.text = ordenes.sucursal
        holder.persona_encargada.text = ordenes.persona_encargada
        holder.tecnico.text = ordenes.tecnico
        holder.observaciones.text = ordenes.observaciones
        holder.fecha_orden_trabajo.text = ordenes.fecha_orden_trabajo
        holder.equipo.text = ordenes.equipo
        holder.marca.text = ordenes.marca
        holder.estado_equipo.text = ordenes.estado_equipo
        holder.hora_inicio.text = ordenes.hora_inicio
        holder.hora_finalizacion.text = ordenes.hora_finalizacion
        holder.voltaje.text = ordenes.voltaje
        holder.amperaje.text = ordenes.amperaje
        holder.clavija.text = ordenes.clavija
        holder.modelo.text = ordenes.modelo
        holder.serie.text = ordenes.serie

        val encodedImage = ordenes.firma_cliente

        if (isValidBase64(encodedImage)) {
            val byteArray = Base64.decode(encodedImage, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            holder.firma_cliente.setImageBitmap(bitmap)
         }else{
            Log.e("Error","La cadena no es valida")
        }

        holder.btnEditar.setOnClickListener{
            editarOrden(ordenes)
            editTaskDialog()
        }

       holder.btnAgregarRe.setOnClickListener{
           val codigoOrden = listaOrdenes[holder.bindingAdapterPosition].codigo_orden_trabajo
           addRTaskDialog(codigoOrden)
        }

       holder.btnDetalle.setOnClickListener{
           val codigoOrden = listaOrdenes[holder.bindingAdapterPosition].codigo_orden_trabajo
           addDetalleDialog(codigoOrden)
       }

    }

    private fun isValidBase64(str: String): Boolean {
        return try {
            Base64.decode(str, Base64.DEFAULT)
            true
        } catch (ex: IllegalArgumentException) {
            false
        }
    }

    //retornamos el tamaño de la lista

    override fun getItemCount(): Int {
        return listaOrdenes.size
    }


    inner class OrdenesViewHolder(itemView: View): ViewHolder(itemView){

        val codigo_orden_trabajo = itemView.findViewById(R.id.Codigo) as TextView
        val tipo_orden_trabajo = itemView.findViewById(R.id.Tipo) as TextView
        val cliente = itemView.findViewById(R.id.Cliente) as TextView
        val sucursal = itemView.findViewById(R.id.Sucursal) as TextView
        val persona_encargada = itemView.findViewById(R.id.Persona) as TextView
        val tecnico = itemView.findViewById(R.id.Tecnico) as TextView
        val observaciones = itemView.findViewById(R.id.Observaciones) as TextView
        val fecha_orden_trabajo = itemView.findViewById(R.id.Fecha_ot) as TextView
        val equipo = itemView.findViewById(R.id.Equipo) as TextView
        val marca = itemView.findViewById(R.id.Marca) as TextView
        val estado_equipo = itemView.findViewById(R.id.Estado) as TextView
        val hora_inicio = itemView.findViewById(R.id.HoraI) as TextView
        val hora_finalizacion = itemView.findViewById(R.id.HoraF) as TextView
        val voltaje = itemView.findViewById(R.id.Voltaje) as TextView
        val amperaje = itemView.findViewById(R.id.Amperaje) as TextView
        val clavija = itemView.findViewById(R.id.Clavija) as TextView
        val modelo = itemView.findViewById(R.id.Modelo) as TextView
        val serie = itemView.findViewById(R.id.Serie) as TextView
        val firma_cliente = itemView.findViewById<ImageView>(R.id.Firma)

        val btnEditar = itemView.findViewById(R.id.btnEditar) as Button
        val btnAgregarRe = itemView.findViewById(R.id.btnAgregarRe) as Button
        val btnDetalle = itemView.findViewById(R.id.btnDetalle) as Button

    }


    private fun editTaskDialog() {

        val inflater = LayoutInflater.from(context)
        val subView = inflater.inflate(R.layout.item_formulario_orden, null)

        mSig = CaptureBitmapView(context, null)
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
        val firmacampo: LinearLayout =subView.findViewById(R.id.etFirma)
        val firma1 : ImageView = subView.findViewById(R.id.FirmaC)

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

        val encodedImage = ordenes.firma_cliente

        if (isValidBase64(encodedImage)) {
            val byteArray = Base64.decode(encodedImage, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            firma1.setImageBitmap(bitmap)
        }else{
            Log.e("Error","La cadena no es valida")
        }

        this.ordenes= ordenes

        //Realizamos la misma operacion que al crear una orden
        //con builder estamos seteando un titulo y una vista y un boton que sera ek boton agregar
        val builder = AlertDialog.Builder(context)
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

            if (mSig.touchEventOcurre) {
                this.ordenes.firma_cliente = mSig.getBytes(firmacampo).toString()
                mSig.touchEventOcurre = false
            }else{

            }

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
                    context,
                    "Se deben llenar los campos",
                    Toast.LENGTH_LONG
                ).show()
            }

            //llamamos la funcion updateOrdenes de la clase Sqlite y le pasamos los parametros necesarios

            else {
                CoroutineScope(Dispatchers.IO).launch {
                    val call = RetrofitClient.webService.actualizarOrden(ordenes)

                    if(call.isSuccessful){
                        Thread { toast(R.string.Ordenes) }.start()
                            obtenerOrdenes()
                            limpiarObjeto()

                    }
                    else{
                        Thread { toast(R.string.OrdenesError) }.start()
                        }
                }
           }

            //Guarda la imagen editada en files/ imagenes del telefono: con formato de fecha

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "PNG_" + timeStamp + "_"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        context as Activity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
                } else {
                    // Permission has been granted
                    mSig.storeBitmap(mSig.getBitmap(), "$imageFileName.png")
                }
            } else {
                // Permission has been granted
                mSig.storeBitmap(mSig.getBitmap(), "$imageFileName.png")
            }

        }

        //si se cancela ...

        builder.setNegativeButton(
            "CANCELAR"
        ) { _, _ -> Toast.makeText(context, "Tarea Cancelada", Toast.LENGTH_LONG).show() }


        val alertDialog = builder.show()

        val positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        /*positiveButton.setBackgroundColor(ContextCompat.getColor(this,
            R.color.md_theme_light_primary
        ))*/
        positiveButton.setTextColor(ContextCompat.getColor(context,android.R.color.black))

        val negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        /*negativeButton.setBackgroundColor(ContextCompat.getColor(this,
            R.color.md_theme_light_primary
        ))*/
        negativeButton.setTextColor(ContextCompat.getColor(context,android.R.color.black))


    }

    private fun obtenerOrdenes(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.obtenerOrdenes()

            if(call.isSuccessful){
                    listaOrdenes = call.body()!!.listaOrdenes
                    setupRecyclerView1()
                }else{
                    Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
                }

        }
    }

    private fun setupRecyclerView1() {
        val inflater = LayoutInflater.from(context)
        val subView = inflater.inflate(R.layout.activity_main, null)

        adaptador= OrdenesAdapter(context, listaOrdenes)
        val lista = subView.findViewById<RecyclerView>(R.id.myOrdenesList)
        lista.adapter= adaptador

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

    fun editarOrden(ordenes: Ordenes) {

        val inflater = LayoutInflater.from(context)
        val subView = inflater.inflate(R.layout.item_formulario_orden, null)

        mSig = CaptureBitmapView(context, null)
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

        val codigoField: EditText = subView.findViewById(R.id.etCodigo)
        val tipoField: EditText = subView.findViewById(R.id.etTipo)
        val cliField: EditText = subView.findViewById(R.id.etOrden)
        val sucField: EditText = subView.findViewById(R.id.etSucursal)
        val perField: EditText = subView.findViewById(R.id.etPersona)
        val tecField: EditText = subView.findViewById(R.id.etTecnico)
        val obField: EditText = subView.findViewById(R.id.etObservaciones)
        val feField: EditText = subView.findViewById(R.id.etFecha_ot)
        val equField: EditText = subView.findViewById(R.id.etEquipo)
        val marField: EditText = subView.findViewById(R.id.etMarca)
        val estField: EditText = subView.findViewById(R.id.etEstado)
        val hriField: EditText = subView.findViewById(R.id.etHoraI)
        val hrfField: EditText = subView.findViewById(R.id.etHoraF)
        val volField: EditText = subView.findViewById(R.id.etVoltaje)
        val ampField: EditText = subView.findViewById(R.id.etAmperaje)
        val claField: EditText = subView.findViewById(R.id.etClavija)
        val modField: EditText = subView.findViewById(R.id.etModelo)
        val serField: EditText = subView.findViewById(R.id.etSerie)
        val firma1: ImageView =subView.findViewById(R.id.FirmaC)

        codigoField.setText(ordenes.codigo_orden_trabajo)
        tipoField.setText(ordenes.tipo_orden_trabajo)
        cliField.setText(ordenes.cliente)
        sucField.setText(ordenes.sucursal)
        perField.setText(ordenes.persona_encargada)
        tecField.setText(ordenes.tecnico)
        obField.setText(ordenes.observaciones)
        feField.setText(ordenes.fecha_orden_trabajo)
        equField.setText(ordenes.equipo)
        marField.setText(ordenes.marca)
        estField.setText(ordenes.estado_equipo)
        hriField.setText(ordenes.hora_inicio)
        hrfField.setText(ordenes.hora_finalizacion)
        volField.setText(ordenes.voltaje)
        ampField.setText(ordenes.amperaje)
        claField.setText(ordenes.clavija)
        modField.setText(ordenes.modelo)
        serField.setText(ordenes.serie)

        val encodedImage = ordenes.firma_cliente

        if (isValidBase64(encodedImage)) {
            val byteArray = Base64.decode(encodedImage, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            firma1.setImageBitmap(bitmap)
        }else{
            Log.e("Error","La cadena no es valida")
        }

        this.ordenes= ordenes
    }


    private fun addRTaskDialog(codigoOrden: String){

        val inflater = LayoutInflater.from(context)
        val subView = inflater.inflate(R.layout.item_formulario_repuestos, null)

        //val orden: TextView = subView.findViewById(R.id.etOrden)
        val cantidad: EditText = subView.findViewById(R.id.etCantidad)
        val repuestos_sugeridos_tecnico: EditText = subView.findViewById(R.id.etRepuesto)
        val cantidad1: EditText = subView.findViewById(R.id.etCantidad1)
        val repuestos_sugeridos_tecnico1: EditText = subView.findViewById(R.id.etRepuesto1)
        val cantidad2: EditText = subView.findViewById(R.id.etCantidad2)
        val repuestos_sugeridos_tecnico2: EditText = subView.findViewById(R.id.etRepuesto2)

        //Realizamos la misma operacion que al crear una orden

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Añadir Repuesto")
        builder.setView(subView)
        builder.create()

        builder.setPositiveButton(
            "AGREGAR"
        ) { _, _ ->

            this.repuestos.cantidad = cantidad.text.toString()
            this.repuestos.orden_trabajo = codigoOrden
            this.repuestos.repuestos_sugeridos_tecnico = repuestos_sugeridos_tecnico.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                val call = RetrofitClient.webService.agregarRepuestos(repuestos)

                repuestos.cantidad = cantidad1.text.toString()
                repuestos.orden_trabajo = codigoOrden
                repuestos.repuestos_sugeridos_tecnico = repuestos_sugeridos_tecnico1.text.toString()


                val call1 = RetrofitClient.webService.agregarRepuestos(repuestos)

                repuestos.cantidad = cantidad2.text.toString()
                repuestos.orden_trabajo = codigoOrden
                repuestos.repuestos_sugeridos_tecnico = repuestos_sugeridos_tecnico2.text.toString()


                val call2 = RetrofitClient.webService.agregarRepuestos(repuestos)

                if (call.isSuccessful || call1.isSuccessful || call2.isSuccessful) {

                    Thread { toast(R.string.Repuestos) }.start()
                    obtenerOrdenes()
                    limpiarObjeto()

                    } else {
                    Thread { toast(R.string.RepuestosError) }.start()
                    }
            }

        }

        builder.setNegativeButton("CANCELAR") { _, _ -> Toast.makeText(context, "Tarea Cancelada",
            Toast.LENGTH_LONG).show()}


        val alertDialog = builder.show()

        val positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        /*positiveButton.setBackgroundColor(ContextCompat.getColor(this,
            R.color.md_theme_light_primary
        ))*/
        positiveButton.setTextColor(ContextCompat.getColor(context,android.R.color.black))

        val negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        /*negativeButton.setBackgroundColor(ContextCompat.getColor(this,
            R.color.md_theme_light_primary
        ))*/
        negativeButton.setTextColor(ContextCompat.getColor(context,android.R.color.black))


    }


    private fun addDetalleDialog(codigoOrden: String){

        val inflater = LayoutInflater.from(context)
        val subView = inflater.inflate(R.layout.item_formulario_detalle, null)

        val etEstadoChasis: EditText = subView.findViewById(R.id.etEstadoChasis)
        val etEstadoCubiertas: EditText = subView.findViewById(R.id.etEstadoCubiertas)
        val etPintura: EditText = subView.findViewById(R.id.etPintura)
        val etGolpes: EditText = subView.findViewById(R.id.etGolpes)
        val etTornillos: EditText = subView.findViewById(R.id.etTornillos)
        val etAsientos: EditText = subView.findViewById(R.id.etAsientos)
        val etEstadoMotores: EditText = subView.findViewById(R.id.etEstadoMotores)
        val etEstadoEscobillas: EditText = subView.findViewById(R.id.etEstadoEscobillas)
        val etNivelAceiteTransm: EditText = subView.findViewById(R.id.etNivelAceiteTransm)
        val etEstadoTransmision: EditText = subView.findViewById(R.id.etEstadoTransmision)
        val etFugas: EditText = subView.findViewById(R.id.etFugas)
        val etEstadoDeFrenos: EditText = subView.findViewById(R.id.etEstadoDeFrenos)
        val etEstadoDeBandas: EditText = subView.findViewById(R.id.etEstadoDeBandas)
        val etFuncionamiento: EditText = subView.findViewById(R.id.etFuncionamiento)
        val etNivelDeLiquido: EditText = subView.findViewById(R.id.etNivelDeLiquido)
        val etAjusteFrenos: EditText = subView.findViewById(R.id.etAjusteFrenos)
        val etPruebaFrenado: EditText = subView.findViewById(R.id.etPruebaFrenado)
        val etEstadoDelMotor: EditText = subView.findViewById(R.id.etEstadoDelMotor)
        val etEstadoCadena: EditText = subView.findViewById(R.id.etEstadoCadena)
        val etEstadoPiston: EditText = subView.findViewById(R.id.etEstadoPiston)
        val etNivelDeAceite: EditText = subView.findViewById(R.id.etNivelDeAceite)
        val etLubricacion: EditText = subView.findViewById(R.id.etLubricacion)
        val etEscobillas: EditText = subView.findViewById(R.id.etEscobillas)
        val etEjeDeDireccion: EditText = subView.findViewById(R.id.etEjeDeDireccion)
        val etFuncionamiento1: EditText = subView.findViewById(R.id.etFuncionamiento1)
        val etFusibles: EditText = subView.findViewById(R.id.etFusibles)
        val etControladores: EditText = subView.findViewById(R.id.etControladores)
        val etEstadoDeCables: EditText = subView.findViewById(R.id.etEstadoDeCables)
        val etFuncionamiento2: EditText = subView.findViewById(R.id.etFuncionamiento2)
        val etIndicadorODisplay: EditText = subView.findViewById(R.id.etIndicadorODisplay)
        val etMultipilotoOJoystick: EditText = subView.findViewById(R.id.etMultipilotoOJoystick)
        val etEstadoContactos: EditText = subView.findViewById(R.id.etEstadoContactos)
        val etContactor: EditText = subView.findViewById(R.id.etContactor)
        val etOtros: EditText = subView.findViewById(R.id.etOtros)
        val etFugas1: EditText = subView.findViewById(R.id.etFugas1)
        val etEstadoMangueras: EditText = subView.findViewById(R.id.etEstadoMangueras)
        val etEstadoPoleas: EditText = subView.findViewById(R.id.etEstadoPoleas)
        val etElongacionCadenas: EditText = subView.findViewById(R.id.etElongacionCadenas)
        val etEstadoCilindros: EditText = subView.findViewById(R.id.etEstadoCilindros)
        val etBloqueValvulas: EditText = subView.findViewById(R.id.etBloqueValvulas)
        val etFuncionamiento3: EditText = subView.findViewById(R.id.etFuncionamiento3)
        val etPiezasDeslizantes: EditText = subView.findViewById(R.id.etPiezasDeslizantes)
        val etLubricacionEspejo: EditText = subView.findViewById(R.id.etLubricacionEspejo)
        val etEstadosHorquillas: EditText = subView.findViewById(R.id.etEstadosHorquillas)
        val etEstadoDeFiltros: EditText = subView.findViewById(R.id.etEstadoDeFiltros)
        val etFugas2: EditText = subView.findViewById(R.id.etFugas2)
        val etNivelDeAceite1: EditText = subView.findViewById(R.id.etNivelDeAceite1)
        val etTanqueODeposito: EditText = subView.findViewById(R.id.etTanqueODeposito)
        val etFuncionamiento4: EditText = subView.findViewById(R.id.etFuncionamiento4)
        val etMotorDeElevacion: EditText = subView.findViewById(R.id.etMotorDeElevacion)
        val etEscobillas1: EditText = subView.findViewById(R.id.etEscobillas1)
        val etEstadoInterconectores: EditText = subView.findViewById(R.id.etEstadoInterconectores)
        val etEstadoTornillos: EditText = subView.findViewById(R.id.etEstadoTornillos)
        val etVoltaje: EditText = subView.findViewById(R.id.etVoltaje)
        val etCaidaVoltaje: EditText = subView.findViewById(R.id.etCaidaVoltaje)
        val etCubiertas: EditText = subView.findViewById(R.id.etCubiertas)
        val etPostes: EditText = subView.findViewById(R.id.etPostes)
        val etSulfatacion: EditText = subView.findViewById(R.id.etSulfatacion)
        val etNivelElectrolito: EditText = subView.findViewById(R.id.etNivelElectrolito)
        val etExploradoras: EditText = subView.findViewById(R.id.etExploradoras)
        val etLuzGiratoria: EditText = subView.findViewById(R.id.etLuzGiratoria)
        val etCinturon: EditText = subView.findViewById(R.id.etCinturon)
        val etExtintor: EditText = subView.findViewById(R.id.etExtintor)
        val etCargador: EditText = subView.findViewById(R.id.etCargador)
        val etRuedaMotriz: EditText = subView.findViewById(R.id.etRuedaMotriz)
        val etRuedasDeApoyo: EditText = subView.findViewById(R.id.etRuedasDeApoyo)
        val etRuedasDeDireccion: EditText = subView.findViewById(R.id.etRuedasDeDireccion)
        val etRuedasDeCarga: EditText = subView.findViewById(R.id.etRuedasDeCarga)

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Añadir Nuevo Detalle")
        builder.setView(subView)
        builder.create()

        builder.setPositiveButton("AGREGAR") { _, _ ->

            this.detalles.orden_trabajo = codigoOrden
            this.detalles.estado_chasis = etEstadoChasis.text.toString()
            this.detalles.estado_cubiertas = etEstadoCubiertas.text.toString()
            this.detalles.pintura = etPintura.text.toString()
            this.detalles.golpes = etGolpes.text.toString()
            this.detalles.tornillos = etTornillos.text.toString()
            this.detalles.asientos = etAsientos.text.toString()
            this.detalles.estado_motores = etEstadoMotores.text.toString()
            this.detalles.estado_escobillas = etEstadoEscobillas.text.toString()
            this.detalles.nivel_aceite_transm = etNivelAceiteTransm.text.toString()
            this.detalles.estado_transmision = etEstadoTransmision.text.toString()
            this.detalles.fugas = etFugas.text.toString()
            this.detalles.estado_de_frenos = etEstadoDeFrenos.text.toString()
            this.detalles.estado_de_bandas = etEstadoDeBandas.text.toString()
            this.detalles.funcionamiento = etFuncionamiento.text.toString()
            this.detalles.nivel_de_liquido = etNivelDeLiquido.text.toString()
            this.detalles.ajuste_de_frenos = etAjusteFrenos.text.toString()
            this.detalles.prueba_de_frenado = etPruebaFrenado.text.toString()
            this.detalles.estado_del_motor = etEstadoDelMotor.text.toString()
            this.detalles.estado_cadena = etEstadoCadena.text.toString()
            this.detalles.estado_piston = etEstadoPiston.text.toString()
            this.detalles.nivel_de_aceite = etNivelDeAceite.text.toString()
            this.detalles.lubricacion = etLubricacion.text.toString()
            this.detalles.escobillas = etEscobillas.text.toString()
            this.detalles.eje_de_direccion = etEjeDeDireccion.text.toString()
            this.detalles.funcionamiento1 = etFuncionamiento1.text.toString()
            this.detalles.fusibles = etFusibles.text.toString()
            this.detalles.controladores = etControladores.text.toString()
            this.detalles.estado_de_cables = etEstadoDeCables.text.toString()
            this.detalles.funcionamiento2 = etFuncionamiento2.text.toString()
            this.detalles.indicador_o_display = etIndicadorODisplay.text.toString()
            this.detalles.multipiloto_o_joystick = etMultipilotoOJoystick.text.toString()
            this.detalles.estado_contactos = etEstadoContactos.text.toString()
            this.detalles.contactor = etContactor.text.toString()
            this.detalles.otros = etOtros.text.toString()
            this.detalles.fugas1 = etFugas1.text.toString()
            this.detalles.estado_mangueras = etEstadoMangueras.text.toString()
            this.detalles.estado_poleas = etEstadoPoleas.text.toString()
            this.detalles.enlogacion_cadenas = etElongacionCadenas.text.toString()
            this.detalles.estado_cilindros = etEstadoCilindros.text.toString()
            this.detalles.bloque_valvulas = etBloqueValvulas.text.toString()
            this.detalles.funcionamiento3 = etFuncionamiento3.text.toString()
            this.detalles.piezas_deslizantes = etPiezasDeslizantes.text.toString()
            this.detalles.lubricacion_espejo = etLubricacionEspejo.text.toString()
            this.detalles.estados_horquillas = etEstadosHorquillas.text.toString()
            this.detalles.estado_de_filtros = etEstadoDeFiltros.text.toString()
            this.detalles.fugas2 = etFugas2.text.toString()
            this.detalles.nivel_de_aceite1 = etNivelDeAceite1.text.toString()
            this.detalles.tanque_o_deposito = etTanqueODeposito.text.toString()
            this.detalles.funcionamiento4 = etFuncionamiento4.text.toString()
            this.detalles.motor_de_elevacion = etMotorDeElevacion.text.toString()
            this.detalles.escobillas1 = etEscobillas1.text.toString()
            this.detalles.estado_interconectores = etEstadoInterconectores.text.toString()
            this.detalles.estado_tornillos = etEstadoTornillos.text.toString()
            this.detalles.voltaje = etVoltaje.text.toString()
            this.detalles.caida_de_voltaje = etCaidaVoltaje.text.toString()
            this.detalles.cubiertas = etCubiertas.text.toString()
            this.detalles.postes = etPostes.text.toString()
            this.detalles.sulfatacion = etSulfatacion.text.toString()
            this.detalles.nivel_electrolito = etNivelElectrolito.text.toString()
            this.detalles.exploradoras = etExploradoras.text.toString()
            this.detalles.luz_giratoria = etLuzGiratoria.text.toString()
            this.detalles.cinturon = etCinturon.text.toString()
            this.detalles.extintor = etExtintor.text.toString()
            this.detalles.cargador = etCargador.text.toString()
            this.detalles.rueda_motriz = etRuedaMotriz.text.toString()
            this.detalles.ruedas_de_apoyo = etRuedasDeApoyo.text.toString()
            this.detalles.ruedas_de_direccion = etRuedasDeDireccion.text.toString()
            this.detalles.ruedas_de_carga = etRuedasDeCarga.text.toString()


            CoroutineScope(Dispatchers.IO).launch {
                val call = RetrofitClient.webService.agregarDetalle(detalles)

                if(call.isSuccessful){
                    Thread { toast(R.string.detalle) }.start()
                    //obtenerOrdenes()
                    //limpiarObjeto()

                }
                else{
                    Thread { toast(R.string.detalleError) }.start()
                }
            }

        }

        builder.setNegativeButton("CANCELAR") { _, _ ->
            Toast.makeText(context, "Detalle Cancelado", Toast.LENGTH_LONG).show()
        }

        val alertDialog = builder.show()

        val positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.setTextColor(ContextCompat.getColor(context,android.R.color.black))

        val negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        negativeButton.setTextColor(ContextCompat.getColor(context,android.R.color.black))

        }

    fun filtrado(txtBuscar: String) {
        val longitud = txtBuscar.length
        if (longitud == 0) {
            listaOrdenes.clear()
            listaOrdenes.addAll(listaOriginal)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val collecion: List<Ordenes> = listaOrdenes.stream()
                    .filter { i ->
                        i.codigo_orden_trabajo.lowercase()
                            .contains(txtBuscar.lowercase(Locale.getDefault()))
                    }
                    .collect(Collectors.toList())
                listaOrdenes.clear()
                listaOrdenes.addAll(collecion)
            } else {
                for (c in listaOriginal) {
                    if (c.codigo_orden_trabajo.lowercase()
                            .contains(txtBuscar.lowercase(Locale.getDefault()))
                    ) {
                        listaOrdenes.add(c)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

}