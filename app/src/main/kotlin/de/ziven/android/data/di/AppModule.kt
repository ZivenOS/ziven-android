package de.ziven.android.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.ziven.android.BuildConfig
import de.ziven.shared.api.ApiClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiClient(): ApiClient = ApiClient(BuildConfig.BASE_URL)
}
