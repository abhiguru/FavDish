package `in`.tutorial.favdish.application

import android.app.Application
import `in`.tutorial.favdish.model.database.FavDishRepository
import `in`.tutorial.favdish.model.database.FavDishRoomDatabase

class FavDishApplication:Application() {
    private val database by lazy {FavDishRoomDatabase.getDatabase(this@FavDishApplication)}
    val repository by lazy {FavDishRepository(database.favDishDao())}
}