package com.example.starextask.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.starextask.R
import com.example.starextask.databinding.ActivityMainBinding
import com.example.starextask.ui.adapter.PostsAdapter
import com.example.starextask.util.*
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(), KodeinAware {

    companion object {
        const val ANIMATION_DURATION = 1000.toLong()
    }

    override val kodein by kodein()
    private lateinit var dataBind: ActivityMainBinding
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var customAdapterPosts: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBind = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupViewModel()
        setupUI()
        initializeObserver()
        handleNetworkChanges()
        setupAPICall()
        searchPost(etInput, btnSendRequest)
    }

    private fun setupUI() {
        customAdapterPosts = PostsAdapter()
        dataBind.recyclerViewPosts.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = customAdapterPosts
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
    }

    private fun initializeObserver() {
        viewModel.idQueryLiveData.observe(this, Observer {
            Log.i("Info", "Posts = $it")
        })
    }

    private fun setupAPICall() {
        viewModel.postsLiveData.observe(this, Observer { state ->
            when (state) {
                is State.Loading -> {
                    dataBind.recyclerViewPosts.hide()
                    dataBind.progressBar.show()
                }
                is State.Success -> {
                    dataBind.recyclerViewPosts.show()
                    dataBind.progressBar.hide()
                    customAdapterPosts.setData(state.data)
                    if(state.data.isEmpty()){
                        showToast("There is no post with this id.")
                    }
                }
                is State.Error -> {
                    dataBind.progressBar.hide()
                    showToast(state.message)
                }
            }
        })

    }

    @SuppressLint("NewApi")
    private fun handleNetworkChanges() {
        NetworkUtils.getNetworkLiveData(applicationContext).observe(this, Observer { isConnected ->
            if (!isConnected) {
                dataBind.textViewNetworkStatus.text = getString(R.string.text_no_connectivity)
                dataBind.networkStatusLayout.apply {
                    show()
                    setBackgroundColor(getColorRes(R.color.colorStatusNotConnected))
                }
            } else {
                if (viewModel.postsLiveData.value is State.Error || customAdapterPosts.itemCount == 0) {
                    viewModel.getPosts()
                }
                dataBind.textViewNetworkStatus.text = getString(R.string.text_connectivity)
                dataBind.networkStatusLayout.apply {
                    setBackgroundColor(getColorRes(R.color.colorStatusConnected))

                    animate()
                        .alpha(1f)
                        .setStartDelay(ANIMATION_DURATION)
                        .setDuration(ANIMATION_DURATION)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                hide()
                            }
                        })
                }
            }
        })
    }

    private fun searchPost(etSearch: EditText, btnSearch: Button) {

        btnSearch.setOnClickListener {
            dismissKeyboard(etSearch)
            etSearch.clearFocus()
            viewModel.searchPost(etSearch.text.toString())
        }

        etSearch.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    dismissKeyboard(etSearch)
                    etSearch.clearFocus()
                    viewModel.searchPost(etSearch.text.toString())
                    return true
                }
                return false
            }
        })

    }
}