package pl.edu.pwr.lab3.i238162;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import pl.edu.pwr.lab3.i238162.ui.UiUpdatable;

public class MainActivity extends AppCompatActivity {
    private GameController controller;
    private final ArrayList<UiUpdatable> currentUiElements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_workshop, R.id.navigation_upgrades, R.id.navigation_main).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        controller = new GameController(this);
    }

    public GameController getController() {
        return controller;
    }

    public void registerCurrentUiElement(UiUpdatable uiElement) {
        currentUiElements.add(uiElement);
    }

    public void deregisterCurrentUiElement(UiUpdatable uiElement) {
        currentUiElements.remove(uiElement);
    }

    public void updateUi() {
        for (UiUpdatable uiElement: currentUiElements) {
            uiElement.updateUi();
        }
    }
}
