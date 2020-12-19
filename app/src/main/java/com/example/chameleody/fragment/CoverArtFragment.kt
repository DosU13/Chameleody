package com.example.chameleody.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.example.chameleody.R

class CoverArtFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cover_art, container, false)
    }

    private fun metaData(uri: Uri){
        val coverArt = activity?.findViewById<ImageView>(R.id.cover_art)
        val gradient = activity?.findViewById<ImageView>(R.id.imageViewGradient)
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        val art : ByteArray? = retriever.embeddedPicture
        val bitmap : Bitmap?
        if (art != null){
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.size)
            if (bitmap != null) {
                imageAnimation(coverArt, bitmap)
                Palette.from(bitmap).generate(Palette.PaletteAsyncListener {
                    val swatch = it?.dominantSwatch
                    gradient?.setBackgroundResource(R.drawable.gradient_bg)
                    if (swatch != null) {
                        val gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(swatch.rgb, 0x00000000)
                        )
                        gradient?.background = gradientDrawable
                    } else {
                        val gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(0xff000000.toInt(), 0x00000000)
                        )
                        gradient?.background = gradientDrawable
                    }
                })
            }
            else{
                coverArt?.let { Glide.with(this).asBitmap().load(R.drawable.default_art).into(it) }
                gradient?.setBackgroundResource(R.drawable.gradient_bg)
            }
        }
        else {
            coverArt?.let { Glide.with(this).asBitmap().load(R.drawable.default_art).into(it) }
            gradient?.setBackgroundResource(R.drawable.gradient_bg)
        }
    }

    private fun imageAnimation(imageView: ImageView?, bitmap: Bitmap) {
        val animOut = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out)
        val animIn = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in)
        animOut.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                imageView?.let { Glide.with(this@CoverArtFragment).load(bitmap).into(it) }
                animIn.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation?) {
                    }

                    override fun onAnimationEnd(p0: Animation?) {
                    }

                    override fun onAnimationStart(p0: Animation?) {
                    }

                })
                imageView?.startAnimation(animIn)
            }

            override fun onAnimationStart(p0: Animation?) {
            }
        })
        imageView?.startAnimation(animOut)
    }
}