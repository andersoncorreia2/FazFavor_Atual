package com.example.fazfavor_atual.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fazfavor_atual.ui.theme.*

@Composable
fun PerfilScreen(nome: String, email: String, veiculo: String, placa: String, aoClicarSair: () -> Unit, aoClicarVoltar: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

        // --- BOTÃO DE VOLTAR CONSERTADO ---
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = aoClicarVoltar, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = AzulPrincipal)
            }
            Text("Perfil", color = AzulPrincipal, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Avatar Circular
        Box(modifier = Modifier.size(100.dp).background(Color(0xFFE3F2FD), CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(50.dp), tint = AzulPrincipal)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(nome.ifEmpty { "Maria Santos" }, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(if (email.isEmpty()) "maria.santos@email.com" else email, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        // Caixinhas de Informação do Perfil
        CaixaInfo("Membro desde", "Janeiro de 2026")
        CaixaInfo("Caronas realizadas", "12 caronas")
        CaixaInfo("Tipo de conta", if(veiculo.isEmpty()) "Passageiro" else "Motorista ($veiculo)")

        Spacer(modifier = Modifier.weight(1f))

        // Botão de Sair Igual ao Desenho (Branco com contorno)
        OutlinedButton(
            onClick = aoClicarSair,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sair da conta")
        }
    }
}

// Pecinha de Lego para as Caixinhas do Perfil
@Composable
fun CaixaInfo(titulo: String, subtitulo: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)).padding(16.dp)) {
        Column {
            Text(titulo, fontSize = 12.sp, color = Color.Gray)
            Text(subtitulo, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}