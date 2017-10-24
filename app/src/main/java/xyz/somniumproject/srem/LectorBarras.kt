package xyz.somniumproject.srem

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.google.zxing.Result

import me.dm7.barcodescanner.zxing.ZXingScannerView

class LectorBarras : AppCompatActivity(), ZXingScannerView.ResultHandler {

    internal lateinit var mScannerView: ZXingScannerView
    internal lateinit var servidor: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)
        servidor = "http://deamon3.ddns.net:8888"
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

    override fun handleResult(rawResult: Result) {
        //TODO: Ejecutar servicio y  hacer finish al intent
    }


}