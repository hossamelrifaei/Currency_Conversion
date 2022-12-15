package database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.LocalCurrenciesDTO


@Dao
interface CurrenciesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<LocalCurrenciesDTO>)

    @Query("SELECT * FROM LocalCurrenciesDTO")
    suspend fun getLocalCurrencies(): List<LocalCurrenciesDTO>
}