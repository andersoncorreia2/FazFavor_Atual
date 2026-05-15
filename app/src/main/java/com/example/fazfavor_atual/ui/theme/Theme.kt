package com.example.fazfavor_atual.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// As nossas cores aplicadas ao tema
private val NossoEsquemaCores = lightColorScheme(
    primary = AzulPrincipal,
    secondary = VerdeBotao,
    background = FundoCinza,
    error = VermelhoErro
)

// A ETIQUETA CORRETA QUE O MAINACTIVITY ESTÁ PROCURANDO! 👇
@Composable
fun FazFavor_AtualTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NossoEsquemaCores,
        content = content
    )
}