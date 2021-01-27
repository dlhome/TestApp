package com.example.testapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.testapp.MainActivity
import com.example.testapp.R
import com.example.testapp.adapter.PagerAdapter
import com.example.testapp.databinding.EnterDataFragmentBinding


class EnterDataFragment: Fragment() {

    lateinit var bind : EnterDataFragmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback{
            startActivity(MainActivity.intentOf(requireContext()))
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = EnterDataFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.enter_data)

        val adapter = PagerAdapter(requireContext(), childFragmentManager)
        bind.pager.adapter = adapter

        bind.indicator.setViewPager(bind.pager, false)
        bind.indicator.addOnStepClickListener {
            step ->
                    activity?.title = adapter.getPageTitle(step)
                    bind.pager.setCurrentItem(step, true)
        }
        activity?.title = adapter.getPageTitle(bind.pager.currentItem)
        bind.nextBtn.setOnClickListener {
            val pos = bind.pager.currentItem
            val f = adapter.getItem(pos) as EnterFieldFragment
            f?.updateData()
            bind.pager.currentItem++
            activity?.title = adapter.getPageTitle(bind.pager.currentItem)
            if (bind.nextBtn.text==getString(R.string.finish)) {
                startActivity(MainActivity.intentOf(requireContext()))
            }
            if (bind.pager.currentItem == adapter.count-1) {
                bind.nextBtn.setText(R.string.finish)
            }
        }

        bind.backBtn.setOnClickListener {
            if (bind.pager.currentItem == 0) startActivity(MainActivity.intentOf(requireContext()))
            bind.pager.currentItem--
            activity?.title = adapter.getPageTitle(bind.pager.currentItem)
        }
    }

}