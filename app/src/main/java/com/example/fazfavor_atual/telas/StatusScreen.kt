package com.example.fazfavor_atual.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fazfavor_atual.BancoDeDados
import com.example.fazfavor_atual.ui.theme.*

@Composable
fun MinhasSolicitacoesScreen(aoClicarPerfil: () -> Unit, aoClicarVoltar: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = aoClicarVoltar, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = AzulPrincipal)
            }
            Text("Minhas Solicitações", color = AzulPrincipal, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- A MÁGICA ACONTECE AQUI! ---
        if (BancoDeDados.minhasSolicitacoes.isEmpty()) {
            // Se o usuário não pediu nenhuma carona ainda, avisa na tela!
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Você ainda não pediu nenhuma carona.", color = Color.Gray)
            }
        } else {
            // Se ele pediu, mostra a lista exata do que foi solicitado!
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(BancoDeDados.minhasSolicitacoes) { pedido ->
                    // Se estiver pendente fica amarelo, se não, verde!
                    val corEtiqueta = if (pedido.status == "Pendente") AmareloAviso else VerdeBotao

                    CartaoStatus(
                        destino = pedido.carona.destino,
                        motorista = pedido.carona.motorista,
                        horario = pedido.carona.horario,
                        status = pedido.status,
                        corStatus = corEtiqueta
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = aoClicarPerfil,
            colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
            modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(8.dp)
        ) {
            Text("Ver Meu Perfil")
        }
    }
}

// O Cartão Inteligente
@Composable
fun CartaoStatus(destino: String, motorista: String, horario: String, status: String, corStatus: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(destino, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    // Agora mostra o Motorista e o Horário reais!
                    Text("Motorista: $motorista", fontSize = 12.sp, color = Color.Gray)
                    Text("Horário: $horario", fontSize = 12.sp, color = Color.Gray)
                }
            }
            Surface(color = corStatus.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                Text(status, color = corStatus, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
            }
        }
    }
}