package com.opt.android_startup.sort

import android.util.Log
import com.opt.android_startup.Startup
import com.opt.android_startup.extensions.getUniqueKey
import com.opt.android_startup.model.StartupSortStore
import com.opt.android_startup.utils.StartupLogUtils
import java.lang.RuntimeException
import java.util.ArrayDeque

/**
 * 实现拓补排序
 */

internal object TopologySort {

    fun sort(startupList: List<Startup<*>>): StartupSortStore {

        val mainResult = mutableListOf<Startup<*>>()
        val ioResult = mutableListOf<Startup<*>>()
        val temp = mutableListOf<Startup<*>>()
        val startupMap = hashMapOf<String, Startup<*>>()
        val zeroDeque = ArrayDeque<String>()
        val startupChildrenMap = hashMapOf<String, MutableList<String>>()
        val inDegreeMap = hashMapOf<String, Int>()

        startupList.forEach {
            val uniqueKey = it::class.java.getUniqueKey()
            if (!startupMap.containsKey(uniqueKey)) {
                startupMap[uniqueKey] = it
                inDegreeMap[uniqueKey] = it.getDependenciesCount()

                if (it.getDependenciesCount() == 0) {
                    zeroDeque.offer(uniqueKey)
                } else {
                    it.dependencies()?.forEach { parent ->
                        val parentUniqueKey = parent.getUniqueKey()
                        if (startupChildrenMap[parentUniqueKey] == null) {
                            startupChildrenMap[parentUniqueKey] = arrayListOf()
                        }
                        startupChildrenMap[parentUniqueKey]?.add(uniqueKey)
                    }
                }

            } else {
                throw RuntimeException("$it multiple add")
            }
        }

        while (!zeroDeque.isEmpty()) {
            zeroDeque.poll()?.let {
                startupMap[it]?.let { androidStartup ->
                    temp.add(androidStartup)
                    if (androidStartup.callCreateOnMainThread()) {
                        mainResult.add(androidStartup)
                    } else {
                        ioResult.add(androidStartup)
                    }
                    startupChildrenMap[it]?.forEach { children ->
                        inDegreeMap[children] = inDegreeMap[children]?.minus(1) ?: 0
                        if (inDegreeMap[children] == 0) {
                            zeroDeque.offer(children)
                        }
                    }
                }
            }
        }


        if (mainResult.size + ioResult.size != startupList.size) {
            throw RuntimeException("lack of dependencies or have circle dependencies.")
        }

        val result = mutableListOf<Startup<*>>().apply {
            addAll(ioResult)
            addAll(mainResult)
        }

        printResult(result)

        return StartupSortStore(result, startupMap, startupChildrenMap)
    }

    private fun printResult(result: List<Startup<*>>) {
        val printBuilder = buildString {
            append("TopologySort result: ")
            append("\n")
            append("|================================================================")
            result.forEachIndexed { index, it ->
                append("\n")
                append("|         order          |    [${index + 1}] ")
                append("\n")
                append("|----------------------------------------------------------------")
                append("\n")
                append("|        Startup         |    ${it::class.java.simpleName}")
                append("\n")
                append("|----------------------------------------------------------------")
                append("\n")
                append("|   Dependencies size    |    ${it.getDependenciesCount()}")
                append("\n")
                append("|----------------------------------------------------------------")
                append("\n")
                append("| callCreateOnMainThread |    ${it.callCreateOnMainThread()}")
                append("\n")
                append("|----------------------------------------------------------------")
                append("\n")
                append("|    waitOnMainThread    |    ${it.waitOnMainThread()}")
                append("\n")
                append("|================================================================")
            }
        }
        StartupLogUtils.d { printBuilder }
    }

}