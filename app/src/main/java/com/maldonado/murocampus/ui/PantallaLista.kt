package com.maldonado.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maldonado.murocampus.viewmodel.PostsViewModel
import com.maldonado.murocampus.viewmodel.UiState

@Composable
fun PantallaLista(
    vm: PostsViewModel,
    onAbrir: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by vm.lista.collectAsStateWithLifecycle()

    Box(modifier.fillMaxSize()) {
        when (val e = estado) {
            UiState.Cargando -> Cargando()
            is UiState.Error -> ErrorConReintento(e.mensaje, onReintentar = { vm.cargar() })
            is UiState.Exito -> LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(e.datos, key = { it.id }) { post ->
                    TarjetaPost(
                        userId = post.userId,
                        titulo = post.title,
                        cuerpo = post.body,
                        onClick = { onAbrir(post.id) }
                    )
                }
            }
        }
    }
}
