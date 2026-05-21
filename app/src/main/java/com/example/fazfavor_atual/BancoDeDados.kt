package com.example.fazfavor_atual

import androidx.compose.runtime.mutableStateListOf
import java.net.URL
import kotlin.concurrent.thread
import java.net.HttpURLConnection
import java.io.OutputStreamWriter
import org.json.JSONArray
import org.json.JSONObject

data class Carona(val id: Int = 0, val origem: String, val destino: String, val horario: String, val vagas: String, val motorista: String)
data class Usuario(val nome: String, val cpf: String, val email: String, val telefone: String, val veiculo: String = "", val placa: String = "", val senha: String = "")
data class Pedido(val idReal: Int, val caronaId: Int, val passageiro: String, val status: String)

object BancoDeDados {
    var caronas = mutableStateListOf<Carona>()
    var todosOsPedidos = mutableStateListOf<Pedido>()
    var temEventoAtivo: Boolean = false

    fun buscarCaronasDoServidor() {
        thread {
            try {
                val enderecoMagico = "https://fazfavor-backend.onrender.com/caronas"
                val resposta = URL(enderecoMagico).readText()
                val jsonArray = JSONArray(resposta)

                caronas.clear()
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    caronas.add(Carona(
                        id = item.getInt("id"), origem = item.getString("origem"),
                        destino = item.getString("destino"), horario = item.getString("horario"),
                        vagas = item.getString("vagas"), motorista = item.getString("motorista")
                    ))
                }
            } catch (erro: Exception) { println("❌ ERRO CARONAS NUVEM: ${erro.message}") }
        }
    }

    fun enviarCaronaParaServidor(origem: String, destino: String, horario: String, vagas: String, motorista: String) {
        thread {
            try {
                val conexao = URL("https://fazfavor-backend.onrender.com/caronas").openConnection() as HttpURLConnection
                conexao.requestMethod = "POST"
                conexao.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                conexao.doOutput = true
                val json = """{"origem": "$origem", "destino": "$destino", "horario": "$horario", "vagas": "$vagas", "motorista": "$motorista"}"""
                val escritor = OutputStreamWriter(conexao.outputStream)
                escritor.write(json)
                escritor.flush()
                val code = conexao.responseCode
                buscarCaronasDoServidor()
            } catch (erro: Exception) {}
        }
    }

    fun fazerSolicitacao(carona: Carona, nomePassageiro: String) {
        todosOsPedidos.add(Pedido(idReal = 0, caronaId = carona.id, passageiro = nomePassageiro, status = "Pendente"))
        thread {
            try {
                val conexao = URL("https://fazfavor-backend.onrender.com/solicitacoes").openConnection() as HttpURLConnection
                conexao.requestMethod = "POST"
                conexao.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                conexao.doOutput = true
                val json = """{"carona_id": ${carona.id}, "passageiro": "$nomePassageiro"}"""
                val escritor = OutputStreamWriter(conexao.outputStream)
                escritor.write(json)
                escritor.flush()
                val code = conexao.responseCode
                buscarSolicitacoesDoServidor()
            } catch (erro: Exception) {}
        }
    }

    fun responderPedidoMotorista(pedidoIdReal: Int, statusDecidido: String) {
        val index = todosOsPedidos.indexOfFirst { it.idReal == pedidoIdReal }
        if (index != -1) {
            val antigo = todosOsPedidos[index]
            todosOsPedidos[index] = antigo.copy(status = statusDecidido)
        }

        thread {
            try {
                val conexao = URL("https://fazfavor-backend.onrender.com/solicitacoes/$pedidoIdReal").openConnection() as HttpURLConnection
                conexao.requestMethod = "PUT"
                conexao.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                conexao.doOutput = true
                val json = """{"status": "$statusDecidido"}"""
                val escritor = OutputStreamWriter(conexao.outputStream)
                escritor.write(json)
                escritor.flush()
                val code = conexao.responseCode
                buscarSolicitacoesDoServidor()
            } catch (erro: Exception) {}
        }
    }

    fun excluirCaronaDoServidor(caronaId: Int) {
        caronas.removeAll { it.id == caronaId }
        todosOsPedidos.removeAll { it.caronaId == caronaId }
        temEventoAtivo = false
        thread {
            try {
                val conexao = URL("https://fazfavor-backend.onrender.com/caronas/$caronaId").openConnection() as HttpURLConnection
                conexao.requestMethod = "DELETE"
                val code = conexao.responseCode
                buscarCaronasDoServidor()
            } catch (erro: Exception) {}
        }
    }

    fun buscarSolicitacoesDoServidor() {
        try {
            val resposta = URL("https://fazfavor-backend.onrender.com/solicitacoes").readText()
            val jsonArray = JSONArray(resposta)

            val novaLista = mutableListOf<Pedido>()
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                novaLista.add(Pedido(
                    idReal = item.getInt("id"), caronaId = item.getInt("carona_id"),
                    passageiro = item.getString("passageiro"), status = item.getString("status")
                ))
            }
            todosOsPedidos.clear()
            todosOsPedidos.addAll(novaLista)
        } catch (erro: Exception) { println("❌ Radar falhou: ${erro.message}") }
    }

    fun ligarRadar() {
        thread {
            while (true) {
                buscarCaronasDoServidor()
                buscarSolicitacoesDoServidor()
                Thread.sleep(5000)
            }
        }
    }

    fun fazerLoginNuvem(emailRecebido: String, senhaRecebida: String, aoTerminar: (Usuario?, String) -> Unit) {
        thread {
            try {
                val conexao = URL("https://fazfavor-backend.onrender.com/login").openConnection() as HttpURLConnection
                conexao.requestMethod = "POST"
                conexao.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                val json = """{"email": "$emailRecebido", "senha": "$senhaRecebida"}"""
                val escritor = OutputStreamWriter(conexao.outputStream)
                escritor.write(json)
                escritor.flush()

                if (conexao.responseCode == 200) {
                    val res = JSONObject(conexao.inputStream.bufferedReader().readText())
                    aoTerminar(Usuario(res.getString("nome"), res.getString("cpf"), res.getString("email"), res.getString("telefone"), res.optString("veiculo", ""), res.optString("placa", ""), senhaRecebida), "")
                } else { aoTerminar(null, "Negado") }
            } catch (erro: Exception) { aoTerminar(null, "Erro") }
        }
    }

    fun cadastrarUsuarioNuvem(nome: String, cpf: String, telefone: String, email: String, senha: String, veiculo: String, placa: String, vagas: String, aoTerminar: (Boolean, String) -> Unit) {
        thread {
            try {
                val conexao = URL("https://fazfavor-backend.onrender.com/usuarios").openConnection() as HttpURLConnection
                conexao.requestMethod = "POST"
                conexao.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                val json = """{"nome": "$nome", "cpf": "$cpf", "telefone": "$telefone", "email": "$email", "senha": "$senha", "veiculo": "$veiculo", "placa": "$placa"}"""
                val escritor = OutputStreamWriter(conexao.outputStream)
                escritor.write(json)
                escritor.flush()
                when (conexao.responseCode) {
                    201 -> aoTerminar(true, "")
                    400 -> aoTerminar(false, "Já existe")
                    else -> aoTerminar(false, "Erro")
                }
            } catch (erro: Exception) { aoTerminar(false, "Falha") }
        }
    }

    fun verificarCpfExistente(cpf: String, aoDescobrir: (Boolean) -> Unit) {
        thread {
            try {
                val conexao = URL("https://fazfavor-backend.onrender.com/verificar_cpf/$cpf").openConnection() as HttpURLConnection
                conexao.requestMethod = "GET"
                if (conexao.responseCode == 200) {
                    val res = JSONObject(conexao.inputStream.bufferedReader().readText())
                    aoDescobrir(res.getBoolean("existe"))
                }
            } catch (erro: Exception) {}
        }
    }

    fun excluirUsuario(email: String) {
        thread {
            try {
                // 🛡️ TRADUTOR: Transforma o "@" e o "." em código seguro para a internet
                val emailSeguroParaInternet = java.net.URLEncoder.encode(email, "UTF-8")

                val enderecoMagico = URL("https://fazfavor-backend.onrender.com/usuarios/$emailSeguroParaInternet")
                val conexao = enderecoMagico.openConnection() as HttpURLConnection
                conexao.requestMethod = "DELETE"

                // 🔥 O GATILHO
                val codigoResposta = conexao.responseCode
                println("🗑️ ORDEM DE EXCLUSÃO ENVIADA! Servidor respondeu: $codigoResposta")

            } catch (erro: Exception) {
                println("❌ ERRO AO EXCLUIR CONTA NUVEM: ${erro.message}")
            }
        }
    }
}