package com.github.gericass.world_heritage_client.di

import androidx.room.Room
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.github.gericass.world_heritage_client.common.navigator.AvgleNavigator
import com.github.gericass.world_heritage_client.data.AvgleRepository
import com.github.gericass.world_heritage_client.data.AvgleRepositoryImpl
import com.github.gericass.world_heritage_client.data.local.AvgleDatabase
import com.github.gericass.world_heritage_client.data.remote.BASE_URL
import com.github.gericass.world_heritage_client.home.HomeViewModel
import com.github.gericass.world_heritage_client.home.category.CategoryViewModel
import com.github.gericass.world_heritage_client.home.collection.CollectionViewModel
import com.github.gericass.world_heritage_client.navigator.AvgleNavigatorImpl
import com.github.gericass.world_heritage_client.search.result.ResultViewModel
import com.github.gericass.world_heritage_client.search.search.SearchViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object Modules {
    val apiModule = module {
        single {
            OkHttpClient()
                .newBuilder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addNetworkInterceptor(StethoInterceptor())
                .build()
        }

        single {
            Moshi
                .Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        }


        single {
            Retrofit.Builder()
                .client(get())
                .addConverterFactory(MoshiConverterFactory.create(get()))
                .baseUrl(BASE_URL)
                .build()
        }
    }

    val databaseModule = module {
        single {
            Room.databaseBuilder(
                androidApplication(),
                AvgleDatabase::class.java,
                "avgle.db"
            ).build()
        }
    }

    val repositoryModule = module {
        single<AvgleRepository> { AvgleRepositoryImpl(get(), get()) }
    }

    val viewModelModule = module {
        viewModel { CategoryViewModel(get()) }
        viewModel { CollectionViewModel(get()) }
        viewModel { SearchViewModel(get()) }
        viewModel { ResultViewModel(get()) }
        viewModel { HomeViewModel() }
    }

    val navigatorModule = module {
        single<AvgleNavigator> {
            AvgleNavigatorImpl()
        }
    }

    //val navigationModule = module {
    //    factory<Navigtor.MainNavigator> {
    //        MainNavigator()
    //    }
    //}
}