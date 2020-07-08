package com.indo.news.modules.detail.view

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.indo.news.R
import com.indo.news.data.model.News
import com.indo.news.databinding.FragDetailBinding
import com.indo.news.modules.detail.adapter.RecommendedRV
import com.indo.news.modules.detail.viewmodel.DetailVM
import com.indo.news.utils.Result
import com.indo.news.utils.extension.TimeAgo
import com.indo.news.utils.extension.setFragBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFrag : Fragment() {

    private lateinit var binding: FragDetailBinding
    private lateinit var recommendedAdapter: RecommendedRV
    private val viewModel: DetailVM by viewModels()
    private val args: DetailFragArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = setFragBinding(R.layout.frag_detail, container)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initLiveData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.favorite -> {
            true
        }
        R.id.share -> {
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }


    private fun initView() {
        binding.detailLayout.apply {
            data = args.news
            time = TimeAgo.getTimeAgo(args.news.publishedAt)
            content = args.news.content?.slice(0..200)
            tvSeeMore.setOnClickListener {
                val action = DetailFragDirections.actionDetailFragToMoreFrag(args.news.url)
                findNavController().navigate(action)
            }
        }
        binding.errorLayout.btnRetry.setOnClickListener {
            initLiveData()
        }
        binding.bottomAppbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initLiveData() {
        viewModel.getRecommendedNews.observe(viewLifecycleOwner, Observer {
            setupViewState(it)
        })
    }

    private fun initAdapter(listNews: News) {
        recommendedAdapter = RecommendedRV(listNews)
        binding.recommendLayout.rvRecommend.adapter = recommendedAdapter
    }

    private fun setupViewState(result: Result<News>) {
        when (result) {
            is Result.InProgress -> {
                binding.apply {
                    loadingLayout.root.isVisible = true
                    detail.isVisible = false
                }
            }
            is Result.Success -> {
                initAdapter(result.data)
                binding.apply {
                    loadingLayout.root.isVisible = false
                    detail.isVisible = true
                }
            }
            is Result.Error -> {
                binding.apply {
                    loadingLayout.root.isVisible = false
                    errorLayout.root.isVisible = true
                }
            }
        }
    }

}