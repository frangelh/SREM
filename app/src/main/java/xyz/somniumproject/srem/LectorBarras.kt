package xyz.somniumproject.srem

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.getAs
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import xyz.somniumproject.srem.encapsulaciones.RetornoParticipante
import xyz.somniumproject.srem.encapsulaciones.RetornoProcesar

class LectorBarras : AppCompatActivity(), ZXingScannerView.ResultHandler {

    internal lateinit var mScannerView: ZXingScannerView
    internal lateinit var urlActualizar: String
    internal lateinit var urlConsultar: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)
        urlActualizar = "http://www.somniumproject.xyz/SRE/actualizar.php"
        urlConsultar = "http://www.somniumproject.xyz/SRE/buscar.php"

    }

    public override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView.startCamera()          // Start camera on resume
    }

    public override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()           // Stop camera on pause
    }

    override fun handleResult(r: Result) {

        //0000000010|Juan|Beato|03400466945|lbeato@pucmm.edu.do|Participante|Depósito o transferencia bancaria
        val codigo: String = r.text//r.text.split("|")[0]
        //Procesar el codigo
        Fuel.get(urlConsultar, listOf("codigo" to codigo)).responseObject(RetornoParticipante.Deserializer()) { req, res, result ->
            when (result) {
                is com.github.kittinunf.result.Result.Failure -> Toast.makeText(baseContext, "Hubo un Error del servidor", Toast.LENGTH_LONG).show()
                is com.github.kittinunf.result.Result.Success -> {
                    val retorno: RetornoParticipante? = result.getAs()
                    if (retorno != null) {
                        if (!retorno.participante.error && retorno.participante.registrado == 0) {
                            val alert = AlertDialog.Builder(this@LectorBarras).create()
                            alert.setTitle("ALERTA...")
                            alert.setMessage("La entrada # $codigo puede ser procesada...")
                            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", { dialogInterface, i ->
                                Fuel.get(urlActualizar, listOf("codigo" to codigo)).responseObject(RetornoProcesar.Deserializer()) { req, res, result ->
                                    when (result) {
                                        is com.github.kittinunf.result.Result.Failure -> {
                                            Toast.makeText(baseContext, "Hubo un Error del servidor", Toast.LENGTH_LONG).show()
                                            finish()
                                        }
                                        is com.github.kittinunf.result.Result.Success -> {
                                            val retornoProcesar: RetornoProcesar? = result.getAs()
                                            if (retornoProcesar != null) {
                                                if (!retornoProcesar.error) {
                                                    Toast.makeText(baseContext, retornoProcesar.mensaje, Toast.LENGTH_LONG).show()
                                                    finish()
                                                }
                                            }
                                        }
                                    }
                                }
                            })
                            alert.show()
                        } else {
                            val alert = AlertDialog.Builder(this@LectorBarras).create()
                            alert.setTitle("ALERTA...")
                            alert.setMessage("Este codigo no puede ser procesado...")
                            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", { dialogInterface, i ->
                                finish()
                            })
                            alert.show()
                        }
                    }
                }
            }
        }
    }


}