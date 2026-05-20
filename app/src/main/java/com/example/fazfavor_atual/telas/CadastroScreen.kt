package com.example.fazfavor_atual.telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.example.fazfavor_atual.ui.theme.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.fazfavor_atual.ui.theme.AzulPrincipal
import com.example.fazfavor_atual.ui.theme.VerdeBotao
import com.example.fazfavor_atual.ui.theme.VermelhoErro

// --- MÁSCARAS ---
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
            override fun originalToTransformed(offset: Int): Int =
                if (offset <= 2) offset else if (offset <= 5) offset + 1 else if (offset <= 8) offset + 2 else if (offset <= 11) offset + 3 else 14
            override fun transformedToOriginal(offset: Int): Int =
                if (offset <= 3) offset else if (offset <= 7) offset - 1 else if (offset <= 11) offset - 2 else if (offset <= 14) offset - 3 else 11
        }
        return TransformedText(AnnotatedString(out), cpfOffsetTranslator)
    }
}

class TelefoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 11) text.text.substring(0..10) else text.text
        var out = ""
        for (i in trimmed.indices) {
            if (i == 0) out += "("
            out += trimmed[i]
            if (i == 1) out += ") "
            if (i == 6) out += "-"
        }
        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                if (offset == 0) 0 else if (offset == 1) 2 else if (offset == 2) 5 else if (offset <= 6) offset + 3 else if (offset <= 11) offset + 4 else 15
            override fun transformedToOriginal(offset: Int): Int =
                if (offset <= 1) 0 else if (offset <= 3) 1 else if (offset <= 5) 2 else if (offset <= 10) offset - 3 else if (offset <= 15) offset - 4 else 11
        }
        return TransformedText(AnnotatedString(out), offsetTranslator)
    }
}

class PlacaVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(7)
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 2 && trimmed.length > 3) out += "-"
        }
        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                if (offset <= 3) offset else if (offset <= 7) offset + 1 else 8
            override fun transformedToOriginal(offset: Int): Int =
                if (offset <= 3) offset else if (offset <= 8) offset - 1 else 7
        }
        return TransformedText(AnnotatedString(out), offsetTranslator)
    }
}

@Composable
fun CadastroScreen(
    aoConcluirCadastro: (String, String, String, String, String, String, String, String) -> Unit,
    aoClicarFechar: () -> Unit,
    mensagemErro: String = ""
) {
    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var veiculo by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }
    var vagas by remember { mutableStateOf("") }
    var ofertarCarona by remember { mutableStateOf(false) }
    var senhaVisivel by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {

        Text("Cadastro", fontSize = 24.sp, color = AzulPrincipal, fontWeight = FontWeight.Bold)

        if (mensagemErro.isNotEmpty()) {
            val corAlerta = if (mensagemErro.contains("Conectando")) AzulPrincipal else VermelhoErro
            Text(mensagemErro, color = corAlerta, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nome, onValueChange = { nome = it }, label = { Text("Nome completo") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )

        OutlinedTextField(
            value = cpf, onValueChange = { cpf = it.filter { char -> char.isDigit() }.take(11) }, label = { Text("CPF") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            visualTransformation = CpfVisualTransformation()
        )

        OutlinedTextField(
            value = telefone, onValueChange = { telefone = it.filter { char -> char.isDigit() }.take(11) }, label = { Text("Telefone / WhatsApp") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            visualTransformation = TelefoneVisualTransformation()
        )

        OutlinedTextField(
            value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )

        OutlinedTextField(
            value = senha, onValueChange = { senha = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            trailingIcon = {
                val image = if (senhaVisivel) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (senhaVisivel) "Ocultar senha" else "Mostrar senha"
                IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = if (ofertarCarona) "Ofertar Carona Ativado" else "Ofertar Carona Desativado", color = if (ofertarCarona) VerdeBotao else Color.Gray, fontWeight = if (ofertarCarona) FontWeight.Bold else FontWeight.Normal)
            Spacer(modifier = Modifier.width(8.dp))
            Switch(checked = ofertarCarona, onCheckedChange = { ofertarCarona = it })
        }

        if (ofertarCarona) {
            OutlinedTextField(value = veiculo, onValueChange = { veiculo = it }, label = { Text("Veículo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = placa, onValueChange = { placa = it.uppercase().take(7) }, label = { Text("Placa") }, modifier = Modifier.fillMaxWidth(),
                visualTransformation = PlacaVisualTransformation(), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )
            OutlinedTextField(value = vagas, onValueChange = { vagas = it }, label = { Text("Vagas") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { aoConcluirCadastro(nome, cpf, telefone, email, senha, veiculo, placa, vagas) }, colors = ButtonDefaults.buttonColors(containerColor = VerdeBotao), modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text("Concluir cadastro")
            }
            OutlinedButton(onClick = { nome = ""; cpf = ""; telefone = ""; email = ""; senha = ""; veiculo = ""; placa = ""; vagas = ""; ofertarCarona = false }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text("Limpar Todos os Campos", color = Color.Gray)
            }
            TextButton(onClick = aoClicarFechar, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text("Fechar Cadastro", color = VermelhoErro)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}