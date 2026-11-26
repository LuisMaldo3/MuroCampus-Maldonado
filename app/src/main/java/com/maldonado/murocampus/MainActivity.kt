package com.maldonado.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        // Íconos de la barra de estado en blanco sobre el encabezado azul
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
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
    val volver: () -> Unit = {
        idSeleccionado = null
        creando = false
    }

    // Botón Atrás del sistema: vuelve a la lista
    BackHandler(enabled = idSeleccionado != null || creando, onBack = volver)

    val id = idSeleccionado
    val pantalla = when {
        creando -> "crear"
        id != null -> "detalle:$id"
        else -> "lista"
    }

    Scaffold(
        topBar = {
            BarraMuroCampus(
                subtitulo = when {
                    creando -> "Nueva publicación"
                    id != null -> "Detalle de la publicación"
                    else -> "El muro de tu campus"
                },
                onVolver = if (pantalla == "lista") null else volver
            )
        },
        floatingActionButton = {
            if (pantalla == "lista") {
                ExtendedFloatingActionButton(
                    onClick = {
                        vm.reiniciarEnvio()
                        creando = true
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Publicar") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Crossfade(targetState = pantalla, label = "pantallas", modifier = Modifier.padding(padding)) { p ->
            when {
                p == "crear" -> PantallaCrear(vm)
                p.startsWith("detalle:") -> PantallaDetalle(
                    vm = vm,
                    id = p.removePrefix("detalle:").toInt(),
                )
                else -> PantallaLista(vm, onAbrir = { idSeleccionado = it })
            }
        }
    }
}
