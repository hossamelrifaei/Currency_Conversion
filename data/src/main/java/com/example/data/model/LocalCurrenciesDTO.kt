package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalCurrenciesDTO(
    @PrimaryKey val id: String,
    @ColumnInfo val rate: Double
)