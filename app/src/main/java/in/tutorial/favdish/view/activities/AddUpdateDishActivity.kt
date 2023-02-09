package `in`.tutorial.favdish.view.activities

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import `in`.tutorial.favdish.R
import `in`.tutorial.favdish.application.FavDishApplication
import `in`.tutorial.favdish.databinding.ActivityAddUpdateDishBinding
import `in`.tutorial.favdish.databinding.DialogCustomImageSelectionBinding
import `in`.tutorial.favdish.databinding.DialogCustomListBinding
import `in`.tutorial.favdish.model.entities.FavDish
import `in`.tutorial.favdish.utils.Constants
import `in`.tutorial.favdish.view.adapters.CustomListItemAdapter
import `in`.tutorial.favdish.viewmodel.FavDishViewModel
import `in`.tutorial.favdish.viewmodel.FavDishViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {
    var binding: ActivityAddUpdateDishBinding? = null
    private var imagePath : String = ""
    private lateinit var customListDialog: Dialog
    private val mFavDishViewModel : FavDishViewModel by viewModels {
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }
    private var mFavDishDetails: FavDish? = null
    val resLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
            if(result.resultCode == Activity.RESULT_OK){
                result.data?.let {
                    val thumb : Bitmap =  it.extras?.get("data") as Bitmap
                    imagePath = saveImgToInternalStore(thumb)
                    Glide.with(this)
                        .load(imagePath)
                        .centerCrop()
                        .into(binding?.ivDishImage!!)
                    Log.i("imagePath", imagePath)
                    binding?.ivAddDishImage?.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.ic_vector_edit))
                }
            }
        }
    val resLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
            if(result.resultCode == Activity.RESULT_OK){
                result.data?.let {
                    val selPhotoUri = it.data
                    Glide.with(this)
                        .load(selPhotoUri)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object: RequestListener<Drawable>{
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.i("imagePath", "Error loading")
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                resource?.let { it ->
                                    val bitmap:Bitmap = it.toBitmap()
                                    imagePath = saveImgToInternalStore(bitmap)
                                    Log.i("imagePath2", imagePath)
                                }
                                return false
                            }

                        })
                        .into(binding?.ivDishImage!!)
                    binding?.ivAddDishImage?.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.ic_vector_edit))
                }
            }else if(result.resultCode == Activity.RESULT_CANCELED){
                //Log ?
            }
        }
    companion object{
        private const val IMAGE_DIRECTORY = "FavDishImage"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        if(intent.hasExtra(Constants.EXTRA_DISH_DETAILS)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mFavDishDetails = intent.getParcelableExtra(Constants.EXTRA_DISH_DETAILS, FavDish::class.java)
            }else{
                @Suppress("DEPRECATION")
                mFavDishDetails = intent.getParcelableExtra(Constants.EXTRA_DISH_DETAILS)
            }
        }
        setupActionBar()
        mFavDishDetails?.let {
            if(it.id!=0){
               imagePath = it.image
                Glide.with(this)
                    .load(it.image)
                    .centerCrop()
                    .into(binding!!.ivDishImage)
                binding?.etTitle?.setText(it.title)
                binding?.etType?.setText(it.type)
                binding?.etIngredients?.setText(it.ingredients)
                binding?.etCategory?.setText(it.category)
                binding?.etCookingTime?.setText(it.cookingTime)
                binding?.etDirectionToCook?.setText(it.directionToCook)
                binding?.btnAddDish?.text = resources.getString(R.string.lbl_update_dish)
            }
        }
        binding?.ivAddDishImage?.setOnClickListener(this)
        binding?.etType?.setOnClickListener(this)
        binding?.etCategory?.setOnClickListener(this)
        binding?.etCookingTime?.setOnClickListener(this)
        binding?.btnAddDish?.setOnClickListener(this)
    }
    private fun setupActionBar(){
        setSupportActionBar(binding!!.toolbarAddDishActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(mFavDishDetails!=null && mFavDishDetails!!.id != 0){
            supportActionBar?.let {
                it.title = resources.getString(R.string.title_edit_dish)
            }
        }else{
            supportActionBar?.let {
                it.title = resources.getString(R.string.title_add_dish)
            }
        }
        binding?.toolbarAddDishActivity!!.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    override fun onClick(v: View?) {
        v?.let {
            when(v.id){
                R.id.iv_add_dish_image->{
                    customImageSelectionDialog()
                    return
                }
                R.id.et_category->{
                    customItemsDialog(getString(R.string.title_select_dish_category),
                        Constants.dishCategories(), Constants.DISH_CATEGORY)
                    return
                }
                R.id.et_type->{
                    customItemsDialog(getString(R.string.title_select_dish_type),
                                            Constants.dishTypes(), Constants.DISH_TYPE)
                    return
                }
                R.id.et_cooking_time->{
                    customItemsDialog(getString(R.string.title_select_dish_cooking_time),
                        Constants.dishCookTime(), Constants.DISH_COOKING_TIME)
                    return
                }
                R.id.btn_add_dish->{
                    val title = binding?.etTitle?.text.toString().trim{it <= ' '}
                    val type = binding?.etType?.text.toString().trim{it <= ' '}
                    val category = binding?.etCategory?.text.toString().trim{it <= ' '}
                    val ingredients = binding?.etIngredients?.text.toString().trim{it <= ' '}
                    val cookingTimeInMinutes = binding?.etCookingTime?.text.toString().trim{it <= ' '}
                    val cookingDirection = binding?.etDirectionToCook?.text.toString().trim{it <= ' '}
                    when{
                        TextUtils.isEmpty(imagePath)->{
                            Toast.makeText(this@AddUpdateDishActivity,
                                "Image not selected", Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(title)->{
                            Toast.makeText(this@AddUpdateDishActivity,
                                "Title not entered", Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(type)->{
                            Toast.makeText(this@AddUpdateDishActivity,
                                "Type not entered", Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(category)->{
                            Toast.makeText(this@AddUpdateDishActivity,
                                "Category not entered", Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(ingredients)->{
                            Toast.makeText(this@AddUpdateDishActivity,
                                "Ingredients not entered", Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(cookingTimeInMinutes)->{
                            Toast.makeText(this@AddUpdateDishActivity,
                                "Cooking times not entered", Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(cookingDirection)->{
                            Toast.makeText(this@AddUpdateDishActivity,
                                "Cooking direction not entered", Toast.LENGTH_SHORT).show()
                        }
                        else->{
                            var dishId = 0
                            var imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL
                            var favoriteDish = false
                            mFavDishDetails?.let {
                                if(it.id!=0){
                                    dishId = it.id
                                    imageSource = it.imageSource
                                    favoriteDish = it.favoriteDish
                                }
                            }
                            val favDishDetails:FavDish = FavDish(
                                imagePath,
                                imageSource,
                                title,
                                type,
                                category,
                                ingredients,
                                cookingTimeInMinutes,
                                cookingDirection,
                                favoriteDish = favoriteDish,
                                id = dishId
                            )
                            if(dishId == 0){
                                mFavDishViewModel.insert(favDishDetails)
                                Toast.makeText(this@AddUpdateDishActivity,
                                    "You have created dish", Toast.LENGTH_SHORT).show()
                                Log.e("Insertion","Success")
                            }else{
                                mFavDishViewModel.update(favDishDetails)
                                Toast.makeText(this@AddUpdateDishActivity,
                                    "You have update dish", Toast.LENGTH_SHORT).show()
                                Log.e("Updation","Success")
                            }
                            finish()
                        }
                    }
                }
                else -> {
                    return
                }
            }
        }
    }
    private fun customImageSelectionDialog() {
        val imageDialog = Dialog(this@AddUpdateDishActivity)
        val binding: DialogCustomImageSelectionBinding =
            DialogCustomImageSelectionBinding.inflate(layoutInflater)
        imageDialog.setContentView(binding.root)
        binding.tvCamera.setOnClickListener {
            Dexter.withContext(this@AddUpdateDishActivity)
                .withPermissions(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
                )
                .withListener(object: MultiplePermissionsListener{
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if(report.areAllPermissionsGranted()){
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                resLauncher.launch(intent)
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }

                }).onSameThread().check();
            imageDialog.dismiss()
        }
        binding.tvGallery.setOnClickListener {
            Dexter.withContext(this@AddUpdateDishActivity)
                .withPermission(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(object: PermissionListener{
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        resLauncherGallery.launch(galleryIntent)
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(this@AddUpdateDishActivity,
                            "You have denied perms.", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }


                }).onSameThread().check();
            imageDialog.dismiss()
        }
        imageDialog.show()
    }
    private fun saveImgToInternalStore(bitmap: Bitmap):String{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        try {
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            return file.absolutePath.toString()
        }catch (e:IOException){
            e.printStackTrace()
        }
        return ""

    }
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
    private fun customItemsDialog(title:String,itemsList: List<String>, selection:String){
        customListDialog = Dialog(this)
        val binding:DialogCustomListBinding =
            DialogCustomListBinding.inflate(layoutInflater)
        customListDialog.setContentView(binding.root)
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(this)
        val adapter = CustomListItemAdapter(this, null, itemsList, selection)
        binding.rvList.adapter = adapter
        customListDialog.show()
    }
    fun onListItemSelected(item:String, selection:String){
        when(selection){
            Constants.DISH_TYPE->{
                customListDialog.dismiss()
                binding?.etType?.setText(item)
            }
            Constants.DISH_CATEGORY->{
                customListDialog.dismiss()
                binding?.etCategory?.setText(item)
            }
            else->{
                customListDialog.dismiss()
                binding?.etCookingTime?.setText(item)
            }
        }
    }
}