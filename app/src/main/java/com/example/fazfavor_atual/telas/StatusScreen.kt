package com.example.fazfavor_atual.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fazfavor_atual.BancoDeDados
import com.example.fazfavor_atual.ui.theme.*

@Composable
fun MinhasSolicitacoesScreen(
    isMotorista: Boolean,
    nomeMotoristaLogado: String,
    aoClicarPerfil: () -> Unit,
    aoClicarVoltar: () -> Unit,
    aoClicarNovoEvento: () -> Unit
) {
    val minhasCaronas = BancoDeDados.caronas.filter { it.motorista == nomeMotoristaLogado }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Minhas Solicitações", color = AzulPrincipal, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            OutlinedButton(
                onClick = aoClicarVoltar, shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp), modifier = Modifier.height(36.dp)
            ) { Text("🚪 Sair", color = VermelhoErro, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isMotorista) {
            Button(
                onClick = aoClicarNovoEvento, colors = ButtonDefaults.buttonColors(containerColor = VerdeBotao),
                modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(8.dp)
            ) { Text("➕ Criar Novo Evento/Carona", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (minhasCaronas.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Nenhum evento criado por você no momento.", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(minhasCaronas) { carona ->
                    val nomeDoPassageiro = BancoDeDados.nomesPassageiros[carona.id]
                    val statusAtual = BancoDeDados.statusDasCaronas[carona.id] ?: if (nomeDoPassageiro != null) "Pendente" else "Livre"

                    val statusFormatado = statusAtual.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    val statusLimpo = statusAtual.trim().lowercase()

                    val cor = when {
                        statusLimpo.contains("aceito") -> VerdeBotao
                        statusLimpo.contains("recusado") -> Color(0xFFD32F2F)
                        statusLimpo.contains("pendente") -> AmareloAviso
                        else -> Color.Transparent
                    }

                    CartaoStatus(
                        idSolicitacao = carona.id, origemCrua = carona.origem, destino = carona.destino,
                        passageiro = nomeDoPassageiro, vagas = carona.vagas,
                        statusMemoria = statusFormatado, corStatus = cor, isMotorista = isMotorista
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = aoClicarPerfil, colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
            modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(8.dp)
        ) { Text("Ver Meu Perfil") }
    }
}

@Composable
fun CartaoStatus(idSolicitacao: Int, origemCrua: String, destino: String, passageiro: String?, vagas: String, statusMemoria: String, corStatus: Color, isMotorista: Boolean) {

    val partes = origemCrua.split(" - ", limit = 2)
    val eventoNome = if (partes.size > 1) partes[0] else "Evento"
    val origemReal = if (partes.size > 1) partes[1] else origemCrua

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Evento: $eventoNome", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulPrincipal)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Origem/Saída: $origemReal", fontSize = 14.sp, color = Color.DarkGray)
                    Text("Destino: $destino", fontSize = 14.sp, color = Color.DarkGray)

                    Spacer(modifier = Modifier.height(4.dp))
                    if (isMotorista) {
                        if (passageiro != null) {
                            Text("Passageiro: $passageiro", fontSize = 14.sp, color = AzulPrincipal, fontWeight = FontWeight.Bold)
                        } else {
                            Text("Aguardando passageiros...", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    Text("Vagas: $vagas", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if(vagas == "0") VermelhoErro else VerdeBotao)
                }

                if (statusMemoria != "Livre" && statusMemoria != "Transparente") {
                    val textColor = if (statusMemoria.lowercase().contains("pendente")) Color.Black else corStatus

                    // 🎨 MAGIA VISUAL: Coloca os emojis na TELA sem mandar pro banco de dados
                    val textoComEmoji = when {
                        statusMemoria.lowercase().contains("aceito") -> "Aceito ✅"
                        statusMemoria.lowercase().contains("recusado") -> "Recusado ❌"
                        statusMemoria.lowercase().contains("pendente") -> "Pendente ⏳"
                        else -> statusMemoria
                    }

                    Surface(color = corStatus.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                        Text(textoComEmoji, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isMotorista) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    if (statusMemoria.lowercase().contains("pendente") && passageiro != null) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    BancoDeDados.responderPedidoMotorista(idSolicitacao, "Aceito")
                                    BancoDeDados.statusDasCaronas[idSolicitacao] = "Aceito"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VerdeBotao), modifier = Modifier.weight(1f).height(36.dp), shape = RoundedCornerShape(8.dp)
                            ) { Text("Aceitar", fontSize = 12.sp, color = Color.White) }

                            Button(
                                onClick = {
                                    BancoDeDados.responderPedidoMotorista(idSolicitacao, "Recusado")
                                    BancoDeDados.statusDasCaronas[idSolicitacao] = "Recusado"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), modifier = Modifier.weight(1f).height(36.dp), shape = RoundedCornerShape(8.dp)
                            ) { Text("Recusar", fontSize = 12.sp, color = Color.White) }
                        }
                    }

                    OutlinedButton(
                        onClick = { BancoDeDados.excluirCaronaDoServidor(idSolicitacao) },
                        modifier = Modifier.fillMaxWidth().height(36.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = VermelhoErro)
                    ) { Text("🗑️ Excluir Evento", fontWeight = FontWeight.Bold, fontSize = 12.sp) }

                    // ↩️ NOVO BOTÃO: Desfazer ação (Só aparece se já foi aceito ou recusado)
                    if (!statusMemoria.lowercase().contains("pendente") && passageiro != null) {
                        Button(
                            onClick = {
                                BancoDeDados.responderPedidoMotorista(idSolicitacao, "Pendente")
                                BancoDeDados.statusDasCaronas[idSolicitacao] = "Pendente"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AmareloAviso),
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("↩️ Desfazer Decisão (Voltar p/ Pendente)", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}