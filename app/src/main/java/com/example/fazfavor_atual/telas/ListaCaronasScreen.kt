package com.example.fazfavor_atual.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fazfavor_atual.BancoDeDados
import com.example.fazfavor_atual.Carona
import com.example.fazfavor_atual.ui.theme.*

@Composable
fun ListaCaronasScreen(nomeLogado: String, aoClicarEmSolicitar: (Carona) -> Unit, aoClicarVoltar: () -> Unit, aoClicarPerfil: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Caronas Disponíveis", color = AzulPrincipal, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            OutlinedButton(onClick = aoClicarVoltar, shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp), modifier = Modifier.height(36.dp)) {
                Text("🚪 Sair", color = VermelhoErro, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (BancoDeDados.caronas.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Nenhuma carona disponível no momento.", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(BancoDeDados.caronas) { carona ->
                    CartaoCaronaDisponivel(carona, nomeLogado, aoClicarEmSolicitar)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = aoClicarPerfil, colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal), modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(8.dp)) {
            Text("Ver Meu Perfil")
        }
    }
}

@Composable
fun CartaoCaronaDisponivel(carona: Carona, nomeLogado: String, aoClicarEmSolicitar: (Carona) -> Unit) {
    val pedidosDaCarona = BancoDeDados.todosOsPedidos.filter { it.caronaId == carona.id }
    val meuPedido = pedidosDaCarona.find { it.passageiro == nomeLogado }

    val totalVagas = carona.vagas.toIntOrNull() ?: 0
    val qtdAceitos = pedidosDaCarona.count { it.status.lowercase().contains("aceito") }
    val vagasRestantes = totalVagas - qtdAceitos

    val partes = carona.origem.split(" - ", limit = 2)
    val eventoNome = if (partes.size > 1) partes[0] else "Evento"
    val origemReal = if (partes.size > 1) partes[1] else carona.origem

    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Evento: $eventoNome", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulPrincipal)
                Spacer(modifier = Modifier.height(4.dp))
                Text("De: $origemReal", fontSize = 14.sp, color = Color.DarkGray)
                Text("Para: ${carona.destino}", fontSize = 14.sp, color = Color.DarkGray)
                Text("Motorista: ${carona.motorista}", fontSize = 14.sp, color = AzulPrincipal, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Vagas Livres: $vagasRestantes", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if(vagasRestantes <= 0) VermelhoErro else VerdeBotao)
            }

            if (meuPedido != null) {
                val statusLimpo = meuPedido.status.trim().lowercase()
                val corStatus = when {
                    statusLimpo.contains("aceito") -> VerdeBotao
                    statusLimpo.contains("recusado") -> VermelhoErro
                    else -> AmareloAviso
                }
                val textoComEmoji = when {
                    statusLimpo.contains("aceito") -> "Aceito ✅"
                    statusLimpo.contains("recusado") -> "Recusado ❌"
                    else -> "Pendente ⏳"
                }
                Surface(color = corStatus.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                    Text(textoComEmoji, color = corStatus, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                }
            } else {
                if (vagasRestantes > 0) {
                    // 🆕 INÍCIO DA ALTERAÇÃO: O botão agora chama 'aoClicarEmSolicitar' para abrir a tela de Detalhes
                    Button(onClick = { aoClicarEmSolicitar(carona) }, colors = ButtonDefaults.buttonColors(containerColor = VerdeBotao), shape = RoundedCornerShape(8.dp)) {
                        Text("Solicitar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    // 🆕 FIM DA ALTERAÇÃO
                } else {
                    Surface(color = Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp)) {
                        Text("Esgotado", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                    }
                }
            }
        }
    }
}