package com.andrei.car_rental_android.state

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "localStorage")



interface LocalRepository {

    val refreshToken: Flow<String?>
    val accessToken: Flow<String?>

    suspend fun setRefreshToken(refreshToken:String)
    suspend fun setAccessToken(accessToken:String)

    suspend fun clear()

}
class LocalRepositoryImpl @Inject constructor(
    @ApplicationContext private val  context: Context,
    ): LocalRepository{


   private  val keyRefreshToken: Preferences.Key<String> = stringPreferencesKey("keyRefreshToken")
   private  val keyAccessToken: Preferences.Key<String> = stringPreferencesKey("keyAccessToken")

    override val refreshToken: Flow<String?> = context.dataStore.data.map { preferences->
        preferences[keyRefreshToken]
    }
    override val accessToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[keyAccessToken]
    }

    override suspend fun setRefreshToken(refreshToken: String) {
             context.dataStore.edit { preferences->
                 preferences[keyRefreshToken] = refreshToken
             }

    }

    override suspend fun setAccessToken(accessToken: String) {
            context.dataStore.edit { preferences->
                preferences[keyAccessToken] = accessToken
        }
    }

    override suspend  fun clear() {
            context.dataStore.edit{ preferences->
                preferences.clear()
            }
    }


}