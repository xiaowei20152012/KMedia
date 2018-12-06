package com.k.todo.fragments


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.k.todo.R
import com.k.todo.base.BaseFragment
import com.k.todo.base.OnItemClickListener
import com.k.todo.fragments.main.MainAdapter
import com.k.todo.model.Song
import com.k.todo.service.MusicPlayerRemote

import java.util.ArrayList

class MainFragment : BaseFragment() {

    private var recyclerView: RecyclerView? = null
    private var adapter: MainAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recycler_view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = MainAdapter()
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        recyclerView!!.adapter = adapter
        adapter!!.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(itemView: View, position: Int, `object`: Any) {
                //                MusicPlayerRemote.playNext()
                //                MusicPlayerRemote.playNext((Song) object);
                MusicPlayerRemote.playNextSong()
            }
        })
    }

    override fun onDataChanged(datas: Any) {
        super.onDataChanged(datas)
        if (datas is ArrayList<*>) {
            adapter!!.setAdapter(datas as ArrayList<Song>?)
            adapter!!.notifyDataSetChanged()
        }
        MusicPlayerRemote.openQueue(datas as ArrayList<Song>, 0, false)
    }

    override fun onLoaded(datas: Any) {
        super.onLoaded(datas)
        if (datas is ArrayList<*>) {
            adapter!!.setAdapter(datas as ArrayList<Song>)
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onLoadError(error: Any) {
        super.onLoadError(error)
    }

    companion object {

        fun instance(): MainFragment {
            return MainFragment()
        }
    }
}
