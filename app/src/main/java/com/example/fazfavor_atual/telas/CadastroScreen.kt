package com.example.fazfavor_atual.telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fazfavor_atual.BancoDeDados // <-- O ESPIÃO PRECISA DO BANCO DE DADOS AQUI!
import com.example.fazfavor_atual.ui.theme.*

class CpfVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 11) text.text.substring(0..10) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 2 || i == 5) out += "."
            if (i == 8) out += "-"
        }

        val cpfOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset + 1
                if (offset <= 8) return offset + 2
                if (offset <= 11) return offset + 3
                return 14
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset - 1
                if (offset <= 11) return offset - 2
                if (offset <= 14) return offset - 3
                return 11
            }
        }
        return TransformedText(AnnotatedString(out), cpfOffsetTranslator)
    }
}

@Composable
fun CadastroScreen(
    aoConcluirCadastro: (String, String, String, String, String, String, String) -> Unit,
    aoClicarFechar: () -> Unit,
    mensagemErro: String = ""
) {
    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var erroCpfTempoReal by remember { mutableStateOf("") } // <-- O AVISO INSTANTÂNEO
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var veiculo by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }
    var vagas by remember { mutableStateOf("") }
    var ofertarCarona by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {

        Text("Cadastro", fontSize = 24.sp, color = AzulPrincipal, fontWeight = FontWeight.Bold)

        // Erro geral vindo do botão "Concluir"
        if (mensagemErro.isNotEmpty()) {
            Text(mensagemErro, color = VermelhoErro, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nome, onValueChange = { nome = it }, label = { Text("Nome completo") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )

        // --- CAIXA DO CPF COM RADAR DE DUPLICADOS ---
        OutlinedTextField(
            value = cpf,
            onValueChange = { valorDigitado ->
                val apenasNumeros = valorDigitado.filter { it.isDigit() }.take(11)
                cpf = apenasNumeros

                // Se o utilizador acabou de digitar os 11 números, o radar dispara!
                if (apenasNumeros.length == 11) {
                    if (BancoDeDados.cpfJaCadastrado(apenasNumeros)) {
                        erroCpfTempoReal = "Este CPF já possui cadastro!"
                    } else {
                        erroCpfTempoReal = "" // Tudo limpo!
                    }
                } else {
                    erroCpfTempoReal = "" // Se apagou um número, tira o aviso
                }
            },
            label = { Text("CPF") },
            modifier = Modifier.fillMaxWidth(),
            isError = erroCpfTempoReal.isNotEmpty(), // Pinta a caixa de vermelho se houver erro!
            supportingText = { // Mostra o textinho vermelho debaixo da caixa
                if (erroCpfTempoReal.isNotEmpty()) {
                    Text(erroCpfTempoReal, color = VermelhoErro)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            visualTransformation = CpfVisualTransformation()
        )

        OutlinedTextField(
            value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )

        OutlinedTextField(
            value = senha, onValueChange = { senha = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            // A MÁGICA ACONTECE AQUI:
            // Se ofertarCarona for true, mostra "Ativado". Se for false, mostra "Desativado".
            val statusTexto = if (ofertarCarona) "Ofertar Carona Ativado" else "Ofertar Carona Desativado"

            Text(
                text = statusTexto,
                color = if (ofertarCarona) VerdeBotao else Color.Gray, // Bônus: muda a cor também!
                fontWeight = if (ofertarCarona) FontWeight.Bold else FontWeight.Normal
            )

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = ofertarCarona,
                onCheckedChange = { ofertarCarona = it }
            )
        }

        if (ofertarCarona) {
            OutlinedTextField(value = veiculo, onValueChange = { veiculo = it }, label = { Text("Veículo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = placa, onValueChange = { placa = it }, label = { Text("Placa") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = vagas, onValueChange = { vagas = it }, label = { Text("Vagas") }, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { aoConcluirCadastro(nome, cpf, email, senha, veiculo, placa, vagas) },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeBotao),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Concluir cadastro")
            }

            OutlinedButton(
                onClick = {
                    nome = ""
                    cpf = ""
                    erroCpfTempoReal = "" // Limpa o erro também!
                    email = ""
                    senha = ""
                    veiculo = ""
                    placa = ""
                    vagas = ""
                    ofertarCarona = false
                },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Limpar Todos os Campos", color = Color.Gray)
            }

            TextButton(
                onClick = aoClicarFechar,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Fechar Cadastro", color = VermelhoErro)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}