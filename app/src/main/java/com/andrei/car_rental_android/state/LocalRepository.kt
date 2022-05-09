package com.andrei.car_rental_android.state

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "localStorage")



interface LocalRepository {

    val refreshTokenFlow: Flow<String?>
    val accessTokenFlow: Flow<String?>
    val identityVerifiedFlow:Flow<Boolean?>

    suspend fun setRefreshToken(refreshToken:String)
    suspend fun setAccessToken(accessToken:String)
    suspend fun setIdentityVerified(verified:Boolean)

    suspend fun clearAccessToken()
    suspend fun clearRefreshToken()

    suspend fun clear()

}
class LocalRepositoryImpl @Inject constructor(
    @ApplicationContext private val  context: Context,
): LocalRepository{


    private  val keyRefreshToken: Preferences.Key<String> = stringPreferencesKey("keyRefreshToken")
    private  val keyAccessToken: Preferences.Key<String> = stringPreferencesKey("keyAccessToken")
    private  val keyIdentityVerified: Preferences.Key<Boolean> = booleanPreferencesKey("keyIdentityVerified")

    override val refreshTokenFlow: Flow<String?> = context.dataStore.data.map { preferences->
        preferences[keyRefreshToken]
    }
    override val accessTokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[keyAccessToken]
    }
    override val identityVerifiedFlow: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[keyIdentityVerified]
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

    override suspend fun setIdentityVerified(verified: Boolean) {
        context.dataStore.edit { preferences->
            preferences[keyIdentityVerified] = verified
        }
    }

    override suspend fun clearAccessToken() {
        context.dataStore.edit { preferences->
            preferences.remove(keyAccessToken)
        }
    }

    override suspend fun clearRefreshToken() {
         context.dataStore.edit { preferences->
             preferences.remove(keyRefreshToken)
         }
    }

    override suspend  fun clear() {
        context.dataStore.edit{ preferences->
            preferences.clear()
        }
    }


}