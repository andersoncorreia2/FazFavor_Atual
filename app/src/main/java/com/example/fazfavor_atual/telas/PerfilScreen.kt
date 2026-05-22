package com.example.fazfavor_atual.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun PerfilScreen(
    nome: String,
    email: String,
    veiculo: String,
    placa: String,
    aoClicarSair: () -> Unit,
    aoClicarVoltar: () -> Unit,
    aoClicarExcluirConta: () -> Unit
) {
    // 📜 MODIFICADOR ADICIONADO: rememberScrollState() permite arrastar a tela para baixo!
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = aoClicarVoltar, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = AzulPrincipal)
            }
            Text("Perfil", color = AzulPrincipal, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.size(100.dp).background(Color(0xFFE3F2FD), CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(50.dp), tint = AzulPrincipal)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(nome.ifEmpty { "Usuário Teste" }, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(if (email.isEmpty()) "usuario@email.com" else email, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        CaixaInfo("Membro desde", "Maio de 2026")
        CaixaInfo("Caronas realizadas", "0 caronas")
        CaixaInfo("Tipo de conta", if(veiculo.isEmpty()) "Solicitante/Passageiro" else "Motorista ($veiculo)")

        Spacer(modifier = Modifier.height(32.dp)) // Trocado weight(1f) por um espaçamento fixo seguro para rolagem

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

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = aoClicarExcluirConta,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Excluir minha conta permanentemente", color = VermelhoErro)
        }
    }
}

@Composable
fun CaixaInfo(titulo: String, subtitulo: String) {
    Box(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(titulo, fontSize = 12.sp, color = Color.Gray)
            Text(subtitulo, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

