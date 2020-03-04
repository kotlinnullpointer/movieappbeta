package com.nagarro.poptvworkshop.ui.main.component.carousal

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.nagarro.poptvworkshop.databinding.MediaCarousalViewParentBinding
import com.nagarro.poptvworkshop.domain.CarousalItem
import com.nagarro.poptvworkshop.domain.CarousalMediaItem
import com.nagarro.poptvworkshop.domain.CastItem
import com.nagarro.poptvworkshop.domain.MediaItem


/*Enum to declare a type for the View*/
enum class CarousalType(id: Int) { WIDE(1), TALL(2), PORTRAIT(3), ROUNDED(4) }

class CarousalView(private val context: Context?, private val carousalType: CarousalType, private val carousalMediaItem: CarousalMediaItem, private val onMediaClickListener: OnMediaClickListener) {

    private lateinit var binding: MediaCarousalViewParentBinding

    var displayTitle = true
        set(value) {
            setTitleDisplayed(value)
        }

    private fun setTitleDisplayed(value: Boolean) {
        displayTitle = value
    }


    init {
        context?.let { prepareCarousal() }
    }

    private fun prepareCarousal() {

        val inflater = LayoutInflater.from(context)

        //init binding for this layout
        binding = MediaCarousalViewParentBinding.inflate(inflater)

        //Assign variable value for Binding adapters in xml
        binding.data = carousalMediaItem

        //Bind adapter with recycler
        binding.carousalList.adapter =
            MediaCarousalAdapter(carousalType, onMediaClickListener)

        binding.carousalList.addItemDecoration(SpacesItemDecoration(8))


    }

    fun updateItems(list: List<CarousalItem>?) {
        (binding.carousalList.adapter as MediaCarousalAdapter).submitList(list)
    }

    fun get(): View {
        return binding.root
    }


    /*A recycler Decorator to create appropriate margins amongst list items*/
    class SpacesItemDecoration(private val space: Int) : ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.right = space
            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = space
            } else {
                outRect.left = 0
            }
        }

    }
}