package com.sample.myweather.ui.weather

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.myweather.R
import com.sample.myweather.data.network.response.forecasts.Day
import com.sample.myweather.data.network.response.forecasts.ResponseForecast
import com.sample.myweather.utils.CommonUtils
import com.sample.myweather.utils.Coroutines
import com.sample.myweather.utils.ProgressStatus
import com.sample.myweather.utils.log
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.weather_fragment.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*


class WeatherFragment : Fragment(), KodeinAware {
    override val kodein by kodein()

    companion object {
        fun newInstance(selectedCity: String): WeatherFragment {
            val fragment = WeatherFragment()

            val args = Bundle()
            args.putString("selectedCity", selectedCity)
            fragment.arguments = args

            return fragment
        }
    }

    private var selectedCity = ""

    private val factory: WeatherViewModelFactory by instance()
    private lateinit var viewModel: WeatherViewModel

    val progressStatus = MutableLiveData<ProgressStatus>()
    val dismissFragment = MutableLiveData<Boolean>()

    lateinit var responseForecast: ResponseForecast
    var selectedDay:Int = 0 //initially today

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            selectedCity = it.getString("selectedCity", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)

        context?.let { cntx ->

            getForecasts()

            btn_close.setOnClickListener {
                dismissFragment.postValue(true)
            }
            layout_Body.setOnTouchListener { v, event -> true }
        }

        //listen forecast result
        viewModel.resultForecast.observe(viewLifecycleOwner, Observer { response ->
            Log.d("chk", "Observed forecast result $response")

            responseForecast = response

            //merge by day | ignoring the advance merging for saving our time
            val lists = response.list.distinctBy { it.dt_txt.substring(0, 10) }
            log("filtered days $lists")

            responseForecast.list = lists
            log("final merged response $responseForecast")

            displayValues()
            initRecyclerView(responseForecast.list.toRVItem())

            progressStatus.postValue(ProgressStatus.COMPLETED)
        })
        //listen for api errors
        viewModel.networkError.observe(viewLifecycleOwner, Observer { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            progressStatus.postValue(ProgressStatus.COMPLETED)
            dismissFragment.postValue(true)
        })
    }

    private fun displayValues() {
        val day = responseForecast.list[selectedDay]

        vCity.text = CommonUtils.getInstance()?.getCityNameFromCity(selectedCity)

        vTime.text = CommonUtils.getInstance()?.getCurrentDateTime("hh:mm a")

        vDayName.text = CommonUtils.getInstance()?.getDayName(day.dt_txt)

        vFeelsLike.text = ("${day.main.feels_like.toInt()}" + 0x00B0.toChar())

        vHumidity.text = "${day.main.humidity} %"
        vTemperature.text = ("${day.main.temp.toInt()}" + 0x00B0.toChar())
        vWind.text = "${day.wind.speed} km/h"

        //based on weather
        //value
        vChances.text = "${day.weather.get(0).icon}"
        //icon
        when {
            day.weather[0].main.toLowerCase(Locale.ROOT).contains("clear") -> {
                //background image
                vBackgroundImage.setImageDrawable(
                    context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.bg_sunny
                        )
                    }
                )
                //value icon
                vChancesIcon.setImageDrawable(
                    context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.icn_sun
                        )
                    }
                )
            }
            day.weather[0].main.toLowerCase(Locale.ROOT).contains("cloud") -> {
                vBackgroundImage.setImageDrawable(
                    context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.bg_cloudy
                        )
                    }
                )
                //value icon
                vChancesIcon.setImageDrawable(
                    context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.icn_cloud
                        )
                    }
                )
            }
            day.weather[0].main.toLowerCase(Locale.ROOT).contains("rain") -> {
                vBackgroundImage.setImageDrawable(
                    context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.bg_rain
                        )
                    }
                )
                //value icon
                vChancesIcon.setImageDrawable(
                    context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.icn_rain
                        )
                    }
                )
            }
        }
    }

    private fun initRecyclerView(days: List<DayItem>) {
        val mAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(days)
        }

        //get device type
        val isTablet = resources.getBoolean(R.bool.isTablet)
        log("is This device a Tablet: $isTablet")
        //get Orientation
        val orientation = context?.resources?.configuration?.orientation
        //set RecyclerView orientation
        val mLayoutManager = if(isTablet)
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        else {
            if(orientation == ORIENTATION_PORTRAIT)
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            else
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        }

        rvDays.apply {
            layoutManager = mLayoutManager
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            adapter = mAdapter
        }

        mAdapter.setOnItemClickListener { item, view ->
            if (item is DayItem) {
                Log.d("chk", "clicked day ${item.dataSet}")
                selectedDay = mAdapter.getAdapterPosition(item)
                displayValues()
            }
        }
    }

    private fun getForecasts() = Coroutines.io {
        log("get forecast of $selectedCity")

        progressStatus.postValue(ProgressStatus.LOADING)

        val locationOfCity = CommonUtils.getInstance()?.getLocationFromCity(selectedCity)
        viewModel.lat = locationOfCity?.latitude
        viewModel.lon = locationOfCity?.longitude

        viewModel.getForecasts()
    }



    private fun List<Day>.toRVItem() : List<DayItem>{
        return this.map {
            DayItem(context, it)
        }
    }

}