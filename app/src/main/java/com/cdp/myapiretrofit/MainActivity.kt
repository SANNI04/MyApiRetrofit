package com.cdp.myapiretrofit

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.cdp.myapiretrofit.adaptadorRecycler.OrdenesAdapter
import com.cdp.myapiretrofit.capturaFirma.CaptureBitmapView
import com.cdp.myapiretrofit.clases.Clientes
import com.cdp.myapiretrofit.clases.Ordenes
import com.cdp.myapiretrofit.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1

    private lateinit var mSig: CaptureBitmapView

    lateinit var binding: ActivityMainBinding

    lateinit var adaptador: OrdenesAdapter

    var listaOrdenes = arrayListOf<Ordenes>()

    var ordenes = Ordenes(-1, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")

    private val TAG = "MainActivity()"


    lateinit var tipos: Spinner


    lateinit var txtBuscar:SearchView

    companion object {
        val instance = MainActivity()
    }


    lateinit var cliente: Spinner
    var listaClientes = arrayListOf<Clientes>()
    //var listaClientes: List<Clientes> = emptyList()


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

        obtenerClientes()

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
            ArrayAdapter.createFromResource(this, R.array.tipos, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.preference_category)
        tipos.adapter = adapter

        var tipoField:String? = null

        tipos.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
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
        var cliField:String? = null

        cliente.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                cliField = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                TODO("Not yet implemented")
            }
        }

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
            this.ordenes.sucursal = sucField.text.toString()
            this.ordenes.persona_encargada = perField.text.toString()
            this.ordenes.tecnico = tecField.text.toString()
            this.ordenes.observaciones = obField.text.toString()
            this.ordenes.fecha_orden_trabajo = feField.text.toString()
            this.ordenes.equipo = equField.text.toString()
            this.ordenes.marca = marField.text.toString()
            this.ordenes.estado_equipo = estField.text.toString()
            this.ordenes.hora_inicio = hriField.text.toString()
            this.ordenes.hora_finalizacion = hrfField.text.toString()
            this.ordenes.voltaje = volField.text.toString()
            this.ordenes.amperaje = ampField.text.toString()
            this.ordenes.clavija = claField.text.toString()
            this.ordenes.modelo = modField.text.toString()
            this.ordenes.serie = serField.text.toString()
            this.ordenes.firma_cliente = mSig.getBytes(firma1).toString()
            //this.ordenes.firma_cliente = mSig.getBytes().toString()

            // valida los campos que no esten vacios:

            if (TextUtils.isEmpty(codigoField.toString())
                && TextUtils.isEmpty(tipoField.toString())
                && TextUtils.isEmpty(cliField.toString())
                && TextUtils.isEmpty(sucField.toString())
                && TextUtils.isEmpty(perField.toString())
                && TextUtils.isEmpty(tecField.toString())
                && TextUtils.isEmpty(obField.toString())
                && TextUtils.isEmpty(feField.toString())
                && TextUtils.isEmpty(equField.toString())
                && TextUtils.isEmpty(marField.toString())
                && TextUtils.isEmpty(estField.toString())
                && TextUtils.isEmpty(hriField.toString())
                && TextUtils.isEmpty(hrfField.toString())
                && TextUtils.isEmpty(volField.toString())
                && TextUtils.isEmpty(ampField.toString())
                && TextUtils.isEmpty(claField.toString())
                && TextUtils.isEmpty(modField.toString())
                && TextUtils.isEmpty(serField.toString()))
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