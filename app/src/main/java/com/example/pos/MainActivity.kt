package com.example.pos

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.pos.data.Product
import com.example.pos.databinding.ActivityMainBinding
import com.example.pos.ui.ProductViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ProductViewModel
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var categoriesAdapter: CategoriesPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        drawerLayout = binding.drawerLayout
        viewPager = binding.viewPager

        // Setup ViewPager with adapter
        categoriesAdapter = CategoriesPagerAdapter(this)
        viewPager.adapter = categoriesAdapter

        // Setup TabLayout with ViewPager
        TabLayoutMediator(binding.categoryTabs, viewPager) { tab, position ->
            tab.text = categoriesAdapter.getCategoryAt(position)
        }.attach()

        // Setup Navigation Drawer
        binding.navView.setNavigationItemSelectedListener(this)

        // Setup FAB
        binding.fabAddProduct.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun setupObservers() {
        viewModel.productTypes.observe(this) { types ->
            categoriesAdapter.updateCategories(types)
        }
    }

    private fun showAddProductDialog() {
        // TODO: Implement add product dialog
        Snackbar.make(binding.root, "Add Product Clicked", Snackbar.LENGTH_SHORT).show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_products -> {
                // Handle products navigation
            }
            R.id.nav_orders -> {
                // Handle orders navigation
            }
            R.id.nav_reports -> {
                // Handle reports navigation
            }
            R.id.nav_settings -> {
                // Handle settings navigation
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            R.id.action_search -> {
                // Handle search
                true
            }
            R.id.action_cart -> {
                // Handle cart
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
