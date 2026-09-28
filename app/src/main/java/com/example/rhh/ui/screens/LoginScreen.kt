package com.example.rhh.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.rhh.ui.components.GradientButton
import com.example.rhh.ui.theme.Accent
import com.example.rhh.ui.theme.Border
import com.example.rhh.ui.theme.Bg
import com.example.rhh.ui.theme.Surface
import com.example.rhh.ui.theme.TextDim
import com.example.rhh.ui.theme.TextPri

@Composable
fun LoginScreen(
    busy: Boolean,
    error: String?,
    onLogin: (String, String) -> Unit,
    onGoRegister: () -> Unit
) {
    var id by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome back.", color = TextPri, style = MaterialTheme.typography.headlineLarge)
        Text("Sign in to keep tracking.", color = TextDim, modifier = Modifier.padding(top = 6.dp, bottom = 28.dp))

        Field("Username or email", id) { id = it }
        Spacer(Modifier.height(12.dp))
        Field("Password", pw, password = true) { pw = it }

        if (error != null) {
            Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp))
        }

        GradientButton(
            text = if (busy) "Signing in…" else "Sign in",
            onClick = { if (!busy) onLogin(id, pw) },
            modifier = Modifier.padding(top = 24.dp)
        )

        TextButton(
            onClick = onGoRegister,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
        ) {
            Text("No account? Create one", color = TextDim)
            Text("  Create one", color = Accent)
        }
    }
}

@Composable
private fun Field(
    placeholder: String,
    value: String,
    password: Boolean = false,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text(placeholder, color = TextDim) },
        singleLine = true,
        visualTransformation = if (password) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Accent,
            unfocusedBorderColor = Border,
            focusedContainerColor = Surface,
            unfocusedContainerColor = Surface,
            focusedTextColor = TextPri,
            unfocusedTextColor = TextPri,
            cursorColor = Accent
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
