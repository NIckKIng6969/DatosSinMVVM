package com.example.datossinmvvm

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import com.example.datossinmvvm.ui.theme.DatosSinMVVMTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun crearDatabase(context: Context): UserDatabase {
    return UserDatabase.getInstance(context)
}

suspend fun getUsers(dao: UserDao): String = withContext(Dispatchers.IO) {
    val lista = dao.getAll()
    var rpta = ""
    for (u in lista) {
        rpta += "ID: ${u.uid}  ${u.firstName} ${u.lastName}\n"
    }
    rpta
}

suspend fun AgregarUsuario(user: User, dao: UserDao) = withContext(Dispatchers.IO) {
    dao.insert(user)
}

// Ejercicio 1: elimina el ultimo registro agregado
suspend fun eliminarUltimo(dao: UserDao): String = withContext(Dispatchers.IO) {
    val ultimo = dao.getLast()
    if (ultimo != null) {
        dao.delete(ultimo)
        "Se elimino el ultimo registro: ${ultimo.firstName} ${ultimo.lastName}"
    } else {
        "No hay registros para eliminar"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenUser() {
    val context = LocalContext.current
    val db: UserDatabase = crearDatabase(context)
    val dao = db.dao
    val coroutineScope = rememberCoroutineScope()

    var id by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    val dataUser = remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuarios - SQLite") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            dataUser.value = getUsers(dao)
                            mensaje = ""
                        }
                    }) { Text("Listar") }
                    TextButton(onClick = {
                        coroutineScope.launch {
                            AgregarUsuario(User(0, firstName, lastName), dao)
                            firstName = ""
                            lastName = ""
                            mensaje = "Usuario agregado"
                        }
                    }) { Text("Agregar") }
                    TextButton(onClick = {
                        coroutineScope.launch {
                            mensaje = eliminarUltimo(dao)
                            dataUser.value = getUsers(dao)
                        }
                    }) { Text("Eliminar") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Spacer(Modifier.height(20.dp))
            TextField(
                value = id,
                onValueChange = { id = it },
                label = { Text("ID (solo lectura)") },
                readOnly = true,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name: ") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name:") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        AgregarUsuario(User(0, firstName, lastName), dao)
                        firstName = ""
                        lastName = ""
                        mensaje = "Usuario agregado"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Agregar Usuario", fontSize = 16.sp) }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        dataUser.value = getUsers(dao)
                        mensaje = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Listar Usuarios", fontSize = 16.sp) }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        mensaje = eliminarUltimo(dao)
                        dataUser.value = getUsers(dao)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Eliminar Ultimo Usuario", fontSize = 16.sp) }
            Spacer(Modifier.height(16.dp))
            Text(text = mensaje, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Text(text = dataUser.value, fontSize = 20.sp)
        }
    }
}


@Preview(showBackground = true, heightDp = 800)
@Composable
fun PreviewScreenUser() {
    DatosSinMVVMTheme {
        ScreenUser()
    }
}
