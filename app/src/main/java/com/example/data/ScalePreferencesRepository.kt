package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.SensorMode
import com.example.model.WeighingRecord
import com.example.model.WeightUnit
import org.json.JSONArray
import org.json.JSONObject

class ScalePreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("weigh_high_prefs", Context.MODE_PRIVATE)

    private var cachedHistory: List<WeighingRecord>? = null

    companion object {
        private const val KEY_TOUCH_SLOPE = "touch_slope"
        private const val KEY_BAROMETER_SLOPE = "barometer_slope"
        private const val KEY_UNIT = "selected_unit"
        private const val KEY_MODE = "selected_mode"
        private const val KEY_HISTORY = "weighing_history"

        const val DEFAULT_TOUCH_SLOPE = 125.0f
        const val DEFAULT_BAROMETER_SLOPE = 280.0f
    }

    fun getTouchSlope(): Float = prefs.getFloat(KEY_TOUCH_SLOPE, DEFAULT_TOUCH_SLOPE)

    fun saveTouchSlope(slope: Float) {
        prefs.edit().putFloat(KEY_TOUCH_SLOPE, slope).apply()
    }

    fun getBarometerSlope(): Float = prefs.getFloat(KEY_BAROMETER_SLOPE, DEFAULT_BAROMETER_SLOPE)

    fun saveBarometerSlope(slope: Float) {
        prefs.edit().putFloat(KEY_BAROMETER_SLOPE, slope).apply()
    }

    fun getSelectedUnit(): WeightUnit {
        val code = prefs.getString(KEY_UNIT, WeightUnit.GRAMS.name) ?: WeightUnit.GRAMS.name
        return try {
            WeightUnit.valueOf(code)
        } catch (_: Exception) {
            WeightUnit.GRAMS
        }
    }

    fun saveSelectedUnit(unit: WeightUnit) {
        prefs.edit().putString(KEY_UNIT, unit.name).apply()
    }

    fun getSelectedMode(): SensorMode {
        val code = prefs.getString(KEY_MODE, SensorMode.TOUCH_PRESSURE.name) ?: SensorMode.TOUCH_PRESSURE.name
        return try {
            SensorMode.valueOf(code)
        } catch (_: Exception) {
            SensorMode.TOUCH_PRESSURE
        }
    }

    fun saveSelectedMode(mode: SensorMode) {
        prefs.edit().putString(KEY_MODE, mode.name).apply()
    }

    fun getHistory(): List<WeighingRecord> {
        cachedHistory?.let { return it }

        val jsonString = prefs.getString(KEY_HISTORY, "[]") ?: "[]"
        val list = ArrayList<WeighingRecord>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("id", "")
                val timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                val weightInGrams = obj.optDouble("weightInGrams", 0.0).toFloat()
                val unitName = obj.optString("unit", WeightUnit.GRAMS.name)
                val unit = try { WeightUnit.valueOf(unitName) } catch (_: Exception) { WeightUnit.GRAMS }
                val formattedDisplay = obj.optString("formattedDisplay", "$weightInGrams g")
                val modeName = obj.optString("sensorMode", SensorMode.TOUCH_PRESSURE.name)
                val sensorMode = try { SensorMode.valueOf(modeName) } catch (_: Exception) { SensorMode.TOUCH_PRESSURE }
                val itemLabel = obj.optString("itemLabel", "")

                list.add(
                    WeighingRecord(
                        id = id,
                        timestamp = timestamp,
                        weightInGrams = weightInGrams,
                        unit = unit,
                        formattedDisplay = formattedDisplay,
                        sensorMode = sensorMode,
                        itemLabel = itemLabel
                    )
                )
            }
        } catch (_: Exception) {
            // fallback empty
        }
        cachedHistory = list
        return list
    }

    fun saveHistory(history: List<WeighingRecord>) {
        cachedHistory = history
        val array = JSONArray()
        for (item in history.take(100)) { // limit to last 100 entries
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("timestamp", item.timestamp)
            obj.put("weightInGrams", item.weightInGrams.toDouble())
            obj.put("unit", item.unit.name)
            obj.put("formattedDisplay", item.formattedDisplay)
            obj.put("sensorMode", item.sensorMode.name)
            obj.put("itemLabel", item.itemLabel)
            array.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, array.toString()).apply()
    }
}
