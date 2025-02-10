package com.zaurh.bober.data.message.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zaurh.bober.data.user.room.UserDao
import com.zaurh.bober.data.user.room.UserDataConverters
import com.zaurh.bober.data.user.room.UserEntity

@Database(entities = [MessageEntity::class, UserEntity::class], version = 7)
@TypeConverters(UserDataConverters::class)

abstract class MessageDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun userDao(): UserDao
}

