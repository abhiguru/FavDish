package `in`.tutorial.favdish.view.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import `in`.tutorial.favdish.R
import `in`.tutorial.favdish.application.FavDishApplication
import `in`.tutorial.favdish.databinding.FragmentDishDetailsBinding
import `in`.tutorial.favdish.model.entities.FavDish
import `in`.tutorial.favdish.viewmodel.FavDishViewModel
import `in`.tutorial.favdish.viewmodel.FavDishViewModelFactory
import java.io.IOException

class DishDetailsFragment : Fragment() {

    var binding : FragmentDishDetailsBinding? = null
    val mFavDishViewModel:FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDishDetailsBinding.inflate(inflater, container, false)
        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DishDetailsFragmentArgs by navArgs()
        Log.e("Dish Title", args.dishDetails.title)
        Log.e("Dish Type", args.dishDetails.type)
        args.let {
            try {
                Glide.with(requireActivity())
                    .load(it.dishDetails.image)
                    .centerCrop()
                    .listener(object : RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("Failed loading", "Error loading image", e)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource.let {
                                Palette.from(resource!!.toBitmap()).generate(){
                                        palette ->
                                    val intColor = palette?.vibrantSwatch?.rgb ?: 0
                                    binding?.rlDishDetailMain?.setBackgroundColor(intColor)
                                }
                            }
                            return false
                        }

                    })
                    .into(binding!!.ivDishImage)
            }   catch (e:IOException){
                e.printStackTrace()
            }
            binding?.tvTitle?.text = args.dishDetails.title
            binding?.tvType?.text = args.dishDetails.type
            binding?.tvCategory?.text = args.dishDetails.category
            binding?.tvCookingTime?.text = args.dishDetails.cookingTime
            binding?.tvCookingDirection?.text = args.dishDetails.directionToCook
            binding?.tvIngredients?.text = args.dishDetails.ingredients
            if(args.dishDetails.favoriteDish){
                binding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(requireActivity(),
                        R.drawable.ic_favorite_selected))
            }else{
                binding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(requireActivity(),
                        R.drawable.ic_favorite_unselected))
            }
        }
        binding?.ivFavoriteDish?.setOnClickListener {
            args.dishDetails.favoriteDish = !args.dishDetails.favoriteDish
            mFavDishViewModel.update(args.dishDetails)
            if(args.dishDetails.favoriteDish){
                binding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(requireActivity(), R.drawable.ic_favorite_selected))
                Toast.makeText(this@DishDetailsFragment.activity,
                    "Added to Fav", Toast.LENGTH_SHORT).show()
            }else{
                binding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(requireActivity(), R.drawable.ic_favorite_unselected))
                Toast.makeText(this@DishDetailsFragment.activity,
                    "Removed from Fav", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}