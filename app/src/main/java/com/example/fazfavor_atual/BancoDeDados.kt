package com.example.fazfavor_atual

import androidx.compose.runtime.mutableStateListOf

// 1. Adicionamos o CPF na ficha do Usuário
data class Usuario(val nome: String, val cpf: String, val email: String, val senha: String, val veiculo: String, val placa: String)
data class Carona(val origem: String, val destino: String, val horario: String, val vagas: String, val motorista: String)
data class Solicitacao(val carona: Carona, val status: String)

object BancoDeDados {
    val usuarios = mutableStateListOf<Usuario>()
    val caronas = mutableStateListOf<Carona>(
        Carona("Centro da Cidade", "Igreja Batista Central", "09:00", "3", "João Silva"),
        Carona("Bairro Jardim", "Culto de Quarta", "19:30", "2", "Maria Oliveira")
    )
    val minhasSolicitacoes = mutableStateListOf<Solicitacao>()

    // 2. Função nova: Verifica se o CPF já existe na nossa lista
    fun cpfJaCadastrado(cpfProcurado: String): Boolean {
        return usuarios.any { it.cpf == cpfProcurado }
    }

    // 3. Atualizamos a função de cadastrar para receber o CPF
    fun cadastrarUsuario(nome: String, cpf: String, email: String, senha: String, veiculo: String, placa: String, vagas: String) {
        usuarios.add(Usuario(nome, cpf, email, senha, veiculo, placa))
        if (veiculo.isNotBlank()) {
            caronas.add(Carona("Casa do(a) $nome", "Igreja", "A combinar", vagas, nome))
        }
    }

    fun fazerLogin(emailDigitado: String, senhaDigitada: String): Usuario? {
        return usuarios.find { it.email == emailDigitado && it.senha == senhaDigitada }
    }

    fun fazerSolicitacao(caronaPedida: Carona) {
        val novoPedido = Solicitacao(caronaPedida, "Pendente")
        minhasSolicitacoes.add(novoPedido)
    }
}