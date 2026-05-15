package com.example.fazfavor_atual.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fazfavor_atual.Carona
import com.example.fazfavor_atual.ui.theme.*

@Composable
fun DetalhesScreen(caronaInfo: Carona?, aoConfirmarCarona: () -> Unit, aoClicarVoltar: () -> Unit) {

    if (caronaInfo == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Erro ao carregar detalhes!")
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { aoClicarVoltar() }, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = AzulPrincipal)
            }
            Text("Detalhes da Carona", color = AzulPrincipal, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        ItemDetalhe(Icons.Default.LocationOn, AzulPrincipal, "Origem", caronaInfo.origem)
        ItemDetalhe(Icons.Default.LocationOn, VerdeBotao, "Destino", caronaInfo.destino)
        ItemDetalhe(Icons.Default.Info, Color.Gray, "Horário", caronaInfo.horario)
        ItemDetalhe(Icons.Default.Person, Color.Gray, "Vagas disponíveis", "${caronaInfo.vagas} vagas")
        ItemDetalhe(Icons.Default.AccountCircle, Color.Gray, "Motorista", caronaInfo.motorista)

        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            Text("$", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.width(32.dp))
            Column {
                Text("Valor", color = Color.Gray, fontSize = 12.sp)
                Text("Gratuito", color = VerdeBotao, fontWeight = FontWeight.Bold)
            }
        }

        ItemDetalhe(Icons.Default.Info, Color.Gray, "Informações Adicionais", "Saindo após o culto. Ser pontual.")

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = aoConfirmarCarona,
            colors = ButtonDefaults.buttonColors(containerColor = VerdeBotao),
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Solicitar Carona", fontSize = 16.sp)
        }
    }
}

@Composable
fun ItemDetalhe(icone: androidx.compose.ui.graphics.vector.ImageVector, corIcone: Color, titulo: String, valor: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
        Icon(icone, contentDescription = null, tint = corIcone, modifier = Modifier.width(32.dp))
        Column {
            Text(titulo, color = Color.Gray, fontSize = 12.sp)
            Text(valor, fontSize = 14.sp)
        }
    }
}