package com.maldonado.murocampus.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.maldonado.murocampus.ui.theme.AzulCampus
import com.maldonado.murocampus.ui.theme.AzulProfundo
import com.maldonado.murocampus.ui.theme.Borde
import com.maldonado.murocampus.ui.theme.Cielo
import com.maldonado.murocampus.ui.theme.TextoSuave
import com.maldonado.murocampus.ui.theme.colorUsuario

// Componentes reutilizables con el diseño de la Fase 2.

/** Encabezado con degradado azul; muestra la flecha Volver si se le pasa onVolver. */
@Composable
fun BarraMuroCampus(
    subtitulo: String = "El muro de tu campus",
    onVolver: (() -> Unit)? = null
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(AzulProfundo, AzulCampus, Cielo)))
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onVolver != null) {
                IconButton(onClick = onVolver) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
            } else {
                Spacer(Modifier.width(12.dp))
            }
            Column {
                Text(
                    "MuroCampus",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Text(
                    subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.82f)
                )
            }
        }
    }
}

@Composable
fun AvatarLetras(texto: String, color: Color, tamano: Dp = 40.dp) {
    Box(
        Modifier.size(tamano).background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            texto,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = if (tamano >= 48.dp) MaterialTheme.typography.titleMedium
            else MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun AvatarUsuario(userId: Int, tamano: Dp = 40.dp) {
    AvatarLetras("U$userId", colorUsuario(userId), tamano)
}

@Composable
fun EtiquetaUsuario(userId: Int) {
    val color = colorUsuario(userId)
    Surface(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(50)) {
        Text(
            "Usuario $userId",
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun TarjetaPost(
    postId: Int,
    userId: Int,
    titulo: String,
    cuerpo: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(18.dp)
    Card(
        modifier = modifier.fillMaxWidth().clip(forma).clickable(onClick = onClick),
        shape = forma,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Borde)
    ) {
        Row(Modifier.padding(14.dp)) {
            AvatarUsuario(userId)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EtiquetaUsuario(userId)
                    Spacer(Modifier.weight(1f))
                    Text("#$postId", style = MaterialTheme.typography.labelSmall, color = TextoSuave)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    titulo,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    cuerpo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSuave,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** Tarjetas "fantasma" que laten mientras carga la lista. */
@Composable
fun EsqueletoLista() {
    val transicion = rememberInfiniteTransition(label = "esqueleto")
    val alfa by transicion.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(850), RepeatMode.Reverse),
        label = "alfa"
    )
    Column(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        ) {
            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
            Text("Cargando publicaciones…", style = MaterialTheme.typography.bodyMedium, color = TextoSuave)
        }
        repeat(5) {
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Borde)
            ) {
                Row(Modifier.padding(14.dp)) {
                    Box(Modifier.size(40.dp).alpha(alfa).background(Borde, CircleShape))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.fillMaxWidth(0.3f).height(12.dp).alpha(alfa).background(Borde, RoundedCornerShape(6.dp)))
                        Box(Modifier.fillMaxWidth(0.9f).height(14.dp).alpha(alfa).background(Borde, RoundedCornerShape(6.dp)))
                        Box(Modifier.fillMaxWidth(0.7f).height(12.dp).alpha(alfa).background(Borde, RoundedCornerShape(6.dp)))
                    }
                }
            }
        }
    }
}

@Composable
fun Cargando(texto: String = "Cargando publicaciones…") {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CircularProgressIndicator()
            Text(texto, color = TextoSuave)
        }
    }
}

@Composable
fun ErrorConReintento(mensaje: String, onReintentar: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(72.dp).background(MaterialTheme.colorScheme.errorContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text("Algo salió mal", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                mensaje,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(14.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        Button(onClick = onReintentar, shape = RoundedCornerShape(14.dp)) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Reintentar")
        }
    }
}

@Composable
fun EstadoVacio(titulo: String, detalle: String) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(72.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Text(
            detalle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSuave,
            textAlign = TextAlign.Center
        )
    }
}
