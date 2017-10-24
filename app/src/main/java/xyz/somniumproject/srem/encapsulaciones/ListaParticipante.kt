package xyz.somniumproject.srem.encapsulaciones

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Created by frang on 10/23/2017.
 */
internal class ListaParticipante(
        var listado: List<Participante>
) {
    class Deserializer : ResponseDeserializable<ListaParticipante> {
        override fun deserialize(content: String) = Gson().fromJson(content, ListaParticipante::class.java)
    }
}