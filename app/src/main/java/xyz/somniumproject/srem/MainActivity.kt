package xyz.somniumproject.srem

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TabHost
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.getAs
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import xyz.somniumproject.srem.encapsulaciones.ListaParticipante
import xyz.somniumproject.srem.encapsulaciones.Participante
import xyz.somniumproject.srem.encapsulaciones.RetornoProcesar

class MainActivity : AppCompatActivity() {
    var listadoPendiente: List<Participante> = ArrayList()
    var listadoProcesado: List<Participante> = ArrayList()
    val urlActualizar = "http://www.somniumproject.xyz/SRE/actualizar.php"
    val urlListar = "http://www.somniumproject.xyz/SRE/listar.php"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // inicializando TAB
        tab_main.setup()
        var spec: TabHost.TabSpec = tab_main.newTabSpec("tag1")
        spec.setContent(R.id.tab_main1)
        spec.setIndicator("Entradas Pendientes")
        tab_main.addTab(spec)
        spec = tab_main.newTabSpec("tag2")
        spec.setContent(R.id.tab_main2)
        spec.setIndicator("Entradas Procesadas")
        tab_main.addTab(spec)

        //inicializando coroutina
        actualizar()
        txt_filtro_pendientes.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                val valores = ArrayList<String>()
                val adapter: ArrayAdapter<String>
                listadoPendiente.filter {
                    it.nombres.contains(txt_filtro_pendientes.text.toString(), true) ||
                            it.apellidos.contains(txt_filtro_pendientes.text.toString(), true) ||
                            it.codigo.contains(txt_filtro_pendientes.text.toString(), true)
                }.mapTo(valores) { it.codigo + " - " + it.nombres + " " + it.apellidos + " - " + it.categoria }
                adapter = ArrayAdapter(baseContext, android.R.layout.simple_list_item_1, android.R.id.text1, valores)
                lista_pendientes.adapter = adapter

            }


        })

        txt_filtro_procesados.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                val valores = ArrayList<String>()
                val adapter: ArrayAdapter<String>
                listadoProcesado.filter {
                    it.nombres.contains(txt_filtro_procesados.text.toString(), true) ||
                            it.apellidos.contains(txt_filtro_procesados.text.toString(), true) ||
                            it.codigo.contains(txt_filtro_procesados.text.toString(), true)
                }.mapTo(valores) { it.codigo + " - " + it.nombres + " " + it.apellidos + " - " + it.categoria }
                adapter = ArrayAdapter(baseContext, android.R.layout.simple_list_item_1, android.R.id.text1, valores)
                lista_pendientes.adapter = adapter

            }


        })


        btn_escanear.setOnClickListener {

            val myIntent = Intent(baseContext, LectorBarras::class.java)
            myIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            baseContext.startActivity(myIntent)
        }
        lista_pendientes.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, i, _ ->
            val codigo = listadoPendiente[i].codigo
            Fuel.get(urlActualizar, listOf("codigo" to codigo)).responseObject(RetornoProcesar.Deserializer()) { req, res, result ->
                when (result) {
                    is com.github.kittinunf.result.Result.Failure -> {
                        Toast.makeText(baseContext, "Hubo un Error del servidor", Toast.LENGTH_LONG).show()
                    }
                    is com.github.kittinunf.result.Result.Success -> {
                        val retornoProcesar: RetornoProcesar? = result.getAs()
                        if (retornoProcesar != null) {
                            if (!retornoProcesar.error) {
                                Toast.makeText(baseContext, retornoProcesar.mensaje, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
            true
        }


    }


    fun actualizar() {
        launch(CommonPool) {
            while (true) {
                Fuel.get(urlListar, listOf("registrado" to "0")).responseObject(ListaParticipante.Deserializer()) { req, res, result ->
                    when (result) {
                        is Result.Failure -> println(result)
                        is Result.Success -> {
                            val retorno: ListaParticipante? = result.getAs()
                            if (retorno != null) {
                                val valores = ArrayList<String>()
                                val adapter: ArrayAdapter<String>
                                if (!retorno.listado[0].error) {
                                    listadoPendiente = retorno.listado
                                    listadoPendiente.mapTo(valores) { it.codigo + " - " + it.nombres + " " + it.apellidos + " - " + it.categoria }
                                    adapter = ArrayAdapter(baseContext, android.R.layout.simple_list_item_1, android.R.id.text1, valores)
                                    lista_pendientes.adapter = adapter

                                } else {
                                    lista_pendientes.adapter = null
                                }
                            }
                        }
                    }
                }
                Fuel.get(urlListar, listOf("registrado" to "1")).responseObject(ListaParticipante.Deserializer()) { req, res, result ->
                    when (result) {
                        is Result.Failure -> println(result)
                        is Result.Success -> {
                            val retorno: ListaParticipante? = result.getAs()
                            if (retorno != null) {
                                val valores = ArrayList<String>()
                                val adapter: ArrayAdapter<String>
                                if (!retorno.listado[0].error) {
                                    listadoProcesado = retorno.listado
                                    listadoProcesado.mapTo(valores) { it.codigo + " - " + it.nombres + " " + it.apellidos + " - " + it.categoria }
                                    adapter = ArrayAdapter(baseContext, android.R.layout.simple_list_item_1, android.R.id.text1, valores)
                                    lista_procesados.adapter = adapter

                                } else {
                                    lista_procesados.adapter = null
                                }
                            }
                        }
                    }
                }
                delay(1000)
            }
        }
    }

}
