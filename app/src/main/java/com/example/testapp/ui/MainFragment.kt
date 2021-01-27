package com.example.testapp.ui

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.testapp.MainActivity
import com.example.testapp.R
import com.example.testapp.adapter.DragSimpleCallBack
import com.example.testapp.adapter.ItemClickListener
import com.example.testapp.adapter.PersonAdapter
import com.example.testapp.adapter.SwipeListener
import com.example.testapp.databinding.MainFragmentBinding
import com.example.testapp.model.MainViewModel
import com.example.testapp.model.PersonData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.*

class MainFragment : Fragment(), ItemClickListener, SwipeListener {

    companion object {
        const val ACTION_DATA_FRAGMENT="action.data_fragment"
        const val DATA_PREFERENCES = "DATA_PREFERENCES"
        const val DATA_LIST = "DATA_LIST"
        fun newInstance() = MainFragment()
    }

    private lateinit var bind : MainFragmentBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: PersonAdapter
    private lateinit var prefs: SharedPreferences
    private lateinit var itemTouchHelper: ItemTouchHelper;
    private val dataList= mutableListOf<PersonData>()
    private val gson = Gson()
    val listType = object : TypeToken<List<PersonData>>() { }.type

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = MainFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = requireContext().getSharedPreferences(DATA_PREFERENCES, Context.MODE_PRIVATE)
        val stl = prefs.getString(DATA_LIST, "")
        if (!stl.isNullOrBlank()) dataList += gson.fromJson<List<PersonData>>(stl, listType)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        activity?.title = getString(R.string.main_screen)
        activity?.actionBar?.setBackgroundDrawable(ColorDrawable(Color.GREEN));
        bind.addClient.setOnClickListener {
            viewModel.personLiveData.removeObservers(viewLifecycleOwner)
            viewModel.clearPerson()
            enterData()
        }

        viewModel.editPersonLivedata.observe(viewLifecycleOwner) {
            if (it == null || it.first < 0) return@observe
            val item =dataList[it.first]
            dataList[it.first] = it.second
            File(item.image).delete()
            saveData()
            adapter.notifyDataSetChanged()
        }

        viewModel.personLiveData.observe(viewLifecycleOwner) {
                if (it == null || it.image.isNullOrBlank() || it.weight==0.0 ) return@observe
                dataList += it
                adapter.update(dataList)
                adapter.notifyDataSetChanged()
                saveData()
        }
        adapter = PersonAdapter(this, dataList)
        bind.recyclerView.adapter = adapter
        itemTouchHelper = ItemTouchHelper(DragSimpleCallBack(this))
        itemTouchHelper.attachToRecyclerView(bind.recyclerView)
    }

    private fun enterData() {
        startActivity(MainActivity.intentOf(requireContext(), ACTION_DATA_FRAGMENT))
    }

    private fun saveData() {
        val str = gson.toJson(dataList).toString()
        prefs.edit().putString(DATA_LIST, str).apply()
    }

    override fun onItemClickListener(pos: Int) {
        viewModel.editPersonLivedata.removeObservers(viewLifecycleOwner)
        viewModel.personLiveData.removeObservers(viewLifecycleOwner)
        viewModel.clearPerson()
        viewModel.setEditPerson(pos, dataList[pos])
        enterData()
    }

    override fun onSwipe(pos: Int) {
        val item = dataList[pos]
        File(item.image).delete()
        dataList.removeAt(pos)
        adapter.update(dataList)
        adapter.notifyDataSetChanged()
        saveData()
    }

}