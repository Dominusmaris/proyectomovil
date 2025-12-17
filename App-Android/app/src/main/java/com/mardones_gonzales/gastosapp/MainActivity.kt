package com.mardones_gonzales.gastosapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.mardones_gonzales.gastosapp.autenticacion.CredencialesManager
import com.mardones_gonzales.gastosapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var estaLogueado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // VERIFICAR SESIÓN Y MOSTRAR/OCULTAR BOTTOM NAV
        verificarSesionYConfigurar()
    }

    private fun verificarSesionYConfigurar() {
        estaLogueado = CredencialesManager.haySesionActiva(this)

        if (estaLogueado) {
            // Usuario ya logueado - mostrar app normal
            mostrarAppNormal()
        } else {
            // Usuario no logueado - ocultar bottom nav
            ocultarBottomNavigation()
        }

        setupNavigation()
        navegarAlFragmentCorrecto()
    }

    private fun navegarAlFragmentCorrecto() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (estaLogueado) {
            // Si ya está logueado, ir directo a Home
            navController.navigate(R.id.homeFragment)
        }
        // Si no está logueado, nav_graph ya empieza con loginFragment
    }

    private fun mostrarAppNormal() {
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    private fun ocultarBottomNavigation() {
        binding.bottomNavigation.visibility = View.GONE
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (estaLogueado) {
            binding.bottomNavigation.setupWithNavController(navController)
        }
    }

    // Llamado desde LoginFragment cuando login exitoso
    fun onLoginExitoso() {
        estaLogueado = true
        mostrarAppNormal()

        // Re-configurar navegación
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        // Navegar a Home
        navController.navigate(com.mardones_gonzales.gastosapp.R.id.homeFragment)
    }
}