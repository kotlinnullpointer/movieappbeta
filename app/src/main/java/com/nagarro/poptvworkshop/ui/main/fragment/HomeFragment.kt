package com.nagarro.poptvworkshop.ui.main.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nagarro.poptvworkshop.R
import com.nagarro.poptvworkshop.databinding.HomeFragmentBinding
import com.nagarro.poptvworkshop.domain.CarousalMediaItem
import com.nagarro.poptvworkshop.domain.CastItem
import com.nagarro.poptvworkshop.domain.MediaItem
import com.nagarro.poptvworkshop.network.PopTvApiStatus
import com.nagarro.poptvworkshop.ui.main.component.carousal.CarousalType
import com.nagarro.poptvworkshop.ui.main.component.carousal.CarousalView
import com.nagarro.poptvworkshop.ui.main.component.carousal.OnMediaClickListener
import com.nagarro.poptvworkshop.ui.main.viewmodel.HomeViewModel
import com.nagarro.poptvworkshop.utils.BUNDLE_MEDIA_ITEM
import com.nagarro.poptvworkshop.utils.showToast

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    private lateinit var scrollView: NestedScrollView

    /*Initialize Parent by Lazy*/
    private val parentCarousal: LinearLayout? by lazy {
        LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = HomeFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this

        scrollView = binding.homeScroll
        binding.viewmodel = viewModel

        prepareAndAddCarousals(binding)

        addOtherObservers(binding)

        return binding.root
    }

    /**
     * Prepares the carousals. It initially loads the Views on the holder Linear Parent.
     * But if the fragment view was destroyed, as what BottomNavigationView does, we try
     * to reload the previous view state.
     */
    private fun prepareAndAddCarousals(binding: HomeFragmentBinding) {
        val holder = binding.mainHome
        if (parentCarousal?.childCount == 0) {
            initializeCarousals(parentCarousal)
            holder.addView(parentCarousal)
        } else {
            (parentCarousal?.parent as ViewGroup).removeView(parentCarousal)
            holder.addView(parentCarousal)
        }
    }

    /**
     * Adds data related observers for the Screen UI
     */
    private fun addOtherObservers(binding: HomeFragmentBinding) {

        /*An observer to navigate to the details page upon click*/
        viewModel.navigateToDetailsPage.observe(viewLifecycleOwner, Observer { item ->
            item?.let {
                val bundle = Bundle()
                bundle.putParcelable(BUNDLE_MEDIA_ITEM, item as MediaItem)
                findNavController().navigate(R.id.action_nav_home_to_nav_detail, bundle)
                viewModel.navigateToDetailsPageDone()
            }

        })

        /*Observer the API status to determine the state of Homepage UI*/
        viewModel.apiStatus.observe(viewLifecycleOwner, Observer { apiStatus ->
            apiStatus?.let {
                when (apiStatus) {
                    PopTvApiStatus.ERROR -> {
                        binding.error = true
                        binding.success = false
                        binding.cached = false
                        binding.loading = false
                    }
                    PopTvApiStatus.CACHED -> {
                        binding.cached = true
                        showToast(context as Context, getString(R.string.message_stored_cache))
                        binding.loading = false
                        binding.error = false
                    }
                    PopTvApiStatus.DONE -> {
                        binding.success = true
                        binding.loading = false
                        binding.error = false
                    }
                    PopTvApiStatus.LOADING -> {
                        binding.loading = true
                        binding.error = false
                        binding.success = false
                    }
                    else -> {
                        binding.error = true
                        binding.loading = false
                        showToast(context as Context, "No Network")
                    }
                }
            }

        })
    }

    /**
     * Adds carousals in this view
     */
    private fun initializeCarousals(parentCarousal: LinearLayout?) {

        parentCarousal?.removeAllViews()

        /*This is the click listener for all the carousals
        navigates to the detail page of the clicked item or shows the Toast*/
        val onClickListener = OnMediaClickListener { item ->
            if (item is MediaItem) {
                viewModel.navigateToDetailsPage(item)
            } else {
                Toast.makeText(context, (item as CastItem).name, Toast.LENGTH_SHORT).show()
            }
        }

        val carousalViewSciFi =
            CarousalView(
                context,
                CarousalType.TALL,
                CarousalMediaItem(getString(R.string.car_label_scifi), viewModel.carousalSciFiList.value, true),
                onClickListener
            )

        val carousalViewLatest = CarousalView(
            context,
            CarousalType.PORTRAIT,
            CarousalMediaItem(getString(R.string.car_label_latest), viewModel.carousalLatestList.value, true),
            onClickListener
        )
        val carousalViewCelebrity = CarousalView(
            context,
            CarousalType.ROUNDED,
            CarousalMediaItem(getString(R.string.car_label_actor), viewModel.carousalCelebList.value, true),
            onClickListener
        )
        val carousalViewRomance =
            CarousalView(
                context, CarousalType.WIDE,
                CarousalMediaItem(getString(R.string.car_label_romance), viewModel.carousalRomanceList.value, true),
                onClickListener
            )

        val carousalViewAnimated =
            CarousalView(
                context, CarousalType.PORTRAIT,
                CarousalMediaItem(getString(R.string.car_label_animated), viewModel.carousalAnimatedList.value, true),
                onClickListener
            )

        parentCarousal?.addView(carousalViewLatest.get())
        parentCarousal?.addView(carousalViewSciFi.get())
        parentCarousal?.addView(carousalViewRomance.get())
        parentCarousal?.addView(carousalViewCelebrity.get())
        parentCarousal?.addView(carousalViewAnimated.get())

        viewModel.carousalLatestList.observe(viewLifecycleOwner, Observer { list ->
            list?.apply {
                carousalViewLatest.updateItems(list)
            }
        })

        viewModel.carousalSciFiList.observe(viewLifecycleOwner, Observer { list ->
            list?.apply {
                carousalViewSciFi.updateItems(list)
            }
        })
        viewModel.carousalRomanceList.observe(viewLifecycleOwner, Observer { list ->
            list?.apply {
                carousalViewRomance.updateItems(list)
            }
        })
        viewModel.carousalCelebList.observe(viewLifecycleOwner, Observer { list ->
            list?.apply {
                carousalViewCelebrity.updateItems(list)
            }
        })

        viewModel.carousalAnimatedList.observe(viewLifecycleOwner, Observer { list ->
            list?.apply {
                carousalViewAnimated.updateItems(list)
            }
        })
    }

}
