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
import com.example.prova2cugler.Classes.Cliente
import com.example.prova2cugler.ui.theme.Purple40
import com.example.prova2cugler.ui.theme.Purple80
import com.example.prova2cugler.ui.theme.PurpleGrey80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCliente(navController: NavController, dao: DAO) {
    //inicializa variaveis
    var clientes by remember { mutableStateOf(emptyList<Cliente>()) }
    var filteredClientes by remember { mutableStateOf(emptyList<Cliente>()) }
    var isFiltering by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var cpfBusca by remember { mutableStateOf(TextFieldValue()) }
    var nomeBusca by remember { mutableStateOf(TextFieldValue()) }


    var cpf by remember { mutableStateOf(TextFieldValue()) }
    var nome by remember { mutableStateOf(TextFieldValue()) }
    var email by remember { mutableStateOf(TextFieldValue()) }
    var instagram by remember { mutableStateOf(TextFieldValue("@")) }

    var editingCliente by remember { mutableStateOf<Cliente?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var confirmDialogType by remember { mutableStateOf("") }
    var clienteToBeDeleted by remember { mutableStateOf<Cliente?>(null) }


    val context = LocalContext.current

    LaunchedEffect(Unit) {
        clientes = dao.listarClientes()
        filteredClientes = clientes
    }


    fun addOuAtualizaCliente() {
        showConfirmDialog = true
        confirmDialogType = if (editingCliente == null) "Adicionar" else "Editar Cliente"
    }

    fun confirmaCliente() {
        val cliente = Cliente(
            cpf = cpf.text,
            nome = nome.text,
            email = email.text,
            instagram = instagram.text
        )

        CoroutineScope(Dispatchers.Main).launch {
            if (editingCliente == null) {
                withContext(Dispatchers.IO) {
                    dao.inserirCliente(cliente)
                }
                Toast.makeText(context, "Cliente adicionado com sucesso!", Toast.LENGTH_SHORT).show()
            } else {
                withContext(Dispatchers.IO) {
                    dao.atualizarCliente(cliente)
                }
                Toast.makeText(context, "Cliente atualizado com sucesso!", Toast.LENGTH_SHORT).show()
            }
            clientes = withContext(Dispatchers.IO) { dao.listarClientes() }
            filteredClientes = clientes
        }
        showConfirmDialog = false
    }

    fun removeCliente(cliente: Cliente) {
        clienteToBeDeleted = cliente
        showConfirmDialog = true
        confirmDialogType = "Excluir"
    }

    fun confirmaRemoveCliente() {
        clienteToBeDeleted?.let { cliente ->
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    dao.deletarCliente(cliente.cpf)
                }
                clientes = withContext(Dispatchers.IO) { dao.listarClientes() }
                filteredClientes = clientes
                Toast.makeText(context, "Cliente excluído com sucesso!", Toast.LENGTH_SHORT).show()
                if (editingCliente == cliente) {
                    editingCliente = null
                }
            }
        }
        showConfirmDialog = false
    }


    fun filtro() {
        filteredClientes = if (cpfBusca.text.isNotBlank()) {
            clientes.filter { it.cpf == cpfBusca.text.filter { it.isDigit() } }
        } else if (nomeBusca.text.isNotBlank()) {
            clientes.filter { it.nome.contains(nomeBusca.text, ignoreCase = true) }
        } else {
            clientes
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
                    text = "Clientes",
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
                            value = cpf,
                            onValueChange = { textFieldValue ->
                                val newText = textFieldValue.text.filter { it.isDigit() }
                                val cursorPosition = if (newText.length > textFieldValue.text.length) {
                                    newText.length
                                } else {
                                    textFieldValue.selection.end + (newText.length - textFieldValue.text.length)
                                }
                                cpf = TextFieldValue(newText, TextRange(cursorPosition))
                            },
                            label = { Text("CPF") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = nome,
                            onValueChange = {nome = it},
                            label = { Text("Nome") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = instagram,
                            onValueChange = { instagram = it },
                            label = { Text("Instagram") },
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
                            Button(onClick = { addOuAtualizaCliente() },
                                colors = ButtonDefaults.buttonColors(
                                    Purple40, Color.White
                                )
                            ) {
                                Text(if (editingCliente == null) "Adicionar Cliente" else "Salvar Edição")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    items(filteredClientes.size) { index ->
                        val cliente = filteredClientes[index]
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
                                Text("CPF: ${cliente.cpf}")
                                Text("Nome: ${cliente.nome}")
                                Text("Email: ${cliente.email}")
                                Text("Instagram: ${cliente.instagram}")

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(onClick = {
                                        cpf = TextFieldValue(cliente.cpf)
                                        nome = TextFieldValue(cliente.nome)
                                        email = TextFieldValue(cliente.email)
                                        instagram = TextFieldValue(cliente.instagram)
                                        editingCliente = cliente
                                    },
                                        colors = ButtonDefaults.buttonColors(
                                            Purple40, Color.White
                                        )
                                    ) {
                                        Text("Editar")
                                    }
                                    Button(
                                        onClick = { removeCliente(cliente) },
                                        colors = ButtonDefaults.buttonColors(
                                            Purple40, Color.White
                                        )
                                    ) {
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
                        Text("Buscar Cliente por CPF")
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
                                text = if (confirmDialogType == "Excluir") "Deseja excluir este cliente?"
                                else "Deseja ${confirmDialogType.lowercase()} este cliente?"
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (confirmDialogType == "Excluir") confirmaRemoveCliente()
                                    else confirmaCliente()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    Purple40, Color.White
                                )
                            ) {
                                Text("Confirmar")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showConfirmDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    Purple40, Color.White
                                )) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

                if (showSearchDialog) {
                    AlertDialog(
                        onDismissRequest = { showSearchDialog = false },
                        title = { Text("Buscar Cliente") },
                        text = {
                            Column {
                                    TextField(
                                        value = cpfBusca,
                                        onValueChange = { textFieldValue ->
                                            val newText = textFieldValue.text.filter { it.isDigit() }
                                            val cursorPosition = if (newText.length > textFieldValue.text.length) {
                                                newText.length
                                            } else {
                                                textFieldValue.selection.end + (newText.length - textFieldValue.text.length)
                                            }
                                            cpfBusca = TextFieldValue(newText, TextRange(cursorPosition))
                                        },
                                        label = { Text("CPF") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                TextField(
                                    value = nomeBusca,
                                    onValueChange = { textFieldValue ->
                                        nomeBusca = textFieldValue
                                    },
                                    label = { Text("Nome") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = { filtro() }) {
                                Text("Buscar")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showSearchDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    )
}
