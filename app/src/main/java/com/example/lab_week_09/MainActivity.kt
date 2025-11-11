package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme
import com.example.lab_week_09.ui.theme.OnBackgroundItemText
import com.example.lab_week_09.ui.theme.OnBackgroundTitleText
import com.example.lab_week_09.ui.theme.PrimaryTextButton


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                // The main container surface
                Surface(
                    // We use Modifier.fillMaxSize() to make the surface fill the whole screen
                    modifier = Modifier.fillMaxSize(),
                    // Use the background color from the theme
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Create and remember the NavController for navigation
                    val navController = rememberNavController()
                    App(
                        navController = navController
                    )
                }
            }
        }
    }
}

// Data Model for the user input/list items
data class Student(
    var name: String
)

//================================================================================
// ROOT COMPOSABLE: App (Navigation Host)
//================================================================================
// This will be the root composable of the app, hosting the navigation graph
@Composable
fun App(navController: NavHostController) {
    // NavHost creates the navigation graph and manages screen switching
    NavHost(
        navController = navController,
        startDestination = "home" // Sets the starting screen
    ) {
        // Route definition for the "home" screen
        composable("home") {
            // Home composable receives a lambda to trigger navigation
            Home { listDataJson ->
                // Navigates to the result screen, passing the list data as a URL argument
                navController.navigate(
                    "resultContent/?listData=$listDataJson"
                )
            }
        }
        // Route definition for the "resultContent" screen
        composable(
            "resultContent/?listData={listData}",
            arguments = listOf(navArgument("listData") {
                type = NavType.StringType // Define the argument type as String
            })
        ) { backStackEntry ->
            // Passes the argument value to the ResultContent composable
            ResultContent(
                backStackEntry.arguments?.getString("listData").orEmpty()
            )
        }
    }
}

//================================================================================
// DESTINATION 1: Home (State Holder)
//================================================================================
// Home manages the state and provides navigation functionality
@Composable
fun Home(
    navigateFromHomeToResult: (String) -> Unit // Lambda to trigger navigation
) {
    // Creates a mutable state list for displaying students
    val listData = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }
    // Creates a mutable state for the text input field value
    var inputField by remember { mutableStateOf(Student("")) }

    // Passes state and event handlers to the stateless HomeContent
    HomeContent(
        listData,
        inputField,
        // Handler for input field value change
        { input -> inputField = inputField.copy(name = input) },
        // Handler for the Add Student button click
        {
            // FIX: Ensure input is not blank before adding the student
            if (inputField.name.isNotBlank()) {
                listData.add(inputField.copy())
                inputField = Student("") // Reset input field
            }
        },
        // Handler for the Finish/Navigate button click
        {
            // Triggers navigation, passing the string representation of the current list data
            navigateFromHomeToResult(listData.toList().toString())
        }
    )
}

//================================================================================
// CHILD COMPOSABLE: HomeContent (UI Renderer)
//================================================================================
// HomeContent displays the UI and exposes events (clicks, input changes) to the parent
@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    navigateFromHomeToResult: () -> Unit // Lambda to navigate
) {
    LazyColumn {
        item {
            Column(
                // Modifier to set padding and fill size
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                // Aligns items horizontally in the center
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // UI Element for the Title Text
                OnBackgroundTitleText(
                    text = stringResource(
                        id = R.string.enter_item
                    )
                )

                TextField(
                    value = inputField.name,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    onValueChange = {
                        onInputValueChange(it) // Calls parent handler on change
                    }
                )

                // Row to hold the two buttons horizontally
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Button to add a new student to the list
                    PrimaryTextButton(
                        text = stringResource(id = R.string.button_click)
                    ) {
                        onButtonClick() // Calls parent handler (with validation logic)
                    }
                    // Button to navigate to the result screen
                    PrimaryTextButton(
                        text = "Finish"
                    ) {
                        navigateFromHomeToResult() // Calls navigation handler
                    }
                }
            }
        }
        // Loops through the listData to display each student name
        items(listData) { item ->
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // UI Element for the Item Text
                OnBackgroundItemText(text = item.name)
            }
        }
    }
}

//================================================================================
// DESTINATION 2: ResultContent (Result Page)
//================================================================================
// ResultContent displays the student list data passed from the Home screen
@Composable
fun ResultContent(listData: String) {
    Column(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnBackgroundTitleText(text = "Result Content")
        // Displays the received data string (which is the student list)
        OnBackgroundItemText(text = listData)
    }
}


//================================================================================
// PREVIEW
//================================================================================
@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    LAB_WEEK_09Theme {
        // Previewing the UI component (HomeContent) with dummy data and empty handlers
        HomeContent(
            listData = mutableStateListOf(Student("Tanu"), Student("Tina"), Student("Tono")),
            inputField = Student(""),
            onInputValueChange = {},
            onButtonClick = {},
            navigateFromHomeToResult = {}
        )
    }
}