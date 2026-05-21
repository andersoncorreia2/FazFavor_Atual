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
fun ListaCaronasScreen(
    nomeLogado: String,
    aoClicarEmSolicitar: (Carona) -> Unit,
    aoClicarVoltar: () -> Unit,
    aoClicarPerfil: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Caronas Disponíveis", color = AzulPrincipal, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            OutlinedButton(
                onClick = aoClicarVoltar, shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp), modifier = Modifier.height(36.dp)
            ) { Text("🚪 Sair", color = VermelhoErro, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (BancoDeDados.caronas.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Nenhuma carona disponível no momento.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(BancoDeDados.caronas) { carona ->
                    CartaoCaronaDisponivel(carona, nomeLogado, aoClicarEmSolicitar)
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
fun CartaoCaronaDisponivel(carona: Carona, nomeLogado: String, aoClicarEmSolicitar: (Carona) -> Unit) {

    val passageiroDestaCarona = BancoDeDados.nomesPassageiros[carona.id]
    val statusDaCarona = BancoDeDados.statusDasCaronas[carona.id]

    val partes = carona.origem.split(" - ", limit = 2)
    val eventoNome = if (partes.size > 1) partes[0] else "Evento"
    val origemReal = if (partes.size > 1) partes[1] else carona.origem

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {

                Text("Evento: $eventoNome", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulPrincipal)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Origem/Saída: $origemReal", fontSize = 14.sp, color = Color.DarkGray)
                Text("Destino: ${carona.destino}", fontSize = 14.sp, color = Color.DarkGray)
                Text("Motorista: ${carona.motorista}", fontSize = 14.sp, color = AzulPrincipal, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(4.dp))
                Text("Vagas: ${carona.vagas}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if(carona.vagas == "0") VermelhoErro else VerdeBotao)
            }

            if (passageiroDestaCarona == nomeLogado && statusDaCarona != null) {

                val statusLimpo = statusDaCarona.trim().lowercase()

                // 🛡️ BLINDAGEM SUPREMA: Procura a palavra no meio do texto, ignorando emojis!
                val corStatus = when {
                    statusLimpo.contains("aceito") -> VerdeBotao
                    statusLimpo.contains("recusado") -> VermelhoErro
                    statusLimpo.contains("pendente") -> AmareloAviso
                    else -> AmareloAviso
                }

                val textoParaExibir = statusDaCarona.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

                Surface(color = corStatus.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                    Text(textoParaExibir, color = corStatus, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                }
            } else {
                if ((carona.vagas.toIntOrNull() ?: 0) > 0) {
                    Button(
                        onClick = { aoClicarEmSolicitar(carona) },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeBotao), shape = RoundedCornerShape(8.dp)
                    ) { Text("Solicitar", color = Color.White, fontWeight = FontWeight.Bold) }
                } else {
                    Text("Esgotado", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}