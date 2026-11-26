package com.maldonado.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maldonado.murocampus.data.PostRepository
import com.maldonado.murocampus.model.Comentario
import com.maldonado.murocampus.model.NuevoPost
import com.maldonado.murocampus.model.Post
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetalleUi(val post: Post, val comentarios: List<Comentario>)

// Solo conoce la interface PostRepository: nada de Retrofit, PostApi ni HttpException
class PostsViewModel(private val repo: PostRepository) : ViewModel() {

    // ---------- Paso 6: lista ----------
    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista: StateFlow<UiState<List<Post>>> = _lista.asStateFlow()

    // ---------- Paso 8: filtro (null = Todos) ----------
    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro: StateFlow<Int?> = _usuarioFiltro.asStateFlow()

    // ---------- Paso 7: detalle ----------
    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle: StateFlow<UiState<DetalleUi>> = _detalle.asStateFlow()

    // ---------- Paso 9: crear (null = aún no se envió) ----------
    private val _envio = MutableStateFlow<UiState<Post>?>(null)
    val envio: StateFlow<UiState<Post>?> = _envio.asStateFlow()

    // ---------- Fase 2: borrar (null = nada en curso) ----------
    private val _borrado = MutableStateFlow<UiState<Int>?>(null)
    val borrado: StateFlow<UiState<Int>?> = _borrado.asStateFlow()

    // Mensaje corto para el Snackbar
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private var trabajoLista: Job? = null
    private var trabajoDetalle: Job? = null
    private var idDetalle: Int? = null

    init {
        cargar()
    }

    fun cargar() {
        trabajoLista?.cancel() // si tocas chips rápido, solo vale la última petición
        trabajoLista = viewModelScope.launch {
            _lista.value = UiState.Cargando
            val userId = _usuarioFiltro.value
            val resultado = if (userId == null) repo.listar() else repo.porUsuario(userId)
            resultado
                .onSuccess { _lista.value = UiState.Exito(it) }
                .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun filtrar(userId: Int?) {
        _usuarioFiltro.value = userId
        cargar()
    }

    fun abrir(id: Int) {
        // Si rotas la pantalla no vuelve a descargar lo mismo
        if (id == idDetalle && _detalle.value is UiState.Exito) return
        idDetalle = id
        _borrado.value = null
        trabajoDetalle?.cancel()
        _detalle.value = UiState.Cargando
        trabajoDetalle = viewModelScope.launch {
            // Pide el post y los comentarios a la vez
            val (rPost, rComentarios) = coroutineScope {
                val post = async { repo.detalle(id) }
                val comentarios = async { repo.comentarios(id) }
                post.await() to comentarios.await()
            }
            val post = rPost.getOrElse {
                _detalle.value = UiState.Error(mensajeDe(it))
                return@launch
            }
            val comentarios = rComentarios.getOrElse {
                _detalle.value = UiState.Error(mensajeDe(it))
                return@launch
            }
            _detalle.value = UiState.Exito(DetalleUi(post, comentarios))
        }
    }

    fun publicar(titulo: String, texto: String) {
        if (_envio.value is UiState.Cargando) return
        _envio.value = UiState.Cargando
        viewModelScope.launch {
            repo.crear(NuevoPost(userId = 1, title = titulo.trim(), body = texto.trim()))
                .onSuccess { _envio.value = UiState.Exito(it) }
                .onFailure { _envio.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun reiniciarEnvio() {
        _envio.value = null
    }

    fun borrar(id: Int) {
        if (_borrado.value is UiState.Cargando) return
        _borrado.value = UiState.Cargando
        viewModelScope.launch {
            repo.borrar(id)
                .onSuccess {
                    // JSONPlaceholder no borra de verdad: la quitamos de la lista local
                    val actual = _lista.value
                    if (actual is UiState.Exito) {
                        _lista.value = UiState.Exito(actual.datos.filterNot { it.id == id })
                    }
                    idDetalle = null
                    _borrado.value = UiState.Exito(id)
                    _mensaje.value = "Publicación #$id eliminada"
                }
                .onFailure { _borrado.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun reiniciarBorrado() {
        _borrado.value = null
    }

    fun mensajeMostrado() {
        _mensaje.value = null
    }
}
