package `in`.tutorial.favdish.view.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `in`.tutorial.favdish.R
import `in`.tutorial.favdish.databinding.ItemDishLayoutBinding
import `in`.tutorial.favdish.model.entities.FavDish
import `in`.tutorial.favdish.utils.Constants
import `in`.tutorial.favdish.view.activities.AddUpdateDishActivity
import `in`.tutorial.favdish.view.fragments.AllDishesFragment
import `in`.tutorial.favdish.view.fragments.FavoriteDishesFragment

class FavDishAdapter(
    private val fragment:Fragment
) :RecyclerView.Adapter<FavDishAdapter.ViewHolder>(){
    private var dishes:List<FavDish> = listOf()
    class ViewHolder(binding: ItemDishLayoutBinding):RecyclerView.ViewHolder(binding.root) {
        val ivDishImage = binding?.ivDishImage
        val tvDishTitle = binding?.tvDishTitle
        val ibMore = binding?.ibMore
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
            }else if(fragment is FavoriteDishesFragment){
                fragment.dishDetails(model)
            }
        }
        holder.ibMore?.setOnClickListener {
           val popup = PopupMenu(fragment.context, holder.ibMore)
           popup.menuInflater.inflate(R.menu.menu_adapter, popup.menu)
           popup.setOnMenuItemClickListener {
               if(it.itemId == R.id.action_edit_dish){
                   val intent = Intent(fragment.activity, AddUpdateDishActivity::class.java)
                   intent.putExtra(Constants.EXTRA_DISH_DETAILS, model)
                   fragment.activity?.startActivity(intent)
               }else if(it.itemId == R.id.action_delete_dish){
                   if(fragment is AllDishesFragment){
                       fragment.deleteDish(model)
                   }
               }
               true
           }
           popup.show()
        }
        if(fragment is AllDishesFragment){
            holder.ibMore?.visibility = View.VISIBLE
        }else if(fragment is FavoriteDishesFragment){
            holder.ibMore?.visibility = View.GONE
        }
    }
    fun dishesList(list: List<FavDish>){
        dishes = list
        notifyDataSetChanged()
    }
}