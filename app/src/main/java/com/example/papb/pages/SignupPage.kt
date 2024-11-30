package com.example.papb.pages

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.papb.AuthState
import com.example.papb.AuthViewModel

@Composable
fun SignupPage(
        modifier: Modifier = Modifier,
        navController: NavController,
        authViewModel: AuthViewModel
) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        val authState = authViewModel.authState.observeAsState()

        val context = LocalContext.current

        LaunchedEffect(authState.value) {
                when (authState.value) {
                        is AuthState.Authenticated -> navController.navigate("home")
                        is AuthState.Error ->
                                Toast.makeText(
                                                context,
                                                (authState.value as AuthState.Error).message,
                                                Toast.LENGTH_SHORT
                                        )
                                        .show()
                        else -> Unit
                }
        }

        Column(
                modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Text(
                        text = "Sign Up",
                        fontSize = 32.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )

                OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )

                OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )

                Button(
                        onClick = {
                            if (password == confirmPassword) {
                                authViewModel.signUp(email, password)
                            } else {
                                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = authState.value != AuthState.Loading && password.isNotEmpty() && confirmPassword.isNotEmpty()
                ) { Text(text = "Sign Up") }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = { navController.navigate("login") }) {
                        Text(text = "Already have an account? Login")
                }
        }
}
