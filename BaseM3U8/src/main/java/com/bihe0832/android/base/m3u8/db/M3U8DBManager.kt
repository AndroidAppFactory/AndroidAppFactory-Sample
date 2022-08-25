package com.bihe0832.android.base.m3u8.db;

import android.content.Context
import com.bihe0832.android.base.m3u8.bean.M3U8Info

/**
 *
 * @author zixie code@bihe0832.com
 * Created on 2020/6/11.
 * Description: Description
 *
 */
object M3U8DBManager {

    private var mApplicationContext: Context? = null
    private var commonDBHelperInstance: M3U8InfoDBHelper? = null

    fun init(context: Context) {
        mApplicationContext = context
        if(null == commonDBHelperInstance){
            commonDBHelperInstance = M3U8InfoDBHelper(mApplicationContext)
        }
    }

    fun getAll(): List<M3U8Info> {
        try {
            commonDBHelperInstance?.let {
                return M3U8InfoTableModel.getAllData(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mutableListOf()
    }

    fun getData(url: String): M3U8Info? {
        try {
            commonDBHelperInstance?.let {
                return M3U8InfoTableModel.getData(it, url)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun saveData(info: M3U8Info): Boolean {
        try {
            commonDBHelperInstance?.let {
                return M3U8InfoTableModel.saveData(it, info)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun deleteData(url: String): Boolean {
        try {
            commonDBHelperInstance?.let {
                return M3U8InfoTableModel.clearData(it, url)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun deleteAll(): Int {
        try {
            commonDBHelperInstance?.let {
                return M3U8InfoTableModel.deleteAll(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }
}