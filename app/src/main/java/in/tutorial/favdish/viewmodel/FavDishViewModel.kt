package `in`.tutorial.favdish.viewmodel

import androidx.lifecycle.*
import `in`.tutorial.favdish.model.database.FavDishRepository
import `in`.tutorial.favdish.model.entities.FavDish
import kotlinx.coroutines.launch

class FavDishViewModel(
    private val repository: FavDishRepository
): ViewModel() {
    fun insert(dish:FavDish) = viewModelScope.launch {
        repository.insertFavDishData(dish)
    }
    fun update(dish:FavDish) = viewModelScope.launch {
        repository.updateFavDishData(dish)
    }
    fun delete(dish: FavDish) = viewModelScope.launch {
        repository.deleteFavDishData(dish)
    }

    fun filteredDishesList(filterType:String) : LiveData<List<FavDish>> =
                            repository.filteredDishesList(filterType).asLiveData()

    val allDishesList:LiveData<List<FavDish>> = repository.allDishesList.asLiveData()

    val favoriteDishes:LiveData<List<FavDish>> = repository.favoriteDishes.asLiveData()
}

class FavDishViewModelFactory(private val repository: FavDishRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FavDishViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return FavDishViewModel(repository) as T
        }
        throw java.lang.IllegalArgumentException("Unknown ViewModel Class")
    }
}