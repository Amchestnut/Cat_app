package com.example.cat_app.features.edit_profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onClose: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // slušamo efekat da se vratimo nakon Save
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            if (effect is EditProfileContract.SideEffect.ProfileSaved) {
                onClose()
            }
        }
    }

    LaunchedEffect (Unit) {
        viewModel.setEvent(EditProfileContract.UiEvent.LoadData)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
    ) { padding ->
        Column (
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.loading) {
                CircularProgressIndicator()
            } else {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = {
                        viewModel.setEvent(EditProfileContract.UiEvent.NameChanged(it))
                    },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.nickname,
                    onValueChange = {
                        viewModel.setEvent(EditProfileContract.UiEvent.NicknameChanged(it))
                    },
                    label = { Text("Nickname") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.email,
                    onValueChange = {
                        viewModel.setEvent(EditProfileContract.UiEvent.EmailChanged(it))
                    },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = {
                        viewModel.setEvent(EditProfileContract.UiEvent.SaveProfile)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            }
        }
    }
}