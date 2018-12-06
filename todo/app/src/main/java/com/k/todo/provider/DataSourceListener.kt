package com.k.todo.provider


interface DataSourceListener {

    fun onLoaded(datas: Any)

    fun onLoadError(error: Any)

    fun onDataChanged(datas: Any)

    fun onLoading()
}
