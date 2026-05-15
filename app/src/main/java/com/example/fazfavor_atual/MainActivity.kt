package com.example.fazfavor_atual

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.fazfavor_atual.telas.*
import com.example.fazfavor_atual.ui.theme.FazFavor_AtualTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FazFavor_AtualTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    var telaAtual by remember { mutableStateOf("login") }
                    var erroDeLogin by remember { mutableStateOf("") }
                    var erroDeCadastro by remember { mutableStateOf("") }

                    var nomeLogado by remember { mutableStateOf("") }
                    var emailLogado by remember { mutableStateOf("") }
                    var veiculoLogado by remember { mutableStateOf("") }
                    var placaLogada by remember { mutableStateOf("") }
                    var caronaSelecionada by remember { mutableStateOf<Carona?>(null) }

                    when (telaAtual) {
                        "login" -> LoginScreen(
                            aoClicarEntrar = { email, senha ->
                                if (email.isBlank() || senha.isBlank()) {
                                    erroDeLogin = "Escreva o e-mail e a senha!"
                                } else {
                                    val usuario = BancoDeDados.fazerLogin(email, senha)
                                    if (usuario != null) {
                                        nomeLogado = usuario.nome
                                        emailLogado = usuario.email
                                        veiculoLogado = usuario.veiculo
                                        placaLogada = usuario.placa
                                        erroDeLogin = ""
                                        telaAtual = "listaCaronas"
                                    } else {
                                        erroDeLogin = "E-mail ou senha errados!"
                                    }
                                }
                            },
                            aoClicarCriarConta = {
                                erroDeLogin = ""
                                erroDeCadastro = ""
                                telaAtual = "cadastro"
                            },
                            mensagemErro = erroDeLogin
                        )
                        "cadastro" -> CadastroScreen(
                            aoConcluirCadastro = { nome, cpf, email, senha, veiculo, placa, vagas ->
                                if (nome.isBlank() || cpf.isBlank() || email.isBlank() || senha.isBlank()) {
                                    erroDeCadastro = "Preencha todos os campos obrigatórios!"
                                } else if (BancoDeDados.cpfJaCadastrado(cpf)) {
                                    erroDeCadastro = "Usuário já existe (CPF já cadastrado)!"
                                } else {
                                    BancoDeDados.cadastrarUsuario(nome, cpf, email, senha, veiculo, placa, vagas)
                                    erroDeCadastro = ""
                                    telaAtual = "login"
                                }
                            },
                            // --- O CÉREBRO AGORA OUVE O BOTÃO FECHAR! ---
                            aoClicarFechar = {
                                erroDeCadastro = ""
                                telaAtual = "login" // Retorna para a tela 1
                            },
                            mensagemErro = erroDeCadastro
                        )
                        "listaCaronas" -> ListaCaronasScreen(
                            aoClicarEmSolicitar = { carona ->
                                caronaSelecionada = carona
                                telaAtual = "detalhes"
                            },
                            aoClicarVoltar = { telaAtual = "cadastro" }
                        )
                        "detalhes" -> DetalhesScreen(
                            caronaInfo = caronaSelecionada,
                            aoConfirmarCarona = {
                                if (caronaSelecionada != null) {
                                    BancoDeDados.fazerSolicitacao(caronaSelecionada!!)
                                }
                                telaAtual = "status"
                            },
                            aoClicarVoltar = { telaAtual = "listaCaronas" }
                        )
                        "status" -> MinhasSolicitacoesScreen(
                            aoClicarPerfil = { telaAtual = "perfil" },
                            aoClicarVoltar = { telaAtual = "detalhes" }
                        )
                        "perfil" -> PerfilScreen(
                            nome = nomeLogado,
                            email = emailLogado,
                            veiculo = veiculoLogado,
                            placa = placaLogada,
                            aoClicarSair = { telaAtual = "login" },
                            aoClicarVoltar = { telaAtual = "status" }
                        )
                    }
                }
            }
        }
    }
}