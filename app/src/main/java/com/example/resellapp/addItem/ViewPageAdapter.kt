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


        // on below line we are inflating our custom
        // layout file which we have created.
        val itemView: View = mLayoutInflater.inflate(R.layout.image_slider_item, container, false)

        // on below line we are initializing
        // our image view with the id.
        val imageView: ImageView = itemView.findViewById<View>(R.id.idImage) as ImageView
//        val textView: TextView = itemView.findViewById<TextView>(R.id.imageNumber)
//        textView.text = "${position+1}/${imageList.size}"



        imageNumber?.let {
            it.text = "${position+1}/${imageList.size}"
        }
        Log.e("mesajNr","${imageNumber?.text}")

        // on below line we are setting
        // image resource for image view.
        Glide.with(context).load(imageList.get(position)).into(imageView)
        // on the below line we are adding this
        // item view to the container.
        Objects.requireNonNull(container).addView(itemView)

        // on below line we are simply
        // returning our item view.
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