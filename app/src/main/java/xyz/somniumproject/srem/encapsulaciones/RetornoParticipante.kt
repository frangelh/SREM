package xyz.somniumproject.srem.encapsulaciones

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Created by frang on 10/23/2017.
 */
internal class RetornoParticipante(
        var participante: Participante
) {
    class Deserializer : ResponseDeserializable<RetornoParticipante> {
        override fun deserialize(content: String) = Gson().fromJson(content, RetornoParticipante::class.java)
    }
}