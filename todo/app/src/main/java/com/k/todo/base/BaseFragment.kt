package com.k.todo.base


import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.k.todo.provider.DataSourceListener
import com.k.todo.provider.MusicProvider

open class BaseFragment : Fragment(), DataProvider, DataSourceListener {

    override val musicProvider: MusicProvider
        get() {
            var provider: MusicProvider? = null
            if (activity != null && activity is DataProvider) {
                provider = (activity as DataProvider).musicProvider
            }
            return if (provider == null) MusicProvider.create() else provider
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        musicProvider.registerDataSourceListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        musicProvider.loadData()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        musicProvider.unregisterDataSourceListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }


    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onLoaded(datas: Any) {

    }

    override fun onLoadError(error: Any) {

    }

    override fun onDataChanged(datas: Any) {

    }

    override fun onLoading() {

    }
}

