package com.example.fazfavor_atual

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.fazfavor_atual.telas.*
import com.example.fazfavor_atual.ui.theme.FazFavor_AtualTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BancoDeDados.ligarRadar()

        setContent {
            FazFavor_AtualTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().safeDrawingPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var telaAtual by remember { mutableStateOf("login") }
                    var erroDeCadastro by remember { mutableStateOf("") }
                    var mensagemLogin by remember { mutableStateOf("") }
                    var nomeLogado by remember { mutableStateOf("") }
                    var emailLogado by remember { mutableStateOf("") }
                    var veiculoLogado by remember { mutableStateOf("") }
                    var placaLogada by remember { mutableStateOf("") }
                    var caronaSelecionada by remember { mutableStateOf<Carona?>(null) }

                    when (telaAtual) {
                        "login" -> LoginScreen(
                            aoFazerLogin = { email, senha ->
                                mensagemLogin = "Conectando ao servidor..."
                                BancoDeDados.fazerLoginNuvem(email, senha) { usuarioEncontrado, erro ->
                                    if (usuarioEncontrado != null) {
                                        nomeLogado = usuarioEncontrado.nome
                                        emailLogado = usuarioEncontrado.email
                                        veiculoLogado = usuarioEncontrado.veiculo
                                        placaLogada = usuarioEncontrado.placa
                                        mensagemLogin = ""

                                        if (usuarioEncontrado.veiculo.isNotEmpty()) {
                                            telaAtual = "status"
                                        } else {
                                            BancoDeDados.buscarCaronasDoServidor()
                                            telaAtual = "listaCaronas"
                                        }
                                    } else {
                                        mensagemLogin = erro
                                    }
                                }
                            },
                            aoClicarCriarConta = {
                                erroDeCadastro = ""
                                mensagemLogin = ""
                                telaAtual = "cadastro"
                            },
                            mensagemErro = mensagemLogin
                        )
                        "cadastro" -> CadastroScreen(
                            aoConcluirCadastro = { nome, cpf, telefone, email, senha, veiculo, placa, vagas ->
                                if (nome.isBlank() || cpf.isBlank() || telefone.isBlank() || email.isBlank() || senha.isBlank()) {
                                    erroDeCadastro = "Preencha todos os campos obrigatórios!"
                                } else {
                                    erroDeCadastro = "Conectando ao servidor..."
                                    BancoDeDados.cadastrarUsuarioNuvem(nome, cpf, telefone, email, senha, veiculo, placa, vagas) { sucesso, mensagem ->
                                        if (sucesso) {
                                            erroDeCadastro = ""
                                            telaAtual = "login"
                                        } else {
                                            erroDeCadastro = mensagem
                                        }
                                    }
                                }
                            },
                            aoClicarFechar = {
                                erroDeCadastro = ""
                                telaAtual = "login"
                            },
                            mensagemErro = erroDeCadastro
                        )
                        "criarEvento" -> CriarEventoScreen(
                            aoPublicarEvento = { nomeEvento, origem, destino, horario, vagas ->
                                val origemComEvento = "$nomeEvento - $origem"
                                BancoDeDados.enviarCaronaParaServidor(origemComEvento, destino, horario, vagas, nomeLogado)
                                BancoDeDados.temEventoAtivo = true
                                telaAtual = "status"
                            },
                            aoClicarSair = {
                                telaAtual = "status"
                            }
                        )
                        "listaCaronas" -> ListaCaronasScreen(
                            nomeLogado = nomeLogado,
                            aoClicarEmSolicitar = { carona ->
                                caronaSelecionada = carona
                                telaAtual = "detalhes"
                            },
                            aoClicarVoltar = {
                                veiculoLogado = ""
                                nomeLogado = ""
                                emailLogado = ""
                                telaAtual = "login"
                            },
                            aoClicarPerfil = {
                                telaAtual = "perfil"
                            }
                        )
                        "detalhes" -> DetalhesScreen(
                            caronaInfo = caronaSelecionada,
                            aoConfirmarCarona = {
                                if (caronaSelecionada != null) {
                                    BancoDeDados.fazerSolicitacao(caronaSelecionada!!, nomeLogado)
                                }
                                telaAtual = "listaCaronas"
                            },
                            aoClicarVoltar = { telaAtual = "listaCaronas" }
                        )
                        "status" -> MinhasSolicitacoesScreen(
                            isMotorista = veiculoLogado.isNotEmpty(),
                            nomeMotoristaLogado = nomeLogado,
                            aoClicarPerfil = { telaAtual = "perfil" },
                            aoClicarVoltar = {
                                veiculoLogado = ""
                                nomeLogado = ""
                                emailLogado = ""
                                telaAtual = "login"
                            },
                            aoClicarNovoEvento = {
                                telaAtual = "criarEvento"
                            }
                        )
                        "perfil" -> PerfilScreen(
                            nome = nomeLogado,
                            email = emailLogado,
                            veiculo = veiculoLogado,
                            placa = placaLogada,
                            aoClicarSair = {
                                veiculoLogado = ""
                                nomeLogado = ""
                                emailLogado = ""
                                telaAtual = "login"
                            },
                            aoClicarVoltar = {
                                telaAtual = if (veiculoLogado.isNotEmpty()) "status" else "listaCaronas"
                            },
                            aoClicarExcluirConta = {
                                BancoDeDados.excluirUsuario(emailLogado)
                                veiculoLogado = ""
                                nomeLogado = ""
                                emailLogado = ""
                                telaAtual = "login"
                            }
                        )
                    }
                }
            }
        }
    }
}