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
fun RegisterScreen(
    busy: Boolean,
    error: String?,
    onRegister: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create account.", color = TextPri, style = MaterialTheme.typography.headlineLarge)
        Text("7-day Elite trial. No card needed.", color = TextDim, modifier = Modifier.padding(top = 6.dp, bottom = 28.dp))

        Input("Username (letters & numbers)", username) { username = it }
        Spacer(Modifier.height(12.dp))
        Input("Email", email) { email = it }
        Spacer(Modifier.height(12.dp))
        Input("Password (min 6 chars)", pw, password = true) { pw = it }

        if (error != null) {
            Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp))
        }

        GradientButton(
            text = if (busy) "Creating…" else "Create account",
            onClick = { if (!busy) onRegister(username, email, pw) },
            modifier = Modifier.padding(top = 24.dp)
        )

        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
        ) { Text("Already have an account? Sign in", color = Accent) }
    }
}

@Composable
private fun Input(placeholder: String, value: String, password: Boolean = false, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text(placeholder, color = TextDim) },
        singleLine = true,
        visualTransformation = if (password) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
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
