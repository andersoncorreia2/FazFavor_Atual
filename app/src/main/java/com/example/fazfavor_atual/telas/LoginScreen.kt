package com.example.fazfavor_atual.telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fazfavor_atual.ui.theme.*

@Composable
fun LoginScreen(aoClicarEntrar: (String, String) -> Unit, aoClicarCriarConta: () -> Unit, mensagemErro: String = "") {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    // O mestre do Tab!
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("FazFavor", fontSize = 32.sp, color = AzulPrincipal, fontWeight = FontWeight.Bold)
        Text("Caronas solidárias", fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                aoClicarEntrar(email, senha)
            })
        )

        if (mensagemErro.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(mensagemErro, color = VermelhoErro, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- NOSSOS BOTÕES GRANDES E BONITOS VOLTARAM! ---

        // Botão Principal Verde
        Button(
            onClick = { aoClicarEntrar(email, senha) },
            colors = ButtonDefaults.buttonColors(containerColor = VerdeBotao),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Entrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão Secundário (Branco com borda azul)
        OutlinedButton(
            onClick = aoClicarCriarConta,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Criar conta", color = AzulPrincipal)
        }
    }
}