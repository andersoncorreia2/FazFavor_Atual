package com.example.fazfavor_atual

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import java.net.URL
import kotlin.concurrent.thread
import java.net.HttpURLConnection
import java.io.OutputStreamWriter
import org.json.JSONArray
import org.json.JSONObject

data class Carona(val id: Int = 0, val origem: String, val destino: String, val horario: String, val vagas: String, val motorista: String)
data class Usuario(val nome: String, val cpf: String, val email: String, val telefone: String, val veiculo: String = "", val placa: String = "", val senha: String = "")
data class Solicitacao(val carona: Carona, val status: String)

object BancoDeDados {
    var caronas = mutableStateListOf<Carona>()

    var nomesPassageiros = mutableStateMapOf<Int, String>()
    var statusDasCaronas = mutableStateMapOf<Int, String>()
    var meusPedidos = mutableStateMapOf<Int, String>()
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
                    val caronaDoCofre = Carona(
                        id = item.getInt("id"),
                        origem = item.getString("origem"),
                        destino = item.getString("destino"),
                        horario = item.getString("horario"),
                        vagas = item.getString("vagas"),
                        motorista = item.getString("motorista")
                    )
                    caronas.add(caronaDoCofre)
                }
                println("✨ TELA ATUALIZADA COM SUCESSO A PARTIR DO COFRE NUVEM!")
            } catch (erro: Exception) {
                println("❌ ERRO NA VIAGEM DE BUSCA NUVEM: ${erro.message}")
            }
        }
    }

    fun enviarCaronaParaServidor(origem: String, destino: String, horario: String, vagas: String, motorista: String) {
        thread {
            try {
                val enderecoMagico = URL("https://fazfavor-backend.onrender.com/caronas")
                val conexao = enderecoMagico.openConnection() as HttpURLConnection
                conexao.requestMethod = "POST"
                conexao.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                conexao.doOutput = true

                val pacoteJson = """
                {
                "origem": "$origem",
                "destino": "$destino",
                "horario": "$horario",
                "vagas": "$vagas",
                "motorista": "$motorista"
                }
                """.trimIndent()

                val escritor = OutputStreamWriter(conexao.outputStream)
                escritor.write(pacoteJson)
                escritor.flush()

                println("📦 PACOTE ENVIADO PARA NUVEM! Servidor respondeu: ${conexao.responseCode}")
                buscarCaronasDoServidor()
            } catch (erro: Exception) {
                println("❌ ERRO NA ENTREGA DA CARONA NUVEM: ${erro.message}")
            }
        }
    }

    fun enviarPedidoDeCarona(caronaId: Int, nomePassageiro: String) {
        thread {
            try {
                val enderecoMagico = URL("https://fazfavor-backend.onrender.com/solicitacoes")
                val conexao = enderecoMagico.openConnection() as HttpURLConnection
                conexao.requestMethod = "POST"
                conexao.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                conexao.doOutput = true

                val pacoteJson = """
                {
                "carona_id": $caronaId,
                "passageiro": "$nomePassageiro"
                }
                """.trimIndent()

                val escritor = OutputStreamWriter(conexao.outputStream)
                escritor.write(pacoteJson)
                escritor.flush()

                println("🙋‍♂️ PEDIDO ENVIADO NUVEM! O Servidor respondeu: ${conexao.responseCode}")
                buscarCaronasDoServidor()
            } catch (erro: Exception) {
                println("❌ ERRO NO PEDIDO NUVEM: ${erro.message}")
            }
        }
    }

    fun responderPedidoMotorista(solicitacaoId: Int, statusDecidido: String) {
        thread {
            try {
                val enderecoMagico = URL("https://fazfavor-backend.onrender.com/solicitacoes/$solicitacaoId")
                val conexao = enderecoMagico.openConnection() as HttpURLConnection
                conexao.requestMethod = "PUT"
                conexao.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                conexao.doOutput = true

                val pacoteJson = """
                {
                "status": "$statusDecidido"
                }
                """.trimIndent()

                val escritor = OutputStreamWriter(conexao.outputStream)
                escritor.write(pacoteJson)
                escritor.flush()

                println("PROCESSO ATUALIZADO PARA '$statusDecidido' NA NUVEM! Servidor respondeu: ${conexao.responseCode}")
                buscarCaronasDoServidor()
            } catch (erro: Exception) {
                println("❌ ERRO AO RESPONDER NA NUVEM: ${erro.message}")
            }
        }
    }

    fun excluirCaronaDoServidor(caronaId: Int) {
        caronas.removeAll { it.id == caronaId }
        temEventoAtivo = false
        println("✨ EVENTO REMOVIDO DA TELA COM SUCESSO!")

        thread {
            try {
                val enderecoMagico = URL("https://fazfavor-backend.onrender.com/caronas/$caronaId")
                val conexao = enderecoMagico.openConnection() as HttpURLConnection
                conexao.requestMethod = "DELETE"
                println("🗑️ ORDEM DE CANCELAMENTO ENVIADA NUVEM! Servidor respondeu: ${conexao.responseCode}")
                buscarCaronasDoServidor()
            } catch (erro: Exception) {
                println("❌ ERRO AO AVISAR O SERVIDOR NUVEM: ${erro.message}")
            }
        }
    }

    fun fazerSolicitacao(carona: Carona, nomePassageiro: String) {
        println("🚗 Enviando solicitação oficial para o banco... ID: ${carona.id}")
        statusDasCaronas[carona.id] = "Pendente"
        nomesPassageiros[carona.id] = nomePassageiro
        meusPedidos[carona.id] = "Pendente"
        enviarPedidoDeCarona(carona.id, nomePassageiro)
    }

    // ==========================================
    // 🌐 NOVAS FUNÇÕES DE AUTENTICAÇÃO NA NUVEM
    // ==========================================

    fun fazerLoginNuvem(emailRecebido: String, senhaRecebida: String, aoTerminar: (Usuario?, String) -> Unit) {
        thread {
            try {
                val url = URL("https://fazfavor-backend.onrender.com/login")
                val conexao = url.openConnection() as HttpURLConnection
                conexao.requestMethod = "POST"
                conexao.connectTimeout = 15000
                conexao.readTimeout = 60000
                conexao.setRequestProperty("Content-Type", "application/json; charset=utf-8")

                val json = """{"email": "$emailRecebido", "senha": "$senhaRecebida"}"""
                val escritor = OutputStreamWriter(conexao.outputStream)
                escritor.write(json)
                escritor.flush()

                if (conexao.responseCode == 200) {
                    val resposta = conexao.inputStream.bufferedReader().readText()
                    val jsonResponse = JSONObject(resposta)
                    val usuario = Usuario(
                        nome = jsonResponse.getString("nome"),
                        cpf = jsonResponse.getString("cpf"),
                        email = jsonResponse.getString("email"),
                        telefone = jsonResponse.getString("telefone"),
                        veiculo = jsonResponse.optString("veiculo", ""),
                        placa = jsonResponse.optString("placa", ""),
                        senha = senhaRecebida
                    )
                    aoTerminar(usuario, "") // Sucesso!
                } else {
                    aoTerminar(null, "Acesso Negado: E-mail ou senha incorretos.")
                }
            } catch (erro: Exception) {
                aoTerminar(null, "Falha de conexão: Verifique sua internet ou o servidor.")
            }
        }
    }

    fun cadastrarUsuarioNuvem(nome: String, cpf: String, telefone: String, email: String, senha: String, veiculo: String, placa: String, vagas: String, aoTerminar: (Boolean, String) -> Unit) {
        thread {
            try {
                val url = URL("https://fazfavor-backend.onrender.com/usuarios")
                val conexao = url.openConnection() as HttpURLConnection
                conexao.requestMethod = "POST"
                conexao.connectTimeout = 15000 // Aguarda 15s para achar a internet
                conexao.readTimeout = 60000    // Aguarda 60s o Render "acordar"
                conexao.setRequestProperty("Content-Type", "application/json; charset=utf-8")

                val json = """
                {
                    "nome": "$nome",
                    "cpf": "$cpf",
                    "telefone": "$telefone",
                    "email": "$email",
                    "senha": "$senha",
                    "veiculo": "$veiculo",
                    "placa": "$placa"
                }
                """.trimIndent()

                val escritor = OutputStreamWriter(conexao.outputStream)
                escritor.write(json)
                escritor.flush()

                when (conexao.responseCode) {
                    201 -> aoTerminar(true, "") // Sucesso
                    400 -> aoTerminar(false, "Este CPF ou E-mail já possui cadastro!") // Barrado pelo servidor
                    else -> aoTerminar(false, "Erro desconhecido ao cadastrar.")
                }
            } catch (erro: Exception) {
                aoTerminar(false, "Falha de conexão com o servidor.")
            }
        }
    }

    // 🕵️ NOVO: O ESPIÃO DE CPF EM TEMPO REAL
    fun verificarCpfExistente(cpfParaChecar: String, aoDescobrir: (Boolean) -> Unit) {
        thread {
            try {
                val url = URL("https://fazfavor-backend.onrender.com/verificar_cpf/$cpfParaChecar")
                val conexao = url.openConnection() as HttpURLConnection
                conexao.requestMethod = "GET"

                if (conexao.responseCode == 200) {
                    val resposta = conexao.inputStream.bufferedReader().readText()
                    val jsonResponse = JSONObject(resposta)
                    val cpfJaExiste = jsonResponse.getBoolean("existe")
                    aoDescobrir(cpfJaExiste) // Avisa a tela de Cadastro se existe ou não
                }
            } catch (erro: Exception) {
                println("❌ Erro ao checar CPF na nuvem: ${erro.message}")
            }
        }
    }

    fun excluirUsuario(emailParaExcluir: String) {
        println("🗑️ Pedido de exclusão feito para: $emailParaExcluir")
    }

    // ==========================================
    // 📡 O RADAR (Sincronização em Tempo Real)
    // ==========================================

    fun buscarSolicitacoesDoServidor() {
        try {
            val enderecoMagico = "https://fazfavor-backend.onrender.com/solicitacoes"
            val resposta = URL(enderecoMagico).readText()
            val jsonArray = JSONArray(resposta)

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val idSolicitacao = item.getInt("id")
                val caronaId = item.getInt("carona_id")
                val passageiro = item.getString("passageiro")
                val status = item.getString("status")

                // Atualiza as listas do aplicativo com os dados que vieram da nuvem
                nomesPassageiros[caronaId] = passageiro
                statusDasCaronas[caronaId] = status
                meusPedidos[caronaId] = status
            }
        } catch (erro: Exception) {
            println("❌ O Radar falhou em buscar solicitações: ${erro.message}")
        }
    }

    fun ligarRadar() {
        thread {
            while (true) {
                // A cada 5 segundos, o app busca caronas e pedidos novos!
                buscarCaronasDoServidor()
                buscarSolicitacoesDoServidor()
                Thread.sleep(5000)
            }
        }
    }
}