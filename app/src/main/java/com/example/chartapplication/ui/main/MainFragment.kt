package com.example.chartapplication.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chartapplication.R
import com.example.chartapplication.databinding.FragmentMainBinding
import com.example.chartapplication.ui.adapter.ChartsAdapter
import com.example.chartapplication.utils.jsonFromAssets
import com.google.android.material.tabs.TabLayoutMediator

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var pagerAdapter: ChartsAdapter

    companion object {
        private const val LINE_CHART_POSITION = 0
        private const val CANDLE_CHART_POSITION = 1

        private const val WEEK_CHART_PATH = "response_quotes_week.json"
        private const val MONTH_CHART_PATH = "response_quotes_month.json"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPager()
    }

    private fun setupPager() {
        pagerAdapter = ChartsAdapter(this).apply {
            setChartsData(
                requireContext().jsonFromAssets(WEEK_CHART_PATH),
                requireContext().jsonFromAssets(MONTH_CHART_PATH)
            )
        }
        binding.chartsPager.adapter = pagerAdapter

        setupLayoutMediator()
    }

    private fun setupLayoutMediator() {
        TabLayoutMediator(binding.tabLayout, binding.chartsPager) { tab, position ->
            when (position) {
                LINE_CHART_POSITION -> tab.text = getString(R.string.tab_line_chart_title)
                CANDLE_CHART_POSITION -> tab.text = getString(R.string.tab_candle_chart_title)
            }
        }.attach()
    }
}