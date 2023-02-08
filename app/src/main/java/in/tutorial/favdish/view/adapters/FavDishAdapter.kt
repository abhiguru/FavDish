package `in`.tutorial.favdish.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `in`.tutorial.favdish.databinding.ItemDishLayoutBinding
import `in`.tutorial.favdish.model.entities.FavDish
import `in`.tutorial.favdish.view.fragments.AllDishesFragment

class FavDishAdapter(
    private val fragment:Fragment
) :RecyclerView.Adapter<FavDishAdapter.ViewHolder>(){
    private var dishes:List<FavDish> = listOf()
    class ViewHolder(binding: ItemDishLayoutBinding):RecyclerView.ViewHolder(binding.root) {
        val ivDishImage = binding?.ivDishImage
        val tvDishTitle = binding?.tvDishTitle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDishLayoutBinding.inflate(
            LayoutInflater.from(fragment.context), parent, false))
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = dishes[position]
        Glide.with(fragment)
            .load(model.image)
            .centerCrop()
            .into(holder.ivDishImage!!)
        holder.tvDishTitle?.text = model.title
        holder.itemView.setOnClickListener {
            if(fragment is AllDishesFragment){
                fragment.dishDetails(model)
            }
        }
    }

    fun dishesList(list: List<FavDish>){
        dishes = list
        notifyDataSetChanged()
    }
}