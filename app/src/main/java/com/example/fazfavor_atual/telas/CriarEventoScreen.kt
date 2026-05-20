package com.example.fazfavor_atual.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fazfavor_atual.ui.theme.*

// --- MÁSCARA DE RELÓGIO (00:00) ---
class MascaraHorario : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val limpo = text.text.take(4)
        var formatado = ""

        for (i in limpo.indices) {
            formatado += limpo[i]
            if (i == 1 && limpo.length > 2) formatado += ":"
        }

        val mapeamento = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return offset + 1
                return 5
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                return 4
            }
        }
        return TransformedText(AnnotatedString(formatado), mapeamento)
    }
}

@Composable
fun CriarEventoScreen(aoPublicarEvento: (String, String, String, String, String) -> Unit, aoClicarSair: () -> Unit) {
    var nomeEvento by remember { mutableStateOf("") }
    var origem by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }
    var horario by remember { mutableStateOf("") }
    var vagas by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = aoClicarSair, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Sair", tint = AzulPrincipal)
            }
            Text("Novo Evento/Carona", color = AzulPrincipal, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Para onde vamos hoje?", fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = nomeEvento, onValueChange = { nomeEvento = it }, label = { Text("Nome do Evento (Ex: Culto de Domingo)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = origem, onValueChange = { origem = it }, label = { Text("Origem (De onde sai?)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = destino, onValueChange = { destino = it }, label = { Text("Destino (Para onde vai?)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // ⏰ CAMPO DE HORÁRIO ATUALIZADO (Apenas números + Máscara)
            OutlinedTextField(
                value = horario,
                onValueChange = { novoValor ->
                    val apenasNumeros = novoValor.filter { it.isDigit() }
                    if (apenasNumeros.length <= 4) {
                        horario = apenasNumeros
                    }
                },
                label = { Text("Horário") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = MascaraHorario()
            )

            // 🔢 CAMPO DE VAGAS ATUALIZADO (Apenas números)
            OutlinedTextField(
                value = vagas,
                onValueChange = { novoValor ->
                    val apenasNumeros = novoValor.filter { it.isDigit() }
                    vagas = apenasNumeros
                },
                label = { Text("Vagas") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { aoPublicarEvento(nomeEvento, origem, destino, horario, vagas) },
            colors = ButtonDefaults.buttonColors(containerColor = VerdeBotao),
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Publicar Carona", fontSize = 16.sp)
        }
    }
}