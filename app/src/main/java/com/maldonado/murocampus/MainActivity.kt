package com.maldonado.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.maldonado.murocampus.data.PostRepositoryRemote
import com.maldonado.murocampus.data.remote.RetrofitClient
import com.maldonado.murocampus.ui.BarraMuroCampus
import com.maldonado.murocampus.ui.PantallaCrear
import com.maldonado.murocampus.ui.PantallaDetalle
import com.maldonado.murocampus.ui.PantallaLista
import com.maldonado.murocampus.ui.theme.MuroCampusTheme
import com.maldonado.murocampus.viewmodel.PostsViewModel
import com.maldonado.murocampus.viewmodel.PostsViewModelFactory

class MainActivity : ComponentActivity() {

    private val vm: PostsViewModel by viewModels {
        PostsViewModelFactory(PostRepositoryRemote(RetrofitClient.api))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuroCampusTheme {
                AppMuroCampus(vm)
            }
        }
    }
}

@Composable
fun AppMuroCampus(vm: PostsViewModel) {
    // null = se ve la lista; un número = se ve el detalle de esa publicación
    var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
    var creando by rememberSaveable { mutableStateOf(false) }

    // Botón Atrás del sistema: vuelve a la lista
    BackHandler(enabled = idSeleccionado != null || creando) {
        idSeleccionado = null
        creando = false
    }

    Scaffold(
        topBar = { BarraMuroCampus() },
        floatingActionButton = {
            if (idSeleccionado == null && !creando) {
                FloatingActionButton(onClick = {
                    vm.reiniciarEnvio()
                    creando = true
                }) {
                    Text("+", style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
    ) { padding ->
        val mod = Modifier.padding(padding)
        val id = idSeleccionado
        when {
            creando -> PantallaCrear(vm, mod)
            id != null -> PantallaDetalle(vm, id, mod)
            else -> PantallaLista(vm, onAbrir = { idSeleccionado = it }, modifier = mod)
        }
    }
}
