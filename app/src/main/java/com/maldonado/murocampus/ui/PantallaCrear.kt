package com.maldonado.murocampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maldonado.murocampus.ui.theme.Borde
import com.maldonado.murocampus.ui.theme.TextoSuave
import com.maldonado.murocampus.ui.theme.VerdeOk
import com.maldonado.murocampus.ui.theme.VerdeOkFondo
import com.maldonado.murocampus.viewmodel.PostsViewModel
import com.maldonado.murocampus.viewmodel.UiState

@Composable
fun PantallaCrear(vm: PostsViewModel, modifier: Modifier = Modifier) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var texto by rememberSaveable { mutableStateOf("") }
    val envio by vm.envio.collectAsStateWithLifecycle()

    // Si se publicó bien, limpia el formulario. Si falló, conserva lo escrito.
    LaunchedEffect(envio) {
        if (envio is UiState.Exito) {
            titulo = ""
            texto = ""
        }
    }

    val enviando = envio is UiState.Cargando

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Borde)
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarUsuario(1, 44.dp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Nueva publicación", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Publicarás como Usuario 1",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSuave
                        )
                    }
                }
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    supportingText = { Text("${titulo.length} caracteres") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    label = { Text("Contenido") },
                    minLines = 5,
                    shape = RoundedCornerShape(14.dp),
                    supportingText = { Text("${texto.length} caracteres") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { vm.publicar(titulo, texto) },
                    enabled = titulo.isNotBlank() && texto.isNotBlank() && !enviando,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    if (enviando) {
                        CircularProgressIndicator(
                            Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Publicando…")
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Publicar")
                    }
                }
            }
        }

        when (val e = envio) {
            null, UiState.Cargando -> Unit
            is UiState.Exito -> Surface(
                color = VerdeOkFondo,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VerdeOk)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Publicado con id ${e.datos.id}",
                            color = VerdeOk,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "«${e.datos.title}»",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSuave,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            is UiState.Error -> Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    e.mensaje,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
