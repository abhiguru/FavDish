package `in`.tutorial.favdish.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import `in`.tutorial.favdish.application.FavDishApplication
import `in`.tutorial.favdish.databinding.FragmentFavoriteDishesBinding
import `in`.tutorial.favdish.model.entities.FavDish
import `in`.tutorial.favdish.view.activities.MainActivity
import `in`.tutorial.favdish.view.adapters.FavDishAdapter
import `in`.tutorial.favdish.viewmodel.DashboardViewModel
import `in`.tutorial.favdish.viewmodel.FavDishViewModel
import `in`.tutorial.favdish.viewmodel.FavDishViewModelFactory

class FavoriteDishesFragment : Fragment() {
    private var binding: FragmentFavoriteDishesBinding? = null

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFavDishViewModel.favoriteDishes.observe(viewLifecycleOwner){
            dishes->
            dishes.let {
                if(it.isNotEmpty()){
                    val adapter = FavDishAdapter(this@FavoriteDishesFragment)
                    binding?.rvDishesList?.layoutManager = GridLayoutManager(context, 2)
                    binding?.rvDishesList?.adapter = adapter
                    adapter.dishesList(it)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun dishDetails(model: FavDish) {
        findNavController().navigate(
            FavoriteDishesFragmentDirections.actionFavoriteDishesToDishDetail(model)
        )
        if(requireActivity() is MainActivity){
            (activity as MainActivity)!!.hideBottomNavigationView()
        }
    }

    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity){
            (activity as MainActivity)!!.showBottomNavigationView()
        }
    }
}