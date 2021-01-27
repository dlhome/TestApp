package com.example.testapp.ui

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.testapp.R
import com.example.testapp.databinding.EditFieldFragmentBinding
import com.example.testapp.model.MainViewModel
import com.example.testapp.model.PersonData
import java.lang.IllegalArgumentException
import java.util.*

const val MY_CAMERA_PERMISSION_CODE = 100
const val MY_STORAGE_PERMISSION_CODE =101
const val  CAMERA_REQUEST = 1001
const val  GALLERY_REQUEST = 1002

class EnterFieldFragment: Fragment() {
    companion object {
        private val ARG_POS = "arg_pos"
        fun instance(page : Int):EnterFieldFragment  {
            val fragment = EnterFieldFragment()
            val b = bundleOf(ARG_POS to page)
            fragment.arguments = b
            return fragment
        }
    }

    lateinit var bind : EditFieldFragmentBinding
    private var page: Int = -1
    private var editPersonData: PersonData? = null

    private var isKg: Boolean = true
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            page = it.getInt(ARG_POS, -1)
        }
        if (page == -1) throw IllegalArgumentException("Page can't be -1")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = EditFieldFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.editPersonLivedata.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            editPersonData = it.second
            updateUI(page)
        }
        bind.dateLayout.hint = requireContext().resources.getStringArray(R.array.stepLabels)[page]
        updateUI(page)
    }

    private fun updateUI(page: Int) {
        when(page) {
            0 -> {
                bind.dateLayout.suffixText = "${getString(R.string.kgs)}/${getString(R.string.lbs)}"
                bind.dateLayout.startIconDrawable = null
                bind.radioGroup.visibility = View.VISIBLE
                bind.imageLayout.visibility = View.GONE
                bind.dateText.setText("")
                bind.dateText.requestFocus()
                editPersonData?.let {
                    isKg = it.isKgUnits
                    bind.dateText.setText(it.weight.toString())
                    bind.radioGroup.check(if (it.isKgUnits) R.id.kgRadio else R.id.lbRadio)
                }

                bind.dateText.doAfterTextChanged {tx->
                    val w = tx.toString().toDoubleOrNull()
                    w?.let {  viewModel.setWeight(it, isKg) }
                }

                bind.radioGroup.setOnCheckedChangeListener { _, id ->
                    isKg = (id == R.id.kgRadio)
                    val w = bind.dateText.text.toString().toDoubleOrNull()
                    w?.let { viewModel.setWeight(it, isKg) }
                }

            }
            1-> {//date
                bind.dateLayout.suffixText = getText(R.string.date)
                bind.radioGroup.visibility = View.GONE
                bind.imageLayout.visibility = View.GONE
                bind.dateText.setText(dateToStr(dob))
                bind.dateText.isFocusableInTouchMode = false
                hideKeyboardFrom(requireContext(), requireView())
                editPersonData?.let {dob = it.dob; bind.dateText.setText(dateToStr(dob))}
                bind.dateText.setOnClickListener{getDate()}
            }
            2 -> {//photo
                bind.dateLayout.visibility = View.GONE
                bind.imageLayout.visibility = View.VISIBLE
                editPersonData?.let{
                    showPhoto(it.image)
                }
                bind.takePhoto.setOnClickListener {takePhoto()
                }
                bind.getPhoto.setOnClickListener {
                    chooseImage()
                }
            }
        }
    }
    private fun showPhoto(path: String) {
        bind.photoView.visibility = View.VISIBLE
        Glide.with(requireContext()).load(path).into(bind.photoView);
    }

    private fun takePhoto() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_PERMISSION_CODE);
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST)
        }
    }

    private fun chooseImage() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_STORAGE_PERMISSION_CODE
            )
            return
        }
        chooseImageGranted()
    }

    private fun chooseImageGranted() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"

        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"

        val chooserIntent = Intent.createChooser(getIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        startActivityForResult(chooserIntent, GALLERY_REQUEST)
    }

    var  dob = Date()
    private fun getDate() {
        val cl = Calendar.getInstance()
        cl.time = dob
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                cl.set(year, month, dayOfMonth)
                dob = cl.time
                bind.dateText.setText(dateToStr(dob))
                viewModel.setDob(dob)

            },
            cl.get(Calendar.YEAR),
            cl.get(Calendar.MONTH),
            cl.get(Calendar.DAY_OF_MONTH)
        )
        .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
        } else if (requestCode == MY_STORAGE_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImageGranted()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)  {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val photo = data?.extras!!["data"] as Bitmap?
            photo?.let {
                var bmp = Bitmap.createScaledBitmap(it, 1024, 1024, true)
                val path = "${requireContext().filesDir.absoluteFile}/photo${it.hashCode()}.jpg"
                bmp = rotateImage(bmp, 90f)
                saveBitmapToFile(bmp, path)
                viewModel.setPhoto(path)
                showPhoto(path)
            }
        }
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri = data?.data!!
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            val cursor = requireContext().contentResolver.query(
                selectedImage,
                filePathColumn, null, null, null
            )
            var picturePath: String = ""
            cursor?.let {
                it.moveToFirst()
                val columnIndex = it.getColumnIndex(filePathColumn[0])
                picturePath  = it.getString(columnIndex)
                it.close()
            }
            val photo = BitmapFactory.decodeFile(picturePath)
            photo?.let {
                val bmp = Bitmap.createScaledBitmap(it, 1000, 1000, true)
                val path = "${requireContext().filesDir.absoluteFile}/photo${it.hashCode()}.jpg"
                saveBitmapToFile(bmp, path)
                viewModel.setPhoto(path)
                showPhoto(path)
            }

        }

    }

    fun updateData() {
        editPersonData?.let {
            when (page) {
                0 -> if (isKg != it.isKgUnits) viewModel.setWeight(it.weight, isKg)
            }
        }
    }


}