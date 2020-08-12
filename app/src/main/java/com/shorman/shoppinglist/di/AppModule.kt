package com.shorman.shoppinglist.di

import android.app.Application
import androidx.room.Room
import com.shorman.shoppinglist.db.ShoppingDatabase
import com.shorman.shoppinglist.db.ShoppingDoa
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@InstallIn(ApplicationComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideDataBase(app:Application):ShoppingDatabase{

        return Room.databaseBuilder(app, ShoppingDatabase::class.java, "shopping_items")
            .fallbackToDestructiveMigration()
            .build()

    }

    @Provides
    @Singleton
    fun provideShoppingDoa(db:ShoppingDatabase):ShoppingDoa{
        return db.getShoppingDoa()
    }

}