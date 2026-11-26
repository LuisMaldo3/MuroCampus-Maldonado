package com.maldonado.murocampus.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maldonado.murocampus.ui.theme.AzulCampus
import com.maldonado.murocampus.ui.theme.Borde
import com.maldonado.murocampus.ui.theme.TextoSuave
import com.maldonado.murocampus.ui.theme.colorUsuario
import com.maldonado.murocampus.viewmodel.PostsViewModel
import com.maldonado.murocampus.viewmodel.UiState

@Composable
fun PantallaLista(
    vm: PostsViewModel,
    onAbrir: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by vm.lista.collectAsStateWithLifecycle()
    val filtro by vm.usuarioFiltro.collectAsStateWithLifecycle()

    Column(modifier.fillMaxSize()) {
        FilaFiltros(seleccionado = filtro, onElegir = { vm.filtrar(it) })

        Crossfade(targetState = estado, label = "estadoLista", modifier = Modifier.weight(1f)) { e ->
            when (e) {
                UiState.Cargando -> EsqueletoLista()
                is UiState.Error -> ErrorConReintento(e.mensaje, onReintentar = { vm.cargar() })
                is UiState.Exito -> {
                    if (e.datos.isEmpty()) {
                        EstadoVacio("Sin publicaciones", "Este usuario todavía no ha publicado.")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                val n = e.datos.size
                                Text(
                                    if (n == 1) "1 publicación" else "$n publicaciones",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TextoSuave,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                )
                            }
                            items(e.datos, key = { it.id }) { post ->
                                TarjetaPost(
                                    postId = post.id,
                                    userId = post.userId,
                                    titulo = post.title,
                                    cuerpo = post.body,
                                    onClick = { onAbrir(post.id) },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CampoBusqueda(texto: String, onCambio: (String) -> Unit) {
    OutlinedTextField(
        value = texto,
        onValueChange = onCambio,
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 14.dp),
        placeholder = { Text("Buscar por título") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (texto.isNotEmpty()) {
                IconButton(onClick = { onCambio("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Borrar búsqueda")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedBorderColor = Borde
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilaFiltros(seleccionado: Int?, onElegir: (Int?) -> Unit) {
    val opciones: List<Int?> = listOf<Int?>(null) + (1..10).toList()
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(opciones) { id ->
            val activo = seleccionado == id
            FilterChip(
                selected = activo,
                onClick = { onElegir(id) },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (id != null && !activo) {
                            Box(Modifier.size(8.dp).background(colorUsuario(id), CircleShape))
                            Spacer(Modifier.width(6.dp))
                        }
                        Text(if (id == null) "Todos" else "Usuario $id")
                    }
                },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    selectedContainerColor = AzulCampus,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}
