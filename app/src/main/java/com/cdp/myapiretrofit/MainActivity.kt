package com.cdp.myapiretrofit

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.cdp.myapiretrofit.adaptadorRecycler.OrdenesAdapter
import com.cdp.myapiretrofit.capturaFirma.CaptureBitmapView
import com.cdp.myapiretrofit.clases.*
import com.cdp.myapiretrofit.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1

    private lateinit var mSig: CaptureBitmapView

    lateinit var binding: ActivityMainBinding

    lateinit var adaptador: OrdenesAdapter

    var listaOrdenes = arrayListOf<Ordenes>()

    var ordenes = Ordenes(-1, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")

    private val TAG = "MainActivity()"

    lateinit var txtBuscar:SearchView

    companion object {
        val instance = MainActivity()
    }

    lateinit var tipos: Spinner

    lateinit var equipos: Spinner

    lateinit var cliente: Spinner
    var listaClientes = arrayListOf<Clientes>()

    lateinit var sucursal : Spinner
    var listaSucursales = arrayListOf<Sucursales>()

    lateinit var  tecnico : Spinner
    var listaTecnicos = arrayListOf<Tecnicos>()

    lateinit var marcas : Spinner
    var listaMarcas = arrayListOf<Marcas>()

    lateinit var  modelos : Spinner
    var listaModelos = arrayListOf<Modelos>()

    lateinit var  series : Spinner
    var listaSeries = arrayListOf<Series>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //funcion para scrollear

        val parentScroll: NestedScrollView = findViewById(R.id.parent_scroll)
        parentScroll.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, _, _ ->
                title = "Scroll ($scrollX, $scrollY)"

                if (!v.canScrollVertically(1))
                    Toast.makeText(this, "Final", Toast.LENGTH_SHORT).show()
            })

        //se posiciona un layout manager para que el recycler view funcione
        //el linear layout nos mostrara las cosas de manera lineal.

        binding.myOrdenesList.layoutManager = LinearLayoutManager(this)
        setupRecyclerView()

        txtBuscar = binding.txtBuscar

        obtenerOrdenes()

        binding.btnAgregar.setOnClickListener {
            addOrdenDialog()
        }

        txtBuscar.setOnQueryTextListener(this)


        binding.btnSalir.setOnClickListener{
            finish()
        }


        obtenerClientes()
        obtenerSucursales()
        obtenerTecnicos()
        obtenerMarcas()
        obtenerModelos()
        obtenerSeries()

    }

    private fun addOrdenDialog() {

        //infla la vista del formulario.

        val inflater = LayoutInflater.from(this)
        val subView = inflater.inflate(R.layout.item_formulario_orden, null)
        // el el campo de firma se añade lo que se valla dibujando que se va guardando el la variable msig

        mSig = CaptureBitmapView(this, null)
        val firma: LinearLayout = subView.findViewById(R.id.etFirma)
        firma.addView(
            mSig,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        // Boton que limpia el lienzo pasandole la funcion ClearCanvas()

        val limpiar: Button = subView.findViewById(R.id.btnLimpiar)
        limpiar.setOnClickListener {
            mSig.ClearCanvas()
        }

        //variables que guardan el valor de cada celda del formulario

        val codigoField: EditText = subView.findViewById(R.id.etCodigo)

        tipos = subView.findViewById(R.id.etTipo) as Spinner

        val adapter =
            ArrayAdapter.createFromResource(
                this,
                R.array.tipos,
                android.R.layout.simple_spinner_item
            )
        adapter.setDropDownViewResource(android.R.layout.preference_category)
        tipos.adapter = adapter

        tipos.setSelection(0)

        var tipoField: String? = null

        tipos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                tipoField = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        cliente = subView.findViewById(R.id.etOrden) as Spinner

        cliente.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaClientes.map { it.nombre_cliente })
        adapter.setDropDownViewResource(android.R.layout.preference_category)

        cliente.setSelection(13)

        var cliField: Int = -1

        cliente.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val clienteSeleccionado = listaClientes[position]
                cliField = clienteSeleccionado.index_id
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                TODO("Not yet implemented")
            }
        }

        sucursal = subView.findViewById(R.id.etSucursal) as Spinner

        sucursal.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaSucursales.map { it.nombre_sucursal })
        adapter.setDropDownViewResource(android.R.layout.preference_category)

        sucursal.setSelection(24)

        var sucField:String? = null

        sucursal.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                sucField = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        val perField: EditText = subView.findViewById(R.id.etPersona)

        tecnico= subView.findViewById(R.id.etTecnico) as Spinner

        val titulo =Tecnicos(0,"Tecnico: ")
        val opciones = listaTecnicos.plus(titulo)

        tecnico.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones.map{ it.primer_nombre })
        adapter.setDropDownViewResource(android.R.layout.preference_category)

        tecnico.setSelection(14)

        var tecField : String? = null

        tecnico.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                tecField = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        val obField: EditText = subView.findViewById(R.id.etObservaciones)

        val feField = subView.findViewById<EditText>(R.id.etFecha_ot)
        val calendar = Calendar.getInstance()

        feField.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this,
                { _, y, m, d ->
                // Obtener la fecha seleccionada y mostrarla en el EditText
                val selectedDate = String.format("%04d-%02d-%02d", y, m+1, d)
                feField.setText(selectedDate)
                },
            year,
            month,
            dayOfMonth
            )

            datePickerDialog.show()
        }

        equipos = subView.findViewById(R.id.etEquipo) as Spinner

        val adapter1 =
            ArrayAdapter.createFromResource(
                this,
                R.array.equipos,
                android.R.layout.simple_spinner_item
            )
        adapter1.setDropDownViewResource(android.R.layout.preference_category)
        equipos.adapter = adapter1

        equipos.setSelection(0)

        var equField: String? = null

        equipos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                equField = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        marcas= subView.findViewById(R.id.etMarca) as Spinner

        marcas.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaMarcas.map{ it.marca })
        adapter.setDropDownViewResource(android.R.layout.preference_category)

        var marField : String? = null

        marcas.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                marField = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        val estField: EditText = subView.findViewById(R.id.etEstado)
        val horoField: EditText = subView.findViewById(R.id.etHorometro)
        val hriField: EditText = subView.findViewById(R.id.etHoraI)

        hriField.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
                    val selectedTime = String.format("%02d:%02d:%02d", hourOfDay, minute, 0)
                    hriField.setText(selectedTime)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }

        val hrfField: EditText = subView.findViewById(R.id.etHoraF)

        hrfField.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
                    val selectedTime = String.format("%02d:%02d:%02d", hourOfDay, minute, 0)
                    hrfField.setText(selectedTime)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }

        val volField: EditText = subView.findViewById(R.id.etVoltaje)
        val ampField: EditText = subView.findViewById(R.id.etAmperaje)
        val claField: EditText = subView.findViewById(R.id.etClavija)

        modelos= subView.findViewById(R.id.etModelo) as Spinner

        modelos.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaModelos.map{ it.modelo })
        adapter.setDropDownViewResource(android.R.layout.preference_category)

        modelos.setSelection(226)

        var modField : String? = null

        modelos.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                modField = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        series= subView.findViewById(R.id.etSerie) as Spinner

        series.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaSeries.map{ it.serie })
        adapter.setDropDownViewResource(android.R.layout.preference_category)

        series.setSelection(226)

        var serField : String? = null

        series.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                serField = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

       // val serField: EditText = subView.findViewById(R.id.etSerie)
        val firma1: LinearLayout = subView.findViewById(R.id.etFirma)
        //mSig.getBitmap()
        //val firField: LinearLayout = subView.findViewById(R.id.Firma)

        //con builder estamos seteando un titulo y una vista y un boton que sera el boton agregar
        //creamos un objeto a traves de la clase Builder
        //configuramos titulo

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Añadir Nueva Orden")
        builder.setView(subView)
        builder.create()

        builder.setPositiveButton("AGREGAR") { _, _ ->    //boton agregar dentro del formulario

            //guarda en cada atributo de la clase ordenes lo que exista en el campo

            this.ordenes.codigo_orden_trabajo = codigoField.text.toString()
            this.ordenes.tipo_orden_trabajo = tipoField.toString()
            this.ordenes.cliente = cliField.toString()
            this.ordenes.sucursal = sucField.toString()
            this.ordenes.persona_encargada = perField.text.toString()
            this.ordenes.tecnico = tecField.toString()
            this.ordenes.observaciones = obField.text.toString()
            this.ordenes.fecha_orden_trabajo = feField.text.toString()
            this.ordenes.equipo = equField.toString()
            this.ordenes.marca = marField.toString()
            this.ordenes.estado_equipo = estField.text.toString()
            this.ordenes.horometro = horoField.text.toString()
            this.ordenes.hora_inicio = hriField.text.toString()
            this.ordenes.hora_finalizacion = hrfField.text.toString()
            this.ordenes.voltaje = volField.text.toString()
            this.ordenes.amperaje = ampField.text.toString()
            this.ordenes.clavija = claField.text.toString()
            this.ordenes.modelo = modField.toString()
            this.ordenes.serie = serField.toString()
            this.ordenes.firma_cliente = mSig.getBytes(firma1).toString()
            //this.ordenes.firma_cliente = mSig.getBytes().toString()

            // valida los campos que no esten vacios:

            if (TextUtils.isEmpty(codigoField.toString()))
            {

                Toast.makeText(this, "Se deben llenar los campos", Toast.LENGTH_LONG).show()

            } else {

                //Una corrutina es un conjunto de sentencias que realizan una tarea específica, con la capacidad suspender o resumir su ejecución sin bloquear un hilo.
                //se inicia la corrutina con launch y un despachador (Input, Output)
                //maneja procesos de entrada y salida en segundo plano
                //runOnUiThread solo ejecuta una acción específica desde un thread(sub procesos o hilos) que estés ejecutando sobre una view (un componente, ya sea TextView u otro) del hilo principal, es decir, un componente de tu app.

                CoroutineScope(Dispatchers.IO).launch {
                    val call = RetrofitClient.webService.agregarOrden(ordenes)
                    runOnUiThread {
                        if (call.isSuccessful) {
                            Toast.makeText(
                                this@MainActivity,  //hace referencia al contexto de clase actual <- calificador
                                call.body().toString(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            obtenerOrdenes()
                            limpiarObjeto()

                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                call.body().toString(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "PNG_" + timeStamp + "_"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
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

        //se crea el boton cancelar

        builder.setNegativeButton("CANCELAR") { _, _ ->
            Toast.makeText(
                this@MainActivity, "Tarea Cancelada",
                Toast.LENGTH_LONG
            ).show()
        }

        val alertDialog = builder.show()

        val positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        /*positiveButton.setBackgroundColor(ContextCompat.getColor(this,
            R.color.md_theme_light_primary
        ))*/
        positiveButton.setTextColor(ContextCompat.getColor(this,android.R.color.black))

        val negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        /*negativeButton.setBackgroundColor(ContextCompat.getColor(this,
            R.color.md_theme_light_primary
        ))*/
        negativeButton.setTextColor(ContextCompat.getColor(this,android.R.color.black))

    }

       // var equField: String? = null

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_WRITE_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                    val imageFileName = "PNG_" + timeStamp + "_"

                    // Permission has been granted
                    mSig.storeBitmap(mSig.getBitmap(), "$imageFileName.png")
                } else {
                    // Permission has been denied
                }
                return
            }
            else -> {
                // Permission request was not for WRITE_EXTERNAL_STORAGE
            }
        }
    }

    //creamos a la función que trae de la clase OrdenesAdapter la lista que se mostrara en pantalla
    //setea la info que trae el adapter

    fun setupRecyclerView(){
        adaptador= OrdenesAdapter(this, listaOrdenes)
        binding.myOrdenesList.adapter= adaptador
    }

    // creamos la funcion que consume de la api para consultar todas las ordenes
    // usamos retrofit q a un servicio web REST traduciendo la API a interfaces
    //retrofit  Permite hacer peticiones al servidor tipo: GET, POST, PUT, PATCH, DELETE y HEAD

    fun obtenerOrdenes(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.obtenerOrdenes()
            runOnUiThread{
                if(call.isSuccessful){
                    listaOrdenes = call.body()!!.listaOrdenes
                    setupRecyclerView()
                }else{
                    Toast.makeText(this@MainActivity, "Error consultar todos", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

  fun obtenerClientes(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.obtenerClientes()
            runOnUiThread{
                if(call.isSuccessful){
                    listaClientes = call.body()!!.listaClientes
                }else{
                    Toast.makeText(this@MainActivity, "Error consultar todos", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun obtenerSucursales(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.obtenerSucursales()
            runOnUiThread{
                if(call.isSuccessful){
                    listaSucursales = call.body()!!.listaSucursales
                }else{
                    Toast.makeText(this@MainActivity, "Error consultar todos", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun obtenerTecnicos(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.obtenerTecnicos()
            runOnUiThread{
                if(call.isSuccessful){
                    listaTecnicos = call.body()!!.listaTecnicos
                }else{
                    Toast.makeText(this@MainActivity, "Error consultar todos", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun obtenerMarcas(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.obtenerMarcas()
            runOnUiThread{
                if(call.isSuccessful){
                    listaMarcas = call.body()!!.listaMarcas
                }else{
                    Toast.makeText(this@MainActivity, "Error consultar todos", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun obtenerModelos(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.obtenerModelos()
            runOnUiThread{
                if(call.isSuccessful){
                    listaModelos = call.body()!!.listaModelos
                }else{
                    Toast.makeText(this@MainActivity, "Error consultar todos", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun obtenerSeries(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.obtenerSeries()
            runOnUiThread{
                if(call.isSuccessful){
                    listaSeries = call.body()!!.listaSeries
                }else{
                    Toast.makeText(this@MainActivity, "Error consultar todos", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

        //var equField: String? = null

    //Limpiamos el objeto

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
        this.ordenes.horometro= ""
        this.ordenes.hora_inicio= ""
        this.ordenes.hora_finalizacion= ""
        this.ordenes.voltaje= ""
        this.ordenes.amperaje= ""
        this.ordenes.clavija= ""
        this.ordenes.modelo= ""
        this.ordenes.serie= ""
        this.ordenes.firma_cliente= ""
    }

    //Netodos que ayudan al momento de escribir en el boton de busqueda estar buscando en tiempo real
    override fun onQueryTextSubmit(s: String): Boolean {
        return false
    }

    override fun onQueryTextChange(s: String): Boolean {
        adaptador.filtrado(s)
        return false
    }


    /*fun crearAdaptador(contexto: Context, datos: List<String>): ArrayAdapter<String> {
        val adaptador = object : ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, datos) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val vista = super.getDropDownView(position, convertView, parent)
                // Personalizar la vista de cada elemento del menú desplegable aquí si es necesario
                return vista
            }
        }
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adaptador
    }*/
}