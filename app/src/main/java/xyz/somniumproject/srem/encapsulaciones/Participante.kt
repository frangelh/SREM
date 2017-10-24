package xyz.somniumproject.srem.encapsulaciones

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Created by frang on 10/23/2017.
 */
class Participante(
        var codigo: String,
        var nombres: String,
        var apellidos: String,
        var documento: String,
        var email: String,
        var categoria: String,
        var pago: String,
        var registrado: Int,
        var error: Boolean, // TRAMA ERROR
        var mensaje: String // TRAMA ERROR
) {
    class Deserializer : ResponseDeserializable<Participante> {
        override fun deserialize(content: String) = Gson().fromJson(content, Participante::class.java)
    }
}