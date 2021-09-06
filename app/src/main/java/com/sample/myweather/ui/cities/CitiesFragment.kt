package com.sample.myweather.ui.cities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.myweather.R
import com.sample.myweather.utils.Coroutines
import com.sample.myweather.utils.ProgressStatus
import com.sample.myweather.utils.RecyclerItemTouchHelper
import com.sample.myweather.utils.log
import kotlinx.android.synthetic.main.cities_fragment.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class CitiesFragment : Fragment(), KodeinAware,
    RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    override val kodein by kodein()

    companion object {
        fun newInstance(bookmark: Boolean): CitiesFragment {
            val fragment = CitiesFragment()

            val args = Bundle()
            args.putBoolean("bookmark", bookmark)
            fragment.arguments = args

            return fragment
        }
    }

    private var bookmark:Boolean = false

    private val factory: CitiesViewModelFactory by instance()
    private lateinit var viewModel: CitiesViewModel

    val progressStatus = MutableLiveData<ProgressStatus>()
    val selectedCity = MutableLiveData<String>()

    private lateinit var mAdapter: CityAdapter
    private var dataSet: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            bookmark = it.getBoolean("bookmark", false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cities_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(CitiesViewModel::class.java)

        initData()

        viewModel.listenerBookmarkADD.observe(this, Observer {
            if (it)
                Toast.makeText(context, "Successfully Bookmarked", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(context, "Already Bookmarked !", Toast.LENGTH_SHORT).show()
        })
        viewModel.listenerBookmarkREMOVE.observe(this, Observer { map ->
            val entry = map.iterator().next()
            if (entry.value) {
                dataSet.removeAt(entry.key)
                log("after removed bookmark = $dataSet")
                mAdapter.setItems(dataSet)
                Toast.makeText(context, "Bookmarked Removed", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(context, "Failed !", Toast.LENGTH_SHORT).show()
        })
        viewModel.listenerRemove.observe(this, Observer { map ->
            val entry = map.iterator().next()
            if (entry.value) {
                dataSet.removeAt(entry.key)
                log("after removed city = $dataSet")
                mAdapter.setItems(dataSet)
                Toast.makeText(context, "City Removed", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(context, "Failed !", Toast.LENGTH_SHORT).show()
        })
    }

    private fun initData() = Coroutines.main {
        progressStatus.postValue(ProgressStatus.LOADING)
        if(bookmark)
            viewModel.bookmarkCitiesData.await().observe(this, Observer {
                log("All city list: $it")

                if (it.isEmpty()) {
                    Toast.makeText(context, "No bookmarked yet !", Toast.LENGTH_SHORT).show()
                    progressStatus.postValue(ProgressStatus.COMPLETED)
                } else {
                    dataSet.addAll(it)
                    initRecyclerView()
                }
            })
        else
            viewModel.citiesData.await().observe(this, Observer {
                log("Bookmark city list: $it")
                dataSet.addAll(it)
                initRecyclerView()
            })
    }

    private fun initRecyclerView() {
        context?.let { cntx ->

            mAdapter = CityAdapter(cntx, bookmark)

            val mLayoutManager = LinearLayoutManager(activity)
            vRecyclerview.apply {
                layoutManager = mLayoutManager
                setHasFixedSize(false)
                adapter = mAdapter
            }

            mAdapter.setItems(dataSet)
            mAdapter.notifyDataSetChanged()

            progressStatus.postValue(ProgressStatus.COMPLETED)

            mAdapter.onItemClick = { dataSet ->
                log("clicked city. $dataSet")
                selectedCity.postValue(dataSet)
            }

            //swipe LEFT to delete | RIGHT to bookmark
            val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
                RecyclerItemTouchHelper(
                    0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                    this
                )
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(vRecyclerview)
        }
    }

    //listen on swiped
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        if (viewHolder is CityAdapter.MyViewHolder) {

            mAdapter.notifyItemChanged(viewHolder.adapterPosition)
            val city = dataSet[viewHolder.adapterPosition]

            if (direction == ItemTouchHelper.RIGHT) {
                log("swiped right - Bookmark")

                if(bookmark)//check screen-type
                    viewModel.removeBookmark(viewHolder.adapterPosition, city) //remove from bookmarks
                else
                    viewModel.setNewBookmark(city) // add into bookmarks

            } else if (direction == ItemTouchHelper.LEFT) {
                log("swiped left - Delete")

                viewModel.removeCity(viewHolder.adapterPosition, city)
            }

        }
    }

}