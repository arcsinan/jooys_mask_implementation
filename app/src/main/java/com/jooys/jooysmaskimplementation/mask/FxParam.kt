package com.jooys.jooysmaskimplementation.mask

import android.annotation.SuppressLint


class FxParam<T> @SuppressLint("NewApi") constructor(// object 或其他 object or other
    var type: String, var key: String,
    /*
     * This could be a float or a boolean or a set of numbers (region)
     */
    var value: T
) {

    override fun equals(obj: Any?): Boolean {
        return super.equals(obj)
    }

    companion object {
        private const val TAG = "FxParam"
        const val TYPE_STRING = "string"
        const val TYPE_STRING_OLD = "String"
        const val TYPE_BOOLEAN = "boolean"
        const val TYPE_FLOAT = "float"
        const val TYPE_OBJECT = "Object"
    }
}

