package xyz.somniumproject.srem.encapsulaciones

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Created by frang on 10/23/2017.
 */
internal class RetornoProcesar(
        var error: Boolean, // TRAMA ERROR
        var mensaje: String // TRAMA ERROR
) {
    class Deserializer : ResponseDeserializable<RetornoProcesar> {
        override fun deserialize(content: String) = Gson().fromJson(content, RetornoProcesar::class.java)
    }
}