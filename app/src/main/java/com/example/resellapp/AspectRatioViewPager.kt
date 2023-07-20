package com.example.resellapp

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

class AspectRatioViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth
        val height = width * 4 / 3

        setMeasuredDimension(width, height)
    }
}