package `in`.tutorial.favdish.view.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import `in`.tutorial.favdish.databinding.ItemCustomListLayoutBinding
import `in`.tutorial.favdish.view.activities.AddUpdateDishActivity
import `in`.tutorial.favdish.view.fragments.AllDishesFragment

class CustomListItemAdapter(
    private val activity:Activity,
    private val fragment: Fragment?,
    private val listItems:List<String>,
    private val selection:String) :RecyclerView.Adapter<CustomListItemAdapter.MyViewHolder>(){
    class MyViewHolder(view:ItemCustomListLayoutBinding):RecyclerView.ViewHolder(view.root){
        val tvText = view.tvText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemCustomListLayoutBinding.inflate(
            LayoutInflater.from(activity), parent, false
        ))
    }

    override fun getItemCount() = listItems.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvText.text = item
        holder.itemView.setOnClickListener {
            if(activity is AddUpdateDishActivity){
                activity.onListItemSelected(item, selection)
            }
            if(fragment is AllDishesFragment){
                fragment.filterSelection(item)
            }
        }
    }
}