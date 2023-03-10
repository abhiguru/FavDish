package `in`.tutorial.favdish.view.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.tutorial.favdish.R
import `in`.tutorial.favdish.application.FavDishApplication
import `in`.tutorial.favdish.databinding.DialogCustomListBinding
import `in`.tutorial.favdish.databinding.FragmentAllDishesBinding
import `in`.tutorial.favdish.model.entities.FavDish
import `in`.tutorial.favdish.utils.Constants
import `in`.tutorial.favdish.view.activities.AddUpdateDishActivity
import `in`.tutorial.favdish.view.activities.MainActivity
import `in`.tutorial.favdish.view.adapters.CustomListItemAdapter
import `in`.tutorial.favdish.view.adapters.FavDishAdapter
import `in`.tutorial.favdish.viewmodel.FavDishViewModel
import `in`.tutorial.favdish.viewmodel.FavDishViewModelFactory
import `in`.tutorial.favdish.viewmodel.HomeViewModel

class AllDishesFragment : Fragment() {

    private var mbinding: FragmentAllDishesBinding? = null
    private val mFavDishViewModel:FavDishViewModel by viewModels{
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }
    private lateinit var mFavDishAdapter: FavDishAdapter
    private lateinit var mCustomListDialog:Dialog

    fun filterSelection(filterItemSelection : String){
        mCustomListDialog.dismiss()
        Log.i("Filter Selection", filterItemSelection)
        if(filterItemSelection == Constants.ALL_ITEMS){
            mFavDishViewModel.allDishesList.observe(viewLifecycleOwner){
                dishes->
                dishes.let {
                    if(it.isNotEmpty()){
                        mbinding?.rvDishesList?.visibility = View.VISIBLE
                        mbinding?.tvNoDishesAddedYet?.visibility = View.GONE
                        mFavDishAdapter.dishesList(it)
                    }else{
                        mbinding?.rvDishesList?.visibility = View.GONE
                        mbinding?.tvNoDishesAddedYet?.visibility = View.VISIBLE
                    }
                }
            }
        }else{
            mFavDishViewModel.filteredDishesList(filterItemSelection).observe(viewLifecycleOwner){
                dishes->
                dishes.let {
                    if(it.isNotEmpty()){
                        mbinding?.rvDishesList?.visibility = View.VISIBLE
                        mbinding?.tvNoDishesAddedYet?.visibility = View.GONE
                        mFavDishAdapter.dishesList(it)
                    }else{
                        mbinding?.rvDishesList?.visibility = View.GONE
                        mbinding?.tvNoDishesAddedYet?.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
    private fun filterDishesListDialog(){
        mCustomListDialog = Dialog(requireActivity())
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)
        val dishTypes = Constants.dishTypes()
        dishTypes.add(0, Constants.ALL_ITEMS)
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = CustomListItemAdapter(requireActivity(), this@AllDishesFragment,
                                            dishTypes, Constants.FILTER_SELECTION)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
    }
    fun deleteDish(dish: FavDish){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.title_delete_dish))
        builder.setMessage(resources.getString(R.string.msg_delete_dish_dialog, dish.title))
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(resources.getString(R.string.lbl_yes)){ dialogInterface, _ ->
            mFavDishViewModel.delete(dish)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.lbl_no)){ dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog:AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    fun dishDetails(favDish: FavDish){
        findNavController().navigate(
            AllDishesFragmentDirections.actionAllDishesToDishDetails(favDish)
        )
        if(requireActivity() is MainActivity){
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mbinding?.rvDishesList?.layoutManager = GridLayoutManager(requireActivity(), 2)
        mFavDishAdapter = FavDishAdapter(this@AllDishesFragment)
        mbinding?.rvDishesList?.adapter = mFavDishAdapter
        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner){
            dishes->
                dishes.let {
                    if(it.isNotEmpty()){
                        mbinding?.rvDishesList?.visibility = View.VISIBLE
                        mbinding?.tvNoDishesAddedYet?.visibility = View.GONE
                        mFavDishAdapter.dishesList(it)
                    }else{
                        mbinding?.rvDishesList?.visibility = View.GONE
                        mbinding?.tvNoDishesAddedYet?.visibility = View.VISIBLE
                    }
                }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //https://stackoverflow.com/questions/71917856/sethasoptionsmenuboolean-unit-is-deprecated-deprecated-in-java
        val menuHost:MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_all_dishes, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.action_add_dish->{
                        startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                        return true
                    }
                    R.id.action_filter_dishes->{
                        filterDishesListDialog()
                        return true
                    }
                }
                return false
            }
        })
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mbinding = FragmentAllDishesBinding.inflate(inflater, container, false)
        return mbinding!!.root
    }
    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity){
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mbinding = null
    }
}