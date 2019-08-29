package me.antonio.noack.maths

import android.opengl.GLES20.*
import me.antonio.noack.gl.ConstStorage.M3x3Buffers
import me.antonio.noack.gl.ConstStorage.M4x4Buffers
import me.antonio.noack.gl.buffer.Utils.floatBuffer
import java.lang.Math.abs
import java.lang.RuntimeException
import java.nio.FloatBuffer
import kotlin.math.tan

class Matrix constructor(var data: Array<DoubleArray>) {

	private constructor(dimension: Int = 4, unitNotZero: Boolean) : this(when (unitNotZero) {
		true -> Array(dimension) { index -> DoubleArray(dimension) { index2 -> if (index == index2) 1.0 else 0.0 } }
		else -> Array(dimension) { DoubleArray(dimension) { 0.0 } }
	})

	private constructor(m: Int, n: Int, unitNotZero: Boolean) : this(when (unitNotZero) {
		true -> Array(m) { index -> DoubleArray(n) { index2 -> if (index == index2) 1.0 else 0.0 } }
		else -> Array(m) { DoubleArray(n) { 0.0 } }
	})

	init {
		/*if(data.size == 4 && data[0].size == 4){
            markForReuse(this)
        }*/
	}

	operator fun times(matrix: Matrix): Matrix {
		multiply(matrix)
		return this
	}

	operator fun timesAssign(matrix: Matrix) {
		multiply(matrix)
	}

	fun getX(x: Double = 0.0, y: Double = 0.0, z: Double = 1.0, w: Double = 1.0): Double {
		return get(data[0], x, y, z, w)
	}

	fun getY(x: Double = 0.0, y: Double = 0.0, z: Double = 1.0, w: Double = 1.0): Double {
		return get(data[1], x, y, z, w)
	}

	fun getZ(x: Double = 0.0, y: Double = 0.0, z: Double = 1.0, w: Double = 1.0): Double {
		return get(data[2], x, y, z, w)
	}

	fun get(row: DoubleArray, x: Double = 0.0, y: Double = 0.0, z: Double = 1.0, w: Double = 1.0): Double {
		return row[0] * x + row[1] * y + row[2] * z + if (row.size > 3) row[3] * w else 0.0
	}

	var uploaded: FloatBuffer? = null
	fun upload4x4(uniform: Int, index: Int){

		if(index < 0){
			if(uploaded == null){
				val buffer = floatBuffer(16)
				for(i in 0..3){
					for(j in 0..3){
						buffer.put(data[i][j].toFloat())
					}
				}
				uploaded = buffer.position(0) as FloatBuffer
			}
		} else {
			val buffer = M4x4Buffers[index]
			for(i in 0..3){
				for(j in 0..3){
					buffer.put(data[i][j].toFloat())
				}
			}
			uploaded = buffer
		}

		uploaded!!.rewind()
		glUniformMatrix4fv(uniform, 1, true, uploaded)
	}

	fun upload3x3(uniform: Int, index: Int){

		if(index < 0){
			if(uploaded == null){
				val buffer = floatBuffer(9)
				for(i in 0..2){
					for(j in 0..2){
						buffer.put(data[i][j].toFloat())
					}
				}
				uploaded = buffer.position(0) as FloatBuffer
			}
		} else {
			val buffer = M3x3Buffers[index]
			for(i in 0..2){
				for(j in 0..2){
					buffer.put(data[i][j].toFloat())
				}
			}
			uploaded = buffer
		}

        uploaded!!.rewind()
		glUniformMatrix3fv(uniform, 1, true, uploaded)
	}

	fun multiply(matrix: Matrix): Matrix {

		// save into this matrix...
		val sec = matrix.data
		if (sec.size != data[0].size) {
			throw RuntimeException("Can't multiply ${data.size} x ${data[0].size} times ${sec.size} x ${sec[0].size} matrix!")
		}

		val n = sec.size
		data = Array(data.size) { i ->
			val slice = data[i]
			DoubleArray(sec[0].size) { j ->
				var sum = slice[0] * sec[0][j]
				for (k in 1 until n) {
					sum += slice[k] * sec[k][j]
				}
				sum
			}
		}

		return this
	}

	fun rotateX(alpha: Float): Matrix {
		return rotateX(alpha.toDouble())
	}

	fun rotateX(alpha: Double): Matrix {
		if (alpha != 0.0) {
			val sin = Math.sin(alpha)
			val cos = Math.cos(alpha)
			data.forEach { array ->
				val a = array[1]
				val b = array[2]
				array[1] = cos * a - sin * b
				array[2] = sin * a + cos * b
			}
		}
		return this
	}

	fun rotateY(alpha: Float): Matrix {
		return rotateY(alpha.toDouble())
	}

	fun rotateY(alpha: Double): Matrix {
		if (alpha != 0.0) {
			val sin = Math.sin(alpha)
			val cos = Math.cos(alpha)
			data.forEach { array ->
				val a = array[0]
				val b = array[2]
				array[0] = cos * a + sin * b
				array[2] = -sin * a + cos * b
			}
		}
		return this
	}

	fun rotateZ(alpha: Float): Matrix {
		return rotateZ(alpha.toDouble())
	}

