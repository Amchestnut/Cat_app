package com.example.cat_app.features.login_screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cat_app.R // Obavezno dodaj import za tvoje resurse

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginScreenContract.SideEffect.NavigateToMain -> onLoginSuccess()
                is LoginScreenContract.SideEffect.ShowError -> Log.e("Login", effect.message)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Pozadinska slika
        Image(
            painter = painterResource(id = R.drawable.my_wallpaper),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // Polu-providni sloj preko slike za bolju čitljivost
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        // Sadržaj ekrana koji se može skrolovati
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- Zaglavlje ---
            item {
                Icon(
                    imageVector = Icons.Outlined.Pets,
                    contentDescription = "App Logo",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Welcome to Catapult!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Join our purr-fect community\nby creating a profile.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(32.dp))
            }

            // --- Forma za unos ---
            item {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.setEvent(LoginScreenContract.UiEvent.NameChanged(it)) },
                    label = { Text("Your Name") },
                    leadingIcon = { Icon(Icons.Outlined.PersonOutline, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            item { Spacer(Modifier.height(12.dp)) }

            item {
                OutlinedTextField(
                    value = state.nickname,
                    onValueChange = { viewModel.setEvent(LoginScreenContract.UiEvent.NicknameChanged(it)) },
                    label = { Text("Choose a Nickname") },
                    leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            item { Spacer(Modifier.height(12.dp)) }

            item {
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.setEvent(LoginScreenContract.UiEvent.EmailChanged(it)) },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Outlined.AlternateEmail, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    ),

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    )
                )
            }

            item { Spacer(Modifier.height(24.dp)) }

            // --- Dugme i greška ---
            item {
                Button(
                    onClick = { viewModel.setEvent(LoginScreenContract.UiEvent.Submit) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !state.loading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Create Profile", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            item {
                state.error?.let { err ->
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}