package database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.model.LocalCurrenciesDTO

@Database(entities = [LocalCurrenciesDTO::class], version = 1)
abstract class CurrenciesDataBase : RoomDatabase() {
    abstract fun currenciesDao(): CurrenciesDao
}