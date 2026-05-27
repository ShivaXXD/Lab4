package com.example.lab3

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsListScreen(navController: NavController, viewModel: ProjectViewModel) {
    val projects by viewModel.projects.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Мої проекти") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add") }) {
                Icon(Icons.Default.Add, contentDescription = "Додати")
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(projects) { project ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable {
                        navController.navigate("details/${project.id}")
                    },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = project.name, style = MaterialTheme.typography.titleMedium)
                        Text(text = project.description, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(progress = { project.progress }, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectScreen(navController: NavController, viewModel: ProjectViewModel) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Новий проект") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Назва проекту") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Опис") }, modifier = Modifier.fillMaxWidth().height(120.dp))
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addProject(name, description)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Створити") }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                Text("Скасувати")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsProjectScreen(navController: NavController, viewModel: ProjectViewModel, projectId: String?) {
    if (projectId == null) return
    val projectState by viewModel.getProjectById(projectId).collectAsState(initial = null)
    val project = projectState ?: return

    var currentProgress by remember(project) { mutableFloatStateOf(project.progress) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Деталі проекту") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = project.name, style = MaterialTheme.typography.headlineMedium)
            Text(text = project.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Прогрес: ${(currentProgress * 100).toInt()}%")
            Slider(
                value = currentProgress,
                onValueChange = { currentProgress = it },
                onValueChangeFinished = { viewModel.updateProject(project.copy(progress = currentProgress)) }
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { navController.navigate("edit/${project.id}") }, modifier = Modifier.fillMaxWidth()) {
                Text("Редагувати")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                Text("Назад")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    viewModel.deleteProject(project)
                    navController.navigate("list") { popUpTo("list") { inclusive = true } }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text("Видалити") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProjectScreen(navController: NavController, viewModel: ProjectViewModel, projectId: String?) {
    if (projectId == null) return
    val projectState by viewModel.getProjectById(projectId).collectAsState(initial = null)
    val project = projectState ?: return

    var name by remember(project) { mutableStateOf(project.name) }
    var description by remember(project) { mutableStateOf(project.description) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Редагувати проект") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Назва проекту") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Опис") }, modifier = Modifier.fillMaxWidth().height(120.dp))
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.updateProject(project.copy(name = name, description = description))
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Зберегти зміни") }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                Text("Скасувати")
            }
        }
    }
}