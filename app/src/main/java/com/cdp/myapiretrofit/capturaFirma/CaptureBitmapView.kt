package com.cdp.myapiretrofit.capturaFirma

import android.content.Context
import android.graphics.*
import android.os.Environment
import android.util.AttributeSet
import android.util.Base64
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs

    //La clase Canvas define métodos para diseñar texto, líneas, mapas de bits y muchas otras primitivas gráficas
    //Canvas define formas que puede dibujar en la pantalla.
    //paint  define el color, el estilo, la fuente, etc., de cada forma que dibuje.
    //clase que hereda de view y detecta las pulsaciones y movimientos del dedo sobre la pantalla para dibujar las lineas


class CaptureBitmapView(context: Context, attr: AttributeSet?) :
    View(context, attr) {

    var touchEventOcurre = false
    //var isDrawing = false
    private lateinit var _Bitmap: Bitmap
    private lateinit var canvas: Canvas  //¿que diseñar? manejado por canvas
    private var path: Path = Path()
    private val bitmapPaint: Paint = Paint(Paint.DITHER_FLAG)  //¿como diseñar? manejado por paint
    private val paint: Paint = Paint()
    private var mX = 0f
    private var mY = 0f
    private val touchTolerance = 4f
    private val lineThickness = 4f

    //estilo color y grsor de linea

    init {
        paint.isAntiAlias = true
        paint.isDither = true
        paint.color = Color.argb(255, 0, 0, 0)
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = lineThickness
    }


    //Este metodo es llamado por el sistema Android cada vez que una vista cambia de tamaño
    //Este método es el lugar ideal para crear y configurar el lienzo de la vista

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        _Bitmap = Bitmap.createBitmap(
            w,
            if (h > 0) h else (this.parent as View).height, Bitmap.Config.ARGB_8888
        )
        canvas = Canvas(_Bitmap)
    }


    //Todo el trabajo de dibujo ocurre en el metodo ondraw()
    //para ello tengo que proporcionar el mapa de bits, las coordenadas en pixeles

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(_Bitmap, 0f, 0f, bitmapPaint)
        canvas.drawPath(path, paint)

        //SE llena a partir del metodo onTouch
        //captura la ruta del dedo con el metodo drawpath (dibuja la ruta del dedo) y le pasamos el paint y el path  que trae las coordenadas
        //La ruta es la ruta de acceso de lo que el usuario está dibujando.
    }

    //Esta función se llama cuando el usuario toca la pantalla por primera vez

    fun TouchStart(x: Float, y: Float) {
        path.reset()
        path.moveTo(x, y)
        mX = x
        mY = y
    }

    //Esta función se llama cuando el usuario dibuja

    private fun TouchMove(x: Float, y: Float) {
        val dx = abs(x - mX)
        val dy = abs(y - mY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    //Esta función se llama cuando el usuario levanta su mano
    private fun TouchUp() {
        if (!path.isEmpty) {
            path.lineTo(mX, mY)
            canvas.drawPath(path, paint)
        } else {
            canvas.drawPoint(mX, mY, paint)
        }
        path.reset()
    }


    // con la funcion onTOuchEvent se responde al movimiento en la pantalla(cada vez que el usuario toca la pantalla)
    //controla los eventos de movimiento

    override fun onTouchEvent(e: MotionEvent): Boolean {
        touchEventOcurre = true
        super.onTouchEvent(e)
        val x = e.x
        val y = e.y
        when (e.action) { // se detecta si el usuario esta arrastrando o alza la mano y dibuja de nuevo o esta presionando
            MotionEvent.ACTION_DOWN -> {
                //isDrawing = true
                TouchStart(x, y) //llamamos a cada una de las funciones dependiendo de la accion
                invalidate()

            }
            MotionEvent.ACTION_MOVE -> { //se esta moviendo
                    TouchMove(x, y)
                    invalidate()  // con el invalidate refrescamos y dibujar

            }
            MotionEvent.ACTION_UP -> {
                //isDrawing = false
                TouchUp()
                invalidate()
            }
        }
        return true // regresa un booleano que quiere decir que se ha realizado el dibujo
    }


    //Con esta funcio se limpia el lienzo en blanco nuevamente

    fun ClearCanvas() {
        canvas.drawColor(Color.WHITE)
        invalidate()
    }


    //Guarda el Bitmap en un mapa te bits

    fun getBytes(firma1: LinearLayout): String? {
        val b = getBitmap()
        val baos = ByteArrayOutputStream()
        b.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val bm = baos.toByteArray()
        return Base64.encodeToString(bm, Base64.DEFAULT)
    }

    //almacena el Bitmap (mapa de bits) del dibujo generado
    //Consiste en el conjunto de puntos, identificados como píxeles, que forman una imagen. Un píxel contiene información sobre el color que debe representar.

    fun getBitmap(): Bitmap {
        val v = this.parent as View
        val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return b
    }

    fun storeBitmap(bitmap: Bitmap, filename: String) {
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/imagenes")
        myDir.mkdirs()

        val file = File(myDir, filename)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun isEmpty(): Boolean {
        val emptyBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val emptyCanvas = Canvas(emptyBitmap)
        return _Bitmap.sameAs(emptyBitmap)
    }


}