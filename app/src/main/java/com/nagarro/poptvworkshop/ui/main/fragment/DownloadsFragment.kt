package com.nagarro.poptvworkshop.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nagarro.poptvworkshop.R
import com.nagarro.poptvworkshop.databinding.ComingSoonFragmentBinding

class DownloadsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ComingSoonFragmentBinding.inflate(inflater)
        binding.title = getString(R.string.label_nav_downloads)

        return binding.root
    }
}