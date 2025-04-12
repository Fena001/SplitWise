package eu.tutorials.fena.splitwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import eu.tutorials.fena.splitwise.ui.theme.SplitWiseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplitWiseTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "sign_in") {
                        composable("sign_in") {
                            SignInScreen(onSignIn = { userName ->
                                navController.navigate("add_expense?user=$userName")
                            })
                        }
                        composable(route = "add_expense?user={user}",
                            arguments = listOf(navArgument("user") { type = NavType.StringType })
                        ) { backStackEntry -> val user = backStackEntry.arguments?.getString("user") ?: ""
                            SplitwiseApp(currentUser = user)
                        } }
                }

            }
        }
    }
}

