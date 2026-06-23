package com.example.coffeeapp.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Phase 3: Firebase Auth and Firestore providers will go here
    // Phase 6: Room database provider will go here
}