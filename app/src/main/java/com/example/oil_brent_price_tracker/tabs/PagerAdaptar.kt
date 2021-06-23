@file:Suppress("DEPRECATION")

package com.example.oil_brent_price_tracker.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.oil_brent_price_tracker.fragments.InfoFragment
import com.example.oil_brent_price_tracker.fragments.NewsFragment
import com.example.oil_brent_price_tracker.fragments.PredictFragment

internal  class PagerAdaptar (fm:FragmentManager?):
        FragmentPagerAdapter(fm!!){


    override fun getItem(position: Int): Fragment {
        return when(position){

            0 -> {
                PredictFragment()
            }

            1 -> {

                InfoFragment()
            }

            2 -> {
                NewsFragment()
            }
            else -> InfoFragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}