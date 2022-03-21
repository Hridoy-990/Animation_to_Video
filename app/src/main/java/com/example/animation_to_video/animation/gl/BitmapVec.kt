package com.example.animation_to_video.animation.gl

import kotlin.math.sqrt

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 21,March,2022
 */

data class Vec3(var x: Float = 0f,
                var y: Float = 0f,
                var z: Float = 0f) {


    operator fun plus(other: Vec3): Vec3 {
        return Vec3(x + other.x, y + other.y, z + other.z)
    }

    operator fun minus(other: Vec3): Vec3 {
        return Vec3(x - other.x, y - other.y, z - other.z)
    }

    operator fun times(Vec3: Vec3): Vec3 {
        return Vec3(x * Vec3.x, y * Vec3.y, z * Vec3.z)
    }

    operator fun times(scalar: Float): Vec3 {
        return Vec3(x * scalar, y * scalar, z * scalar)
    }

    operator fun div(scalar: Float): Vec3 {
        return Vec3(x / scalar, y / scalar, z / scalar)
    }
}

fun distance(first: Vec3, second: Vec3): Float {
    return length(first - second)
}

fun length(Vec3: Vec3): Float {
    return sqrt(Vec3.x * Vec3.x + Vec3.y * Vec3.y + Vec3.z * Vec3.z)
}

fun normalize(Vec3: Vec3): Vec3 {
    val l = length(Vec3)
    if (l == 0f)
        return Vec3

    val lr = 1f / l
    return Vec3 * lr
}