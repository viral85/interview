package com.viralsonawala.articles.ui

import android.databinding.ObservableArrayList
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import com.github.nitrico.lastadapter.LastAdapter
import com.livinglifetechway.k4kotlin.*
import com.livinglifetechway.k4kotlin_retrofit.RetrofitCallback
import com.livinglifetechway.k4kotlin_retrofit.enqueue
import com.viralsonawala.articles.BR
import com.viralsonawala.articles.R
import com.viralsonawala.articles.databinding.ActivityHomeBinding
import com.viralsonawala.articles.databinding.ItemArticleBinding
import com.viralsonawala.articles.databinding.ItemAuthorBinding
import com.viralsonawala.articles.model.ArticlesResp
import com.viralsonawala.articles.network.ApiClient
import org.jetbrains.anko.alert

class HomeActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityHomeBinding

    /**
     * Articles list used to display on the UI
     */
    private val mArticles = ObservableArrayList<ArticlesResp.Article>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setBindingView(R.layout.activity_home)

        // setup UI
        setupUi()

        // fetch data
        fetchData()
    }

    /**
     * Fetches data for the articles and shows in the UI
     */
    private fun setupUi() {
        LastAdapter(mArticles, BR.item)
                .map<ArticlesResp.Article, ItemArticleBinding>(R.layout.item_article) {
                    onBind {
                        val binding = it.binding
                        val item = binding.item

                        // tags
                        if (item?.tagList?.size.orZero() > 0) {
                            showViews(binding.tags, binding.imageTag)
                            binding.tags.text = item?.tagList?.joinToString { it.title }
                        } else {
                            hideViews(binding.tags, binding.imageTag)
                        }

                        // regions
                        if (item?.regionList?.size.orZero() > 0) {
                            showViews(binding.region, binding.imageRegion)
                            binding.region.text = item?.regionList?.joinToString { it.name }
                        } else {
                            hideViews(binding.region, binding.imageRegion)
                        }

                        // authors
                        binding.authorContainer.removeAllViews()
                        if (item?.authors?.size.orZero() > 0) {
                            showViews(binding.authorContainer, binding.imageAuthor)
                            item?.authors?.forEach {
                                val itemAuthorBinding = ItemAuthorBinding.inflate(LayoutInflater.from(this@HomeActivity), binding.authorContainer, true)
                                itemAuthorBinding.item = it
                            }
                        } else {
                            hideViews(binding.authorContainer, binding.imageAuthor)
                        }
                    }
                }
                .into(mBinding.recyclerView)

        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.setHasFixedSize(true)
    }

    /**
     * Fetches data for the articles and shows in the UI
     */
    private fun fetchData() {
        ApiClient.service.getArticles("https://s3.eu-west-2.amazonaws.com/interview-question-data/articles/articles.json")
                .enqueue(this, RetrofitCallback {
                    progressView = mBinding.progressBar
                    on2xxSuccess { call, response ->
                        val body = response?.body()

                        // add all articles to observable list
                        mArticles.addAll(body?.data.orEmpty())
                    }
                    onUnsuccessfulResponseOrFailureNotCancelled { call, response, throwable ->
                        handleError()
                    }
                })
    }

    /**
     * This method handles error from the API by showing a dialog for retrying
     */
    private fun handleError() {
        alert {
            message = getString(if (!isNetworkAvailable()) R.string.network_error else R.string.default_error)
            positiveButton(R.string.btn_try_again) { fetchData() }
            negativeButton(R.string.btn_cancel) {}
        }.show()
    }
}
