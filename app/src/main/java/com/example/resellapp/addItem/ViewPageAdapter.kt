package com.example.resellapp.addItem

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.resellapp.R
import java.util.*

class ViewPageAdapter(val context: Context, val imageList: List<Uri>, var imageNumber: TextView? = null): PagerAdapter() {

    private var currentPosition = 0

    override fun getCount(): Int {
        return imageList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    @SuppressLint("MissingInflatedId")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mLayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val itemView: View = mLayoutInflater.inflate(R.layout.image_slider_item, container, false)

        val imageView: ImageView = itemView.findViewById<View>(R.id.idImage) as ImageView

        imageNumber?.let {
            it.text = "${position+1}/${imageList.size}"
        }
        Log.e("mesajNr","${imageNumber?.text}")

        Glide.with(context).load(imageList.get(position)).into(imageView)
        Objects.requireNonNull(container).addView(itemView)

        return itemView
    }


    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
        currentPosition = position
        updateImageNumber()
    }

    private fun updateImageNumber() {
        val imageNumber2 = currentPosition + 1
        val totalImages = imageList.size
        imageNumber!!.text   = "$imageNumber2/$totalImages"
    }


}