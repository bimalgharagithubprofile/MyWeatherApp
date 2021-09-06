package com.sample.myweather.ui.weather

import android.content.Context
import androidx.core.content.ContextCompat
import com.sample.myweather.R
import com.sample.myweather.data.network.response.forecasts.Day
import com.sample.myweather.databinding.ItemDayBinding
import com.sample.myweather.utils.CommonUtils
import com.xwray.groupie.databinding.BindableItem
import java.util.*


class DayItem(
    private val context: Context?,
    val dataSet: Day
) : BindableItem<ItemDayBinding>(){

    override fun getLayout() = R.layout.item_day

    override fun bind(viewBinding: ItemDayBinding, position: Int) {

        //name of the day
        viewBinding.vDay.text = CommonUtils.getDayName(dataSet.dt_txt)

        //temperature
        viewBinding.vHigh.text = ("High:  ${dataSet.main.temp_max.toInt()}" + 0x00B0.toChar())
        viewBinding.vLow.text = ("Low:  ${dataSet.main.temp_min.toInt()}" + 0x00B0.toChar())

        //icon based on weather
        context?.let {
            when {
                dataSet.weather[0].main.toLowerCase(Locale.ROOT).contains("clear") ->
                    viewBinding.vIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.icon_sunny
                        )
                    )
                dataSet.weather[0].main.toLowerCase(Locale.ROOT).contains("cloud") ->
                    viewBinding.vIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.icon_cloudy
                        )
                    )
                dataSet.weather[0].main.toLowerCase(Locale.ROOT).contains("rain") ->
                    viewBinding.vIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.icon_rainy
                        )
                    )
            }
        }
    }

}
