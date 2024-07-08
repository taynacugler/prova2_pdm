package com.example.prova2cugler

import DAO
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.prova2cugler.Classes.Bicicleta
import com.example.prova2cugler.ui.theme.Purple40
import com.example.prova2cugler.ui.theme.PurpleGrey80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaBicicleta(navController: NavController, dao: DAO) {
    var bicicletas by remember { mutableStateOf(emptyList<Bicicleta>()) }
    var filteredBicicletas by remember { mutableStateOf(emptyList<Bicicleta>()) }
    var isFiltering by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var codigoBusca by remember { mutableStateOf(TextFieldValue()) }
    var modeloBusca by remember { mutableStateOf(TextFieldValue()) }

    var codigo by remember { mutableStateOf(TextFieldValue()) }
    var modelo by remember { mutableStateOf(TextFieldValue()) }
    var materialDoChassi by remember { mutableStateOf(TextFieldValue()) }
    var aro by remember { mutableStateOf(TextFieldValue()) }
    var preco by remember { mutableStateOf(TextFieldValue()) }
    var quantidadeDeMarchas by remember { mutableStateOf(TextFieldValue()) }

    var editingBicicleta by remember { mutableStateOf<Bicicleta?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var confirmDialogType by remember { mutableStateOf("") }
    var bicicletaToBeDeleted by remember { mutableStateOf<Bicicleta?>(null) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        bicicletas = dao.listarBicicletas()
        filteredBicicletas = bicicletas
    }




    fun addOuAtualizarBicicleta() {

        showConfirmDialog = true
        confirmDialogType = if (editingBicicleta == null) "Adicionar" else "Salvar"
    }

    fun confirmaBicicleta() {
        val bicicleta = Bicicleta(
            codigo = codigo.text,
            modelo = modelo.text,
            materialDoChassi = materialDoChassi.text,
            aro = aro.text,
            preco = preco.text.toDouble(),
            quantidadeDeMarchas = quantidadeDeMarchas.text
        )

        CoroutineScope(Dispatchers.Main).launch {
            if (editingBicicleta == null) {
                withContext(Dispatchers.IO) {
                    dao.inserirBicicleta(bicicleta)
                }
                Toast.makeText(context, "Bicicleta adicionada com sucesso!", Toast.LENGTH_SHORT).show()
            } else {
                withContext(Dispatchers.IO) {
                    dao.atualizarBicicleta(bicicleta)
                }
                Toast.makeText(context, "Bicicleta adicionado com sucesso!", Toast.LENGTH_SHORT).show()
            }
            bicicletas = withContext(Dispatchers.IO) { dao.listarBicicletas() }
            filteredBicicletas = bicicletas
        }
        showConfirmDialog = false
    }

    fun removeBicicleta(bicicleta: Bicicleta) {
        bicicletaToBeDeleted = bicicleta
        showConfirmDialog = true
        confirmDialogType = "Excluir"
    }

    fun confirmaRemoveBicicleta() {
        bicicletaToBeDeleted?.let { bicicleta ->
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    dao.deletarBicicleta(bicicleta.codigo)
                }
                bicicletas = withContext(Dispatchers.IO) { dao.listarBicicletas() }
                filteredBicicletas = bicicletas
                Toast.makeText(context, "Bicicleta excluída", Toast.LENGTH_SHORT).show()
                if (editingBicicleta == bicicleta) {
                    editingBicicleta = null
                }
            }
        }
        showConfirmDialog = false
    }

    fun filtro() {
        filteredBicicletas = bicicletas.filter {
            it.codigo.contains(codigoBusca.text, ignoreCase = true) &&
                    it.modelo.contains(modeloBusca.text, ignoreCase = true)
        }
        isFiltering = true
        showSearchDialog = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "JeffBikes",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bicicletas",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        TextField(
                            value = codigo,
                            onValueChange = { codigo = it },
                            label = { Text("Código") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = modelo,
                            onValueChange = { modelo = it },
                            label = { Text("Modelo") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = materialDoChassi,
                            onValueChange = { materialDoChassi = it },
                            label = { Text("Material do Chassi") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = aro,
                            onValueChange = { aro = it },
                            label = { Text("Aro") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = preco,
                            onValueChange = { preco = it },
                            label = { Text("Preço") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = quantidadeDeMarchas,
                            onValueChange = { quantidadeDeMarchas = it },
                            label = { Text("Quantidade de Marchas") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = { addOuAtualizarBicicleta() },
                                colors = ButtonDefaults.buttonColors(
                                    Purple40, Color.White
                                )) {
                                Text(if (editingBicicleta == null) "Adicionar Bicicleta" else "Salvar Edição")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    items(filteredBicicletas.size) { index ->
                        val bicicleta = filteredBicicletas[index]
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = PurpleGrey80,
                                contentColor = Color.Black
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text("Código: ${bicicleta.codigo}")
                                Text("Modelo: ${bicicleta.modelo}")
                                Text("Material do Chassi: ${bicicleta.materialDoChassi}")
                                Text("Aro: ${bicicleta.aro}")
                                Text("Preço: ${bicicleta.preco}")
                                Text("Quantidade de Marchas: ${bicicleta.quantidadeDeMarchas}")

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(onClick = {
                                        codigo = TextFieldValue(bicicleta.codigo)
                                        modelo = TextFieldValue(bicicleta.modelo)
                                        materialDoChassi = TextFieldValue(bicicleta.materialDoChassi)
                                        aro = TextFieldValue(bicicleta.aro)
                                        preco = TextFieldValue(bicicleta.preco.toString())
                                        quantidadeDeMarchas = TextFieldValue(bicicleta.quantidadeDeMarchas)
                                        editingBicicleta = bicicleta
                                    },
                                        colors = ButtonDefaults.buttonColors(
                                            Purple40, Color.White
                                        )) {
                                        Text("Editar")
                                    }
                                    Button(onClick = { removeBicicleta(bicicleta) },
                                        colors = ButtonDefaults.buttonColors(
                                            Purple40, Color.White
                                        )) {
                                        Text("Excluir")
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { showSearchDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            Purple40, Color.White
                        )) {
                        Text("Buscar Bicicleta por Código")
                    }
                    if (isFiltering) {
                        Button(onClick = { filtro() },
                            colors = ButtonDefaults.buttonColors(
                                Purple40, Color.White
                            )) {
                            Text("Remover Filtro")
                        }
                    }
                }

                if (showConfirmDialog) {
                    AlertDialog(
                        onDismissRequest = { showConfirmDialog = false },
                        title = {
                            Text(
                                text = if (confirmDialogType == "Excluir") "Confirmação de Exclusão"
                                else "Confirmação de ${confirmDialogType}"
                            )
                        },
                        text = {
                            Text(
                                text = if (confirmDialogType == "Excluir") "Deseja excluir esta bicicleta?"
                                else "Deseja ${confirmDialogType.lowercase()} esta bicicleta?"
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (confirmDialogType == "Excluir") confirmaRemoveBicicleta()
                                    else confirmaBicicleta()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    Purple40, Color.White
                                )
                            ) {
                                Text("Confirmar")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showConfirmDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

                if (showSearchDialog) {
                    AlertDialog(
                        onDismissRequest = { showSearchDialog = false },
                        title = { Text("Buscar Bicicleta") },
                        text = {
                            Column {
                                TextField(
                                    value = codigoBusca,
                                    onValueChange = { codigoBusca = it },
                                    label = { Text("Código") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                TextField(
                                    value = modeloBusca,
                                    onValueChange = { modeloBusca = it },
                                    label = { Text("Modelo") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = { filtro() },
                                colors = ButtonDefaults.buttonColors(
                                    Purple40, Color.White
                                )) {
                                Text("Buscar")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showSearchDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    Purple40, Color.White
                                )) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    )
}
