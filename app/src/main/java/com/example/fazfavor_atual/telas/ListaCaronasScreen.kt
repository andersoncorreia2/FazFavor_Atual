package com.example.fazfavor_atual.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.fazfavor_atual.BancoDeDados
import com.example.fazfavor_atual.Carona
import com.example.fazfavor_atual.ui.theme.*

@Composable
fun ListaCaronasScreen(aoClicarEmSolicitar: (Carona) -> Unit, aoClicarVoltar: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)) {

        // NOVO: Cabeçalho com botão de voltar para a tela de Cadastro
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = aoClicarVoltar) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = AzulPrincipal)
            }
            Text("FazFavor", fontSize = 24.sp, color = AzulPrincipal, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Caronas Disponíveis", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(BancoDeDados.caronas) { carona ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("📍 Origem: ${carona.origem}")
                            Text("📍 Destino: ${carona.destino}")
                            Text("👤 Motorista: ${carona.motorista}", color = AzulPrincipal)
                        }
                        Button(onClick = { aoClicarEmSolicitar(carona) }, colors = ButtonDefaults.buttonColors(containerColor = VerdeBotao)) {
                            Text("Solicitar")
                        }
                    }
                }
            }
        }
    }
}