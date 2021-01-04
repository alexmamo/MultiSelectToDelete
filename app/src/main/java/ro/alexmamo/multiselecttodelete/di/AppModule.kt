package ro.alexmamo.multiselecttodelete.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import ro.alexmamo.multiselecttodelete.utils.Constants.PRODUCTS_COLLECTION
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideProductsCollectionReference(rootRef: FirebaseFirestore) = rootRef.collection(PRODUCTS_COLLECTION)
}