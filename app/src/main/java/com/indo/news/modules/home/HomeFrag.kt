package com.indo.news.modules.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.indo.news.R
import com.indo.news.databinding.FragHomeBinding
import com.indo.news.modules.home.adapter.HomeAdapter
import com.indo.news.modules.home.adapter.ItemLoadMoreAdapter
import com.indo.news.modules.home.viewmodel.HomeVM
import com.indo.news.utils.extension.setFragBinding
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeFrag : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: HomeVM by viewModels {
        viewModelFactory
    }
    private lateinit var binding: FragHomeBinding
    private lateinit var homeAdapter: HomeAdapter

    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = setFragBinding(R.layout.frag_home, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHomeAdapter()
        setHomeNews()
        setSwipeRefresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    private fun setHomeNews() {
        job?.cancel()
        job = lifecycleScope.launch {
//            viewModel.getHomeNews().collectLatest {
//                homeAdapter.submitData(it)
//            }
            viewModel.getHomeNews().collect {
                homeAdapter.submitData(it)
            }
        }
    }

    private fun setHomeAdapter() {
        homeAdapter = HomeAdapter()
        binding.rv.adapter = homeAdapter.withLoadStateHeaderAndFooter(
            header = ItemLoadMoreAdapter { homeAdapter.retry() },
            footer = ItemLoadMoreAdapter { homeAdapter.retry() }
        )
    }

    private fun setSwipeRefresh() {
        val swipeRefresh = binding.swipeRefresh
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = true
            setHomeNews()
            swipeRefresh.isRefreshing = false
        }
    }

}