package raf.console.qalamsharifaudio.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import raf.console.qalamsharifaudio.R;
import raf.console.qalamsharifaudio.databinding.ActivityMainBinding;
import raf.console.qalamsharifaudio.logic.viewmodel.PlayerViewModelGlobal;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    public PlayerViewModelGlobal playerViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Инициализация ViewModel
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModelGlobal.class);
        playerViewModel.initializePlayer(this);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController
                (this, navController, appBarConfiguration);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // Навигация на AppAboutFragment
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

            // Проверяем, не находимся ли мы уже на AppAboutFragment
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() != R.id.appAboutFragment) {
                navController.navigate(R.id.action_surasFragment_to_appAboutFragment);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerViewModel.releasePlayer();
    }
}