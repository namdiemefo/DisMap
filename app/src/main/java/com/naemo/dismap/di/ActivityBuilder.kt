package com.naemo.dismap.di

import com.naemo.dismap.ui.main.MainActivity
import com.naemo.dismap.ui.main.MainModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun contributeMainActivity(): MainActivity
}