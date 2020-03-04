package com.nagarro.poptvworkshop.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nagarro.poptvworkshop.R
import com.nagarro.poptvworkshop.databinding.ShowDetailFragmentBinding
import com.nagarro.poptvworkshop.domain.CarousalMediaItem
import com.nagarro.poptvworkshop.domain.CastItem
import com.nagarro.poptvworkshop.domain.MediaItem
import com.nagarro.poptvworkshop.network.PopTvApiStatus
import com.nagarro.poptvworkshop.ui.main.component.carousal.CarousalType
import com.nagarro.poptvworkshop.ui.main.component.carousal.CarousalView
import com.nagarro.poptvworkshop.ui.main.component.carousal.OnMediaClickListener
import com.nagarro.poptvworkshop.ui.main.viewmodel.ShowDetailsViewModel
import com.nagarro.poptvworkshop.utils.BUNDLE_MEDIA_ITEM

class ShowDetailFragment : Fragment() {

    private lateinit var bundleItem: MediaItem
    private val viewModel: ShowDetailsViewModel by lazy {
        ViewModelProvider(
            this, ShowDetailsViewModel
                .Factory(bundleItem.id.toString(), context!!)
        ).get(ShowDetailsViewModel::class.java)
    }

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
        val binding = ShowDetailFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this


        arguments?.let {

            bundleItem = it.getParcelable(BUNDLE_MEDIA_ITEM)!!
        }

        /*update Binding variables in the xml*/
        binding.mediaItem = bundleItem
        binding.viewModel = viewModel

        prepareAndAddCarousals(binding)

        observeViewModel(binding)


        return binding.root
    }

    /**
     * Prepares the carousals. It initially loads the Views on the holder Linear Parent.
     * But if the fragment view was destroyed, as what BottomNavigationView does, we try
     * to reload the previous view state.
     */
    private fun prepareAndAddCarousals(binding: ShowDetailFragmentBinding) {
        val holder = binding.detailBottomParent
        if (parentCarousal?.childCount == 0) {
            initializeCarousals(parentCarousal)
            holder.addView(parentCarousal)
        } else {
            (parentCarousal?.parent as ViewGroup).removeView(parentCarousal)
            holder.addView(parentCarousal)
        }
    }


    /**
     * Adds carousals in this view
     */
    private fun initializeCarousals(main: LinearLayout?) {
        main?.removeAllViews()

        /*This is the click listener for all the carousals
        navigates to the detail page of the clicked item or shows the Toast*/
        val onClickListener = OnMediaClickListener { item ->
            if (item is MediaItem) {
                val bundle = Bundle()
                bundle.putParcelable(BUNDLE_MEDIA_ITEM, item)
                findNavController().navigate(R.id.action_nav_detail_to_nav_detail, bundle)
            } else {
                Toast.makeText(context, (item as CastItem).name, Toast.LENGTH_SHORT).show()
            }
        }

        //Carousal for Movie Cast
        val carousalViewCelebrity = CarousalView(
            context,
            CarousalType.ROUNDED,
            CarousalMediaItem(
                getString(R.string.car_label_cast),
                viewModel.carousalCastList.value,
                true
            ),
            onClickListener
        )

        //Carousal for recommendations
        val carousalViewRandom =
            CarousalView(
                context,
                CarousalType.PORTRAIT,
                CarousalMediaItem(
                    getString(R.string.car_label_suggestion),
                    viewModel.carousalRandomList.value,
                    true
                ),
                onClickListener
            )

        main?.addView(carousalViewCelebrity.get())// Add this movie's cast
        main?.addView(carousalViewRandom.get())//add suggestions

        viewModel.carousalRandomList.observe(viewLifecycleOwner, Observer { list ->
            list?.apply {
                carousalViewRandom.updateItems(list)
            }
        })

        viewModel.carousalCastList.observe(viewLifecycleOwner, Observer { list ->
            list?.apply {
                carousalViewCelebrity.updateItems(list)
            }
        })
    }

    /**
     * register other observers to view model
     */
    private fun observeViewModel(binding: ShowDetailFragmentBinding) {

        /*Movie details have arrived from API*/
        viewModel.movieDetailsData.observe(viewLifecycleOwner, Observer { itemDetail ->
            itemDetail?.let {
                binding.mediaItem = it
            }
        })

        /*Updated in the watchlist*/
        viewModel.isAddedInWatchlist.observe(viewLifecycleOwner, Observer {
            binding.isInWatchlist = it
        })

        /*Updated in the download list*/
        viewModel.isDownloaded.observe(viewLifecycleOwner, Observer {
            binding.isInDownloads = it
        })

        /*Cast API call status ERROR/SUCCESS*/
        viewModel.castApiStatus.observe(viewLifecycleOwner, Observer { status ->
            if (status == PopTvApiStatus.ERROR) {
                binding.isApiError = true
            }
        })

    }
}