import com.example.prova2cugler.Classes.Bicicleta
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.example.prova2cugler.Classes.Cliente

class DAO {
    private val db = FirebaseFirestore.getInstance()
    private val clientesCollection = db.collection("clientes")
    private val bicicletaCollection = db.collection("bicicletas")


    suspend fun inserirCliente(cliente: Cliente) {
        withContext(Dispatchers.IO) {
            val data = hashMapOf(
                "cpf" to cliente.cpf,
                "nome" to cliente.nome,
                "email" to cliente.email,
                "instagram" to cliente.instagram
            )
            clientesCollection.document(cliente.cpf).set(data).await()
        }
    }

    suspend fun listarClientes(): List<Cliente> {
        return withContext(Dispatchers.IO) {
            val snapshot = clientesCollection.get().await()
            snapshot.documents.map { doc ->
                Cliente(
                    doc.getString("cpf")!!,
                    doc.getString("nome")!!,
                    doc.getString("email")!!,
                    doc.getString("instagram")!!
                )
            }
        }
    }
//
//    suspend fun buscarCliente(cpf: String): Cliente? {
//        return withContext(Dispatchers.IO) {
//            val document = clientesCollection.document(cpf).get().await()
//            document.toObject(Cliente::class.java)
//        }
//    }

    suspend fun deletarCliente(cpf: String) {
        withContext(Dispatchers.IO) {
            clientesCollection.document(cpf).delete().await()
        }
    }

    suspend fun atualizarCliente(cliente: Cliente) {
        withContext(Dispatchers.IO) {
            val data = hashMapOf(
                "cpf" to cliente.cpf,
                "nome" to cliente.nome,
                "email" to cliente.email,
                "instagram" to cliente.instagram
            )
            clientesCollection.document(cliente.cpf).set(data).await()
        }
    }
    suspend fun listarBicicletas(): List<Bicicleta> {
        return try {
            val snapshot = bicicletaCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Bicicleta::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun inserirBicicleta(bicicleta: Bicicleta) {
        try {
            bicicletaCollection.document(bicicleta.codigo).set(bicicleta).await()
        } catch (e: Exception) {
        }
    }

    suspend fun atualizarBicicleta(bicicleta: Bicicleta) {
        try {
            bicicletaCollection.document(bicicleta.codigo).set(bicicleta).await()
        } catch (e: Exception) {
        }
    }

    suspend fun deletarBicicleta(codigo: String) {
        try {
            bicicletaCollection.document(codigo).delete().await()
        } catch (e: Exception) {
        }
    }

//    suspend fun buscarClientePorNome(nome: String): List<Cliente> {
//        return withContext(Dispatchers.IO) {
//            val query = clientesCollection.whereEqualTo("nome", nome).get().await()
//            query.documents.mapNotNull { it.toObject(Cliente::class.java) }
//        }
//    }
//
//    suspend fun buscarBicicletaPorModelo(modelo: String): List<Bicicleta> {
//        return withContext(Dispatchers.IO) {
//            val query = bicicletaCollection.whereEqualTo("modelo", modelo).get().await()
//            query.documents.mapNotNull { it.toObject(Bicicleta::class.java) }
//        }
//    }
}
