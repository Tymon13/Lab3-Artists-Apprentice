package pl.edu.pwr.lab3.i238162;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.menu_item_credits) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.credits_title)
                    .setMessage(R.string.credits_text)
                    .setPositiveButton(R.string.button_ok, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        for (UiUpdatable uiElement : currentUiElements) {
            uiElement.updateUi();
        }
    }
}
