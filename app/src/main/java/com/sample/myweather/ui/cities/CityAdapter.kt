package com.sample.myweather.ui.cities

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.mrbean355.android.EnhancedAdapter
import com.sample.myweather.R
import com.sample.myweather.utils.CommonUtils

private const val MAX_SELECTIONS = 1

class CityAdapter(private val context: Context, private val bookmark: Boolean) : EnhancedAdapter<String, CityAdapter.MyViewHolder>(
    DiffCallbacks(),
    MAX_SELECTIONS
) {

    var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater.inflate(R.layout.item_city, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dataSet = getItemAt(position)

        holder.vCity.text = CommonUtils.getInstance()?.getCityNameFromCity(dataSet)

        if(bookmark){
            holder.bookmarkIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bookmark_minus_24))
            ImageViewCompat.setImageTintList(holder.bookmarkIcon, ColorStateList.valueOf(Color.RED))
        } else {
            holder.bookmarkIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bookmark_plus_24))
            ImageViewCompat.setImageTintList(holder.bookmarkIcon, ColorStateList.valueOf(Color.GREEN))
        }

        // invoke click(s)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(dataSet)
        }

    }



    open class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var viewBackground: RelativeLayout
        lateinit var viewForeground: RelativeLayout
        var bookmarkIcon: ImageView

        var vCity: TextView

        init {
            viewBackground = view.findViewById(R.id.view_background)
            viewForeground = view.findViewById(R.id.view_foreground)
            bookmarkIcon = view.findViewById(R.id.bookmark_icon)

            vCity = view.findViewById(R.id.vCity)

        }
    }

    //for pagination (with animation)
    class DiffCallbacks : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return true
        }
    }

}
