package `in`.tutorial.favdish.view.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import `in`.tutorial.favdish.R
import `in`.tutorial.favdish.application.FavDishApplication
import `in`.tutorial.favdish.databinding.FragmentRandomDishBinding
import `in`.tutorial.favdish.model.entities.FavDish
import `in`.tutorial.favdish.model.entities.RandomDish
import `in`.tutorial.favdish.utils.Constants
import `in`.tutorial.favdish.viewmodel.FavDishViewModel
import `in`.tutorial.favdish.viewmodel.FavDishViewModelFactory
import `in`.tutorial.favdish.viewmodel.NotificationsViewModel
import `in`.tutorial.favdish.viewmodel.RandomDishViewModel

class RandomDishFragment : Fragment() {

    private var binding: FragmentRandomDishBinding? = null
    private lateinit var mRandomDishViewModel: RandomDishViewModel
    private var progressDialog: Dialog? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRandomDishViewModel =
            ViewModelProvider(this).get(RandomDishViewModel::class.java)
        mRandomDishViewModel.getRandomRecipeFromAPI()
        randomDishViewModelObserver()
        binding?.srlRandomDish?.setOnRefreshListener {
            mRandomDishViewModel.getRandomRecipeFromAPI()
        }
    }
    private fun showCustomProgressDialog(){
        progressDialog = Dialog(requireActivity())
        progressDialog?.let {
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }
    private fun hideProgressDialog(){
        progressDialog?.let {
            it.dismiss()
        }
    }
    private fun randomDishViewModelObserver(){
        mRandomDishViewModel.randomDishResponse.observe(viewLifecycleOwner) {
        randomDishResponse ->
            randomDishResponse?.let {
                if(binding!!.srlRandomDish.isRefreshing){
                    binding!!.srlRandomDish.isRefreshing = false
                }
                setRandomDishResponseInUI(randomDishResponse.recipes[0])
            }
        }
        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner){
        loadRandomDish ->
            loadRandomDish?.let {
                if(it && !binding!!.srlRandomDish.isRefreshing){
                    showCustomProgressDialog()
                }else{
                    hideProgressDialog()
                }
            }
        }
        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner){
        error ->
            error?.let {
                if(binding!!.srlRandomDish.isRefreshing){
                    binding!!.srlRandomDish.isRefreshing = false
                }
                Log.e("Random Dish Response", "Error $error")
            }
        }
    }
    private fun setRandomDishResponseInUI(recipe: RandomDish.Recipe) {

        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(binding?.ivDishImage!!)
        binding?.tvTitle?.text = recipe.title
        var dishType: String = "other"
        if (recipe.dishTypes.isNotEmpty()) {
            dishType = recipe.dishTypes[0].toString()
            binding?.tvType?.text = dishType
        }
        binding?.tvCategory?.text = "Other"
        var ingredient = "";
        for (value in recipe.extendedIngredients) {
            if (ingredient.isEmpty()) {
                ingredient = value.original
            } else {
                ingredient = ingredient + " , \n" + value.original
            }
        }
        binding?.tvIngredients?.text = ingredient
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding!!.tvCookingDirection.text = Html.fromHtml(
                recipe.instructions,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            binding!!.tvCookingDirection.text = Html.fromHtml(recipe.instructions)
        }

        binding!!.ivFavoriteDish.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_favorite_unselected
            )
        )
        var addedToFav = false

        binding!!.tvCookingTime.text =
            resources.getString(
                R.string.lbl_estimate_cooking_time,
                recipe.readyInMinutes.toString()
            )

        binding!!.ivFavoriteDish.setOnClickListener {
            if(addedToFav){
                Toast.makeText(requireActivity(),"Already Fav", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val randomDishDetails = FavDish(
                recipe.image,
                Constants.DISH_IMAGE_SOURCE_ONLINE,
                recipe.title,
                dishType,
                "Other",
                ingredient,
                recipe.readyInMinutes.toString(),
                recipe.instructions,
                true
            )
            val mFavDishViewModel: FavDishViewModel by viewModels {
                FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
            }
            mFavDishViewModel.insert(randomDishDetails)
            addedToFav = true
            binding!!.ivFavoriteDish.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_favorite_selected
                )
            )
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRandomDishBinding.inflate(inflater, container, false)

        return binding!!.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}