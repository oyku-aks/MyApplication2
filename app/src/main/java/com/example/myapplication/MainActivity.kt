package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(Repository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val textError = findViewById<TextView>(R.id.textError)

        recyclerView.layoutManager = LinearLayoutManager(this)


        val adapter = UserAdapter { user ->
            viewModel.selectUser(user)
        }
        recyclerView.adapter = adapter


        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        textError.visibility = View.GONE
                    }
                    is UiState.Success -> {
                        progressBar.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        textError.visibility = View.GONE
                        adapter.submitList(state.users)
                    }
                    is UiState.Error -> {
                        progressBar.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                        textError.visibility = View.VISIBLE
                        textError.text = state.message
                    }
                }
            }
        }


        lifecycleScope.launch {
            viewModel.selectedUser.collectLatest { user ->
                if (user != null) {

                }
            }
        }


        viewModel.fetchUsers()
    }
}