	fun rotateZ(alpha: Double): Matrix {
		if (alpha != 0.0) {
			val sin = Math.sin(alpha)
			val cos = Math.cos(alpha)
			data.forEach { array ->
				val a = array[0]
				val b = array[1]
				array[0] = cos * a - sin * b
				array[1] = sin * a + cos * b
			}
		}
		return this
	}

	fun clone(): Matrix {
		return Matrix(Array(data.size) { index ->
            data[index].clone()
        })
	}

	private fun transposedData(): Array<DoubleArray> {
		return Array(data[0].size) { i ->
			DoubleArray(data.size) { j ->
				data[j][i]
			}
		}
	}

	fun transpose(): Matrix {
		data = transposedData()
		return this
	}

	fun transposed(): Matrix {
		return Matrix(transposedData())
	}

	fun inverse(): Matrix {

		val n = data.size - 1

		val newMatrixData = Array(n + 1) { i -> DoubleArray(n + 1) { j -> if (i == j) 1.0 else 0.0 } }
		for (row in 0..n) {

			// multiply row...
			val rowData = data[row]
			val rowData2 = newMatrixData[row]
			val element = rowData[row]
			if (element != 1.0) {
				if (abs(element) < .001) {

					// suche eine Spalte, mit der wir uns reparieren können
					var absMax = abs(element)
					var bestI = row
					for (i in 0..n) {
						if (i != row && abs(data[i][row]) > absMax) {
							absMax = abs(data[i][row])
							bestI = i
						}
					}
					if (bestI == row) {
						throw RuntimeException("Matrix is not invertible!")
					}

					// addiere diese Zeile auf unsere drauf, bis unser Wert = 1 ist
					val bestRow = data[bestI]
					val bestRow2 = newMatrixData[bestI]
					val factor = (1 - element) / bestRow[row]
					/*for(i in 0 .. 3){// can be made a little more efficient because of the first its known to be 0
                        rowData[i] += factor * bestRow[i]
                        rowData2[i] += factor * bestRow2[i]
                    }*/
					for (i in row + 1..n) {// can be made a little more efficient because of the first its known to be 0
						rowData[i] += factor * bestRow[i]
					}
					for (i in 0..n) {
						rowData2[i] += factor * bestRow2[i]
					}

					// is allowed
					rowData[row] = 1.0

				} else {
					// multiply the line
					val factor = 1 / element
					rowData[row] = 1.0
					for (i in row + 1..n) {
						rowData[i] *= factor
					}
					for (i in 0..n) {
						rowData2[i] *= factor
					}
				}
			}

			// add the other rows...
			for (row2 in 0..n) {
				if (row2 != row) {
					val other = data[row2]
					val other2 = newMatrixData[row2]
					val factor = -other[row]
					// add self onto other
					other[row] = 0.0
					for (i in row + 1..n) {
						other[i] += factor * rowData[i]
					}
					for (i in 0..n) {
						other2[i] += factor * rowData2[i]
					}
				}
			}
		}

		this.data = newMatrixData
		return this
	}

	fun writeUnit(): Matrix {
		for (i in 0 until data.size) {
			val row = data[i]
			for (j in 0 until row.size) {
				row[j] = if (i == j) 1.0 else 0.0
			}
		}
		return this
	}

	override fun toString(): String {
		return data.joinToString("],\n\t[", prefix = "[\n\t[", postfix = "]\n]") { x -> x.joinToString(", ") }
	}

	companion object {

		fun frameHappened() {}

		private fun clean(array: Array<DoubleArray>, unitNotZero: Boolean) {
			val s = array.size
			val z = array[0].size
			if (unitNotZero) {
				for (i in 0 until s) {
					for (j in 0 until z) {
						array[i][j] == if (i == j) 1.0 else 0.0
					}
				}
			} else {
				for (i in 0 until s) {
					for (j in 0 until z) {
						array[i][j] = 0.0
					}
				}
			}
		}

		fun I(size: Int = 4, uuid: Int): Matrix {
			return Matrix(size, true)
		}

		fun perspectiveMatrix(wfh: Double, fov: Double, near: Double, far: Double, uuid: Int): Matrix {
			val fDif = 1 / (far - near)
			val fTan = 1 / tan(fov / 2)
			return Matrix(
                arrayOf(
                    doubleArrayOf(fTan / wfh, 0.0, 0.0, 0.0),
                    doubleArrayOf(0.0, fTan, 0.0, 0.0),
                    doubleArrayOf(0.0, 0.0, -(far + near) * fDif, -2 * far * near * fDif),
                    doubleArrayOf(0.0, 0.0, -1.0, 0.0)
                )
				/*arrayOf(
					doubleArrayOf(fTan / wfh, 0.0, 0.0, 0.0),
					doubleArrayOf(0.0, fTan, 0.0, 0.0),
					doubleArrayOf(0.0, 0.0, 1.0, 0.0),
					doubleArrayOf(0.0, 0.0, -1.0, 0.0)
				)*/
            )
		}

		/*fun orthographicMatrix(sizeX: Double, sizeY: Double, sizeZ: Double): Matrix {
            return Matrix(arrayOf(
                doubleArrayOf(1/sizeX, 0.0, 0.0, 0.0),
                doubleArrayOf(0.0, 1/sizeY, 0.0, 0.0),
                doubleArrayOf(0.0, 0.0, 1/sizeZ, 0.0),
                doubleArrayOf(0.0, 0.0, 0.0, 1.0)
            ))
        }*/

	}

}