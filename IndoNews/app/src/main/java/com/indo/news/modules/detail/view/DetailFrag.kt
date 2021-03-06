package com.indo.news.modules.detail.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.indo.news.R
import com.indo.news.data.model.News
import com.indo.news.databinding.FragDetailBinding
import com.indo.news.modules.detail.adapter.RecommendedRV
import com.indo.news.modules.detail.viewmodel.DetailVM
import com.indo.news.utils.Result
import com.indo.news.utils.extension.TimeAgo
import com.indo.news.utils.extension.setFragBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

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
        getFavorite()
        initView()
        initLiveData()
        initAds()
    }

    private fun initView() {
        binding.detailLayout.apply {
            data = args.news
            time = TimeAgo.getTimeAgo(args.news.publishedAt)
            content = args.news.content?.slice(0..200)

        }
        binding.errorLayout.btnRetry.setOnClickListener {
            initLiveData()
        }
        binding.bottomAppbar.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.bottomAppbar.btnFavorite.setOnClickListener {
            setFavorite()
        }
        binding.bottomAppbar.btnShare.setOnClickListener { actionShare() }
    }

    private fun initLiveData() {
        viewModel.getRecommendedNews.observe(viewLifecycleOwner, Observer {
            setupViewState(it)
        })
    }

    private fun getFavorite() {
        lifecycleScope.launch {
            if (viewModel.isFavorite(args.news.title) == 1) {
                binding.bottomAppbar.btnFavorite.setImageResource(R.drawable.ic_favorite)
            } else {
                binding.bottomAppbar.btnFavorite.setImageResource(R.drawable.ic_favorite_border)
            }
        }
    }

    private fun setFavorite() {
        lifecycleScope.launch {
            if (viewModel.isFavorite(args.news.title) == 1) {
                viewModel.deleteFavorite(args.news)
                binding.bottomAppbar.btnFavorite.setImageResource(R.drawable.ic_favorite_border)
            } else {
                viewModel.insertFavorite(args.news)
                binding.bottomAppbar.btnFavorite.setImageResource(R.drawable.ic_favorite)
            }
        }
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

    private fun initAds() {
        val adView = binding.detailLayout.adView

        adView.loadAd(AdRequest.Builder().build())
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                super.onAdFailedToLoad(errorCode)
                Timber.d(errorCode.toString())
            }
        }
    }

    private fun actionShare() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, args.news.url)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}