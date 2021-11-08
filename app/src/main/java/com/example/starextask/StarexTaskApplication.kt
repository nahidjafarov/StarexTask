package com.example.starextask

import android.app.Application
import com.example.starextask.data.network.ApiInterface
import com.example.starextask.data.network.NetworkConnectionInterceptor
import com.example.starextask.data.repository.HomeRepository
import com.example.starextask.ui.home.HomeViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class StarexTaskApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@StarexTaskApplication))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { ApiInterface(instance()) }
        bind() from singleton { HomeRepository(instance()) }
        bind() from provider { HomeViewModelFactory(instance()) }

    }

}